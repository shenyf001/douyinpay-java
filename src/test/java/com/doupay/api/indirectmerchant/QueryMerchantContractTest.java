package com.doupay.api.indirectmerchant;

import com.douyinpay.api.DefaultDouyinpayClient;
import com.douyinpay.api.DouyinpayClient;
import com.douyinpay.api.DouyinpayRequest;
import com.douyinpay.api.DouyinpayResponse;
import com.douyinpay.api.indirectmerchant.ApiIndirectMerchantService;
import com.douyinpay.api.indirectmerchant.models.QueryMerchantRequest;
import com.douyinpay.api.indirectmerchant.models.QueryMerchantResponse;
import com.douyinpay.component.http.HttpMethod;
import com.douyinpay.exception.DouyinpayException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
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

public class QueryMerchantContractTest {
    private static final Gson GSON = new Gson();
    private static final String[] SENSITIVE_FIELDS = {"identification_name", "identification_number"};
    private static final String CONTRACT_JSON = "{"
            + "\"indirect_merchant_id\":\"2026045587123820\",\"out_indirect_merchant_id\":\"10434920032\","
            + "\"out_payment_inst_id\":\"41110000\",\"out_channel_sp_id\":\"C023828488\","
            + "\"business_code\":\"602323_10000\",\"applyment_id\":\"NIM2000002124775691\","
            + "\"subject_info\":{\"subject_type\":\"SUBJECT_TYPE_ENTERPRISE\",\"certificate_info\":{"
            + "\"cert_no\":\"91310000TEST123456\",\"merchant_name\":\"Contract Shop\","
            + "\"cert_copy\":\"license_media\",\"register_address\":\"123 Contract Road\","
            + "\"legal_person_name\":\"Alice Zhang\",\"period_begin\":\"2019-08-01\",\"period_end\":\"2039-08-01\"}},"
            + "\"identification_info\":{\"identification_type\":\"IDENTIFICATION_TYPE_IDCARD\","
            + "\"identification_name\":\"Alice Zhang\",\"identification_number\":\"110101199001011234\","
            + "\"period_begin\":\"2019-08-01\",\"period_end\":\"2039-08-01\","
            + "\"identification_front_copy\":\"front_media\",\"identification_back_copy\":\"back_media\"},"
            + "\"business_info\":{\"merchant_short_name\":\"Contract Shop\",\"service_phone\":\"18042002458\","
            + "\"mcc\":\"7300\",\"special_industry_pics\":[\"industry_1\",\"industry_2\"]}}";

    @Test
    public void testQueryMerchant_successWithCustomClientAndBuilderKey() throws Exception {
        KeyPair keys = keys();
        RecordingClient client = new RecordingClient(encryptedContract(keys));
        ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder()
                .douyinpayClient(client).merchantPrivateKey(keys.getPrivate()).build();

        for (boolean externalLocator : new boolean[] {false, true}) {
            QueryMerchantResponse result = service.queryMerchant(request(externalLocator));
            Assert.assertEquals(contract(), GSON.toJsonTree(result));
            assertRequest(client.requests.get(client.requests.size() - 1), externalLocator);
        }
        Assert.assertEquals(2, client.requests.size());
    }

    @Test
    public void testQueryMerchant_defaultClientPrivateKeyFallback() throws Exception {
        KeyPair keys = keys();
        DefaultDouyinpayClient client = defaultClient(encryptedContract(keys));
        when(client.getMerchantPrivateKey()).thenReturn(keys.getPrivate());
        ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder()
                .douyinpayClient(client).build();

        Assert.assertEquals(contract(), GSON.toJsonTree(service.queryMerchant(request(false))));

        ArgumentCaptor<DouyinpayRequest> captured = ArgumentCaptor.forClass(DouyinpayRequest.class);
        verify(client).execute(captured.capture(), eq(QueryMerchantResponse.class));
        assertRequest(captured.getValue(), false);
    }

    @Test
    public void testQueryMerchant_builderKeyOverridesDefaultClientKey() throws Exception {
        KeyPair keys = keys();
        DefaultDouyinpayClient client = defaultClient(encryptedContract(keys));
        when(client.getMerchantPrivateKey()).thenReturn(keys().getPrivate());
        ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder()
                .douyinpayClient(client).merchantPrivateKey(keys.getPrivate()).build();

        Assert.assertEquals(contract(), GSON.toJsonTree(service.queryMerchant(request(false))));
    }

    @Test
    public void testQueryMerchant_missingKeyRejectsCiphertext() throws Exception {
        RecordingClient client = new RecordingClient(encryptedContract(keys()));
        ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder()
                .douyinpayClient(client).build();

        assertDecryptionRejected(service, client);
    }

    @Test
    public void testQueryMerchant_wrongKeyRejectsCiphertext() throws Exception {
        RecordingClient client = new RecordingClient(encryptedContract(keys()));
        ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder()
                .douyinpayClient(client).merchantPrivateKey(keys().getPrivate()).build();

        assertDecryptionRejected(service, client);
    }

