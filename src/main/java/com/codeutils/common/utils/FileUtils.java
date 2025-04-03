package com.codeutils.common.utils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 文件工具类
 */
public class FileUtils {

    private static final int BUFFER_SIZE = 4096;
    
    /**
     * 创建目录
     * 
     * @param dirPath 目录路径
     * @return 是否创建成功
     */
    public static boolean createDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (dir.exists()) {
            return dir.isDirectory();
        }
        return dir.mkdirs();
    }
    
    /**
     * 创建文件
     * 
     * @param filePath 文件路径
     * @return 是否创建成功
     * @throws IOException IO异常
     */
    public static boolean createFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            return file.isFile();
        }
        
        File parentFile = file.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            parentFile.mkdirs();
        }
        
        return file.createNewFile();
    }
    
    /**
     * 删除文件
     * 
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return true;
        }
        
        if (file.isFile()) {
            return file.delete();
        }
        
        return false;
    }
    
    /**
     * 递归删除目录及其内容
     * 
     * @param dirPath 目录路径
     * @return 是否删除成功
     */
    public static boolean deleteDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            return true;
        }
        
        if (!dir.isDirectory()) {
            return false;
        }
        
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file.getAbsolutePath());
                } else {
                    file.delete();
                }
            }
        }
        
        return dir.delete();
    }
    
    /**
     * 复制文件
     * 
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @throws IOException IO异常
     */
    public static void copyFile(String sourcePath, String targetPath) throws IOException {
        File sourceFile = new File(sourcePath);
        File targetFile = new File(targetPath);
        
        if (!sourceFile.exists()) {
            throw new FileNotFoundException("源文件不存在：" + sourcePath);
        }
        
        if (!sourceFile.isFile()) {
            throw new IOException("源路径不是文件：" + sourcePath);
        }
        
        File targetParentFile = targetFile.getParentFile();
        if (targetParentFile != null && !targetParentFile.exists()) {
            targetParentFile.mkdirs();
        }
        
        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(targetFile);
             FileChannel inputChannel = fis.getChannel();
             FileChannel outputChannel = fos.getChannel()) {
            
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }
    }
    
    /**
     * 复制目录
     * 
     * @param sourceDirPath 源目录路径
     * @param targetDirPath 目标目录路径
     * @throws IOException IO异常
     */
    public static void copyDirectory(String sourceDirPath, String targetDirPath) throws IOException {
        File sourceDir = new File(sourceDirPath);
        if (!sourceDir.exists()) {
            throw new FileNotFoundException("源目录不存在：" + sourceDirPath);
        }
        
        if (!sourceDir.isDirectory()) {
            throw new IOException("源路径不是目录：" + sourceDirPath);
        }
        
        File targetDir = new File(targetDirPath);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        
        File[] files = sourceDir.listFiles();
        if (files != null) {
            for (File file : files) {
                String targetFilePath = targetDirPath + File.separator + file.getName();
                
                if (file.isDirectory()) {
                    copyDirectory(file.getAbsolutePath(), targetFilePath);
                } else {
                    copyFile(file.getAbsolutePath(), targetFilePath);
                }
            }
        }
    }
    
    /**
     * 移动文件
     * 
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @throws IOException IO异常
     */
    public static void moveFile(String sourcePath, String targetPath) throws IOException {
        copyFile(sourcePath, targetPath);
        deleteFile(sourcePath);
    }
    
    /**
     * 移动目录
     * 
     * @param sourceDirPath 源目录路径
     * @param targetDirPath 目标目录路径
     * @throws IOException IO异常
     */
    public static void moveDirectory(String sourceDirPath, String targetDirPath) throws IOException {
        copyDirectory(sourceDirPath, targetDirPath);
        deleteDirectory(sourceDirPath);
    }
    
    /**
     * 获取文件扩展名
     * 
     * @param fileName 文件名
     * @return 扩展名
     */
    public static String getFileExtension(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return "";
        }
        
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return "";
        }
        
        return fileName.substring(dotIndex + 1);
    }
    
    /**
     * 获取文件名（不含扩展名）
     * 
     * @param fileName 文件名
     * @return 文件名（不含扩展名）
     */
    public static String getFileNameWithoutExtension(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return "";
        }
        
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            return fileName;
        }
        
        return fileName.substring(0, dotIndex);
    }
    
    /**
     * 读取文件内容为字符串
     * 
     * @param filePath 文件路径
     * @return 文件内容
     * @throws IOException IO异常
     */
    public static String readFileToString(String filePath) throws IOException {
        return readFileToString(filePath, StandardCharsets.UTF_8);
    }
    
    /**
     * 读取文件内容为字符串
     * 
     * @param filePath 文件路径
     * @param charset 字符集
     * @return 文件内容
     * @throws IOException IO异常
     */
    public static String readFileToString(String filePath, Charset charset) throws IOException {
        Path path = Paths.get(filePath);
        return new String(Files.readAllBytes(path), charset);
    }
    
    /**
     * 读取文件内容为字节数组
     * 
     * @param filePath 文件路径
     * @return 字节数组
     * @throws IOException IO异常
     */
    public static byte[] readFileToByteArray(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }
    
    /**
     * 按行读取文件内容
     * 
     * @param filePath 文件路径
     * @return 行列表
     * @throws IOException IO异常
     */
    public static List<String> readLines(String filePath) throws IOException {
        return readLines(filePath, StandardCharsets.UTF_8);
    }
    
    /**
     * 按行读取文件内容
     * 
     * @param filePath 文件路径
     * @param charset 字符集
     * @return 行列表
     * @throws IOException IO异常
     */
    public static List<String> readLines(String filePath, Charset charset) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllLines(path, charset);
    }
    
    /**
     * 写字符串内容到文件
     * 
     * @param filePath 文件路径
     * @param content 内容
     * @param append 是否追加
     * @throws IOException IO异常
     */
    public static void writeStringToFile(String filePath, String content, boolean append) throws IOException {
        writeStringToFile(filePath, content, StandardCharsets.UTF_8, append);
    }
    
    /**
     * 写字符串内容到文件
     * 
     * @param filePath 文件路径
     * @param content 内容
     * @param charset 字符集
     * @param append 是否追加
     * @throws IOException IO异常
     */
    public static void writeStringToFile(String filePath, String content, Charset charset, boolean append) throws IOException {
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        
        if (parentFile != null && !parentFile.exists()) {
            parentFile.mkdirs();
        }
        
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), charset))) {
            writer.write(content);
            writer.flush();
        }
    }
    
    /**
     * 写字节数组到文件
     * 
     * @param filePath 文件路径
     * @param bytes 字节数组
     * @param append 是否追加
     * @throws IOException IO异常
     */
    public static void writeByteArrayToFile(String filePath, byte[] bytes, boolean append) throws IOException {
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        
        if (parentFile != null && !parentFile.exists()) {
            parentFile.mkdirs();
        }
        
        try (FileOutputStream fos = new FileOutputStream(file, append)) {
            fos.write(bytes);
            fos.flush();
        }
    }
    
    /**
     * 压缩文件或目录
     * 
     * @param sourcePath 源文件或目录路径
     * @param zipFilePath 目标ZIP文件路径
     * @throws IOException IO异常
     */
    public static void zip(String sourcePath, String zipFilePath) throws IOException {
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            throw new FileNotFoundException("源文件或目录不存在：" + sourcePath);
        }
        
        File zipFile = new File(zipFilePath);
        File parentFile = zipFile.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            parentFile.mkdirs();
        }
        
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            if (sourceFile.isDirectory()) {
                zipDirectory(sourceFile, sourceFile.getName(), zos);
            } else {
                zipFile(sourceFile, sourceFile.getName(), zos);
            }
        }
    }
    
    /**
     * 压缩目录到ZIP输出流
     * 
     * @param dir 目录
     * @param baseName 基础名称
     * @param zos ZIP输出流
     * @throws IOException IO异常
     */
    private static void zipDirectory(File dir, String baseName, ZipOutputStream zos) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        
        for (File file : files) {
            String entryName = baseName + "/" + file.getName();
            
            if (file.isDirectory()) {
                zipDirectory(file, entryName, zos);
            } else {
                zipFile(file, entryName, zos);
            }
        }
    }
    
    /**
     * 压缩文件到ZIP输出流
     * 
     * @param file 文件
     * @param entryName 条目名称
     * @param zos ZIP输出流
     * @throws IOException IO异常
     */
    private static void zipFile(File file, String entryName, ZipOutputStream zos) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry entry = new ZipEntry(entryName);
            zos.putNextEntry(entry);
            
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            
            while ((bytesRead = fis.read(buffer)) != -1) {
                zos.write(buffer, 0, bytesRead);
            }
            
            zos.closeEntry();
        }
    }
    
    /**
     * 解压ZIP文件
     * 
     * @param zipFilePath ZIP文件路径
     * @param targetDirPath 目标目录路径
     * @throws IOException IO异常
     */
    public static void unzip(String zipFilePath, String targetDirPath) throws IOException {
        File zipFile = new File(zipFilePath);
        if (!zipFile.exists()) {
            throw new FileNotFoundException("ZIP文件不存在：" + zipFilePath);
        }
        
        File targetDir = new File(targetDirPath);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            
            while ((entry = zis.getNextEntry()) != null) {
                String filePath = targetDirPath + File.separator + entry.getName();
                File entryFile = new File(filePath);
                
                if (entry.isDirectory()) {
                    entryFile.mkdirs();
                } else {
                    File parentFile = entryFile.getParentFile();
                    if (parentFile != null && !parentFile.exists()) {
                        parentFile.mkdirs();
                    }
                    
                    try (FileOutputStream fos = new FileOutputStream(entryFile)) {
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int bytesRead;
                        
                        while ((bytesRead = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                }
                
                zis.closeEntry();
            }
        }
    }
    
    /**
     * 获取文件大小
     * 
     * @param filePath 文件路径
     * @return 文件大小（字节）
     */
    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return 0;
        }
        
        return file.length();
    }
    
    /**
     * 列出目录下的所有文件
     * 
     * @param dirPath 目录路径
     * @param recursive 是否递归子目录
     * @return 文件列表
     */
    public static List<File> listFiles(String dirPath, boolean recursive) {
        List<File> fileList = new ArrayList<>();
        File dir = new File(dirPath);
        
        if (!dir.exists() || !dir.isDirectory()) {
            return fileList;
        }
        
        File[] files = dir.listFiles();
        if (files == null) {
            return fileList;
        }
        
        for (File file : files) {
            if (file.isFile()) {
                fileList.add(file);
            } else if (recursive && file.isDirectory()) {
                fileList.addAll(listFiles(file.getAbsolutePath(), true));
            }
        }
        
        return fileList;
    }
    
    /**
     * 检查路径是否为目录
     * 
     * @param path 路径
     * @return 是否为目录
     */
    public static boolean isDirectory(String path) {
        File file = new File(path);
        return file.exists() && file.isDirectory();
    }
    
    /**
     * 检查路径是否为文件
     * 
     * @param path 路径
     * @return 是否为文件
     */
    public static boolean isFile(String path) {
        File file = new File(path);
        return file.exists() && file.isFile();
    }
}