package com.codeutils.storage.factory;

import com.codeutils.storage.config.StorageProperties;
import com.codeutils.storage.service.StorageService;
import com.codeutils.storage.service.impl.CosStorageServiceImpl;
import com.codeutils.storage.service.impl.MinioStorageServiceImpl;
import com.codeutils.storage.service.impl.OssStorageServiceImpl;

/**
 * 存储服务工厂类
 */
public class StorageFactory {
    
    private final StorageProperties storageProperties;
    
    public StorageFactory(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }
    
    /**
     * 创建存储服务实例
     * @return 存储服务实现
     */
    public StorageService createStorageService() {
        String type = storageProperties.getType();
        
        switch (type.toLowerCase()) {
            case "oss":
                return new OssStorageServiceImpl(storageProperties.getOss());
            case "cos":
                return new CosStorageServiceImpl(storageProperties.getCos());
            case "minio":
                return new MinioStorageServiceImpl(storageProperties.getMinio());
            default:
                throw new IllegalArgumentException("不支持的存储类型: " + type);
        }
    }
} 