package com.codeutils.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 数据校验工具类
 */
public class ValidationUtils {

    /**
     * 邮箱正则表达式
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");

    /**
     * 手机号正则表达式（中国大陆）
     */
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 身份证号正则表达式（18位）
     */
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^[1-9]\\d{5}(19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$");

    /**
     * URL正则表达式
     */
    private static final Pattern URL_PATTERN = Pattern.compile("^(https?|ftp)://([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\\.)*[a-zA-Z0-9-]+\\.(com|edu|gov|int|mil|net|org|biz|info|name|museum|coop|aero|[a-zA-Z][a-zA-Z])(:[0-9]+)*)((\\/($|[a-zA-Z0-9.,?'\\\\+&%$#=~_-]+))*)?$");

    /**
     * IP地址正则表达式（IPv4）
     */
    private static final Pattern IPV4_PATTERN = Pattern.compile("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

    /**
     * 邮政编码正则表达式（中国大陆）
     */
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^[1-9]\\d{5}$");

    /**
     * 日期正则表达式（yyyy-MM-dd格式）
     */
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$");

    /**
     * 时间正则表达式（HH:mm:ss格式）
     */
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$");

    /**
     * 日期时间正则表达式（yyyy-MM-dd HH:mm:ss格式）
     */
    private static final Pattern DATETIME_PATTERN = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01]) ([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$");

    /**
     * 字母数字正则表达式
     */
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");

    /**
     * 中文正则表达式
     */
    private static final Pattern CHINESE_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5]+$");

    /**
     * 密码强度正则表达式（至少包含大小写字母和数字，长度至少为8）
     */
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$");

    /**
     * 验证对象是否为空
     *
     * @param obj 待验证对象
     * @return 是否为空
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof String) {
            return ((String) obj).isEmpty();
        }

        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }

        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        }

        if (obj.getClass().isArray()) {
            return java.lang.reflect.Array.getLength(obj) == 0;
        }

        return false;
    }

    /**
     * 验证对象是否不为空
     *
     * @param obj 待验证对象
     * @return 是否不为空
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 验证字符串是否为空白
     *
     * @param str 待验证字符串
     * @return 是否为空白
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 验证字符串是否不为空白
     *
     * @param str 待验证字符串
     * @return 是否不为空白
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 验证是否为有效的邮箱地址
     *
     * @param email 待验证的邮箱地址
     * @return 是否为有效的邮箱地址
     */
    public static boolean isEmail(String email) {
        if (isBlank(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 验证是否为有效的手机号（中国大陆）
     *
     * @param mobile 待验证的手机号
     * @return 是否为有效的手机号
     */
    public static boolean isMobile(String mobile) {
        if (isBlank(mobile)) {
            return false;
        }
        return MOBILE_PATTERN.matcher(mobile).matches();
    }

    /**
     * 验证是否为有效的身份证号（18位）
     *
     * @param idCard 待验证的身份证号
     * @return 是否为有效的身份证号
     */
    public static boolean isIdCard(String idCard) {
        if (isBlank(idCard)) {
            return false;
        }
        
        // 基本格式校验
        if (!ID_CARD_PATTERN.matcher(idCard).matches()) {
            return false;
        }
        
        // 校验省份代码
        String provinceCode = idCard.substring(0, 2);
        if (!isValidProvinceCode(provinceCode)) {
            return false;
        }
        
        // 校验出生日期
        String birthDateStr = idCard.substring(6, 14);
        if (!isValidBirthDate(birthDateStr)) {
            return false;
        }
        
        // 校验校验码
        return isValidCheckCode(idCard);
    }

    /**
     * 验证省份代码是否有效
     *
     * @param provinceCode 省份代码
     * @return 是否有效
     */
    private static boolean isValidProvinceCode(String provinceCode) {
        String[] validProvinceCodes = {
            "11", "12", "13", "14", "15", "21", "22", "23", "31", "32", "33", "34", "35", "36", 
            "37", "41", "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", 
            "63", "64", "65", "71", "81", "82", "91"
        };
        
        for (String code : validProvinceCodes) {
            if (code.equals(provinceCode)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 验证出生日期是否有效
     *
     * @param birthDateStr 出生日期字符串（yyyyMMdd格式）
     * @return 是否有效
     */
    private static boolean isValidBirthDate(String birthDateStr) {
        int year = Integer.parseInt(birthDateStr.substring(0, 4));
        int month = Integer.parseInt(birthDateStr.substring(4, 6));
        int day = Integer.parseInt(birthDateStr.substring(6, 8));
        
        // 年份范围检查
        if (year < 1900 || year > java.time.Year.now().getValue()) {
            return false;
        }
        
        // 月份检查
        if (month < 1 || month > 12) {
            return false;
        }
        
        // 日期检查
        boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
        int[] daysInMonth = {0, 31, isLeapYear ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        
        return day >= 1 && day <= daysInMonth[month];
    }

    /**
     * 验证身份证校验码是否有效
     *
     * @param idCard 身份证号
     * @return 是否有效
     */
    private static boolean isValidCheckCode(String idCard) {
        char[] charArray = idCard.toCharArray();
        int[] weightArray = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] validCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (charArray[i] - '0') * weightArray[i];
        }
        
        int mod = sum % 11;
        char validCode = validCodes[mod];
        
        char lastChar = Character.toUpperCase(charArray[17]);
        
        return lastChar == validCode;
    }

    /**
     * 验证是否为有效的URL
     *
     * @param url 待验证的URL
     * @return 是否为有效的URL
     */
    public static boolean isUrl(String url) {
        if (isBlank(url)) {
            return false;
        }
        return URL_PATTERN.matcher(url).matches();
    }

    /**
     * 验证是否为有效的IPv4地址
     *
     * @param ipv4 待验证的IPv4地址
     * @return 是否为有效的IPv4地址
     */
    public static boolean isIpv4(String ipv4) {
        if (isBlank(ipv4)) {
            return false;
        }
        return IPV4_PATTERN.matcher(ipv4).matches();
    }

    /**
     * 验证是否为有效的邮政编码（中国大陆）
     *
     * @param postalCode 待验证的邮政编码
     * @return 是否为有效的邮政编码
     */
    public static boolean isPostalCode(String postalCode) {
        if (isBlank(postalCode)) {
            return false;
        }
        return POSTAL_CODE_PATTERN.matcher(postalCode).matches();
    }

    /**
     * 验证是否为有效的日期（yyyy-MM-dd格式）
     *
     * @param date 待验证的日期
     * @return 是否为有效的日期
     */
    public static boolean isDate(String date) {
        if (isBlank(date)) {
            return false;
        }
        return DATE_PATTERN.matcher(date).matches();
    }

    /**
     * 验证是否为有效的时间（HH:mm:ss格式）
     *
     * @param time 待验证的时间
     * @return 是否为有效的时间
     */
    public static boolean isTime(String time) {
        if (isBlank(time)) {
            return false;
        }
        return TIME_PATTERN.matcher(time).matches();
    }

    /**
     * 验证是否为有效的日期时间（yyyy-MM-dd HH:mm:ss格式）
     *
     * @param dateTime 待验证的日期时间
     * @return 是否为有效的日期时间
     */
    public static boolean isDateTime(String dateTime) {
        if (isBlank(dateTime)) {
            return false;
        }
        return DATETIME_PATTERN.matcher(dateTime).matches();
    }

    /**
     * 验证是否为字母数字组合
     *
     * @param str 待验证的字符串
     * @return 是否为字母数字组合
     */
    public static boolean isAlphanumeric(String str) {
        if (isBlank(str)) {
            return false;
        }
        return ALPHANUMERIC_PATTERN.matcher(str).matches();
    }

    /**
     * 验证是否为中文
     *
     * @param str 待验证的字符串
     * @return 是否为中文
     */
    public static boolean isChinese(String str) {
        if (isBlank(str)) {
            return false;
        }
        return CHINESE_PATTERN.matcher(str).matches();
    }

    /**
     * 验证是否为强密码
     * 强密码定义：至少包含大小写字母和数字，长度至少为8
     *
     * @param password 待验证的密码
     * @return 是否为强密码
     */
    public static boolean isStrongPassword(String password) {
        if (isBlank(password)) {
            return false;
        }
        return STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 验证是否在指定范围内
     *
     * @param value 待验证的值
     * @param min 最小值
     * @param max 最大值
     * @return 是否在指定范围内
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * 验证是否在指定范围内
     *
     * @param value 待验证的值
     * @param min 最小值
     * @param max 最大值
     * @return 是否在指定范围内
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    /**
     * 验证字符串长度是否在指定范围内
     *
     * @param str 待验证的字符串
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return 是否在指定范围内
     */
    public static boolean isLengthInRange(String str, int minLength, int maxLength) {
        if (str == null) {
            return minLength <= 0;
        }
        int length = str.length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * 验证对象是否为指定类型
     *
     * @param obj 待验证的对象
     * @param type 类型
     * @return 是否为指定类型
     */
    public static boolean isInstanceOf(Object obj, Class<?> type) {
        return obj != null && type.isInstance(obj);
    }

    /**
     * 验证对象是否不为null且为指定类型
     *
     * @param obj 待验证的对象
     * @param type 类型
     * @return 是否不为null且为指定类型
     */
    public static boolean isNotNullAndInstanceOf(Object obj, Class<?> type) {
        return obj != null && type.isInstance(obj);
    }

    /**
     * 验证字符串是否包含指定子串
     *
     * @param str 待验证的字符串
     * @param subStr 子串
     * @return 是否包含指定子串
     */
    public static boolean contains(String str, String subStr) {
        if (str == null || subStr == null) {
            return false;
        }
        return str.contains(subStr);
    }

    /**
     * 验证字符串是否以指定前缀开始
     *
     * @param str 待验证的字符串
     * @param prefix 前缀
     * @return 是否以指定前缀开始
     */
    public static boolean startsWith(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        return str.startsWith(prefix);
    }

    /**
     * 验证字符串是否以指定后缀结束
     *
     * @param str 待验证的字符串
     * @param suffix 后缀
     * @return 是否以指定后缀结束
     */
    public static boolean endsWith(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        return str.endsWith(suffix);
    }

    /**
     * 验证字符串是否匹配正则表达式
     *
     * @param str 待验证的字符串
     * @param regex 正则表达式
     * @return 是否匹配
     */
    public static boolean matches(String str, String regex) {
        if (str == null || regex == null) {
            return false;
        }
        return str.matches(regex);
    }

    /**
     * 验证字符串是否匹配正则表达式
     *
     * @param str 待验证的字符串
     * @param pattern 正则表达式模式
     * @return 是否匹配
     */
    public static boolean matches(String str, Pattern pattern) {
        if (str == null || pattern == null) {
            return false;
        }
        return pattern.matcher(str).matches();
    }

    /**
     * 验证集合是否包含指定元素
     *
     * @param collection 集合
     * @param element 元素
     * @param <T> 元素类型
     * @return 是否包含
     */
    public static <T> boolean contains(Collection<T> collection, T element) {
        if (collection == null || element == null) {
            return false;
        }
        return collection.contains(element);
    }

    /**
     * 验证所有元素是否都满足条件
     *
     * @param collection 集合
     * @param validator 验证器
     * @param <T> 元素类型
     * @return 是否都满足条件
     */
    public static <T> boolean allMatch(Collection<T> collection, java.util.function.Predicate<T> validator) {
        if (collection == null || validator == null) {
            return false;
        }
        return collection.stream().allMatch(validator);
    }

    /**
     * 验证是否至少有一个元素满足条件
     *
     * @param collection 集合
     * @param validator 验证器
     * @param <T> 元素类型
     * @return 是否至少有一个元素满足条件
     */
    public static <T> boolean anyMatch(Collection<T> collection, java.util.function.Predicate<T> validator) {
        if (collection == null || validator == null) {
            return false;
        }
        return collection.stream().anyMatch(validator);
    }
} 