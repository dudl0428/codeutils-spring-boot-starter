package com.codeutils.pay.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 退款结果模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundResult {
    
    /**
     * 退款状态：SUCCESS-成功，FAIL-失败，PROCESSING-处理中
     */
    private String status;
    
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
     * 第三方支付平台退款单号
     */
    private String refundNo;
    
    /**
     * 退款金额
     */
    private BigDecimal amount;
    
    /**
     * 退款时间
     */
    private Date refundTime;
    
    /**
     * 渠道类型（支付宝、微信等）
     */
    private String channel;
    
    /**
     * 错误码
     */
    private String errorCode;
    
    /**
     * 错误信息
     */
    private String errorMsg;
} 