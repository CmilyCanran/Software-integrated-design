# CLAUDE.md

此文件为 Claude Code (claude.ai/code) 在此代码库中工作时提供指导。

## 项目概述

这是一个**教程驱动的服装购物系统**，专为初学者学习现代全栈Web开发而设计。项目采用**功能驱动、前端优先**的方法，每个功能模块都在实现后端之前先提供即时视觉反馈。

## 架构说明

### 前端 (Vue 3)
- **位置**: `frontend/` 目录
- **技术栈**: Vue 3 + Vue Router + Pinia + Element Plus + Axios
- **开发服务器**: `npm run dev` (运行在 localhost:5173)
- **构建**: `npm run build`
- **代码检查**: `npm run lint`

### 后端 (Spring Boot 3)
- **位置**: 根目录 (`src/`)
- **技术栈**: Spring Boot 3 + Spring Security + JPA + MySQL
- **主类**: `com.cmliy.springweb.SpringWebApplication`
- **开发服务器**: `mvn spring-boot:run` (运行在 localhost:8080)
- **构建**: `mvn clean package`
- **测试**: `mvn test`

### 教程结构
- **教程文档**: `Spring-Boot-Web开发教程完整版/`
- **教学方法**: 功能驱动开发 (需求驱动 → 前端实现 → 后端API → 集成测试)
- **渐进式学习**: 每个功能都建立在之前的基础上

## 开发命令

### 前端 (在 `frontend/` 目录中)
```bash
# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview

# 运行ESLint
npm run lint
```

### 后端 (在根目录中)
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

## 核心开发原则

### 教程驱动开发
1. **需求优先**: 每个功能都从明确的业务需求开始
2. **前端优先**: 先实现UI/UX以获得即时视觉反馈
3. **后端其次**: 创建API以支持前端功能
4. **集成最后**: 连接前端和后端并进行适当的错误处理

### 功能模块
系统按功能模块组织：
1. **用户认证系统** - 登录、注册、会话管理
2. **商品管理系统** - 商品目录、CRUD操作
3. **购物车系统** - 购物车操作、持久化
4. **订单管理系统** - 订单创建、状态跟踪
5. **管理后台** - 管理功能

### 代码组织
- **前端组件**: 按功能组织在 `src/views/` 和 `src/components/` 中
- **状态管理**: Pinia存储在 `src/stores/` 中
- **API服务**: 集中在 `src/services/` 中
- **后端控制器**: 按领域组织在 `src/main/java/com/cmliy/springweb/controller/` 中
- **后端服务**: 业务逻辑在 `src/main/java/com/cmliy/springweb/service/` 中
- **后端模型**: 实体在 `src/main/java/com/cmliy/springweb/model/` 中

## 重要说明

### 教程开发要求
- **初学者友好**: 所有代码都应有良好注释且易于理解
- **渐进式复杂度**: 从简单开始，逐步增加复杂性
- **可运行示例**: 每个功能在继续之前都必须完全可用
- **错误处理**: 包含适当的错误处理和用户反馈

### 技术约束
- **不使用TypeScript**: 教程使用纯JavaScript以提高可访问性
- **Vue 3 Composition API**: 使用 `<script setup>` 语法
- **Element Plus UI**: 整个项目使用一致的UI组件
- **RESTful API**: 后端遵循标准REST约定
- **MySQL数据库**: 所有数据持久化通过MySQL

### 开发工作流程
1. 按照 `Spring-Boot-Web开发教程完整版/` 中的教程顺序进行
2. 每个功能模块都是独立的，但建立在之前知识的基础上
3. 在实现后端之前始终测试前端功能
4. 确保前端和后端之间的正确集成
5. 先生成教程，用户学习后提问并完成功能
6. 用户的问题记入相关教程，且在记入后明确给用户回答要怎么做，且告诉用户教程更新在哪里了让用户可以方便查阅
7. 做出重大更改时更新教程文档