<template>
  <div class="stock-indicator">
    <!-- 库存状态图标 -->
    <el-icon
      :class="['stock-icon', stockStatus]"
      :size="iconSize"
    >
      <component :is="stockIcon" />
    </el-icon>

    <!-- 库存文本 -->
    <span
      :class="['stock-text', stockStatus, size]"
    >
      {{ stockText }}
    </span>

    <!-- 库存数量（可选） -->
    <span
      v-if="showCount && stockQuantity >= 0"
      class="stock-count"
    >
      ({{ stockQuantity }})
    </span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import {
  Check,
  Warning,
  CircleClose,
  RemoveFilled
} from '@element-plus/icons-vue'

// 定义库存状态类型
type StockStatus = 'in-stock' | 'low-stock' | 'out-of-stock' | 'unlimited'

// 定义props接口
interface Props {
  stockQuantity: number             // 库存数量
  lowStockThreshold?: number        // 低库存阈值
  showCount?: boolean               // 是否显示具体数量
  size?: 'small' | 'medium' | 'large'  // 组件尺寸
  showUnlimitedAs?: StockStatus     // 无限库存的显示方式
}

// 使用withDefaults设置默认值
const props = withDefaults(defineProps<Props>(), {
  lowStockThreshold: 10,
  showCount: false,
  size: 'medium',
  showUnlimitedAs: 'in-stock'
})

// 计算属性：库存状态
const stockStatus = computed<StockStatus>(() => {
  // 如果库存数量为负数或undefined，视为无限库存
  if (props.stockQuantity < 0) {
    return props.showUnlimitedAs
  }

  // 库存为0
  if (props.stockQuantity === 0) {
    return 'out-of-stock'
  }

  // 低库存
  if (props.stockQuantity <= props.lowStockThreshold) {
    return 'low-stock'
  }

  // 库存充足
  return 'in-stock'
})

// 计算属性：库存文本
const stockText = computed<string>(() => {
  switch (stockStatus.value) {
    case 'in-stock':
      return '库存充足'
    case 'low-stock':
      return '库存紧张'
    case 'out-of-stock':
      return '缺货'
    case 'unlimited':
      return '无限供应'
    default:
      return '未知状态'
  }
})

// 计算属性：库存图标
const stockIcon = computed(() => {
  switch (stockStatus.value) {
    case 'in-stock':
      return Check
    case 'low-stock':
      return Warning
    case 'out-of-stock':
      return CircleClose
    case 'unlimited':
      return RemoveFilled
    default:
      return Warning
  }
})

// 计算属性：图标尺寸
const iconSize = computed(() => {
  switch (props.size) {
    case 'small':
      return 14
    case 'medium':
      return 16
    case 'large':
      return 20
    default:
      return 16
  }
})

// 计算属性：是否可购买
const isPurchasable = computed<boolean>(() => {
  return stockStatus.value === 'in-stock' || stockStatus.value === 'unlimited'
})

// 暴露给父组件的方法和属性
defineExpose({
  stockStatus,
  isPurchasable,
  stockText
})
</script>

<style scoped>
.stock-indicator {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: inherit;
}

.stock-icon {
  display: flex;
  align-items: center;
}

.stock-icon.in-stock {
  color: #67c23a;
}

.stock-icon.low-stock {
  color: #e6a23c;
}

.stock-icon.out-of-stock {
  color: #f56c6c;
}

.stock-icon.unlimited {
  color: #909399;
}

.stock-text {
  font-weight: 500;
}

.stock-text.small {
  font-size: 12px;
}

.stock-text.medium {
  font-size: 14px;
}

.stock-text.large {
  font-size: 16px;
}

.stock-text.in-stock {
  color: #67c23a;
}

.stock-text.low-stock {
  color: #e6a23c;
}

.stock-text.out-of-stock {
  color: #f56c6c;
}

.stock-text.unlimited {
  color: #909399;
}

.stock-count {
  color: #666;
  font-size: 0.9em;
  font-weight: normal;
}
</style>