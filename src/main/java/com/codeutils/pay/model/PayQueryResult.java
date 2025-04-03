package com.codeutils.pay.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付查询结果模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayQueryResult {
    
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
     * 交易状态：
     * WAIT_BUYER_PAY：交易创建，等待买家付款
     * TRADE_CLOSED：未付款交易超时关闭，或支付完成后全额退款
     * TRADE_SUCCESS：交易支付成功
     * TRADE_FINISHED：交易结束，不可退款
     */
    private String tradeStatus;
    
    /**
     * 支付时间
     */
    private Date payTime;
    
    /**
     * 买家ID
     */
    private String buyerId;
    
    /**
     * 交易描述
     */
    private String body;
    
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