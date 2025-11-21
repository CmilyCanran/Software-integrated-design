---
tags:
  - TODO
  - 后端开发
  - API实现
created: 2025-11-21
modified: 2025-11-21
category: 开发任务
difficulty: intermediate
---

# 📋 后端API实现TODO清单

## 🔴 紧急任务（必须优先完成）

### 1. 实现商品管理控制器
- [ ] 创建 `ProductController.java`
- [ ] 实现商品查询相关端点（7个）
  - [ ] `GET /products` - 商品列表
  - [ ] `GET /products/merchant` - 商家商品列表
  - [ ] `GET /products/{id}` - 商品详情
  - [ ] `GET /products/search` - 商品搜索
  - [ ] `GET /products/category/{category}` - 分类查询
  - [ ] `GET /products/top-selling` - 热销商品
  - [ ] `GET /products/newest` - 新品商品
- [ ] 实现商品操作相关端点（7个）
  - [ ] `POST /products` - 创建商品
  - [ ] `PUT /products/{id}` - 更新商品
  - [ ] `DELETE /products/{id}` - 删除商品
  - [ ] `PUT /products/{id}/status` - 商品状态切换
  - [ ] `PUT /products/{id}/stock` - 库存更新
  - [ ] `PUT /products/{id}/price` - 价格更新
  - [ ] `PUT /products/{id}/discount` - 折扣更新
- [ ] 实现图片管理相关端点（2个）
  - [ ] `POST /products/{id}/image` - 上传商品图片
  - [ ] `DELETE /products/{id}/image` - 删除商品图片
- [ ] 实现统计与批量操作端点（5个）
  - [ ] `GET /products/stats` - 商品统计
  - [ ] `GET /products/merchant/stats` - 商家统计
  - [ ] `POST /products/batch/publish` - 批量上架
  - [ ] `POST /products/batch/unpublish` - 批量下架
  - [ ] `POST /products/batch/delete` - 批量删除

### 2. 修复认证控制器问题
- [ ] 调试并修复 `/auth/login` 和 `/auth/register` 端点404问题
- [ ] 实现缺失的 `/auth/userinfo` 端点
- [ ] 实现缺失的 `/auth/logout` 端点

### 3. 修复公共端点访问问题
- [ ] 调试 `/api/public/health` 返回401的问题
- [ ] 确保所有公共端点真正公开访问

## 🟡 中等优先级任务

### 4. 统一API响应格式
- [ ] 验证所有端点返回 `{code, data, message}` 格式
- [ ] 确保错误处理一致性
- [ ] 添加适当的HTTP状态码

### 5. 完善安全配置
- [ ] 验证角色权限控制（USER/SHOPER/ADMIN）
- [ ] 确保敏感操作需要适当权限

## 🟢 低优先级任务

### 6. 更新API文档
- [ ] 为每个实现的端点创建详细文档
- [ ] 添加请求/响应示例
- [ ] 更新权限说明

### 7. 添加API测试
- [ ] 为关键端点添加集成测试
- [ ] 验证边界条件和错误场景