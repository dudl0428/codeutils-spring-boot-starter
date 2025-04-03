# CodeUtils Starter 发布指南

本文档介绍如何将 codeutils-spring-boot-starter 打包并发布到 GitHub Packages，使其可以在其他项目中通过 Maven 依赖引入。

## 1. 打包项目

首先，确保项目能够正确编译并打包：

```bash
# 进入项目目录
cd codeutils-spring-boot-starter

# 编译并打包项目
mvn clean package

# 安装到本地Maven仓库（可选，用于本地测试）
mvn clean install
```

成功执行后，将在 `target` 目录下生成 JAR 文件：`codeutils-spring-boot-starter-1.0.0.jar`。

## 2. 创建 GitHub 仓库

如果还没有 GitHub 仓库，需要先创建一个：

1. 登录 GitHub 账号
2. 点击右上角 "+" 图标，选择 "New repository"
3. 填写仓库名称（如 "codeutils-spring-boot-starter"）
4. 添加描述（可选）
5. 选择仓库可见性（公开或私有）
6. 创建仓库

## 3. 上传项目到 GitHub

将本地项目上传到 GitHub 仓库：

```bash
# 初始化 Git 仓库（如果尚未初始化）
git init

# 添加所有文件到 Git 暂存区
git add .

# 提交更改
git commit -m "Initial commit"

# 添加远程仓库
git remote add origin https://github.com/你的用户名/codeutils-spring-boot-starter.git

# 推送到远程仓库
git push -u origin master
```

## 4. 配置 GitHub Packages

### 4.1 配置 Maven POM 文件

修改 `pom.xml` 文件，添加分发管理配置：

```xml
<distributionManagement>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/你的用户名/codeutils-spring-boot-starter</url>
    </repository>
</distributionManagement>
```

### 4.2 配置 Maven settings.xml

在你的 Maven `settings.xml` 文件中（通常位于 `~/.m2/settings.xml`）添加认证信息：

```xml
<servers>
    <server>
        <id>github</id>
        <username>你的GitHub用户名</username>
        <password>你的GitHub个人访问令牌</password>
    </server>
</servers>
```

注意：这里的密码不是你的 GitHub 密码，而是需要创建的个人访问令牌（Personal Access Token）。

### 4.3 生成 GitHub 个人访问令牌

1. 登录 GitHub 账号
2. 点击右上角头像，选择 "Settings"
3. 在左侧菜单中，选择 "Developer settings"
4. 点击 "Personal access tokens"，然后 "Generate new token"
5. 为令牌添加描述
6. 选择作用域（至少需要选择 `read:packages` 和 `write:packages`）
7. 点击 "Generate token"
8. 复制生成的令牌（注意：这是你唯一能看到令牌的机会）

## 5. 发布项目到 GitHub Packages

使用以下命令将项目发布到 GitHub Packages：

```bash
mvn deploy
```

## 6. 在其他项目中引用

在其他项目中，需要执行以下步骤来引用这个库：

### 6.1 配置 GitHub Packages 仓库

在项目的 `pom.xml` 文件中添加仓库配置：

```xml
<repositories>
    <repository>
        <id>github</id>
        <name>GitHub 用户名 Apache Maven Packages</name>
        <url>https://maven.pkg.github.com/你的用户名/codeutils-spring-boot-starter</url>
    </repository>
</repositories>
```

### 6.2 配置认证信息

确保在 Maven `settings.xml` 文件中也添加了相应的认证信息（同上述步骤 4.2）。

### 6.3 添加依赖

在项目的 `pom.xml` 文件中添加依赖：

```xml
<dependency>
    <groupId>com.codeutils</groupId>
    <artifactId>codeutils-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 7. 使用 JitPack 作为替代方案（更简单）

如果你觉得上述过程较为复杂，可以考虑使用 JitPack，这是一个更简单的方法：

1. 将项目上传到 GitHub
2. 在 GitHub 上创建一个发布版本（Release）
3. 在其他项目中，添加 JitPack 仓库：

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

4. 添加依赖：

```xml
<dependency>
    <groupId>com.github.你的用户名</groupId>
    <artifactId>codeutils-spring-boot-starter</artifactId>
    <version>发布版本标签，如 v1.0.0</version>
</dependency>
```

使用 JitPack 无需配置认证信息，只要 GitHub 仓库是公开的即可。

## 注意事项

1. 确保你的 pom.xml 文件中的 groupId、artifactId 和 version 是正确的。
2. 如果是私有仓库，确保有适当的访问权限。
3. 依赖发布后，可能需要一段时间才能在 Maven 中央仓库或 GitHub Packages 中可用。
4. 定期更新个人访问令牌以保持安全。 