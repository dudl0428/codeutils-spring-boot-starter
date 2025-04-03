package com.codeutils.common.utils.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES加密解密工具类
 */
public class AESUtils {

    /**
     * 密钥算法
     */
    private static final String KEY_ALGORITHM = "AES";

    /**
     * 默认加密算法
     */
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    
    /**
     * GCM加密算法
     */
    private static final String GCM_CIPHER_ALGORITHM = "AES/GCM/NoPadding";

    /**
     * GCM认证标签长度（单位：位）
     */
    private static final int GCM_TAG_LENGTH = 128;

    /**
     * 生成AES密钥
     *
     * @return Base64编码的AES密钥
     * @throws NoSuchAlgorithmException 算法不存在异常
     */
    public static String generateKey() throws NoSuchAlgorithmException {
        return generateKey(128);
    }

    /**
     * 生成指定长度的AES密钥
     *
     * @param keySize 密钥长度（128/192/256位）
     * @return Base64编码的AES密钥
     * @throws NoSuchAlgorithmException 算法不存在异常
     */
    public static String generateKey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
        keyGenerator.init(keySize);
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * 生成随机IV
     *
     * @return Base64编码的IV
     */
    public static String generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return Base64.getEncoder().encodeToString(iv);
    }

    /**
     * 使用CBC模式加密
     *
     * @param content 待加密内容
     * @param key     Base64编码的AES密钥
     * @param iv      Base64编码的IV
     * @return Base64编码的加密结果
     * @throws Exception 加密异常
     */
    public static String encryptCBC(String content, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), KEY_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 使用CBC模式解密
     *
     * @param encryptedContent Base64编码的加密内容
     * @param key              Base64编码的AES密钥
     * @param iv               Base64编码的IV
     * @return 解密后的内容
     * @throws Exception 解密异常
     */
    public static String decryptCBC(String encryptedContent, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), KEY_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedContent));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 使用GCM模式加密（更安全的模式）
     *
     * @param content 待加密内容
     * @param key     Base64编码的AES密钥
     * @param iv      Base64编码的IV（GCM模式中称为nonce）
     * @return Base64编码的加密结果
     * @throws Exception 加密异常
     */
    public static String encryptGCM(String content, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance(GCM_CIPHER_ALGORITHM);
        SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), KEY_ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, Base64.getDecoder().decode(iv));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
        byte[] encryptedBytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 使用GCM模式解密（更安全的模式）
     *
     * @param encryptedContent Base64编码的加密内容
     * @param key              Base64编码的AES密钥
     * @param iv               Base64编码的IV（GCM模式中称为nonce）
     * @return 解密后的内容
     * @throws Exception 解密异常
     */
    public static String decryptGCM(String encryptedContent, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance(GCM_CIPHER_ALGORITHM);
        SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), KEY_ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, Base64.getDecoder().decode(iv));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedContent));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 使用ECB模式加密（不推荐用于敏感数据）
     *
     * @param content 待加密内容
     * @param key     Base64编码的AES密钥
     * @return Base64编码的加密结果
     * @throws Exception 加密异常
     */
    public static String encryptECB(String content, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 使用ECB模式解密（不推荐用于敏感数据）
     *
     * @param encryptedContent Base64编码的加密内容
     * @param key              Base64编码的AES密钥
     * @return 解密后的内容
     * @throws Exception 解密异常
     */
    public static String decryptECB(String encryptedContent, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedContent));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 使用密码派生密钥进行加密（适合使用密码作为密钥的情况）
     *
     * @param content  待加密内容
     * @param password 密码
     * @param salt     盐值（Base64编码）
     * @return Base64编码的加密结果
     * @throws Exception 加密异常
     */
    public static String encryptWithPassword(String content, String password, String salt) throws Exception {
        // 使用密码和盐派生密钥
        String key = deriveKeyFromPassword(password, salt);
        // 生成随机IV
        String iv = generateIV();
        // 使用CBC模式加密
        String encryptedData = encryptCBC(content, key, iv);
        // 返回IV和加密数据的组合（IV:加密数据），解密时需要拆分
        return iv + ":" + encryptedData;
    }

    /**
     * 使用密码派生密钥进行解密（适合使用密码作为密钥的情况）
     *
     * @param encryptedContent 加密内容（格式为：IV:加密数据）
     * @param password         密码
     * @param salt             盐值（Base64编码）
     * @return 解密后的内容
     * @throws Exception 解密异常
     */
    public static String decryptWithPassword(String encryptedContent, String password, String salt) throws Exception {
        // 拆分IV和加密数据
        String[] parts = encryptedContent.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("加密内容格式不正确");
        }
        String iv = parts[0];
        String data = parts[1];
        
        // 使用密码和盐派生密钥
        String key = deriveKeyFromPassword(password, salt);
        
        // 使用CBC模式解密
        return decryptCBC(data, key, iv);
    }

    /**
     * 从密码派生密钥
     *
     * @param password 密码
     * @param salt     盐值（Base64编码）
     * @return Base64编码的密钥
     * @throws Exception 异常
     */
    private static String deriveKeyFromPassword(String password, String salt) throws Exception {
        // 这里使用简单的密钥派生方法，实际应用中建议使用PBKDF2或其他更复杂的密钥派生函数
        byte[] saltBytes = Base64.getDecoder().decode(salt);
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        
        // 创建结果数组
        byte[] keyBytes = new byte[16]; // AES-128
        
        // 简单混合密码和盐（实际应用中请使用更安全的方法）
        int saltLength = saltBytes.length;
        int passwordLength = passwordBytes.length;
        
        for (int i = 0; i < 16; i++) {
            keyBytes[i] = (byte) (passwordBytes[i % passwordLength] ^ saltBytes[i % saltLength]);
        }
        
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    /**
     * 生成随机盐值
     *
     * @param length 盐值长度
     * @return Base64编码的盐值
     */
    public static String generateSalt(int length) {
        byte[] salt = new byte[length];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
} 