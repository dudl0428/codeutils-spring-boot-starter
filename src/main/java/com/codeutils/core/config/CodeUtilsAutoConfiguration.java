package com.codeutils.core.config;

import com.codeutils.pay.config.PayAutoConfiguration;
import com.codeutils.sms.config.SmsAutoConfiguration;
import com.codeutils.storage.config.StorageAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * CodeUtils自动配置类
 */
@Configuration
@EnableConfigurationProperties(CodeUtilsProperties.class)
@ConditionalOnProperty(prefix = "codeutils", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import({
    StorageAutoConfiguration.class,
    SmsAutoConfiguration.class,
    PayAutoConfiguration.class
})
public class CodeUtilsAutoConfiguration {
    
} 