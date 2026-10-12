package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

/** 查询商户响应的主体资料。 */
public class QueryMerchantSubjectInfo {
    /** 字段含义：主体类型。 */
    @SerializedName("subject_type") private String subjectType;
    /** 字段含义：主体证件信息。 */
    @SerializedName("certificate_info") private QueryMerchantCertificateInfo certificateInfo;

    public String getSubjectType() { return subjectType; }
    public void setSubjectType(String value) { subjectType = value; }
    public QueryMerchantCertificateInfo getCertificateInfo() { return certificateInfo; }
    public void setCertificateInfo(QueryMerchantCertificateInfo value) { certificateInfo = value; }
}
