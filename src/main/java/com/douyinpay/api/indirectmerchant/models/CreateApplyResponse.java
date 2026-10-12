package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

public class CreateApplyResponse {
    /**
     * 字段含义：抖音支付分配的申请单号。
     */
    @SerializedName("applyment_id") private String applymentId;
    public String getApplymentId() { return applymentId; }
    public void setApplymentId(String value) { applymentId = value; }
}
