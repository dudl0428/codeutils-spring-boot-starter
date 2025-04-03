# StreamUtils 流处理工具类使用文档

`StreamUtils` 是一个功能强大的集合流处理工具类，简化了Java 8 Stream API的使用，提供了丰富的集合处理功能，包括过滤、映射、排序、分组、聚合、分页等操作的便捷方法。

## 主要功能

- 集合转换为流
- 流的过滤操作
- 流的映射转换
- 流的排序操作
- 流的分组聚合
- 分页和批处理
- 并行流处理

## 使用方法

### 1. 集合转换为流

```java
// 将List转换为Stream
List<User> userList = getUserList();
Stream<User> userStream = StreamUtils.toStream(userList);

// 将数组转换为Stream
User[] userArray = getUserArray();
Stream<User> arrayStream = StreamUtils.toStream(userArray);

// 将Map转换为Stream
Map<String, User> userMap = getUserMap();
Stream<Map.Entry<String, User>> entryStream = StreamUtils.toStream(userMap);

// 获取Map的键流
Stream<String> keyStream = StreamUtils.toKeyStream(userMap);

// 获取Map的值流
Stream<User> valueStream = StreamUtils.toValueStream(userMap);
```

### 2. 流的过滤操作

```java
// 过滤集合
List<User> adultUsers = StreamUtils.filter(userList, user -> user.getAge() >= 18);

// 过滤非空元素
List<String> nonEmptyNames = StreamUtils.filterNotEmpty(nameList);

// 过滤非null元素
List<User> nonNullUsers = StreamUtils.filterNotNull(userList);

// 过滤并限制数量
List<User> top5Users = StreamUtils.filterLimit(userList, user -> user.getAge() > 20, 5);

// 获取不重复元素
List<String> distinctNames = StreamUtils.distinct(nameList);
```

### 3. 流的映射转换

```java
// 映射转换
List<String> userNames = StreamUtils.map(userList, User::getName);

// 映射并过滤null结果
List<Address> addresses = StreamUtils.mapNotNull(userList, User::getAddress);

// 扁平化映射 (flatMap)
List<String> allTags = StreamUtils.flatMap(articleList, article -> article.getTags().stream());

// 转换为Map
Map<Long, User> userMap = StreamUtils.toMap(userList, User::getId);

// 带值映射的转换为Map
Map<Long, String> userNameMap = StreamUtils.toMap(userList, User::getId, User::getName);

// 允许重复键的转换为Map
Map<String, User> userByNameMap = StreamUtils.toMap(
    userList, 
    User::getName, 
    (existing, replacement) -> replacement  // 重复键时使用新值
);
```

### 4. 流的排序操作

```java
// 自然顺序排序
List<String> sortedNames = StreamUtils.sort(nameList);

// 指定比较器排序
List<User> sortedByAge = StreamUtils.sort(userList, Comparator.comparing(User::getAge));

// 按字段正序排序
List<User> sortedByNameAsc = StreamUtils.sortAsc(userList, User::getName);

// 按字段倒序排序
List<User> sortedByNameDesc = StreamUtils.sortDesc(userList, User::getName);

// 多字段排序
List<User> sortedUsers = StreamUtils.sort(userList, 
    StreamUtils.comparing(User::getGender)     // 先按性别排序
        .thenComparing(User::getAge)         // 再按年龄升序排序
        .thenComparing(User::getName, true)  // 最后按姓名降序排序
);
```

### 5. 流的分组聚合

```java
// 按字段分组
Map<Integer, List<User>> usersByAge = StreamUtils.groupBy(userList, User::getAge);

// 分组计数
Map<String, Long> cityUserCount = StreamUtils.groupCount(userList, user -> user.getAddress().getCity());

// 分组并映射值
Map<String, List<String>> cityUserNames = StreamUtils.groupBy(
    userList, 
    user -> user.getAddress().getCity(), 
    User::getName
);

// 计算统计数据
DoubleSummaryStatistics ageStats = StreamUtils.summarizing(userList, User::getAge);
double avgAge = ageStats.getAverage();
double maxAge = ageStats.getMax();

// 求和
int totalAge = StreamUtils.sum(userList, User::getAge);

// 平均值
double averageAge = StreamUtils.average(userList, User::getAge);

// 最大值
Optional<User> oldestUser = StreamUtils.max(userList, Comparator.comparing(User::getAge));

// 最小值
Optional<User> youngestUser = StreamUtils.min(userList, Comparator.comparing(User::getAge));
```

### 6. 分页和批处理

