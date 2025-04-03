package com.codeutils.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * JSON工具类，基于Jackson实现
 */
public class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    // 初始化ObjectMapper配置
    static {
        // 忽略未知属性
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 序列化时忽略null值
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
    
    /**
     * 获取ObjectMapper实例
     * 
     * @return ObjectMapper实例
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
    
    /**
     * 对象转JSON字符串
     * 
     * @param object 对象
     * @return JSON字符串
     */
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("对象转JSON字符串失败", e);
        }
    }
    
    /**
     * 对象转格式化的JSON字符串
     * 
     * @param object 对象
     * @return 格式化的JSON字符串
     */
    public static String toPrettyJson(Object object) {
        if (object == null) {
            return null;
        }
        
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("对象转格式化JSON字符串失败", e);
        }
    }
    
    /**
     * JSON字符串转对象
     * 
     * @param json JSON字符串
     * @param clazz 目标类型
     * @param <T> 泛型
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("JSON字符串转对象失败", e);
        }
    }
    
    /**
     * JSON字符串转复杂类型对象
     * 
     * @param json JSON字符串
     * @param typeReference 类型引用
     * @param <T> 泛型
     * @return 对象
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException("JSON字符串转复杂类型对象失败", e);
        }
    }
    
    /**
     * JSON字符串转List
     * 
     * @param json JSON字符串
     * @param elementClass 元素类型
     * @param <T> 泛型
     * @return List对象
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> elementClass) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, elementClass);
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (IOException e) {
            throw new RuntimeException("JSON字符串转List失败", e);
        }
    }
    
    /**
     * JSON字符串转Map
     * 
     * @param json JSON字符串
     * @param keyClass 键类型
     * @param valueClass 值类型
     * @param <K> 键泛型
     * @param <V> 值泛型
     * @return Map对象
     */
    public static <K, V> Map<K, V> fromJsonToMap(String json, Class<K> keyClass, Class<V> valueClass) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (IOException e) {
            throw new RuntimeException("JSON字符串转Map失败", e);
        }
    }
    
    /**
     * 对象转换为另一类型的对象
     * 
     * @param fromValue 源对象
     * @param toValueType 目标类型
     * @param <T> 目标泛型
     * @return 目标对象
     */
    public static <T> T convert(Object fromValue, Class<T> toValueType) {
        if (fromValue == null) {
            return null;
        }
        
        return OBJECT_MAPPER.convertValue(fromValue, toValueType);
    }
    
    /**
     * 对象转换为另一复杂类型的对象
     * 
     * @param fromValue 源对象
     * @param toValueTypeRef 目标类型引用
     * @param <T> 目标泛型
     * @return 目标对象
     */
    public static <T> T convert(Object fromValue, TypeReference<T> toValueTypeRef) {
        if (fromValue == null) {
            return null;
        }
        
        return OBJECT_MAPPER.convertValue(fromValue, toValueTypeRef);
    }
    
    /**
     * 创建JSON对象节点
     * 
     * @return ObjectNode对象
     */
    public static ObjectNode createObjectNode() {
        return OBJECT_MAPPER.createObjectNode();
    }
    
    /**
     * 创建JSON数组节点
     * 
     * @return ArrayNode对象
     */
    public static ArrayNode createArrayNode() {
        return OBJECT_MAPPER.createArrayNode();
    }
    
    /**
     * 将JSON字符串转换为JsonNode
     * 
     * @param json JSON字符串
     * @return JsonNode对象
     */
    public static JsonNode parseJson(String json) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException("解析JSON字符串失败", e);
        }
    }
    
    /**
     * 获取JsonNode中的文本值
     * 
     * @param jsonNode JsonNode对象
     * @param fieldName 字段名
     * @return 文本值
     */
    public static String getTextValue(JsonNode jsonNode, String fieldName) {
        if (jsonNode == null || !jsonNode.has(fieldName)) {
            return null;
        }
        
        JsonNode node = jsonNode.get(fieldName);
        return node.isNull() ? null : node.asText();
    }
    
    /**
     * 获取JsonNode中的整数值
     * 
     * @param jsonNode JsonNode对象
     * @param fieldName 字段名
     * @return 整数值
     */
    public static Integer getIntValue(JsonNode jsonNode, String fieldName) {
        if (jsonNode == null || !jsonNode.has(fieldName)) {
            return null;
        }
        
        JsonNode node = jsonNode.get(fieldName);
        return node.isNull() ? null : node.asInt();
    }
    
    /**
     * 获取JsonNode中的长整数值
     * 
     * @param jsonNode JsonNode对象
     * @param fieldName 字段名
     * @return 长整数值
     */
    public static Long getLongValue(JsonNode jsonNode, String fieldName) {
        if (jsonNode == null || !jsonNode.has(fieldName)) {
            return null;
        }
        
        JsonNode node = jsonNode.get(fieldName);
        return node.isNull() ? null : node.asLong();
    }
    
    /**
     * 获取JsonNode中的双精度浮点数值
     * 
     * @param jsonNode JsonNode对象
     * @param fieldName 字段名
     * @return 双精度浮点数值
     */
    public static Double getDoubleValue(JsonNode jsonNode, String fieldName) {
        if (jsonNode == null || !jsonNode.has(fieldName)) {
            return null;
        }
        
        JsonNode node = jsonNode.get(fieldName);
        return node.isNull() ? null : node.asDouble();
    }
    
    /**
     * 获取JsonNode中的布尔值
     * 
     * @param jsonNode JsonNode对象
     * @param fieldName 字段名
     * @return 布尔值
     */
    public static Boolean getBooleanValue(JsonNode jsonNode, String fieldName) {
        if (jsonNode == null || !jsonNode.has(fieldName)) {
            return null;
        }
        
        JsonNode node = jsonNode.get(fieldName);
        return node.isNull() ? null : node.asBoolean();
    }
} 