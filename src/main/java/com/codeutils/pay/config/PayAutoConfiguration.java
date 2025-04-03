package com.codeutils.pay.config;

import com.codeutils.core.config.CodeUtilsProperties;
import com.codeutils.pay.factory.PayFactory;
import com.codeutils.pay.service.PayService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支付服务自动配置类
 */
@Configuration
@ConditionalOnProperty(prefix = "codeutils", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PayAutoConfiguration {
    
    private final CodeUtilsProperties codeUtilsProperties;
    
    public PayAutoConfiguration(CodeUtilsProperties codeUtilsProperties) {
        this.codeUtilsProperties = codeUtilsProperties;
    }
    
    @Bean
    @ConditionalOnMissingBean
    public PayFactory payFactory() {
        return new PayFactory(codeUtilsProperties.getPay());
    }
    
    @Bean
    @ConditionalOnMissingBean
    public PayService payService(PayFactory payFactory) {
        return payFactory.createPayService();
    }
} 