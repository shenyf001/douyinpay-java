package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

public class QueryAppIdResponse {
    @SerializedName("app_info_list") private JsonArray appInfoList;
    public JsonArray getAppInfoList() { return appInfoList; }
    public void setAppInfoList(JsonArray value) { appInfoList = value; }
}
