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
import com.douyinpay.define.Constants;
import com.douyinpay.define.DomainName;
import com.douyinpay.exception.DouyinpayException;
import com.douyinpay.util.GsonUtil;
import com.douyinpay.util.PemUtil;
import com.douyinpay.util.StringUtil;
import com.google.gson.JsonObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    private ApiIndirectMerchantService(DouyinpayClient douyinpayClient, DomainName domainName) {
        this.douyinpayClient = douyinpayClient;
        this.domainName = domainName;
    }

    public static class Builder {
        private DouyinpayClient douyinpayClient;
        private DomainName domainName;
        public Builder douyinpayClient(DouyinpayClient value) { douyinpayClient = value; return this; }
        public Builder domainName(DomainName value) { domainName = value; return this; }
        public ApiIndirectMerchantService build() {
            return new ApiIndirectMerchantService(douyinpayClient, domainName);
        }
    }

    public String getRequestUrl() {
        return domainName == null ? DomainName.API.getValue() : domainName.getValue();
    }

    public CreateApplyResponse createApply(CreateApplyRequest request) {
        request.validate();
        return execute(HttpMethod.POST, CREATE_APPLY_PATH, serialHeader(request), GsonUtil.objectToJson(request),
                CreateApplyResponse.class);
    }

    public QueryApplyResponse queryApply(QueryApplyRequest request) {
        request.validate();
        return execute(HttpMethod.GET, QUERY_APPLY_PATH + applicationQuery(request), null, null, QueryApplyResponse.class);
    }

    public void cancelApply(CancelApplyRequest request) {
        request.validate();
        execute(HttpMethod.POST, CANCEL_APPLY_PATH, null, GsonUtil.objectToJson(request), Object.class);
    }

    public QueryMerchantResponse queryMerchant(QueryMerchantRequest request) {
        request.validate();
        return execute(HttpMethod.GET, QUERY_MERCHANT_PATH + merchantQuery(request), null, null,
                QueryMerchantResponse.class);
    }

    public void modifyMerchant(ModifyMerchantRequest request) {
        request.validate();
        execute(HttpMethod.POST, MODIFY_MERCHANT_PATH, null, GsonUtil.objectToJson(request), Object.class);
    }

    public BindAppIdResponse bindAppId(BindAppIdRequest request) {
        request.validate();
        return execute(HttpMethod.POST, BIND_APP_ID_PATH, null, GsonUtil.objectToJson(request), BindAppIdResponse.class);
    }

    public QueryAppIdResponse queryAppId(QueryAppIdRequest request) {
        request.validate();
        String path = QUERY_APP_ID_PATH + merchantQuery(request);
        if (MerchantLocator.hasText(request.getAppId())) {
            path += "&app_id=" + StringUtil.urlEncode(request.getAppId());
        }
        return execute(HttpMethod.GET, path, null, null, QueryAppIdResponse.class);
    }

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
        if (!MerchantLocator.hasText(serial) && douyinpayClient instanceof DefaultDouyinpayClient) {
            X509Certificate certificate = ((DefaultDouyinpayClient) douyinpayClient).getPlatformCertificate();
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
