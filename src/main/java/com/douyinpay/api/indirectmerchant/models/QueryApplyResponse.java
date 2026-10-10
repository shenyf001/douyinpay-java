package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

public class QueryApplyResponse {
    @SerializedName("business_code") private String businessCode;
    @SerializedName("applyment_id") private String applymentId;
    @SerializedName("indirect_merchant_id") private String indirectMerchantId;
    @SerializedName("applyment_state") private String applymentState;
    @SerializedName("audit_detail") private JsonArray auditDetail;
    public String getBusinessCode() { return businessCode; }
    public void setBusinessCode(String value) { businessCode = value; }
    public String getApplymentId() { return applymentId; }
    public void setApplymentId(String value) { applymentId = value; }
    public String getIndirectMerchantId() { return indirectMerchantId; }
    public void setIndirectMerchantId(String value) { indirectMerchantId = value; }
    public String getApplymentState() { return applymentState; }
    public void setApplymentState(String value) { applymentState = value; }
    public JsonArray getAuditDetail() { return auditDetail; }
    public void setAuditDetail(JsonArray value) { auditDetail = value; }
}
