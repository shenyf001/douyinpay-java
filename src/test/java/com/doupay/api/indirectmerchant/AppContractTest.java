package com.doupay.api.indirectmerchant;

import com.douyinpay.api.*;
import com.douyinpay.api.indirectmerchant.ApiIndirectMerchantService;
import com.douyinpay.api.indirectmerchant.models.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;

public class AppContractTest {
    @Test
    public void merchantChannelShouldReachModifyBindAndQuery() {
        final DouyinpayRequest[] captured = new DouyinpayRequest[1];
        DouyinpayClient client = new DouyinpayClient() {
            public <T> DouyinpayResponse<T> execute(DouyinpayRequest request, Class<T> type) {
                captured[0] = request;
                DouyinpayResponse<T> response = new DouyinpayResponse<T>(null, 200, null, null, null);
                response.setApiResponse(new Gson().fromJson("{\"app_info\":{\"app_id\":\"app_1\","
                        + "\"app_type\":\"移动应用\",\"bind_status\":\"WAIT_CONFIRM\"},"
                        + "\"app_info_list\":[{\"app_id\":\"app_1\",\"app_type\":\"移动应用\","
                        + "\"bind_status\":\"FUTURE\"}]}", type));
                return response;
            }
        };
        ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder().douyinpayClient(client).build();
        ModifyMerchantRequest modify = external(new ModifyMerchantRequest());
        modify.setMerchantShortName("商户");
        service.modifyMerchant(modify);
        Assert.assertEquals("channel", new Gson().fromJson(captured[0].getBody(), JsonObject.class)
                .get("out_channel_sp_id").getAsString());
        BindAppIdRequest bind = external(new BindAppIdRequest());
        bind.setAppId("app_1");
        Assert.assertEquals("WAIT_CONFIRM", service.bindAppId(bind).getAppInfo().getBindStatus());
        Assert.assertEquals("channel", new Gson().fromJson(captured[0].getBody(), JsonObject.class)
                .get("out_channel_sp_id").getAsString());
        QueryAppIdRequest query = external(new QueryAppIdRequest());
        query.setAppId("app_1");
        QueryAppIdResponse result = service.queryAppId(query);
        Assert.assertTrue(captured[0].getPath().contains("out_channel_sp_id=channel"));
        Assert.assertTrue(captured[0].getPath().contains("app_id=app_1"));
        Assert.assertEquals("FUTURE", result.getAppInfoList().get(0).getBindStatus());
        Assert.assertEquals("移动应用", result.getAppInfoList().get(0).getAppType());
    }

    private static <T extends MerchantLocator> T external(T request) {
        request.setOutIndirectMerchantId("external");
        request.setOutPaymentInstId("institution");
        request.setOutChannelSpId("channel");
        return request;
    }
}
