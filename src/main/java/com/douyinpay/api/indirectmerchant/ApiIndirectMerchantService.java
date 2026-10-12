package com.douyinpay.api.indirectmerchant;

import com.douyinpay.api.DefaultDouyinpayClient;
import com.douyinpay.api.DouyinpayClient;
import com.douyinpay.api.DouyinpayMultipartRequest;
import com.douyinpay.api.DouyinpayRequest;
import com.douyinpay.api.DouyinpayResponse;
import com.douyinpay.api.indirectmerchant.models.ApplymentStatusNotification;
import com.douyinpay.api.indirectmerchant.models.ApplicationLocator;
import com.douyinpay.api.indirectmerchant.models.BindAppIdRequest;
import com.douyinpay.api.indirectmerchant.models.BindAppIdResponse;
import com.douyinpay.api.indirectmerchant.models.CancelApplyRequest;
import com.douyinpay.api.indirectmerchant.models.CreateApplyRequest;
import com.douyinpay.api.indirectmerchant.models.CreateApplyResponse;
import com.douyinpay.api.indirectmerchant.models.MerchantLocator;
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
import com.douyinpay.component.crypto.RsaCryptor;
import com.douyinpay.define.Constants;
import com.douyinpay.define.DomainName;
import com.douyinpay.exception.DouyinpayException;
import com.douyinpay.util.GsonUtil;
import com.douyinpay.util.PemUtil;
import com.douyinpay.util.StringUtil;
import com.google.gson.JsonObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Indirect merchant application, merchant management, AppID, upload, and notification APIs. */
public class ApiIndirectMerchantService {
    private static final String CREATE_APPLY_PATH = "/v1/merchant/indirect_merchant/create_apply";
    private static final String QUERY_APPLY_PATH = "/v1/merchant/indirect_merchant/query_apply";
    private static final String CANCEL_APPLY_PATH = "/v1/merchant/indirect_merchant/cancel_apply";
    private static final String QUERY_MERCHANT_PATH = "/v1/merchant/indirect_merchant/query_merchant";
    private static final String MODIFY_MERCHANT_PATH = "/v1/merchant/indirect_merchant/modify";
    private static final String BIND_APP_ID_PATH = "/v1/merchant/indirect_merchant/bind_app_id";
    private static final String QUERY_APP_ID_PATH = "/v1/merchant/indirect_merchant/query_app_id";
    private static final String UPLOAD_PATH = "/v1/merchant/upload";
    private static final int MAX_UPLOAD_BYTES = 5 * 1024 * 1024;

    private final DouyinpayClient douyinpayClient;
    private final DomainName domainName;
    private final X509Certificate platformCertificate;
    private final PrivateKey merchantPrivateKey;

    private ApiIndirectMerchantService(DouyinpayClient douyinpayClient, DomainName domainName,
                                      X509Certificate platformCertificate, PrivateKey merchantPrivateKey) {
        this.douyinpayClient = douyinpayClient;
        this.domainName = domainName;
        this.platformCertificate = platformCertificate;
        this.merchantPrivateKey = merchantPrivateKey;
    }

    public static class Builder {
        private DouyinpayClient douyinpayClient;
        private DomainName domainName;
        private X509Certificate platformCertificate;
        private PrivateKey merchantPrivateKey;
        public Builder douyinpayClient(DouyinpayClient value) { douyinpayClient = value; return this; }
        public Builder domainName(DomainName value) { domainName = value; return this; }
        /** 设置用于敏感字段加密的平台证书，可供自定义 Client 使用。 */
        public Builder platformCertificate(X509Certificate value) { platformCertificate = value; return this; }
        /** 设置用于敏感字段解密的商户私钥，可供自定义 Client 使用。 */
        public Builder merchantPrivateKey(PrivateKey value) { merchantPrivateKey = value; return this; }
        public ApiIndirectMerchantService build() {
            return new ApiIndirectMerchantService(douyinpayClient, domainName, platformCertificate, merchantPrivateKey);
        }
    }

    public String getRequestUrl() {
        return domainName == null ? DomainName.API.getValue() : domainName.getValue();
    }

