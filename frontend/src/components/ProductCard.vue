<template>
  <div
    v-if="product"
    :class="['product-card', { 'hoverable': hoverable, 'clickable': clickable }]"
    @click="handleCardClick"
  >
    <!-- 商品图片 -->
    <div class="product-image">
      <ImageLoader
        :src="productImage"
        :alt="product.productName"
        :placeholder="defaultImage"
        :fallback="defaultImage"
        class="product-image-loader"
        @click="handleImageClick"
      />

      <!-- 商品状态标签 -->
      <div class="product-badges">
        <StatusTag :status="product.isAvailable ? 'available' : 'unavailable'" />
      </div>

      <!-- 快速操作按钮 -->
      <div
        v-if="effectiveQuickActions.length > 0"
        class="quick-actions"
        @click.stop
      >
        <el-tooltip
          v-for="action in effectiveQuickActions"
          :key="action.event"
          :content="action.tooltip"
          placement="left"
          :disabled="!action.tooltip"
        >
          <el-button
            :type="action.type || 'default'"
            size="small"
            circle
            :disabled="action.disabled"
            @click="handleQuickAction(action)"
          >
            <el-icon>
              <component :is="iconMap[action.icon as keyof typeof iconMap]" />
            </el-icon>
          </el-button>
        </el-tooltip>
      </div>
    </div>

    <!-- 商品信息 -->
    <div class="product-info">
      <!-- 商品名称 -->
      <h3
        :class="['product-name', { 'clickable': clickable }]"
        :title="product.productName"
        @click="handleNameClick"
      >
        {{ product.productName }}
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
import {
  View,
  ShoppingCart,
  Edit,
  Delete,
  Plus,
  Star,
  Setting
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import ImageLoader from './ImageLoader.vue'
import StatusTag from './StatusTag.vue'
import PriceDisplay from './PriceDisplay.vue'
import StockIndicator from './StockIndicator.vue'
import type { Product } from '@/types/product'

// 快速操作按钮配置接口
interface QuickAction {
  icon: string                      // 图标名称
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'default'  // 按钮类型
  event: string                     // 事件名称
  tooltip?: string                  // 提示文本
  disabled?: boolean                // 是否禁用
  condition?: (product: Product) => boolean  // 显示条件函数，接收product参数
}

// 定义props接口
interface Props {
  product: Product                  // 商品数据
  hoverable?: boolean               // 是否悬停效果
  clickable?: boolean               // 是否可点击
  showDescription?: boolean         // 是否显示描述
  showQuickActions?: boolean        // 是否显示快速操作按钮 (向后兼容)
  quickActions?: QuickAction[]      // 自定义快速操作按钮配置
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

// 图标组件映射
const iconMap = {
  View,
  ShoppingCart,
  Edit,
  Delete,
  Plus,
  Star,
  Setting
}

// 使用withDefaults设置默认值
const props = withDefaults(defineProps<Props>(), {
  hoverable: true,
  clickable: true,
  showDescription: true,
  showQuickActions: true,
  quickActions: undefined,
  showActions: false,
  showStockCount: false,
  priceSize: 'medium',
  lowStockThreshold: 10,
  canEdit: false,
  canDelete: false,
  defaultImage: '/images/placeholder-product.png'
})

// 默认快速操作配置
const defaultQuickActions: QuickAction[] = [
  {
    icon: 'View',
    type: 'primary',
    event: 'quick-view',
    tooltip: '快速查看'
  },
  {
    icon: 'ShoppingCart',
    type: 'danger',
    event: 'add-to-cart',
    tooltip: '加入购物车',
    condition: (product: Product) => product.stockQuantity > 0
  }
]

// 计算实际使用的快速操作配置
const effectiveQuickActions = computed<QuickAction[]>(() => {
  // 如果提供了自定义配置，使用自定义配置
  if (props.quickActions) {
    return props.quickActions.filter(action => {
      // 检查显示条件
      if (action.condition && !action.condition(props.product)) {
        return false
      }
      return true
    })
  }

  // 如果启用showQuickActions，使用默认配置
  if (props.showQuickActions) {
    return defaultQuickActions.filter(action => {
      // 检查显示条件
      if (action.condition && !action.condition(props.product)) {
        return false
      }
      return true
    })
  }

  // 否则不显示任何快速操作按钮
  return []
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


// 通用快速操作处理方法
const handleQuickAction = (action: QuickAction): void => {
  // 如果是购物车操作，执行库存检查
  if (action.event === 'add-to-cart') {
    if (props.product.stockQuantity === 0) {
      ElMessage.warning('商品库存不足')
      return
    }
  }

  // 动态触发事件
  const eventData = props.product
  emit(action.event as any, eventData)
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

const handleImageClick = (): void => {
  // 如果卡片可点击，触发卡片点击事件
  if (props.clickable) {
    handleCardClick()
  }
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

.product-image-loader {
  width: 100%;
  height: 100%;
}

.product-card.hoverable:hover .product-image .image-loader-image {
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
  line-clamp: 2;
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
  line-clamp: 2;
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