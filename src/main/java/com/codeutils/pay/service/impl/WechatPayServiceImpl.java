package com.codeutils.pay.service.impl;

import com.codeutils.pay.config.PayProperties;
import com.codeutils.pay.model.PayOrder;
import com.codeutils.pay.model.PayQueryResult;
import com.codeutils.pay.model.PayResult;
import com.codeutils.pay.model.RefundOrder;
import com.codeutils.pay.model.RefundResult;
import com.codeutils.pay.service.PayService;
import com.github.wechatpay.apiv3.WechatPayHttpClientBuilder;
import com.github.wechatpay.apiv3.auth.AutoUpdateCertificatesVerifier;
import com.github.wechatpay.apiv3.auth.PrivateKeySigner;
import com.github.wechatpay.apiv3.auth.WechatPay2Credentials;
import com.github.wechatpay.apiv3.auth.WechatPay2Validator;
import com.github.wechatpay.apiv3.util.PemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付服务实现
 */
public class WechatPayServiceImpl implements PayService {
    
    private final PayProperties.WechatPayProperties wechatProperties;
    private final CloseableHttpClient httpClient;
    private final Gson gson = new Gson();
    
    /**
     * 微信支付API基础地址
     */
    private static final String API_BASE_URL = "https://api.mch.weixin.qq.com";
    
    /**
     * 支付API路径
     */
    private static final String PAY_API_PATH = "/v3/pay/transactions/";
    
    /**
     * 订单查询API路径
     */
    private static final String ORDER_QUERY_API_PATH = "/v3/pay/transactions/out-trade-no/";
    
    /**
     * 关闭订单API路径
     */
    private static final String CLOSE_ORDER_API_PATH = "/v3/pay/transactions/out-trade-no/%s/close";
    
    /**
     * 退款API路径
     */
    private static final String REFUND_API_PATH = "/v3/refund/domestic/refunds";
    
    /**
     * 退款查询API路径
     */
    private static final String REFUND_QUERY_API_PATH = "/v3/refund/domestic/refunds/";
    
    public WechatPayServiceImpl(PayProperties.WechatPayProperties wechatProperties) {
        this.wechatProperties = wechatProperties;
        
        try {
            // 加载证书
            PrivateKey privateKey = getPrivateKey();
            
            // 创建验证器
            AutoUpdateCertificatesVerifier verifier = new AutoUpdateCertificatesVerifier(
                new WechatPay2Credentials(wechatProperties.getMchId(), 
                    new PrivateKeySigner(wechatProperties.getCertSerialNo(), privateKey)),
                wechatProperties.getApiV3Key().getBytes(StandardCharsets.UTF_8));
            
            // 创建HttpClient
            this.httpClient = WechatPayHttpClientBuilder.create()
                .withMerchant(wechatProperties.getMchId(), wechatProperties.getCertSerialNo(), privateKey)
                .withValidator(new WechatPay2Validator(verifier))
                .build();
        } catch (Exception e) {
            throw new RuntimeException("初始化微信支付客户端失败", e);
        }
    }
    
