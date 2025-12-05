<template>
  <div class="order-actions">
    <!-- 买家操作 -->
    <template v-if="userRole === 'USER'">
      <!-- 待处理状态 - 可以取消订单 -->
      <template v-if="order.status === 'PENDING'">
        <el-button
          type="danger"
          size="small"
          @click="handleCancelOrder"
          :loading="loading"
        >
          <el-icon><CircleClose /></el-icon>
          取消订单
        </el-button>
      </template>

      <!-- 已发货状态 - 可以确认收货 -->
      <template v-if="order.status === 'SHIPPED'">
        <el-button
          type="primary"
          size="small"
          @click="handleConfirmReceipt"
          :loading="loading"
        >
          <el-icon><CircleCheck /></el-icon>
          确认收货
        </el-button>
      </template>

      <!-- 已完成状态 - 可以评价 -->
      <template v-if="order.status === 'COMPLETED'">
        <el-button
          type="info"
          size="small"
          @click="handleReview"
          :loading="loading"
        >
          <el-icon><Star /></el-icon>
          评价商品
        </el-button>
      </template>

      <!-- 已取消状态 - 可以重新购买 -->
      <template v-if="order.status === 'CANCELLED'">
        <el-button
          type="primary"
          size="small"
          @click="handleRebuy"
          :loading="loading"
        >
          <el-icon><ShoppingCart /></el-icon>
          重新购买
        </el-button>
      </template>
    </template>

    <!-- 商家操作 -->
    <template v-else-if="userRole === 'SELLER'">
      <!-- 待处理状态 - 可以标记为已发货 -->
      <template v-if="order.status === 'PENDING'">
        <el-button
          type="primary"
          size="small"
          @click="handleMarkAsShipped"
          :loading="loading"
        >
          <el-icon><Document /></el-icon>
          标记发货
        </el-button>
      </template>

      <!-- 已发货状态 - 可以查看物流 -->
      <template v-if="order.status === 'SHIPPED'">
        <el-button
          type="info"
          size="small"
          @click="handleViewShipping"
          :loading="loading"
        >
          <el-icon><Document /></el-icon>
          查看物流
        </el-button>
      </template>
    </template>

    <!-- 管理员操作 -->
    <template v-else-if="userRole === 'ADMIN'">
      <!-- 可以更新任何订单状态 -->
      <el-dropdown
        trigger="click"
        @command="handleStatusUpdate"
      >
        <el-button
          type="primary"
          size="small"
          :loading="loading"
        >
          <el-icon><Edit /></el-icon>
          更新状态
          <el-icon class="el-icon--right"><ArrowDown /></el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="PENDING">
              <el-icon><Clock /></el-icon>
              设为待处理
            </el-dropdown-item>
            <el-dropdown-item command="PAID">
              <el-icon><Money /></el-icon>
              设为已支付
            </el-dropdown-item>
            <el-dropdown-item command="SHIPPED">
              <el-icon><Document /></el-icon>
              设为已发货
            </el-dropdown-item>
            <el-dropdown-item command="COMPLETED">
              <el-icon><CircleCheck /></el-icon>
              设为已完成
            </el-dropdown-item>
            <el-dropdown-item command="CANCELLED" divided>
              <el-icon><CircleClose /></el-icon>
              设为已取消
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </template>

    <!-- 通用操作 -->
    <div class="common-actions">
      <!-- 查看订单详情 -->
      <el-button
        type="info"
        size="small"
        @click="handleViewDetail"
        :loading="loading"
      >
        <el-icon><View /></el-icon>
        查看详情
      </el-button>
    </div>

    <!-- 加载遮罩 -->
    <div v-if="loading" class="loading-overlay">
      <el-loading :model-value="true" text="处理中..." />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import {
  CircleClose,
  CircleCheck,
  Star,
  ShoppingCart,
  Document,
  Edit,
  ArrowDown,
  Clock,
  Money,
  View
} from '@element-plus/icons-vue'
import type { Order, OrderStatus } from '@/types/order'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'

// Props定义
interface Props {
  order: Order
  userRole: 'USER' | 'SELLER' | 'ADMIN'
}

const props = defineProps<Props>()

// Emits定义
const emit = defineEmits<{
  cancelOrder: [orderId: number]
  confirmReceipt: [orderId: number]
  updateStatus: [orderId: number, status: OrderStatus]
  review: [orderId: number]
  rebuy: [order: Order]
  markAsShipped: [orderId: number]
  viewShipping: [orderId: number]
  viewDetail: [orderId: number]
}>()

// 状态
const loading = ref(false)

// 方法
const handleCancelOrder = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要取消这个订单吗？取消后无法恢复。',
      '确认取消',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    loading.value = true
    emit('cancelOrder', props.order.id)
    ElMessage.success('订单取消成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('订单取消失败')
    }
  } finally {
    loading.value = false
  }
}

const handleConfirmReceipt = async () => {
  try {
    await ElMessageBox.confirm(
      '确认已收到商品？确认后将无法申请退款。',
      '确认收货',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    loading.value = true
    emit('confirmReceipt', props.order.id)
    ElMessage.success('确认收货成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('确认收货失败')
    }
  } finally {
    loading.value = false
  }
}

const handleReview = () => {
  emit('review', props.order.id)
  ElMessage.info('评价功能开发中...')
}

const handleRebuy = () => {
  emit('rebuy', props.order)
  ElMessage.info('重新购买功能开发中...')
}

const handleMarkAsShipped = async () => {
  try {
    await ElMessageBox.prompt(
      '请输入物流单号（可选）：',
      '标记发货',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '物流单号（选填）'
      }
    )

    loading.value = true
    emit('markAsShipped', props.order.id)
    ElMessage.success('标记发货成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('标记发货失败')
    }
  } finally {
    loading.value = false
  }
}

const handleViewShipping = () => {
  emit('viewShipping', props.order.id)
  ElMessage.info('物流信息功能开发中...')
}

const handleStatusUpdate = (command: OrderStatus) => {
  ElMessageBox.confirm(
    `确定要将订单状态更新为"${ORDER_STATUS_DESCRIPTIONS[command]}"吗？`,
    '确认更新',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    loading.value = true
    emit('updateStatus', props.order.id, command)
    ElMessage.success('订单状态更新成功')
  }).catch(() => {
    // 用户取消
  }).finally(() => {
    loading.value = false
  })
}

const handleViewDetail = () => {
  emit('viewDetail', props.order.id)
}
</script>

<style scoped>
.order-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  position: relative;
}

.common-actions {
  display: flex;
  gap: 8px;
  margin-left: auto;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .order-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .common-actions {
    flex-direction: row;
    margin-left: 0;
    width: 100%;
    justify-content: space-between;
  }

  :deep(.el-button) {
    flex: 1;
    justify-content: center;
  }
}
</style>