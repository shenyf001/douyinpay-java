package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

public class BindAppIdResponse {
    /**
     * 字段含义：应用信息，绑定成功或待授权时返回。
     */
    @SerializedName("app_info") private AppInfo appInfo;
    public AppInfo getAppInfo() { return appInfo; }
    public void setAppInfo(AppInfo value) { appInfo = value; }
}
