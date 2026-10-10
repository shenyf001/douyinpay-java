package com.douyinpay.api;

import com.douyinpay.component.http.HttpMethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Multipart HTTP request whose signing body is separate from its transmitted body.
 */
public final class DouyinpayMultipartRequest {
    private final HttpMethod httpMethod;
    private final String url;
    private final String path;
    private final Map<String, String> headers;
    private final String signBody;
    private final String fileField;
    private final String fileName;
    private final String fileContentType;
    private final byte[] fileContent;

    public DouyinpayMultipartRequest(HttpMethod httpMethod, String url, String path, Map<String, String> headers,
                                    String signBody, String fileField, String fileName, String fileContentType,
                                    byte[] fileContent) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.path = path;
        this.headers = headers == null ? Collections.<String, String>emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<String, String>(headers));
        this.signBody = signBody;
        this.fileField = fileField;
        this.fileName = fileName;
        this.fileContentType = fileContentType;
        this.fileContent = fileContent == null ? null : Arrays.copyOf(fileContent, fileContent.length);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url + path;
    }

    public String getBaseUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getSignBody() {
        return signBody;
    }

    public String getFileField() {
        return fileField;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public byte[] getFileContent() {
        return fileContent == null ? null : Arrays.copyOf(fileContent, fileContent.length);
    }
}
