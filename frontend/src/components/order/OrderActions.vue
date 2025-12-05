<template>
  <div class="order-actions">
    <!-- 买家操作 -->
    <template v-if="userRole === 'USER'">
      <!-- 待处理状态 - 可以支付或取消订单 -->
      <template v-if="order.status === 'PENDING'">
        <el-button
          type="primary"
          size="small"
          @click="handlePayOrder"
          :loading="loading"
        >
          <el-icon><Money /></el-icon>
          确认支付
        </el-button>
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
      <!-- 订单待处理 - 商家无法操作（等待买家支付） -->
      <template v-if="order.status === 'PENDING'">
        <el-button
          type="info"
          size="small"
          :disabled="true"
        >
          <el-icon><Clock /></el-icon>
          等待买家支付
        </el-button>
      </template>

      <!-- 订单已支付 - 商家可以发货 -->
      <template v-else-if="order.status === 'PAID'">
        <el-button
          type="success"
          size="small"
          @click="handleShipOrder"
          :loading="loading"
        >
          <el-icon><Document /></el-icon>
          确认发货
        </el-button>
      </template>

      <!-- 订单已发货 - 商家无法操作（等待买家确认收货） -->
      <template v-else-if="order.status === 'SHIPPED'">
        <el-button
          type="info"
          size="small"
          :disabled="true"
        >
          <el-icon><Document /></el-icon>
          已发货待确认
        </el-button>
      </template>

      <!-- 订单已完成 - 商家无法操作 -->
      <template v-else-if="order.status === 'COMPLETED'">
        <el-button
          type="success"
          size="small"
          :disabled="true"
        >
          <el-icon><CircleCheck /></el-icon>
          订单已完成
        </el-button>
      </template>

      <!-- 订单已取消 - 商家无法操作 -->
      <template v-else-if="order.status === 'CANCELLED'">
        <el-button
          type="danger"
          size="small"
          :disabled="true"
        >
          <el-icon><CircleClose /></el-icon>
          订单已取消
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
import { ORDER_STATUS_DESCRIPTIONS } from '@/types/order'
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
  payOrder: [orderId: number]
  updateStatus: [orderId: number, status: OrderStatus]
  review: [orderId: number]
  rebuy: [order: Order]
  viewDetail: [orderId: number]
}>()

// 状态
const loading = ref(false)

// 方法
const handlePayOrder = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要支付 ¥${props.order.totalAmount.toFixed(2)} 订单吗？支付后订单将进入处理流程。`,
      '确认支付',
      {
        confirmButtonText: '确定支付',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    loading.value = true
    emit('payOrder', props.order.id)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('订单支付取消')
    }
  } finally {
    loading.value = false
  }
}

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
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('订单取消操作失败')
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
  }).catch(() => {
    // 用户取消
  }).finally(() => {
    loading.value = false
  })
}

const handleViewDetail = () => {
  emit('viewDetail', props.order.id)
}

const handleShipOrder = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要将订单标记为已发货吗？发货后买家将收到通知并可以确认收货。',
      '确认发货',
      {
        confirmButtonText: '确定发货',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    loading.value = true
    emit('updateStatus', props.order.id, 'SHIPPED')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('订单发货取消')
    }
  } finally {
    loading.value = false
  }
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