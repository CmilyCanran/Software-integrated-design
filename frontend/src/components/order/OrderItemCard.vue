<template>
  <div class="order-item-card" :class="{ 'order-item-card--readonly': readonly }">
    <!-- 商品图片 -->
    <div class="item-image" @click="handleImageClick">
      <el-image
        :src="item.productImage || '/placeholder/product.png'"
        :alt="item.productName"
        fit="cover"
        class="product-image"
        @error="handleImageError"
      >
        <template #error>
          <div class="image-error">
            <el-icon :size="32">
              <Picture />
            </el-icon>
          </div>
        </template>
      </el-image>
    </div>

    <!-- 商品信息 -->
    <div class="item-info">
      <h4 class="item-name" @click="handleNameClick">
        {{ item.productName }}
      </h4>

      <!-- 商品规格 -->
      <div v-if="item.specifications && Object.keys(item.specifications).length > 0" class="item-specs">
        <el-tag
          v-for="(value, key) in item.specifications"
          :key="key"
          size="small"
          type="info"
          class="spec-tag"
        >
          {{ key }}: {{ value }}
        </el-tag>
      </div>

      <!-- 单价 -->
      <div class="item-price">
        <span class="unit-price">单价: ¥{{ item.unitPrice.toFixed(2) }}</span>
      </div>
    </div>

    <!-- 数量和价格 -->
    <div class="item-quantity-price">
      <div class="quantity">
        <span class="quantity-label">数量:</span>
        <span class="quantity-value">{{ item.quantity }}</span>
      </div>

      <div class="subtotal">
        <span class="subtotal-label">小计:</span>
        <span class="subtotal-value">¥{{ item.subtotal.toFixed(2) }}</span>
      </div>
    </div>

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
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Picture } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import type { OrderItem } from '@/types/order'
import { ElMessage } from 'element-plus'

// Props定义
interface Props {
  item: OrderItem
  showActions?: boolean
  readonly?: boolean
  canReturn?: boolean
}

// Props默认值
const props = withDefaults(defineProps<Props>(), {
  showActions: false,
  readonly: true,
  canReturn: false
})

// Emits定义
const emit = defineEmits<{
  viewProduct: [productId: number]
  return: [item: OrderItem]
}>()

// 路由
const router = useRouter()

// 方法
const handleImageClick = () => {
  handleViewProduct()
}

const handleNameClick = () => {
  handleViewProduct()
}

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

const handleImageError = () => {
  console.warn('商品图片加载失败:', props.item.productImage)
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

.item-image {
  flex-shrink: 0;
  width: 80px;
  height: 80px;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
}

.product-image {
  width: 100%;
  height: 100%;
}

.image-error {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  color: #909399;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  cursor: pointer;
  transition: color 0.3s;

  &:hover {
    color: #409EFF;
  }
}

.item-specs {
  margin-bottom: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.spec-tag {
  margin: 0;
}

.item-price {
  font-size: 14px;
  color: #606266;
}

.unit-price {
  color: #909399;
}

.item-quantity-price {
  text-align: right;
  min-width: 120px;
}

.quantity {
  margin-bottom: 8px;

  .quantity-label {
    color: #909399;
    margin-right: 4px;
  }

  .quantity-value {
    color: #303133;
    font-weight: 500;
  }
}

.subtotal {
  .subtotal-label {
    color: #909399;
    margin-right: 4px;
  }

  .subtotal-value {
    color: #303133;
    font-size: 16px;
    font-weight: 600;
  }
}

.item-actions {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-left: 16px;
}

// 响应式设计
@media (max-width: 768px) {
  .order-item-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .item-image {
    width: 60px;
    height: 60px;
  }

  .item-quantity-price {
    text-align: left;
    width: 100%;
  }

  .item-actions {
    flex-direction: row;
    margin-left: 0;
    width: 100%;
  }
}
</style>