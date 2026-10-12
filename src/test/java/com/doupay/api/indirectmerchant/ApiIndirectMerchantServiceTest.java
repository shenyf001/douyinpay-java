package com.doupay.api.indirectmerchant;

import com.douyinpay.api.DefaultDouyinpayClient;
import com.douyinpay.api.DouyinpayClient;
import com.douyinpay.api.DouyinpayMultipartRequest;
import com.douyinpay.api.DouyinpayRequest;
import com.douyinpay.api.DouyinpayResponse;
import com.douyinpay.api.indirectmerchant.ApiIndirectMerchantService;
import com.douyinpay.api.indirectmerchant.models.ApplymentStatusNotification;
import com.douyinpay.api.indirectmerchant.models.BindAppIdRequest;
import com.douyinpay.api.indirectmerchant.models.BindAppIdResponse;
import com.douyinpay.api.indirectmerchant.models.CancelApplyRequest;
import com.douyinpay.api.indirectmerchant.models.CreateApplyRequest;
import com.douyinpay.api.indirectmerchant.models.CreateApplyResponse;
import com.douyinpay.api.indirectmerchant.models.ModifyMerchantRequest;
import com.douyinpay.api.indirectmerchant.models.QueryAppIdRequest;
import com.douyinpay.api.indirectmerchant.models.QueryAppIdResponse;
import com.douyinpay.api.indirectmerchant.models.QueryApplyRequest;
import com.douyinpay.api.indirectmerchant.models.QueryApplyResponse;
import com.douyinpay.api.indirectmerchant.models.QueryMerchantRequest;
import com.douyinpay.api.indirectmerchant.models.QueryMerchantResponse;
import com.douyinpay.api.indirectmerchant.models.UploadRequest;
import com.douyinpay.api.indirectmerchant.models.UploadResponse;
import com.douyinpay.api.notification.NotificationParser;
import com.douyinpay.api.notification.RequestParam;
import com.douyinpay.component.http.HttpMethod;
import com.douyinpay.define.Constants;
import com.douyinpay.exception.DouyinpayException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import javax.crypto.Cipher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApiIndirectMerchantServiceTest {
    @Test
    public void queryApplyShouldParseAuditArrayAndUseEitherLocator() {
        final DouyinpayRequest[] captured = new DouyinpayRequest[1];
        final String json = "{\"business_code\":\"602323_10000\",\"applyment_id\":\"NIM2000002124775691\","
                + "\"applyment_state\":\"APPLYMENT_STATE_REJECTED\",\"audit_detail\":["
                + "{\"field\":\"id_card_copy\",\"reject_reason\":\"请上传清晰图片\",\"future_field\":true}]}";
        DouyinpayClient client = new DouyinpayClient() {
            public <T> DouyinpayResponse<T> execute(DouyinpayRequest request, Class<T> type) {
                captured[0] = request;
                return response(new com.google.gson.Gson().fromJson(json, type));
            }
        };
        ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder().douyinpayClient(client).build();
        for (boolean useId : new boolean[] {true, false}) {
            QueryApplyRequest request = new QueryApplyRequest();
            if (useId) request.setApplymentId("NIM2000002124775691");
            else request.setBusinessCode("602323_10000");
            QueryApplyResponse result = service.queryApply(request);
            Assert.assertEquals(HttpMethod.GET, captured[0].getHttpMethod());
            Assert.assertEquals("/v1/merchant/indirect_merchant/query_apply?"
                    + (useId ? "applyment_id=NIM2000002124775691" : "business_code=602323_10000"),
                    captured[0].getPath());
            Assert.assertEquals("APPLYMENT_STATE_REJECTED", result.getApplymentState());
            Assert.assertEquals(1, result.getAuditDetail().size());
            Assert.assertEquals("id_card_copy", result.getAuditDetail().get(0).getField());
            Assert.assertEquals("请上传清晰图片", result.getAuditDetail().get(0).getRejectReason());
        }
    }

    @Test
    public void serviceShouldBuildAllRequestContractsAndMapResponses() {
        DefaultDouyinpayClient client = mock(DefaultDouyinpayClient.class);
        stubResponse(client, CreateApplyResponse.class, new CreateApplyResponse());
        stubResponse(client, QueryApplyResponse.class, new QueryApplyResponse());
        stubResponse(client, QueryMerchantResponse.class, new QueryMerchantResponse());
        stubResponse(client, BindAppIdResponse.class, new BindAppIdResponse());
        stubResponse(client, QueryAppIdResponse.class, new QueryAppIdResponse());
        stubResponse(client, Object.class, new Object());
        X509Certificate certificate = mock(X509Certificate.class);
        when(certificate.getSerialNumber()).thenReturn(new BigInteger("1234", Constants.HEX));
        when(client.getPlatformCertificate()).thenReturn(certificate);

        ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder().douyinpayClient(client).build();
        CreateApplyRequest create = new CreateApplyRequest();
        create.setBusinessCode("apply_1");
        create.setOutIndirectMerchantId("out_merchant_1");
        create.setOutPaymentInstId("payment_inst_1");
        create.setOutChannelSpId("channel_sp_1");
        create.setOutPaymentInstSubId("payment_inst_sub_1");
        create.setIndirectMerchantId("merchant_1");
        service.createApply(create);
        QueryApplyRequest queryApply = new QueryApplyRequest();
        queryApply.setBusinessCode("apply_1");
        service.queryApply(queryApply);
        CancelApplyRequest cancel = new CancelApplyRequest();
        cancel.setApplymentId("applyment_1");
        service.cancelApply(cancel);
        QueryMerchantRequest merchant = new QueryMerchantRequest();
        merchant.setOutIndirectMerchantId("out_merchant_1");
        merchant.setOutPaymentInstId("payment_inst_1");
        merchant.setOutChannelSpId("channel_1");
        service.queryMerchant(merchant);
        ModifyMerchantRequest modify = platformLocator(new ModifyMerchantRequest());
        modify.setMerchantShortName("shop");
        service.modifyMerchant(modify);
        BindAppIdRequest bind = platformLocator(new BindAppIdRequest());
        bind.setAppId("app_1");
        service.bindAppId(bind);
        QueryAppIdRequest queryApp = platformLocator(new QueryAppIdRequest());
        queryApp.setAppId("app_1");
        service.queryAppId(queryApp);

        ArgumentCaptor<DouyinpayRequest> requests = ArgumentCaptor.forClass(DouyinpayRequest.class);
        verify(client, org.mockito.Mockito.times(7)).execute(requests.capture(), any(Class.class));
        Assert.assertEquals("/v1/merchant/indirect_merchant/create_apply", requests.getAllValues().get(0).getPath());
        Assert.assertEquals(HttpMethod.POST, requests.getAllValues().get(0).getHttpMethod());
        Assert.assertEquals("1234", requests.getAllValues().get(0).getHeaders().get(Constants.DOUYIN_PAY_SERIAL));
        JsonObject createBody = new com.google.gson.Gson().fromJson(requests.getAllValues().get(0).getBody(),
                JsonObject.class);
        Assert.assertEquals("apply_1", createBody.get("business_code").getAsString());
        Assert.assertEquals("out_merchant_1", createBody.get("out_indirect_merchant_id").getAsString());
        Assert.assertEquals("payment_inst_1", createBody.get("out_payment_inst_id").getAsString());
        Assert.assertEquals("channel_sp_1", createBody.get("out_channel_sp_id").getAsString());
        Assert.assertEquals("payment_inst_sub_1", createBody.get("out_payment_inst_sub_id").getAsString());
        Assert.assertEquals("merchant_1", createBody.get("indirect_merchant_id").getAsString());
        Assert.assertEquals("?business_code=apply_1",
                requests.getAllValues().get(1).getPath().substring(
                        "/v1/merchant/indirect_merchant/query_apply".length()));
        Assert.assertEquals(HttpMethod.POST, requests.getAllValues().get(2).getHttpMethod());
        Assert.assertEquals("?out_indirect_merchant_id=out_merchant_1&out_payment_inst_id=payment_inst_1"
                        + "&out_channel_sp_id=channel_1",
                requests.getAllValues().get(3).getPath().substring(
                        "/v1/merchant/indirect_merchant/query_merchant".length()));
        Assert.assertEquals(HttpMethod.POST, requests.getAllValues().get(4).getHttpMethod());
        Assert.assertEquals(HttpMethod.POST, requests.getAllValues().get(5).getHttpMethod());
        Assert.assertEquals("/v1/merchant/indirect_merchant/query_app_id?indirect_merchant_id=im_1&app_id=app_1",
                requests.getAllValues().get(6).getPath());
    }

    @Test
    public void uploadShouldUseOneMetaStringAndValidateLimits() {
        DefaultDouyinpayClient client = mock(DefaultDouyinpayClient.class);
        UploadResponse uploadResponse = new UploadResponse();
        uploadResponse.setMediaId("media_1");
        DouyinpayResponse<UploadResponse> response = response(uploadResponse);
        when(client.executeMultipart(any(DouyinpayMultipartRequest.class), eq(UploadResponse.class))).thenReturn(response);
        ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder().douyinpayClient(client).build();

        UploadResponse actual = service.upload(new UploadRequest("license.jpg", "image/jpeg", new byte[] {1, 2, 3}));

        ArgumentCaptor<DouyinpayMultipartRequest> request = ArgumentCaptor.forClass(DouyinpayMultipartRequest.class);
        verify(client).executeMultipart(request.capture(), eq(UploadResponse.class));
        JsonObject meta = new com.google.gson.Gson().fromJson(request.getValue().getSignBody(), JsonObject.class);
        Assert.assertEquals("license.jpg", meta.get("filename").getAsString());
        Assert.assertEquals("039058c6f2c0cb492c533b0a4d14ef77cc0f78abccced5287d84a1a2011cfb81",
                meta.get("sha256").getAsString());
        Assert.assertEquals("media_1", actual.getMediaId());
        assertInvalidUpload(service, new UploadRequest("license.jpg", "image/jpeg", new byte[0]));
        assertInvalidUpload(service, new UploadRequest("license.txt", "text/plain", new byte[] {1}));
        assertInvalidUpload(service, new UploadRequest("license.png", "image/png", new byte[5 * 1024 * 1024 + 1]));
    }

    @Test
    public void parserShouldDelegateAndPreserveUnknownState() {
        NotificationParser parser = mock(NotificationParser.class);
        RequestParam requestParam = mock(RequestParam.class);
        ApplymentStatusNotification expected = new ApplymentStatusNotification();
        expected.setApplymentState("NEW_STATE");
        when(parser.parse(requestParam, ApplymentStatusNotification.class)).thenReturn(expected);
        ApplymentStatusNotification actual = new ApiIndirectMerchantService.Builder().build()
                .parseApplymentStatusNotification(parser, requestParam);
        Assert.assertEquals("NEW_STATE", actual.getApplymentState());
    }

    @Test
    public void createApplyShouldRequireExternalMerchantFields() {
        ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder().build();

        CreateApplyRequest missingOutMerchant = new CreateApplyRequest();
        missingOutMerchant.setBusinessCode("apply_1");
        missingOutMerchant.setOutPaymentInstId("payment_inst_1");
        assertInvalidCreateApply(service, missingOutMerchant);

        CreateApplyRequest missingPaymentInstitution = new CreateApplyRequest();
        missingPaymentInstitution.setBusinessCode("apply_1");
        missingPaymentInstitution.setOutIndirectMerchantId("out_merchant_1");
        assertInvalidCreateApply(service, missingPaymentInstitution);
    }

    @Test(expected = IllegalArgumentException.class)
    public void queryMerchantShouldRejectChannelWithPlatformLocator() {
        QueryMerchantRequest request = platformLocator(new QueryMerchantRequest());
        request.setOutChannelSpId("channel_1");

        new ApiIndirectMerchantService.Builder().build().queryMerchant(request);
    }

    @Test
    public void createApplyShouldAcceptSerialWithCustomClient() {
        final DouyinpayRequest[] captured = new DouyinpayRequest[1];
        DouyinpayClient client = new DouyinpayClient() {
            @Override
            public <T> DouyinpayResponse<T> execute(DouyinpayRequest request, Class<T> responseClass) {
                captured[0] = request;
                return response(responseClass.cast(new CreateApplyResponse()));
            }
        };
        ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder().douyinpayClient(client).build();
        CreateApplyRequest request = new CreateApplyRequest();
        request.setBusinessCode("apply_1");
        request.setDouyinpaySerial("custom-serial");
        request.setOutIndirectMerchantId("out_merchant_1");
        request.setOutPaymentInstId("payment_inst_1");

        service.createApply(request);

        Assert.assertEquals("custom-serial", captured[0].getHeaders().get(Constants.DOUYIN_PAY_SERIAL));
        Assert.assertFalse(new com.google.gson.Gson().fromJson(captured[0].getBody(), JsonObject.class)
                .has("douyinpaySerial"));
    }

    @Test(expected = DouyinpayException.class)
    public void createApplyShouldRequireSerialWhenCertificateIsUnavailable() {
        CreateApplyRequest request = new CreateApplyRequest();
        request.setBusinessCode("apply_1");
        request.setOutIndirectMerchantId("out_merchant_1");
        request.setOutPaymentInstId("payment_inst_1");

        new ApiIndirectMerchantService.Builder().build().createApply(request);
    }

    @Test
    public void testCreateApply_completeContractWithBuilderCertificate() throws Exception {
        KeyPair keys = createApplyKeys();
        X509Certificate certificate = createApplyCertificate(keys);
        for (boolean explicitSerial : new boolean[] {false, true}) {
            CreateApplyRecordingClient client = new CreateApplyRecordingClient();
            ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder()
                    .douyinpayClient(client).platformCertificate(certificate).build();
            CreateApplyRequest request = createApplyContractRequest();
            if (explicitSerial) {
                request.setDouyinpaySerial("ABC123");
            }
            String original = new Gson().toJson(request);
            for (int attempt = 0; attempt < 2; attempt++) {
                CreateApplyResponse actual = service.createApply(request);
                Assert.assertEquals("NIM2000002124775691", actual.getApplymentId());
                Assert.assertEquals(attempt + 1, client.requests.size());
                assertCreateApplyContract(client.requests.get(attempt), keys.getPrivate());
                Assert.assertEquals(original, new Gson().toJson(request));
                Assert.assertEquals(explicitSerial ? "ABC123" : null, request.getDouyinpaySerial());
                Assert.assertEquals("Alice Zhang", request.getIdentificationInfo().getIdentificationName());
                Assert.assertEquals("110101199001011234",
                        request.getIdentificationInfo().getIdentificationNumber());
            }
        }
    }

    @Test
    public void testCreateApply_completeContractWithDefaultClientCertificate() throws Exception {
        KeyPair keys = createApplyKeys();
        DefaultDouyinpayClient client = mock(DefaultDouyinpayClient.class);
        X509Certificate certificate = createApplyCertificate(keys);
        when(client.getPlatformCertificate()).thenReturn(certificate);
        stubResponse(client, CreateApplyResponse.class, new Gson().fromJson(
                "{\"applyment_id\":\"NIM2000002124775691\"}", CreateApplyResponse.class));
        ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder().douyinpayClient(client).build();
        CreateApplyRequest request = createApplyContractRequest();
        String original = new Gson().toJson(request);

        Assert.assertEquals("NIM2000002124775691", service.createApply(request).getApplymentId());
        Assert.assertEquals("NIM2000002124775691", service.createApply(request).getApplymentId());

        ArgumentCaptor<DouyinpayRequest> captured = ArgumentCaptor.forClass(DouyinpayRequest.class);
        verify(client, org.mockito.Mockito.times(2)).execute(captured.capture(), eq(CreateApplyResponse.class));
        for (DouyinpayRequest sent : captured.getAllValues()) {
            assertCreateApplyContract(sent, keys.getPrivate());
        }
        Assert.assertEquals(original, new Gson().toJson(request));
        Assert.assertNull(request.getDouyinpaySerial());
    }

    @Test
    public void testCreateApply_encryptSingleSensitiveField() throws Exception {
        KeyPair keys = createApplyKeys();
        for (boolean nameOnly : new boolean[] {true, false}) {
            CreateApplyRecordingClient client = new CreateApplyRecordingClient();
            ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder()
                    .douyinpayClient(client).platformCertificate(createApplyCertificate(keys)).build();
            CreateApplyRequest request = createApplyContractRequest();
            String field = nameOnly ? "identification_name" : "identification_number";
            String absentField = nameOnly ? "identification_number" : "identification_name";
            String plaintext = nameOnly ? "Alice Zhang" : "110101199001011234";
            if (nameOnly) {
                request.getIdentificationInfo().setIdentificationNumber(null);
            } else {
                request.getIdentificationInfo().setIdentificationName(null);
            }
            String original = new Gson().toJson(request);

            service.createApply(request);

            Assert.assertEquals(1, client.requests.size());
            DouyinpayRequest sent = client.requests.get(0);
            JsonObject identification = new Gson().fromJson(sent.getBody(), JsonObject.class)
                    .getAsJsonObject("identification_info");
            Assert.assertEquals(plaintext, decryptCreateApplyField(
                    identification.get(field).getAsString(), keys.getPrivate()));
            Assert.assertFalse(identification.has(absentField));
            Assert.assertEquals("ABC123", sent.getHeaders().get(Constants.DOUYIN_PAY_SERIAL));
            Assert.assertEquals(original, new Gson().toJson(request));
        }
    }

    @Test
    public void testCreateApply_missingCertificateDoesNotSend() {
        for (String serial : new String[] {null, "ABC123"}) {
            CreateApplyRecordingClient client = new CreateApplyRecordingClient();
            ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder()
                    .douyinpayClient(client).build();
            CreateApplyRequest request = createApplyContractRequest();
            request.setDouyinpaySerial(serial);
            assertCreateApplyRejectedBeforeSend(service, client, request,
                    serial == null ? "Douyinpay-Serial" : "platform certificate");
        }
    }

    @Test
    public void testCreateApply_serialMismatchDoesNotSend() throws Exception {
        KeyPair keys = createApplyKeys();
        CreateApplyRecordingClient client = new CreateApplyRecordingClient();
        ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder()
                .douyinpayClient(client).platformCertificate(createApplyCertificate(keys)).build();
        CreateApplyRequest request = createApplyContractRequest();
        request.setDouyinpaySerial("OTHER_SERIAL");

        assertCreateApplyRejectedBeforeSend(service, client, request, "match");
    }

    @Test
    public void testCreateApply_missingSerialDoesNotSend() {
        CreateApplyRecordingClient client = new CreateApplyRecordingClient();
        ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder().douyinpayClient(client).build();
        CreateApplyRequest request = createApplyContractRequest();
        request.setIdentificationInfo(null);

        assertCreateApplyRejectedBeforeSend(service, client, request, "Douyinpay-Serial");
    }

    @Test
    public void testCreateApply_oversizedPlaintextDoesNotSend() throws Exception {
        KeyPair keys = createApplyKeys();
        CreateApplyRecordingClient client = new CreateApplyRecordingClient();
        ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder()
                .douyinpayClient(client).platformCertificate(createApplyCertificate(keys)).build();
        CreateApplyRequest request = createApplyContractRequest();
        request.getIdentificationInfo().setIdentificationName(String.join("",
                Collections.nCopies(246, "a")));

        assertCreateApplyRejectedBeforeSend(service, client, request, "RSA");
    }

    private static final String CREATE_APPLY_CONTRACT_JSON = "{"
            + "\"business_code\":\"602323_10000\",\"out_indirect_merchant_id\":\"10434920032\","
            + "\"out_payment_inst_id\":\"41110000\",\"out_channel_sp_id\":\"C023828488\","
            + "\"out_payment_inst_sub_id\":\"41110100\",\"indirect_merchant_id\":\"2026045587123820\","
            + "\"subject_info\":{\"subject_type\":\"SUBJECT_TYPE_ENTERPRISE\",\"certificate_info\":{"
            + "\"cert_no\":\"91310000TEST123456\",\"merchant_name\":\"Contract Shop\","
            + "\"cert_copy\":\"license_media\",\"register_address\":\"123 Contract Road\","
            + "\"legal_person_name\":\"Alice Zhang\",\"period_begin\":\"2019-08-01\",\"period_end\":\"2039-08-01\"}},"
            + "\"identification_info\":{\"identification_type\":\"IDENTIFICATION_TYPE_IDCARD\","
            + "\"identification_name\":\"Alice Zhang\",\"identification_number\":\"110101199001011234\","
            + "\"period_begin\":\"2019-08-01\",\"period_end\":\"2039-08-01\","
            + "\"identification_front_copy\":\"front_media\",\"identification_back_copy\":\"back_media\"},"
            + "\"business_info\":{\"merchant_short_name\":\"Contract Shop\",\"service_phone\":\"18042002458\","
            + "\"mcc\":\"7300\",\"special_industry_pics\":[\"industry_1\",\"industry_2\"]},"
            + "\"addition_info\":{\"addition_pics\":[\"addition_1\",\"addition_2\"],"
            + "\"addition_remark\":\"Contract materials\"}}";

    private static CreateApplyRequest createApplyContractRequest() {
        return new Gson().fromJson(CREATE_APPLY_CONTRACT_JSON, CreateApplyRequest.class);
    }

    private static KeyPair createApplyKeys() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    private static X509Certificate createApplyCertificate(KeyPair keys) {
        X509Certificate certificate = mock(X509Certificate.class);
        when(certificate.getSerialNumber()).thenReturn(new BigInteger("ABC123", 16));
        when(certificate.getPublicKey()).thenReturn(keys.getPublic());
        return certificate;
    }

    private static String decryptCreateApplyField(String ciphertext, PrivateKey key) throws Exception {
        byte[] encrypted = Base64.getDecoder().decode(ciphertext);
        Assert.assertEquals(256, encrypted.length);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
    }

    private static void assertCreateApplyContract(DouyinpayRequest sent, PrivateKey key) throws Exception {
        Assert.assertEquals(HttpMethod.POST, sent.getHttpMethod());
        Assert.assertEquals("/v1/merchant/indirect_merchant/create_apply", sent.getPath());
        Assert.assertEquals("ABC123", sent.getHeaders().get(Constants.DOUYIN_PAY_SERIAL));
        Gson gson = new Gson();
        JsonObject body = gson.fromJson(sent.getBody(), JsonObject.class);
        JsonObject identification = body.getAsJsonObject("identification_info");
        for (String field : new String[] {"identification_name", "identification_number"}) {
            identification.addProperty(field, decryptCreateApplyField(identification.get(field).getAsString(), key));
        }
        Assert.assertEquals(gson.fromJson(CREATE_APPLY_CONTRACT_JSON, JsonObject.class), body);
    }

    private static void assertCreateApplyRejectedBeforeSend(ApiIndirectMerchantService service,
            CreateApplyRecordingClient client, CreateApplyRequest request, String message) {
        String original = new Gson().toJson(request);
        String serial = request.getDouyinpaySerial();
        try {
            service.createApply(request);
            Assert.fail("expected create apply rejection");
        } catch (DouyinpayException expected) {
            Assert.assertTrue(expected.getMessage(), expected.getMessage().contains(message));
        }
        Assert.assertEquals(0, client.requests.size());
        Assert.assertEquals(original, new Gson().toJson(request));
        Assert.assertEquals(serial, request.getDouyinpaySerial());
    }

    private static class CreateApplyRecordingClient implements DouyinpayClient {
        private final List<DouyinpayRequest> requests = new ArrayList<DouyinpayRequest>();

        @Override
        public <T> DouyinpayResponse<T> execute(DouyinpayRequest request, Class<T> responseClass) {
            Assert.assertEquals(CreateApplyResponse.class, responseClass);
            requests.add(request);
            return response(new Gson().fromJson("{\"applyment_id\":\"NIM2000002124775691\"}", responseClass));
        }
    }

    private static void assertInvalidCreateApply(ApiIndirectMerchantService service, CreateApplyRequest request) {
        try {
            service.createApply(request);
            Assert.fail("expected validation failure");
        } catch (IllegalArgumentException expected) {
            // Expected.
        }
    }

    private static void assertInvalidUpload(ApiIndirectMerchantService service, UploadRequest request) {
        try {
            service.upload(request);
            Assert.fail("expected validation failure");
        } catch (IllegalArgumentException expected) {
            // Expected.
        }
    }

    private static <T> T platformLocator(T locator) {
        if (locator instanceof com.douyinpay.api.indirectmerchant.models.MerchantLocator) {
            ((com.douyinpay.api.indirectmerchant.models.MerchantLocator) locator).setIndirectMerchantId("im_1");
        }
        return locator;
    }

    private static <T> void stubResponse(DefaultDouyinpayClient client, Class<T> type, T body) {
        when(client.execute(any(DouyinpayRequest.class), eq(type))).thenReturn(response(body));
    }

    private static <T> DouyinpayResponse<T> response(T body) {
        DouyinpayResponse<T> response = new DouyinpayResponse<T>(null, 200, null,
                Collections.<String, String>emptyMap(), null);
        response.setApiResponse(body);
        return response;
    }
}
