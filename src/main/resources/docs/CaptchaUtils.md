# CaptchaUtils 验证码工具类使用文档

`CaptchaUtils` 是一个功能强大的验证码生成工具类，支持多种类型的验证码生成，包括数字、字母、混合、算术和中文验证码，并提供了丰富的图片处理和输出功能。

## 主要功能

- 支持多种验证码类型：纯数字、纯字母、数字字母混合、算术运算、中文汉字
- 支持验证码图片自定义：背景色、干扰线、干扰点、字体、字符旋转等
- 支持多种输出格式：文件、输出流、Base64编码
- 支持多种图片格式：JPEG、PNG、GIF

## 使用方法

### 1. 生成验证码

#### 1.1 生成默认验证码

```java
// 生成默认验证码（4位数字字母混合，宽120，高40）
CaptchaUtils.CaptchaResult captcha = CaptchaUtils.generateDefault();
// 获取验证码文本
String text = captcha.getText();
// 获取验证码图片
BufferedImage image = captcha.getImage();
```

#### 1.2 生成不同类型的验证码

```java
// 生成6位纯数字验证码
CaptchaUtils.CaptchaResult numberCaptcha = CaptchaUtils.generateNumbers(6, 150, 50);

// 生成4位纯字母验证码
CaptchaUtils.CaptchaResult letterCaptcha = CaptchaUtils.generateLetters(4, 120, 40);

// 生成5位混合验证码
CaptchaUtils.CaptchaResult mixedCaptcha = CaptchaUtils.generateMixed(5, 150, 50);

// 生成算术运算验证码
CaptchaUtils.CaptchaResult arithmeticCaptcha = CaptchaUtils.generateArithmetic(150, 50);

// 生成4位中文验证码
CaptchaUtils.CaptchaResult chineseCaptcha = CaptchaUtils.generateChinese(4, 180, 60);
```

#### 1.3 通用验证码生成方法

```java
// 使用通用方法生成验证码，更加灵活
CaptchaUtils.CaptchaResult captcha = CaptchaUtils.generate(
    4,                                // 长度
    CaptchaUtils.CaptchaType.MIXED,   // 类型
    120,                              // 宽度
    40                                // 高度
);
```

### 2. 输出验证码

#### 2.1 转换为Base64

```java
// 转换为Base64格式（可直接用于前端img标签的src属性）
try {
    String base64 = captcha.toBase64(CaptchaUtils.ImageType.PNG);
    // 在HTML中使用: <img src="data:image/png;base64,iVBORw0KG...">
} catch (IOException e) {
    e.printStackTrace();
}
```

#### 2.2 保存为文件

```java
// 保存为PNG文件
try {
    captcha.toFile("/path/to/captcha.png", CaptchaUtils.ImageType.PNG);
} catch (IOException e) {
    e.printStackTrace();
}
```

#### 2.3 写入输出流

```java
// 写入HttpServletResponse的输出流
try {
    captcha.write(response.getOutputStream(), CaptchaUtils.ImageType.JPEG);
} catch (IOException e) {
    e.printStackTrace();
}

// 或写入任意输出流
try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
    captcha.write(baos, CaptchaUtils.ImageType.PNG);
    byte[] imageBytes = baos.toByteArray();
    // 进一步处理图片字节数据...
} catch (IOException e) {
    e.printStackTrace();
}
```

### 3. 验证用户输入

```java
// 用户输入验证
String userInput = "1234"; // 从用户表单获取
String captchaText = captcha.getText(); // 从会话或缓存中获取原始验证码

boolean isValid = CaptchaUtils.verify(userInput, captchaText);
if (isValid) {
    // 验证成功，继续处理
} else {
    // 验证失败，提示用户重新输入
}
```

### 4. 自定义验证码图片

如果需要更大程度的自定义，可以使用以下方法：

```java
// 先生成验证码文本
String captchaText = CaptchaUtils.generateText(6, CaptchaUtils.CaptchaType.MIXED);

// 然后生成图片
BufferedImage image = CaptchaUtils.generateImage(captchaText, 180, 60);

// 创建验证码结果对象
CaptchaUtils.CaptchaResult captcha = new CaptchaUtils.CaptchaResult(captchaText, image);
```

## API文档

### 主要类

1. `CaptchaUtils` - 验证码工具主类
2. `CaptchaUtils.CaptchaType` - 验证码类型枚举
3. `CaptchaUtils.ImageType` - 图片类型枚举
4. `CaptchaUtils.CaptchaResult` - 验证码结果类

### 枚举值

#### CaptchaType（验证码类型）

| 枚举值 | 说明 |
| ----- | ---- |
| NUMBERS | 纯数字验证码 |
| LETTERS | 纯字母验证码 |
| MIXED | 数字和字母混合验证码 |
| ARITHMETIC | 算术运算验证码 |
| CHINESE | 中文汉字验证码 |

#### ImageType（图片类型）

| 枚举值 | 值 | 说明 |
| ----- | --- | ---- |
| JPEG | "jpeg" | JPEG格式图片 |
| PNG | "png" | PNG格式图片 |
| GIF | "gif" | GIF格式图片 |

