package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class CreateApplyRequest {
    @SerializedName("business_code") private String businessCode;
    private transient String douyinpaySerial;
    @SerializedName("out_indirect_merchant_id") private String outIndirectMerchantId;
    @SerializedName("out_payment_inst_id") private String outPaymentInstId;
    @SerializedName("out_channel_sp_id") private String outChannelSpId;
    @SerializedName("out_payment_inst_sub_id") private String outPaymentInstSubId;
    @SerializedName("indirect_merchant_id") private String indirectMerchantId;
    @SerializedName("subject_info") private JsonObject subjectInfo;
    @SerializedName("identification_info") private JsonObject identificationInfo;
    @SerializedName("business_info") private JsonObject businessInfo;
    @SerializedName("addition_info") private JsonObject additionInfo;
    public void validate() {
        if (businessCode == null || !businessCode.matches("[a-z0-9_]{1,64}")) {
            throw new IllegalArgumentException("business_code must be 1-64 lowercase letters, digits, or underscores");
        }
        if (!MerchantLocator.hasText(outIndirectMerchantId) || !MerchantLocator.hasText(outPaymentInstId)) {
            throw new IllegalArgumentException("out_indirect_merchant_id and out_payment_inst_id are required");
        }
    }
    public String getBusinessCode() { return businessCode; }
    public void setBusinessCode(String value) { businessCode = value; }
    public String getDouyinpaySerial() { return douyinpaySerial; }
    public void setDouyinpaySerial(String value) { douyinpaySerial = value; }
    public String getOutIndirectMerchantId() { return outIndirectMerchantId; }
    public void setOutIndirectMerchantId(String value) { outIndirectMerchantId = value; }
    public String getOutPaymentInstId() { return outPaymentInstId; }
    public void setOutPaymentInstId(String value) { outPaymentInstId = value; }
    public String getOutChannelSpId() { return outChannelSpId; }
    public void setOutChannelSpId(String value) { outChannelSpId = value; }
    public String getOutPaymentInstSubId() { return outPaymentInstSubId; }
    public void setOutPaymentInstSubId(String value) { outPaymentInstSubId = value; }
    public String getIndirectMerchantId() { return indirectMerchantId; }
    public void setIndirectMerchantId(String value) { indirectMerchantId = value; }
    public JsonObject getSubjectInfo() { return subjectInfo; }
    public void setSubjectInfo(JsonObject value) { subjectInfo = value; }
    public JsonObject getIdentificationInfo() { return identificationInfo; }
    public void setIdentificationInfo(JsonObject value) { identificationInfo = value; }
    public JsonObject getBusinessInfo() { return businessInfo; }
    public void setBusinessInfo(JsonObject value) { businessInfo = value; }
    public JsonObject getAdditionInfo() { return additionInfo; }
    public void setAdditionInfo(JsonObject value) { additionInfo = value; }
}
