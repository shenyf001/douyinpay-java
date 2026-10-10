package com.douyinpay.api;

public interface DouyinpayClient {

    public final String ClientVersion = "JAVA-v1.0.11";

    /**
     * 发送HTTP请求
     *
     * @param request       HTTP请求
     * @param responseClass 业务返回类的Class对象，如果不确定业务返回类型，或该请求没有返回体，可以传入Object.class
     * @param <T>           由Class对象建模的类的类型
     * @return HTTP返回
     */
    <T> DouyinpayResponse<T> execute(DouyinpayRequest request, Class<T> responseClass);

    /**
     * Sends a multipart request. Implementations that predate multipart support remain compatible.
     *
     * @param request multipart request
     * @param responseClass business response class
     * @param <T> business response type
     * @return HTTP response
     */
    default <T> DouyinpayResponse<T> executeMultipart(
            DouyinpayMultipartRequest request, Class<T> responseClass) {
        throw new UnsupportedOperationException("multipart requests are not supported by this client");
    }
}
