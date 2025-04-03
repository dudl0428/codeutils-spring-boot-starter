package com.codeutils.common.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyAccessorFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Bean工具类，用于对象操作、属性复制等
 */
public class BeanUtils {

    /**
     * 对象属性复制，将源对象的属性复制到目标对象
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target);
    }

    /**
     * 对象属性复制，将源对象的属性复制到目标对象，忽略指定的属性
     *
     * @param source 源对象
     * @param target 目标对象
     * @param ignoreProperties 忽略的属性名称
     */
    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, ignoreProperties);
    }

    /**
     * 获取对象中值为null的属性名称数组
     *
     * @param source 源对象
     * @return 值为null的属性名称数组
     */
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * 复制非空属性，忽略源对象中值为null的属性
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyPropertiesIgnoreNull(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    /**
     * 对象转换，将源对象转换为目标类型的对象
     *
     * @param source 源对象
     * @param targetClass 目标类型
     * @param <T> 目标泛型
     * @return 目标类型对象
     */
    public static <T> T convert(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("对象转换失败", e);
        }
    }

    /**
     * 对象列表转换，将源对象列表转换为目标类型的对象列表
     *
     * @param sourceList 源对象列表
     * @param targetClass 目标类型
     * @param <T> 目标泛型
     * @return 目标类型对象列表
     */
    public static <T> List<T> convertList(List<?> sourceList, Class<T> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>();
        }
        
        return sourceList.stream()
                .map(source -> convert(source, targetClass))
                .collect(Collectors.toList());
    }

    /**
     * 将Map转换为对象
     *
     * @param map Map对象
     * @param beanClass 目标类型
     * @param <T> 目标泛型
     * @return 目标类型对象
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> beanClass) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        
        try {
            T bean = beanClass.getDeclaredConstructor().newInstance();
            BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (wrapper.isWritableProperty(entry.getKey())) {
                    wrapper.setPropertyValue(entry.getKey(), entry.getValue());
                }
            }
            return bean;
        } catch (Exception e) {
            throw new RuntimeException("Map转对象失败", e);
        }
    }

    /**
     * 将对象转换为Map
     *
     * @param bean 对象
     * @return Map对象
     */
    public static Map<String, Object> beanToMap(Object bean) {
        if (bean == null) {
            return Collections.emptyMap();
        }
        
        try {
            Map<String, Object> map = new HashMap<>();
            BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
            for (PropertyDescriptor pd : wrapper.getPropertyDescriptors()) {
                if (!"class".equals(pd.getName())) {
                    Object value = wrapper.getPropertyValue(pd.getName());
                    map.put(pd.getName(), value);
                }
            }
            return map;
        } catch (Exception e) {
            throw new RuntimeException("对象转Map失败", e);
        }
    }

    /**
     * 获取对象的所有字段（包括父类字段）
     *
     * @param clazz 类
     * @return 字段列表
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }

    /**
     * 通过反射获取对象的属性值
     *
     * @param obj 对象
     * @param fieldName 字段名
     * @return 属性值
     */
    public static Object getProperty(Object obj, String fieldName) {
        if (obj == null || StringUtils.isEmpty(fieldName)) {
            return null;
        }
        
        try {
            BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(obj);
            return wrapper.getPropertyValue(fieldName);
        } catch (Exception e) {
            throw new RuntimeException("获取属性值失败", e);
        }
    }

    /**
     * 通过反射设置对象的属性值
     *
     * @param obj 对象
     * @param fieldName 字段名
     * @param value 属性值
     */
    public static void setProperty(Object obj, String fieldName, Object value) {
        if (obj == null || StringUtils.isEmpty(fieldName)) {
            return;
        }
        
        try {
            BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(obj);
            wrapper.setPropertyValue(fieldName, value);
        } catch (Exception e) {
            throw new RuntimeException("设置属性值失败", e);
        }
    }

    /**
     * 通过反射调用对象的方法
     *
     * @param obj 对象
     * @param methodName 方法名
     * @param args 参数值
     * @return 方法返回值
     */
    public static Object invokeMethod(Object obj, String methodName, Object... args) {
        if (obj == null || StringUtils.isEmpty(methodName)) {
            return null;
        }
        
        try {
            Class<?>[] parameterTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i] != null ? args[i].getClass() : Object.class;
            }
            
            Method method = obj.getClass().getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw new RuntimeException("调用方法失败", e);
        }
    }
} 