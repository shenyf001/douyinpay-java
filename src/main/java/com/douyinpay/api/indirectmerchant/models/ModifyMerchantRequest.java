package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

public class ModifyMerchantRequest extends MerchantLocator {
    @SerializedName("merchant_short_name") private String merchantShortName;
    @SerializedName("service_phone") private String servicePhone;
    public String getMerchantShortName() { return merchantShortName; }
    public void setMerchantShortName(String value) { merchantShortName = value; }
    public String getServicePhone() { return servicePhone; }
    public void setServicePhone(String value) { servicePhone = value; }
}
