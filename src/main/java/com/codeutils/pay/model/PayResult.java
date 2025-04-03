package com.codeutils.pay.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付结果模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayResult {
    
    /**
     * 支付状态：SUCCESS-成功，FAIL-失败
     */
    private String status;
    
    /**
     * 商户订单号
     */
    private String outTradeNo;
    
    /**
     * 第三方支付平台订单号
     */
    private String tradeNo;
    
    /**
     * 订单金额
     */
    private BigDecimal amount;
    
    /**
     * 渠道类型（支付宝、微信等）
     */
    private String channel;
    
    /**
     * 附加数据
     */
    private String attach;
    
    /**
     * 支付链接（二维码链接等）
     */
    private String payUrl;
    
    /**
     * 支付表单（跳转支付页的HTML表单）
     */
    private String payForm;
    
    /**
     * 微信支付参数（APP、小程序、JSAPI等支付方式返回的支付参数）
     */
    private Map<String, String> payParams;
    
    /**
     * 错误码
     */
    private String errorCode;
    
    /**
     * 错误信息
     */
    private String errorMsg;
} 