### 方法说明

#### CaptchaUtils类的方法

| 方法名 | 返回类型 | 参数 | 说明 |
| ----- | ------- | ---- | ---- |
| generateDefault() | CaptchaResult | 无 | 生成默认验证码（4位数字字母混合，宽120，高40） |
| generateNumbers(int length, int width, int height) | CaptchaResult | 长度，宽度，高度 | 生成纯数字验证码 |
| generateLetters(int length, int width, int height) | CaptchaResult | 长度，宽度，高度 | 生成纯字母验证码 |
| generateMixed(int length, int width, int height) | CaptchaResult | 长度，宽度，高度 | 生成数字字母混合验证码 |
| generateArithmetic(int width, int height) | CaptchaResult | 宽度，高度 | 生成算术运算验证码 |
| generateChinese(int length, int width, int height) | CaptchaResult | 长度，宽度，高度 | 生成中文汉字验证码 |
| generate(int length, CaptchaType type, int width, int height) | CaptchaResult | 长度，类型，宽度，高度 | 通用验证码生成方法 |
| generateText(int length, CaptchaType type) | String | 长度，类型 | 仅生成验证码文本 |
| generateImage(String text, int width, int height) | BufferedImage | 文本，宽度，高度 | 根据文本生成验证码图片 |
| verify(String inputText, String captchaText) | boolean | 输入文本，原始验证码文本 | 验证用户输入是否正确 |

#### CaptchaResult类的方法

| 方法名 | 返回类型 | 参数 | 说明 |
| ----- | ------- | ---- | ---- |
| getText() | String | 无 | 获取验证码文本 |
| getImage() | BufferedImage | 无 | 获取验证码图片 |
| write(OutputStream os, ImageType imageType) | void | 输出流，图片类型 | 将验证码图片写入输出流 |
| toFile(String filePath, ImageType imageType) | void | 文件路径，图片类型 | 将验证码图片保存到文件 |
| toBase64(ImageType imageType) | String | 图片类型 | 将验证码图片转换为Base64编码字符串 |

## 最佳实践

### 在Web应用中使用

1. **控制器方法**：

```java
@Controller
public class CaptchaController {
    
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 生成验证码
        CaptchaUtils.CaptchaResult captcha = CaptchaUtils.generateMixed(4, 120, 40);
        
        // 将验证码文本存入会话
        request.getSession().setAttribute("captchaText", captcha.getText());
        
        // 设置响应类型
        response.setContentType("image/png");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        
        // 输出图片
        captcha.write(response.getOutputStream(), CaptchaUtils.ImageType.PNG);
    }
    
    @PostMapping("/login")
    @ResponseBody
    public String login(String username, String password, String captcha, HttpServletRequest request) {
        // 从会话获取验证码
        String captchaText = (String) request.getSession().getAttribute("captchaText");
        
        // 验证验证码
        if (!CaptchaUtils.verify(captcha, captchaText)) {
            return "验证码错误";
        }
        
        // 继续登录流程...
        return "登录成功";
    }
}
```

2. **页面使用**：

```html
<!-- 显示验证码 -->
<div class="form-group">
    <label for="captcha">验证码</label>
    <div class="input-group">
        <input type="text" class="form-control" id="captcha" name="captcha" required>
        <div class="input-group-append">
            <img src="/captcha" alt="验证码" onclick="this.src='/captcha?t='+new Date().getTime()" style="cursor:pointer;">
        </div>
    </div>
    <small class="form-text text-muted">点击图片刷新验证码</small>
</div>
```

### 在RESTful API中使用

对于前后端分离的应用，可以返回Base64编码的验证码：

```java
@RestController
public class CaptchaApi {
    
    @GetMapping("/api/captcha")
    public Map<String, String> getCaptcha(HttpServletRequest request) throws IOException {
        // 生成验证码
        CaptchaUtils.CaptchaResult captcha = CaptchaUtils.generateMixed(4, 120, 40);
        
        // 将验证码文本存入会话或缓存系统
        String captchaKey = UUID.randomUUID().toString();
        request.getSession().setAttribute(captchaKey, captcha.getText());
        
        // 转为Base64并返回
        String base64 = captcha.toBase64(CaptchaUtils.ImageType.PNG);
        
        Map<String, String> result = new HashMap<>();
        result.put("key", captchaKey);
        result.put("image", base64);
        
        return result;
    }
    
    @PostMapping("/api/login")
    public Map<String, Object> login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        // 从会话或缓存系统获取验证码
        String captchaText = (String) request.getSession().getAttribute(loginDto.getCaptchaKey());
        
        // 验证验证码
        if (!CaptchaUtils.verify(loginDto.getCaptcha(), captchaText)) {
            // 验证码错误
            // ...
        }
        
        // 继续登录流程...
        // ...
    }
}
```

## 注意事项

1. 验证码文本应当安全地存储在服务器端（如会话或缓存系统中），并设置合理的有效期
2. 对于高安全性要求的场景，应搭配其他安全措施使用
3. 验证码使用后应立即失效，防止重复使用
4. 对于验证码图片的大小和复杂度，应在安全性和用户体验间取得平衡 