    @Override
    public PayResult createOrder(PayOrder order) {
        try {
            // 默认支付类型
            String tradeType = StringUtils.hasText(order.getTradeType()) ? 
                order.getTradeType().toUpperCase() : "NATIVE";
            
            // 构建请求体
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("appid", wechatProperties.getAppId());
            requestMap.put("mchid", wechatProperties.getMchId());
            requestMap.put("description", order.getSubject());
            requestMap.put("out_trade_no", order.getOutTradeNo());
            requestMap.put("notify_url", StringUtils.hasText(order.getNotifyUrl()) ? 
                order.getNotifyUrl() : wechatProperties.getNotifyUrl());
            
            // 金额信息
            Map<String, Object> amountMap = new HashMap<>();
            // 微信支付金额单位为分
            int totalFee = order.getAmount().multiply(new BigDecimal("100")).intValue();
            amountMap.put("total", totalFee);
            amountMap.put("currency", "CNY");
            
            requestMap.put("amount", amountMap);
            
            // 附加数据
            if (StringUtils.hasText(order.getAttach())) {
                requestMap.put("attach", order.getAttach());
            }
            
            // 根据交易类型设置不同的请求参数
            if ("JSAPI".equals(tradeType) || "SMALL_PROGRAM".equals(tradeType)) {
                // JSAPI与小程序支付必须传openid
                if (!StringUtils.hasText(order.getOpenid())) {
                    throw new IllegalArgumentException("JSAPI支付必须提供用户的openid");
                }
                Map<String, String> payerMap = new HashMap<>();
                payerMap.put("openid", order.getOpenid());
                requestMap.put("payer", payerMap);
            } else if ("H5".equals(tradeType)) {
                // H5支付需要传场景信息
                if (StringUtils.hasText(order.getClientIp())) {
                    Map<String, Object> sceneInfo = new HashMap<>();
                    Map<String, String> h5Info = new HashMap<>();
                    h5Info.put("type", "Wap");
                    sceneInfo.put("h5_info", h5Info);
                    sceneInfo.put("payer_client_ip", order.getClientIp());
                    requestMap.put("scene_info", sceneInfo);
                }
            }
            
            // 发送请求
            String apiUrl = API_BASE_URL + PAY_API_PATH + tradeType.toLowerCase();
            String requestBody = gson.toJson(requestMap);
            
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
            
            CloseableHttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            
            // 解析响应
            if (statusCode >= 200 && statusCode < 300) {
                JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                
                PayResult.PayResultBuilder resultBuilder = PayResult.builder()
                    .status("SUCCESS")
                    .outTradeNo(order.getOutTradeNo())
                    .amount(order.getAmount())
                    .channel("wechat");
                
                // 不同交易类型返回不同的支付信息
                if ("NATIVE".equals(tradeType)) {
                    // 二维码链接
                    resultBuilder.payUrl(jsonResponse.get("code_url").getAsString());
                } else if ("JSAPI".equals(tradeType) || "SMALL_PROGRAM".equals(tradeType)) {
                    // 小程序/JSAPI支付需要的参数
                    Map<String, String> payParams = new HashMap<>();
                    payParams.put("appId", wechatProperties.getAppId());
                    payParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
                    payParams.put("nonceStr", generateNonceStr());
                    payParams.put("package", "prepay_id=" + jsonResponse.get("prepay_id").getAsString());
                    payParams.put("signType", "RSA");
                    // 签名逻辑略
                    payParams.put("paySign", "签名结果");
                    resultBuilder.payParams(payParams);
                } else if ("H5".equals(tradeType)) {
                    // H5支付返回的链接
                    resultBuilder.payUrl(jsonResponse.get("h5_url").getAsString());
                } else if ("APP".equals(tradeType)) {
                    // APP支付返回的参数
                    Map<String, String> payParams = new HashMap<>();
                    payParams.put("appid", wechatProperties.getAppId());
                    payParams.put("partnerid", wechatProperties.getMchId());
                    payParams.put("prepayid", jsonResponse.get("prepay_id").getAsString());
                    payParams.put("package", "Sign=WXPay");
                    payParams.put("noncestr", generateNonceStr());
                    payParams.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
                    // 签名逻辑略
                    payParams.put("sign", "签名结果");
                    resultBuilder.payParams(payParams);
                }
                
                return resultBuilder.build();
            } else {
                // 处理错误
                JsonObject errorResponse = gson.fromJson(responseBody, JsonObject.class);
                return PayResult.builder()
                    .status("FAIL")
                    .outTradeNo(order.getOutTradeNo())
                    .errorCode(errorResponse.has("code") ? errorResponse.get("code").getAsString() : String.valueOf(statusCode))
                    .errorMsg(errorResponse.has("message") ? errorResponse.get("message").getAsString() : "未知错误")
                    .build();
            }
            
        } catch (Exception e) {
            return PayResult.builder()
                .status("FAIL")
                .outTradeNo(order.getOutTradeNo())
                .errorCode("SYSTEM_ERROR")
                .errorMsg(e.getMessage())
                .build();
        }
    }
    
    @Override
    public PayQueryResult queryOrder(String outTradeNo) {
        return queryOrder(outTradeNo, null);
    }
    
