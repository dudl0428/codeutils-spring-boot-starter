package com.codeutils.common.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 加密解密工具类
 */
public class EncryptUtils {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_CBC_PADDING = "AES/CBC/PKCS5Padding";
    private static final String DES_ALGORITHM = "DES";
    private static final String DES_CBC_PADDING = "DES/CBC/PKCS5Padding";
    private static final String RSA_ALGORITHM = "RSA";
    private static final String MD5_ALGORITHM = "MD5";
    private static final String SHA1_ALGORITHM = "SHA-1";
    private static final String SHA256_ALGORITHM = "SHA-256";
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    
    /**
     * MD5加密
     * 
     * @param data 明文数据
     * @return 加密后的十六进制字符串
     */
    public static String md5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance(MD5_ALGORITHM);
            byte[] bytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }
    
    /**
     * SHA-1加密
     * 
     * @param data 明文数据
     * @return 加密后的十六进制字符串
     */
    public static String sha1(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance(SHA1_ALGORITHM);
            byte[] bytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1加密失败", e);
        }
    }
    
    /**
     * SHA-256加密
     * 
     * @param data 明文数据
     * @return 加密后的十六进制字符串
     */
    public static String sha256(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance(SHA256_ALGORITHM);
            byte[] bytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256加密失败", e);
        }
    }
    
    /**
     * HMAC-SHA256加密
     * 
     * @param data 明文数据
     * @param key 密钥
     * @return 加密后的十六进制字符串
     */
    public static String hmacSha256(String data, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA256_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(secretKeySpec);
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA256加密失败", e);
        }
    }
    
    /**
     * AES加密
     * 
     * @param data 明文数据
     * @param key 密钥（长度必须是16、24或32字节）
     * @param iv 初始向量（长度必须是16字节）
     * @return 加密后的Base64字符串
     */
    public static String aesEncrypt(String data, String key, String iv) {
        try {
            Cipher cipher = Cipher.getInstance(AES_CBC_PADDING);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("AES加密失败", e);
        }
    }
    
    /**
     * AES解密
     * 
     * @param encryptedData 加密的Base64字符串
     * @param key 密钥（长度必须是16、24或32字节）
     * @param iv 初始向量（长度必须是16字节）
     * @return 解密后的明文
     */
    public static String aesDecrypt(String encryptedData, String key, String iv) {
        try {
            Cipher cipher = Cipher.getInstance(AES_CBC_PADDING);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES解密失败", e);
        }
    }
    
    /**
     * 生成AES密钥
     * 
     * @param keySize 密钥大小（128、192或256位）
     * @return Base64编码的密钥
     */
    public static String generateAesKey(int keySize) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGenerator.init(keySize);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("生成AES密钥失败", e);
        }
    }
    
    /**
     * DES加密
     * 
     * @param data 明文数据
     * @param key 密钥（长度必须是8字节）
     * @param iv 初始向量（长度必须是8字节）
     * @return 加密后的Base64字符串
     */
    public static String desEncrypt(String data, String key, String iv) {
        try {
            Cipher cipher = Cipher.getInstance(DES_CBC_PADDING);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), DES_ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("DES加密失败", e);
        }
    }
    
    /**
     * DES解密
     * 
     * @param encryptedData 加密的Base64字符串
     * @param key 密钥（长度必须是8字节）
     * @param iv 初始向量（长度必须是8字节）
     * @return 解密后的明文
     */
    public static String desDecrypt(String encryptedData, String key, String iv) {
        try {
            Cipher cipher = Cipher.getInstance(DES_CBC_PADDING);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), DES_ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("DES解密失败", e);
        }
    }
    
    /**
     * 生成RSA密钥对
     * 
     * @param keySize 密钥大小（一般为1024或2048位）
     * @return RSA密钥对（第一个元素为私钥，第二个元素为公钥，均为Base64编码）
     */
    public static String[] generateRsaKeyPair(int keySize) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            keyPairGenerator.initialize(keySize);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            
            String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            
            return new String[] { privateKey, publicKey };
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("生成RSA密钥对失败", e);
        }
    }
    
    /**
     * RSA公钥加密
     * 
     * @param data 明文数据
     * @param publicKeyBase64 Base64编码的公钥
     * @return 加密后的Base64字符串
     */
    public static String rsaEncrypt(String data, String publicKeyBase64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("RSA加密失败", e);
        }
    }
    
    /**
     * RSA私钥解密
     * 
     * @param encryptedData 加密的Base64字符串
     * @param privateKeyBase64 Base64编码的私钥
     * @return 解密后的明文
     */
    public static String rsaDecrypt(String encryptedData, String privateKeyBase64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("RSA解密失败", e);
        }
    }
    
    /**
     * RSA签名
     * 
     * @param data 明文数据
     * @param privateKeyBase64 Base64编码的私钥
     * @param algorithm 签名算法（如MD5withRSA、SHA1withRSA、SHA256withRSA）
     * @return 签名的Base64字符串
     */
    public static String rsaSign(String data, String privateKeyBase64, String algorithm) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(privateKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] signBytes = signature.sign();
            return Base64.getEncoder().encodeToString(signBytes);
        } catch (Exception e) {
            throw new RuntimeException("RSA签名失败", e);
        }
    }
    
    /**
     * RSA验签
     * 
     * @param data 明文数据
     * @param signBase64 Base64编码的签名
     * @param publicKeyBase64 Base64编码的公钥
     * @param algorithm 签名算法（如MD5withRSA、SHA1withRSA、SHA256withRSA）
     * @return 验签结果
     */
    public static boolean rsaVerify(String data, String signBase64, String publicKeyBase64, String algorithm) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(publicKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.getDecoder().decode(signBase64));
        } catch (Exception e) {
            throw new RuntimeException("RSA验签失败", e);
        }
    }
    
    /**
     * 字节数组转十六进制字符串
     * 
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                sb.append("0");
            }
            sb.append(hex);
        }
        return sb.toString();
    }
    
    /**
     * 十六进制字符串转字节数组
     * 
     * @param hexString 十六进制字符串
     * @return 字节数组
     */
    private static byte[] hexToBytes(String hexString) {
        if (hexString == null || hexString.isEmpty()) {
            return new byte[0];
        }
        
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }
        
        int length = hexString.length() / 2;
        byte[] bytes = new byte[length];
        
        for (int i = 0; i < length; i++) {
            String hex = hexString.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(hex, 16);
        }
        
        return bytes;
    }
} 