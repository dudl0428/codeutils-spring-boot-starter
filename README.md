# CodeUtils Spring Boot Starter

一个功能全面的Java工具集合，支持通过Maven轻松引入，提供文件存储、短信发送、支付接口等常用功能的集成。

## 特性

- 文件存储：支持阿里云OSS、腾讯云COS、MinIO等多种存储方式
- 短信服务：集成阿里云短信、腾讯云短信等多种短信服务商
- 支付服务：支持微信支付、支付宝支付、银联支付等多种支付方式
- 通用工具：提供各种常用工具类，如日期处理、加密解密、字符串处理等
- 灵活配置：采用工厂模式和策略模式，支持灵活配置和扩展
- 自动装配：基于Spring Boot自动装配机制，使用简单方便

## 快速开始

### Maven 依赖

```xml
<dependency>
    <groupId>com.codeutils</groupId>
    <artifactId>codeutils-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 基本配置

在 `application.yml` 或 `application.properties` 中添加相关配置：

```yaml
codeutils:
  # 文件存储配置
  storage:
    # 存储类型：oss(阿里云)、cos(腾讯云)、minio
    type: oss
    oss:
      endpoint: http://oss-cn-hangzhou.aliyuncs.com
      access-key: your-access-key
      secret-key: your-secret-key
      bucket-name: your-bucket-name
    cos:
      region: ap-guangzhou
      secret-id: your-secret-id
      secret-key: your-secret-key
      bucket-name: your-bucket-name
    minio:
      endpoint: http://localhost:9000
      access-key: minioadmin
      secret-key: minioadmin
      bucket-name: your-bucket-name
  
  # 短信服务配置
  sms:
    # 短信类型：aliyun(阿里云)、tencent(腾讯云)
    type: aliyun
    aliyun:
      access-key: your-access-key
      secret-key: your-secret-key
      sign-name: your-sign-name
      template-code: your-template-code
    tencent:
      secret-id: your-secret-id
      secret-key: your-secret-key
      app-id: your-app-id
      sign-name: your-sign-name
      template-id: your-template-id
  
  # 支付服务配置
  pay:
    # 支付类型：wechat(微信)、alipay(支付宝)、unionpay(银联)
    type: wechat
    wechat:
      app-id: your-app-id
      mch-id: your-mch-id
      api-key: your-api-key
      cert-path: classpath:cert/wechat_cert.p12
    alipay:
      app-id: your-app-id
      private-key: your-private-key
      public-key: your-public-key
      gateway-url: https://openapi.alipay.com/gateway.do
    unionpay:
      mch-id: your-mch-id
      cert-path: classpath:cert/unionpay_cert.pfx
      cert-pwd: your-cert-pwd
```

## 使用示例

### 文件存储

```java
@Autowired
private StorageService storageService;

// 上传文件
String fileUrl = storageService.uploadFile(file);

// 下载文件
byte[] fileBytes = storageService.downloadFile(fileUrl);

// 删除文件
boolean result = storageService.deleteFile(fileUrl);
```

### 短信服务

```java
@Autowired
private SmsService smsService;

// 发送短信
boolean result = smsService.sendSms("13812345678", Map.of("code", "123456"));

// 发送批量短信
boolean result = smsService.batchSendSms(List.of("13812345678", "13987654321"), Map.of("code", "123456"));
```

### 支付服务

```java
@Autowired
private PayService payService;

// 创建支付订单
PayResult result = payService.createOrder(PayOrder.builder()
    .outTradeNo("123456789")
    .amount(new BigDecimal("0.01"))
    .subject("测试商品")
    .build());

// 查询支付结果
PayQueryResult queryResult = payService.queryOrder("123456789");

// 退款
RefundResult refundResult = payService.refund(RefundOrder.builder()
    .outTradeNo("123456789")
    .outRefundNo("R123456789")
    .amount(new BigDecimal("0.01"))
    .reason("商品退款")
    .build());
```

## 扩展开发

本项目采用工厂模式和策略模式设计，便于扩展新功能。如需添加新的存储服务商、短信服务商或支付服务商，只需实现对应的接口并注册到对应的工厂即可。

详细开发文档请参考相关接口的实现说明。

## 贡献指南

欢迎提交Issue和Pull Request，共同完善本项目。

## 许可证

Apache License 2.0 