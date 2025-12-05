<template>
  <div class="order-card" :class="{ 'order-card--compact': compact }" @click="handleClick">
    <!-- 订单头部信息 -->
    <div class="order-header">
      <div class="order-info">
        <span class="order-number">订单号: #{{ order.id }}</span>
        <span class="order-date">{{ formatDate(order.createdAt) }}</span>
      </div>
      <OrderStatus :status="order.status" size="small" />
    </div>

    <!-- 商品预览 -->
    <div class="order-items-preview">
      <div
        v-for="(item, index) in previewItems"
        :key="index"
        class="item-preview"
      >
        <el-image
          :src="item.productImage || '/placeholder/product.png'"
          :alt="item.productName"
          fit="cover"
          class="preview-image"
          :preview-src-list="[item.productImage || '/placeholder/product.png']"
          :z-index="2000"
        />
      </div>
      <div v-if="remainingItemsCount > 0" class="remaining-count">
        +{{ remainingItemsCount }}
      </div>
    </div>

    <!-- 订单统计 -->
    <div class="order-stats">
      <div class="stat-item">
        <span class="stat-label">共 {{ order.quantity }} 件商品</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">总金额:</span>
        <span class="stat-value">¥{{ order.totalAmount.toFixed(2) }}</span>
      </div>
    </div>

    <!-- 订单底部信息 -->
    <div class="order-footer">
      <div class="seller-info">
        <el-icon size="14">
          <Shop />
        </el-icon>
        <span class="seller-name">{{ order.sellerName }}</span>
      </div>
      <div class="order-actions">
        <el-button
          v-if="showDetailButton"
          type="primary"
          link
          size="small"
          @click.stop="handleViewDetail"
        >
          查看详情
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { Shop } from '@element-plus/icons-vue'
import type { Order } from '@/types/order'
import OrderStatus from './OrderStatus.vue'

// Props定义
interface Props {
  order: Order
  compact?: boolean
  showDetailButton?: boolean
}

// Props默认值
const props = withDefaults(defineProps<Props>(), {
  compact: false,
  showDetailButton: true
})

// Emits定义
const emit = defineEmits<{
  click: [order: Order]
  viewDetail: [orderId: number]
}>()

// 路由
const router = useRouter()

// 计算属性
const previewItems = computed(() => {
  // 对于单商品订单，创建一个商品预览项
  if (props.order) {
    return [{
      productImage: props.order.productImage,
      productName: props.order.productName,
      quantity: props.order.quantity
    }]
  }
  return []
})

const remainingItemsCount = computed(() => {
  // 对于单商品订单，始终为0
  return 0
})

// 方法
const handleClick = () => {
  emit('click', props.order)
}

const handleViewDetail = () => {
  emit('viewDetail', props.order.id)
  // 或者跳转到订单详情页
  router.push(`/orders/${props.order.id}`)
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) {
    const hours = Math.floor(diff / (1000 * 60 * 60))
    if (hours === 0) {
      const minutes = Math.floor(diff / (1000 * 60))
      return minutes <= 1 ? '刚刚' : `${minutes}分钟前`
    }
    return `${hours}小时前`
  } else if (days === 1) {
    return '昨天'
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    })
  }
}
</script>

<style scoped>
.order-card {
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background-color: #fff;
  cursor: pointer;
  transition: all 0.3s ease;
}

.order-card:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.order-card--compact {
  padding: 12px;
  font-size: 13px;
}

.order-card--compact .order-header {
  margin-bottom: 8px;
}

.order-card--compact .order-items-preview {
  margin-bottom: 8px;
}

.order-card--compact .order-stats {
  margin-bottom: 8px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.order-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.order-number {
  font-weight: 600;
  color: #303133;
}

.order-date {
  color: #909399;
  font-size: 12px;
}

.order-items-preview {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  padding: 8px 0;
}

.item-preview {
  width: 48px;
  height: 48px;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remaining-count {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background-color: #f5f7fa;
  border-radius: 4px;
  font-size: 12px;
  color: #909399;
  font-weight: 500;
}

.order-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 8px 0;
  border-top: 1px solid #f0f2f5;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-label {
  color: #909399;
  font-size: 12px;
}

.stat-value {
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f2f5;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
  font-size: 13px;
}

.seller-name {
  color: #303133;
  font-weight: 500;
}

.order-actions {
  display: flex;
  gap: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .order-card {
    padding: 12px;
  }

  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .order-stats {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .order-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .order-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>