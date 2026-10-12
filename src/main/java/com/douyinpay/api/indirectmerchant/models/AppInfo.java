package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

/** 绑定与查询应用接口返回的应用信息。 */
public class AppInfo {
    /**
     * 字段含义：应用 APPID。
     */
    @SerializedName("app_id") private String appId;
    /**
     * 字段含义：中文应用类型，包括小程序应用、移动应用、网站应用。
     */
    @SerializedName("app_type") private String appType;
    /**
     * 字段含义：绑定状态，BIND 表示已绑定，WAIT_CONFIRM 表示待应用开发者确认。
     */
    @SerializedName("bind_status") private String bindStatus;
    public String getAppId() { return appId; }
    public void setAppId(String value) { appId = value; }
    public String getAppType() { return appType; }
    public void setAppType(String value) { appType = value; }
    public String getBindStatus() { return bindStatus; }
    public void setBindStatus(String value) { bindStatus = value; }
}
