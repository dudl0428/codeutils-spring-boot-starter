package com.codeutils.pay.factory;

import com.codeutils.pay.config.PayProperties;
import com.codeutils.pay.service.PayService;
import com.codeutils.pay.service.impl.AlipayServiceImpl;
import com.codeutils.pay.service.impl.WechatPayServiceImpl;

/**
 * 支付服务工厂类
 */
public class PayFactory {
    
    private final PayProperties payProperties;
    
    public PayFactory(PayProperties payProperties) {
        this.payProperties = payProperties;
    }
    
    /**
     * 创建支付服务实例
     * @return 支付服务实现
     */
    public PayService createPayService() {
        String type = payProperties.getType();
        
        switch (type.toLowerCase()) {
            case "wechat":
                return new WechatPayServiceImpl(payProperties.getWechat());
            case "alipay":
                return new AlipayServiceImpl(payProperties.getAlipay());
            // 银联支付暂未实现，可根据需要添加
            default:
                throw new IllegalArgumentException("不支持的支付类型: " + type);
        }
    }
} 