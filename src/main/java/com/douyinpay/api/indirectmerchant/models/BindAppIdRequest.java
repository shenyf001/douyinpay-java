package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

public class BindAppIdRequest extends MerchantLocator {
    @SerializedName("app_id") private String appId;
    public void validate() {
        super.validate();
        if (!hasText(appId)) {
            throw new IllegalArgumentException("app_id is required");
        }
    }
    public String getAppId() { return appId; }
    public void setAppId(String value) { appId = value; }
}
