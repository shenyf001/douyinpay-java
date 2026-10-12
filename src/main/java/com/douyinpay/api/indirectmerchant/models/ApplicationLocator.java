package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

/** Shared locator for an indirect merchant application. */
public class ApplicationLocator {
    /**
     * 字段含义：抖音支付申请单号。
     * 格式规则：字符串。
     * 业务规则：抖音支付生成并下发的申请单号，与业务申请编号选择一种定位方式。
     * 示例：NIM2000002124775691
     */
    @SerializedName("applyment_id") private String applymentId;
    /**
     * 字段含义：业务申请编号。
     * 格式规则：字符串，由数字、小写字母或下划线组成。
     * 业务规则：自定义唯一编号，每个编号对应一个申请单。
     * 示例：1900013511_10000
     */
    @SerializedName("business_code") private String businessCode;
    public void validate() {
        if (MerchantLocator.hasText(applymentId) == MerchantLocator.hasText(businessCode)) {
            throw new IllegalArgumentException("specify exactly one of applyment_id or business_code");
        }
    }
    public String getApplymentId() { return applymentId; }
    public void setApplymentId(String value) { applymentId = value; }
    public String getBusinessCode() { return businessCode; }
    public void setBusinessCode(String value) { businessCode = value; }
}
