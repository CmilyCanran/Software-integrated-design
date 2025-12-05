<template>
  <div class="product-display" :class="{ 'product-display--readonly': readonly }">
    <!-- 商品图片 -->
    <div class="product-image-wrapper" @click="handleImageClick">
      <el-image
        :src="displayImage || '/images/placeholder-product.png'"
        :alt="displayName"
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
    <div class="product-info">
      <h4 class="product-name" @click="handleNameClick">
        {{ displayName }}
      </h4>

      <!-- 商品规格 -->
      <div v-if="specifications && Object.keys(specifications).length > 0" class="product-specs">
        <el-tag
          v-for="(value, key) in specifications"
          :key="key"
          size="small"
          type="info"
          class="spec-tag"
        >
          {{ key }}: {{ value }}
        </el-tag>
      </div>

      <!-- 价格信息 -->
      <div class="product-price">
        <span class="unit-price">单价: ¥{{ unitPrice.toFixed(2) }}</span>
      </div>
    </div>

    <!-- 数量和价格 -->
    <div v-if="showQuantity" class="product-quantity-price">
      <div class="quantity">
        <span class="quantity-label">数量:</span>
        <span class="quantity-value">{{ quantity }}</span>
      </div>

      <div class="subtotal">
        <span class="subtotal-label">小计:</span>
        <span class="subtotal-value">¥{{ subtotal.toFixed(2) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Picture } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { processImageUrl } from '@/utils/imageUtils'

// 定义后端可能返回的字段类型
interface ProductData {
  // 订单相关字段
  productImage?: string
  productName?: string
  // 商品相关字段
  mainImageUrl?: string
  name?: string
  // 价格和数量信息
  unitPrice?: number
  quantity?: number
  subtotal?: number
  // 规格信息
  specifications?: Record<string, string>
  // 通用字段
  productId?: number
}

// Props定义
interface Props {
  product: ProductData
  showQuantity?: boolean
  readonly?: boolean
}

// Props默认值
const props = withDefaults(defineProps<Props>(), {
  showQuantity: false,
  readonly: true
})

// 方法
const router = useRouter()

// 计算显示的图片，适配后端不同字段名
const displayImage = computed<string>(() => {
  // 优先级: productImage (订单) > mainImageUrl (商品) > main_image (直接从productData) > name相关字段或其他可能的图片字段
  const imageUrl = props.product.productImage ||
                  props.product.mainImageUrl ||
                  props.product.main_image ||  // 直接从productData中的字段
                  null

  if (imageUrl) {
    return processImageUrl(imageUrl)
  }
  return undefined
})

// 计算显示的名称，适配后端不同字段名
const displayName = computed<string>(() => {
  // 优先级: productName (订单) > name (商品) > productId作为备选
  return props.product.productName ||
         props.product.name ||
         `商品${props.product.productId || '未知'}`
})

// 计算价格信息
const unitPrice = computed<number>(() => props.product.unitPrice || 0)
const quantity = computed<number>(() => props.product.quantity || 1)
const subtotal = computed<number>(() => props.product.subtotal || unitPrice.value * quantity.value)
const specifications = computed<Record<string, string>>(() => props.product.specifications || {})

// 事件处理
const handleImageClick = () => {
  if (!props.readonly) {
    emit('click', props.product.productId)
  } else {
    // 只读模式下跳转到商品详情
    if (props.product.productId) {
      router.push(`/products/${props.product.productId}`)
    }
  }
}

const handleNameClick = () => {
  handleImageClick()
}

const handleImageError = () => {
  console.warn('商品图片加载失败:', displayImage.value)
}

// 定义组件事件
const emit = defineEmits<{
  click: [productId: number | undefined]
}>()
</script>

<style scoped lang="scss">
.product-display {
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

.product-image-wrapper {
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

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
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

.product-specs {
  margin-bottom: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.spec-tag {
  margin: 0;
}

.product-price {
  font-size: 14px;
  color: #606266;
}

.unit-price {
  color: #909399;
}

.product-quantity-price {
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

// 响应式设计
@media (max-width: 768px) {
  .product-display {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .product-image-wrapper {
    width: 60px;
    height: 60px;
  }

  .product-quantity-price {
    text-align: left;
    width: 100%;
  }
}
</style>