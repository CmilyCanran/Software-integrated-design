# 01-Spring Boot项目环境准备

> **学习目标**：搭建Spring Boot开发环境，为用户认证系统后端开发做准备

## 🎯 本章概览

| 内容 | 预计时间 | 难度 | 状态 |
|------|----------|------|------|
| Spring Boot项目创建 | 20分钟 | ⭐⭐ | ⏳ |
| 依赖配置 | 15分钟 | ⭐⭐ | ⏳ |
| 项目结构理解 | 10分钟 | ⭐ | ⏳ |
| 数据库配置 | 15分钟 | ⭐⭐ | ⏳ |

---

## 📚 核心概念预习

### 🏗️ Spring Boot基础概念
在开始实际开发之前，先了解几个核心概念：

#### 🔹 控制反转（IoC）
- **什么是IoC**：控制权的转移，由容器管理对象的生命周期
- **IoC容器**：负责创建、配置和管理Bean对象
- **优势**：降低耦合度，提高代码的可测试性和可维护性

#### 🔹 依赖注入（DI）
- **什么是DI**：容器负责将依赖对象注入到需要它的类中
- **注入方式**：构造函数注入、字段注入、Setter注入
- **推荐方式**：构造函数注入（推荐）

#### 🔹 Bean对象
- **什么是Bean**：由Spring容器管理的对象
- **Bean生命周期**：实例化→属性赋值→初始化→使用→销毁
- **Bean作用域**：singleton（单例）、prototype（原型）等

#### 🔹 自动配置
- **什么是自动配置**：Spring Boot根据类路径自动配置应用
- **条件注解**：@Conditional系列注解控制配置的生效条件
- **Starter机制**：简化依赖引入和配置

---

## 📋 环境要求

### 必需软件
- **Java 17+** - Spring Boot 3.x需要Java 17或更高版本
- **Maven 3.6+** - 项目构建和依赖管理
- **MySQL 8.0+** - 数据库服务器
- **IDE** - 推荐IntelliJ IDEA

### 检查环境
```bash
# 检查Java版本
java -version

# 检查Maven版本
mvn -version

# 检查MySQL服务
mysql --version
```

> **提示**：如果Java版本低于17，请先升级到Java 17或更高版本

---

## 🚀 创建Spring Boot项目

