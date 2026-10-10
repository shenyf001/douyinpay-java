package com.douyinpay.api.indirectmerchant.models;

import java.util.Arrays;

public class UploadRequest {
    private final String fileName;
    private final String contentType;
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
