package com.codeutils.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtils {
    
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_TIME = "HH:mm:ss";
    public static final String PATTERN_MONTH = "yyyy-MM";
    public static final String PATTERN_YEAR = "yyyy";
    public static final String PATTERN_DATETIME_COMPACT = "yyyyMMddHHmmss";
    public static final String PATTERN_DATE_COMPACT = "yyyyMMdd";
    
    /**
     * 日期格式化
     * @param date 日期
     * @param pattern 格式
     * @return 格式化后的日期字符串
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
    
    /**
     * 日期格式化（默认yyyy-MM-dd HH:mm:ss格式）
     * @param date 日期
     * @return 格式化后的日期字符串
     */
    public static String formatDateTime(Date date) {
        return format(date, PATTERN_DATETIME);
    }
    
    /**
     * 日期格式化（默认yyyy-MM-dd格式）
     * @param date 日期
     * @return 格式化后的日期字符串
     */
    public static String formatDate(Date date) {
        return format(date, PATTERN_DATE);
    }
    
    /**
     * 字符串解析为日期
     * @param dateStr 日期字符串
     * @param pattern 格式
     * @return 日期对象
     */
    public static Date parse(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("日期解析异常", e);
        }
    }
    
    /**
     * 字符串解析为日期（默认yyyy-MM-dd HH:mm:ss格式）
     * @param dateStr 日期字符串
     * @return 日期对象
     */
    public static Date parseDateTime(String dateStr) {
        return parse(dateStr, PATTERN_DATETIME);
    }
    
    /**
     * 字符串解析为日期（默认yyyy-MM-dd格式）
     * @param dateStr 日期字符串
     * @return 日期对象
     */
    public static Date parseDate(String dateStr) {
        return parse(dateStr, PATTERN_DATE);
    }
    
    /**
     * 获取当前日期时间
     * @return 当前日期时间
     */
    public static Date now() {
        return new Date();
    }
    
    /**
     * 获取当前日期时间字符串（默认yyyy-MM-dd HH:mm:ss格式）
     * @return 当前日期时间字符串
     */
    public static String nowStr() {
        return formatDateTime(now());
    }
    
    /**
     * 获取当前日期字符串（默认yyyy-MM-dd格式）
     * @return 当前日期字符串
     */
    public static String todayStr() {
        return formatDate(now());
    }
    
    /**
     * Date转LocalDateTime
     * @param date Date对象
     * @return LocalDateTime对象
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime())
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    }
    
    /**
     * Date转LocalDate
     * @param date Date对象
     * @return LocalDate对象
     */
    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime())
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
    }
    
    /**
     * LocalDateTime转Date
     * @param localDateTime LocalDateTime对象
     * @return Date对象
     */
    public static Date fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * LocalDate转Date
     * @param localDate LocalDate对象
     * @return Date对象
     */
    public static Date fromLocalDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * 日期加减天数
     * @param date 日期
     * @param days 天数（正数为加，负数为减）
     * @return 计算后的日期
     */
    public static Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = toLocalDateTime(date);
        return fromLocalDateTime(localDateTime.plusDays(days));
    }
    
    /**
     * 日期加减月数
     * @param date 日期
     * @param months 月数（正数为加，负数为减）
     * @return 计算后的日期
     */
    public static Date addMonths(Date date, int months) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = toLocalDateTime(date);
        return fromLocalDateTime(localDateTime.plusMonths(months));
    }
    
    /**
     * 日期加减年数
     * @param date 日期
     * @param years 年数（正数为加，负数为减）
     * @return 计算后的日期
     */
    public static Date addYears(Date date, int years) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = toLocalDateTime(date);
        return fromLocalDateTime(localDateTime.plusYears(years));
    }
    
    /**
     * 日期加减小时数
     * @param date 日期
     * @param hours 小时数（正数为加，负数为减）
     * @return 计算后的日期
     */
    public static Date addHours(Date date, int hours) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = toLocalDateTime(date);
        return fromLocalDateTime(localDateTime.plusHours(hours));
    }
    
    /**
     * 日期加减分钟数
     * @param date 日期
     * @param minutes 分钟数（正数为加，负数为减）
     * @return 计算后的日期
     */
    public static Date addMinutes(Date date, int minutes) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = toLocalDateTime(date);
        return fromLocalDateTime(localDateTime.plusMinutes(minutes));
    }
    
    /**
     * 获取两个日期之间相差的天数
     * @param date1 日期1
     * @param date2 日期2
     * @return 相差的天数
     */
    public static long daysBetween(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return 0;
        }
        LocalDate localDate1 = toLocalDate(date1);
        LocalDate localDate2 = toLocalDate(date2);
        return Math.abs(localDate1.toEpochDay() - localDate2.toEpochDay());
    }
} 