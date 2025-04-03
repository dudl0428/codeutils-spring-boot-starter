package com.codeutils.common.utils;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.*;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Word文档工具类，基于Apache POI实现
 */
public class WordUtils {

    /**
     * 创建Word文档
     * 
     * @return XWPFDocument对象
     */
    public static XWPFDocument createDocument() {
        return new XWPFDocument();
    }

    /**
     * 读取Word文档
     * 
     * @param filePath 文件路径
     * @return XWPFDocument对象
     * @throws IOException IO异常
     */
    public static XWPFDocument readDocument(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            return new XWPFDocument(fis);
        }
    }

    /**
     * 保存Word文档
     * 
     * @param document 文档对象
     * @param filePath 文件路径
     * @throws IOException IO异常
     */
    public static void saveDocument(XWPFDocument document, String filePath) throws IOException {
        if (document == null) {
            return;
        }
        
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            document.write(fos);
        } finally {
            document.close();
        }
    }

    /**
     * 添加段落
     * 
     * @param document 文档对象
     * @param text 文本内容
     * @return 段落对象
     */
    public static XWPFParagraph addParagraph(XWPFDocument document, String text) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        return paragraph;
    }

    /**
     * 添加具有样式的段落
     * 
     * @param document 文档对象
     * @param text 文本内容
     * @param fontSize 字体大小（单位：磅）
     * @param fontFamily 字体名称
     * @param bold 是否加粗
     * @param color 字体颜色（RGB格式，例如："FF0000"表示红色）
     * @param alignment 对齐方式（0居左，1居中，2居右）
     * @return 段落对象
     */
    public static XWPFParagraph addStyledParagraph(XWPFDocument document, String text, int fontSize, 
            String fontFamily, boolean bold, String color, int alignment) {
        XWPFParagraph paragraph = document.createParagraph();
        
        // 设置对齐方式
        if (alignment == 1) {
            paragraph.setAlignment(ParagraphAlignment.CENTER);
        } else if (alignment == 2) {
            paragraph.setAlignment(ParagraphAlignment.RIGHT);
        } else {
            paragraph.setAlignment(ParagraphAlignment.LEFT);
        }
        
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        
        // 设置字体样式
        if (fontSize > 0) {
            run.setFontSize(fontSize);
        }
        
        if (fontFamily != null && !fontFamily.isEmpty()) {
            run.setFontFamily(fontFamily);
        }
        
        run.setBold(bold);
        
        if (color != null && !color.isEmpty()) {
            run.setColor(color);
        }
        
        return paragraph;
    }

    /**
     * 添加标题
     * 
     * @param document 文档对象
     * @param text 标题文本
     * @param level 标题级别（1-9）
     * @return 段落对象
     */
    public static XWPFParagraph addTitle(XWPFDocument document, String text, int level) {
        XWPFParagraph paragraph = document.createParagraph();
        
        // 设置标题级别
        paragraph.setStyle("Heading" + Math.min(Math.max(level, 1), 9));
        
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(true);
        
        // 根据级别设置字体大小
        switch (level) {
            case 1:
                run.setFontSize(20);
                break;
            case 2:
                run.setFontSize(16);
                break;
            case 3:
                run.setFontSize(14);
                break;
            default:
                run.setFontSize(12);
                break;
        }
        
        return paragraph;
    }

    /**
     * 添加表格
     * 
     * @param document 文档对象
     * @param rows 行数
     * @param cols 列数
     * @return 表格对象
     */
    public static XWPFTable addTable(XWPFDocument document, int rows, int cols) {
        XWPFTable table = document.createTable(rows, cols);
        
        // 设置表格样式
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        tblWidth.setW(BigInteger.valueOf(8500)); // 表格宽度
        tblWidth.setType(STTblWidth.DXA);
        
        return table;
    }

    /**
     * 设置表格单元格内容
     * 
     * @param table 表格对象
     * @param rowIndex 行索引
     * @param colIndex 列索引
     * @param text 文本内容
     */
    public static void setCellText(XWPFTable table, int rowIndex, int colIndex, String text) {
        XWPFTableRow row = table.getRow(rowIndex);
        XWPFTableCell cell = row.getCell(colIndex);
        
        // 清空单元格内容
        for (int i = cell.getParagraphs().size() - 1; i >= 0; i--) {
            cell.removeParagraph(i);
        }
        
        // 添加新内容
        XWPFParagraph paragraph = cell.addParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
    }

    /**
     * 设置表格表头样式
     * 
     * @param table 表格对象
     * @param headerTexts 表头文本数组
     */
    public static void setTableHeader(XWPFTable table, String[] headerTexts) {
        if (headerTexts == null || headerTexts.length == 0) {
            return;
        }
        
        XWPFTableRow headerRow = table.getRow(0);
        
        // 设置表头样式
        for (int i = 0; i < headerTexts.length && i < headerRow.getTableCells().size(); i++) {
            XWPFTableCell cell = headerRow.getCell(i);
            
            // 清空单元格内容
            for (int j = cell.getParagraphs().size() - 1; j >= 0; j--) {
                cell.removeParagraph(j);
            }
            
            // 添加新内容
            XWPFParagraph paragraph = cell.addParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = paragraph.createRun();
            run.setText(headerTexts[i]);
            run.setBold(true);
            run.setFontSize(12);
            
            // 设置单元格背景色
            CTTcPr tcPr = cell.getCTTc().getTcPr();
            if (tcPr == null) {
                tcPr = cell.getCTTc().addNewTcPr();
            }
            CTShd ctShd = tcPr.isSetShd() ? tcPr.getShd() : tcPr.addNewShd();
            ctShd.setColor("auto");
            ctShd.setVal(STShd.CLEAR);
            ctShd.setFill("D9D9D9"); // 浅灰色背景
        }
    }

    /**
     * 填充表格数据
     * 
     * @param table 表格对象
     * @param data 数据列表（每行数据是一个字符串数组）
     * @param startRowIndex 起始行索引
     */
    public static void fillTableData(XWPFTable table, List<String[]> data, int startRowIndex) {
        if (data == null || data.isEmpty()) {
            return;
        }
        
        // 确保表格有足够的行
        int requiredRows = startRowIndex + data.size();
        while (table.getRows().size() < requiredRows) {
            table.createRow();
        }
        
        // 填充数据
        for (int i = 0; i < data.size(); i++) {
            String[] rowData = data.get(i);
            XWPFTableRow row = table.getRow(startRowIndex + i);
            
            // 确保行有足够的单元格
            while (row.getTableCells().size() < rowData.length) {
                row.addNewTableCell();
            }
            
            // 填充单元格
            for (int j = 0; j < rowData.length; j++) {
                setCellText(table, startRowIndex + i, j, rowData[j]);
            }
        }
    }

    /**
     * 添加分页符
     * 
     * @param document 文档对象
     */
    public static void addPageBreak(XWPFDocument document) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setPageBreak(true);
    }

    /**
     * 添加图片
     * 
     * @param document 文档对象
     * @param imagePath 图片路径
     * @param width 宽度（单位：像素）
     * @param height 高度（单位：像素）
     * @throws Exception 异常
     */
    public static void addImage(XWPFDocument document, String imagePath, int width, int height) throws Exception {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        
        try (FileInputStream fis = new FileInputStream(imagePath)) {
            String imageType = getImageType(imagePath);
            
            if ("png".equalsIgnoreCase(imageType)) {
                run.addPicture(fis, Document.PICTURE_TYPE_PNG, imagePath, width, height);
            } else if ("jpg".equalsIgnoreCase(imageType) || "jpeg".equalsIgnoreCase(imageType)) {
                run.addPicture(fis, Document.PICTURE_TYPE_JPEG, imagePath, width, height);
            } else if ("gif".equalsIgnoreCase(imageType)) {
                run.addPicture(fis, Document.PICTURE_TYPE_GIF, imagePath, width, height);
            } else {
                throw new IllegalArgumentException("不支持的图片格式: " + imageType);
            }
        }
    }

    /**
     * 获取图片类型
     * 
     * @param imagePath 图片路径
     * @return 图片类型
     */
    private static String getImageType(String imagePath) {
        String extension = imagePath.substring(imagePath.lastIndexOf('.') + 1);
        return extension.toLowerCase();
    }

    /**
     * 添加超链接
     * 
     * @param document 文档对象
     * @param text 超链接文本
     * @param url 超链接URL
     * @return 段落对象
     */
    public static XWPFParagraph addHyperlink(XWPFDocument document, String text, String url) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        
        // 设置超链接文本样式
        run.setText(text);
        run.setColor("0000FF"); // 蓝色
        run.setUnderline(UnderlinePatterns.SINGLE); // 下划线
        
        // 添加超链接
        CTHyperlink hyperlink = paragraph.getCTP().addNewHyperlink();
        hyperlink.setId("rId" + System.currentTimeMillis());
        hyperlink.addNewR().set(run.getCTR());
        
        return paragraph;
    }

    /**
     * 替换文档中的占位符
     * 
     * @param document 文档对象
     * @param placeholders 占位符映射（键是占位符，值是替换内容）
     */
    public static void replaceText(XWPFDocument document, Map<String, String> placeholders) {
        if (placeholders == null || placeholders.isEmpty()) {
            return;
        }
        
        // 替换段落中的占位符
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replaceParagraphText(paragraph, placeholders);
        }
        
        // 替换表格中的占位符
        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        replaceParagraphText(paragraph, placeholders);
                    }
                }
            }
        }
    }

    /**
     * 替换段落中的占位符
     * 
     * @param paragraph 段落对象
     * @param placeholders 占位符映射
     */
    private static void replaceParagraphText(XWPFParagraph paragraph, Map<String, String> placeholders) {
        String paragraphText = paragraph.getText();
        
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String placeholder = entry.getKey();
            String replacement = entry.getValue();
            
            if (paragraphText.contains(placeholder)) {
                // 替换文本
                List<XWPFRun> runs = paragraph.getRuns();
                TextSegment segment = null;
                
                // 查找包含占位符的文本段
                for (int i = 0; i < runs.size(); i++) {
                    String runText = runs.get(i).getText(0);
                    if (runText != null && runText.contains(placeholder)) {
                        segment = new TextSegment(i, i, placeholder, replacement);
                        break;
                    }
                }
                
                // 替换文本段
                if (segment != null) {
                    XWPFRun run = runs.get(segment.getBeginRun());
                    String text = run.getText(0);
                    text = text.replace(segment.getOldText(), segment.getNewText());
                    run.setText(text, 0);
                }
            }
        }
    }

    /**
     * 文本段类，用于替换文本
     */
    private static class TextSegment {
        private int beginRun;
        private int endRun;
        private String oldText;
        private String newText;
        
        public TextSegment(int beginRun, int endRun, String oldText, String newText) {
            this.beginRun = beginRun;
            this.endRun = endRun;
            this.oldText = oldText;
            this.newText = newText;
        }
        
        public int getBeginRun() {
            return beginRun;
        }
        
        public int getEndRun() {
            return endRun;
        }
        
        public String getOldText() {
            return oldText;
        }
        
        public String getNewText() {
            return newText;
        }
    }
} 