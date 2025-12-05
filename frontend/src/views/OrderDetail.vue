<template>
  <div class="order-detail-page">
    <!-- Header组件 -->
    <Header :page-title="`订单详情 #${orderId}`" />

    <!-- 加载状态 -->
    <div v-if="orderStore.currentLoading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 错误提示 -->
    <el-alert
      v-else-if="orderStore.currentError"
      :title="orderStore.currentError"
      type="error"
      show-icon
      :closable="false"
      class="error-alert"
    />

    <!-- 订单详情内容 -->
    <div v-else-if="currentOrder" class="order-detail-content">
      <!-- 订单状态时间线 -->
      <el-card class="status-timeline-card">
        <template #header>
          <div class="card-header">
            <span>订单状态</span>
            <OrderStatus :status="currentOrder.status" size="large" />
          </div>
        </template>
        <OrderTimeline :order="currentOrder" />
      </el-card>

      <!-- 订单基本信息 -->
      <el-card class="basic-info-card">
        <template #header>
          <span>订单信息</span>
        </template>
        <div class="basic-info-content">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单号">#{{ currentOrder.id }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatDateTime(currentOrder.createdAt) }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ formatDateTime(currentOrder.updatedAt) }}</el-descriptions-item>
            <el-descriptions-item label="总金额">
              <span class="amount">¥{{ currentOrder.totalAmount.toFixed(2) }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="购买数量">{{ currentOrder.quantity }} 件</el-descriptions-item>
            <el-descriptions-item label="商家">
              <div class="seller-info">
                <el-icon><Shop /></el-icon>
                <span>{{ currentOrder.sellerName }}</span>
              </div>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </el-card>

      <!-- 订单商品列表 -->
      <el-card class="items-card">
        <template #header>
          <span>商品信息</span>
        </template>
        <div class="items-list">
          <OrderItemCard
            v-for="item in orderItems"
            :key="item.productId"
            :item="item"
            readonly
          />
        </div>
      </el-card>

      <!-- 订单操作 -->
      <div class="actions-section">
        <OrderActions
          :order="currentOrder"
          :user-role="currentUserRole"
          @cancel="handleCancelOrder"
          @viewDetail="handleViewDetail"
        />
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <el-empty description="订单不存在或已删除" />
      <el-button type="primary" @click="goToOrderList">
        返回订单列表
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Header from '@/components/Header.vue'
import { useOrderStore } from '@/stores/order'
import OrderStatus from '@/components/order/OrderStatus.vue'
import OrderTimeline from '@/components/order/OrderTimeline.vue'
import OrderItemCard from '@/components/order/OrderItemCard.vue'
import OrderActions from '@/components/order/OrderActions.vue'
import { Shop } from '@element-plus/icons-vue'
import type { OrderItem } from '@/types/order'
import { useAuthStore } from '@/stores/auth'

// 路由
const route = useRoute()
const router = useRouter()

// Store
const orderStore = useOrderStore()
const authStore = useAuthStore()

// 状态
const orderId = computed(() => Number(route.params.id))
const currentUserRole = computed(() => {
  const user = authStore.user
  return user?.role || 'USER'
})

// 计算属性
const currentOrder = computed(() => {
  return orderStore.currentOrder
})

// 订单商品数据（从订单中提取商品信息）
const orderItems = computed<OrderItem[]>(() => {
  if (!currentOrder.value) return []

  // 从订单数据中提取商品信息
  // 一个订单可能包含多个商品，但当前实现是一个订单只对应一个商品
  return [
    {
      productId: currentOrder.value.productId,
      productName: currentOrder.value.productName,
      productImage: currentOrder.value.productImage,
      quantity: currentOrder.value.quantity,
      unitPrice: currentOrder.value.unitPrice,
      subtotal: currentOrder.value.unitPrice * currentOrder.value.quantity  // 正确计算小计
    }
  ]
})

// 方法
/**
 * 加载订单详情
 */
const loadOrderDetail = async () => {
  const order = await orderStore.fetchOrderDetail(orderId.value)
  if (!order) {
    ElMessage.error('订单不存在')
    setTimeout(() => {
      router.push('/orders')
    }, 2000)
  }
}

/**
 * 取消订单
 */
const handleCancelOrder = async () => {
  const success = await orderStore.cancelOrder(orderId.value)
  if (success) {
    // 重新加载订单详情
    await loadOrderDetail()
  }
}

/**
 * 查看订单详情（兼容组件事件）
 */
const handleViewDetail = () => {
  // 当前已经在详情页，可以刷新或其他操作
  loadOrderDetail()
}

/**
 * 返回订单列表
 */
const goToOrderList = () => {
  router.push('/orders')
}

/**
 * 格式化日期时间
 */
const formatDateTime = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 生命周期钩子
onMounted(async () => {
  if (!orderId.value) {
    ElMessage.error('订单ID无效')
    router.push('/orders')
    return
  }

  await loadOrderDetail()
})
</script>

<style scoped>
.order-detail-page {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.loading-container {
  padding: 40px 20px;
}

.error-alert {
  margin: 20px;
}

.order-detail-content {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.status-timeline-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.basic-info-card {
  margin-bottom: 20px;
}

.basic-info-content {
  padding: 20px 0;
}

.amount {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
}

.seller-info .el-icon {
  color: #909399;
}

.items-card {
  margin-bottom: 20px;
}

.items-list {
  padding: 10px 0;
}

.actions-section {
  padding: 20px 0;
  text-align: center;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .order-detail-content {
    padding: 10px;
  }

  .basic-info-content {
    padding: 10px 0;
  }

  .actions-section {
    padding: 10px 0;
  }
}
</style>