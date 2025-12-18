# Spring Boot + Vue 3 服装购物系统


## 📋 项目概述

### 🎯 学习目标
- 掌握Spring Boot 3后端开发技术栈
- 熟练运用Vue 3前端开发技术栈
- 理解前后端分离架构设计
- 学会RESTful API设计与实现
- 掌握现代Web开发最佳实践

### 🛠️ 技术栈

#### 后端技术栈
- **框架**: Spring Boot 3
- **安全**: Spring Security + JWT
- **数据**: Spring Data JPA + PostgreSQL
- **构建**: Maven
- **测试**: JUnit 5

#### 前端技术栈
- **框架**: Vue 3 (Composition API)
- **路由**: Vue Router 4
- **状态**: Pinia
- **UI**: Element Plus
- **HTTP**: Axios
- **构建**: Vite

## 🚀 快速开始

### 📋 环境要求

- **Node.js**: 16.0+
- **Java**: 17+
- **Maven**: 3.6+
- **PostgreSQL**: 18

### 🛠️ 安装与运行

#### 1. 克隆项目
```bash
git clone <repository-url>
cd SpringWeb
```

#### 2. 数据库配置
```sql
-- 创建数据库
CREATE DATABASE clothes_db;

#### 3. 后端启动
```bash
# 在根目录执行
mvn clean install
mvn spring-boot:run
```
后端服务将在 `http://localhost:8080` 启动

#### 4. 前端启动
```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```
前端应用将在 `http://localhost:3000` 启动

### 🛠️ 开发命令

#### 前端开发 (在 `frontend/` 目录)
```bash
# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview

# 代码检查
npm run lint
```

#### 后端开发 (在根目录)
```bash
# 启动开发服务器
mvn spring-boot:run

# 构建项目
mvn clean package

# 运行测试
mvn test

# 运行特定测试
mvn test -Dtest=ClassName
```

