package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

/** 入驻申请的营业执照/登记证书信息。 */
public class CreateCertificateInfo {
    /**
     * 字段含义：证件编号。
     * 格式规则：字符串。
     * 业务规则：【证件编号】1、请填写营业执照/登记证书上的统一社会信用代码。
     * 示例：
     * TODO: 官方文档未提供标准示例值，暂留空。
     */
    @SerializedName("cert_no") private String certNo;
    /**
     * 字段含义：商户名称。
     * 格式规则：字符串，长度为2-128个字符；不能仅含特殊字符和数字；仅支持utf-8格式。
     * 业务规则：【商户名称】对应其证件的主体名称。1、长度为2-128个字符；2、不能仅含特殊字符和数字；3、仅支持utf-8格式；4、小微商户使用法人名称。
     * 示例：
     * TODO: 官方文档未提供标准示例值，暂留空。
     */
    @SerializedName("merchant_name") private String merchantName;
    /**
     * 字段含义：证件图片。
     * 格式规则：字符串，填写通过图片上传接口预先上传图片生成好的MediaID，可上传1张图片。
     * 业务规则：【证件图片】1、照片应正面拍摄、清晰、四角完整、无反光或遮挡；不得翻拍、截图、镜像、PS；2、上传彩色照片、彩色扫描件，复印件需加盖公章鲜章；3、水印仅限于抖音支付业务相关；4、可上传1张图片，请填写通过图片上传接口预先上传图片生成好的MediaID。
     * 示例：
     * TODO: 官方文档未提供标准示例值，暂留空。
     */
    @SerializedName("cert_copy") private String certCopy;
    /**
     * 字段含义：注册地址。
     * 格式规则：字符串，长度为4-128个字符；前后不能有空格、制表符、换行符；不能仅含数字、特殊字符；仅能填写数字、英文字母、汉字及特殊字符；仅支持utf-8格式。
     * 业务规则：【注册地址】营业执照/登记证书的注册地址。1、长度为4-128个字符；2、前后不能有空格、制表符、换行符；3、不能仅含数字、特殊字符；4、仅能填写数字、英文字母、汉字及特殊字符；5、仅支持utf-8格式。
     * 示例：湖南省长沙市xx区xx路xx号
     */
    @SerializedName("register_address") private String registerAddress;
    /**
     * 字段含义：经营者/法人姓名。
     * 格式规则：字符串，长度为2-100个字符；前后不能有空格、制表符、换行符；不能仅含特殊字符；仅能填写数字、英文字母、汉字及特殊字符。
     * 业务规则：【经营者/法人姓名】请填写营业执照的经营者/法定代表人姓名。1、长度为2-100个字符；2、前后不能有空格、制表符、换行符；3、不能仅含特殊字符；4、仅能填写数字、英文字母、汉字及特殊字符。
     * 示例：
     * TODO: 官方文档未提供标准示例值，暂留空。
     */
    @SerializedName("legal_person_name") private String legalPersonName;
    /**
     * 字段含义：有效期限开始日期。
     * 格式规则：字符串，日期格式应满足合法的YYYY-MM-DD格式。
     * 业务规则：【有效期限开始日期】请填写营业执照/登记证书的有效期限开始时间。1、日期格式应满足合法的YYYY-MM-DD格式；3、开始时间不能大于等于当前日期。
     * 示例：2019-08-01
     */
    @SerializedName("period_begin") private String periodBegin;
    /**
     * 字段含义：有效期限结束日期。
     * 格式规则：字符串，日期格式应满足合法的YYYY-MM-DD格式或长期。
     * 业务规则：【有效期限结束日期】请填写营业执照/登记证书的有效期限结束时间。1、日期格式应满足合法的YYYY-MM-DD格式或长期；2、结束时间需大于开始时间；3、若证件有效期为长期，请填写：长期。
     * 示例：2019-08-02
     */
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
