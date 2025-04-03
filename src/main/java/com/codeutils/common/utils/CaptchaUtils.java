package com.codeutils.common.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

/**
 * 验证码工具类，支持多种类型的验证码生成
 */
public class CaptchaUtils {
    
    private static final String NUMBERS = "0123456789";
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LETTERS_NUMBERS = LETTERS + NUMBERS;
    private static final Random RANDOM = new SecureRandom();
    
    /**
     * 验证码类型枚举
     */
    public enum CaptchaType {
        /**
         * 纯数字
         */
        NUMBERS,
        
        /**
         * 纯字母
         */
        LETTERS,
        
        /**
         * 字母和数字混合
         */
        MIXED,
        
        /**
         * 算术运算(加减乘)
         */
        ARITHMETIC,
        
        /**
         * 中文汉字
         */
        CHINESE
    }
    
    /**
     * 验证码图片类型枚举
     */
    public enum ImageType {
        /**
         * JPEG格式
         */
        JPEG("jpeg"),
        
        /**
         * PNG格式
         */
        PNG("png"),
        
        /**
         * GIF格式
         */
        GIF("gif");
        
        private final String value;
        
        ImageType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    /**
     * 验证码结果类，包含验证码文本和图片
     */
    public static class CaptchaResult {
        private final String text;
        private final BufferedImage image;
        
        public CaptchaResult(String text, BufferedImage image) {
            this.text = text;
            this.image = image;
        }
        
        /**
         * 获取验证码文本
         *
         * @return 验证码文本
         */
        public String getText() {
            return text;
        }
        
        /**
         * 获取验证码图片
         *
         * @return 验证码图片
         */
        public BufferedImage getImage() {
            return image;
        }
        
        /**
         * 将验证码图片写入到输出流
         *
         * @param os 输出流
         * @param imageType 图片类型
         * @throws IOException IO异常
         */
        public void write(OutputStream os, ImageType imageType) throws IOException {
            ImageIO.write(image, imageType.getValue(), os);
        }
        
        /**
         * 将验证码图片保存到文件
         *
         * @param filePath 文件路径
         * @param imageType 图片类型
         * @throws IOException IO异常
         */
        public void toFile(String filePath, ImageType imageType) throws IOException {
            File outputFile = new File(filePath);
            // 确保目录存在
            Files.createDirectories(Paths.get(outputFile.getParent()));
            ImageIO.write(image, imageType.getValue(), outputFile);
        }
        
        /**
         * 将验证码图片转为Base64编码字符串
         *
         * @param imageType 图片类型
         * @return Base64编码字符串
         * @throws IOException IO异常
         */
        public String toBase64(ImageType imageType) throws IOException {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, imageType.getValue(), os);
            return "data:image/" + imageType.getValue() + ";base64," + 
                   Base64.getEncoder().encodeToString(os.toByteArray());
        }
    }
    
    /**
     * 生成随机验证码
     *
     * @param length 长度
     * @param type 验证码类型
     * @return 验证码文本
     */
    public static String generateText(int length, CaptchaType type) {
        if (length <= 0) {
            throw new IllegalArgumentException("验证码长度必须大于0");
        }
        
        StringBuilder sb = new StringBuilder();
        String characters;
        
        switch (type) {
            case NUMBERS:
                characters = NUMBERS;
                for (int i = 0; i < length; i++) {
                    sb.append(characters.charAt(RANDOM.nextInt(characters.length())));
                }
                break;
            case LETTERS:
                characters = LETTERS;
                for (int i = 0; i < length; i++) {
                    sb.append(characters.charAt(RANDOM.nextInt(characters.length())));
                }
                break;
            case MIXED:
                characters = LETTERS_NUMBERS;
                for (int i = 0; i < length; i++) {
                    sb.append(characters.charAt(RANDOM.nextInt(characters.length())));
                }
                break;
            case ARITHMETIC:
                // 生成算术运算表达式
                return generateArithmeticExpression();
            case CHINESE:
                // 生成中文验证码
                return generateChineseCaptcha(length);
            default:
                throw new IllegalArgumentException("不支持的验证码类型");
        }
        
        return sb.toString();
    }
    
