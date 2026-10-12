## 回调通知验签和解密

可以使用 [notification](/src/main/java/com/douyinpay/api/notification) 中的 `NotificationParser` 解析回调通知。具体步骤如下：

1. 获取HTTP请求头中的 `douyinpay-signature` 、 `douyinpay-nonce` 、 `douyinpay-timestamp` 、 `douyinpay-serial` 对应的值，构建 `RequestParam` 。
2. 获取 HTTP 请求体的 `JSON` 纯文本。
3. 根据解密后的通知数据数据结构，构造解密对象类 `DecryptObject` 。支付结果通知和代扣结果通知解密对象类为 [`ApiTransaction`](src/main/java/com/douyinpay/api/payments/common/ApiTransaction.java)，退款结果通知解密对象类为 [ApiRefundNotification](src/main/java/com/douyinpay/api/refund/model/ApiRefundNotification.java)。
4. 使用抖音支付平台证书（验签）和商户API密钥（解密）初始化 `NotificationConfig` 和 `NotificationParser` 。
5. 使用请求参数 `requestParam` 和 `DecryptObject.class` ，调用 `parser.parse` 验签并解密报文。

```java
// 构造 RequestParam
//签名、随机数、时间戳从header中获取
RequestParam requestParam =  new RequestParam.Builder().serialNumber("623DFCD5354E06BC")
        .nonce(verifyNonce)
        .signature(signature);
        .signType(verifyAlgorithm)
        .timestamp(timestamp)
        .body(notifyStr)
         .build();

// 初始化 NotificationConfig
NotificationConfig notificationConfig = new NotificationConfig.Builder()
        .signType(Constants.SIGN_TYPE_RSA)
        .cipherType(Constants.ENCRYPT_TYPE_AES)
        .platformPublicKey("")
        .platformCertPath(platformPath)
        .encryptKey(aesKey).build();

// 初始化 NotificationParser
NotificationParser parser=new NotificationParser(rsaNotificationConfig);

// 验签并解密报文
DecryptObject decryptObject = parser.parse(requestParam,DecryptObject.class);
```

## 间连商户

`ApiIndirectMerchantService` 提供间连商户申请、查询、撤销、商户维护、AppID 绑定和图片上传能力，使用完整的强类型请求与响应模型。申请中的证件持有人姓名、证件号码传入明文，由 SDK 使用平台证书 RSA PKCS#1 v1.5 加密；查询商户时先验签，再使用商户私钥解密这两个字段。

```java
ApiIndirectMerchantService service = new ApiIndirectMerchantService.Builder()
        .douyinpayClient(douyinpayClient)
        .domainName(DomainName.API)
        .platformCertificate(platformCertificate)
        .merchantPrivateKey(merchantPrivateKey)
        .build();

QueryApplyRequest query = new QueryApplyRequest();
query.setBusinessCode("merchant_apply_1");
QueryApplyResponse applyment = service.queryApply(query);

UploadResponse media = service.upload(new UploadRequest(
        "license.jpg", "image/jpeg", imageBytes));

ApplymentStatusNotification notification =
        service.parseApplymentStatusNotification(parser, requestParam);
```

图片上传仅支持 JPG、BMP 和 PNG，文件不得为空且最大为 5 MB。申请单状态通知会先验签再解密；调用方应使用通知标识或申请单标识自行去重，并且不得记录解密后的敏感内容。

使用 `DefaultDouyinpayClient` 时默认沿用客户端的平台证书和商户私钥；自定义客户端可通过 Builder 显式配置。显式设置的 `DouyinpaySerial` 必须与加密证书序列号一致。SDK 在请求副本中加密，不修改调用方资料。自定义客户端应完成响应验签后返回业务对象。图片大小按官方参数表的 5 MB 校验，官方错误码表仍有 2 MB 旧描述，实际服务端限制需以返回结果为准。
