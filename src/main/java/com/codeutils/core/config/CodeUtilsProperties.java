package com.codeutils.core.config;

import com.codeutils.pay.config.PayProperties;
import com.codeutils.sms.config.SmsProperties;
import com.codeutils.storage.config.StorageProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * CodeUtils工具包配置属性类
 */
@Data
@ConfigurationProperties(prefix = "codeutils")
public class CodeUtilsProperties {
    
    /**
     * 是否启用CodeUtils
     */
    private boolean enabled = true;
    
    /**
     * 文件存储配置
     */
    @NestedConfigurationProperty
    private StorageProperties storage = new StorageProperties();
    
    /**
     * 短信服务配置
     */
    @NestedConfigurationProperty
    private SmsProperties sms = new SmsProperties();
    
    /**
     * 支付服务配置
     */
    @NestedConfigurationProperty
    private PayProperties pay = new PayProperties();
} 