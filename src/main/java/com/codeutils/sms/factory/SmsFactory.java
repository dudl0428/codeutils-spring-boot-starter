package com.codeutils.sms.factory;

import com.codeutils.sms.config.SmsProperties;
import com.codeutils.sms.service.SmsService;
import com.codeutils.sms.service.impl.AliyunSmsServiceImpl;
import com.codeutils.sms.service.impl.TencentSmsServiceImpl;

/**
 * 短信服务工厂类
 */
public class SmsFactory {
    
    private final SmsProperties smsProperties;
    
    public SmsFactory(SmsProperties smsProperties) {
        this.smsProperties = smsProperties;
    }
    
    /**
     * 创建短信服务实例
     * @return 短信服务实现
     */
    public SmsService createSmsService() {
        String type = smsProperties.getType();
        
        switch (type.toLowerCase()) {
            case "aliyun":
                return new AliyunSmsServiceImpl(smsProperties.getAliyun());
            case "tencent":
                return new TencentSmsServiceImpl(smsProperties.getTencent());
            default:
                throw new IllegalArgumentException("不支持的短信服务类型: " + type);
        }
    }
} 