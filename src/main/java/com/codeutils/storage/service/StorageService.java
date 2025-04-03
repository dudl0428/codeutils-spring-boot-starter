package com.codeutils.storage.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

/**
 * 存储服务接口
 */
public interface StorageService {
    
    /**
     * 上传文件
     * @param file 文件对象
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file);
    
    /**
     * 上传文件
     * @param file 文件对象
     * @param fileName 指定文件名
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String fileName);
    
    /**
     * 上传文件
     * @param file 文件对象
     * @param fileName 指定文件名
     * @param path 存储路径
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String fileName, String path);
    
    /**
     * 上传本地文件
     * @param file 文件对象
     * @return 文件访问URL
     */
    String uploadFile(File file);
    
    /**
     * 上传本地文件
     * @param file 文件对象
     * @param fileName 指定文件名
     * @return 文件访问URL
     */
    String uploadFile(File file, String fileName);
    
    /**
     * 上传文件流
     * @param inputStream 输入流
     * @param fileName 文件名
     * @return 文件访问URL
     */
    String uploadFile(InputStream inputStream, String fileName);
    
    /**
     * 下载文件
     * @param fileUrl 文件URL
     * @return 文件字节数组
     */
    byte[] downloadFile(String fileUrl);
    
    /**
     * 删除文件
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    boolean deleteFile(String fileUrl);
    
    /**
     * 获取文件外链
     * @param fileUrl 文件URL
     * @param expireTime 过期时间（秒）
     * @return 临时外链
     */
    String getFileUrl(String fileUrl, Long expireTime);
} 