```java
// 分页 - 获取第2页，每页10条数据
List<User> pagedUsers = StreamUtils.page(userList, 1, 10);  // 页码从0开始

// 分批 - 每批50个元素
List<List<User>> batches = StreamUtils.batch(userList, 50);

// 处理大集合（避免一次性加载全部到内存）
StreamUtils.processBatch(largeList, 100, batch -> {
    // 批量处理逻辑
    batchService.processBatch(batch);
});
```

### 7. 并行流处理

```java
// 并行过滤
List<User> filteredParallel = StreamUtils.filterParallel(userList, user -> user.getAge() > 18);

// 并行映射
List<UserDTO> dtosParallel = StreamUtils.mapParallel(userList, user -> convertToDTO(user));

// 并行forEach操作
StreamUtils.forEachParallel(userList, user -> {
    // 处理每个用户
    processUser(user);
});
```

### 8. 其他实用方法

```java
// 判断是否所有元素都满足条件
boolean allAdults = StreamUtils.allMatch(userList, user -> user.getAge() >= 18);

// 判断是否存在元素满足条件
boolean hasAdmin = StreamUtils.anyMatch(userList, user -> "ADMIN".equals(user.getRole()));

// 判断是否没有元素满足条件
boolean noChildren = StreamUtils.noneMatch(userList, user -> user.getAge() < 18);

// 连接字符串
String names = StreamUtils.joining(userList, User::getName, ", ");

// 查找第一个匹配元素
Optional<User> admin = StreamUtils.findFirst(userList, user -> "ADMIN".equals(user.getRole()));

// 转换为集合
Set<String> nameSet = StreamUtils.toSet(StreamUtils.map(userList, User::getName));
```

## API文档

### 方法说明

#### 集合转流方法

| 方法名 | 返回类型 | 参数 | 说明 |
| ----- | ------- | ---- | ---- |
| toStream(Collection<T> collection) | Stream<T> | 集合 | 将集合转换为流 |
| toStream(T[] array) | Stream<T> | 数组 | 将数组转换为流 |
| toStream(Map<K, V> map) | Stream<Map.Entry<K, V>> | Map对象 | 将Map转换为Entry流 |
| toKeyStream(Map<K, V> map) | Stream<K> | Map对象 | 获取Map的键流 |
| toValueStream(Map<K, V> map) | Stream<V> | Map对象 | 获取Map的值流 |

#### 过滤相关方法

| 方法名 | 返回类型 | 参数 | 说明 |
| ----- | ------- | ---- | ---- |
| filter(Collection<T> collection, Predicate<T> predicate) | List<T> | 集合，断言函数 | 过滤集合中符合条件的元素 |
| filterParallel(Collection<T> collection, Predicate<T> predicate) | List<T> | 集合，断言函数 | 并行过滤集合中符合条件的元素 |
| filterNotNull(Collection<T> collection) | List<T> | 集合 | 过滤掉集合中的null元素 |
| filterNotEmpty(Collection<String> collection) | List<String> | 字符串集合 | 过滤掉集合中的空字符串元素 |
| filterLimit(Collection<T> collection, Predicate<T> predicate, long limit) | List<T> | 集合，断言函数，限制数量 | 过滤集合并限制结果数量 |
| distinct(Collection<T> collection) | List<T> | 集合 | 获取集合中的不重复元素 |

#### 映射相关方法

| 方法名 | 返回类型 | 参数 | 说明 |
| ----- | ------- | ---- | ---- |
| map(Collection<T> collection, Function<T, R> mapper) | List<R> | 集合，映射函数 | 将集合中的元素映射为另一种类型 |
| mapParallel(Collection<T> collection, Function<T, R> mapper) | List<R> | 集合，映射函数 | 并行将集合中的元素映射为另一种类型 |
| mapNotNull(Collection<T> collection, Function<T, R> mapper) | List<R> | 集合，映射函数 | 映射并过滤掉null结果 |
| flatMap(Collection<T> collection, Function<T, Stream<R>> mapper) | List<R> | 集合，流映射函数 | 扁平化映射集合 |

#### 转换为Map方法

| 方法名 | 返回类型 | 参数 | 说明 |
| ----- | ------- | ---- | ---- |
| toMap(Collection<T> collection, Function<T, K> keyMapper) | Map<K, T> | 集合，键映射函数 | 将集合转换为Map |
| toMap(Collection<T> collection, Function<T, K> keyMapper, Function<T, V> valueMapper) | Map<K, V> | 集合，键映射函数，值映射函数 | 将集合转换为Map并映射值 |
| toMap(Collection<T> collection, Function<T, K> keyMapper, BinaryOperator<T> mergeFunction) | Map<K, T> | 集合，键映射函数，合并函数 | 将集合转换为Map，处理重复键 |
| toMap(Collection<T> collection, Function<T, K> keyMapper, Function<T, V> valueMapper, BinaryOperator<V> mergeFunction) | Map<K, V> | 集合，键映射函数，值映射函数，合并函数 | 将集合转换为Map，映射值并处理重复键 |

