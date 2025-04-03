package com.codeutils.common.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Excel工具类，基于Apache POI实现
 */
public class ExcelUtils {

    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    private static final int DEFAULT_BATCH_SIZE = 1000; // 大批量数据导出时的批次大小

    /**
     * 读取Excel文件（自动判断Excel版本）
     * 
     * @param file Excel文件
     * @return 工作簿对象
     * @throws IOException IO异常
     */
    public static Workbook readExcel(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new FileNotFoundException("Excel文件不存在");
        }
        
        try (InputStream is = new FileInputStream(file)) {
            String fileName = file.getName().toLowerCase();
            if (fileName.endsWith(".xls")) {
                return new HSSFWorkbook(is);
            } else if (fileName.endsWith(".xlsx")) {
                return new XSSFWorkbook(is);
            } else {
                throw new IllegalArgumentException("不支持的Excel文件格式");
            }
        }
    }

    /**
     * 读取Excel文件（自动判断Excel版本）
     * 
     * @param filePath Excel文件路径
     * @return 工作簿对象
     * @throws IOException IO异常
     */
    public static Workbook readExcel(String filePath) throws IOException {
        return readExcel(new File(filePath));
    }

    /**
     * 创建Excel工作簿
     * 
     * @param isXlsx 是否为xlsx格式(true:xlsx, false:xls)
     * @return 工作簿对象
     */
    public static Workbook createWorkbook(boolean isXlsx) {
        return isXlsx ? new XSSFWorkbook() : new HSSFWorkbook();
    }

    /**
     * 创建Excel工作簿（大数据量，仅支持xlsx格式）
     * 
     * @param rowAccessWindowSize 内存中行数窗口大小
     * @return 工作簿对象
     */
    public static Workbook createSXSSFWorkbook(int rowAccessWindowSize) {
        return new SXSSFWorkbook(rowAccessWindowSize);
    }

    /**
     * 保存Excel工作簿到文件
     * 
     * @param workbook 工作簿对象
     * @param filePath 文件路径
     * @throws IOException IO异常
     */
    public static void saveExcel(Workbook workbook, String filePath) throws IOException {
        if (workbook == null) {
            return;
        }
        
        try (OutputStream os = new FileOutputStream(filePath)) {
            workbook.write(os);
        } finally {
            if (workbook instanceof SXSSFWorkbook) {
                ((SXSSFWorkbook) workbook).dispose();
            }
            workbook.close();
        }
    }

    /**
     * 将Excel数据读取为Map列表，以第一行为键
     * 
     * @param filePath Excel文件路径
     * @param sheetIndex 工作表索引
     * @return Map列表
     * @throws IOException IO异常
     */
    public static List<Map<String, Object>> readExcelToMapList(String filePath, int sheetIndex) throws IOException {
        try (Workbook workbook = readExcel(filePath)) {
            return readSheetToMapList(workbook.getSheetAt(sheetIndex));
        }
    }

    /**
     * 将工作表数据读取为Map列表，以第一行为键
     * 
     * @param sheet 工作表
     * @return Map列表
     */
    public static List<Map<String, Object>> readSheetToMapList(Sheet sheet) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (sheet == null || sheet.getPhysicalNumberOfRows() <= 1) {
            return result;
        }
        
        Row headerRow = sheet.getRow(0);
        int cellCount = headerRow.getPhysicalNumberOfCells();
        
        List<String> headers = new ArrayList<>();
        for (int i = 0; i < cellCount; i++) {
            Cell cell = headerRow.getCell(i);
            headers.add(getCellValue(cell).toString());
        }
        
        int rowCount = sheet.getPhysicalNumberOfRows();
        for (int i = 1; i < rowCount; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            
            Map<String, Object> rowMap = new HashMap<>();
            for (int j = 0; j < cellCount; j++) {
                if (j >= headers.size()) {
                    break;
                }
                
                Cell cell = row.getCell(j);
                if (cell != null) {
                    rowMap.put(headers.get(j), getCellValue(cell));
                }
            }
            
            if (!rowMap.isEmpty()) {
                result.add(rowMap);
            }
        }
        
        return result;
    }

    /**
     * 将Excel数据读取为对象列表
     * 
     * @param filePath Excel文件路径
     * @param sheetIndex 工作表索引
     * @param clazz 对象类型
     * @param headerRowIndex 表头行索引
     * @param <T> 对象泛型
     * @return 对象列表
     * @throws Exception 异常
     */
    public static <T> List<T> readExcelToObjectList(String filePath, int sheetIndex, Class<T> clazz, int headerRowIndex) throws Exception {
        try (Workbook workbook = readExcel(filePath)) {
            return readSheetToObjectList(workbook.getSheetAt(sheetIndex), clazz, headerRowIndex);
        }
    }

    /**
     * 将工作表数据读取为对象列表
     * 
     * @param sheet 工作表
     * @param clazz 对象类型
     * @param headerRowIndex 表头行索引
     * @param <T> 对象泛型
     * @return 对象列表
     * @throws Exception 异常
     */
    public static <T> List<T> readSheetToObjectList(Sheet sheet, Class<T> clazz, int headerRowIndex) throws Exception {
        List<T> result = new ArrayList<>();
        if (sheet == null || sheet.getPhysicalNumberOfRows() <= headerRowIndex + 1) {
            return result;
        }
        
        Row headerRow = sheet.getRow(headerRowIndex);
        int cellCount = headerRow.getPhysicalNumberOfCells();
        
        List<String> headers = new ArrayList<>();
        for (int i = 0; i < cellCount; i++) {
            Cell cell = headerRow.getCell(i);
            headers.add(getCellValue(cell).toString());
        }
        
        Map<String, Field> fieldMap = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            fieldMap.put(field.getName(), field);
        }
        
        int rowCount = sheet.getPhysicalNumberOfRows();
        for (int i = headerRowIndex + 1; i < rowCount; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            
            T obj = clazz.getDeclaredConstructor().newInstance();
            boolean hasValue = false;
            
            for (int j = 0; j < cellCount; j++) {
                if (j >= headers.size()) {
                    break;
                }
                
                String headerName = headers.get(j);
                Field field = fieldMap.get(headerName);
                if (field == null) {
                    continue;
                }
                
                Cell cell = row.getCell(j);
                if (cell != null) {
                    Object cellValue = getCellValue(cell);
                    if (cellValue != null) {
                        // 根据字段类型进行转换
                        setFieldValue(obj, field, cellValue);
                        hasValue = true;
                    }
                }
            }
            
            if (hasValue) {
                result.add(obj);
            }
        }
        
        return result;
    }

    /**
     * 将对象列表导出为Excel文件
     * 
     * @param dataList 数据列表
     * @param headers 表头
     * @param filePath 文件路径
     * @param sheetName 工作表名称
     * @param isXlsx 是否为xlsx格式(true:xlsx, false:xls)
     * @param <T> 对象泛型
     * @throws Exception 异常
     */
    public static <T> void exportExcel(List<T> dataList, String[] headers, String filePath, String sheetName, boolean isXlsx) throws Exception {
        if (dataList == null || dataList.isEmpty()) {
            throw new IllegalArgumentException("导出数据不能为空");
        }
        
        if (headers == null || headers.length == 0) {
            throw new IllegalArgumentException("表头不能为空");
        }
        
        try (Workbook workbook = isXlsx ? new XSSFWorkbook() : new HSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                
                // 设置表头样式
                CellStyle headerStyle = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                headerStyle.setFont(font);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                cell.setCellStyle(headerStyle);
            }
            
            // 填充数据
            int rowIndex = 1;
            for (T data : dataList) {
                Row row = sheet.createRow(rowIndex++);
                
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = row.createCell(i);
                    
                    // 获取字段值
                    try {
                        Field field = data.getClass().getDeclaredField(headers[i]);
                        field.setAccessible(true);
                        Object value = field.get(data);
                        setCellValue(cell, value);
                    } catch (NoSuchFieldException e) {
                        // 字段不存在，跳过
                    }
                }
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // 保存文件
            try (OutputStream os = new FileOutputStream(filePath)) {
                workbook.write(os);
            }
        }
    }

    /**
     * 大批量数据导出Excel（适用于数据量很大的场景，仅支持xlsx格式）
     * 
     * @param dataList 数据列表
     * @param headers 表头
     * @param filePath 文件路径
     * @param sheetName 工作表名称
     * @param <T> 对象泛型
     * @throws Exception 异常
     */
    public static <T> void exportLargeExcel(List<T> dataList, String[] headers, String filePath, String sheetName) throws Exception {
        if (dataList == null || dataList.isEmpty()) {
            throw new IllegalArgumentException("导出数据不能为空");
        }
        
        if (headers == null || headers.length == 0) {
            throw new IllegalArgumentException("表头不能为空");
        }
        
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) { // 内存中保留100行
            Sheet sheet = workbook.createSheet(sheetName);
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                
                // 设置表头样式
                CellStyle headerStyle = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                headerStyle.setFont(font);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                cell.setCellStyle(headerStyle);
            }
            
            // 分批处理数据
            int batchSize = DEFAULT_BATCH_SIZE;
            int batchCount = (dataList.size() + batchSize - 1) / batchSize;
            
            for (int batchIndex = 0; batchIndex < batchCount; batchIndex++) {
                int startIndex = batchIndex * batchSize;
                int endIndex = Math.min(startIndex + batchSize, dataList.size());
                List<T> batchData = dataList.subList(startIndex, endIndex);
                
                // 填充数据
                int rowIndex = startIndex + 1;
                for (T data : batchData) {
                    Row row = sheet.createRow(rowIndex++);
                    
                    for (int i = 0; i < headers.length; i++) {
                        Cell cell = row.createCell(i);
                        
                        // 获取字段值
                        try {
                            Field field = data.getClass().getDeclaredField(headers[i]);
                            field.setAccessible(true);
                            Object value = field.get(data);
                            setCellValue(cell, value);
                        } catch (NoSuchFieldException e) {
                            // 字段不存在，跳过
                        }
                    }
                }
            }
            
            // 保存文件
            try (OutputStream os = new FileOutputStream(filePath)) {
                workbook.write(os);
            } finally {
                workbook.dispose(); // 清理临时文件
            }
        }
    }

    /**
     * 获取单元格的值
     * 
     * @param cell 单元格
     * @return 单元格的值
     */
    public static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    // 处理整数
                    if (numericValue == (long) numericValue) {
                        return (long) numericValue;
                    } else {
                        return numericValue;
                    }
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                try {
                    return cell.getNumericCellValue();
                } catch (Exception e) {
                    try {
                        return cell.getStringCellValue();
                    } catch (Exception ex) {
                        return null;
                    }
                }
            default:
                return null;
        }
    }

    /**
     * 设置单元格的值
     * 
     * @param cell 单元格
     * @param value 值
     */
    public static void setCellValue(Cell cell, Object value) {
        if (cell == null || value == null) {
            return;
        }
        
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
            
            // 设置日期格式
            CellStyle cellStyle = cell.getSheet().getWorkbook().createCellStyle();
            CreationHelper createHelper = cell.getSheet().getWorkbook().getCreationHelper();
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(DEFAULT_DATE_PATTERN));
            cell.setCellStyle(cellStyle);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 设置对象字段的值
     * 
     * @param obj 对象
     * @param field 字段
     * @param value 值
     * @throws IllegalAccessException 非法访问异常
     */
    private static void setFieldValue(Object obj, Field field, Object value) throws IllegalAccessException {
        Class<?> fieldType = field.getType();
        
        if (value == null) {
            field.set(obj, null);
            return;
        }
        
        if (fieldType == String.class) {
            field.set(obj, value.toString());
        } else if (fieldType == int.class || fieldType == Integer.class) {
            if (value instanceof Number) {
                field.set(obj, ((Number) value).intValue());
            } else {
                field.set(obj, Integer.parseInt(value.toString()));
            }
        } else if (fieldType == long.class || fieldType == Long.class) {
            if (value instanceof Number) {
                field.set(obj, ((Number) value).longValue());
            } else {
                field.set(obj, Long.parseLong(value.toString()));
            }
        } else if (fieldType == double.class || fieldType == Double.class) {
            if (value instanceof Number) {
                field.set(obj, ((Number) value).doubleValue());
            } else {
                field.set(obj, Double.parseDouble(value.toString()));
            }
        } else if (fieldType == float.class || fieldType == Float.class) {
            if (value instanceof Number) {
                field.set(obj, ((Number) value).floatValue());
            } else {
                field.set(obj, Float.parseFloat(value.toString()));
            }
        } else if (fieldType == boolean.class || fieldType == Boolean.class) {
            if (value instanceof Boolean) {
                field.set(obj, value);
            } else {
                field.set(obj, Boolean.parseBoolean(value.toString()));
            }
        } else if (fieldType == Date.class) {
            if (value instanceof Date) {
                field.set(obj, value);
            } else {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
                    field.set(obj, sdf.parse(value.toString()));
                } catch (Exception e) {
                    // 日期解析失败，跳过
                }
            }
        } else {
            // 其他类型，尝试直接赋值
            field.set(obj, value);
        }
    }
} 