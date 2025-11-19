# Spring Boot + Vue 3 服装购物系统

🎯 **教程驱动的全栈Web开发学习项目**

一个专为初学者设计的现代化全栈Web应用，采用**功能驱动、前端优先**的开发方法，通过完整的服装购物系统实战学习Spring Boot和Vue 3开发。

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
- **数据**: Spring Data JPA + MySQL
- **构建**: Maven
- **测试**: JUnit 5

#### 前端技术栈
- **框架**: Vue 3 (Composition API)
- **路由**: Vue Router 4
- **状态**: Pinia
- **UI**: Element Plus
- **HTTP**: Axios
- **构建**: Vite

### 🏗️ 项目架构

```
SpringWeb/
├── frontend/                    # Vue 3 前端应用
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   ├── components/         # 通用组件
│   │   ├── stores/             # Pinia状态管理
│   │   ├── services/           # API服务
│   │   └── router/             # 路由配置
│   ├── package.json
│   └── vite.config.js
├── src/main/java/com/cmliy/springweb/  # Spring Boot 后端
│   ├── controller/             # 控制器层
│   ├── service/                # 业务逻辑层
│   ├── model/                  # 实体模型
│   ├── repository/             # 数据访问层
│   ├── security/               # 安全配置
│   └── config/                 # 配置类
├── src/main/resources/
│   ├── application.yml         # 应用配置
│   └── db/migration/           # 数据库脚本
├── Spring-Boot-Web开发教程完整版/  # 📚 教程文档
│   ├── 01-Spring-Boot学习笔记/  # 概念理论学习
│   ├── 02-Vue学习笔记/         # 前端概念学习
│   └── 00-前后端分离实战-服装销售系统/  # 实战开发教程
├── pom.xml                     # Maven配置
└── CLAUDE.md                   # AI开发指导文档
```

## 🚀 快速开始

### 📋 环境要求

- **Node.js**: 16.0+
- **Java**: 17+
- **Maven**: 3.6+
- **MySQL**: 8.0+

### 🛠️ 安装与运行

#### 1. 克隆项目
```bash
git clone <repository-url>
cd SpringWeb
```

#### 2. 数据库配置
```sql
-- 创建数据库
CREATE DATABASE clothing_store;

-- 创建用户表（后续章节会详细说明）
USE clothing_store;
```

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
前端应用将在 `http://localhost:5173` 启动

## 📚 学习路径

### 🎯 推荐学习流程

#### 📖 第一阶段：概念建立
1. **Spring核心概念** - 理解IoC、DI、注解体系
2. **Vue核心概念** - 掌握响应式、组件化、Composition API

#### 🛠️ 第二阶段：实战开发
按照教程顺序逐步实现功能模块：

1. **用户认证系统**
   - 登录/注册页面
   - JWT认证机制
   - 用户状态管理

2. **商品管理系统**
   - 商品列表展示
   - 商品CRUD操作
   - 分类管理

3. **购物车系统**
   - 购物车操作
   - 本地持久化
   - 数量管理

4. **订单管理系统**
   - 订单创建
   - 状态跟踪
   - 历史记录

5. **管理后台**
   - 用户管理
   - 商品管理
   - 订单管理

### 📖 教程文档导航

#### 🎯 学习笔记（概念理论）
```
01-Spring-Boot学习笔记/
├── 00-Spring核心概念/          # IoC、DI核心思想
├── 01-Spring注解/              # 注解体系详解
├── 02-依赖注入/                # 依赖注入深入
├── 03-配置管理/                # 配置机制学习
└── 04-最佳实践/                # 开发规范总结

02-Vue学习笔记/
├── 00-Vue核心概念/             # 响应式原理、组件化
├── 01-组件系统/                # 组件化开发
├── 02-状态管理/                # Pinia状态管理
└── 03-路由系统/                # Vue Router配置
```

#### 🛠️ 开发教程（实战指导）
```
00-前后端分离实战-服装销售系统/
├── 01-项目环境准备/            # 环境搭建指南
├── 02-功能一-用户认证系统/      # 用户认证完整实现
│   ├── 01-项目环境准备.md
│   ├── 02-Vue核心概念学习.md
│   ├── 03-用户状态管理.md
│   ├── 04-路由配置与守卫.md
│   ├── 05-API服务封装.md
│   ├── 06-登录页面开发.md
│   ├── 07-注册页面开发.md
│   ├── 08-仪表板页面开发.md
│   └── 09-功能测试与验证.md
├── 03-功能二-商品管理系统/      # 商品管理功能
└── 04-功能三-购物车系统/        # 购物车功能
```

## 🎯 核心特性

### 🔥 功能驱动开发
- **需求优先**: 每个功能都从明确的业务需求开始
- **前端优先**: 先实现UI/UX获得即时视觉反馈
- **后端支撑**: 创建API支持前端功能
- **集成测试**: 连接前后端并进行完整测试

### 📚 教程驱动学习
- **学→练→会循环**: 理论学习 → 动手实践 → 验证掌握
- **进度跟踪**: 每章节都有明确的完成标准
- **概念支持**: 开发教程引用学习笔记，理论与实践结合
- **AI辅助**: 实时生成教程和解答疑问

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

## 📋 开发规范

### 🎯 代码组织原则
- **功能模块化**: 按业务功能组织代码
- **分层架构**: 严格的MVC分层设计
- **RESTful API**: 遵循REST设计原则
- **组件化开发**: Vue组件高度复用

### 📝 文档规范
- **Obsidian格式**: 所有教程文档遵循Obsidian格式
- **标签规范**: 使用标准化的标签体系
- **引用规范**: 正确使用双向链接引用
- **分类清晰**: 学习笔记与开发教程明确区分

### 🔄 开发流程
1. **需求分析** - 明确功能需求
2. **前端实现** - 完成UI和交互逻辑
3. **后端API** - 实现对应的后端接口
4. **集成测试** - 前后端联调测试
5. **文档更新** - 更新教程文档

## 🎯 学习成果

完成本项目学习后，你将掌握：

### 🏆 后端技能
- ✅ Spring Boot 3 核心开发
- ✅ Spring Security 安全认证
- ✅ Spring Data JPA 数据访问
- ✅ RESTful API 设计与实现
- ✅ JWT 认证机制
- ✅ MySQL 数据库设计

### 🏆 前端技能
- ✅ Vue 3 Composition API
- ✅ Vue Router 4 路由管理
- ✅ Pinia 状态管理
- ✅ Element Plus UI 组件库
- ✅ Axios HTTP 客户端
- ✅ 响应式设计

### 🏆 全栈技能
- ✅ 前后端分离架构设计
- ✅ 跨域处理与安全配置
- ✅ API 接口设计与文档
- ✅ 项目部署与运维
- ✅ 团队协作与代码规范

---

🎯 **开始你的全栈开发学习之旅！**

从 `Spring-Boot-Web开发教程完整版/00-前后端分离实战-服装销售系统/README.md` 开始，按照教程步骤逐步学习，祝你学习愉快！
