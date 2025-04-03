package com.codeutils.sms.config;

import com.codeutils.core.config.CodeUtilsProperties;
import com.codeutils.sms.factory.SmsFactory;
import com.codeutils.sms.service.SmsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 短信服务自动配置类
 */
@Configuration
@ConditionalOnProperty(prefix = "codeutils", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SmsAutoConfiguration {
    
    private final CodeUtilsProperties codeUtilsProperties;
    
    public SmsAutoConfiguration(CodeUtilsProperties codeUtilsProperties) {
        this.codeUtilsProperties = codeUtilsProperties;
    }
    
    @Bean
    @ConditionalOnMissingBean
    public SmsFactory smsFactory() {
        return new SmsFactory(codeUtilsProperties.getSms());
    }
    
    @Bean
    @ConditionalOnMissingBean
    public SmsService smsService(SmsFactory smsFactory) {
        return smsFactory.createSmsService();
    }
} 