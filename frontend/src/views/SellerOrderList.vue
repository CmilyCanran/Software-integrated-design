<template>
  <div class="seller-order-list-page">
    <!-- Header组件 -->
    <Header page-title="商家订单管理" />

    <!-- 加载状态 -->
    <div v-if="orderStore.loading" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>

    <!-- 错误提示 -->
    <el-alert
      v-else-if="orderStore.error"
      :title="orderStore.error"
      type="error"
      show-icon
      :closable="false"
      class="error-alert"
    />

    <!-- 订单内容 -->
    <div v-else class="order-content">
      <!-- 订单统计卡片 -->
      <el-card v-if="orderStore.orderStats" class="stats-card">
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-value">{{ orderStore.orderStats.total }}</div>
            <div class="stat-label">总订单</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ sellerOrdersByStatus.PENDING || 0 }}</div>
            <div class="stat-label">待处理</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ sellerOrdersByStatus.PAID || 0 }}</div>
            <div class="stat-label">已支付</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">¥{{ (orderStore.orderStats.totalAmount || 0).toFixed(2) }}</div>
            <div class="stat-label">总金额</div>
          </div>
        </div>
      </el-card>

      <!-- 状态筛选 -->
      <div class="filter-section">
        <OrderStatusFilter
          v-model="selectedStatus"
          :counts="sellerOrdersByStatus"
          @change="handleStatusChange"
        />
      </div>

      <!-- 订单列表 -->
      <div v-if="filteredOrders.length > 0" class="order-list">
        <OrderCard
          v-for="order in filteredOrders"
          :key="order.id"
          :order="order"
          :show-detail-button="true"
          @click="goToOrderDetail(order.id)"
          @viewDetail="handleViewDetail"
        >
          <template #actions>
            <OrderActions
              :order="order"
              :user-role="'SELLER'"
              @update-status="handleUpdateStatus"
              @view-detail="goToOrderDetail"
            />
          </template>
        </OrderCard>
      </div>

      <!-- 空状态 -->
      <div v-else-if="!orderStore.loading" class="empty-state">
        <el-empty
          :description="getEmptyDescription()"
          :image-size="200"
        >
          <el-button
            v-if="selectedStatus"
            type="primary"
            @click="clearFilter"
          >
            显示全部订单
          </el-button>
          <el-button
            v-else
            type="primary"
            @click="goToProducts"
          >
            查看商品
          </el-button>
        </el-empty>
      </div>

      <!-- 分页 -->
      <div v-if="totalPages > 1" class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="totalElements"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Header from '@/components/Header.vue'
import { useOrderStore } from '@/stores/order'
import OrderCard from '@/components/order/OrderCard.vue'
import OrderStatusFilter from '@/components/order/OrderStatusFilter.vue'
import OrderActions from '@/components/order/OrderActions.vue'
import type { OrderQueryParams, OrderStatus } from '@/types/order'
import { useAuthStore } from '@/stores/auth'

// 路由
const router = useRouter()

// Store
const orderStore = useOrderStore()
const authStore = useAuthStore()

// 状态
const selectedStatus = ref<string>('')
const currentPage = ref<number>(1)
const pageSize = ref<number>(10)

// 计算属性
const filteredOrders = computed(() => {
  if (!selectedStatus.value) return orderStore.orders
  return orderStore.orders.filter(order => order.status === selectedStatus.value)
})

const totalElements = computed(() => {
  return orderStore.totalElements
})

const totalPages = computed(() => {
  return orderStore.totalPages
})

// 商家订单按状态统计
const sellerOrdersByStatus = computed(() => {
  const result: Record<string, number> = {
    '': 0,
    PENDING: 0,
    PAID: 0,
    COMPLETED: 0,
    CANCELLED: 0
  }

  const ordersArray = orderStore.orders || []
  if (Array.isArray(ordersArray)) {
    ordersArray.forEach(order => {
      result[order.status] = (result[order.status] || 0) + 1
      result[''] += 1 // 全部计数
    })
  }

  return result
})

// 方法
/**
 * 获取商家订单列表
 */
const loadSellerOrders = async () => {
  const params: OrderQueryParams = {
    page: currentPage.value - 1,
    size: pageSize.value,
    status: selectedStatus.value || undefined
  }

  // 调用获取商家订单的API
  const response = await orderStore.fetchSellerOrders(params)
  if (!response) {
    ElMessage.error('获取商家订单失败')
  }
}

/**
 * 处理更新订单状态
 */
const handleUpdateStatus = async (orderId: number, status: OrderStatus) => {
  try {
    const success = await orderStore.updateOrderStatus(orderId, status)
    if (success) {
      // 重新加载订单列表
      await loadSellerOrders()
      ElMessage.success('订单状态更新成功')
    }
  } catch (error) {
    ElMessage.error('订单状态更新失败')
    console.error('更新订单状态失败:', error)
  }
}

/**
 * 获取订单统计
 */
const loadStatistics = async () => {
  await orderStore.fetchStatistics()
}

/**
 * 状态筛选变化
 */
const handleStatusChange = (status: string) => {
  selectedStatus.value = status
  currentPage.value = 1 // 重置到第一页
  loadSellerOrders()
}

/**
 * 分页大小变化
 */
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1 // 重置到第一页
  loadSellerOrders()
}

/**
 * 页码变化
 */
const handlePageChange = (page: number) => {
  currentPage.value = page
  loadSellerOrders()
}

/**
 * 查看订单详情
 */
const goToOrderDetail = (orderId: number) => {
  router.push(`/orders/${orderId}`)
}

/**
 * 查看订单详情（从卡片组件）
 */
const handleViewDetail = (orderId: number) => {
  goToOrderDetail(orderId)
}

/**
 * 获取空状态描述
 */
const getEmptyDescription = () => {
  if (selectedStatus.value) {
    return `暂无${getStatusText(selectedStatus.value)}的订单`
  }
  return '暂无相关订单'
}

/**
 * 获取状态文本
 */
const getStatusText = (status: string): string => {
  const statusMap: Record<string, string> = {
    PENDING: '待处理',
    PAID: '已支付',
    SHIPPED: '已发货',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return statusMap[status] || status
}

/**
 * 清空筛选
 */
const clearFilter = () => {
  selectedStatus.value = ''
  loadSellerOrders()
}

/**
 * 跳转到商品页面
 */
const goToProducts = () => {
  router.push('/products')
}

// 生命周期钩子
onMounted(async () => {
  // 加载订单统计
  await loadStatistics()

  // 加载商家订单列表
  await loadSellerOrders()
})

// 监听状态变化
watch(selectedStatus, () => {
  // 状态变化时重新加载
  loadSellerOrders()
})
</script>

<style scoped>
.seller-order-list-page {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.loading-container {
  padding: 40px 20px;
}

.error-alert {
  margin: 20px;
}

.order-content {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.stats-card {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.filter-section {
  margin-bottom: 20px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 20px;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .order-content {
    padding: 10px;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }

  .stat-value {
    font-size: 24px;
  }
}
</style>