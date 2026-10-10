package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class BindAppIdResponse {
    @SerializedName("app_info") private JsonObject appInfo;
    public JsonObject getAppInfo() { return appInfo; }
    public void setAppInfo(JsonObject value) { appInfo = value; }
}
