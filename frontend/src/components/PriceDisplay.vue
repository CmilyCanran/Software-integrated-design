<template>
  <div class="price-display">
    <!-- 当前价格 -->
    <span :class="['current-price', size]">
      {{ currency }}{{ formatPrice(price) }}
    </span>
  </div>
</template>

<script setup lang="ts">
// 定义props接口
interface Props {
  price: number                      // 当前价格
  size?: 'small' | 'medium' | 'large'  // 价格尺寸
  currency?: string                 // 货币符号
}

// 使用withDefaults设置默认值
const props = withDefaults(defineProps<Props>(), {
  size: 'medium',
  currency: '¥'
})

// 格式化价格函数
const formatPrice = (price: number): string => {
  return price.toFixed(2)
}
</script>

<style scoped>
.price-display {
  display: inline-flex;
  align-items: center;
}

.current-price {
  font-weight: 600;
  color: #333;
}

.current-price.small {
  font-size: 14px;
}

.current-price.medium {
  font-size: 16px;
}

.current-price.large {
  font-size: 20px;
}
</style>