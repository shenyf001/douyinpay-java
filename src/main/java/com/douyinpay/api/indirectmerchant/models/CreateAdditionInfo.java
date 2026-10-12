package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/** 入驻申请的补充信息。 */
public class CreateAdditionInfo {
    /**
     * 字段含义：补充材料。
     * 格式规则：数组，使用字符串列表表示图片，图片大小不超过10M，最多不超过5张图片。
     * 业务规则：【补充材料】补充图片内容，图片大小不超过10M，最多不超过5张图片。
     * 示例：
     * TODO: 官方文档未提供标准示例值，暂留空。
     */
    @SerializedName("addition_pics") private List<String> additionPics;
    /**
     * 字段含义：补充说明。
     * 格式规则：字符串，文案不超过100字。
     * 业务规则：【补充说明】补充说明，文案不超过100字。
     * 示例：
     * TODO: 官方文档未提供标准示例值，暂留空。
     */
    @SerializedName("addition_remark") private String additionRemark;

    public List<String> getAdditionPics() { return additionPics; }
    public void setAdditionPics(List<String> value) { additionPics = value; }
    public String getAdditionRemark() { return additionRemark; }
    public void setAdditionRemark(String value) { additionRemark = value; }
}
