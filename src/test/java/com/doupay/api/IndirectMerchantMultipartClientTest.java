package com.doupay.api;

import com.douyinpay.api.DefaultDouyinpayClient;
import com.douyinpay.api.DouyinpayMultipartRequest;
import com.douyinpay.api.indirectmerchant.models.UploadResponse;
import com.douyinpay.component.http.HttpMethod;
import com.douyinpay.define.Constants;
import com.douyinpay.define.GlobalConfig;
import com.douyinpay.exception.DouyinpayException;
import com.douyinpay.util.PemUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.Assert;
import org.junit.Test;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import okhttp3.MediaType;
import okhttp3.MultipartReader;
import okio.Buffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IndirectMerchantMultipartClientTest {
    @Test
    public void executeMultipartShouldPreserveMetaAndSendMultipartBody() throws Exception {
        executeMultipart(false, Collections.<String, String>emptyMap());
    }

    @Test
    public void executeMultipartShouldUseBodyBoundaryWithCustomContentType() throws Exception {
        executeMultipart(false, Collections.singletonMap("Content-Type", "application/json"));
    }

    @Test
    public void executeMultipartShouldRejectTamperedResponse() throws Exception {
        executeMultipart(true, Collections.<String, String>emptyMap());
    }

    @Test
    public void executeMultipartShouldDefaultMissingFileContentType() throws Exception {
        executeMultipart(false, Collections.<String, String>emptyMap(), null);
        executeMultipart(false, Collections.<String, String>emptyMap(), "");
    }

    private void executeMultipart(final boolean tamper, Map<String, String> headers) throws Exception {
        executeMultipart(tamper, headers, "image/jpeg");
    }

    private void executeMultipart(final boolean tamper, Map<String, String> headers,
                                  String fileContentType) throws Exception {
        final String meta = "{\"filename\":\"license.jpg\",\"sha256\":\"abc\"}";
        final KeyPair merchant = testKeyPair();
        final KeyPair platform = testKeyPair();
        final X509CertificateHolder certificate = new JcaX509v3CertificateBuilder(
                new X500Name("CN=Multipart Test Platform"), BigInteger.valueOf(12345),
                new Date(System.currentTimeMillis() - 60000), new Date(System.currentTimeMillis() + 3600000),
                new X500Name("CN=Multipart Test Platform"), platform.getPublic())
                .build(new JcaContentSignerBuilder("SHA256withRSA").build(platform.getPrivate()));
        final String certificatePem = "-----BEGIN CERTIFICATE-----\n"
                + Base64.getEncoder().encodeToString(certificate.getEncoded()) + "\n-----END CERTIFICATE-----";
        final X509Certificate platformCertificate = PemUtil.loadX509FromString(certificatePem);
        final byte[] fileContent = new byte[] {0, (byte) 255, 13, 10, 1, 2};
        final String[] received = new String[2];
        final byte[][] receivedBody = new byte[1][];
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/v1/merchant/upload", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                received[0] = exchange.getRequestHeaders().getFirst("Content-Type");
                received[1] = exchange.getRequestHeaders().getFirst(Constants.AUTHORIZATION);
                receivedBody[0] = readAll(exchange);
                String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
                String signedBody = "{\"media_id\":\"media_1\"}";
                exchange.getResponseHeaders().add(Constants.DOUYIN_PAY_TIMESTAMP, timestamp);
                exchange.getResponseHeaders().add(Constants.DOUYIN_PAY_NONCE, "nonce");
                try {
                    exchange.getResponseHeaders().add(Constants.DOUYIN_PAY_SIGNATURE,
                            sign(platform.getPrivate(), timestamp + "\nnonce\n" + signedBody + "\n"));
                } catch (Exception e) {
                    throw new IOException(e);
                }
                exchange.getResponseHeaders().add(Constants.DOUYIN_PAY_SERIAL,
                        PemUtil.getSerialNumber(platformCertificate));
                byte[] response = (tamper ? "{\"media_id\":\"tampered\"}" : signedBody)
                        .getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        });
        server.start();
        try {
            GlobalConfig config = new GlobalConfig();
            config.setMchId("mchid");
            config.setSignType(Constants.SIGN_TYPE_RSA);
            config.setEncryptType(Constants.ENCRYPT_TYPE_AES);
            config.setPrivateKey(Base64.getEncoder().encodeToString(merchant.getPrivate().getEncoded()));
            config.setEncryptKey("01234567890123456789012345678901");
            config.setMerchantSerialNumber("merchant-serial");
            config.setPlatformCertificate(certificatePem);
            DefaultDouyinpayClient client = new DefaultDouyinpayClient(config);
            DouyinpayMultipartRequest request = new DouyinpayMultipartRequest(HttpMethod.POST,
                    "http://localhost:" + server.getAddress().getPort(), "/v1/merchant/upload?kind=license",
                    headers, meta, "file", "license.jpg", fileContentType, fileContent);

            if (tamper) {
                try {
                    client.executeMultipart(request, UploadResponse.class);
                    Assert.fail("Tampered response must fail RSA verification");
                } catch (DouyinpayException e) {
                    Assert.assertTrue(e.getMessage(), e.getMessage().contains("signature is incorrect"));
                }
            } else {
                UploadResponse response = client.executeMultipart(request, UploadResponse.class).getApiResponse();
                Assert.assertEquals("media_1", response.getMediaId());
            }
            Assert.assertEquals(meta, request.getSignBody());
            Assert.assertTrue(received[1].startsWith("DouyinPay-RSA "));
            Map<String, String> authorization = new HashMap<>();
            Matcher matcher = Pattern.compile("(\\w+)=\"([^\"]*)\"").matcher(received[1]);
            while (matcher.find()) {
                authorization.put(matcher.group(1), matcher.group(2));
            }
            Assert.assertEquals("mchid", authorization.get("mchid"));
            Assert.assertEquals("merchant-serial", authorization.get("serial_no"));
            String prefix = "POST\n/v1/merchant/upload?kind=license\n" + authorization.get("timestamp")
                    + "\n" + authorization.get("nonce_str") + "\n";
            Assert.assertTrue(verify(merchant.getPublic(), prefix + meta + "\n", authorization.get("signature")));
            Assert.assertFalse(verify(merchant.getPublic(), prefix + meta + " \n", authorization.get("signature")));
            Assert.assertFalse(verify(merchant.getPublic(),
                    prefix + new String(receivedBody[0], StandardCharsets.UTF_8) + "\n", authorization.get("signature")));
            Assert.assertFalse(verify(merchant.getPublic(),
                    prefix + new String(fileContent, StandardCharsets.UTF_8) + "\n", authorization.get("signature")));

            MediaType contentType = MediaType.parse(received[0]);
            Assert.assertNotNull(contentType);
            Assert.assertEquals("multipart", contentType.type());
            Assert.assertEquals("form-data", contentType.subtype());
            String boundary = contentType.parameter("boundary");
            Assert.assertNotNull(boundary);
            int count = 0;
            try (MultipartReader reader = new MultipartReader(new Buffer().write(receivedBody[0]), boundary)) {
                MultipartReader.Part part;
                while ((part = reader.nextPart()) != null) {
                    count++;
                    String disposition = part.headers().get("Content-Disposition");
                    if (disposition.contains("name=\"meta\"")) {
                        MediaType metaType = MediaType.parse(part.headers().get("Content-Type") == null
                                ? "" : part.headers().get("Content-Type"));
                        Assert.assertNotNull("meta requires application/json", metaType);
                        Assert.assertEquals("application", metaType.type());
                        Assert.assertEquals("json", metaType.subtype());
                        Assert.assertEquals(meta, part.body().readUtf8());
                        Assert.assertFalse(disposition.contains("filename="));
                    } else {
                        Assert.assertEquals("form-data; name=\"file\"; filename=\"license.jpg\"", disposition);
                        Assert.assertEquals(fileContentType == null || fileContentType.isEmpty()
                                ? "application/octet-stream" : fileContentType, part.headers().get("Content-Type"));
                        Assert.assertArrayEquals(fileContent, part.body().readByteArray());
                    }
                }
            }
            Assert.assertEquals(2, count);
        } finally {
            server.stop(0);
        }
    }

    private static KeyPair testKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    private static String sign(PrivateKey key, String message) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(key);
        signature.update(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    private static boolean verify(PublicKey key, String message, String signatureValue) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(key);
        signature.update(message.getBytes(StandardCharsets.UTF_8));
        return signature.verify(Base64.getDecoder().decode(signatureValue));
    }

    private static byte[] readAll(HttpExchange exchange) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read;
        while ((read = exchange.getRequestBody().read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }
        return output.toByteArray();
    }
}
