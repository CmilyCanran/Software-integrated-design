<template>
  <div v-if="!simplified" class="order-item-card" :class="{ 'order-item-card--readonly': readonly }">
    <!-- 使用通用商品显示组件 -->
    <ProductDisplay
      :product="item"
      :showQuantity="true"
      :readonly="readonly"
      @click="handleViewProduct"
    />

    <!-- 操作按钮（可选） -->
    <div v-if="showActions && !readonly" class="item-actions">
      <el-button
        type="primary"
        link
        size="small"
        @click="handleViewProduct"
      >
        查看商品
      </el-button>

      <el-button
        v-if="canReturn"
        type="warning"
        link
        size="small"
        @click="handleReturn"
      >
        申请退货
      </el-button>
    </div>
  </div>
  <div v-else>
    <!-- 简化模式：直接使用ProductDisplay，减少一层嵌套 -->
    <ProductDisplay
      :product="item"
      :showQuantity="true"
      :readonly="readonly"
      @click="handleViewProduct"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import type { OrderItem } from '@/types/order'
import { ElMessage } from 'element-plus'
import ProductDisplay from '@/components/ProductDisplay.vue'

// Props定义
interface Props {
  item: OrderItem
  showActions?: boolean
  readonly?: boolean
  canReturn?: boolean
  simplified?: boolean  // 简化模式，减少嵌套和操作按钮
}

// Props默认值
const props = withDefaults(defineProps<Props>(), {
  showActions: false,
  readonly: true,
  canReturn: false,
  simplified: false
})

// Emits定义
const emit = defineEmits<{
  viewProduct: [productId: number]
  return: [item: OrderItem]
}>()

// 路由
const router = useRouter()

// 方法
const handleViewProduct = () => {
  if (props.readonly) {
    // 只读模式下跳转到商品详情
    router.push(`/products/${props.item.productId}`)
  } else {
    // 触发查看商品事件
    emit('viewProduct', props.item.productId)
  }
}

const handleReturn = () => {
  emit('return', props.item)
  ElMessage.info('退货功能开发中...')
}
</script>

<style scoped lang="scss">
.order-item-card {
  display: flex;
  align-items: center;
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background-color: #fff;
  transition: all 0.3s ease;
  gap: 16px;

  &:hover {
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    transform: translateY(-1px);
  }

  &--readonly {
    cursor: pointer;

    &:hover {
      border-color: #409EFF;
    }
  }
}

.item-actions {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-left: 16px;
}

/* 简化模式下的样式调整 */
:deep(.product-display) {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  background-color: #fff;
  transition: all 0.3s ease;
  gap: 16px;
}

:deep(.product-display:hover) {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

// 响应式设计
@media (max-width: 768px) {
  .order-item-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .item-actions {
    flex-direction: row;
    margin-left: 0;
    width: 100%;
  }
}
</style>