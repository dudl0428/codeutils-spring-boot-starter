package com.codeutils.common.utils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 对象操作工具类，支持对象的流式操作
 */
public class ObjectUtils {

    /**
     * 判断对象是否为空
     *
     * @param obj 对象
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
     * 判断对象是否非空
     *
     * @param obj 对象
     * @return 是否非空
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 对象深拷贝（通过序列化方式）
     *
     * @param obj  源对象
     * @param <T>  对象类型
     * @return 拷贝后的对象
     * @throws IOException IO异常
     * @throws ClassNotFoundException 类未找到异常
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T deepCopy(T obj) throws IOException, ClassNotFoundException {
        if (obj == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (T) ois.readObject();
    }

    /**
     * 对象比较（比较两个对象是否相等）
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 是否相等
     */
    public static boolean equals(Object obj1, Object obj2) {
        if (obj1 == obj2) {
            return true;
        }

        if (obj1 == null || obj2 == null) {
            return false;
        }

        return obj1.equals(obj2);
    }

    /**
     * 获取对象哈希码
     *
     * @param obj 对象
     * @return 哈希码
     */
    public static int hashCode(Object obj) {
        return obj == null ? 0 : obj.hashCode();
    }

    /**
     * 对象转字符串
     *
     * @param obj 对象
     * @return 字符串表示
     */
    public static String toString(Object obj) {
        return obj == null ? "null" : obj.toString();
    }

    /**
     * 对象转字符串，如果为null则返回默认值
     *
     * @param obj 对象
     * @param defaultValue 默认值
     * @return 字符串表示
     */
    public static String toString(Object obj, String defaultValue) {
        return obj == null ? defaultValue : obj.toString();
    }

    /**
     * 获取对象类型
     *
     * @param obj 对象
     * @return 对象类型
     */
    public static Class<?> getType(Object obj) {
        return obj == null ? null : obj.getClass();
    }

    /**
     * 对象为空时返回默认值
     *
     * @param obj 对象
     * @param defaultValue 默认值
     * @param <T> 对象类型
     * @return 对象或默认值
     */
    public static <T> T defaultIfNull(T obj, T defaultValue) {
        return obj == null ? defaultValue : obj;
    }

    /**
     * 获取对象字段值
     *
     * @param obj 对象
     * @param fieldName 字段名
     * @return 字段值
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        if (obj == null || StringUtils.isEmpty(fieldName)) {
            return null;
        }

        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException("获取字段值失败: " + fieldName, e);
        }
    }

    /**
     * 设置对象字段值
     *
     * @param obj 对象
     * @param fieldName 字段名
     * @param value 字段值
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) {
        if (obj == null || StringUtils.isEmpty(fieldName)) {
            return;
        }

        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException("设置字段值失败: " + fieldName, e);
        }
    }

    /**
     * 调用对象方法
     *
     * @param obj 对象
     * @param methodName 方法名
     * @param args 参数
     * @return 方法返回值
     */
    public static Object invokeMethod(Object obj, String methodName, Object... args) {
        if (obj == null || StringUtils.isEmpty(methodName)) {
            return null;
        }

        try {
            Class<?>[] parameterTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i] != null ? args[i].getClass() : Object.class;
            }