    @Override
    public PayQueryResult queryOrder(String outTradeNo, String tradeNo) {
        try {
            // 构建请求URL
            String apiUrl = API_BASE_URL + ORDER_QUERY_API_PATH + outTradeNo + "?mchid=" + wechatProperties.getMchId();
            
            HttpGet httpGet = new HttpGet(apiUrl);
            httpGet.setHeader("Accept", "application/json");
            
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            
            if (statusCode >= 200 && statusCode < 300) {
                JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                
                PayQueryResult.PayQueryResultBuilder resultBuilder = PayQueryResult.builder()
                    .outTradeNo(outTradeNo)
                    .channel("wechat");
                
                if (jsonResponse.has("transaction_id")) {
                    resultBuilder.tradeNo(jsonResponse.get("transaction_id").getAsString());
                }
                
                if (jsonResponse.has("trade_state")) {
                    String tradeState = jsonResponse.get("trade_state").getAsString();
                    switch (tradeState) {
                        case "SUCCESS":
                            resultBuilder.tradeStatus("TRADE_SUCCESS");
                            break;
                        case "REFUND":
                            resultBuilder.tradeStatus("TRADE_SUCCESS");
                            break;
                        case "NOTPAY":
                            resultBuilder.tradeStatus("WAIT_BUYER_PAY");
                            break;
                        case "CLOSED":
                            resultBuilder.tradeStatus("TRADE_CLOSED");
                            break;
                        case "USERPAYING":
                            resultBuilder.tradeStatus("WAIT_BUYER_PAY");
                            break;
                        case "PAYERROR":
                            resultBuilder.tradeStatus("TRADE_CLOSED");
                            break;
                        default:
                            resultBuilder.tradeStatus(tradeState);
                    }
                }
                
                if (jsonResponse.has("amount") && jsonResponse.getAsJsonObject("amount").has("total")) {
                    // 微信返回的金额单位为分，需要转换为元
                    int totalFee = jsonResponse.getAsJsonObject("amount").get("total").getAsInt();
                    resultBuilder.amount(new BigDecimal(totalFee).divide(new BigDecimal("100")));
                }
                
                if (jsonResponse.has("payer") && jsonResponse.getAsJsonObject("payer").has("openid")) {
                    resultBuilder.buyerId(jsonResponse.getAsJsonObject("payer").get("openid").getAsString());
                }
                
                // 其他字段的处理略...
                
                return resultBuilder.build();
            } else {
                // 处理错误
                JsonObject errorResponse = gson.fromJson(responseBody, JsonObject.class);
                return PayQueryResult.builder()
                    .outTradeNo(outTradeNo)
                    .channel("wechat")
                    .errorCode(errorResponse.has("code") ? errorResponse.get("code").getAsString() : String.valueOf(statusCode))
                    .errorMsg(errorResponse.has("message") ? errorResponse.get("message").getAsString() : "未知错误")
                    .build();
            }
        } catch (Exception e) {
            return PayQueryResult.builder()
                .outTradeNo(outTradeNo)
                .channel("wechat")
                .errorCode("SYSTEM_ERROR")
                .errorMsg(e.getMessage())
                .build();
        }
    }
    
