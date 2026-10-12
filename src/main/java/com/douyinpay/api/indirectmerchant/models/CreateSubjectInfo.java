package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

/** 入驻申请的主体资料，不用于查询响应。 */
public class CreateSubjectInfo {
    /**
     * 字段含义：主体类型。
     * 格式规则：字符串。
     * 业务规则：【主体类型】主体类型需与营业执照/登记证书上一致。枚举：SUBJECT_TYPE_INDIVIDUAL：营业执照上的主体类型一般为个体户、个体工商户、个体经营；SUBJECT_TYPE_ENTERPRISE：营业执照上的主体类型一般为有限公司、有限责任公司；SUBJECT_TYPE_GOVERNMENT：包括各级、各类政府机关，如机关党委、税务、民政、人社、工商、商务、市监等；SUBJECT_TYPE_INSTITUTIONS：包括国内各类事业单位，如：医疗、教育、学校等单位；SUBJECT_TYPE_OTHERS：包括社会团体、民办非企业、基金会、基层群众性自治组织、农村集体经济组织等组织；SUBJECT_TYPE_MICRO：小微商户。
     * 示例：SUBJECT_TYPE_ENTERPRISE
     */
    @SerializedName("subject_type") private String subjectType;
    /**
     * 字段含义：主体证件信息。
     * 格式规则：对象。
     * 业务规则：【主体证件信息】主体为企业/个体户/政府机关/事业单位/其他组织时，请填写主体证件信息；小微商户不需要填写主体证件信息，只需要提交“经营者/法人身份证件”信息。
     * 示例：{"register_address":"湖南省长沙市xx区xx路xx号","period_begin":"2019-08-01","period_end":"2019-08-02"}
     */
    @SerializedName("certificate_info") private CreateCertificateInfo certificateInfo;

    public String getSubjectType() { return subjectType; }
    public void setSubjectType(String value) { subjectType = value; }
    public CreateCertificateInfo getCertificateInfo() { return certificateInfo; }
    public void setCertificateInfo(CreateCertificateInfo value) { certificateInfo = value; }
}
