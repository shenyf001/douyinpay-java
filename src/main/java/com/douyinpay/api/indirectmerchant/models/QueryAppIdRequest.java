package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

public class QueryAppIdRequest extends MerchantLocator {
    @SerializedName("app_id") private String appId;
    public String getAppId() { return appId; }
    public void setAppId(String value) { appId = value; }
}
