package com.codeutils.common.utils;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 流操作工具类，简化集合的流式操作
 */
public class StreamUtils {

    /**
     * 将集合转换为Stream
     *
     * @param collection 集合
     * @param <T> 集合元素类型
     * @return Stream流
     */
    public static <T> Stream<T> toStream(Collection<T> collection) {
        return collection == null ? Stream.empty() : collection.stream();
    }

    /**
     * 将数组转换为Stream
     *
     * @param array 数组
     * @param <T> 数组元素类型
     * @return Stream流
     */
    @SafeVarargs
    public static <T> Stream<T> toStream(T... array) {
        return array == null ? Stream.empty() : Arrays.stream(array);
    }

    /**
     * 过滤集合
     *
     * @param collection 集合
     * @param predicate 过滤条件
     * @param <T> 集合元素类型
     * @return 过滤后的列表
     */
    public static <T> List<T> filter(Collection<T> collection, Predicate<T> predicate) {
        return toStream(collection)
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * 转换集合元素
     *
     * @param collection 集合
     * @param mapper 转换函数
     * @param <T> 源集合元素类型
     * @param <R> 目标集合元素类型
     * @return 转换后的列表
     */
    public static <T, R> List<R> map(Collection<T> collection, Function<T, R> mapper) {
        return toStream(collection)
                .map(mapper)
                .collect(Collectors.toList());
    }

    /**
     * 扁平化转换集合元素
     *
     * @param collection 集合
     * @param mapper 转换函数
     * @param <T> 源集合元素类型
     * @param <R> 目标集合元素类型
     * @return 转换后的列表
     */
    public static <T, R> List<R> flatMap(Collection<T> collection, Function<T, Collection<R>> mapper) {
        return toStream(collection)
                .flatMap(item -> toStream(mapper.apply(item)))
                .collect(Collectors.toList());
    }

    /**
     * 对集合进行排序
     *
     * @param collection 集合
     * @param comparator 比较器
     * @param <T> 集合元素类型
     * @return 排序后的列表
     */
    public static <T> List<T> sort(Collection<T> collection, Comparator<T> comparator) {
        return toStream(collection)
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * 根据属性对集合进行排序
     *
     * @param collection 集合
     * @param keyExtractor 属性提取函数
     * @param <T> 集合元素类型
     * @param <U> 属性类型
     * @return 排序后的列表
     */
    public static <T, U extends Comparable<U>> List<T> sortBy(Collection<T> collection, Function<T, U> keyExtractor) {
        return toStream(collection)
                .sorted(Comparator.comparing(keyExtractor))
                .collect(Collectors.toList());
    }

    /**
     * 根据属性对集合进行倒序排序
     *
     * @param collection 集合
     * @param keyExtractor 属性提取函数
     * @param <T> 集合元素类型
     * @param <U> 属性类型
     * @return 排序后的列表
     */
    public static <T, U extends Comparable<U>> List<T> sortByDesc(Collection<T> collection, Function<T, U> keyExtractor) {
        return toStream(collection)
                .sorted(Comparator.comparing(keyExtractor).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 获取集合中的第一个元素
     *
     * @param collection 集合
     * @param <T> 集合元素类型
     * @return 第一个元素，如果集合为空则返回null
     */
    public static <T> T first(Collection<T> collection) {
        return toStream(collection)
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取集合中符合条件的第一个元素
     *
     * @param collection 集合
     * @param predicate 条件
     * @param <T> 集合元素类型
     * @return 符合条件的第一个元素，如果没有则返回null
     */
    public static <T> T firstWhere(Collection<T> collection, Predicate<T> predicate) {
        return toStream(collection)
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取集合中的最后一个元素
     *
     * @param collection 集合
     * @param <T> 集合元素类型
     * @return 最后一个元素，如果集合为空则返回null
     */
    public static <T> T last(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        
        if (collection instanceof List) {
            List<T> list = (List<T>) collection;
            return list.get(list.size() - 1);
        }
        
        return toStream(collection)
                .reduce((first, second) -> second)
                .orElse(null);
    }

    /**
     * 集合分组
     *
     * @param collection 集合
     * @param classifier 分组函数
     * @param <T> 集合元素类型
     * @param <K> 分组键类型
     * @return 分组结果
     */
    public static <T, K> Map<K, List<T>> groupBy(Collection<T> collection, Function<T, K> classifier) {
        return toStream(collection)
                .collect(Collectors.groupingBy(classifier));
    }

    /**
     * 集合转换为Map
     *
     * @param collection 集合
     * @param keyMapper 键映射函数
     * @param <T> 集合元素类型
     * @param <K> Map键类型
     * @return Map结果
     */
    public static <T, K> Map<K, T> toMap(Collection<T> collection, Function<T, K> keyMapper) {
        return toStream(collection)
                .collect(Collectors.toMap(keyMapper, Function.identity(), (a, b) -> a));
    }

    /**
     * 集合转换为Map
     *
     * @param collection 集合
     * @param keyMapper 键映射函数
     * @param valueMapper 值映射函数
     * @param <T> 集合元素类型
     * @param <K> Map键类型
     * @param <V> Map值类型
     * @return Map结果
     */
    public static <T, K, V> Map<K, V> toMap(Collection<T> collection, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return toStream(collection)
                .collect(Collectors.toMap(keyMapper, valueMapper, (a, b) -> a));
    }

    /**
     * 判断集合是否存在符合条件的元素
     *
     * @param collection 集合
     * @param predicate 条件
     * @param <T> 集合元素类型
     * @return 是否存在
     */
    public static <T> boolean any(Collection<T> collection, Predicate<T> predicate) {
        return toStream(collection).anyMatch(predicate);
    }

    /**
     * 判断集合是否所有元素都符合条件
     *
     * @param collection 集合
     * @param predicate 条件
     * @param <T> 集合元素类型
     * @return 是否所有元素都符合条件
     */
    public static <T> boolean all(Collection<T> collection, Predicate<T> predicate) {
        return toStream(collection).allMatch(predicate);
    }

    /**
     * 判断集合是否没有元素符合条件
     *
     * @param collection 集合
     * @param predicate 条件
     * @param <T> 集合元素类型
     * @return 是否没有元素符合条件
     */
    public static <T> boolean none(Collection<T> collection, Predicate<T> predicate) {
        return toStream(collection).noneMatch(predicate);
    }

    /**
     * 获取集合元素数量
     *
     * @param collection 集合
     * @param <T> 集合元素类型
     * @return 元素数量
     */
    public static <T> long count(Collection<T> collection) {
        return collection == null ? 0 : collection.size();
    }

    /**
     * 获取符合条件的集合元素数量
     *
     * @param collection 集合
     * @param predicate 条件
     * @param <T> 集合元素类型
     * @return 符合条件的元素数量
     */
    public static <T> long countWhere(Collection<T> collection, Predicate<T> predicate) {
        return toStream(collection).filter(predicate).count();
    }

    /**
     * 对集合元素进行聚合操作
     *
     * @param collection 集合
     * @param identity 初始值
     * @param accumulator 累加器
     * @param <T> 集合元素类型
     * @param <U> 结果类型
     * @return 聚合结果
     */
    public static <T, U> U reduce(Collection<T> collection, U identity, BiFunction<U, T, U> accumulator) {
        return toStream(collection).reduce(identity, accumulator, (a, b) -> a);
    }

    /**
     * 获取集合元素的最大值
     *
     * @param collection 集合
     * @param <T> 集合元素类型
     * @return 最大值，如果集合为空则返回null
     */
    public static <T extends Comparable<T>> T max(Collection<T> collection) {
        return toStream(collection)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    /**
     * 获取集合元素的最大值（根据指定字段）
     *
     * @param collection 集合
     * @param keyExtractor 字段提取函数
     * @param <T> 集合元素类型
     * @param <U> 字段类型
     * @return 最大值对应的元素，如果集合为空则返回null
     */
    public static <T, U extends Comparable<U>> T maxBy(Collection<T> collection, Function<T, U> keyExtractor) {
        return toStream(collection)
                .max(Comparator.comparing(keyExtractor))
                .orElse(null);
    }

    /**
     * 获取集合元素的最小值
     *
     * @param collection 集合
     * @param <T> 集合元素类型
     * @return 最小值，如果集合为空则返回null
     */
    public static <T extends Comparable<T>> T min(Collection<T> collection) {
        return toStream(collection)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    /**
     * 获取集合元素的最小值（根据指定字段）
     *
     * @param collection 集合
     * @param keyExtractor 字段提取函数
     * @param <T> 集合元素类型
     * @param <U> 字段类型
     * @return 最小值对应的元素，如果集合为空则返回null
     */
    public static <T, U extends Comparable<U>> T minBy(Collection<T> collection, Function<T, U> keyExtractor) {
        return toStream(collection)
                .min(Comparator.comparing(keyExtractor))
                .orElse(null);
    }

    /**
     * 获取集合元素的平均值
     *
     * @param collection 集合
     * @param mapper 数值映射函数
     * @param <T> 集合元素类型
     * @return 平均值，如果集合为空则返回0
     */
    public static <T> double average(Collection<T> collection, ToDoubleFunction<T> mapper) {
        return toStream(collection)
                .mapToDouble(mapper)
                .average()
                .orElse(0);
    }

    /**
     * 获取集合元素的总和
     *
     * @param collection 集合
     * @param mapper 数值映射函数
     * @param <T> 集合元素类型
     * @return 总和
     */
    public static <T> double sum(Collection<T> collection, ToDoubleFunction<T> mapper) {
        return toStream(collection)
                .mapToDouble(mapper)
                .sum();
    }

    /**
     * 集合去重
     *
     * @param collection 集合
     * @param <T> 集合元素类型
     * @return 去重后的列表
     */
    public static <T> List<T> distinct(Collection<T> collection) {
        return toStream(collection)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 集合根据字段去重
     *
     * @param collection 集合
     * @param keyExtractor 字段提取函数
     * @param <T> 集合元素类型
     * @param <U> 字段类型
     * @return 去重后的列表
     */
    public static <T, U> List<T> distinctBy(Collection<T> collection, Function<T, U> keyExtractor) {
        Map<U, T> map = new LinkedHashMap<>();
        for (T item : collection) {
            map.putIfAbsent(keyExtractor.apply(item), item);
        }
        return new ArrayList<>(map.values());
    }

    /**
     * 集合取前N个元素
     *
     * @param collection 集合
     * @param n 元素数量
     * @param <T> 集合元素类型
     * @return 前N个元素列表
     */
    public static <T> List<T> take(Collection<T> collection, int n) {
        return toStream(collection)
                .limit(n)
                .collect(Collectors.toList());
    }

    /**
     * 集合跳过前N个元素
     *
     * @param collection 集合
     * @param n 跳过数量
     * @param <T> 集合元素类型
     * @return 跳过前N个元素后的列表
     */
    public static <T> List<T> skip(Collection<T> collection, int n) {
        return toStream(collection)
                .skip(n)
                .collect(Collectors.toList());
    }

    /**
     * 集合分页
     *
     * @param collection 集合
     * @param pageIndex 页码（从0开始）
     * @param pageSize 每页大小
     * @param <T> 集合元素类型
     * @return 分页后的列表
     */
    public static <T> List<T> page(Collection<T> collection, int pageIndex, int pageSize) {
        return toStream(collection)
                .skip((long) pageIndex * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    /**
     * 集合分批处理
     *
     * @param collection 集合
     * @param batchSize 批次大小
     * @param <T> 集合元素类型
     * @return 分批后的列表的列表
     */
    public static <T> List<List<T>> batch(Collection<T> collection, int batchSize) {
        if (collection == null || collection.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<T> list = new ArrayList<>(collection);
        int size = list.size();
        int batchCount = (size + batchSize - 1) / batchSize;
        List<List<T>> result = new ArrayList<>(batchCount);
        
        for (int i = 0; i < batchCount; i++) {
            int fromIndex = i * batchSize;
            int toIndex = Math.min(fromIndex + batchSize, size);
            result.add(list.subList(fromIndex, toIndex));
        }
        
        return result;
    }

    /**
     * 集合转字符串
     *
     * @param collection 集合
     * @param delimiter 分隔符
     * @param <T> 集合元素类型
     * @return 字符串
     */
    public static <T> String join(Collection<T> collection, String delimiter) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        
        return toStream(collection)
                .map(Object::toString)
                .collect(Collectors.joining(delimiter));
    }

    /**
     * 集合转字符串（带前缀和后缀）
     *
     * @param collection 集合
     * @param delimiter 分隔符
     * @param prefix 前缀
     * @param suffix 后缀
     * @param <T> 集合元素类型
     * @return 字符串
     */
    public static <T> String join(Collection<T> collection, String delimiter, String prefix, String suffix) {
        if (collection == null || collection.isEmpty()) {
            return prefix + suffix;
        }
        
        return toStream(collection)
                .map(Object::toString)
                .collect(Collectors.joining(delimiter, prefix, suffix));
    }

    /**
     * 并行处理集合
     *
     * @param collection 集合
     * @param action 处理操作
     * @param <T> 集合元素类型
     */
    public static <T> void forEach(Collection<T> collection, Consumer<T> action) {
        toStream(collection).forEach(action);
    }

    /**
     * 并行处理集合
     *
     * @param collection 集合
     * @param action 处理操作
     * @param <T> 集合元素类型
     */
    public static <T> void forEachParallel(Collection<T> collection, Consumer<T> action) {
        toStream(collection).parallel().forEach(action);
    }

    /**
     * 将Stream转换为List
     *
     * @param stream Stream流
     * @param <T> 流元素类型
     * @return List列表
     */
    public static <T> List<T> toList(Stream<T> stream) {
        return stream.collect(Collectors.toList());
    }

    /**
     * 将Stream转换为Set
     *
     * @param stream Stream流
     * @param <T> 流元素类型
     * @return Set集合
     */
    public static <T> Set<T> toSet(Stream<T> stream) {
        return stream.collect(Collectors.toSet());
    }

    /**
     * 创建集合生成器
     *
     * @param <T> 集合元素类型
     * @return 生成器
     */
    public static <T> CollectionBuilder<T> builder() {
        return new CollectionBuilder<>();
    }

    /**
     * 集合生成器，支持链式构建集合
     *
     * @param <T> 集合元素类型
     */
    public static class CollectionBuilder<T> {
        private final List<T> items = new ArrayList<>();

        /**
         * 添加元素
         *
         * @param item 元素
         * @return 生成器
         */
        public CollectionBuilder<T> add(T item) {
            items.add(item);
            return this;
        }

        /**
         * 添加多个元素
         *
         * @param items 元素数组
         * @return 生成器
         */
        @SafeVarargs
        public final CollectionBuilder<T> addAll(T... items) {
            this.items.addAll(Arrays.asList(items));
            return this;
        }

        /**
         * 添加集合元素
         *
         * @param items 元素集合
         * @return 生成器
         */
        public CollectionBuilder<T> addAll(Collection<T> items) {
            this.items.addAll(items);
            return this;
        }

        /**
         * 构建列表
         *
         * @return 列表
         */
        public List<T> build() {
            return new ArrayList<>(items);
        }

        /**
         * 构建不可变列表
         *
         * @return 不可变列表
         */
        public List<T> buildImmutable() {
            return Collections.unmodifiableList(new ArrayList<>(items));
        }

        /**
         * 构建集合
         *
         * @return 集合
         */
        public Set<T> buildSet() {
            return new HashSet<>(items);
        }

        /**
         * 构建不可变集合
         *
         * @return 不可变集合
         */
        public Set<T> buildImmutableSet() {
            return Collections.unmodifiableSet(new HashSet<>(items));
        }
    }
} 