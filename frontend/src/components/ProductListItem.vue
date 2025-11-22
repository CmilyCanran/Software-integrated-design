<template>
  <div
    class="product-list-item"
    @click="handleClick"
  >
    <!-- 商品图片 -->
    <div class="product-image">
      <ImageLoader
        :src="productImage"
        :alt="product.productName"
        :placeholder="defaultImage"
        :fallback="defaultImage"
        class="product-image-loader"
      />
      <!-- 商品状态标签 -->
      <StatusTag :status="product.isAvailable ? 'available' : 'unavailable'" />
    </div>

    <!-- 商品信息 -->
    <div class="product-info">
      <h3>{{ product.productName }}</h3>
      <p class="description">{{ product.description }}</p>

      <!-- 价格和库存信息 -->
      <div class="product-meta">
        <PriceDisplay
          :price="product.price"
          :size="priceSize"
        />
        <StockIndicator
          :stock-quantity="product.stockQuantity"
          :show-count="showStockCount"
        />
      </div>

      <!-- 商品规格 -->
      <div
        v-if="hasSpecifications"
        class="product-specifications"
      >
        <div
          v-for="(values, attributeName) in productSpecifications"
          :key="attributeName"
          class="spec-item"
        >
          <span class="spec-name">{{ attributeName }}:</span>
          <span class="spec-values">{{ formatSpecificationValues(values) }}</span>
        </div>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="product-actions">
      <el-button
        type="primary"
        :size="buttonSize"
        @click.stop="handleViewDetails"
      >
        查看详情
      </el-button>

      <el-button
        v-if="showEditButton"
        type="default"
        :size="buttonSize"
        @click.stop="handleEdit"
      >
        编辑
      </el-button>

      <el-button
        v-if="showDeleteButton"
        type="danger"
        :size="buttonSize"
        @click.stop="handleDelete"
      >
        删除
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import ImageLoader from './ImageLoader.vue'
import StatusTag from './StatusTag.vue'
import PriceDisplay from './PriceDisplay.vue'
import StockIndicator from './StockIndicator.vue'
import type { Product } from '@/types/product'

// 定义props接口
interface Props {
  product: Product                    // 商品数据
  priceSize?: 'small' | 'medium' | 'large'  // 价格显示大小
  buttonSize?: 'small' | 'default' | 'large'  // 按钮大小
  showStockCount?: boolean              // 是否显示库存数量
  showEditButton?: boolean              // 是否显示编辑按钮
  showDeleteButton?: boolean            // 是否显示删除按钮
  defaultImage?: string                 // 默认图片
  clickable?: boolean                   // 是否可点击
}

// 定义emits
interface Emits {
  (e: 'click', product: Product): void
  (e: 'view-details', product: Product): void
  (e: 'edit', product: Product): void
  (e: 'delete', product: Product): void
}

// 使用withDefaults设置默认值
const props = withDefaults(defineProps<Props>(), {
  priceSize: 'medium',
  buttonSize: 'small',
  showStockCount: true,
  showEditButton: false,
  showDeleteButton: false,
  defaultImage: '/images/placeholder-product.png',
  clickable: true
})

// 定义emits
const emit = defineEmits<Emits>()

// 计算属性：商品图片
const productImage = computed<string>(() => {
  // 优先使用主图片
  if (props.product.images && props.product.images.length > 0) {
    const mainImage = props.product.images.find(img => img.isMain)
    if (mainImage?.imageUrl) {
      return mainImage.imageUrl
    }
    if (props.product.images[0]?.imageUrl) {
      return props.product.images[0].imageUrl
    }
  }

  // 如果没有images数组，尝试直接从image字段获取（向后兼容）
  if ('image' in props.product) {
    return (props.product as any).image || props.defaultImage
  }

  return props.defaultImage
})

// 计算属性：商品规格
const productSpecifications = computed(() => {
  const specs = props.product.productData?.specifications
  if (!specs || typeof specs !== 'object') return {}
  return specs
})

// 计算属性：是否有规格
const hasSpecifications = computed(() => {
  return Object.keys(productSpecifications.value).length > 0
})

// 格式化规格值显示
const formatSpecificationValues = (values: unknown): string => {
  // 类型守卫：确保values是字符串数组
  if (!Array.isArray(values)) return ''
  const stringValues = values.filter((item): item is string => typeof item === 'string')
  if (stringValues.length === 0) return ''
  // 只显示前两个值，如果超过则显示省略号
  return stringValues.slice(0, 2).join(', ') + (stringValues.length > 2 ? '...' : '')
};

// 事件处理函数
const handleClick = (): void => {
  if (props.clickable) {
    emit('click', props.product)
  }
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

// ImageLoader组件会自动处理图片错误

// 暴露方法给父组件
defineExpose({
  product: () => props.product
})
</script>

<style scoped>
.product-list-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.product-list-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.product-list-item:not(.clickable) {
  cursor: default;
}

.product-image {
  position: relative;
  width: 120px;
  height: 120px;
  flex-shrink: 0;
}

.product-image-loader {
  width: 100%;
  height: 100%;
  border-radius: 6px;
}

.product-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.product-info h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.description {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex-grow: 1;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.product-specifications {
  font-size: 12px;
  color: #606266;
  margin-top: 4px;
}

.spec-item {
  display: flex;
  margin-bottom: 2px;
}

.spec-name {
  font-weight: 500;
  margin-right: 4px;
  white-space: nowrap;
}

.spec-values {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-actions {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-shrink: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-list-item {
    flex-direction: column;
    gap: 12px;
  }

  .product-image {
    width: 100%;
    height: 200px;
  }

  .product-info {
    gap: 12px;
  }

  .product-info h3 {
    font-size: 14px;
  }

  .description {
    font-size: 12px;
  }

  .product-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .product-actions {
    justify-content: stretch;
  }

  .product-actions .el-button {
    flex: 1;
  }
}

/* 大尺寸变体 */
.product-list-item.large {
  padding: 20px;
}

.product-list-item.large .product-image {
  width: 150px;
  height: 150px;
}

.product-list-item.large .product-info h3 {
  font-size: 18px;
}

.product-list-item.large .description {
  font-size: 16px;
}

/* 小尺寸变体 */
.product-list-item.small {
  padding: 12px;
  gap: 12px;
}

.product-list-item.small .product-image {
  width: 80px;
  height: 80px;
}

.product-list-item.small .product-info h3 {
  font-size: 14px;
}

.product-list-item.small .description {
  font-size: 12px;
}

.product-list-item.small .product-actions {
  gap: 6px;
}

.product-list-item.small .product-actions .el-button {
  padding: 6px 12px;
  font-size: 12px;
}
</style>