    /**
     * 提交间连商户入驻申请，返回抖音支付分配的申请单号。
     * 在请求副本中使用平台证书加密证件持有人姓名和证件号码，
     * 并核对 Douyinpay-Serial 与加密证书序列号一致。
     *
     * @param request 入驻申请明文资料
     * @return 申请单号
     */
    public CreateApplyResponse createApply(CreateApplyRequest request) {
        request.validate();
        JsonObject body = GsonUtil.getGson().toJsonTree(request).getAsJsonObject();
        Map<String, String> headers = serialHeader(request);
        if (body.has("identification_info")) {
            JsonObject identification = body.getAsJsonObject("identification_info");
            if (hasSensitiveValue(identification, "identification_name")
                    || hasSensitiveValue(identification, "identification_number")) {
                X509Certificate certificate = encryptionCertificate();
                if (certificate == null) {
                    throw new DouyinpayException("platform certificate is required to encrypt identification fields");
                }
                if (!PemUtil.getSerialNumber(certificate).equals(headers.get(Constants.DOUYIN_PAY_SERIAL))) {
                    throw new DouyinpayException("Douyinpay-Serial must match the encryption certificate");
                }
                encryptField(identification, "identification_name", certificate);
                encryptField(identification, "identification_number", certificate);
            }
        }
        return execute(HttpMethod.POST, CREATE_APPLY_PATH, headers, GsonUtil.getGson().toJson(body),
                CreateApplyResponse.class);
    }

    /** 通过申请单号或业务申请编号查询申请状态及资料审核结果。 */
    public QueryApplyResponse queryApply(QueryApplyRequest request) {
        request.validate();
        return execute(HttpMethod.GET, QUERY_APPLY_PATH + applicationQuery(request), null, null, QueryApplyResponse.class);
    }

    /** 撤销待提交或审核中的入驻申请，申请当前状态由服务端校验。 */
    public void cancelApply(CancelApplyRequest request) {
        request.validate();
        execute(HttpMethod.POST, CANCEL_APPLY_PATH, null, GsonUtil.objectToJson(request), Object.class);
    }

    /** 查询商户资料，响应验签成功后使用商户私钥解密证件持有人姓名和证件号码。 */
    public QueryMerchantResponse queryMerchant(QueryMerchantRequest request) {
        request.validate();
        QueryMerchantResponse result = execute(HttpMethod.GET, QUERY_MERCHANT_PATH + merchantQuery(request), null, null,
                QueryMerchantResponse.class);
        if (result.getIdentificationInfo() != null) {
            com.douyinpay.api.indirectmerchant.models.QueryMerchantIdentificationInfo info =
                    result.getIdentificationInfo();
            PrivateKey key = merchantPrivateKey;
            if (key == null && douyinpayClient instanceof DefaultDouyinpayClient) {
                key = ((DefaultDouyinpayClient) douyinpayClient).getMerchantPrivateKey();
            }
            String name = decryptField(info.getIdentificationName(), key);
            String number = decryptField(info.getIdentificationNumber(), key);
            info.setIdentificationName(name);
            info.setIdentificationNumber(number);
        }
        return result;
    }

    /** 修改商户简称和客服电话。 */
    public void modifyMerchant(ModifyMerchantRequest request) {
        request.validate();
        execute(HttpMethod.POST, MODIFY_MERCHANT_PATH, null, GsonUtil.objectToJson(request), Object.class);
    }

    /** 新增应用绑定；非同主体小程序可能需要开发者确认授权。 */
    public BindAppIdResponse bindAppId(BindAppIdRequest request) {
        request.validate();
        return execute(HttpMethod.POST, BIND_APP_ID_PATH, null, GsonUtil.objectToJson(request), BindAppIdResponse.class);
    }

    /** 查询商户配置的应用信息，可指定单个应用。 */
    public QueryAppIdResponse queryAppId(QueryAppIdRequest request) {
        request.validate();
        String path = QUERY_APP_ID_PATH + merchantQuery(request);
        if (MerchantLocator.hasText(request.getAppId())) {
            path += "&app_id=" + StringUtil.urlEncode(request.getAppId());
        }
        return execute(HttpMethod.GET, path, null, null, QueryAppIdResponse.class);
    }

    /** 上传资质图片，以meta JSON签名并返回media_id。 */
    public UploadResponse upload(UploadRequest request) {
        validateUpload(request);
        JsonObject meta = new JsonObject();
        meta.addProperty("filename", request.getFileName());
        meta.addProperty("sha256", sha256(request.getFileContent()));
        String signBody = GsonUtil.getGson().toJson(meta);
        DouyinpayMultipartRequest multipartRequest = new DouyinpayMultipartRequest(HttpMethod.POST, getRequestUrl(),
                UPLOAD_PATH, Collections.<String, String>emptyMap(), signBody, "file", request.getFileName(),
                request.getContentType(), request.getFileContent());
        DouyinpayResponse<UploadResponse> response = douyinpayClient.executeMultipart(multipartRequest,
                UploadResponse.class);
        return response.getApiResponse();
    }

