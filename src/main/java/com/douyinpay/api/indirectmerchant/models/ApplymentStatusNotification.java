package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

public class ApplymentStatusNotification {
    /** 字段含义：由抖音支付生成并下发的从业机构户号。 */
    @SerializedName("sp_mchid") private String spMchid;
    /** 字段含义：抖音支付生成的申请单号。 */
    @SerializedName("applyment_id") private String applymentId;
    /** 字段含义：商户自提交的申请单号，站点页面提交时为空。 */
    @SerializedName("business_code") private String businessCode;
    /** 字段含义：抖音支付间连商户号，申请完成时返回。 */
    @SerializedName("indirect_merchant_id") private String indirectMerchantId;
    /** 字段含义：申请单状态，APPLYMENT_STATE_REJECTED 已驳回、APPLYMENT_STATE_PASSED 已完成、APPLYMENT_STATE_CANCELED 已作废；保留未知状态原值。 */
    @SerializedName("applyment_state") private String applymentState;
    /** 字段含义：失败原因详情，驳回时返回原因，作废时返回申请单已撤销。 */
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
