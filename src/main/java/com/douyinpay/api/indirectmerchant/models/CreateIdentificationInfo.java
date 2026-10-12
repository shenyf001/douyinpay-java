package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

/** 入驻申请的经营者/法人身份证件信息。 */
public class CreateIdentificationInfo {
    /**
     * 字段含义：证件类型。
     * 格式规则：字符串。
     * 业务规则：【证件类型】1、个体户/企业/事业单位/社会组织：可选择任一证件类型，政府机关、小微商户仅支持中国大陆居民-身份证类型。枚举：IDENTIFICATION_TYPE_IDCARD：中国大陆居民-身份证；IDENTIFICATION_TYPE_OVERSEA_PASSPORT：其他国家或地区居民-护照；IDENTIFICATION_TYPE_HONGKONG_PASSPORT：中国香港居民-来往内地通行证；IDENTIFICATION_TYPE_MACAO_PASSPORT：中国澳门居民-来往内地通行证；IDENTIFICATION_TYPE_TAIWAN_PASSPORT：中国台湾居民-来往大陆通行证；IDENTIFICATION_TYPE_FOREIGN_RESIDENT：外国人居留证；IDENTIFICATION_TYPE_HONGKONG_MACAO_RESIDENT：港澳居民证；IDENTIFICATION_TYPE_TAIWAN_RESIDENT：台湾居民证。
     * 示例：IDENTIFICATION_TYPE_IDCARD
     */
    @SerializedName("identification_type") private String identificationType;
    /**
     * 字段含义：证件持有人姓名。
     * 格式规则：字符串，明文长度为2-100个字符；前后不能有空格、制表符、换行符；不能仅含数字、特殊字符；仅能填写数字、英文字母、汉字及特殊字符。
     * 业务规则：【证件持有人姓名】1、请填写经营者/法定代表人对应证件的姓名；2、长度为2-100个字符；3、前后不能有空格、制表符、换行符；4、不能仅含数字、特殊字符；5、仅能填写数字、英文字母、汉字及特殊字符；6、该字段需进行加密处理，详见 https://partner.douyinpay.com/wiki/682561fbd3e93f04ff85a04f/682563b39faab1051f8f5abc。调用方传入明文，由 SDK 加密。
     * 示例：
     * TODO: 官方文档未提供标准示例值，暂留空。
     */
    @SerializedName("identification_name") private String identificationName;
    /**
     * 字段含义：证件持有人证件号码。
     * 格式规则：字符串，调用方传入明文。
     * 业务规则：【证件持有人证件号码】1、请填写经营者/法定代表人对应证件的号码；2、该字段需进行加密处理，详见 https://partner.douyinpay.com/wiki/682561fbd3e93f04ff85a04f/682563b39faab1051f8f5abc。由 SDK 加密。
     * 示例：
     * TODO: 官方文档未提供标准示例值，暂留空。
     */
    @SerializedName("identification_number") private String identificationNumber;
    /**
     * 字段含义：证件持有人有效期开始时间。
     * 格式规则：字符串，日期格式应满足合法的YYYY-MM-DD格式。
     * 业务规则：【证件持有人有效期开始时间】1、日期格式应满足合法的YYYY-MM-DD格式；2、开始时间不能小于1900-01-01；3、开始时间不能大于等于当前日期。
     * 示例：2019-08-01
     */
    @SerializedName("period_begin") private String periodBegin;
    /**
     * 字段含义：证件持有人有效期结束时间。
     * 格式规则：字符串，日期格式应满足合法的YYYY-MM-DD格式或长期。
     * 业务规则：【证件持有人有效期结束时间】1、日期格式应满足合法的YYYY-MM-DD格式或长期；2、结束时间需大于开始时间；3、若证件有效期为长期，请填写：长期。
     * 示例：2019-08-02
     */
    @SerializedName("period_end") private String periodEnd;
    /**
     * 字段含义：证件正面照片。
     * 格式规则：字符串，填写通过图片上传接口预先上传图片生成好的MediaID，可上传1张图片。
     * 业务规则：【证件正面照片】1、证件类型不为“身份证”时，上传证件正面照片；若法人证件类型为身份证，请上传人像面照片；2、正面拍摄、清晰、四角完整、无反光或遮挡；不得翻拍、截图、镜像、PS；3、请上传彩色照片or彩色扫描件，复印件需加盖公章鲜章，可添加“抖音支付”相关水印（如抖音支付认证）；4、可上传1张图片，请填写通过图片上传接口预先上传图片生成好的MediaID。
     * 示例：
     * TODO: 官方文档未提供标准示例值，暂留空。
     */
    @SerializedName("identification_front_copy") private String identificationFrontCopy;
    /**
     * 字段含义：证件背面照片。
     * 格式规则：字符串，填写通过图片上传接口预先上传图片生成好的MediaID，可上传1张图片。
     * 业务规则：【证件背面照片】1、若法人证件类型为身份证，请上传国徽面照片；2、正面拍摄、清晰、四角完整、无反光或遮挡；不得翻拍、截图、镜像、PS；3、请上传彩色照片or彩色扫描件，复印件需加盖公章鲜章，可添加“抖音支付”相关水印（如抖音支付认证）；4、可上传1张图片，请填写通过图片上传接口预先上传图片生成好的MediaID。
     * 示例：
     * TODO: 官方文档未提供标准示例值，暂留空。
     */
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
