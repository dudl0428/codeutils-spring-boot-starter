package com.codeutils.pay.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.codeutils.pay.config.PayProperties;
import com.codeutils.pay.model.PayOrder;
import com.codeutils.pay.model.PayQueryResult;
import com.codeutils.pay.model.PayResult;
import com.codeutils.pay.model.RefundOrder;
import com.codeutils.pay.model.RefundResult;
import com.codeutils.pay.service.PayService;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝支付服务实现
 */
public class AlipayServiceImpl implements PayService {
    
    private final PayProperties.AlipayProperties alipayProperties;
    private final AlipayClient alipayClient;
    
    public AlipayServiceImpl(PayProperties.AlipayProperties alipayProperties) {
        this.alipayProperties = alipayProperties;
        
        // 初始化支付宝客户端
        this.alipayClient = new DefaultAlipayClient(
            alipayProperties.getGatewayUrl(),
            alipayProperties.getAppId(),
            alipayProperties.getPrivateKey(),
            "json",
            "UTF-8",
            alipayProperties.getAlipayPublicKey(),
            "RSA2"
        );
    }
    
    @Override
    public PayResult createOrder(PayOrder order) {
        try {
            String tradeType = StringUtils.hasText(order.getTradeType()) ? 
                order.getTradeType().toLowerCase() : "page";
            
            if ("page".equals(tradeType)) {
                return createPageOrder(order);
            } else if ("wap".equals(tradeType)) {
                return createWapOrder(order);
            } else if ("app".equals(tradeType)) {
                return createAppOrder(order);
            } else {
                return PayResult.builder()
                    .status("FAIL")
                    .outTradeNo(order.getOutTradeNo())
                    .errorCode("INVALID_TRADE_TYPE")
                    .errorMsg("不支持的交易类型: " + tradeType)
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
    
    /**
     * 创建电脑网站支付订单
     */
    private PayResult createPageOrder(PayOrder order) throws AlipayApiException {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setReturnUrl(alipayProperties.getReturnUrl());
        request.setNotifyUrl(StringUtils.hasText(order.getNotifyUrl()) ? 
            order.getNotifyUrl() : alipayProperties.getNotifyUrl());
        
        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("out_trade_no", order.getOutTradeNo());
        bizContent.put("total_amount", order.getAmount().toString());
        bizContent.put("subject", order.getSubject());
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        
        if (StringUtils.hasText(order.getBody())) {
            bizContent.put("body", order.getBody());
        }
        
        if (order.getTimeoutExpress() != null) {
            bizContent.put("timeout_express", order.getTimeoutExpress() + "m");
        }
        
        if (StringUtils.hasText(order.getAttach())) {
            bizContent.put("passback_params", order.getAttach());
        }
        
        request.setBizContent(new com.google.gson.Gson().toJson(bizContent));
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        
        if (response.isSuccess()) {
            return PayResult.builder()
                .status("SUCCESS")
                .outTradeNo(order.getOutTradeNo())
                .amount(order.getAmount())
                .channel("alipay")
                .payForm(response.getBody())
                .build();
        } else {
            return PayResult.builder()
                .status("FAIL")
                .outTradeNo(order.getOutTradeNo())
                .errorCode(response.getCode())
                .errorMsg(response.getMsg())
                .build();
        }
    }
    
    /**
     * 创建手机网站支付订单
     */
    private PayResult createWapOrder(PayOrder order) throws AlipayApiException {
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        request.setReturnUrl(alipayProperties.getReturnUrl());
        request.setNotifyUrl(StringUtils.hasText(order.getNotifyUrl()) ? 
            order.getNotifyUrl() : alipayProperties.getNotifyUrl());
        
        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("out_trade_no", order.getOutTradeNo());
        bizContent.put("total_amount", order.getAmount().toString());
        bizContent.put("subject", order.getSubject());
        bizContent.put("product_code", "QUICK_WAP_WAY");
        
        if (StringUtils.hasText(order.getBody())) {
            bizContent.put("body", order.getBody());
        }
        
        if (order.getTimeoutExpress() != null) {
            bizContent.put("timeout_express", order.getTimeoutExpress() + "m");
        }
        
        if (StringUtils.hasText(order.getAttach())) {
            bizContent.put("passback_params", order.getAttach());
        }
        
        request.setBizContent(new com.google.gson.Gson().toJson(bizContent));
        AlipayTradeWapPayResponse response = alipayClient.pageExecute(request);
        
        if (response.isSuccess()) {
            return PayResult.builder()
                .status("SUCCESS")
                .outTradeNo(order.getOutTradeNo())
                .amount(order.getAmount())
                .channel("alipay")
                .payForm(response.getBody())
                .build();
        } else {
            return PayResult.builder()
                .status("FAIL")
                .outTradeNo(order.getOutTradeNo())
                .errorCode(response.getCode())
                .errorMsg(response.getMsg())
                .build();
        }
    }
    
    /**
     * 创建APP支付订单
     */
    private PayResult createAppOrder(PayOrder order) throws AlipayApiException {
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        request.setNotifyUrl(StringUtils.hasText(order.getNotifyUrl()) ? 
            order.getNotifyUrl() : alipayProperties.getNotifyUrl());
        
        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("out_trade_no", order.getOutTradeNo());
        bizContent.put("total_amount", order.getAmount().toString());
        bizContent.put("subject", order.getSubject());
        bizContent.put("product_code", "QUICK_MSECURITY_PAY");
        
        if (StringUtils.hasText(order.getBody())) {
            bizContent.put("body", order.getBody());
        }
        
        if (order.getTimeoutExpress() != null) {
            bizContent.put("timeout_express", order.getTimeoutExpress() + "m");
        }
        
        if (StringUtils.hasText(order.getAttach())) {
            bizContent.put("passback_params", order.getAttach());
        }
        
        request.setBizContent(new com.google.gson.Gson().toJson(bizContent));
        AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
        
        if (response.isSuccess()) {
            Map<String, String> payParams = new HashMap<>();
            payParams.put("orderInfo", response.getBody());
            
            return PayResult.builder()
                .status("SUCCESS")
                .outTradeNo(order.getOutTradeNo())
                .amount(order.getAmount())
                .channel("alipay")
                .payParams(payParams)
                .build();
        } else {
            return PayResult.builder()
                .status("FAIL")
                .outTradeNo(order.getOutTradeNo())
                .errorCode(response.getCode())
                .errorMsg(response.getMsg())
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
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", outTradeNo);
            
            if (StringUtils.hasText(tradeNo)) {
                bizContent.put("trade_no", tradeNo);
            }
            
            request.setBizContent(new com.google.gson.Gson().toJson(bizContent));
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            
            if (response.isSuccess()) {
                PayQueryResult.PayQueryResultBuilder resultBuilder = PayQueryResult.builder()
                    .outTradeNo(outTradeNo)
                    .channel("alipay");
                
                if (StringUtils.hasText(response.getTradeNo())) {
                    resultBuilder.tradeNo(response.getTradeNo());
                }
                
                if (StringUtils.hasText(response.getTotalAmount())) {
                    resultBuilder.amount(new BigDecimal(response.getTotalAmount()));
                }
                
                if (StringUtils.hasText(response.getTradeStatus())) {
                    resultBuilder.tradeStatus(response.getTradeStatus());
                }
                
                if (StringUtils.hasText(response.getBuyerUserId())) {
                    resultBuilder.buyerId(response.getBuyerUserId());
                }
                
                if (StringUtils.hasText(response.getBody())) {
                    resultBuilder.body(response.getBody());
                }
                
                return resultBuilder.build();
            } else {
                return PayQueryResult.builder()
                    .outTradeNo(outTradeNo)
                    .channel("alipay")
                    .errorCode(response.getCode())
                    .errorMsg(response.getMsg())
                    .build();
            }
        } catch (Exception e) {
            return PayQueryResult.builder()
                .outTradeNo(outTradeNo)
                .channel("alipay")
                .errorCode("SYSTEM_ERROR")
                .errorMsg(e.getMessage())
                .build();
        }
    }
    
    @Override
    public boolean closeOrder(String outTradeNo) {
        try {
            AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
            
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", outTradeNo);
            
            request.setBizContent(new com.google.gson.Gson().toJson(bizContent));
            AlipayTradeCloseResponse response = alipayClient.execute(request);
            
            return response.isSuccess();
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public RefundResult refund(RefundOrder refundOrder) {
        try {
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", refundOrder.getOutTradeNo());
            
            if (StringUtils.hasText(refundOrder.getOutRefundNo())) {
                bizContent.put("out_request_no", refundOrder.getOutRefundNo());
            } else {
                bizContent.put("out_request_no", refundOrder.getOutTradeNo());
            }
            
            bizContent.put("refund_amount", refundOrder.getAmount().toString());
            
            if (StringUtils.hasText(refundOrder.getReason())) {
                bizContent.put("refund_reason", refundOrder.getReason());
            }
            
            request.setBizContent(new com.google.gson.Gson().toJson(bizContent));
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            
            if (response.isSuccess()) {
                return RefundResult.builder()
                    .status("SUCCESS")
                    .outTradeNo(refundOrder.getOutTradeNo())
                    .outRefundNo(refundOrder.getOutRefundNo())
                    .tradeNo(response.getTradeNo())
                    .refundNo(response.getTradeNo())
                    .amount(new BigDecimal(response.getRefundFee()))
                    .channel("alipay")
                    .build();
            } else {
                return RefundResult.builder()
                    .status("FAIL")
                    .outTradeNo(refundOrder.getOutTradeNo())
                    .outRefundNo(refundOrder.getOutRefundNo())
                    .channel("alipay")
                    .errorCode(response.getCode())
                    .errorMsg(response.getMsg())
                    .build();
            }
        } catch (Exception e) {
            return RefundResult.builder()
                .status("FAIL")
                .outTradeNo(refundOrder.getOutTradeNo())
                .outRefundNo(refundOrder.getOutRefundNo())
                .channel("alipay")
                .errorCode("SYSTEM_ERROR")
                .errorMsg(e.getMessage())
                .build();
        }
    }
    
    @Override
    public RefundResult queryRefund(String outRefundNo) {
        try {
            AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
            
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_request_no", outRefundNo);
            
            request.setBizContent(new com.google.gson.Gson().toJson(bizContent));
            AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
            
            if (response.isSuccess()) {
                return RefundResult.builder()
                    .status("SUCCESS")
                    .outRefundNo(outRefundNo)
                    .outTradeNo(response.getOutTradeNo())
                    .tradeNo(response.getTradeNo())
                    .amount(new BigDecimal(response.getRefundAmount()))
                    .channel("alipay")
                    .build();
            } else {
                return RefundResult.builder()
                    .status("FAIL")
                    .outRefundNo(outRefundNo)
                    .channel("alipay")
                    .errorCode(response.getCode())
                    .errorMsg(response.getMsg())
                    .build();
            }
        } catch (Exception e) {
            return RefundResult.builder()
                .status("FAIL")
                .outRefundNo(outRefundNo)
                .channel("alipay")
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
        return "success";
    }
} 