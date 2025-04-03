package com.codeutils.pay.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付订单模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayOrder {
    
    /**
     * 商户订单号
     */
    private String outTradeNo;
    
    /**
     * 订单金额（元）
     */
    private BigDecimal amount;
    
    /**
     * 订单标题/商品名称
     */
    private String subject;
    
    /**
     * 商品描述
     */
    private String body;
    
    /**
     * 用户IP
     */
    private String clientIp;
    
    /**
     * 通知地址
     */
    private String notifyUrl;
    
    /**
     * 交易类型（如JSAPI、NATIVE、APP等）
     */
    private String tradeType;
    
    /**
     * 用户标识（如微信openid）
     */
    private String openid;
    
    /**
     * 订单失效时间（秒）
     */
    private Integer timeoutExpress;
    
    /**
     * 附加数据
     */
    private String attach;
    
    /**
     * 场景信息
     */
    private String sceneInfo;
    
    /**
     * 扩展参数，不同支付方式所需额外参数
     */
    private Map<String, Object> extraParams;
} 