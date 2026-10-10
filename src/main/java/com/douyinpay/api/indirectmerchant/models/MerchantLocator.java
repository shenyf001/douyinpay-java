package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

/** Identifies an indirect merchant by platform ID or by its external identifier pair. */
public class MerchantLocator {
    @SerializedName("indirect_merchant_id")
    private String indirectMerchantId;
    @SerializedName("out_indirect_merchant_id")
    private String outIndirectMerchantId;
    @SerializedName("out_payment_inst_id")
    private String outPaymentInstId;
    @SerializedName("out_channel_sp_id")
    private String outChannelSpId;

    public void validate() {
        boolean platform = hasText(indirectMerchantId);
        boolean external = hasText(outIndirectMerchantId) || hasText(outPaymentInstId);
        if (platform == external) {
            throw new IllegalArgumentException("specify exactly one merchant locator");
        }
        if (external && (!hasText(outIndirectMerchantId) || !hasText(outPaymentInstId))) {
            throw new IllegalArgumentException(
                    "out_indirect_merchant_id and out_payment_inst_id must be specified together");
        }
        if (platform && hasText(outChannelSpId)) {
            throw new IllegalArgumentException("out_channel_sp_id is only supported with external merchant locator");
        }
    }

    public static boolean hasText(String value) {
        return value != null && !value.isEmpty();
    }
    public String getIndirectMerchantId() { return indirectMerchantId; }
    public void setIndirectMerchantId(String value) { indirectMerchantId = value; }
    public String getOutIndirectMerchantId() { return outIndirectMerchantId; }
    public void setOutIndirectMerchantId(String value) { outIndirectMerchantId = value; }
    public String getOutPaymentInstId() { return outPaymentInstId; }
    public void setOutPaymentInstId(String value) { outPaymentInstId = value; }
    public String getOutChannelSpId() { return outChannelSpId; }
    public void setOutChannelSpId(String value) { outChannelSpId = value; }
}
