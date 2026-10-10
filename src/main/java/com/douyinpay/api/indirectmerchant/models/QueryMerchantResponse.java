package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class QueryMerchantResponse {
    @SerializedName("indirect_merchant_id") private String indirectMerchantId;
    @SerializedName("out_indirect_merchant_id") private String outIndirectMerchantId;
    @SerializedName("out_payment_inst_id") private String outPaymentInstId;
    @SerializedName("applyment_id") private String applymentId;
    @SerializedName("subject_info") private JsonObject subjectInfo;
    @SerializedName("identification_info") private JsonObject identificationInfo;
    @SerializedName("business_info") private JsonObject businessInfo;
    public String getIndirectMerchantId() { return indirectMerchantId; }
    public void setIndirectMerchantId(String value) { indirectMerchantId = value; }
    public String getOutIndirectMerchantId() { return outIndirectMerchantId; }
    public void setOutIndirectMerchantId(String value) { outIndirectMerchantId = value; }
    public String getOutPaymentInstId() { return outPaymentInstId; }
    public void setOutPaymentInstId(String value) { outPaymentInstId = value; }
    public String getApplymentId() { return applymentId; }
    public void setApplymentId(String value) { applymentId = value; }
    public JsonObject getSubjectInfo() { return subjectInfo; }
    public void setSubjectInfo(JsonObject value) { subjectInfo = value; }
    public JsonObject getIdentificationInfo() { return identificationInfo; }
    public void setIdentificationInfo(JsonObject value) { identificationInfo = value; }
    public JsonObject getBusinessInfo() { return businessInfo; }
    public void setBusinessInfo(JsonObject value) { businessInfo = value; }
}
