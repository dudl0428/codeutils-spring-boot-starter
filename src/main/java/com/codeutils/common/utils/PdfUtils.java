package com.codeutils.common.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * PDF工具类，基于iText实现
 */
public class PdfUtils {

    /**
     * 创建新的PDF文档
     *
     * @param filePath 文件路径
     * @return 文档对象
     * @throws DocumentException 文档异常
     * @throws IOException IO异常
     */
    public static Document createPdf(String filePath) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();
        return document;
    }

    /**
     * 关闭PDF文档
     *
     * @param document 文档对象
     */
    public static void closePdf(Document document) {
        if (document != null && document.isOpen()) {
            document.close();
        }
    }

    /**
     * 添加文本内容
     *
     * @param document 文档对象
     * @param text 文本内容
     * @throws DocumentException 文档异常
     */
    public static void addText(Document document, String text) throws DocumentException {
        document.add(new Paragraph(text));
    }

    /**
     * 添加带样式的文本内容
     *
     * @param document 文档对象
     * @param text 文本内容
     * @param fontFamily 字体名称
     * @param fontSize 字体大小
     * @param isBold 是否加粗
     * @param isItalic 是否斜体
     * @param alignment 对齐方式
     * @throws DocumentException 文档异常
     */
    public static void addStyledText(Document document, String text, String fontFamily,
                                     float fontSize, boolean isBold, boolean isItalic, int alignment) throws DocumentException {
        Font font = getFont(fontFamily, fontSize, isBold, isItalic);
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(alignment);
        document.add(paragraph);
    }

    /**
     * 添加标题
     *
     * @param document 文档对象
     * @param title 标题内容
     * @param level 标题级别（1-7）
     * @throws DocumentException 文档异常
     */
    public static void addTitle(Document document, String title, int level) throws DocumentException {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

        // 根据级别设置字体大小
        switch (level) {
            case 1:
                font.setSize(24);
                break;
            case 2:
                font.setSize(18);
                break;
            case 3:
                font.setSize(16);
                break;
            case 4:
                font.setSize(14);
                break;
            default:
                font.setSize(12);
                break;
        }

        Paragraph paragraph = new Paragraph(title, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingBefore(10);
        paragraph.setSpacingAfter(10);
        document.add(paragraph);
    }

    /**
     * 获取字体
     *
     * @param fontFamily 字体名称
     * @param fontSize 字体大小
     * @param isBold 是否加粗
     * @param isItalic 是否斜体
     * @return 字体对象
     */
    public static Font getFont(String fontFamily, float fontSize, boolean isBold, boolean isItalic) {
        int style = Font.NORMAL;
        if (isBold && isItalic) {
            style = Font.BOLDITALIC;
        } else if (isBold) {
            style = Font.BOLD;
        } else if (isItalic) {
            style = Font.ITALIC;
        }

        if (fontFamily == null || fontFamily.isEmpty()) {
            fontFamily = FontFactory.HELVETICA;
        }

        Font font = FontFactory.getFont(fontFamily, fontSize, style);
        return font;
    }

    /**
     * 添加空行
     *
     * @param document 文档对象
     * @param count 空行数量
     * @throws DocumentException 文档异常
     */
    public static void addEmptyLines(Document document, int count) throws DocumentException {
        for (int i = 0; i < count; i++) {
            document.add(Chunk.NEWLINE);
        }
    }

    /**
     * 添加分页符
     *
     * @param document 文档对象
     * @throws DocumentException 文档异常
     */
    public static void addPageBreak(Document document) throws DocumentException {
        document.newPage();
    }

    /**
     * 添加图片
     *
     * @param document 文档对象
     * @param imagePath 图片路径
     * @param width 宽度
     * @param height 高度
     * @param alignment 对齐方式
     * @throws DocumentException 文档异常
     * @throws IOException IO异常
     */
    public static void addImage(Document document, String imagePath, float width, float height, int alignment)
            throws DocumentException, IOException {
        Image image = Image.getInstance(imagePath);

        if (width > 0 && height > 0) {
            image.scaleAbsolute(width, height);
        }

        image.setAlignment(alignment);
        document.add(image);
    }

    /**
     * 添加表格
     *
     * @param document 文档对象
     * @param headers 表头
     * @param data 数据
     * @throws DocumentException 文档异常
     */
    public static void addTable(Document document, String[] headers, List<String[]> data) throws DocumentException {
        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);

        // 添加表头
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        }

        // 添加数据行
        for (String[] row : data) {
            for (String cellData : row) {
                PdfPCell cell = new PdfPCell(new Phrase(cellData));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPadding(5);
                table.addCell(cell);
            }
        }

        document.add(table);
    }


    /**
     * 为PDF添加水印
     *
     * @param inputFile 输入文件
     * @param outputFile 输出文件
     * @param watermarkText 水印文本
     * @throws IOException IO异常
     * @throws DocumentException 文档异常
     */
    public static void addWatermark(String inputFile, String outputFile, String watermarkText)
            throws IOException, DocumentException {
        PdfReader reader = new PdfReader(inputFile);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputFile));

        // 设置水印字体
        Font font = new Font(Font.FontFamily.HELVETICA, 60, Font.BOLD, new BaseColor(200, 200, 200, 60));

        // 获取页数
        int pageCount = reader.getNumberOfPages();

        // 为每一页添加水印
        for (int i = 1; i <= pageCount; i++) {
            PdfContentByte content = stamper.getUnderContent(i);
            Rectangle pageSize = reader.getPageSize(i);

            // 旋转角度和位置
            content.beginText();
            content.setFontAndSize(font.getBaseFont(), font.getSize());
            content.setColorFill(font.getColor());
            content.showTextAligned(Element.ALIGN_CENTER, watermarkText,
                    pageSize.getWidth() / 2, pageSize.getHeight() / 2, 45);
            content.endText();
        }

        stamper.close();
        reader.close();
    }

    /**
     * 为PDF添加页码
     *
     * @param inputFile 输入文件
     * @param outputFile 输出文件
     * @throws IOException IO异常
     * @throws DocumentException 文档异常
     */
    public static void addPageNumbers(String inputFile, String outputFile)
            throws IOException, DocumentException {
        PdfReader reader = new PdfReader(inputFile);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputFile));

        // 获取页数
        int pageCount = reader.getNumberOfPages();

        // 设置字体
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

        // 为每一页添加页码
        for (int i = 1; i <= pageCount; i++) {
            PdfContentByte content = stamper.getOverContent(i);
            Rectangle pageSize = reader.getPageSize(i);

            content.beginText();
            content.setFontAndSize(bf, 10);
            content.showTextAligned(Element.ALIGN_CENTER, "第 " + i + " 页 / 共 " + pageCount + " 页",
                    pageSize.getWidth() / 2, 20, 0);
            content.endText();
        }

        stamper.close();
        reader.close();
    }

    /**
     * 合并多个PDF文件
     *
     * @param inputFiles 输入文件列表
     * @param outputFile 输出文件
     * @throws IOException IO异常
     * @throws DocumentException 文档异常
     */
    public static void mergePdf(List<String> inputFiles, String outputFile)
            throws IOException, DocumentException {
        if (inputFiles == null || inputFiles.isEmpty()) {
            return;
        }

        Document document = new Document();
        PdfCopy copy = new PdfCopy(document, new FileOutputStream(outputFile));
        document.open();

        for (String inputFile : inputFiles) {
            PdfReader reader = new PdfReader(inputFile);
            int pageCount = reader.getNumberOfPages();

            for (int i = 1; i <= pageCount; i++) {
                copy.addPage(copy.getImportedPage(reader, i));
            }

            reader.close();
        }

        document.close();
    }

    /**
     * 分割PDF文件
     *
     * @param inputFile 输入文件
     * @param outputDir 输出目录
     * @param pageCountPerFile 每个文件的页数
     * @throws IOException IO异常
     * @throws DocumentException 文档异常
     */
    public static void splitPdf(String inputFile, String outputDir, int pageCountPerFile)
            throws IOException, DocumentException {
        PdfReader reader = new PdfReader(inputFile);
        int pageCount = reader.getNumberOfPages();

        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = new File(inputFile).getName();
        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));

        int fileIndex = 1;
        for (int i = 1; i <= pageCount; i += pageCountPerFile) {
            int endPage = Math.min(i + pageCountPerFile - 1, pageCount);
            String outputFile = outputDir + File.separator + baseName + "_" + fileIndex + ".pdf";

            Document document = new Document();
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(outputFile));
            document.open();

            for (int j = i; j <= endPage; j++) {
                copy.addPage(copy.getImportedPage(reader, j));
            }

            document.close();
            fileIndex++;
        }

        reader.close();
    }

    /**
     * 提取PDF文本内容
     *
     * @param inputFile 输入文件
     * @return 文本内容
     * @throws IOException IO异常
     */
    public static String extractText(String inputFile) throws IOException {
        PdfReader reader = new PdfReader(inputFile);
        StringBuilder text = new StringBuilder();

        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            text.append(PdfTextExtractor.getTextFromPage(reader, i));
            text.append("\n");
        }

        reader.close();
        return text.toString();
    }

    /**
     * 为PDF设置密码保护
     *
     * @param inputFile 输入文件
     * @param outputFile 输出文件
     * @param userPassword 用户密码
     * @param ownerPassword 所有者密码
     * @throws IOException IO异常
     * @throws DocumentException 文档异常
     */
    public static void setPasswordProtection(String inputFile, String outputFile,
                                             String userPassword, String ownerPassword) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(inputFile);

        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputFile));
        stamper.setEncryption(userPassword.getBytes(), ownerPassword.getBytes(),
                PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);

        stamper.close();
        reader.close();
    }

    /**
     * 填充PDF表单
     *
     * @param inputFile 模板文件
     * @param outputFile 输出文件
     * @param formData 表单数据
     * @throws IOException IO异常
     * @throws DocumentException 文档异常
     */
    public static void fillPdfForm(String inputFile, String outputFile, Map<String, String> formData)
            throws IOException, DocumentException {
        PdfReader reader = new PdfReader(inputFile);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputFile));

        AcroFields form = stamper.getAcroFields();

        // 填充表单字段
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            form.setField(entry.getKey(), entry.getValue());
        }

        // 设置表单不可编辑
        stamper.setFormFlattening(true);

        stamper.close();
        reader.close();
    }

    /**
     * 获取PDF表单字段
     *
     * @param inputFile 输入文件
     * @return 表单字段列表
     * @throws IOException IO异常
     */
    public static List<String> getPdfFormFields(String inputFile) throws IOException {
        PdfReader reader = new PdfReader(inputFile);
        AcroFields form = reader.getAcroFields();

        List<String> fields = new ArrayList<>(form.getFields().keySet());
        reader.close();

        return fields;
    }

    /**
     * 压缩PDF文件
     *
     * @param inputFile 输入文件
     * @param outputFile 输出文件
     * @throws IOException IO异常
     * @throws DocumentException 文档异常
     */
    public static void compressPdf(String inputFile, String outputFile)
            throws IOException, DocumentException {
        PdfReader reader = new PdfReader(new FileInputStream(inputFile));

        // 配置压缩选项
        int n = reader.getXrefSize();
        PdfObject object;
        PRStream stream;

        // 打开输出文件
        Document document = new Document();
        PdfCopy copy = new PdfCopy(document, new FileOutputStream(outputFile));
        document.open();

        // 压缩页面
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            copy.addPage(copy.getImportedPage(reader, i));
        }

        document.close();
        reader.close();
    }
} 