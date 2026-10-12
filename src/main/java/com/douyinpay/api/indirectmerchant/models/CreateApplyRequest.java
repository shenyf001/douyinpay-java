package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

public class CreateApplyRequest {
    /**
     * 字段含义：业务申请编号。
     * 格式规则：字符串，长度为1-64字符，只能由数字、小写字母或下划线组成。
     * 业务规则：【业务申请编号】1、只能由数字、小写字母或下划线组成，长度为1-64字符；2、自定义的唯一编号；3、每个编号对应一个申请单，每个申请单审核通过后会生成一个抖音支付商户号。
     * 示例：602323_10000
     */
    @SerializedName("business_code") private String businessCode;
    /**
     * 字段含义：平台证书序列号。
     * 格式规则：字符串，通过 Douyinpay-Serial 请求头传递，不参与请求体序列化。
     * 业务规则：用于敏感字段加解密，详见 https://partner.douyinpay.com/wiki/682561fbd3e93f04ff85a04f/682563b39faab1051f8f5abc；敏感字段传入明文，由 SDK 加密。
     * 示例：
     * TODO: 官方文档未提供标准示例值，暂留空。
     */
    private transient String douyinpaySerial;
    /**
     * 字段含义：间连商户外部商户号。
     * 格式规则：字符串。
     * 业务规则：从业机构在清算机构侧登记的间连商户唯一标识。
     * 示例：10434920032
     */
    @SerializedName("out_indirect_merchant_id") private String outIndirectMerchantId;
    /**
     * 字段含义：从业机构外部商户号。
     * 格式规则：字符串。
     * 业务规则：从业机构在清算机构侧登记的商户号。
     * 示例：41110000
     */
    @SerializedName("out_payment_inst_id") private String outPaymentInstId;
    /**
     * 字段含义：渠道商外部商户号。
     * 格式规则：字符串。
     * 业务规则：渠道商在清算机构侧登记的商户号。
     * 示例：C023828488
     */
    @SerializedName("out_channel_sp_id") private String outChannelSpId;
    /**
     * 字段含义：子从业机构外部商户号。
     * 格式规则：字符串。
     * 业务规则：【子从业机构外部商户号】支持银联下同一个从业机构商户按照地区拆分子从业机构的场景，不同地区是不同的子从业机构；若传入该字段，系统会校验子从业机构号与从业机构号（out_payment_inst_id）前4位是否一致；会按照子从业机构号进行间连商户的创建，后续交易发起需要使用该子从业机构。
     * 示例：41110100
     */
    @SerializedName("out_payment_inst_sub_id") private String outPaymentInstSubId;
    /**
     * 字段含义：抖音支付间连商户号。
     * 格式规则：字符串。
     * 业务规则：【抖音支付间连商户号】如传入该商户在抖音支付已有的商户号（另一个清算机构下进件的商户），则会校验与已有商户号的一致性，包括：本申请单的商户外部商户号需与该抖音支付商户号的外部商户号一致；本申请单的商户主体类型和主体名称需与该抖音支付商户号的信息一致。校验通过后，本商户入驻申请返回的抖音支付间连商户号就是该传入的间连商户号；如未写入，则按新入驻申请创建执行。
     * 示例：2026045587123820
     */
    @SerializedName("indirect_merchant_id") private String indirectMerchantId;
    /**
     * 字段含义：主体资料。
     * 格式规则：对象。
     * 业务规则：【主体资料】请填写商家的营业执照/登记证书、经营者/法人的证件等信息。
     * 示例：{"subject_type":"SUBJECT_TYPE_ENTERPRISE","certificate_info":{"register_address":"湖南省长沙市xx区xx路xx号","period_begin":"2019-08-01","period_end":"2019-08-02"}}
     */
    @SerializedName("subject_info") private CreateSubjectInfo subjectInfo;
    /**
     * 字段含义：经营者/法人身份证件。
     * 格式规则：对象。
     * 业务规则：【经营者/法人身份信息】1、个体户：请上传经营者的身份证件。2、企业/社会组织：请上传法人的身份证件。3、政府机关/事业单位：请上传法人/经办人的身份证件。4、小微商户：请上传经营者的身份证件。
     * 示例：{"identification_type":"IDENTIFICATION_TYPE_IDCARD","period_begin":"2019-08-01","period_end":"2019-08-02"}
     */
    @SerializedName("identification_info") private CreateIdentificationInfo identificationInfo;
    /**
     * 字段含义：经营信息。
     * 格式规则：对象。
     * 业务规则：【经营资料】请填写商家的经营业务信息、售卖商品/提供服务场景信息。
     * 示例：{"merchant_short_name":"个体户张三","service_phone":"18042002458","mcc":"7300"}
     */
    @SerializedName("business_info") private CreateBusinessInfo businessInfo;
    /**
     * 字段含义：补充信息。
     * 格式规则：对象。
     * 业务规则：根据实际审核情况，额外要求商家提供指定的补充资料。
     * 示例：
     * TODO: 官方文档未提供标准示例值，暂留空。
     */
    @SerializedName("addition_info") private CreateAdditionInfo additionInfo;
    public void validate() {
        if (businessCode == null || !businessCode.matches("[a-z0-9_]{1,64}")) {
            throw new IllegalArgumentException("business_code must be 1-64 lowercase letters, digits, or underscores");
        }
        if (!MerchantLocator.hasText(outIndirectMerchantId) || !MerchantLocator.hasText(outPaymentInstId)) {
            throw new IllegalArgumentException("out_indirect_merchant_id and out_payment_inst_id are required");
        }
    }
    public String getBusinessCode() { return businessCode; }
    public void setBusinessCode(String value) { businessCode = value; }
    public String getDouyinpaySerial() { return douyinpaySerial; }
    public void setDouyinpaySerial(String value) { douyinpaySerial = value; }
    public String getOutIndirectMerchantId() { return outIndirectMerchantId; }
    public void setOutIndirectMerchantId(String value) { outIndirectMerchantId = value; }
    public String getOutPaymentInstId() { return outPaymentInstId; }
    public void setOutPaymentInstId(String value) { outPaymentInstId = value; }
    public String getOutChannelSpId() { return outChannelSpId; }
    public void setOutChannelSpId(String value) { outChannelSpId = value; }
    public String getOutPaymentInstSubId() { return outPaymentInstSubId; }
    public void setOutPaymentInstSubId(String value) { outPaymentInstSubId = value; }
    public String getIndirectMerchantId() { return indirectMerchantId; }
    public void setIndirectMerchantId(String value) { indirectMerchantId = value; }
    public CreateSubjectInfo getSubjectInfo() { return subjectInfo; }
    public void setSubjectInfo(CreateSubjectInfo value) { subjectInfo = value; }
    public CreateIdentificationInfo getIdentificationInfo() { return identificationInfo; }
    public void setIdentificationInfo(CreateIdentificationInfo value) { identificationInfo = value; }
    public CreateBusinessInfo getBusinessInfo() { return businessInfo; }
    public void setBusinessInfo(CreateBusinessInfo value) { businessInfo = value; }
    public CreateAdditionInfo getAdditionInfo() { return additionInfo; }
    public void setAdditionInfo(CreateAdditionInfo value) { additionInfo = value; }
}
