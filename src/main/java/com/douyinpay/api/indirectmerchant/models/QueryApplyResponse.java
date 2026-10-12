package com.douyinpay.api.indirectmerchant.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class QueryApplyResponse {
    /** 字段含义：业务申请编号，驳回后可用相同编号覆盖修改原申请单。 */
    @SerializedName("business_code") private String businessCode;
    /** 字段含义：抖音支付分配的申请单号。 */
    @SerializedName("applyment_id") private String applymentId;
    /** 字段含义：抖音支付间连商户号，申请完成时返回。 */
    @SerializedName("indirect_merchant_id") private String indirectMerchantId;
    /** 字段含义：申请单状态，APPLYMENT_STATE_WAITTING_FOR_AUDIT 审核中、APPLYMENT_STATE_REJECTED 已驳回、APPLYMENT_STATE_PASSED 已完成、APPLYMENT_STATE_CANCELED 已作废；保留未知状态。 */
    @SerializedName("applyment_state") private String applymentState;
    /** 字段含义：各项资料审核驳回原因，申请驳回时返回。 */
    @SerializedName("audit_detail") private List<AuditDetail> auditDetail;
    public String getBusinessCode() { return businessCode; }
    public void setBusinessCode(String value) { businessCode = value; }
    public String getApplymentId() { return applymentId; }
    public void setApplymentId(String value) { applymentId = value; }
    public String getIndirectMerchantId() { return indirectMerchantId; }
    public void setIndirectMerchantId(String value) { indirectMerchantId = value; }
    public String getApplymentState() { return applymentState; }
    public void setApplymentState(String value) { applymentState = value; }
    public List<AuditDetail> getAuditDetail() { return auditDetail; }
    public void setAuditDetail(List<AuditDetail> value) { auditDetail = value; }
}
