package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

/** 查询商户响应的主体证件信息。 */
public class QueryMerchantCertificateInfo {
    /** 字段含义：证件编号。 */
    @SerializedName("cert_no") private String certNo;
    /** 字段含义：商户名称。 */
    @SerializedName("merchant_name") private String merchantName;
    /** 字段含义：证件图片。 */
    @SerializedName("cert_copy") private String certCopy;
    /** 字段含义：注册地址。 */
    @SerializedName("register_address") private String registerAddress;
    /** 字段含义：经营者/法人姓名。 */
    @SerializedName("legal_person_name") private String legalPersonName;
    /** 字段含义：有效期限开始日期。 */
    @SerializedName("period_begin") private String periodBegin;
    /** 字段含义：有效期限结束日期。 */
    @SerializedName("period_end") private String periodEnd;

    public String getCertNo() { return certNo; }
    public void setCertNo(String value) { certNo = value; }
    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String value) { merchantName = value; }
    public String getCertCopy() { return certCopy; }
    public void setCertCopy(String value) { certCopy = value; }
    public String getRegisterAddress() { return registerAddress; }
    public void setRegisterAddress(String value) { registerAddress = value; }
    public String getLegalPersonName() { return legalPersonName; }
    public void setLegalPersonName(String value) { legalPersonName = value; }
    public String getPeriodBegin() { return periodBegin; }
    public void setPeriodBegin(String value) { periodBegin = value; }
    public String getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(String value) { periodEnd = value; }
}
