package com.codeutils.common.utils;

import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class StringUtils {
    
    /**
     * 判断字符串是否为空或null
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
    
    /**
     * 判断字符串是否不为空且不为null
     * @param str 字符串
     * @return 是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * 判断字符串是否为空白（空或只包含空白字符）
     * @param str 字符串
     * @return 是否为空白
     */
    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 判断字符串是否不为空白
     * @param str 字符串
     * @return 是否不为空白
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
    
    /**
     * 截取指定长度的字符串，超出部分用省略号代替
     * @param str 字符串
     * @param maxLength 最大长度
     * @return 截取后的字符串
     */
    public static String truncate(String str, int maxLength) {
        if (isEmpty(str) || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }
    
    /**
     * 生成UUID（不含连字符）
     * @return UUID字符串
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    
    /**
     * 生成指定长度的随机数字字符串
     * @param length 长度
     * @return 随机数字字符串
     */
    public static String randomNumeric(int length) {
        if (length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    
    /**
     * 生成指定长度的随机字母字符串
     * @param length 长度
     * @return 随机字母字符串
     */
    public static String randomAlphabetic(int length) {
        if (length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int choice = random.nextInt(26);
            char ch = (char) (choice + 'a');
            sb.append(ch);
        }
        return sb.toString();
    }
    
    /**
     * 生成指定长度的随机字母数字字符串
     * @param length 长度
     * @return 随机字母数字字符串
     */
    public static String randomAlphanumeric(int length) {
        if (length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int choice = random.nextInt(36);
            if (choice < 10) {
                sb.append(choice);
            } else {
                char ch = (char) (choice - 10 + 'a');
                sb.append(ch);
            }
        }
        return sb.toString();
    }
    
    /**
     * 驼峰命名转下划线命名
     * @param camelCase 驼峰命名字符串
     * @return 下划线命名字符串
     */
    public static String camelToUnderscore(String camelCase) {
        if (isEmpty(camelCase)) {
            return camelCase;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char ch = camelCase.charAt(i);
            if (Character.isUpperCase(ch)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(ch));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
    
    /**
     * 下划线命名转驼峰命名
     * @param underscore 下划线命名字符串
     * @param capitalizeFirstLetter 首字母是否大写
     * @return 驼峰命名字符串
     */
    public static String underscoreToCamel(String underscore, boolean capitalizeFirstLetter) {
        if (isEmpty(underscore)) {
            return underscore;
        }
        StringBuilder sb = new StringBuilder();
        boolean nextCharToUpper = capitalizeFirstLetter;
        for (int i = 0; i < underscore.length(); i++) {
            char ch = underscore.charAt(i);
            if (ch == '_') {
                nextCharToUpper = true;
            } else {
                if (nextCharToUpper) {
                    sb.append(Character.toUpperCase(ch));
                    nextCharToUpper = false;
                } else {
                    sb.append(ch);
                }
            }
        }
        return sb.toString();
    }
    
    /**
     * 手机号码脱敏（保留前3位和后4位，中间替换为星号）
     * @param mobile 手机号码
     * @return 脱敏后的手机号码
     */
    public static String maskMobile(String mobile) {
        if (isEmpty(mobile) || mobile.length() < 7) {
            return mobile;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
    }
    
    /**
     * 邮箱脱敏（用户名部分保留前3位，其余替换为星号）
     * @param email 邮箱
     * @return 脱敏后的邮箱
     */
    public static String maskEmail(String email) {
        if (isEmpty(email) || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 3) {
            return email.substring(0, 1) + "****" + email.substring(atIndex);
        } else {
            return email.substring(0, 3) + "****" + email.substring(atIndex);
        }
    }
    
    /**
     * 验证字符串是否为有效的邮箱格式
     * @param email 邮箱字符串
     * @return 是否有效
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    
    /**
     * 验证字符串是否为有效的手机号码格式（简单校验）
     * @param mobile 手机号码字符串
     * @return 是否有效
     */
    public static boolean isValidMobile(String mobile) {
        if (isEmpty(mobile)) {
            return false;
        }
        String mobileRegex = "^1[3-9]\\d{9}$";
        Pattern pattern = Pattern.compile(mobileRegex);
        Matcher matcher = pattern.matcher(mobile);
        return matcher.matches();
    }
} 