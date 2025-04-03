package com.codeutils.storage.service.impl;

import com.codeutils.storage.config.StorageProperties;
import com.codeutils.storage.service.StorageService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
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
 * 腾讯云COS存储服务实现
 */
public class CosStorageServiceImpl implements StorageService {
    
    private final StorageProperties.CosProperties cosProperties;
    private final COSClient cosClient;
    
    public CosStorageServiceImpl(StorageProperties.CosProperties cosProperties) {
        this.cosProperties = cosProperties;
        
        // 初始化COS客户端
        COSCredentials credentials = new BasicCOSCredentials(
            cosProperties.getSecretId(), 
            cosProperties.getSecretKey()
        );
        
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setRegion(new Region(cosProperties.getRegion()));
        clientConfig.setHttpProtocol(HttpProtocol.https);
        
        this.cosClient = new COSClient(credentials, clientConfig);
    }
    
    @Override
    public String uploadFile(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileName = generateFileName(originalFilename);
            return uploadFile(file.getInputStream(), fileName);
        } catch (Exception e) {
            throw new RuntimeException("上传文件到COS失败", e);
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
            throw new RuntimeException("上传文件到COS失败", e);
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
            throw new RuntimeException("上传文件到COS失败", e);
        }
    }
    
    @Override
    public String uploadFile(File file) {
        try {
            String fileName = generateFileName(file.getName());
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                cosProperties.getBucketName(), fileName, file
            );
            cosClient.putObject(putObjectRequest);
            return getFileUrl(fileName, null);
        } catch (Exception e) {
            throw new RuntimeException("上传文件到COS失败", e);
        }
    }
    
    @Override
    public String uploadFile(File file, String fileName) {
        try {
            if (!StringUtils.hasText(fileName)) {
                fileName = generateFileName(file.getName());
            }
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                cosProperties.getBucketName(), fileName, file
            );
            cosClient.putObject(putObjectRequest);
            return getFileUrl(fileName, null);
        } catch (Exception e) {
            throw new RuntimeException("上传文件到COS失败", e);
        }
    }
    
    @Override
    public String uploadFile(InputStream inputStream, String fileName) {
        try {
            if (!StringUtils.hasText(fileName)) {
                fileName = generateFileName("file");
            }
            
            ObjectMetadata objectMetadata = new ObjectMetadata();
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                cosProperties.getBucketName(), 
                fileName, 
                inputStream,
                objectMetadata
            );
            
            cosClient.putObject(putObjectRequest);
            return getFileUrl(fileName, null);
        } catch (Exception e) {
            throw new RuntimeException("上传文件到COS失败", e);
        }
    }
    
    @Override
    public byte[] downloadFile(String fileUrl) {
        try {
            String objectKey = getObjectKeyFromUrl(fileUrl);
            GetObjectRequest getObjectRequest = new GetObjectRequest(
                cosProperties.getBucketName(), objectKey
            );
            COSObject cosObject = cosClient.getObject(getObjectRequest);
            return IOUtils.toByteArray(cosObject.getObjectContent());
        } catch (Exception e) {
            throw new RuntimeException("从COS下载文件失败", e);
        }
    }
    
    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            String objectKey = getObjectKeyFromUrl(fileUrl);
            cosClient.deleteObject(cosProperties.getBucketName(), objectKey);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("从COS删除文件失败", e);
        }
    }
    
    @Override
    public String getFileUrl(String fileUrl, Long expireTime) {
        try {
            String objectKey;
            if (fileUrl.startsWith("http")) {
                objectKey = getObjectKeyFromUrl(fileUrl);
            } else {
                objectKey = fileUrl;
            }
            
            if (expireTime == null || expireTime <= 0) {
                return getDefaultUrl(objectKey);
            }
            
            Date expirationDate = new Date(System.currentTimeMillis() + expireTime * 1000);
            URL url = cosClient.generatePresignedUrl(
                cosProperties.getBucketName(), 
                objectKey, 
                expirationDate
            );
            
            return url.toString();
        } catch (Exception e) {
            throw new RuntimeException("获取COS文件链接失败", e);
        }
    }
    
    /**
     * 获取默认的访问URL
     * @param objectKey 对象键
     * @return 默认URL
     */
    private String getDefaultUrl(String objectKey) {
        return String.format("https://%s.cos.%s.myqcloud.com/%s",
            cosProperties.getBucketName(),
            cosProperties.getRegion(),
            objectKey);
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
     * 从URL中获取对象键
     * @param fileUrl 文件URL
     * @return 对象键
     */
    private String getObjectKeyFromUrl(String fileUrl) {
        if (!fileUrl.startsWith("http")) {
            return fileUrl;
        }
        
        try {
            URL url = new URL(fileUrl);
            String path = url.getPath();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            return path;
        } catch (Exception e) {
            return fileUrl;
        }
    }
} 