#### 排序相关方法

| 方法名 | 返回类型 | 参数 | 说明 |
| ----- | ------- | ---- | ---- |
| sort(Collection<T> collection) | List<T> | 集合 | 按自然顺序排序（元素需实现Comparable接口） |
| sort(Collection<T> collection, Comparator<? super T> comparator) | List<T> | 集合，比较器 | 按指定比较器排序 |
| sortAsc(Collection<T> collection, Function<T, Comparable<?>> keyExtractor) | List<T> | 集合，键提取器 | 按指定字段升序排序 |
| sortDesc(Collection<T> collection, Function<T, Comparable<?>> keyExtractor) | List<T> | 集合，键提取器 | 按指定字段降序排序 |

#### 分组聚合方法

| 方法名 | 返回类型 | 参数 | 说明 |
| ----- | ------- | ---- | ---- |
| groupBy(Collection<T> collection, Function<T, K> classifier) | Map<K, List<T>> | 集合，分类函数 | 按指定字段分组 |
| groupBy(Collection<T> collection, Function<T, K> classifier, Function<T, V> valueMapper) | Map<K, List<V>> | 集合，分类函数，值映射函数 | 按指定字段分组并映射值 |
| groupCount(Collection<T> collection, Function<T, K> classifier) | Map<K, Long> | 集合，分类函数 | 按指定字段分组并计数 |
| summarizing(Collection<T> collection, ToDoubleFunction<T> valueExtractor) | DoubleSummaryStatistics | 集合，值提取函数 | 计算集合的统计数据 |
| sum(Collection<T> collection, ToIntFunction<T> valueExtractor) | int | 集合，值提取函数 | 计算整数值的总和 |
| sum(Collection<T> collection, ToLongFunction<T> valueExtractor) | long | 集合，值提取函数 | 计算长整数值的总和 |
| sum(Collection<T> collection, ToDoubleFunction<T> valueExtractor) | double | 集合，值提取函数 | 计算浮点数值的总和 |
| average(Collection<T> collection, ToDoubleFunction<T> valueExtractor) | double | 集合，值提取函数 | 计算平均值 |
| max(Collection<T> collection, Comparator<? super T> comparator) | Optional<T> | 集合，比较器 | 获取最大值 |
| min(Collection<T> collection, Comparator<? super T> comparator) | Optional<T> | 集合，比较器 | 获取最小值 |

#### 分页和批处理方法

| 方法名 | 返回类型 | 参数 | 说明 |
| ----- | ------- | ---- | ---- |
| page(Collection<T> collection, int pageNumber, int pageSize) | List<T> | 集合，页码，每页大小 | 对集合进行分页 |
| batch(Collection<T> collection, int batchSize) | List<List<T>> | 集合，批次大小 | 将集合拆分成多个批次 |
| processBatch(Collection<T> collection, int batchSize, Consumer<List<T>> batchProcessor) | void | 集合，批次大小，批处理函数 | 分批处理集合 |

#### 其他实用方法

| 方法名 | 返回类型 | 参数 | 说明 |
| ----- | ------- | ---- | ---- |
| allMatch(Collection<T> collection, Predicate<T> predicate) | boolean | 集合，断言函数 | 判断是否所有元素都满足条件 |
| anyMatch(Collection<T> collection, Predicate<T> predicate) | boolean | 集合，断言函数 | 判断是否存在元素满足条件 |
| noneMatch(Collection<T> collection, Predicate<T> predicate) | boolean | 集合，断言函数 | 判断是否没有元素满足条件 |
| joining(Collection<T> collection, Function<T, String> mapper, String delimiter) | String | 集合，映射函数，分隔符 | 连接字符串 |
| findFirst(Collection<T> collection, Predicate<T> predicate) | Optional<T> | 集合，断言函数 | 查找第一个匹配元素 |
| toSet(Collection<T> collection) | Set<T> | 集合 | 将集合转换为Set |
| toList(Collection<T> collection) | List<T> | 集合 | 将集合转换为List |

## 使用示例

### 用户数据处理示例

