<template>
  <div class="dashboard-container">
    <el-container>
      <!-- 使用可复用的AppHeader组件 -->
      <AppHeader title="服装销售系统" />

      <el-main>

        <el-row :gutter="20" class="stats-row">
          <el-col :span="6">
            <el-card class="stats-card">
              <div class="stats-content">
                <el-icon class="stats-icon"><User /></el-icon>
                <div class="stats-info">
                  <h3>用户总数</h3>
                  <p class="stats-number">1,234</p>
                </div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="6">
            <el-card class="stats-card">
              <div class="stats-content">
                <el-icon class="stats-icon"><ShoppingCart /></el-icon>
                <div class="stats-info">
                  <h3>商品数量</h3>
                  <p class="stats-number">567</p>
                </div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="6">
            <el-card class="stats-card">
              <div class="stats-content">
                <el-icon class="stats-icon"><Document /></el-icon>
                <div class="stats-info">
                  <h3>订单总数</h3>
                  <p class="stats-number">890</p>
                </div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="6">
            <el-card class="stats-card">
              <div class="stats-content">
                <el-icon class="stats-icon"><Money /></el-icon>
                <div class="stats-info">
                  <h3>销售额</h3>
                  <p class="stats-number">¥12,345</p>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="20" class="content-row">
          <el-col :span="16">
            <el-card>
              <template #header>
                <h3>快速操作</h3>
              </template>
              <div class="quick-actions">
                <el-button type="primary" size="large" icon="Plus">
                  添加商品
                </el-button>
                <el-button type="success" size="large" icon="ShoppingCart">
                  查看订单
                </el-button>
                <el-button type="info" size="large" icon="User">
                  用户管理
                </el-button>
                <el-button type="warning" size="large" icon="Setting">
                  系统设置
                </el-button>
              </div>
            </el-card>
          </el-col>

          <el-col :span="8">
            <el-card>
              <template #header>
                <h3>系统信息</h3>
              </template>
              <div class="system-info">
                <p><strong>版本:</strong> v1.0.0</p>
                <p><strong>环境:</strong> 开发环境</p>
                <p><strong>最后更新:</strong> {{ formatDate(new Date()) }}</p>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
// ============================================================================
// 📦 组件导入
// ============================================================================

import AppHeader from '@/components/AppHeader.vue'      // 导入可复用的头部组件
import { useAuthStore } from '@/stores/auth'            // 导入认证状态管理
import dayjs from 'dayjs'                                // 导入日期处理库

// ============================================================================
// 🎯 响应式数据
// ============================================================================

/**
 * 🔐 认证状态管理
 *
 * 从Pinia store中获取用户认证信息，用于显示用户名和角色信息
 */
const authStore = useAuthStore()

// ============================================================================
// 🛠️ 工具方法
// ============================================================================

/**
 * 📅 日期格式化方法
 *
 * 将日期对象格式化为易读的字符串格式
 * 使用dayjs库进行日期处理，提供更好的浏览器兼容性
 *
 * @param {Date} date - 要格式化的日期对象
 * @returns {string} 格式化后的日期字符串 (YYYY-MM-DD HH:mm:ss)
 */
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}
</script>

<style scoped>
.dashboard-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.el-main {
  padding: 80px 20px 20px 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  height: 120px;
}

.stats-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stats-icon {
  font-size: 32px;
  color: #409eff;
}

.stats-info h3 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #666;
}

.stats-number {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.content-row {
  margin-top: 20px;
}

.quick-actions {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.system-info p {
  margin: 8px 0;
  color: #666;
}
</style>
