package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

public class QueryAppIdRequest extends MerchantLocator {
    /**
     * 字段含义：应用 APPID。
     * 格式规则：字符串。
     * 业务规则：用于查询间连商户配置的应用 APPID 信息。
     * 示例：
     * TODO: 官方文档未提供标准示例值，暂留空。
     */
    @SerializedName("app_id") private String appId;
    public String getAppId() { return appId; }
    public void setAppId(String value) { appId = value; }
}
