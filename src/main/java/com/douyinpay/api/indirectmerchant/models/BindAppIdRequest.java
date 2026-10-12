package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

public class BindAppIdRequest extends MerchantLocator {
    /**
     * 字段含义：关联应用 APPID。
     * 格式规则：字符串。
     * 业务规则：需要给间连商户绑定的应用 APPID；接口只支持新增配置，不支持修改。可绑定与间连商户、渠道商或从业机构公司名字相同的网站应用、移动应用和小程序应用；非同主体的小程序应用也可发起绑定，发起后需开发者登录抖音开放平台，在小程序应用的抖音支付权限页面确认同意授权。
     * 示例：tt2342ffw32fwef23r
     */
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
