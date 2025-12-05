<template>
  <div class="order-status" :class="statusClass">
    <!-- 状态图标 -->
    <el-icon v-if="showIcon" :size="iconSize" class="status-icon">
      <component :is="statusIcon" />
    </el-icon>

    <!-- 状态文本 -->
    <span v-if="showText" class="status-text">
      {{ statusText }}
    </span>

    <!-- 状态描述（可选） -->
    <span v-if="showDescription" class="status-description">
      {{ statusDescription }}
    </span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { OrderStatus } from '@/types/order'
import {
  ORDER_STATUS_DESCRIPTIONS,
  ORDER_STATUS_COLORS,
  ORDER_STATUS_ICONS
} from '@/types/order'

// Props定义
interface Props {
  status: OrderStatus
  size?: 'small' | 'medium' | 'large'
  showIcon?: boolean
  showText?: boolean
  showDescription?: boolean
}

// Props默认值
const props = withDefaults(defineProps<Props>(), {
  size: 'medium',
  showIcon: true,
  showText: true,
  showDescription: false
})

// 计算属性
const statusText = computed(() => {
  return ORDER_STATUS_DESCRIPTIONS[props.status]
})

const statusDescription = computed(() => {
  // 可以添加更详细的状态描述
  const descriptions = {
    PENDING: '订单等待处理',
    PAID: '订单已支付，等待发货',
    SHIPPED: '订单已发货，等待收货',
    COMPLETED: '订单已完成',
    CANCELLED: '订单已取消'
  }
  return descriptions[props.status]
})

const statusColor = computed(() => {
  return ORDER_STATUS_COLORS[props.status]
})

const statusIcon = computed(() => {
  return ORDER_STATUS_ICONS[props.status]
})

const iconSize = computed(() => {
  const sizeMap = {
    small: 14,
    medium: 16,
    large: 20
  }
  return sizeMap[props.size]
})

const statusClass = computed(() => {
  return [
    `order-status--${props.size}`,
    `order-status--${props.status.toLowerCase()}`
  ]
})
</script>

<style scoped>
.order-status {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
}

.order-status--small {
  font-size: 12px;
  gap: 2px;
}

.order-status--medium {
  font-size: 14px;
  gap: 4px;
}

.order-status--large {
  font-size: 16px;
  gap: 6px;
}

.order-status--pending {
  color: #E6A23C;
}

.order-status--paid {
  color: #409EFF;
}

.order-status--shipped {
  color: #67C23A;
}

.order-status--completed {
  color: #67C23A;
}

.order-status--cancelled {
  color: #F56C6C;
}

.status-icon {
  color: inherit;
}

.status-text {
  color: inherit;
}

.status-description {
  color: #909399;
  font-size: 12px;
  margin-left: 4px;
}</style>