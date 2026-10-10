package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

/** Shared locator for an indirect merchant application. */
public class ApplicationLocator {
    @SerializedName("applyment_id") private String applymentId;
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
