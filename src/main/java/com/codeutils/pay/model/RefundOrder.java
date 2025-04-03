package com.codeutils.pay.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 退款订单模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundOrder {
    
    /**
     * 商户订单号
     */
    private String outTradeNo;
    
    /**
     * 商户退款单号
     */
    private String outRefundNo;
    
    /**
     * 第三方支付平台订单号
     */
    private String tradeNo;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 退款金额
     */
    private BigDecimal amount;
    
    /**
     * 退款原因
     */
    private String reason;
    
    /**
     * 退款通知地址
     */
    private String notifyUrl;
} 