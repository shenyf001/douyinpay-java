package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

public class ModifyMerchantRequest extends MerchantLocator {
    /**
     * 字段含义：商户简称。
     * 格式规则：字符串，长度为1-32字符；前后不能有空格、制表符、换行符；不能仅含特殊字符；仅能填写数字、英文字母、汉字及特殊字符；仅支持 UTF-8 格式。
     * 业务规则：在支付完成页向买家展示，需与抖音支付经营类目相关。
     * 示例：个体户张三
     */
    @SerializedName("merchant_short_name") private String merchantShortName;
    /**
     * 字段含义：客服电话。
     * 格式规则：字符串，前后不能有空格、制表符、换行符；为11位数字的手机号码，或由数字、连字符“-”、加号“+”组成的5-20位号码。
     * 业务规则：将在交易记录中向买家展示，请确保电话畅通以便平台回拨确认。
     * 示例：18042002458
     */
    @SerializedName("service_phone") private String servicePhone;
    public String getMerchantShortName() { return merchantShortName; }
    public void setMerchantShortName(String value) { merchantShortName = value; }
    public String getServicePhone() { return servicePhone; }
    public void setServicePhone(String value) { servicePhone = value; }
}