    /**
     * 生成算术运算表达式
     *
     * @return 算术表达式和结果，格式为"表达式=结果"
     */
    private static String generateArithmeticExpression() {
        int num1 = RANDOM.nextInt(10) + 1;
        int num2 = RANDOM.nextInt(10) + 1;
        int operatorIndex = RANDOM.nextInt(3);
        String operator;
        int result;
        
        switch (operatorIndex) {
            case 0:  // 加法
                operator = "+";
                result = num1 + num2;
                break;
            case 1:  // 减法
                if (num1 < num2) {  // 确保结果为正数
                    int temp = num1;
                    num1 = num2;
                    num2 = temp;
                }
                operator = "-";
                result = num1 - num2;
                break;
            case 2:  // 乘法
                operator = "×";
                result = num1 * num2;
                break;
            default:
                operator = "+";
                result = num1 + num2;
        }
        
        return num1 + operator + num2 + "=" + result;
    }
    
    /**
     * 生成中文验证码
     *
     * @param length 长度
     * @return 中文验证码
     */
    private static String generateChineseCaptcha(int length) {
        StringBuilder sb = new StringBuilder();
        
        // 常用汉字的Unicode编码范围
        int start = 0x4E00;  // '\u4e00'
        int end = 0x9FA5;    // '\u9fa5'
        
        for (int i = 0; i < length; i++) {
            char chinese = (char) (RANDOM.nextInt(end - start + 1) + start);
            sb.append(chinese);
        }
        
        return sb.toString();
    }
    
    /**
     * 生成验证码图片
     *
     * @param text 验证码文本
     * @param width 图片宽度
     * @param height 图片高度
     * @return 验证码图片
     */
    public static BufferedImage generateImage(String text, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 填充背景颜色
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        
        // 绘制干扰线
        drawInterferenceLines(g2d, width, height);
        
        // 绘制干扰点
        drawInterferencePoints(g2d, width, height);
        
        // 绘制验证码
        drawText(g2d, text, width, height);
        
        g2d.dispose();
        return image;
    }
    
    /**
     * 绘制干扰线
     *
     * @param g2d 图形上下文
     * @param width 图片宽度
     * @param height 图片高度
     */
    private static void drawInterferenceLines(Graphics2D g2d, int width, int height) {
        int lineCount = 5 + RANDOM.nextInt(5);  // 5-10条干扰线
        
        for (int i = 0; i < lineCount; i++) {
            g2d.setColor(getRandomColor(150, 250));
            int x1 = RANDOM.nextInt(width);
            int y1 = RANDOM.nextInt(height);
            int x2 = RANDOM.nextInt(width);
            int y2 = RANDOM.nextInt(height);
            g2d.setStroke(new BasicStroke(0.5f + RANDOM.nextFloat()));
            g2d.drawLine(x1, y1, x2, y2);
        }
    }
    
    /**
     * 绘制干扰点
     *
     * @param g2d 图形上下文
     * @param width 图片宽度
     * @param height 图片高度
     */
    private static void drawInterferencePoints(Graphics2D g2d, int width, int height) {
        int pointCount = 50 + RANDOM.nextInt(50);  // 50-100个干扰点
        
        for (int i = 0; i < pointCount; i++) {
            g2d.setColor(getRandomColor(120, 240));
            int x = RANDOM.nextInt(width);
            int y = RANDOM.nextInt(height);
            g2d.drawOval(x, y, 1, 1);
        }
    }
    
