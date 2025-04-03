# ObjectUtils 对象工具类使用文档

`ObjectUtils` 是一个功能强大的对象处理工具类，提供了多种对象操作功能，包括对象判空、深拷贝、对象比较、属性操作以及对象流式处理等功能。

## 主要功能

- 对象空值判断
- 对象深拷贝
- 对象比较
- 对象属性操作
- 反射方法调用
- 对象流式操作

## 使用方法

### 1. 对象空值判断

```java
// 判断对象是否为空（null、空字符串、空集合、空数组等）
boolean isEmpty = ObjectUtils.isEmpty(obj);

// 判断对象是否不为空
boolean isNotEmpty = ObjectUtils.isNotEmpty(obj);

// 判断对象是否为null
boolean isNull = ObjectUtils.isNull(obj);

// 判断对象是否不为null
boolean isNotNull = ObjectUtils.isNotNull(obj);

// 获取非空对象，如果为空则返回默认值
String value = ObjectUtils.defaultIfNull(str, "默认值");
```

### 2. 对象深拷贝

```java
// 深拷贝对象（支持各种复杂对象、集合、Map等）
User copy = ObjectUtils.deepCopy(originalUser);

// 使用序列化方式进行深拷贝（对象需实现Serializable接口）
User copiedUser = ObjectUtils.deepCopyBySerialization(originalUser);
```

### 3. 对象比较

```java
// 比较两个对象是否相等（比Java的equals更强大，支持深层比较）
boolean isEqual = ObjectUtils.equals(obj1, obj2);

// 比较两个对象，返回差异属性
Map<String, Object[]> diff = ObjectUtils.difference(obj1, obj2);
```

### 4. 对象属性操作

```java
// 获取对象的属性值
String name = ObjectUtils.getProperty(user, "name");

// 设置对象的属性值
ObjectUtils.setProperty(user, "name", "张三");

// 拷贝对象属性
ObjectUtils.copyProperties(source, target);

// 拷贝指定属性
ObjectUtils.copyProperties(source, target, "name", "age");

// 排除指定属性进行拷贝
ObjectUtils.copyPropertiesExclude(source, target, "password", "salt");
```

### 5. 反射方法调用

```java
// 调用对象的方法
Object result = ObjectUtils.invokeMethod(obj, "methodName", arg1, arg2);

// 调用对象的方法（指定参数类型）
Object result = ObjectUtils.invokeMethod(obj, "methodName", new Class[]{String.class, int.class}, "参数1", 100);

// 调用静态方法
Object result = ObjectUtils.invokeStaticMethod(User.class, "staticMethod", args);
```

### 6. 对象流式操作

`ObjectUtils` 提供了类似 Java 8 Stream 的对象流式处理功能，让对象操作更加简洁优雅：

```java
// 创建对象流
ObjectUtils.ObjectStream<User> stream = ObjectUtils.stream(user);

// 对象流操作示例
String result = ObjectUtils.stream(user)
    .filter(u -> u != null && u.getAge() > 18)   // 过滤
    .map(User::getName)                          // 映射转换
    .orElse("Unknown");                          // 获取结果或默认值

// 对象属性转换流式操作
Integer age = ObjectUtils.stream(user)
    .map(u -> u.getAge())
    .filter(a -> a > 0)
    .orElse(0);
    
// 条件执行
ObjectUtils.stream(user)
    .filter(u -> u.getAge() > 18)
    .ifPresent(u -> System.out.println("成年用户：" + u.getName()));
    
// 获取结果
User result = ObjectUtils.stream(users)
    .filter(u -> "张三".equals(u.getName()))
    .orElseThrow(() -> new RuntimeException("用户不存在"));
```

### 7. 类型转换

```java
// 类型安全的转换
Integer value = ObjectUtils.cast(obj, Integer.class);

// 带默认值的转换
int num = ObjectUtils.toInt(obj, 0);
long longVal = ObjectUtils.toLong(obj, 0L);
double doubleVal = ObjectUtils.toDouble(obj, 0.0);
boolean boolVal = ObjectUtils.toBoolean(obj, false);
String strVal = ObjectUtils.toString(obj, "");
```

