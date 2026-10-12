package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

/** 单项资料的审核驳回原因。 */
public class AuditDetail {
    /** 字段含义：提交申请单的资料项字段名。 */
    @SerializedName("field") private String field;
    /** 字段含义：提交资料项被驳回的原因。 */
    @SerializedName("reject_reason") private String rejectReason;
    public String getField() { return field; }
    public void setField(String value) { field = value; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String value) { rejectReason = value; }
}