            Method method = obj.getClass().getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw new RuntimeException("调用方法失败: " + methodName, e);
        }
    }

    /**
     * 对象流，支持对象的链式操作
     *
     * @param <T> 对象类型
     */
    public static class ObjectStream<T> {
        private final T value;

        private ObjectStream(T value) {
            this.value = value;
        }

        /**
         * 创建对象流
         *
         * @param value 对象
         * @param <T> 对象类型
         * @return 对象流
         */
        public static <T> ObjectStream<T> of(T value) {
            return new ObjectStream<>(value);
        }

        /**
         * 获取对象值
         *
         * @return 对象值
         */
        public T get() {
            return value;
        }

        /**
         * 对象为空时返回默认值
         *
         * @param defaultValue 默认值
         * @return 对象流
         */
        public ObjectStream<T> defaultIfNull(T defaultValue) {
            return value == null ? ObjectStream.of(defaultValue) : this;
        }

        /**
         * 转换对象类型
         *
         * @param mapper 转换函数
         * @param <R> 目标类型
         * @return 对象流
         */
        public <R> ObjectStream<R> map(Function<? super T, ? extends R> mapper) {
            return ObjectStream.of(value == null ? null : mapper.apply(value));
        }

        /**
         * 对象过滤
         *
         * @param predicate 条件
         * @return 对象流
         */
        public ObjectStream<T> filter(Predicate<? super T> predicate) {
            if (value == null || !predicate.test(value)) {
                return ObjectStream.of(null);
            }
            return this;
        }

        /**
         * 对象处理
         *
         * @param consumer 处理函数
         * @return 对象流
         */
        public ObjectStream<T> ifPresent(Consumer<? super T> consumer) {
            if (value != null) {
                consumer.accept(value);
            }
            return this;
        }

        /**
         * 对象转换为字符串
         *
         * @return 字符串
         */
        public String toString() {
            return value == null ? "null" : value.toString();
        }

        /**
         * 对象转换为字符串，如果为null则返回默认值
         *
         * @param defaultValue 默认值
         * @return 字符串
         */
        public String toString(String defaultValue) {
            return value == null ? defaultValue : value.toString();
        }

        /**
         * 获取对象哈希码
         *
         * @return 哈希码
         */
        public int hashCode() {
            return value == null ? 0 : value.hashCode();
        }

        /**
         * 判断对象是否相等
         *
         * @param obj 比较对象
         * @return 是否相等
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            // 由于泛型擦除，需要安全转换
            @SuppressWarnings("rawtypes")
            ObjectStream other = (ObjectStream) obj;
            // 使用工具类的equals方法比较值
            return ObjectUtils.equals(value, other.value);
        }
    }

    /**
     * 创建对象流
     *
     * @param value 对象
     * @param <T> 对象类型
     * @return 对象流
     */
    public static <T> ObjectStream<T> stream(T value) {
        return ObjectStream.of(value);
    }

    /**
     * 对象转Map
     *
     * @param obj 对象
     * @return Map对象
     */
    public static Map<String, Object> toMap(Object obj) {
        if (obj == null) {
            return Collections.emptyMap();
        }

        // 如果本身已经是Map类型，直接返回
        if (obj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) obj;
            return map;
        }

        try {
            Map<String, Object> result = new HashMap<>();
            Field[] fields = obj.getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                result.put(field.getName(), field.get(obj));
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("对象转Map失败", e);
        }
    }

    /**
     * 将对象转换为给定类型
     *
     * @param obj 对象
     * @param targetClass 目标类型
     * @param <T> 目标泛型
     * @return 转换后的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object obj, Class<T> targetClass) {
        if (obj == null) {
            return null;
        }

        // 如果对象已经是目标类型，直接返回
        if (targetClass.isInstance(obj)) {
            return (T) obj;
        }

        // 基本类型转换
        if (targetClass == String.class) {
            return (T) obj.toString();
        } else if (targetClass == Integer.class || targetClass == int.class) {
            return (T) Integer.valueOf(obj.toString());
        } else if (targetClass == Long.class || targetClass == long.class) {
            return (T) Long.valueOf(obj.toString());
        } else if (targetClass == Double.class || targetClass == double.class) {
            return (T) Double.valueOf(obj.toString());
        } else if (targetClass == Float.class || targetClass == float.class) {
            return (T) Float.valueOf(obj.toString());
        } else if (targetClass == Boolean.class || targetClass == boolean.class) {
            return (T) Boolean.valueOf(obj.toString());
        } else if (targetClass == Byte.class || targetClass == byte.class) {
            return (T) Byte.valueOf(obj.toString());
        } else if (targetClass == Short.class || targetClass == short.class) {
            return (T) Short.valueOf(obj.toString());
        } else if (targetClass == Character.class || targetClass == char.class) {
            String str = obj.toString();
            return (T) Character.valueOf(str.isEmpty() ? ' ' : str.charAt(0));
        }

        // 复杂类型转换
        try {
            // 使用BeanUtils进行转换
            return BeanUtils.convert(obj, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("对象转换失败", e);
        }
    }

    /**
     * 序列化对象为字节数组
     *
     * @param obj 对象
     * @return 字节数组
     * @throws IOException IO异常
     */
    public static byte[] serialize(Serializable obj) throws IOException {
        if (obj == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();

        return baos.toByteArray();
    }

    /**
     * 反序列化字节数组为对象
     *
     * @param bytes 字节数组
     * @param <T> 对象类型
     * @return 对象
     * @throws IOException IO异常
     * @throws ClassNotFoundException 类未找到异常
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (T) ois.readObject();
    }

    /**
     * 获取对象所有字段及其值
     *
     * @param obj 对象
     * @return 字段名和值的映射
     */
    public static Map<String, Object> getFieldsAndValues(Object obj) {
        if (obj == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> result = new LinkedHashMap<>();
        List<Field> fields = BeanUtils.getAllFields(obj.getClass());

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                result.put(field.getName(), field.get(obj));
            } catch (Exception e) {
                // 忽略不可访问字段
            }
        }

        return result;
    }

    /**
     * 打印对象详细信息
     *
     * @param obj 对象
     * @return 对象详细信息
     */
    public static String dump(Object obj) {
        if (obj == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(obj.getClass().getName()).append(" [");

        Map<String, Object> fieldsAndValues = getFieldsAndValues(obj);
        boolean first = true;

        for (Map.Entry<String, Object> entry : fieldsAndValues.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            first = false;

            sb.append(entry.getKey()).append("=");
            Object value = entry.getValue();

            if (value == null) {
                sb.append("null");
            } else if (value.getClass().isArray()) {
                sb.append(arrayToString(value));
            } else {
                sb.append(value);
            }
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * 数组转字符串
     *
     * @param array 数组
     * @return 字符串表示
     */
    private static String arrayToString(Object array) {
        if (array == null) {
            return "null";
        }

        int length = java.lang.reflect.Array.getLength(array);
        if (length == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(", ");
            }

            Object item = java.lang.reflect.Array.get(array, i);
            if (item == null) {
                sb.append("null");
            } else if (item.getClass().isArray()) {
                sb.append(arrayToString(item));
            } else {
                sb.append(item);
            }
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * 判断对象是否包含某个字段
     *
     * @param obj 对象
     * @param fieldName 字段名
     * @return 是否包含
     */
    public static boolean hasField(Object obj, String fieldName) {
        if (obj == null || StringUtils.isEmpty(fieldName)) {
            return false;
        }

        try {
            obj.getClass().getDeclaredField(fieldName);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    /**
     * 判断对象是否包含某个方法
     *
     * @param obj 对象
     * @param methodName 方法名
     * @param parameterTypes 参数类型
     * @return 是否包含
     */
    public static boolean hasMethod(Object obj, String methodName, Class<?>... parameterTypes) {
        if (obj == null || StringUtils.isEmpty(methodName)) {
            return false;
        }

        try {
            obj.getClass().getMethod(methodName, parameterTypes);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
} 