### 第一步：使用Spring Initializr
访问 [https://start.spring.io/](https://start.spring.io/) 创建项目

### 第二步：项目配置
```
Project: Maven
Language: Java
Spring Boot: 3.2.x
Project Metadata:
  Group: com.cmliy
  Artifact: springweb
  Name: springweb
  Package name: com.cmliy.springweb
  Packaging: Jar
  Java: 17

Dependencies:
  □ Spring Web
  □ Spring Data JPA
  □ MySQL Driver
  □ Spring Security
  □ Spring Boot DevTools
```

### 第三步：下载和导入
1. 点击"GENERATE"下载项目
2. 解压项目到工作目录
3. 使用IntelliJ IDEA导入Maven项目

---

## 📦 项目依赖配置

### 修改 `pom.xml` 添加必要依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>

    <groupId>com.cmliy</groupId>
    <artifactId>springweb</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>springweb</name>
    <description>Spring Boot Web Application for Clothing Store</description>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <!-- Spring Web Starter -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Data JPA -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- MySQL Driver -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Spring Security -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <!-- JWT Support -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.11.5</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>

        <!-- Spring Boot DevTools -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>

        <!-- Spring Boot Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### 依赖说明
| 依赖包 | 用途 | 为什么需要 |
|--------|------|------------|
| spring-boot-starter-web | Web开发 | 提供RESTful API开发支持 |
| spring-boot-starter-data-jpa | 数据访问 | 简化JPA使用，提供Repository抽象 |
| mysql-connector-j | MySQL驱动 | 连接MySQL数据库 |
| spring-boot-starter-security | 安全框架 | 提供认证和授权功能 |
| jjwt-api/impl/jackson | JWT令牌 | 实现无状态认证 |
| spring-boot-devtools | 开发工具 | 热部署、自动重启等 |

---

## 🏗️ 项目结构理解

### 创建后的项目结构
```
springweb/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── cmliy/
│   │   │           └── springweb/
│   │   │               ├── SpringWebApplication.java  # 主启动类
│   │   │               ├── controller/               # 控制器层
│   │   │               ├── service/                  # 服务层
│   │   │               ├── model/                    # 实体类
│   │   │               ├── repository/               # 数据访问层
│   │   │               ├── config/                   # 配置类
│   │   │               └── security/                 # 安全配置
│   │   └── resources/
│   │       ├── application.properties            # 应用配置
│   │       ├── application-dev.properties          # 开发环境配置
│   │       └── application-prod.properties         # 生产环境配置
│   └── test/                                     # 测试代码
├── pom.xml                                        # Maven配置
└── README.md                                     # 项目说明
```

### 关键文件说明
- **`SpringWebApplication.java`** - Spring Boot应用入口，包含main方法
- **`controller/`** - 处理HTTP请求的控制器，定义API端点
- **`service/`** - 业务逻辑处理，实现核心功能
- **`model/`** - 数据实体类，对应数据库表结构
- **`repository/`** - 数据访问接口，继承JPA Repository
- **`config/`** - 配置类，包含各种配置信息
- **`security/`** - 安全相关配置，包含认证和授权逻辑

---

## 🗄️ 数据库配置

### 创建数据库
```sql
-- 创建数据库
CREATE DATABASE clothing_store DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE clothing_store;

-- 创建用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 插入测试数据
INSERT INTO users (username, email, password, role) VALUES
('admin', 'admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN'),
('user', 'user@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER');
```

### 配置数据库连接

#### 修改 `src/main/resources/application.properties`
```properties
# 服务器配置
server.port=8080
server.servlet.context-path=/api

# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/clothing_store?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# JWT配置
jwt.secret=mySecretKey123456789012345678901234567890
jwt.expiration=86400000

# CORS配置
spring.web.cors.allowed-origins=http://localhost:5173
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=true
```

---

## 🧪 验证环境配置

### 第一步：启动应用
```bash
# 在项目根目录下执行
mvn spring-boot:run
```

### 第二步：验证启动
- 访问 `http://localhost:8080/api`
- 应该看到Whitelabel Error Page（这是正常的，因为我们还没有创建控制器）

### 第三步：检查数据库连接
- 查看控制台日志，确认数据库连接成功
- 检查数据表是否自动创建

---

## 📝 本章小结

### ✅ 完成内容
- [x] 创建Spring Boot项目
- [x] 配置项目依赖
- [x] 创建数据库和表
- [x] 配置数据库连接
- [x] 验证环境配置

### 🎯 掌握技能
- Spring Boot项目创建流程
- Maven依赖管理
- 数据库配置和连接
- 项目结构理解
- IoC和DI基础概念

### 🚀 下一步
后端环境已准备完成，下一章我们将学习Spring Boot核心概念，深入理解IoC容器和依赖注入机制。

---

## ❓ 常见问题

### Q1: Maven依赖下载很慢怎么办？
**A**: 可以配置国内镜像源，在 `~/.m2/settings.xml` 中添加：
```xml
<mirror>
  <id>aliyun</id>
  <mirrorOf>central</mirrorOf>
  <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```

### Q2: 数据库连接失败怎么办？
**A**: 检查以下几点：
1. MySQL服务是否启动
2. 数据库用户名密码是否正确
3. 数据库是否存在
4. 防火墙是否阻止连接

### Q3: 项目启动失败怎么办？
**A**: 检查以下几点：
1. Java版本是否满足要求（需要Java 17+）
2. Maven依赖是否正确下载
3. 端口8080是否被占用

---

**恭喜！您已经成功搭建了Spring Boot开发环境。** 🎉

**下一章：[02-Spring Boot核心概念学习](02-Spring-Boot核心概念学习.md)**

## 🛠️ 必需软件清单

### 1. Node.js (前端开发必需)

**为什么需要？**
- Vue.js 项目基于 Node.js 运行
- npm 包管理器用于安装依赖
- 提供开发服务器和构建工具

**安装步骤：**

1. 访问 [Node.js 官网](https://nodejs.org/)
2. 下载 **LTS 版本** (推荐 18.x 或 20.x)
3. 运行安装程序，按默认设置安装
4. 重启命令行工具

**验证安装：**
```bash
# 检查 Node.js 版本
node -v
# 应该显示: v18.x.x 或 v20.x.x

# 检查 npm 版本
npm -v
# 应该显示: 9.x.x 或 10.x.x
```

### 2. Java JDK (后端开发必需)

**为什么需要？**
- Spring Boot 基于 Java 运行
- 编译和运行 Java 代码
- Maven 构建工具需要 Java 环境

**安装步骤：**

1. 访问 [Oracle Java 官网](https://www.oracle.com/java/technologies/downloads/)
2. 下载 **JDK 17** 或 **JDK 21** (LTS 版本)
3. 运行安装程序
4. 配置环境变量 `JAVA_HOME`

**环境变量配置 (Windows)：**
```cmd
# 设置 JAVA_HOME
setx JAVA_HOME "C:\Program Files\Java\jdk-17"

# 添加到 PATH
setx PATH "%PATH%;%JAVA_HOME%\bin"
```

**验证安装：**
```bash
# 检查 Java 版本
java -version
# 应该显示: java version "17.x.x" 或 "21.x.x"

# 检查编译器
javac -version
# 应该显示: javac 17.x.x 或 javac 21.x.x
```

### 3. Maven (Java项目管理)

**为什么需要？**
- 管理项目依赖
- 构建 Spring Boot 项目
- 标准化项目结构

**安装步骤：**

1. 访问 [Maven 官网](https://maven.apache.org/download.cgi)
2. 下载 **Binary zip archive** 版本
3. 解压到指定目录 (如 `C:\Program Files\Apache\maven`)
4. 配置环境变量

**环境变量配置 (Windows)：**
```cmd
# 设置 MAVEN_HOME
setx MAVEN_HOME "C:\Program Files\Apache\maven"

# 添加到 PATH
setx PATH "%PATH%;%MAVEN_HOME%\bin"
```

**验证安装：**
```bash
# 检查 Maven 版本
mvn -version
# 应该显示 Maven 版本信息和 Java 版本
```

### 4. MySQL 数据库

**为什么需要？**
- 存储用户信息
- 存储商品数据
- 存储订单信息

**安装步骤：**

1. 访问 [MySQL 官网](https://dev.mysql.com/downloads/mysql/)
2. 下载 **MySQL Community Server**
3. 运行安装程序
4. 设置 root 用户密码 (记住这个密码！)
5. 配置 MySQL 服务

**验证安装：**
```bash
# 启动 MySQL 命令行
mysql -u root -p
# 输入密码后应该看到 MySQL 提示符: mysql>
```

## 🛠️ 开发工具推荐

### 1. VS Code (前端开发)

**推荐安装：**
- 下载地址：[https://code.visualstudio.com/](https://code.visualstudio.com/)

**推荐插件：**
```json
{
  "recommendations": [
    "Vue.volar",           // Vue 3 支持
    "Vue.vscode-typescript-vue-plugin",  // Vue TypeScript 支持
    "bradlc.vscode-tailwindcss",  // CSS 样式提示
    "esbenp.prettier-vscode",       // 代码格式化
    "dbaeumer.vscode-eslint",       // 代码检查
    "ms-vscode.vscode-json"         // JSON 文件支持
  ]
}
```

**安装插件方法：**
1. 打开 VS Code
2. 按 `Ctrl+Shift+X` 打开插件面板
3. 搜索上述插件名称并安装

### 2. IntelliJ IDEA (后端开发)

**推荐安装：**
- 下载地址：[https://www.jetbrains.com/idea/](https://www.jetbrains.com/idea/)
- **Community Edition** (免费版) 即可满足需求

**推荐插件：**
- Lombok (简化 Java 代码)
- Spring Boot Helper (Spring Boot 支持)
- MySQL Connector (数据库连接)

### 3. Postman (API 测试)

**为什么需要？**
- 测试后端 API 接口
- 调试 HTTP 请求
- 查看响应数据

**下载地址：** [https://www.postman.com/](https://www.postman.com/)

### 4. MySQL Workbench (数据库管理)

**为什么需要？**
- 可视化数据库管理
- 执行 SQL 查询
- 数据库设计

**下载地址：** [https://dev.mysql.com/downloads/workbench/](https://dev.mysql.com/downloads/workbench/)

## 📁 项目目录规划

### 推荐的项目结构

```
clothing-store-system/
├── frontend/                    # Vue.js 前端项目
│   ├── src/
│   │   ├── components/         # 可复用组件
│   │   ├── views/             # 页面组件
│   │   ├── stores/            # Pinia 状态管理
│   │   ├── services/          # API 服务
│   │   ├── router/            # 路由配置
│   │   └── assets/            # 静态资源
│   ├── public/                # 公共文件
│   ├── package.json           # 项目配置
│   └── vite.config.js         # 构建配置
├── backend/                     # Spring Boot 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/          # Java 源代码
│   │   │   └── resources/     # 配置文件
│   │   └── test/              # 测试代码
│   ├── pom.xml                # Maven 配置
│   └── application.properties # 应用配置
├── docs/                        # 项目文档
├── database/                    # 数据库脚本
└── README.md                   # 项目说明
```

### 创建项目目录

```bash
# 在您的工作目录下执行
mkdir clothing-store-system
cd clothing-store-system

# 创建子目录
mkdir frontend backend docs database
```

## 🔧 环境验证测试

### 1. 创建测试项目验证前端环境

```bash
# 进入 frontend 目录
cd frontend

# 创建 Vue 项目
npm create vue@latest . --yes

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

如果看到类似以下输出，说明前端环境配置成功：
```
  VITE v5.x.x  ready in xxx ms

  ➜  Local:   http://localhost:5173/
```

### 2. 创建测试项目验证后端环境

```bash
# 进入 backend 目录
cd ../backend

# 使用 Spring Boot CLI 创建项目 (如果有的话)
# 或者访问 https://start.spring.io/ 创建项目

# 验证 Maven 是否工作
mvn --version
```

### 3. 验证数据库连接

```bash
# 启动 MySQL 服务
# Windows: 在服务管理器中启动 MySQL
# Mac: brew services start mysql
# Linux: sudo systemctl start mysql

# 连接数据库
mysql -u root -p
# 输入密码后执行:
CREATE DATABASE test_db;
SHOW DATABASES;
```

## ❓ 常见问题解决

### 问题1: Node.js 安装后命令不识别

**解决方案：**
1. 重新启动命令行工具
2. 检查环境变量 PATH 是否包含 Node.js 路径
3. 重新安装 Node.js，选择"添加到 PATH"选项

### 问题2: Java 环境变量配置失败

**解决方案：**
1. 确认 JDK 安装路径
2. 手动设置环境变量：
   ```cmd
   set JAVA_HOME=C:\Program Files\Java\jdk-17
   set PATH=%PATH%;%JAVA_HOME%\bin
   ```
3. 重启命令行工具

### 问题3: Maven 下载依赖慢

**解决方案：**
1. 配置国内镜像源，编辑 `~/.m2/settings.xml`：
   ```xml
   <mirrors>
     <mirror>
       <id>aliyun</id>
       <name>Aliyun Maven</name>
       <url>https://maven.aliyun.com/repository/public</url>
       <mirrorOf>central</mirrorOf>
     </mirror>
   </mirrors>
   ```

### 问题4: MySQL 连接失败

**解决方案：**
1. 检查 MySQL 服务是否启动
2. 确认用户名和密码正确
3. 检查防火墙设置

## ✅ 环境准备检查清单

在继续下一阶段之前，请确认以下项目：

- [ ] Node.js 安装成功 (`node -v` 显示版本)
- [ ] npm 工作正常 (`npm -v` 显示版本)
- [ ] Java JDK 安装成功 (`java -version` 显示版本)
- [ ] Maven 配置正确 (`mvn -v` 显示版本)
- [ ] MySQL 服务运行正常 (`mysql -u root -p` 能连接)
- [ ] VS Code 安装并配置好插件
- [ ] IntelliJ IDEA 安装完成
- [ ] 项目目录创建完成

## 🎉 下一阶段预告

环境准备完成后，我们将进入 **第一阶段：用户认证系统** 的开发，包括：

1. 创建 Vue.js 前端项目
2. 开发登录注册页面
3. 设计主页面布局
4. 实现路由和状态管理

准备好开始您的全栈开发之旅了吗？让我们继续前进！ 🚀