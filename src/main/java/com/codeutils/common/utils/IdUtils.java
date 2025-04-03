package com.codeutils.common.utils;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ID工具类，提供各种ID生成策略
 */
public class IdUtils {

    private static final String ALPHA_NUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String NUMERIC = "0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final AtomicInteger SEQUENCE = new AtomicInteger(1000);
    
    private static final long EPOCH = 1546300800000L; // 2019-01-01 00:00:00
    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;
    
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
    
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);
    
    private static long workerId;
    private static long datacenterId;
    private static long sequence = 0L;
    private static long lastTimestamp = -1L;
    
    static {
        initSnowflakeIds();
    }
    
    /**
     * 初始化雪花算法的工作机器ID和数据中心ID
     */
    private static void initSnowflakeIds() {
        workerId = getWorkerId();
        datacenterId = getDatacenterId();
        
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            workerId = ThreadLocalRandom.current().nextLong(MAX_WORKER_ID + 1);
        }
        
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            datacenterId = ThreadLocalRandom.current().nextLong(MAX_DATACENTER_ID + 1);
        }
    }
    
    /**
     * 获取工作机器ID
     * 
     * @return 工作机器ID
     */
    private static long getWorkerId() {
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    for (byte b : mac) {
                        sb.append(String.format("%02X", b));
                    }
                }
            }
            return (sb.toString().hashCode() & 0xffff) % (MAX_WORKER_ID + 1);
        } catch (Exception e) {
            return ThreadLocalRandom.current().nextLong(MAX_WORKER_ID + 1);
        }
    }
    
    /**
     * 获取数据中心ID
     * 
     * @return 数据中心ID
     */
    private static long getDatacenterId() {
        try {
            String hostname = java.net.InetAddress.getLocalHost().getHostName();
            return (hostname.hashCode() & 0xffff) % (MAX_DATACENTER_ID + 1);
        } catch (Exception e) {
            return ThreadLocalRandom.current().nextLong(MAX_DATACENTER_ID + 1);
        }
    }
    
    /**
     * 生成UUID（不含连字符）
     * 
     * @return UUID字符串
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    
    /**
     * 生成UUID（含连字符）
     * 
     * @return UUID字符串
     */
    public static String uuidWithHyphens() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 生成短UUID（16位）
     * 
     * @return 短UUID字符串
     */
    public static String shortUuid() {
        UUID uuid = UUID.randomUUID();
        long most = uuid.getMostSignificantBits();
        long least = uuid.getLeastSignificantBits();
        return toString(most, least);
    }
    
    /**
     * 将两个long值转换为可读的字符串
     * 
     * @param most 高位值
     * @param least 低位值
     * @return 字符串
     */
    private static String toString(long most, long least) {
        char[] buf = new char[16];
        int charPos = 16;
        int radix = 1 << 4;
        long mask = radix - 1;
        
        do {
            buf[--charPos] = ALPHA_NUMERIC.charAt((int) (least & mask));
            least >>>= 4;
        } while (least != 0);
        
        long val = most;
        do {
            buf[--charPos] = ALPHA_NUMERIC.charAt((int) (val & mask));
            val >>>= 4;
        } while (val != 0);
        
        return new String(buf, charPos, 16 - charPos);
    }
    
    /**
     * 基于时间戳的ID（17位数字）
     * 格式：年月日时分秒毫秒
     * 
     * @return 基于时间戳的ID
     */
    public static String timeId() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }
    
    /**
     * 带序列号的时间戳ID（21位数字）
     * 格式：年月日时分秒毫秒 + 4位自增序列
     * 
     * @return 带序列号的时间戳ID
     */
    public static String timeWithSeqId() {
        int seq = SEQUENCE.incrementAndGet();
        if (seq > 9999) {
            SEQUENCE.set(1000);
            seq = 1000;
        }
        return timeId() + seq;
    }
    
    /**
     * 生成指定长度的随机数字ID
     * 
     * @param length ID长度
     * @return 随机数字ID
     */
    public static String randomNumeric(int length) {
        if (length <= 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(NUMERIC.charAt(SECURE_RANDOM.nextInt(NUMERIC.length())));
        }
        
        return sb.toString();
    }
    
    /**
     * 生成指定长度的随机字母数字ID
     * 
     * @param length ID长度
     * @return 随机字母数字ID
     */
    public static String randomAlphanumeric(int length) {
        if (length <= 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHA_NUMERIC.charAt(SECURE_RANDOM.nextInt(ALPHA_NUMERIC.length())));
        }
        
        return sb.toString();
    }
    
    /**
     * 生成雪花算法ID（Twitter's Snowflake）
     * 
     * @return 雪花算法ID（18位数字）
     */
    public static synchronized long snowflakeId() {
        long timestamp = System.currentTimeMillis();
        
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("时钟回拨异常");
        }
        
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        
        lastTimestamp = timestamp;
        
        return ((timestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }
    
    /**
     * 生成雪花算法ID的字符串表示
     * 
     * @return 雪花算法ID字符串
     */
    public static String snowflakeIdStr() {
        return String.valueOf(snowflakeId());
    }
    
    /**
     * 等待下一毫秒
     * 
     * @param lastTimestamp 上次生成ID的时间戳
     * @return 当前时间戳
     */
    private static long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
    
    /**
     * 生成订单号
     * 格式：年月日时分秒 + 6位随机数
     * 
     * @return 订单号
     */
    public static String generateOrderNo() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + randomNumeric(6);
    }
    
    /**
     * 生成交易流水号
     * 格式：年月日 + 12位随机数
     * 
     * @return 交易流水号
     */
    public static String generateTradeNo() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + randomNumeric(12);
    }
    
    /**
     * 生成支付单号
     * 格式：P + 年月日时分秒 + 6位随机数
     * 
     * @return 支付单号
     */
    public static String generatePaymentNo() {
        return "P" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + randomNumeric(6);
    }
    
    /**
     * 生成退款单号
     * 格式：R + 年月日时分秒 + 6位随机数
     * 
     * @return 退款单号
     */
    public static String generateRefundNo() {
        return "R" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + randomNumeric(6);
    }
} 