package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/** 查询商户响应的经营信息。 */
public class QueryMerchantBusinessInfo {
    /** 字段含义：商户简称。 */
    @SerializedName("merchant_short_name") private String merchantShortName;
    /** 字段含义：客服电话。 */
    @SerializedName("service_phone") private String servicePhone;
    /** 字段含义：所属行业。 */
    @SerializedName("mcc") private String mcc;
    /** 字段含义：特殊资质图片。 */
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
