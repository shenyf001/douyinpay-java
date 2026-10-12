package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

/** 使用抖音支付间连商户号或外部商户标识组合定位间连商户。 */
public class MerchantLocator {
    /**
     * 字段含义：抖音支付间连商户号。
     * 格式规则：字符串。
     * 业务规则：抖音支付分配的间连商户号，与外部商户标识组合选择一种定位方式；使用该商户号时不传外部商户标识及渠道商外部商户号。
     * 示例：2026045587123820
     */
    @SerializedName("indirect_merchant_id")
    private String indirectMerchantId;
    /**
     * 字段含义：间连商户外部商户号。
     * 格式规则：字符串。
     * 业务规则：商户在清算机构侧登记的唯一标识，与从业机构外部商户号配合使用；外部商户标识组合与抖音支付间连商户号选择一种定位方式。
     * 示例：20434920032
     */
    @SerializedName("out_indirect_merchant_id")
    private String outIndirectMerchantId;
    /**
     * 字段含义：从业机构外部商户号。
     * 格式规则：字符串。
     * 业务规则：从业机构在清算机构侧登记的商户号，与间连商户外部商户号配合使用。
     * 示例：4043424032
     */
    @SerializedName("out_payment_inst_id")
    private String outPaymentInstId;
    /**
     * 字段含义：渠道商外部商户号。
     * 格式规则：字符串。
     * 业务规则：渠道商在清算机构侧登记的商户号，仅用于外部商户标识组合定位方式。
     * 示例：
     * TODO: 官方文档未提供标准示例值，暂留空。
     */
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
