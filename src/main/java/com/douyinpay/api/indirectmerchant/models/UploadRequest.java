package com.douyinpay.api.indirectmerchant.models;

import java.util.Arrays;

public class UploadRequest {
    /**
     * 字段含义：图片文件名称。
     * <p>格式规则：字符串，1-128字符，以jpg、jpeg、bmp或png为后缀。</p>
     * <p>业务规则：商户自定义名称，由SDK写入meta.filename。</p>
     * <p>示例：filea.jpg</p>
     */
    private final String fileName;
    /**
     * 字段含义：图片分段MIME类型。
     * <p>格式规则：字符串，未设置时使用application/octet-stream。</p>
     * <p>业务规则：用于file分段的Content-Type。</p>
     * <p>示例：image/jpg</p>
     */
    private final String contentType;
    /**
     * 字段含义：图片文件二进制内容。
     * <p>格式规则：非空字节数组，不超过5MB。</p>
     * <p>业务规则：只支持JPG、BMP、PNG；SDK计算SHA-256写入meta，使用同一份meta JSON签名。</p>
     * <p>示例：</p>
     * TODO: 官方文档未提供标准示例值，暂留空。
     */
    private final byte[] fileContent;
    public UploadRequest(String fileName, String contentType, byte[] fileContent) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileContent = fileContent == null ? null : Arrays.copyOf(fileContent, fileContent.length);
    }
    public String getFileName() { return fileName; }
    public String getContentType() { return contentType; }
    public byte[] getFileContent() {
        return fileContent == null ? null : Arrays.copyOf(fileContent, fileContent.length);
    }
}