```java
public class UserService {
    public List<UserDTO> findActiveAdminUsers() {
        List<User> allUsers = userRepository.findAll();
        
        // 传统方式
        List<UserDTO> result = new ArrayList<>();
        for (User user : allUsers) {
            if (user.isActive() && "ADMIN".equals(user.getRole())) {
                result.add(convertToDTO(user));
            }
        }
        
        // 使用StreamUtils简化
        return StreamUtils.map(
            StreamUtils.filter(allUsers, 
                user -> user.isActive() && "ADMIN".equals(user.getRole())
            ),
            this::convertToDTO
        );
    }
    
    public Map<String, List<String>> getUserNamesByDepartment() {
        List<User> allUsers = userRepository.findAll();
        
        // 使用StreamUtils分组
        return StreamUtils.groupBy(
            allUsers,
            User::getDepartment,  // 按部门分组
            User::getName         // 提取用户名
        );
    }
    
    public List<User> findTopPerformers(int limit) {
        List<User> allUsers = userRepository.findAll();
        
        // 按绩效评分排序并限制数量
        return StreamUtils.sort(
            allUsers,
            StreamUtils.comparing(User::getPerformanceScore).reversed()
        ).subList(0, Math.min(limit, allUsers.size()));
    }
}
```

### 数据转换和聚合示例

```java
public class OrderAnalysisService {
    public Map<String, Double> calculateTotalAmountByCategory() {
        List<Order> orders = orderRepository.findAll();
        
        // 按商品类别分组并计算总金额
        return orders.stream()
            .flatMap(order -> order.getItems().stream())
            .collect(Collectors.groupingBy(
                OrderItem::getCategory,
                Collectors.summingDouble(OrderItem::getAmount)
            ));
        
        // 使用StreamUtils简化
        return StreamUtils.flatMap(orders, order -> order.getItems().stream())
            .collect(Collectors.groupingBy(
                OrderItem::getCategory,
                Collectors.summingDouble(OrderItem::getAmount)
            ));
    }
    
    public DoubleSummaryStatistics getOrderStatistics() {
        List<Order> orders = orderRepository.findAll();
        
        // 获取订单金额的统计数据
        return StreamUtils.summarizing(orders, Order::getTotalAmount);
    }
}
```

### 分页和批处理示例

```java
public class DataExportService {
    public void exportLargeData() {
        List<DataRecord> allRecords = dataRepository.findAll();
        
        // 分批处理大量数据
        StreamUtils.processBatch(allRecords, 1000, batch -> {
            exportBatch(batch);
            log.info("Processed batch of " + batch.size() + " records");
        });
    }
    
    public List<List<User>> prepareEmailBatches(int batchSize) {
        List<User> subscribers = userRepository.findAllSubscribers();
        
        // 将用户分成多个批次，用于批量发送邮件
        return StreamUtils.batch(subscribers, batchSize);
    }
}
```

## 最佳实践

### 在业务逻辑中使用

```java
public class ReportService {
    public Map<String, Object> generateDashboardData() {
        List<User> users = userRepository.findAll();
        List<Order> orders = orderRepository.findAll();
        
        Map<String, Object> result = new HashMap<>();
        
        // 用户统计
        result.put("totalUsers", users.size());
        result.put("activeUsers", StreamUtils.filter(users, User::isActive).size());
        result.put("usersByRole", StreamUtils.groupCount(users, User::getRole));
        
        // 订单统计
        result.put("totalOrders", orders.size());
        result.put("totalRevenue", StreamUtils.sum(orders, Order::getAmount));
        result.put("averageOrderValue", StreamUtils.average(orders, Order::getAmount));
        
        // 最近订单
        result.put("recentOrders", StreamUtils.sort(orders, 
            StreamUtils.comparing(Order::getCreatedAt).reversed())
            .subList(0, Math.min(5, orders.size()))
        );
        
        return result;
    }
}
```

### 在数据转换层使用

```java
public class DataConversionService {
    public List<ApiResponse> prepareApiResponse(List<DataEntity> entities) {
        return StreamUtils.map(entities, entity -> {
            ApiResponse response = new ApiResponse();
            response.setId(entity.getId());
            response.setName(entity.getName());
            response.setStatus(convertStatus(entity.getStatus()));
            response.setMetadata(convertMetadata(entity.getMetadata()));
            return response;
        });
    }
    
    public <T> PageResponse<T> createPageResponse(List<T> data, int page, int size, long total) {
        PageResponse<T> response = new PageResponse<>();
        response.setData(StreamUtils.page(data, page, size));
        response.setPage(page);
        response.setSize(size);
        response.setTotal(total);
        response.setTotalPages((int) Math.ceil((double) total / size));
        return response;
    }
}
```

## 注意事项

1. 对于大数据量集合，优先考虑使用并行流方法提高性能
2. 并行流操作可能会引入线程安全问题，确保操作是无状态和线程安全的
3. 分页方法返回的是子列表视图，修改会影响原集合，如需独立副本，请使用 `new ArrayList<>(result)`
4. 批处理方法适合处理大数据集，有助于控制内存使用
5. 避免在循环中多次调用流操作，应尽量在一个流操作链中完成所有处理
6. 对于复杂的多级分组和聚合操作，可能需要组合使用Java原生Stream API和StreamUtils 