    @Override
    public boolean closeOrder(String outTradeNo) {
        try {
            // 构建请求URL
            String apiUrl = API_BASE_URL + String.format(CLOSE_ORDER_API_PATH, outTradeNo);
            
            // 构建请求体
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("mchid", wechatProperties.getMchId());
            
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setEntity(new StringEntity(gson.toJson(requestMap), StandardCharsets.UTF_8));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
            
            CloseableHttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            
            // 204状态码表示成功
            return statusCode == 204;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public RefundResult refund(RefundOrder refundOrder) {
        try {
            // 构建请求体
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("out_trade_no", refundOrder.getOutTradeNo());
            requestMap.put("out_refund_no", refundOrder.getOutRefundNo());
            
            if (StringUtils.hasText(refundOrder.getReason())) {
                requestMap.put("reason", refundOrder.getReason());
            }
            
            if (StringUtils.hasText(refundOrder.getNotifyUrl())) {
                requestMap.put("notify_url", refundOrder.getNotifyUrl());
            }
            
            // 金额信息
            Map<String, Object> amountMap = new HashMap<>();
            // 微信支付金额单位为分
            int refundFee = refundOrder.getAmount().multiply(new BigDecimal("100")).intValue();
            amountMap.put("refund", refundFee);
            
            // 如果没有提供订单总金额，使用退款金额作为总金额
            int totalFee = refundOrder.getTotalAmount() != null ?
                refundOrder.getTotalAmount().multiply(new BigDecimal("100")).intValue() : refundFee;
            amountMap.put("total", totalFee);
            
            amountMap.put("currency", "CNY");
            
            requestMap.put("amount", amountMap);
            
            // 发送请求
            HttpPost httpPost = new HttpPost(API_BASE_URL + REFUND_API_PATH);
            httpPost.setEntity(new StringEntity(gson.toJson(requestMap), StandardCharsets.UTF_8));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
            
            CloseableHttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            
            if (statusCode >= 200 && statusCode < 300) {
                JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                
                RefundResult.RefundResultBuilder resultBuilder = RefundResult.builder()
                    .outTradeNo(refundOrder.getOutTradeNo())
                    .outRefundNo(refundOrder.getOutRefundNo())
                    .amount(refundOrder.getAmount())
                    .channel("wechat");
                
                if (jsonResponse.has("status")) {
                    String status = jsonResponse.get("status").getAsString();
                    switch (status) {
                        case "SUCCESS":
                            resultBuilder.status("SUCCESS");
                            break;
                        case "PROCESSING":
                            resultBuilder.status("PROCESSING");
                            break;
                        case "ABNORMAL":
                            resultBuilder.status("FAIL");
                            break;
                        default:
                            resultBuilder.status(status);
                    }
                }
                
                if (jsonResponse.has("refund_id")) {
                    resultBuilder.refundNo(jsonResponse.get("refund_id").getAsString());
                }
                
                if (jsonResponse.has("transaction_id")) {
                    resultBuilder.tradeNo(jsonResponse.get("transaction_id").getAsString());
                }
                
                return resultBuilder.build();
            } else {
                // 处理错误
                JsonObject errorResponse = gson.fromJson(responseBody, JsonObject.class);
                return RefundResult.builder()
                    .status("FAIL")
                    .outTradeNo(refundOrder.getOutTradeNo())
                    .outRefundNo(refundOrder.getOutRefundNo())
                    .channel("wechat")
                    .errorCode(errorResponse.has("code") ? errorResponse.get("code").getAsString() : String.valueOf(statusCode))
                    .errorMsg(errorResponse.has("message") ? errorResponse.get("message").getAsString() : "未知错误")
                    .build();
            }
        } catch (Exception e) {
            return RefundResult.builder()
                .status("FAIL")
                .outTradeNo(refundOrder.getOutTradeNo())
                .outRefundNo(refundOrder.getOutRefundNo())
                .channel("wechat")
                .errorCode("SYSTEM_ERROR")
                .errorMsg(e.getMessage())
                .build();
        }
    }
    
    @Override
    public RefundResult queryRefund(String outRefundNo) {
        try {
            // 构建请求URL
            String apiUrl = API_BASE_URL + REFUND_QUERY_API_PATH + outRefundNo;
            
            HttpGet httpGet = new HttpGet(apiUrl);
            httpGet.setHeader("Accept", "application/json");
            
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            
            if (statusCode >= 200 && statusCode < 300) {
                JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                
                RefundResult.RefundResultBuilder resultBuilder = RefundResult.builder()
                    .outRefundNo(outRefundNo)
                    .channel("wechat");
                
                if (jsonResponse.has("out_trade_no")) {
                    resultBuilder.outTradeNo(jsonResponse.get("out_trade_no").getAsString());
                }
                
                if (jsonResponse.has("refund_id")) {
                    resultBuilder.refundNo(jsonResponse.get("refund_id").getAsString());
                }
                
                if (jsonResponse.has("transaction_id")) {
                    resultBuilder.tradeNo(jsonResponse.get("transaction_id").getAsString());
                }
                
                if (jsonResponse.has("amount") && jsonResponse.getAsJsonObject("amount").has("refund")) {
                    // 微信返回的金额单位为分，需要转换为元
                    int refundFee = jsonResponse.getAsJsonObject("amount").get("refund").getAsInt();
                    resultBuilder.amount(new BigDecimal(refundFee).divide(new BigDecimal("100")));
                }
                
                if (jsonResponse.has("status")) {
                    String status = jsonResponse.get("status").getAsString();
                    switch (status) {
                        case "SUCCESS":
                            resultBuilder.status("SUCCESS");
                            break;
                        case "PROCESSING":
                            resultBuilder.status("PROCESSING");
                            break;
                        case "ABNORMAL":
                            resultBuilder.status("FAIL");
                            break;
                        default:
                            resultBuilder.status(status);
                    }
                }
                
                return resultBuilder.build();
            } else {
                // 处理错误
                JsonObject errorResponse = gson.fromJson(responseBody, JsonObject.class);
                return RefundResult.builder()
                    .status("FAIL")
                    .outRefundNo(outRefundNo)
                    .channel("wechat")
                    .errorCode(errorResponse.has("code") ? errorResponse.get("code").getAsString() : String.valueOf(statusCode))
                    .errorMsg(errorResponse.has("message") ? errorResponse.get("message").getAsString() : "未知错误")
                    .build();
            }
        } catch (Exception e) {
            return RefundResult.builder()
                .status("FAIL")
                .outRefundNo(outRefundNo)
                .channel("wechat")
                .errorCode("SYSTEM_ERROR")
                .errorMsg(e.getMessage())
                .build();
        }
    }
    
    @Override
    public String handleNotify(Map<String, String> params, Map<String, String> headers) {
        // 支付通知处理逻辑
        // 此处应该验证签名，解析通知数据，更新支付状态等
        // 详细实现略
        
        // 返回成功处理的响应
        return "{\n\"code\": \"SUCCESS\",\n\"message\": \"成功\"\n}";
    }
    
    /**
     * 从配置加载私钥
     * @return 私钥对象
     */
    private PrivateKey getPrivateKey() {
        try {
            // 私钥可能以字符串或文件路径形式提供
            // 这里简化实现，假设私钥直接在配置中，实际应该从文件加载
            // 示例仅作参考，实际项目中应从安全位置加载密钥
            String privateKeyString = "..."; // 此处应替换为实际私钥内容或加载逻辑
            return PemUtil.loadPrivateKey(new ByteArrayInputStream(privateKeyString.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("加载微信支付私钥失败", e);
        }
    }
    
    /**
     * 生成随机字符串
     * @return 随机字符串
     */
    private String generateNonceStr() {
        return java.util.UUID.randomUUID().toString().replaceAll("-", "");
    }
} 