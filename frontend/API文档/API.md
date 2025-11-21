---
tags:
  - API文档
  - SpringBoot
  - Vue3
  - RESTful
created: 2025-11-21
modified: 2025-11-21
category: 开发文档
difficulty: intermediate
---

# 📡 前端API接口文档总览

本文档详细描述了前端可访问的所有后端API接口，包含完整的JSON通信示例。

## 📚 文档目录结构

### 🔐 认证相关API
- [[01-认证相关API/01-登录接口.md|登录接口]] (`POST /auth/login`)
- [[01-认证相关API/02-注册接口.md|注册接口]] (`POST /auth/register`)
- [[01-认证相关API/03-用户信息接口.md|用户信息接口]] (`GET /auth/userinfo`)
- [[01-认证相关API/04-登出接口.md|登出接口]] (`POST /auth/logout`)

### 🌐 公共API
- [[02-公共API/01-健康检查接口.md|健康检查接口]] (`GET /api/public/health`)

### 📦 商品管理API

#### 🔍 商品查询相关
- [[03-商品管理API/01-商品查询/01-商品列表.md|商品列表]] (`GET /products`)
- [[03-商品管理API/01-商品查询/02-商家商品列表.md|商家商品列表]] (`GET /products/merchant`)
- [[03-商品管理API/01-商品查询/03-商品详情.md|商品详情]] (`GET /products/{id}`)
- [[03-商品管理API/01-商品查询/04-商品搜索.md|商品搜索]] (`GET /products/search`)
- [[03-商品管理API/01-商品查询/05-分类查询.md|分类查询]] (`GET /products/category/{category}`)
- [[03-商品管理API/01-商品查询/06-热销商品.md|热销商品]] (`GET /products/top-selling`)
- [[03-商品管理API/01-商品查询/07-新品商品.md|新品商品]] (`GET /products/newest`)

#### ✏️ 商品操作相关
- [[03-商品管理API/02-商品操作/01-创建商品.md|创建商品]] (`POST /products`)
- [[03-商品管理API/02-商品操作/02-更新商品.md|更新商品]] (`PUT /products/{id}`)
- [[03-商品管理API/02-商品操作/03-删除商品.md|删除商品]] (`DELETE /products/{id}`)
- [[03-商品管理API/02-商品操作/04-商品状态切换.md|商品状态切换]] (`PUT /products/{id}/status`)
- [[03-商品管理API/02-商品操作/05-库存更新.md|库存更新]] (`PUT /products/{id}/stock`)
- [[03-商品管理API/02-商品操作/06-价格更新.md|价格更新]] (`PUT /products/{id}/price`)
- [[03-商品管理API/02-商品操作/07-折扣更新.md|折扣更新]] (`PUT /products/{id}/discount`)

#### 🖼️ 图片管理相关
- [[03-商品管理API/03-图片管理/01-上传商品图片.md|上传商品图片]] (`POST /products/{id}/image`)
- [[03-商品管理API/03-图片管理/02-删除商品图片.md|删除商品图片]] (`DELETE /products/{id}/image`)

#### 📊 统计与批量操作
- [[03-商品管理API/04-统计与批量操作/01-商品统计.md|商品统计]] (`GET /products/stats`)
- [[03-商品管理API/04-统计与批量操作/02-商家统计.md|商家统计]] (`GET /products/merchant/stats`)
- [[03-商品管理API/04-统计与批量操作/03-批量上架.md|批量上架]] (`POST /products/batch/publish`)
- [[03-商品管理API/04-统计与批量操作/04-批量下架.md|批量下架]] (`POST /products/batch/unpublish`)
- [[03-商品管理API/04-统计与批量操作/05-批量删除.md|批量删除]] (`POST /products/batch/delete`)

### 📋 通用规范
- [[04-通用规范/01-响应格式规范.md|响应格式规范]]
- [[04-通用规范/02-错误处理规范.md|错误处理规范]]
- [[04-通用规范/03-数据模型定义.md|数据模型定义]]
- [[04-通用规范/04-权限说明.md|权限说明]]

## 🎯 使用说明

每个API文档都包含以下内容：
1. **接口概述** - 功能描述和业务场景
2. **请求信息** - HTTP方法、URL路径、权限要求
3. **请求参数** - 路径参数、查询参数、请求体（含字段说明）
4. **成功响应** - JSON结构示例和字段说明
5. **错误响应** - 可能的错误码和消息
6. **使用示例** - 前端调用示例代码

请根据需要点击相应链接查看详细文档。