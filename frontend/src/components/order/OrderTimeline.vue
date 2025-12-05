<template>
  <div class="order-timeline">
    <el-timeline>
      <!-- 订单创建 -->
      <el-timeline-item
        :timestamp="formatDateTime(order.createdAt)"
        :type="getStatusType('PENDING')"
        :icon="getStatusIcon('PENDING')"
        hollow
      >
        <div class="timeline-item">
          <h4>订单创建</h4>
          <p class="status-desc">订单已创建，等待买家付款</p>
          <p class="order-info">订单号: #{{ order.id }}</p>
        </div>
      </el-timeline-item>

      <!-- 订单支付 -->
      <el-timeline-item
        v-if="order.status !== 'PENDING'"
        :timestamp="order.updatedAt ? formatDateTime(order.updatedAt) : formatDateTime(order.createdAt)"
        :type="getStatusType('PAID')"
        :icon="getStatusIcon('PAID')"
        hollow
      >
        <div class="timeline-item">
          <h4>订单支付</h4>
          <p class="status-desc">订单已支付，等待商家发货</p>
          <p class="order-info">金额: ¥{{ order.totalAmount.toFixed(2) }}</p>
        </div>
      </el-timeline-item>


      <!-- 订单完成 -->
      <el-timeline-item
        v-if="order.status === 'COMPLETED'"
        :timestamp="order.updatedAt ? formatDateTime(order.updatedAt) : formatDateTime(order.createdAt)"
        :type="getStatusType('COMPLETED')"
        :icon="getStatusIcon('COMPLETED')"
        hollow
      >
        <div class="timeline-item">
          <h4>订单完成</h4>
          <p class="status-desc">订单已完成，交易结束</p>
          <p class="order-info">感谢您的购买！</p>
        </div>
      </el-timeline-item>

      <!-- 订单取消 -->
      <el-timeline-item
        v-if="order.status === 'CANCELLED'"
        :timestamp="order.updatedAt ? formatDateTime(order.updatedAt) : formatDateTime(order.createdAt)"
        :type="getStatusType('CANCELLED')"
        :icon="getStatusIcon('CANCELLED')"
        hollow
      >
        <div class="timeline-item">
          <h4>订单取消</h4>
          <p class="status-desc">订单已取消</p>
          <p class="order-info" v-if="order.remarks">{{ order.remarks }}</p>
        </div>
      </el-timeline-item>
    </el-timeline>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { Order, OrderStatus } from '@/types/order'
import {
  Clock,
  Money,
  Document,
  CircleCheck,
  CircleClose
} from '@element-plus/icons-vue'

// Props定义
interface Props {
  order: Order
}

const props = defineProps<Props>()

// 计算属性：获取状态类型（用于颜色）
const getStatusType = (status: OrderStatus) => {
  const statusTypes: Record<OrderStatus, string> = {
    PENDING: 'primary',
    PAID: 'warning',
    SHIPPED: 'success',
    COMPLETED: 'success',
    CANCELLED: 'info'
  }
  return statusTypes[status]
}

// 计算属性：获取状态图标
const getStatusIcon = (status: OrderStatus) => {
  const statusIcons: Record<OrderStatus, any> = {
    PENDING: Clock,
    PAID: Money,
    SHIPPED: Document,
    COMPLETED: CircleCheck,
    CANCELLED: CircleClose
  }
  return statusIcons[status]
}

// 格式化日期时间
const formatDateTime = (dateString: string) => {
  try {
    const date = new Date(dateString)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch (error) {
    console.error('日期格式化失败:', error)
    return dateString
  }
}
</script>

<style scoped>
.order-timeline {
  padding: 10px 0;
}

.timeline-item h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.timeline-item .status-desc {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #606266;
}

.timeline-item .order-info {
  margin: 0;
  font-size: 12px;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .order-timeline {
    padding: 5px 0;
  }

  .timeline-item h4 {
    font-size: 14px;
  }

  .timeline-item .status-desc,
  .timeline-item .order-info {
    font-size: 12px;
  }
}
</style>