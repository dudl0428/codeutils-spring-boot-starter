package com.codeutils.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 短信服务配置属性
 */
@Data
public class SmsProperties {
    
    /**
     * 短信类型：aliyun(阿里云)、tencent(腾讯云)
     */
    private String type = "aliyun";
    
    /**
     * 阿里云短信配置
     */
    @NestedConfigurationProperty
    private AliyunSmsProperties aliyun = new AliyunSmsProperties();
    
    /**
     * 腾讯云短信配置
     */
    @NestedConfigurationProperty
    private TencentSmsProperties tencent = new TencentSmsProperties();
    
    /**
     * 阿里云短信配置
     */
    @Data
    public static class AliyunSmsProperties {
        /**
         * 访问密钥
         */
        private String accessKey;
        
        /**
         * 密钥
         */
        private String secretKey;
        
        /**
         * 签名名称
         */
        private String signName;
        
        /**
         * 模板代码
         */
        private String templateCode;
    }
    
    /**
     * 腾讯云短信配置
     */
    @Data
    public static class TencentSmsProperties {
        /**
         * 密钥ID
         */
        private String secretId;
        
        /**
         * 密钥
         */
        private String secretKey;
        
        /**
         * 应用ID
         */
        private String appId;
        
        /**
         * 签名名称
         */
        private String signName;
        
        /**
         * 模板ID
         */
        private String templateId;
    }
} 