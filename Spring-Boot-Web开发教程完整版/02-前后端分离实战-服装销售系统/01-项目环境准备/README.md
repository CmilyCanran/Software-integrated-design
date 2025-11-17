# 项目环境准备

## 🎯 本阶段目标

在开始开发服装销售系统之前，我们需要准备好所有必需的开发环境和工具。本章节将指导您完成：

1. **必需软件安装**
2. **开发工具配置**
3. **项目目录规划**
4. **环境验证测试**

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