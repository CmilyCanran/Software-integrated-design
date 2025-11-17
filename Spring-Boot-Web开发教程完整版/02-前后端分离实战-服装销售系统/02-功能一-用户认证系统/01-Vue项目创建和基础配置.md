# Vue项目创建和基础配置

## 🎯 本节目标

创建服装销售系统的前端项目，完成基础配置，为后续功能开发做好准备。

## 📋 功能需求分析

**用户认证系统需要实现：**
- 用户注册页面
- 用户登录页面
- 登录状态管理
- 路由权限控制
- 主页面布局

## 🚀 第一步：创建Vue项目

### 1. 环境检查

确保Node.js环境已经安装：
```bash
# 检查版本
node -v
npm -v
```

### 2. 创建项目

**重要说明：** 当前您已经在Spring Boot项目目录中，我们将在当前目录下创建前端项目。

在当前目录 (`D:\Code\Learn\Java\Spring\SpringWeb\`) 下执行：

```bash
# 创建前端项目目录
mkdir frontend
cd frontend

# 在frontend目录下创建Vue项目
npm create vue@latest . --yes

# 返回项目根目录
cd ..
```

**项目结构将变成：**
```
SpringWeb/                          # 项目根目录
├── frontend/                       # Vue.js 前端项目
│   ├── src/
│   ├── package.json
│   └── vite.config.js
├── src/                           # Spring Boot 后端源代码
│   ├── main/
│   └── test/
├── pom.xml                        # Maven 配置
└── Spring-Boot-Web开发教程完整版/   # 教程文档
```

创建过程中会询问配置选项，**请严格按照以下方式选择**：

```
✔ Project name: … clothing-store-frontend
✔ Add TypeScript? … No          # ❌ 初学者暂时跳过，专注核心功能
✔ Add JSX Support? … No          # ❌ 不需要：Vue模板语法已足够
✔ Add Vue Router for Single Page Application development? … Yes  # ✅ 必需：多页面导航
✔ Add Pinia for state management? … Yes  # ✅ 必需：管理用户状态和购物车
✔ Add Vitest for Unit Testing? … No      # ❌ 暂时跳过，专注核心功能学习
✔ Add an End-to-End Testing Solution? › No  # ❌ 暂时跳过，专注核心功能学习
✔ Add ESLint for code quality? … Yes     # ✅ 必需：保证代码质量
```

## 📋 配置选项详解（初学者友好版）

### ❌ **TypeScript - 暂时跳过**
**为什么初学者暂时不选择TypeScript？**
- **专注核心功能**：先掌握Vue、路由、状态管理等核心概念
- **降低学习曲线**：避免同时学习太多新技术
- **快速见效**：用JavaScript可以更快看到项目效果
- **后续可添加**：等项目功能完成后，再考虑添加类型支持

**什么时候可以考虑TypeScript？**
- 项目功能基本完成
- 团队协作需要
- 遇到复杂业务逻辑需要类型约束

### ❌ **JSX Support - 不推荐选择**
**为什么不选择JSX？**
- Vue模板语法已经足够强大和直观
- JSX增加学习成本，对初学者不友好
- 服装销售系统的UI相对标准，不需要复杂的模板逻辑
- 保持团队技术栈一致性

### ✅ **Vue Router - 必须选择**
**服装销售系统需要的页面：**
- 首页 (`/`)
- 商品列表 (`/products`)
- 商品详情 (`/products/:id`)
- 购物车 (`/cart`)
- 订单列表 (`/orders`)
- 用户登录 (`/login`)
- 用户注册 (`/register`)
- 管理后台 (`/admin`)

### ✅ **Pinia - 必须选择**
**需要管理的全局状态：**
- 用户登录状态和信息
- 购物车商品
- 全局配置信息
- 主题设置

### ❌ **Vitest - 暂时跳过**
**为什么初学者暂时不选择测试工具？**
- **专注功能实现**：先让功能跑起来
- **简化开发流程**：减少配置复杂度
- **快速迭代**：可以快速修改和测试功能
- **后续可添加**：项目稳定后再添加测试

### ❌ **E2E Testing - 暂时跳过**
**理由同Vitest，先专注核心功能开发。**

### ✅ **ESLint - 必须选择**
**为什么初学者也要选择ESLint？**
- **规范代码风格**：养成良好编码习惯
- **避免低级错误**：及时发现语法和逻辑错误
- **统一代码格式**：让代码更易读
- **IDE集成良好**：在编辑器中直接看到错误提示

### 3. 安装依赖

```bash
# 安装项目依赖
npm install
```

### 4. 安装额外需要的库

```bash
# 安装UI组件库
npm install element-plus

# 安装图标库
npm install @element-plus/icons-vue

# 安装HTTP请求库
npm install axios

# 安装日期处理库
npm install dayjs
```

**依赖说明：**
- **element-plus** - UI组件库，提供漂亮的界面组件
- **@element-plus/icons-vue** - Element Plus的图标库
- **axios** - HTTP请求库，用于和后端API通信
- **dayjs** - 日期处理库，处理订单时间等

## 🎯 现在您需要做的操作

### 第1步：创建Vue项目
在当前目录 (`D:\Code\Learn\Java\Spring\SpringWeb\`) 下执行：

```bash
# 创建前端项目目录
mkdir frontend
cd frontend

# 创建Vue项目
npm create vue@latest .

# 按照下面的选择进行配置（重要！）
```

### 第2步：Vue项目配置选择
当看到配置选项时，**请严格按照以下方式选择**：

```
✔ Project name: … clothing-store-frontend
✔ Add TypeScript? … No          # ← 选择 No
✔ Add JSX Support? … No          # ← 选择 No
✔ Add Vue Router for Single Page Application development? … Yes  # ← 选择 Yes
✔ Add Pinia for state management? … Yes  # ← 选择 Yes
✔ Add Vitest for Unit Testing? … No      # ← 选择 No
✔ Add an End-to-End Testing Solution? › No  # ← 选择 No
✔ Add ESLint for code quality? … Yes     # ← 选择 Yes
```

### 第3步：安装依赖
```bash
# 安装Vue项目的基础依赖
npm install

# 安装我们需要的额外库
npm install element-plus @element-plus/icons-vue axios dayjs
```

### 第4步：启动项目测试
```bash
# 启动开发服务器
npm run dev
```

如果看到类似这样的输出，说明成功了：
```
  VITE v5.x.x  ready in xxx ms

  ➜  Local:   http://localhost:5173/
```

现在访问 http://localhost:5173 应该能看到Vue的欢迎页面！

## ✅ 第三步：启动空白项目测试

### 1. 启动项目

```bash
npm run dev
```

### 2. 验证空白项目

访问 http://localhost:5173，您应该看到：

- **Vue默认欢迎页面** - 包含一些链接和基础信息
- **没有自定义内容** - 完全空白的Vue项目
- **可以正常访问** - 说明项目创建成功

### 3. 测试基础功能

在浏览器中测试：
1. 点击页面上的链接，看看路由是否正常工作
2. 打开浏览器开发者工具，检查是否有错误
3. 确认项目可以正常启动和访问

## 📝 小结

本节我们完成了：

✅ **Vue项目创建** - 创建了一个完全空白的Vue3项目
✅ **基础依赖安装** - 安装了Element Plus等必要库
✅ **项目结构** - 建立了基础的目录结构
✅ **环境验证** - 确认项目可以正常运行

## 🚀 下一步

现在我们有了一个完全空白的Vue项目，接下来我们将：

1. **配置Element Plus** - 添加UI组件库支持
2. **配置路由** - 设置页面导航
3. **创建基础页面** - 登录、注册、主页面
4. **配置状态管理** - 用户状态和购物车管理

**下一节内容：** [登录注册页面开发](02-登录注册页面开发.md) - 我们将从零开始创建第一个功能页面！