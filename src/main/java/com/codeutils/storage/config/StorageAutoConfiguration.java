package com.codeutils.storage.config;

import com.codeutils.core.config.CodeUtilsProperties;
import com.codeutils.storage.factory.StorageFactory;
import com.codeutils.storage.service.StorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 存储服务自动配置类
 */
@Configuration
@ConditionalOnProperty(prefix = "codeutils", name = "enabled", havingValue = "true", matchIfMissing = true)
public class StorageAutoConfiguration {
    
    private final CodeUtilsProperties codeUtilsProperties;
    
    public StorageAutoConfiguration(CodeUtilsProperties codeUtilsProperties) {
        this.codeUtilsProperties = codeUtilsProperties;
    }
    
    @Bean
    @ConditionalOnMissingBean
    public StorageFactory storageFactory() {
        return new StorageFactory(codeUtilsProperties.getStorage());
    }
    
    @Bean
    @ConditionalOnMissingBean
    public StorageService storageService(StorageFactory storageFactory) {
        return storageFactory.createStorageService();
    }
} 