package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class CreateApplyRequest {
    @SerializedName("business_code") private String businessCode;
    @SerializedName("subject_info") private JsonObject subjectInfo;
    @SerializedName("identification_info") private JsonObject identificationInfo;
    @SerializedName("business_info") private JsonObject businessInfo;
    @SerializedName("addition_info") private JsonObject additionInfo;
    public void validate() {
        if (businessCode == null || !businessCode.matches("[a-z0-9_]{1,64}")) {
            throw new IllegalArgumentException("business_code must be 1-64 lowercase letters, digits, or underscores");
        }
    }
    public String getBusinessCode() { return businessCode; }
    public void setBusinessCode(String value) { businessCode = value; }
    public JsonObject getSubjectInfo() { return subjectInfo; }
    public void setSubjectInfo(JsonObject value) { subjectInfo = value; }
    public JsonObject getIdentificationInfo() { return identificationInfo; }
    public void setIdentificationInfo(JsonObject value) { identificationInfo = value; }
    public JsonObject getBusinessInfo() { return businessInfo; }
    public void setBusinessInfo(JsonObject value) { businessInfo = value; }
    public JsonObject getAdditionInfo() { return additionInfo; }
    public void setAdditionInfo(JsonObject value) { additionInfo = value; }
}
