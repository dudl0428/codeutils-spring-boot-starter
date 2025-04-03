package com.codeutils.storage.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import com.codeutils.storage.config.StorageProperties;
import com.codeutils.storage.service.StorageService;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

/**
 * 阿里云OSS存储服务实现
 */
public class OssStorageServiceImpl implements StorageService {
    
    private final StorageProperties.OssProperties ossProperties;
    private final OSS ossClient;
    
    public OssStorageServiceImpl(StorageProperties.OssProperties ossProperties) {
        this.ossProperties = ossProperties;
        this.ossClient = new OSSClientBuilder().build(
            ossProperties.getEndpoint(),
            ossProperties.getAccessKey(),
            ossProperties.getSecretKey()
        );
    }
    
    @Override
    public String uploadFile(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileName = generateFileName(originalFilename);
            return uploadFile(file.getInputStream(), fileName);
        } catch (Exception e) {
            throw new RuntimeException("上传文件到OSS失败", e);
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
            throw new RuntimeException("上传文件到OSS失败", e);
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
            throw new RuntimeException("上传文件到OSS失败", e);
        }
    }
    
    @Override
    public String uploadFile(File file) {
        try {
            String fileName = generateFileName(file.getName());
            PutObjectRequest putObjectRequest = new PutObjectRequest(ossProperties.getBucketName(), fileName, file);
            ossClient.putObject(putObjectRequest);
            return getFileUrl(fileName, null);
        } catch (Exception e) {
            throw new RuntimeException("上传文件到OSS失败", e);
        }
    }
    
    @Override
    public String uploadFile(File file, String fileName) {
        try {
            if (!StringUtils.hasText(fileName)) {
                fileName = generateFileName(file.getName());
            }
            PutObjectRequest putObjectRequest = new PutObjectRequest(ossProperties.getBucketName(), fileName, file);
            ossClient.putObject(putObjectRequest);
            return getFileUrl(fileName, null);
        } catch (Exception e) {
            throw new RuntimeException("上传文件到OSS失败", e);
        }
    }
    
    @Override
    public String uploadFile(InputStream inputStream, String fileName) {
        try {
            if (!StringUtils.hasText(fileName)) {
                fileName = generateFileName("file");
            }
            ossClient.putObject(ossProperties.getBucketName(), fileName, inputStream);
            return getFileUrl(fileName, null);
        } catch (Exception e) {
            throw new RuntimeException("上传文件到OSS失败", e);
        }
    }
    
    @Override
    public byte[] downloadFile(String fileUrl) {
        try {
            String objectName = getObjectNameFromUrl(fileUrl);
            OSSObject ossObject = ossClient.getObject(ossProperties.getBucketName(), objectName);
            return IOUtils.toByteArray(ossObject.getObjectContent());
        } catch (Exception e) {
            throw new RuntimeException("从OSS下载文件失败", e);
        }
    }
    
    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            String objectName = getObjectNameFromUrl(fileUrl);
            ossClient.deleteObject(ossProperties.getBucketName(), objectName);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("从OSS删除文件失败", e);
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
            
            if (expireTime == null || expireTime <= 0) {
                return ossProperties.getEndpoint() + "/" + objectName;
            }
            
            Date expiration = new Date(System.currentTimeMillis() + expireTime * 1000);
            URL url = ossClient.generatePresignedUrl(ossProperties.getBucketName(), objectName, expiration);
            return url.toString();
        } catch (Exception e) {
            throw new RuntimeException("获取OSS文件链接失败", e);
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
        if (fileUrl.startsWith(ossProperties.getEndpoint())) {
            return fileUrl.substring(ossProperties.getEndpoint().length() + 1);
        }
        
        // 尝试从URL中解析对象名称
        try {
            URL url = new URL(fileUrl);
            String path = url.getPath();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            return path;
        } catch (Exception e) {
            return fileUrl; // 如果解析失败，将整个URL作为对象名
        }
    }
} 