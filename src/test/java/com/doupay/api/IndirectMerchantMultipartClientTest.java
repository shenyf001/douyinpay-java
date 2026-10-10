package com.doupay.api;

import com.douyinpay.api.DefaultDouyinpayClient;
import com.douyinpay.api.DouyinpayMultipartRequest;
import com.douyinpay.api.indirectmerchant.models.UploadResponse;
import com.douyinpay.component.http.HttpMethod;
import com.douyinpay.define.Constants;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Collections;

public class IndirectMerchantMultipartClientTest {
    @Test
    public void executeMultipartShouldPreserveMetaAndSendMultipartBody() throws Exception {
        final String meta = "{\"filename\":\"license.jpg\",\"sha256\":\"abc\"}";
        final String[] received = new String[2];
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/v1/merchant/upload", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                received[0] = exchange.getRequestHeaders().getFirst("Content-Type");
                received[1] = new String(readAll(exchange), StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add(Constants.DOUYIN_PAY_TIMESTAMP,
                        String.valueOf(System.currentTimeMillis() / 1000));
                exchange.getResponseHeaders().add(Constants.DOUYIN_PAY_NONCE, "nonce");
                exchange.getResponseHeaders().add(Constants.DOUYIN_PAY_SIGNATURE, "signature");
                exchange.getResponseHeaders().add(Constants.DOUYIN_PAY_SERIAL, "serial");
                byte[] response = "{\"media_id\":\"media_1\"}".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        });
        server.start();
        try {
            DefaultDouyinpayClient client = new DefaultDouyinpayClient("mchid", Constants.SIGN_TYPE_RSA,
                    Constants.ENCRYPT_TYPE_AES, testPrivateKeyPem(), "01234567890123456789012345678901", "serial");
            DouyinpayMultipartRequest request = new DouyinpayMultipartRequest(HttpMethod.POST,
                    "http://localhost:" + server.getAddress().getPort(), "/v1/merchant/upload",
                    Collections.<String, String>emptyMap(), meta, "file", "license.jpg", "image/jpeg",
                    "image-content".getBytes(StandardCharsets.UTF_8));

            UploadResponse response = client.executeMultipart(request, UploadResponse.class).getApiResponse();

            Assert.assertEquals(meta, request.getSignBody());
            Assert.assertTrue(received[0].startsWith("multipart/form-data; boundary="));
            Assert.assertTrue(received[1].contains(meta));
            Assert.assertTrue(received[1].contains("image-content"));
            Assert.assertEquals("media_1", response.getMediaId());
        } finally {
            server.stop(0);
        }
    }

    private static String testPrivateKeyPem() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
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
