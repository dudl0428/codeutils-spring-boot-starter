package com.codeutils.common.utils.security;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA加密解密工具类
 */
public class RSAUtils {

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 密钥算法
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * 公钥
     */
    public static final String PUBLIC_KEY = "RSAPublicKey";

    /**
     * 私钥
     */
    public static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * RSA密钥长度，默认1024位，
     * 密钥长度必须是64的倍数，在512到65536位之间
     */
    private static final int KEY_SIZE = 1024;

    /**
     * 生成密钥对
     *
     * @return 密钥对Map
     * @throws Exception 异常
     */
    public static Map<String, String> generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        
        // 获取公钥
        PublicKey publicKey = keyPair.getPublic();
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        
        // 获取私钥
        PrivateKey privateKey = keyPair.getPrivate();
        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        
        Map<String, String> keyMap = new HashMap<>(2);
        keyMap.put(PUBLIC_KEY, publicKeyString);
        keyMap.put(PRIVATE_KEY, privateKeyString);
        
        return keyMap;
    }

    /**
     * 获取公钥
     *
     * @param keyMap 密钥对Map
     * @return 公钥
     */
    public static String getPublicKey(Map<String, String> keyMap) {
        return keyMap.get(PUBLIC_KEY);
    }

    /**
     * 获取私钥
     *
     * @param keyMap 密钥对Map
     * @return 私钥
     */
    public static String getPrivateKey(Map<String, String> keyMap) {
        return keyMap.get(PRIVATE_KEY);
    }

    /**
     * 根据公钥字符串获取公钥对象
     *
     * @param publicKeyString 公钥字符串（Base64编码）
     * @return 公钥对象
     * @throws Exception 异常
     */
    public static PublicKey getPublicKey(String publicKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 根据私钥字符串获取私钥对象
     *
     * @param privateKeyString 私钥字符串（Base64编码）
     * @return 私钥对象
     * @throws Exception 异常
     */
    public static PrivateKey getPrivateKey(String privateKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 使用公钥加密
     *
     * @param content 待加密内容
     * @param publicKeyString 公钥（Base64编码）
     * @return 加密结果（Base64编码）
     * @throws Exception 异常
     */
    public static String encryptByPublicKey(String content, String publicKeyString) throws Exception {
        PublicKey publicKey = getPublicKey(publicKeyString);
        return encryptByPublicKey(content, publicKey);
    }

    /**
     * 使用公钥加密
     *
     * @param content 待加密内容
     * @param publicKey 公钥
     * @return 加密结果（Base64编码）
     * @throws Exception 异常
     */
    public static String encryptByPublicKey(String content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        
        byte[] data = content.getBytes(StandardCharsets.UTF_8);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        
        byte[] encryptedData = out.toByteArray();
        out.close();
        
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    /**
     * 使用私钥解密
     *
     * @param encryptedContent 已加密内容（Base64编码）
     * @param privateKeyString 私钥（Base64编码）
     * @return 解密结果
     * @throws Exception 异常
     */
    public static String decryptByPrivateKey(String encryptedContent, String privateKeyString) throws Exception {
        PrivateKey privateKey = getPrivateKey(privateKeyString);
        return decryptByPrivateKey(encryptedContent, privateKey);
    }

    /**
     * 使用私钥解密
     *
     * @param encryptedContent 已加密内容（Base64编码）
     * @param privateKey 私钥
     * @return 解密结果
     * @throws Exception 异常
     */
    public static String decryptByPrivateKey(String encryptedContent, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        
        byte[] encryptedData = Base64.getDecoder().decode(encryptedContent);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        
        byte[] decryptedData = out.toByteArray();
        out.close();
        
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    /**
     * 使用私钥加密
     *
     * @param content 待加密内容
     * @param privateKeyString 私钥（Base64编码）
     * @return 加密结果（Base64编码）
     * @throws Exception 异常
     */
    public static String encryptByPrivateKey(String content, String privateKeyString) throws Exception {
        PrivateKey privateKey = getPrivateKey(privateKeyString);
        return encryptByPrivateKey(content, privateKey);
    }

    /**
     * 使用私钥加密
     *
     * @param content 待加密内容
     * @param privateKey 私钥
     * @return 加密结果（Base64编码）
     * @throws Exception 异常
     */
    public static String encryptByPrivateKey(String content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        
        byte[] data = content.getBytes(StandardCharsets.UTF_8);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        
        byte[] encryptedData = out.toByteArray();
        out.close();
        
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    /**
     * 使用公钥解密
     *
     * @param encryptedContent 已加密内容（Base64编码）
     * @param publicKeyString 公钥（Base64编码）
     * @return 解密结果
     * @throws Exception 异常
     */
    public static String decryptByPublicKey(String encryptedContent, String publicKeyString) throws Exception {
        PublicKey publicKey = getPublicKey(publicKeyString);
        return decryptByPublicKey(encryptedContent, publicKey);
    }

    /**
     * 使用公钥解密
     *
     * @param encryptedContent 已加密内容（Base64编码）
     * @param publicKey 公钥
     * @return 解密结果
     * @throws Exception 异常
     */
    public static String decryptByPublicKey(String encryptedContent, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        
        byte[] encryptedData = Base64.getDecoder().decode(encryptedContent);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        
        byte[] decryptedData = out.toByteArray();
        out.close();
        
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    /**
     * 使用私钥对内容进行签名
     *
     * @param content 待签名内容
     * @param privateKeyString 私钥（Base64编码）
     * @return 签名结果（Base64编码）
     * @throws Exception 异常
     */
    public static String sign(String content, String privateKeyString) throws Exception {
        PrivateKey privateKey = getPrivateKey(privateKeyString);
        return sign(content, privateKey);
    }

    /**
     * 使用私钥对内容进行签名
     *
     * @param content 待签名内容
     * @param privateKey 私钥
     * @return 签名结果（Base64编码）
     * @throws Exception 异常
     */
    public static String sign(String content, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(content.getBytes(StandardCharsets.UTF_8));
        
        byte[] signed = signature.sign();
        return Base64.getEncoder().encodeToString(signed);
    }

    /**
     * 校验签名
     *
     * @param content 原始内容
     * @param sign 签名（Base64编码）
     * @param publicKeyString 公钥（Base64编码）
     * @return 校验结果
     * @throws Exception 异常
     */
    public static boolean verify(String content, String sign, String publicKeyString) throws Exception {
        PublicKey publicKey = getPublicKey(publicKeyString);
        return verify(content, sign, publicKey);
    }

    /**
     * 校验签名
     *
     * @param content 原始内容
     * @param sign 签名（Base64编码）
     * @param publicKey 公钥
     * @return 校验结果
     * @throws Exception 异常
     */
    public static boolean verify(String content, String sign, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(content.getBytes(StandardCharsets.UTF_8));
        
        return signature.verify(Base64.getDecoder().decode(sign));
    }
} 