    /** 使用通用解析器先验签、再进行AES-256-GCM解密，返回独立申请状态通知模型。 */
    public ApplymentStatusNotification parseApplymentStatusNotification(
            NotificationParser parser, RequestParam requestParam) {
        return parser.parse(requestParam, ApplymentStatusNotification.class);
    }

    private <T> T execute(HttpMethod method, String path, Map<String, String> headers, String body,
                          Class<T> responseClass) {
        DouyinpayRequest request = new DouyinpayRequest(method, getRequestUrl(), path, headers, body);
        DouyinpayResponse<T> response = douyinpayClient.execute(request, responseClass);
        return response.getApiResponse();
    }

    private static String applicationQuery(ApplicationLocator request) {
        if (MerchantLocator.hasText(request.getApplymentId())) {
            return "?applyment_id=" + StringUtil.urlEncode(request.getApplymentId());
        }
        return "?business_code=" + StringUtil.urlEncode(request.getBusinessCode());
    }

    private static String merchantQuery(MerchantLocator request) {
        String query;
        if (MerchantLocator.hasText(request.getIndirectMerchantId())) {
            query = "?indirect_merchant_id=" + StringUtil.urlEncode(request.getIndirectMerchantId());
        } else {
            query = "?out_indirect_merchant_id=" + StringUtil.urlEncode(request.getOutIndirectMerchantId())
                    + "&out_payment_inst_id=" + StringUtil.urlEncode(request.getOutPaymentInstId());
        }
        if (MerchantLocator.hasText(request.getOutChannelSpId())) {
            query += "&out_channel_sp_id=" + StringUtil.urlEncode(request.getOutChannelSpId());
        }
        return query;
    }

    private Map<String, String> serialHeader(CreateApplyRequest request) {
        String serial = request.getDouyinpaySerial();
        if (!MerchantLocator.hasText(serial)) {
            X509Certificate certificate = encryptionCertificate();
            if (certificate != null) {
                serial = PemUtil.getSerialNumber(certificate);
            }
        }
        if (!MerchantLocator.hasText(serial)) {
            throw new DouyinpayException("Douyinpay-Serial is required for create apply");
        }
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(Constants.DOUYIN_PAY_SERIAL, serial);
        return headers;
    }

    private X509Certificate encryptionCertificate() {
        if (platformCertificate != null) {
            return platformCertificate;
        }
        return douyinpayClient instanceof DefaultDouyinpayClient
                ? ((DefaultDouyinpayClient) douyinpayClient).getPlatformCertificate() : null;
    }

    private static boolean hasSensitiveValue(JsonObject object, String field) {
        return object.has(field) && !object.get(field).isJsonNull() && !object.get(field).getAsString().isEmpty();
    }

    private static void encryptField(JsonObject object, String field, X509Certificate certificate) {
        if (hasSensitiveValue(object, field)) {
            object.addProperty(field, new RsaCryptor().encrypt(object.get(field).getAsString(), certificate));
        }
    }

    private static String decryptField(String ciphertext, PrivateKey key) {
        return ciphertext == null || ciphertext.isEmpty() ? ciphertext : new RsaCryptor().decrypt(ciphertext, key);
    }

    private static void validateUpload(UploadRequest request) {
        if (request == null || !MerchantLocator.hasText(request.getFileName()) || request.getFileContent() == null
                || request.getFileContent().length == 0) {
            throw new IllegalArgumentException("file name and non-empty content are required");
        }
        if (request.getFileName().length() > 128 || request.getFileContent().length > MAX_UPLOAD_BYTES) {
            throw new IllegalArgumentException("file exceeds upload limits");
        }
        String name = request.getFileName().toLowerCase(java.util.Locale.ROOT);
        if (!(name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".bmp") || name.endsWith(".png"))) {
            throw new IllegalArgumentException("only JPG, BMP, and PNG files are supported");
        }
    }

    private static String sha256(byte[] content) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(content);
            StringBuilder result = new StringBuilder(hash.length * 2);
            for (byte value : hash) {
                result.append(String.format("%02x", value & 0xff));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
