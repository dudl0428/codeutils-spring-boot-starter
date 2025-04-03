package com.codeutils.storage.service.impl;

import com.codeutils.storage.config.StorageProperties;
import com.codeutils.storage.service.StorageService;
import io.minio.*;
import io.minio.http.Method;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * MinIO存储服务实现
 */
public class MinioStorageServiceImpl implements StorageService {
    
    private final StorageProperties.MinioProperties minioProperties;
    private final MinioClient minioClient;
    
    public MinioStorageServiceImpl(StorageProperties.MinioProperties minioProperties) {
        this.minioProperties = minioProperties;
        
        // 初始化MinIO客户端
        this.minioClient = MinioClient.builder()
            .endpoint(minioProperties.getEndpoint())
            .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
            .build();
        
        try {
            // 检查存储桶是否存在，不存在则创建
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioProperties.getBucketName())
                .build());
            
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .build());
            }
        } catch (Exception e) {
            throw new RuntimeException("初始化MinIO客户端失败", e);
        }
    }
    
    @Override
    public String uploadFile(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileName = generateFileName(originalFilename);
            return uploadFile(file.getInputStream(), fileName);
        } catch (Exception e) {
            throw new RuntimeException("上传文件到MinIO失败", e);
        }
    }
    
    @Override
    public String uploadFile(MultipartFile file, String fileName) {
        try {
            if (!StringUtils.hasText(fileName)) {
                fileName = generateFileName(file.getOriginalFilename());
            }
            return uploadFile(file.getInputStream(), fileName);
        } catch (Exception e) {
            throw new RuntimeException("上传文件到MinIO失败", e);
        }
    }
    
    @Override
    public String uploadFile(MultipartFile file, String fileName, String path) {
        try {
            if (!StringUtils.hasText(fileName)) {
                fileName = generateFileName(file.getOriginalFilename());
            }
            if (StringUtils.hasText(path)) {
                if (!path.endsWith("/")) {
                    path += "/";
                }
                fileName = path + fileName;
            }
            return uploadFile(file.getInputStream(), fileName);
        } catch (Exception e) {
            throw new RuntimeException("上传文件到MinIO失败", e);
        }
    }
    
    @Override
    public String uploadFile(File file) {
        try {
            String fileName = generateFileName(file.getName());
            minioClient.uploadObject(UploadObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(fileName)
                .filename(file.getAbsolutePath())
                .build());
            
            return getFileUrl(fileName, null);
        } catch (Exception e) {
            throw new RuntimeException("上传文件到MinIO失败", e);
        }
    }
    
    @Override
    public String uploadFile(File file, String fileName) {
        try {
            if (!StringUtils.hasText(fileName)) {
                fileName = generateFileName(file.getName());
            }
            
            minioClient.uploadObject(UploadObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(fileName)
                .filename(file.getAbsolutePath())
                .build());
            
            return getFileUrl(fileName, null);
        } catch (Exception e) {
            throw new RuntimeException("上传文件到MinIO失败", e);
        }
    }
    
    @Override
    public String uploadFile(InputStream inputStream, String fileName) {
        try {
            if (!StringUtils.hasText(fileName)) {
                fileName = generateFileName("file");
            }
            
            minioClient.putObject(PutObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(fileName)
                .stream(inputStream, inputStream.available(), -1)
                .build());
            
            return getFileUrl(fileName, null);
        } catch (Exception e) {
            throw new RuntimeException("上传文件到MinIO失败", e);
        }
    }
    
    @Override
    public byte[] downloadFile(String fileUrl) {
        try {
            String objectName = getObjectNameFromUrl(fileUrl);
            
            GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(objectName)
                .build());
            
            return IOUtils.toByteArray(response);
        } catch (Exception e) {
            throw new RuntimeException("从MinIO下载文件失败", e);
        }
    }
    
    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            String objectName = getObjectNameFromUrl(fileUrl);
            
            minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(objectName)
                .build());
            
            return true;
        } catch (Exception e) {
            throw new RuntimeException("从MinIO删除文件失败", e);
        }
    }
    
    @Override
    public String getFileUrl(String fileUrl, Long expireTime) {
        try {
            String objectName;
            if (fileUrl.startsWith("http")) {
                objectName = getObjectNameFromUrl(fileUrl);
            } else {
                objectName = fileUrl;
            }
            
            // 如果没有设置过期时间或过期时间小于等于0，返回默认URL
            if (expireTime == null || expireTime <= 0) {
                expireTime = 7L * 24 * 60 * 60; // 默认7天
            }
            
            String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(objectName)
                .method(Method.GET)
                .expiry(expireTime.intValue(), TimeUnit.SECONDS)
                .build());
            
            return url;
        } catch (Exception e) {
            throw new RuntimeException("获取MinIO文件链接失败", e);
        }
    }
    
    /**
     * 生成文件名
     * @param originalFilename 原始文件名
     * @return 生成的文件名
     */
    private String generateFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String extension = "";
        if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return uuid + extension;
    }
    
    /**
     * 从URL中获取对象名称
     * @param fileUrl 文件URL
     * @return 对象名称
     */
    private String getObjectNameFromUrl(String fileUrl) {
        if (!fileUrl.startsWith("http")) {
            return fileUrl;
        }
        
        // 解析URL获取对象名称
        try {
            String url = fileUrl;
            // 去除查询参数
            if (url.contains("?")) {
                url = url.substring(0, url.indexOf("?"));
            }
            
            // 获取路径部分
            String endpoint = minioProperties.getEndpoint();
            if (url.contains(endpoint)) {
                String path = url.substring(url.indexOf(endpoint) + endpoint.length());
                // 移除开头的斜杠和存储桶名称
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }
                if (path.startsWith(minioProperties.getBucketName() + "/")) {
                    path = path.substring(minioProperties.getBucketName().length() + 1);
                }
                return path;
            }
            
            // 如果无法解析，则将整个URL作为对象名称
            return fileUrl;
        } catch (Exception e) {
            return fileUrl;
        }
    }
} 