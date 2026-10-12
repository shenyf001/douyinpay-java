package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;

public class UploadResponse {
    /** 字段含义：媒体文件标识Id。 */
    @SerializedName("media_id") private String mediaId;
    public String getMediaId() { return mediaId; }
    public void setMediaId(String value) { mediaId = value; }
}
