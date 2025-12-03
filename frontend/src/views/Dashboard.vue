<template>
  <div class="dashboard-container">
    <Header
      page-title="仪表板"
      :showCart="isUserRole"
      :cartItemCount="cartItemCount"
    />
    <el-container>
      <el-main>
        <!-- 统计卡片 -->
        <el-row :gutter="20" class="stats-row">
          <el-col :span="6">
            <el-card class="stats-card">
              <div class="stats-content">
                <el-icon class="stats-icon"><User /></el-icon>
                <div class="stats-info">
                  <h3>总用户数</h3>
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

        <!-- 根据角色显示不同的快速操作 -->
        <el-row :gutter="20" class="content-row">
          <!-- 商家用户的快速操作 -->
          <el-col :span="16" v-if="isMerchant">
            <el-card>
              <template #header>
                <h3>🏪 商家快速操作</h3>
              </template>
              <div class="quick-actions">
                <el-button type="primary" size="large" @click="goToProductManagement">
                  <el-icon><Plus /></el-icon>
                  商品管理
                </el-button>
                <el-button type="success" size="large" @click="goToProducts">
                  <el-icon><ShoppingCart /></el-icon>
                  查看商品
                </el-button>
                <el-button type="info" size="large" @click="handleViewOrders">
                  <el-icon><Document /></el-icon>
                  订单管理
                </el-button>
                <el-button type="warning" size="large" @click="handleViewStats">
                  <el-icon><DataAnalysis /></el-icon>
                  数据统计
                </el-button>
              </div>
            </el-card>
          </el-col>

          <!-- 普通用户的快速操作 -->
          <el-col :span="16" v-else>
            <el-card>
              <template #header>
                <h3>🛍️ 用户快速操作</h3>
              </template>
              <div class="quick-actions">
                <el-button type="primary" size="large" @click="goToProducts">
                  <el-icon><ShoppingCart /></el-icon>
                  浏览商品
                </el-button>
                <el-button type="success" size="large" @click="handleViewOrders">
                  <el-icon><Document /></el-icon>
                  我的订单
                </el-button>
                <el-button type="warning" size="large" @click="handleSettings">
                  <el-icon><Setting /></el-icon>
                  个人设置
                </el-button>
              </div>
            </el-card>
          </el-col>

          <!-- 系统信息 -->
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
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import {
  User, ShoppingCart, Document, Money, Plus, Setting,
  DataAnalysis, Star
} from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import dayjs from 'dayjs'

// 状态管理
const authStore = useAuthStore()
const router = useRouter()

// 计算属性
const isMerchant = computed(() => {
  return authStore.userInfo?.role === 'SHOPER' || authStore.userInfo?.role === 'ADMIN'
})

// 计算属性：判断是否为普通用户
const isUserRole = computed(() => {
  return authStore.userInfo?.role === 'USER'
})

// 计算属性：购物车商品数量（模拟数据，后续可接入真实的购物车状态管理）
const cartItemCount = computed(() => {
  // TODO: 后续接入真实的购物车状态管理
  return 0
})

// 工具方法
const formatDate = (date: Date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

// 商家专用方法
const goToProductManagement = () => {
  router.push('/merchant/products')
}

// 通用方法
const goToProducts = () => {
  if (isMerchant.value) {
    router.push('/merchant/products')
  } else {
    router.push('/products')
  }
}

const handleViewOrders = () => {
  ElMessage.info('订单功能开发中...')
}

const handleViewStats = () => {
  ElMessage.info('数据统计功能开发中...')
}


const handleSettings = () => {
  ElMessage.info('设置功能开发中...')
}
</script>

<style scoped>
.dashboard-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.simple-header {
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
}

.simple-header h2 {
  margin: 0;
  color: #303133;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.el-main {
  padding: 20px;
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
  height: 100%;
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

@media (max-width: 768px) {
  .simple-header {
    flex-direction: column;
    height: auto;
    padding: 12px;
    gap: 12px;
  }

  .quick-actions {
    flex-direction: column;
  }

  .stats-row {
    display: none;
  }
}
</style>
