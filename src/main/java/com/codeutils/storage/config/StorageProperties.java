package com.codeutils.storage.config;

import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 文件存储配置属性
 */
@Data
public class StorageProperties {
    
    /**
     * 存储类型：oss(阿里云)、cos(腾讯云)、minio
     */
    private String type = "oss";
    
    /**
     * 阿里云OSS配置
     */
    @NestedConfigurationProperty
    private OssProperties oss = new OssProperties();
    
    /**
     * 腾讯云COS配置
     */
    @NestedConfigurationProperty
    private CosProperties cos = new CosProperties();
    
    /**
     * MinIO配置
     */
    @NestedConfigurationProperty
    private MinioProperties minio = new MinioProperties();
    
    /**
     * 阿里云OSS配置
     */
    @Data
    public static class OssProperties {
        /**
         * 节点地址
         */
        private String endpoint;
        
        /**
         * 访问密钥
         */
        private String accessKey;
        
        /**
         * 密钥
         */
        private String secretKey;
        
        /**
         * 存储桶名称
         */
        private String bucketName;
    }
    
    /**
     * 腾讯云COS配置
     */
    @Data
    public static class CosProperties {
        /**
         * 地域
         */
        private String region;
        
        /**
         * 密钥ID
         */
        private String secretId;
        
        /**
         * 密钥
         */
        private String secretKey;
        
        /**
         * 存储桶名称
         */
        private String bucketName;
    }
    
    /**
     * MinIO配置
     */
    @Data
    public static class MinioProperties {
        /**
         * 节点地址
         */
        private String endpoint;
        
        /**
         * 访问密钥
         */
        private String accessKey;
        
        /**
         * 密钥
         */
        private String secretKey;
        
        /**
         * 存储桶名称
         */
        private String bucketName;
    }
} 