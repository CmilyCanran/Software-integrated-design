<template>
  <div
    :class="['product-card', { 'hoverable': hoverable, 'clickable': clickable }]"
    @click="handleCardClick"
  >
    <!-- 商品图片 -->
    <div class="product-image">
      <img
        :src="productImage"
        :alt="product.name"
        @error="handleImageError"
      />

      <!-- 商品状态标签 -->
      <div class="product-badges">
        <StatusTag :status="product.isAvailable ? 'available' : 'unavailable'" />
      </div>

      <!-- 快速操作按钮 -->
      <div
        v-if="showQuickActions"
        class="quick-actions"
        @click.stop
      >
        <el-button
          type="primary"
          size="small"
          circle
          @click="handleQuickView"
        >
          <el-icon><View /></el-icon>
        </el-button>
        <el-button
          type="danger"
          size="small"
          circle
          @click="handleAddToCart"
        >
          <el-icon><ShoppingCart /></el-icon>
        </el-button>
      </div>
    </div>

    <!-- 商品信息 -->
    <div class="product-info">
      <!-- 商品名称 -->
      <h3
        :class="['product-name', { 'clickable': clickable }]"
        :title="product.name"
        @click="handleNameClick"
      >
        {{ product.name }}
      </h3>

      <!-- 商品描述 -->
      <p
        v-if="showDescription"
        class="product-description"
        :title="product.description"
      >
        {{ product.description }}
      </p>

      <!-- 价格和库存 -->
      <div class="product-meta">
        <PriceDisplay
          :price="product.price"
          :size="priceSize"
        />

        <StockIndicator
          :stock-quantity="product.stockQuantity"
          :low-stock-threshold="lowStockThreshold"
          :show-count="showStockCount"
          size="small"
        />
      </div>

      <!-- 操作按钮 -->
      <div
        v-if="showActions"
        class="product-actions"
        @click.stop
      >
        <el-button
          type="default"
          size="small"
          @click="handleViewDetails"
        >
          查看详情
        </el-button>
        <el-button
          v-if="canEdit"
          type="primary"
          size="small"
          @click="handleEdit"
        >
          编辑
        </el-button>
        <el-button
          v-if="canDelete"
          type="danger"
          size="small"
          @click="handleDelete"
        >
          删除
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { View, ShoppingCart } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import StatusTag from './StatusTag.vue'
import PriceDisplay from './PriceDisplay.vue'
import StockIndicator from './StockIndicator.vue'
import type { Product } from '@/types'

// 定义props接口
interface Props {
  product: Product                  // 商品数据
  hoverable?: boolean               // 是否悬停效果
  clickable?: boolean               // 是否可点击
  showDescription?: boolean         // 是否显示描述
  showQuickActions?: boolean        // 是否显示快速操作按钮
  showActions?: boolean             // 是否显示操作按钮
  showStockCount?: boolean          // 是否显示库存数量
  priceSize?: 'small' | 'medium' | 'large'  // 价格显示大小
  lowStockThreshold?: number        // 低库存阈值
  canEdit?: boolean                 // 是否可编辑
  canDelete?: boolean               // 是否可删除
  defaultImage?: string             // 默认图片
}

// 定义emits
interface Emits {
  (e: 'click', product: Product): void
  (e: 'name-click', product: Product): void
  (e: 'quick-view', product: Product): void
  (e: 'add-to-cart', product: Product): void
  (e: 'view-details', product: Product): void
  (e: 'edit', product: Product): void
  (e: 'delete', product: Product): void
}

// 使用withDefaults设置默认值
const props = withDefaults(defineProps<Props>(), {
  hoverable: true,
  clickable: true,
  showDescription: true,
  showQuickActions: true,
  showActions: false,
  showStockCount: false,
  priceSize: 'medium',
  lowStockThreshold: 10,
  canEdit: false,
  canDelete: false,
  defaultImage: '/placeholder-product.png'
})

// 定义emits
const emit = defineEmits<Emits>()

// 计算属性：商品图片
const productImage = computed<string>(() => {
  return props.product.image || props.defaultImage
})

// 事件处理函数
const handleCardClick = (): void => {
  if (props.clickable) {
    emit('click', props.product)
  }
}

const handleNameClick = (): void => {
  if (props.clickable) {
    emit('name-click', props.product)
  }
}

const handleQuickView = (): void => {
  emit('quick-view', props.product)
}

const handleAddToCart = (): void => {
  // 检查库存
  if (props.product.stockQuantity === 0) {
    ElMessage.warning('商品库存不足')
    return
  }

  emit('add-to-cart', props.product)
}

const handleViewDetails = (): void => {
  emit('view-details', props.product)
}

const handleEdit = (): void => {
  emit('edit', props.product)
}

const handleDelete = (): void => {
  emit('delete', props.product)
}

const handleImageError = (event: Event): void => {
  const img = event.target as HTMLImageElement
  img.src = props.defaultImage
}

// 暴露方法给父组件
defineExpose({
  productImage
})
</script>

<style scoped>
.product-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  transition: all 0.3s ease;
  max-width: 300px;
}

.product-card.hoverable:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.product-card.clickable {
  cursor: pointer;
}

.product-image {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card.hoverable:hover .product-image img {
  transform: scale(1.05);
}

.product-badges {
  position: absolute;
  top: 8px;
  left: 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.quick-actions {
  position: absolute;
  top: 8px;
  right: 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.product-card.hoverable:hover .quick-actions {
  opacity: 1;
}

.product-info {
  padding: 16px;
}

.product-name {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  min-height: 44.8px;
}

.product-name.clickable {
  cursor: pointer;
  transition: color 0.3s ease;
}

.product-name.clickable:hover {
  color: #409eff;
}

.product-description {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  min-height: 42px;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  flex-wrap: wrap;
  gap: 8px;
}

.product-actions {
  display: flex;
  gap: 8px;
  justify-content: stretch;
}

.product-actions .el-button {
  flex: 1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-card {
    max-width: 100%;
  }

  .product-image {
    height: 160px;
  }

  .product-info {
    padding: 12px;
  }

  .product-name {
    font-size: 14px;
  }

  .product-description {
    font-size: 12px;
  }

  .product-actions {
    flex-direction: column;
  }

  .product-actions .el-button {
    flex: none;
  }
}
</style>