## API文档

### 主要类和接口

1. `ObjectUtils` - 对象工具主类
2. `ObjectUtils.ObjectStream<T>` - 对象流接口，提供链式操作

### 方法说明

#### 判空相关方法

| 方法名 | 返回类型 | 参数 | 说明 |
| ----- | ------- | ---- | ---- |
| isEmpty(Object obj) | boolean | 对象 | 判断对象是否为空（null、空字符串、空集合等） |
| isNotEmpty(Object obj) | boolean | 对象 | 判断对象是否不为空 |
| isNull(Object obj) | boolean | 对象 | 判断对象是否为null |
| isNotNull(Object obj) | boolean | 对象 | 判断对象是否不为null |
| defaultIfNull(T obj, T defaultValue) | T | 对象，默认值 | 如果对象为null则返回默认值，否则返回对象本身 |
| firstNonNull(T... objs) | T | 对象数组 | 返回第一个非null的对象 |

#### 拷贝相关方法

| 方法名 | 返回类型 | 参数 | 说明 |
| ----- | ------- | ---- | ---- |
| deepCopy(T obj) | T | 对象 | 对象深拷贝 |
| deepCopyBySerialization(T obj) | T | 对象 | 通过序列化方式进行深拷贝 |
| copyProperties(Object source, Object target) | void | 源对象，目标对象 | 复制对象属性 |
| copyProperties(Object source, Object target, String... includeProperties) | void | 源对象，目标对象，包含的属性列表 | 复制指定属性 |
| copyPropertiesExclude(Object source, Object target, String... excludeProperties) | void | 源对象，目标对象，排除的属性列表 | 复制除了指定属性外的所有属性 |

#### 比较相关方法

| 方法名 | 返回类型 | 参数 | 说明 |
| ----- | ------- | ---- | ---- |
| equals(Object obj1, Object obj2) | boolean | 对象1，对象2 | 比较两个对象是否相等 |
| difference(Object obj1, Object obj2) | Map<String, Object[]> | 对象1，对象2 | 比较两个对象的差异，返回不同的属性名及其值 |
| hashCode(Object obj) | int | 对象 | 获取对象的哈希码 |

#### 反射相关方法

| 方法名 | 返回类型 | 参数 | 说明 |
| ----- | ------- | ---- | ---- |
| getProperty(Object obj, String propertyName) | Object | 对象，属性名 | 获取对象的属性值 |
| setProperty(Object obj, String propertyName, Object value) | void | 对象，属性名，属性值 | 设置对象的属性值 |
| invokeMethod(Object obj, String methodName, Object... args) | Object | 对象，方法名，参数列表 | 调用对象的方法 |
| invokeMethod(Object obj, String methodName, Class<?>[] paramTypes, Object... args) | Object | 对象，方法名，参数类型数组，参数列表 | 调用对象的方法（指定参数类型） |
| invokeStaticMethod(Class<?> clazz, String methodName, Object... args) | Object | 类，方法名，参数列表 | 调用静态方法 |

#### 类型转换方法

| 方法名 | 返回类型 | 参数 | 说明 |
| ----- | ------- | ---- | ---- |
| cast(Object obj, Class<T> type) | T | 对象，目标类型 | 类型安全的强制转换 |
| toInt(Object value, int defaultValue) | int | 对象，默认值 | 转换为int类型，失败返回默认值 |
| toLong(Object value, long defaultValue) | long | 对象，默认值 | 转换为long类型，失败返回默认值 |
| toDouble(Object value, double defaultValue) | double | 对象，默认值 | 转换为double类型，失败返回默认值 |
| toBoolean(Object value, boolean defaultValue) | boolean | 对象，默认值 | 转换为boolean类型，失败返回默认值 |
| toString(Object obj, String defaultValue) | String | 对象，默认值 | 转换为String类型，失败返回默认值 |

#### 对象流方法

| 方法名 | 返回类型 | 参数 | 说明 |
| ----- | ------- | ---- | ---- |
| stream(T obj) | ObjectStream<T> | 对象 | 创建对象流 |