    /**
     * 绘制验证码文本
     *
     * @param g2d 图形上下文
     * @param text 验证码文本
     * @param width 图片宽度
     * @param height 图片高度
     */
    private static void drawText(Graphics2D g2d, String text, int width, int height) {
        int fontSize = height - 8;
        Font font = new Font("Arial", Font.BOLD, fontSize);
        g2d.setFont(font);
        
        // 计算文字总宽度
        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        
        // 计算每个字符的位置
        int charWidth = textWidth / text.length();
        int startX = (width - textWidth) / 2;
        
        // 绘制每个字符
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            
            // 随机颜色
            g2d.setColor(getRandomColor(20, 130));
            
            // 随机旋转角度
            double rotation = -0.2 + RANDOM.nextDouble() * 0.4;  // -0.2到+0.2弧度
            AffineTransform originalTransform = g2d.getTransform();
            
            // 计算字符位置
            int x = startX + i * charWidth;
            int y = height / 2 + metrics.getHeight() / 4;
            
            // 对字符进行旋转
            g2d.rotate(rotation, x, y);
            g2d.drawString(String.valueOf(c), x, y);
            
            // 恢复变换
            g2d.setTransform(originalTransform);
        }
    }
    
    /**
     * 获取随机颜色
     *
     * @param min 颜色范围最小值
     * @param max 颜色范围最大值
     * @return 随机颜色
     */
    private static Color getRandomColor(int min, int max) {
        int r = min + RANDOM.nextInt(max - min);
        int g = min + RANDOM.nextInt(max - min);
        int b = min + RANDOM.nextInt(max - min);
        return new Color(r, g, b);
    }
    
    /**
     * 生成验证码结果
     *
     * @param length 验证码长度
     * @param type 验证码类型
     * @param width 图片宽度
     * @param height 图片高度
     * @return 验证码结果
     */
    public static CaptchaResult generate(int length, CaptchaType type, int width, int height) {
        String text = generateText(length, type);
        BufferedImage image = generateImage(text, width, height);
        return new CaptchaResult(text, image);
    }
    
    /**
     * 生成默认验证码结果（4位数字字母混合，宽120，高40）
     *
     * @return 验证码结果
     */
    public static CaptchaResult generateDefault() {
        return generate(4, CaptchaType.MIXED, 120, 40);
    }
    
    /**
     * 生成数字验证码
     *
     * @param length 验证码长度
     * @param width 图片宽度
     * @param height 图片高度
     * @return 验证码结果
     */
    public static CaptchaResult generateNumbers(int length, int width, int height) {
        return generate(length, CaptchaType.NUMBERS, width, height);
    }
    
    /**
     * 生成字母验证码
     *
     * @param length 验证码长度
     * @param width 图片宽度
     * @param height 图片高度
     * @return 验证码结果
     */
    public static CaptchaResult generateLetters(int length, int width, int height) {
        return generate(length, CaptchaType.LETTERS, width, height);
    }
    
    /**
     * 生成混合验证码
     *
     * @param length 验证码长度
     * @param width 图片宽度
     * @param height 图片高度
     * @return 验证码结果
     */
    public static CaptchaResult generateMixed(int length, int width, int height) {
        return generate(length, CaptchaType.MIXED, width, height);
    }
    
    /**
     * 生成算术验证码
     *
     * @param width 图片宽度
     * @param height 图片高度
     * @return 验证码结果
     */
    public static CaptchaResult generateArithmetic(int width, int height) {
        return generate(0, CaptchaType.ARITHMETIC, width, height);
    }
    
    /**
     * 生成中文验证码
     *
     * @param length 验证码长度
     * @param width 图片宽度
     * @param height 图片高度
     * @return 验证码结果
     */
    public static CaptchaResult generateChinese(int length, int width, int height) {
        return generate(length, CaptchaType.CHINESE, width, height);
    }
    
    /**
     * 验证码验证（忽略大小写）
     *
     * @param inputText 用户输入的验证码
     * @param captchaText 原始验证码
     * @return 是否匹配
     */
    public static boolean verify(String inputText, String captchaText) {
        if (inputText == null || captchaText == null) {
            return false;
        }
        
        // 对于算术验证码，需要特殊处理
        if (captchaText.contains("=")) {
            String correctAnswer = captchaText.substring(captchaText.indexOf("=") + 1);
            return inputText.trim().equals(correctAnswer.trim());
        }
        
        return inputText.trim().equalsIgnoreCase(captchaText.trim());
    }
} 