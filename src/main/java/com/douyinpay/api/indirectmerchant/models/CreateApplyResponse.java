package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

public class CreateApplyResponse {
    @SerializedName("applyment_id") private String applymentId;
    public String getApplymentId() { return applymentId; }
    public void setApplymentId(String value) { applymentId = value; }
}
