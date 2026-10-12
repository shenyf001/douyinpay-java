package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

/** 查询商户响应的经营者/法人身份证件信息。 */
public class QueryMerchantIdentificationInfo {
    /** 字段含义：证件类型。 */
    @SerializedName("identification_type") private String identificationType;
    /** 字段含义：证件持有人姓名。 */
    @SerializedName("identification_name") private String identificationName;
    /** 字段含义：证件持有人证件号码。 */
    @SerializedName("identification_number") private String identificationNumber;
    /** 字段含义：证件持有人有效期开始时间。 */
    @SerializedName("period_begin") private String periodBegin;
    /** 字段含义：证件持有人有效期结束时间。 */
    @SerializedName("period_end") private String periodEnd;
    /** 字段含义：证件正面照片。 */
    @SerializedName("identification_front_copy") private String identificationFrontCopy;
    /** 字段含义：证件背面照片。 */
    @SerializedName("identification_back_copy") private String identificationBackCopy;

    public String getIdentificationType() { return identificationType; }
    public void setIdentificationType(String value) { identificationType = value; }
    public String getIdentificationName() { return identificationName; }
    public void setIdentificationName(String value) { identificationName = value; }
    public String getIdentificationNumber() { return identificationNumber; }
    public void setIdentificationNumber(String value) { identificationNumber = value; }
    public String getPeriodBegin() { return periodBegin; }
    public void setPeriodBegin(String value) { periodBegin = value; }
    public String getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(String value) { periodEnd = value; }
    public String getIdentificationFrontCopy() { return identificationFrontCopy; }
    public void setIdentificationFrontCopy(String value) { identificationFrontCopy = value; }
    public String getIdentificationBackCopy() { return identificationBackCopy; }
    public void setIdentificationBackCopy(String value) { identificationBackCopy = value; }
}