#### ObjectStream<T>接口方法

| 方法名 | 返回类型 | 参数 | 说明 |
| ----- | ------- | ---- | ---- |
| filter(Predicate<T> predicate) | ObjectStream<T> | 断言函数 | 根据条件过滤对象 |
| map(Function<T, R> mapper) | ObjectStream<R> | 映射函数 | 将对象映射为其他类型 |
| ifPresent(Consumer<T> consumer) | void | 消费函数 | 如果对象非空则执行操作 |
| orElse(T other) | T | 默认值 | 如果对象为空返回默认值，否则返回对象本身 |
| orElseGet(Supplier<T> supplier) | T | 提供者函数 | 如果对象为空则通过提供者函数获取默认值 |
| orElseThrow(Supplier<X> exceptionSupplier) | T | 异常提供者函数 | 如果对象为空则抛出指定异常 |

## 使用示例

### 对象深拷贝

```java
// 实体类
public class User implements Serializable {
    private String name;
    private int age;
    private List<Address> addresses;
    
    // 省略getter/setter
}

// 深拷贝示例
User user = new User();
user.setName("张三");
user.setAge(30);
List<Address> addresses = new ArrayList<>();
addresses.add(new Address("北京市", "朝阳区"));
user.setAddresses(addresses);

// 进行深拷贝
User copy = ObjectUtils.deepCopy(user);

// 修改原对象不会影响拷贝对象
user.setName("李四");
user.getAddresses().get(0).setCity("上海市");

System.out.println(copy.getName());  // 输出: 张三
System.out.println(copy.getAddresses().get(0).getCity());  // 输出: 北京市
```

### 对象流式操作

```java
public class UserService {
    public void processUser(User user) {
        // 传统写法
        if (user != null && user.getAge() >= 18) {
            System.out.println("成年用户: " + user.getName());
        } else {
            System.out.println("未成年用户或用户不存在");
        }
        
        // 使用ObjectStream的流式写法
        ObjectUtils.stream(user)
            .filter(u -> u.getAge() >= 18)
            .map(User::getName)
            .map(name -> "成年用户: " + name)
            .ifPresent(System.out::println)
            .orElse("未成年用户或用户不存在");
    }
}
```

### 反射方法调用

```java
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
    
    public static String format(double value) {
        return String.format("%.2f", value);
    }
}

// 调用实例方法
Calculator calc = new Calculator();
Integer result = (Integer) ObjectUtils.invokeMethod(calc, "add", 10, 20);
System.out.println(result);  // 输出: 30

// 调用静态方法
String formatted = (String) ObjectUtils.invokeStaticMethod(Calculator.class, "format", 12.3456);
System.out.println(formatted);  // 输出: 12.35
```

## 最佳实践

### 在业务逻辑中安全处理对象

```java
public class UserService {
    public String getUserName(User user) {
        // 传统写法
        if (user == null) {
            return "Unknown";
        }
        String name = user.getName();
        if (name == null || name.isEmpty()) {
            return "Unnamed";
        }
        return name;
        
        // 使用ObjectUtils简化写法
        return ObjectUtils.stream(user)
            .map(User::getName)
            .filter(name -> !ObjectUtils.isEmpty(name))
            .orElse("Unknown");
    }
}
```

### 在数据转换中使用

```java
public class DataConverter {
    public UserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserDTO dto = new UserDTO();
        // 复制所有属性
        ObjectUtils.copyProperties(user, dto);
        
        // 或者复制指定属性
        ObjectUtils.copyProperties(user, dto, "id", "name", "email");
        
        // 或者排除特定属性
        ObjectUtils.copyPropertiesExclude(user, dto, "password", "salt");
        
        return dto;
    }
}
```

## 注意事项

1. 深拷贝方法对于循环引用的对象可能会导致栈溢出，使用时需注意
2. 反射方法在性能敏感场景下应谨慎使用
3. 对于大量数据的处理，建议使用Java 8的Stream API而不是ObjectStream
4. 类型转换方法尽量提供默认值，避免转换异常
5. 在多线程环境中操作共享对象时需注意线程安全问题 