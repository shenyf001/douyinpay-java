package com.doupay.api.indirectmerchant;

import com.douyinpay.api.DefaultDouyinpayClient;
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
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApiIndirectMerchantServiceTest {
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
        service.createApply(create);
        QueryApplyRequest queryApply = new QueryApplyRequest();
        queryApply.setBusinessCode("apply_1");
        service.queryApply(queryApply);
        CancelApplyRequest cancel = new CancelApplyRequest();
        cancel.setApplymentId("applyment_1");
        service.cancelApply(cancel);
        QueryMerchantRequest merchant = platformLocator(new QueryMerchantRequest());
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
        Assert.assertEquals("?business_code=apply_1",
                requests.getAllValues().get(1).getPath().substring(
                        "/v1/merchant/indirect_merchant/query_apply".length()));
        Assert.assertEquals(HttpMethod.POST, requests.getAllValues().get(2).getHttpMethod());
        Assert.assertEquals("?indirect_merchant_id=im_1",
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
