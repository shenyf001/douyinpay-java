package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/** 入驻申请的经营信息。 */
public class CreateBusinessInfo {
    /**
     * 字段含义：商户简称。
     * 格式规则：字符串，长度为1-32个字符；前后不能有空格、制表符、换行符；不能仅含特殊字符；仅能填写数字、英文字母、汉字及特殊字符；仅支持utf-8格式。
     * 业务规则：【商户简称】在支付完成页向买家展示，需与抖音支付经营类目相关。1、请输入1-32个字符；2、前后不能有空格、制表符、换行符；3、不能仅含特殊字符；4、仅能填写数字、英文字母、汉字及特殊字符；5、仅支持utf-8格式。
     * 示例：个体户张三
     */
    @SerializedName("merchant_short_name") private String merchantShortName;
    /**
     * 字段含义：客服电话。
     * 格式规则：字符串；前后不能有空格、制表符、换行符；需满足以下任一条件：11位数字的手机号码；5-20位数字、连字符“-”、加号“+”。
     * 业务规则：【客服电话】将在交易记录中向买家展示，请确保电话畅通以便平台回拨确认。1、前后不能有空格、制表符、换行符；2、需满足以下任一条件：11位数字的手机号码；5-20位数字、连字符“-”、加号“+”。
     * 示例：18042002458
     */
    @SerializedName("service_phone") private String servicePhone;
    /**
     * 字段含义：所属行业。
     * 格式规则：字符串。
     * 业务规则：填写间连商户在清算机构侧登记的行业MCC编码。
     * 示例：7300
     */
    @SerializedName("mcc") private String mcc;
    /**
     * 字段含义：特殊资质图片。
     * 格式规则：数组，使用字符串列表表示图片，图片大小不超过10M，支持1-5张图片。
     * 业务规则：【特殊资质图片】图片大小不超过10M，支持1-5张图片。
     * 示例：
     * TODO: 官方文档未提供标准示例值，暂留空。
     */
    @SerializedName("special_industry_pics") private List<String> specialIndustryPics;

    public String getMerchantShortName() { return merchantShortName; }
    public void setMerchantShortName(String value) { merchantShortName = value; }
    public String getServicePhone() { return servicePhone; }
    public void setServicePhone(String value) { servicePhone = value; }
    public String getMcc() { return mcc; }
    public void setMcc(String value) { mcc = value; }
    public List<String> getSpecialIndustryPics() { return specialIndustryPics; }
    public void setSpecialIndustryPics(List<String> value) { specialIndustryPics = value; }
}
