package com.doupay.api.indirectmerchant;

import com.douyinpay.api.indirectmerchant.ApiIndirectMerchantService;
import com.douyinpay.api.indirectmerchant.models.ApplymentStatusNotification;
import com.douyinpay.api.notification.Notification;
import com.douyinpay.api.notification.NotificationConfig;
import com.douyinpay.api.notification.NotificationParser;
import com.douyinpay.api.notification.RequestParam;
import com.douyinpay.component.crypto.RsaVerifier;
import com.douyinpay.define.Constants;
import com.douyinpay.exception.DouyinpayException;
import com.douyinpay.util.GsonUtil;
import com.douyinpay.util.PemUtil;
import com.google.gson.JsonObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApplymentStatusNotificationTest {
    private static final String AES_KEY = "01234567890123456789012345678901";
    private static final String WRONG_AES_KEY = "11234567890123456789012345678901";
    private static final String TIMESTAMP = "1791763200";
    private static final String HEADER_NONCE = "request-nonce";
    private static final String RESOURCE_NONCE = "0123456789ab";
    private static final String ASSOCIATED_DATA = "merchant";
    private static final String EVENT = "INDIRECT_MERCHANT_APPLYMENT_STATUS";

    private KeyPair keys;
    private X509Certificate certificate;
    private ApiIndirectMerchantService service;

    @Before
    public void setUp() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        keys = generator.generateKeyPair();
        X500Name subject = new X500Name("CN=Notification Test");
        certificate = new JcaX509CertificateConverter().getCertificate(
                new JcaX509v3CertificateBuilder(subject, BigInteger.valueOf(123456),
                        new Date(1767225600000L), new Date(1893456000000L), subject, keys.getPublic())
                        .build(new JcaContentSignerBuilder("SHA256withRSA").build(keys.getPrivate())));
        certificate.verify(keys.getPublic());
        service = new ApiIndirectMerchantService.Builder().build();
    }

    @Test
    public void testParseApplymentStatusNotification_successAllSixFields() throws Exception {
        assertContent("APPLYMENT_STATE_PASSED");
    }

    @Test
    public void testParseApplymentStatusNotification_preservesUnknownState() throws Exception {
        assertContent("APPLYMENT_STATE_FUTURE");
    }

    @Test
    public void testParseApplymentStatusNotification_tamperedBodyRejectedBeforeDecryption() throws Exception {
        String body = body("APPLYMENT_STATE_PASSED", false);
        assertSignatureRejected(request(body + " ", sign(body)));
    }

    @Test
    public void testParseApplymentStatusNotification_tamperedSignatureRejectedBeforeDecryption() throws Exception {
        String body = body("APPLYMENT_STATE_PASSED", false);
        byte[] signature = Base64.getDecoder().decode(sign(body));
        signature[0] ^= 1;
        assertSignatureRejected(request(body, Base64.getEncoder().encodeToString(signature)));
    }

    @Test
    public void testParseApplymentStatusNotification_wrongAESKeyFailsGCMAuthentication() throws Exception {
        String body = body("APPLYMENT_STATE_PASSED", false);
        assertGCMRejected(WRONG_AES_KEY, request(body, sign(body)));
    }

    @Test
    public void testParseApplymentStatusNotification_resignedTamperedCiphertextFailsGCMAuthentication()
            throws Exception {
        String body = body("APPLYMENT_STATE_PASSED", true);
        assertGCMRejected(AES_KEY, request(body, sign(body)));
    }

    private void assertContent(String state) throws Exception {
        String body = body(state, false);
        Notification envelope = GsonUtil.getGson().fromJson(body, Notification.class);
        Assert.assertEquals(EVENT, envelope.getEventType());
        ApplymentStatusNotification content = service.parseApplymentStatusNotification(
                parser(AES_KEY), request(body, sign(body)));
        Assert.assertNotNull(content);
        Assert.assertEquals("6120230301343998", content.getSpMchid());
        Assert.assertEquals("NTAM7638162829987562028", content.getApplymentId());
        Assert.assertEquals("business_1", content.getBusinessCode());
        Assert.assertEquals("2026020211234567890", content.getIndirectMerchantId());
        Assert.assertEquals(state, content.getApplymentState());
        Assert.assertEquals("fixture reason", content.getFailReason());
    }

    private NotificationParser parser(String key) throws Exception {
        // Only replace the file-backed configuration; verification and decryption stay real.
        NotificationConfig config = mock(NotificationConfig.class);
        when(config.getSignType()).thenReturn(Constants.SIGN_TYPE_RSA);
        when(config.getCipherType()).thenReturn(Constants.ENCRYPT_TYPE_AES);
        when(config.getEncryptKey()).thenReturn(key);
        when(config.getCetificate()).thenReturn(new ByteArrayInputStream(certificate.getEncoded()));
        return new NotificationParser(config);
    }

    private String body(String state, boolean tamperCiphertext) throws Exception {
        JsonObject content = new JsonObject();
        content.addProperty("sp_mchid", "6120230301343998");
        content.addProperty("applyment_id", "NTAM7638162829987562028");
        content.addProperty("business_code", "business_1");
        content.addProperty("indirect_merchant_id", "2026020211234567890");
        content.addProperty("applyment_state", state);
        content.addProperty("fail_reason", "fixture reason");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.UTF_8), "AES"),
                new GCMParameterSpec(128, RESOURCE_NONCE.getBytes(StandardCharsets.UTF_8)));
        cipher.updateAAD(ASSOCIATED_DATA.getBytes(StandardCharsets.UTF_8));
        byte[] ciphertext = cipher.doFinal(content.toString().getBytes(StandardCharsets.UTF_8));
        if (tamperCiphertext) {
            ciphertext[ciphertext.length - 1] ^= 1;
        }
        JsonObject resource = new JsonObject();
        resource.addProperty("algorithm", "AEAD_AES_256_GCM");
        resource.addProperty("original_type", "merchant");
        resource.addProperty("ciphertext", Base64.getEncoder().encodeToString(ciphertext));
        resource.addProperty("associated_data", ASSOCIATED_DATA);
        resource.addProperty("nonce", RESOURCE_NONCE);
        JsonObject envelope = new JsonObject();
        envelope.addProperty("id", "notification_1");
        envelope.addProperty("create_time", "2026-10-12T08:00:00+08:00");
        envelope.addProperty("event_type", EVENT);
        envelope.addProperty("resource_type", "encrypt-resource");
        envelope.addProperty("summary", "status changed");
        envelope.add("resource", resource);
        return envelope.toString();
    }

    private String sign(String body) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keys.getPrivate());
        signature.update((TIMESTAMP + "\n" + HEADER_NONCE + "\n" + body + "\n")
                .getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    private RequestParam request(String body, String signature) {
        return new RequestParam.Builder().body(body).timestamp(TIMESTAMP).nonce(HEADER_NONCE)
                .serialNumber(PemUtil.getSerialNumber(certificate))
                .signType(Constants.SIGN_TYPE_RSA).signature(signature).build();
    }

    private void assertSignatureRejected(RequestParam request) throws Exception {
        try {
            // An incorrect AES key makes the first failing stage observable.
            service.parseApplymentStatusNotification(parser(WRONG_AES_KEY), request);
            Assert.fail("expected RSA signature verification failure");
        } catch (DouyinpayException expected) {
            Assert.assertTrue(expected.getMessage().contains("signature verification failed"));
            Assert.assertNull(expected.getCause());
        }
    }

    private void assertGCMRejected(String key, RequestParam request) throws Exception {
        Assert.assertTrue("GCM fixture must first pass real RSA verification",
                new RsaVerifier().verifyPlatformSign(request.getMessage(), certificate,
                        request.getSignature(), request.getSerialNumber()));
        try {
            service.parseApplymentStatusNotification(parser(key), request);
            Assert.fail("expected AES-GCM authentication failure");
        } catch (DouyinpayException expected) {
            Assert.assertNotNull(expected.getCause());
            Assert.assertTrue("expected GCM tag failure, got " + expected.getCause(),
                    expected.getCause() instanceof AEADBadTagException);
        }
    }
}
