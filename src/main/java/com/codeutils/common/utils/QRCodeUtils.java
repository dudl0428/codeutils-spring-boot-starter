package com.codeutils.common.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码工具类
 */
public class QRCodeUtils {

    /**
     * 生成二维码到文件
     *
     * @param content 二维码内容
     * @param width 宽度
     * @param height 高度
     * @param filePath 文件路径
     * @throws WriterException 生成异常
     * @throws IOException IO异常
     */
    public static void generateQRCodeImage(String content, int width, int height, String filePath) 
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        // 设置纠错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 设置编码方式
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 设置边距
        hints.put(EncodeHintType.MARGIN, 1);
        
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, filePath.substring(filePath.lastIndexOf('.') + 1), path);
    }

    /**
     * 生成二维码到输出流
     *
     * @param content 二维码内容
     * @param width 宽度
     * @param height 高度
     * @param outputStream 输出流
     * @param format 图片格式
     * @throws WriterException 生成异常
     * @throws IOException IO异常
     */
    public static void generateQRCodeImage(String content, int width, int height, OutputStream outputStream, String format) 
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        MatrixToImageWriter.writeToStream(bitMatrix, format, outputStream);
    }

    /**
     * 生成带Logo的二维码
     *
     * @param content 二维码内容
     * @param width 宽度
     * @param height 高度
     * @param logoPath logo路径
     * @param outputPath 输出路径
     * @throws WriterException 生成异常
     * @throws IOException IO异常
     */
    public static void generateQRCodeWithLogo(String content, int width, int height, String logoPath, String outputPath) 
            throws WriterException, IOException {
        // 生成二维码
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        
        // 将二维码转换为BufferedImage
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        
        // 读取Logo图片
        BufferedImage logoImage = ImageIO.read(new File(logoPath));
        
        // 计算Logo大小，不超过二维码的30%
        int logoWidth = qrImage.getWidth() / 5;
        int logoHeight = qrImage.getHeight() / 5;
        
        // 计算放置Logo的位置
        int x = (qrImage.getWidth() - logoWidth) / 2;
        int y = (qrImage.getHeight() - logoHeight) / 2;
        
        // 创建Graphics2D对象
        Graphics2D g2 = qrImage.createGraphics();
        
        // 绘制Logo
        g2.drawImage(logoImage, x, y, logoWidth, logoHeight, null);
        g2.dispose();
        
        // 输出带Logo的二维码
        ImageIO.write(qrImage, outputPath.substring(outputPath.lastIndexOf('.') + 1), new File(outputPath));
    }

    /**
     * 生成带背景色的二维码
     *
     * @param content 二维码内容
     * @param width 宽度
     * @param height 高度
     * @param foreColor 前景色
     * @param backColor 背景色
     * @param outputPath 输出路径
     * @throws WriterException 生成异常
     * @throws IOException IO异常
     */
    public static void generateColorfulQRCode(String content, int width, int height, 
            Color foreColor, Color backColor, String outputPath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        
        // 创建BufferedImage
        BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        // 设置颜色
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                qrImage.setRGB(x, y, bitMatrix.get(x, y) ? foreColor.getRGB() : backColor.getRGB());
            }
        }
        
        // 输出彩色二维码
        ImageIO.write(qrImage, outputPath.substring(outputPath.lastIndexOf('.') + 1), new File(outputPath));
    }

    /**
     * 解析二维码
     *
     * @param filePath 二维码图片路径
     * @return 二维码内容
     * @throws IOException IO异常
     * @throws NotFoundException 未找到异常
     */
    public static String decodeQRCode(String filePath) throws IOException, NotFoundException {
        BufferedImage bufferedImage = ImageIO.read(new File(filePath));
        return decodeQRCode(bufferedImage);
    }

    /**
     * 解析二维码
     *
     * @param bufferedImage 二维码图片
     * @return 二维码内容
     * @throws NotFoundException 未找到异常
     */
    public static String decodeQRCode(BufferedImage bufferedImage) throws NotFoundException {
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
        
        Result result = new MultiFormatReader().decode(bitmap, hints);
        return result.getText();
    }

    /**
     * 生成带文字的二维码
     *
     * @param content 二维码内容
     * @param width 宽度
     * @param height 高度
     * @param text 文字内容
     * @param outputPath 输出路径
     * @throws WriterException 生成异常
     * @throws IOException IO异常
     */
    public static void generateQRCodeWithText(String content, int width, int height, 
            String text, String outputPath) throws WriterException, IOException {
        // 留出底部空间给文字
        int qrCodeHeight = height - 30;
        
        // 生成二维码
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, qrCodeHeight, hints);
        
        // 创建完整的图片
        BufferedImage combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = combinedImage.createGraphics();
        
        // 填充背景色
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        
        // 绘制二维码
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < qrCodeHeight; y++) {
                if (x < bitMatrix.getWidth() && y < bitMatrix.getHeight() && bitMatrix.get(x, y)) {
                    combinedImage.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
        
        // 绘制文字
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("宋体", Font.PLAIN, 14));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g2.drawString(text, (width - textWidth) / 2, height - 10);
        
        g2.dispose();
        
        // 输出带文字的二维码
        ImageIO.write(combinedImage, outputPath.substring(outputPath.lastIndexOf('.') + 1), new File(outputPath));
    }

    /**
     * 生成批量二维码
     *
     * @param contentList 内容列表
     * @param width 宽度
     * @param height 高度
     * @param outputDir 输出目录
     * @param fileNamePrefix 文件名前缀
     * @throws WriterException 生成异常
     * @throws IOException IO异常
     */
    public static void generateBatchQRCode(String[] contentList, int width, int height, 
            String outputDir, String fileNamePrefix) throws WriterException, IOException {
        // 确保输出目录存在
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        
        for (int i = 0; i < contentList.length; i++) {
            String content = contentList[i];
            String filePath = outputDir + File.separator + fileNamePrefix + "_" + (i + 1) + ".png";
            
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            Path path = FileSystems.getDefault().getPath(filePath);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        }
    }

    /**
     * 生成条形码
     *
     * @param content 条形码内容
     * @param width 宽度
     * @param height 高度
     * @param outputPath 输出路径
     * @throws WriterException 生成异常
     * @throws IOException IO异常
     */
    public static void generateBarcode(String content, int width, int height, String outputPath) 
            throws WriterException, IOException {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        
        BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.CODE_128, width, height, hints);
        Path path = FileSystems.getDefault().getPath(outputPath);
        MatrixToImageWriter.writeToPath(bitMatrix, outputPath.substring(outputPath.lastIndexOf('.') + 1), path);
    }

    /**
     * 解析条形码
     *
     * @param filePath 条形码图片路径
     * @return 条形码内容
     * @throws IOException IO异常
     * @throws NotFoundException 未找到异常
     */
    public static String decodeBarcode(String filePath) throws IOException, NotFoundException {
        BufferedImage bufferedImage = ImageIO.read(new File(filePath));
        
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
        
        Result result = new MultiFormatReader().decode(bitmap, hints);
        return result.getText();
    }
} 