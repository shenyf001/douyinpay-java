package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

public class ApplymentStatusNotification {
    @SerializedName("sp_mchid") private String spMchid;
    @SerializedName("applyment_id") private String applymentId;
    @SerializedName("business_code") private String businessCode;
    @SerializedName("indirect_merchant_id") private String indirectMerchantId;
    @SerializedName("applyment_state") private String applymentState;
    @SerializedName("fail_reason") private String failReason;
    public String getSpMchid() { return spMchid; }
    public void setSpMchid(String value) { spMchid = value; }
    public String getApplymentId() { return applymentId; }
    public void setApplymentId(String value) { applymentId = value; }
    public String getBusinessCode() { return businessCode; }
    public void setBusinessCode(String value) { businessCode = value; }
    public String getIndirectMerchantId() { return indirectMerchantId; }
    public void setIndirectMerchantId(String value) { indirectMerchantId = value; }
    public String getApplymentState() { return applymentState; }
    public void setApplymentState(String value) { applymentState = value; }
    public String getFailReason() { return failReason; }
    public void setFailReason(String value) { failReason = value; }
}
