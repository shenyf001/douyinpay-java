package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

/** 查询商户响应。 */
public class QueryMerchantResponse {
    /** 字段含义：抖音支付间连商户号。 */
    @SerializedName("indirect_merchant_id") private String indirectMerchantId;
    /** 字段含义：外部间连商户号。 */
    @SerializedName("out_indirect_merchant_id") private String outIndirectMerchantId;
    /** 字段含义：外部从业机构商户号。 */
    @SerializedName("out_payment_inst_id") private String outPaymentInstId;
    /** 字段含义：外部渠道商户号。 */
    @SerializedName("out_channel_sp_id") private String outChannelSpId;
    /** 字段含义：业务申请编号。 */
    @SerializedName("business_code") private String businessCode;
    /** 字段含义：申请单号。 */
    @SerializedName("applyment_id") private String applymentId;
    /** 字段含义：主体资料。 */
    @SerializedName("subject_info") private QueryMerchantSubjectInfo subjectInfo;
    /** 字段含义：经营者/法人身份证件。 */
    @SerializedName("identification_info") private QueryMerchantIdentificationInfo identificationInfo;
    /** 字段含义：经营信息。 */
    @SerializedName("business_info") private QueryMerchantBusinessInfo businessInfo;

    public String getIndirectMerchantId() { return indirectMerchantId; }
    public void setIndirectMerchantId(String value) { indirectMerchantId = value; }
    public String getOutIndirectMerchantId() { return outIndirectMerchantId; }
    public void setOutIndirectMerchantId(String value) { outIndirectMerchantId = value; }
    public String getOutPaymentInstId() { return outPaymentInstId; }
    public void setOutPaymentInstId(String value) { outPaymentInstId = value; }
    public String getOutChannelSpId() { return outChannelSpId; }
    public void setOutChannelSpId(String value) { outChannelSpId = value; }
    public String getBusinessCode() { return businessCode; }
    public void setBusinessCode(String value) { businessCode = value; }
    public String getApplymentId() { return applymentId; }
    public void setApplymentId(String value) { applymentId = value; }
    public QueryMerchantSubjectInfo getSubjectInfo() { return subjectInfo; }
    public void setSubjectInfo(QueryMerchantSubjectInfo value) { subjectInfo = value; }
    public QueryMerchantIdentificationInfo getIdentificationInfo() { return identificationInfo; }
    public void setIdentificationInfo(QueryMerchantIdentificationInfo value) { identificationInfo = value; }
    public QueryMerchantBusinessInfo getBusinessInfo() { return businessInfo; }
    public void setBusinessInfo(QueryMerchantBusinessInfo value) { businessInfo = value; }
}