    @Test
    public void testQueryMerchant_invalidBase64RejectsEitherSensitiveField() throws Exception {
        KeyPair keys = keys();
        for (String field : SENSITIVE_FIELDS) {
            JsonObject wire = encryptedContract(keys);
            wire.getAsJsonObject("identification_info").addProperty(field, "invalid!base64");
            RecordingClient client = new RecordingClient(wire);
            ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder()
                    .douyinpayClient(client).merchantPrivateKey(keys.getPrivate()).build();

            assertDecryptionRejected(service, client);
        }
    }

    @Test
    public void testQueryMerchant_singleSensitiveFieldDecrypts() throws Exception {
        KeyPair keys = keys();
        for (String absentField : SENSITIVE_FIELDS) {
            JsonObject expected = contract();
            expected.getAsJsonObject("identification_info").remove(absentField);
            JsonObject wire = encryptedContract(keys);
            wire.getAsJsonObject("identification_info").remove(absentField);
            RecordingClient client = new RecordingClient(wire);
            ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder()
                    .douyinpayClient(client).merchantPrivateKey(keys.getPrivate()).build();

            Assert.assertEquals(expected, GSON.toJsonTree(service.queryMerchant(request(false))));
            Assert.assertEquals(1, client.requests.size());
        }
    }

    @Test
    public void testQueryMerchant_absentOrEmptySensitiveFieldsNeedNoKey() {
        for (boolean absentIdentification : new boolean[] {false, true}) {
            JsonObject expected = contract();
            if (absentIdentification) {
                expected.remove("identification_info");
            } else {
                JsonObject identification = expected.getAsJsonObject("identification_info");
                identification.remove("identification_name");
                identification.addProperty("identification_number", "");
            }
            RecordingClient client = new RecordingClient(expected);
            ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder()
                    .douyinpayClient(client).build();

            Assert.assertEquals(expected, GSON.toJsonTree(service.queryMerchant(request(false))));
            Assert.assertEquals(1, client.requests.size());
        }
    }

    private static JsonObject contract() {
        return GSON.fromJson(CONTRACT_JSON, JsonObject.class);
    }

    private static KeyPair keys() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    private static JsonObject encryptedContract(KeyPair keys) throws Exception {
        JsonObject wire = contract();
        JsonObject identification = wire.getAsJsonObject("identification_info");
        for (String field : SENSITIVE_FIELDS) {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keys.getPublic());
            byte[] encrypted = cipher.doFinal(identification.get(field).getAsString()
                    .getBytes(StandardCharsets.UTF_8));
            Assert.assertEquals(256, encrypted.length);
            identification.addProperty(field, Base64.getEncoder().encodeToString(encrypted));
        }
        return wire;
    }

    private static QueryMerchantRequest request(boolean externalLocator) {
        QueryMerchantRequest request = new QueryMerchantRequest();
        if (externalLocator) {
            request.setOutIndirectMerchantId("out merchant/1");
            request.setOutPaymentInstId("payment+1");
            request.setOutChannelSpId("channel&1");
        } else {
            request.setIndirectMerchantId("2026045587123820");
        }
        return request;
    }

    private static void assertRequest(DouyinpayRequest sent, boolean externalLocator) {
        Assert.assertEquals(HttpMethod.GET, sent.getHttpMethod());
        Assert.assertEquals("/v1/merchant/indirect_merchant/query_merchant"
                + (externalLocator
                ? "?out_indirect_merchant_id=out+merchant%2F1&out_payment_inst_id=payment%2B1"
                        + "&out_channel_sp_id=channel%261"
                : "?indirect_merchant_id=2026045587123820"), sent.getPath());
        Assert.assertNull(sent.getBody());
    }

    private static void assertDecryptionRejected(ApiIndirectMerchantService service, RecordingClient client) {
        try {
            service.queryMerchant(request(false));
            Assert.fail("expected query merchant decryption failure");
        } catch (DouyinpayException expected) {
            Assert.assertNotNull(expected.getMessage());
            Assert.assertFalse(expected.getMessage().isEmpty());
        }
        Assert.assertEquals(1, client.requests.size());
        assertRequest(client.requests.get(0), false);
    }

    private static DefaultDouyinpayClient defaultClient(JsonObject wire) {
        DefaultDouyinpayClient client = mock(DefaultDouyinpayClient.class);
        when(client.execute(any(DouyinpayRequest.class), eq(QueryMerchantResponse.class)))
                .thenAnswer(invocation -> response(GSON.fromJson(wire, QueryMerchantResponse.class)));
        return client;
    }

    private static <T> DouyinpayResponse<T> response(T body) {
        DouyinpayResponse<T> response = new DouyinpayResponse<T>(null, 200, null,
                Collections.<String, String>emptyMap(), null);
        response.setApiResponse(body);
        return response;
    }

    private static class RecordingClient implements DouyinpayClient {
        private final String json;
        private final List<DouyinpayRequest> requests = new ArrayList<DouyinpayRequest>();

        private RecordingClient(JsonObject wire) {
            json = GSON.toJson(wire);
        }

        @Override
        public <T> DouyinpayResponse<T> execute(DouyinpayRequest request, Class<T> responseClass) {
            Assert.assertEquals(QueryMerchantResponse.class, responseClass);
            requests.add(request);
            return response(GSON.fromJson(json, responseClass));
        }
    }
}
