<template>
  <div class="product-detail">
    <!-- 返回按钮 -->
    <div class="back-button">
      <el-button
        type="default"
        icon="ArrowLeft"
        @click="handleBack"
      >
        返回商品列表
      </el-button>
    </div>

    <!-- 加载状态 -->
    <div
      v-if="loading"
      class="loading-container"
    >
      <el-skeleton animated>
        <template #template>
          <div class="detail-skeleton">
            <el-skeleton-item variant="image" style="width: 100%; height: 400px;" />
            <div style="padding: 20px;">
              <el-skeleton-item variant="h1" style="width: 60%; margin-bottom: 16px;" />
              <el-skeleton-item variant="text" style="width: 40%; margin-bottom: 8px;" />
              <el-skeleton-item variant="text" style="width: 80%; margin-bottom: 24px;" />
              <el-skeleton-item variant="button" style="width: 120px; height: 40px;" />
            </div>
          </div>
        </template>
      </el-skeleton>
    </div>

    <!-- 商品详情内容 -->
    <div
      v-else-if="product"
      class="detail-content"
    >
      <div class="detail-main">
        <!-- 左侧图片区域 -->
        <div class="product-images">
          <!-- 主图片展示 -->
          <div class="main-image-container">
            <img
              :src="currentImage || '/placeholder-product.png'"
              :alt="product.productName"
              class="main-image"
              @error="handleImageError"
              @mousemove="handleImageZoom"
              @mouseleave="handleImageZoomLeave"
            />
            <!-- 放大镜效果 -->
            <div
              v-if="showZoom"
              class="zoom-lens"
              :style="zoomLensStyle"
            ></div>
          </div>

          <!-- 缩略图列表 -->
          <div class="thumbnail-list">
            <div
              v-for="(image, index) in productImages"
              :key="index"
              :class="['thumbnail-item', { active: currentImageIndex === index }]"
              @click="selectImage(index)"
            >
              <img
                :src="image"
                :alt="`${product.productName} 图片${index + 1}`"
                @error="handleThumbnailError(index)"
              />
            </div>
          </div>
        </div>

        <!-- 右侧商品信息 -->
        <div class="product-info">
          <!-- 商品标题 -->
          <h1 class="product-title">{{ product.productName }}</h1>

          <!-- 价格和库存 -->
          <div class="price-stock-section">
            <div class="price-info">
              <PriceDisplay
                :price="currentPrice"
                size="large"
              />
              <span
                v-if="product.originalPrice && product.originalPrice > currentPrice"
                class="original-price"
              >
                原价：¥{{ product.originalPrice.toFixed(2) }}
              </span>
            </div>
            <StockIndicator
              :stock-quantity="currentStock"
              :show-count="true"
            />
          </div>

          <!-- 规格选择 -->
          <div class="spec-section">
            <!-- 颜色选择 -->
            <div
              v-if="productColors && productColors.length > 0"
              class="spec-group"
            >
              <h4>颜色</h4>
              <div class="spec-options">
                <div
                  v-for="(color, index) in productColors"
                  :key="index"
                  :class="['spec-option', 'color-option', { active: selectedColor === color.value }]"
                  :style="{ backgroundColor: color.value }"
                  @click="selectColor(color.value)"
                >
                  <span class="color-name">{{ color.name }}</span>
                </div>
              </div>
            </div>

            <!-- 尺寸选择 -->
            <div
              v-if="productSizes && productSizes.length > 0"
              class="spec-group"
            >
              <h4>尺寸</h4>
              <div class="spec-options">
                <div
                  v-for="size in productSizes"
                  :key="size.value"
                  :class="['spec-option', { active: selectedSize === size.value, disabled: !size.available }]"
                  @click="selectSize(size.value, size.available)"
                >
                  {{ size.label }}
                  <span
                    v-if="!size.available"
                    class="out-of-stock"
                  >
                    缺货
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- 数量选择 -->
          <div class="quantity-section">
            <h4>数量</h4>
            <div class="quantity-controls">
              <el-input-number
                v-model="quantity"
                :min="1"
                :max="currentStock"
                size="large"
                :disabled="currentStock === 0"
              />
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="action-section">
            <el-button
              type="primary"
              size="large"
              :disabled="!canAddToCart"
              :loading="addingToCart"
              @click="handleAddToCart"
            >
              <el-icon><ShoppingCart /></el-icon>
              加入购物车
            </el-button>
            <el-button
              type="danger"
              size="large"
              :disabled="!canAddToCart"
              @click="handleBuyNow"
            >
              立即购买
            </el-button>
          </div>

          <!-- 商品描述 -->
          <div class="description-section">
            <h4>商品描述</h4>
            <div class="description-content">
              {{ product.description }}
            </div>
          </div>

          <!-- 商品属性 -->
          <div class="attributes-section">
            <h4>商品属性</h4>
            <div class="attributes-list">
              <div class="attribute-item">
                <span class="attribute-label">品牌：</span>
                <span class="attribute-value">{{ product.brand || '自营' }}</span>
              </div>
              <div class="attribute-item">
                <span class="attribute-label">分类：</span>
                <span class="attribute-value">{{ product.category || '服装' }}</span>
              </div>
              <div class="attribute-item">
                <span class="attribute-label">上架时间：</span>
                <span class="attribute-value">{{ formatDate(product.createdAt) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 商品详情标签页 -->
      <div class="detail-tabs">
        <el-tabs v-model="activeTab">
          <!-- 商品详情 -->
          <el-tab-pane label="商品详情" name="detail">
            <div class="tab-content">
              <div class="detail-description">
                <h3>产品特点</h3>
                <ul>
                  <li>优质面料，舒适透气</li>
                  <li>精湛工艺，品质保证</li>
                  <li>时尚设计，潮流百搭</li>
                  <li>多种规格，满足需求</li>
                </ul>

                <h3>洗护说明</h3>
                <ul>
                  <li>建议手洗，冷水洗涤</li>
                  <li>不可漂白，不可烘干</li>
                  <li>阴凉处晾干</li>
                  <li>低温熨烫</li>
                </ul>
              </div>
            </div>
          </el-tab-pane>

          <!-- 规格参数 -->
          <el-tab-pane label="规格参数" name="specs">
            <div class="tab-content">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="商品名称">{{ product.productName }}</el-descriptions-item>
                <el-descriptions-item label="品牌">{{ product.brand || '自营' }}</el-descriptions-item>
                <el-descriptions-item label="分类">{{ product.category || '服装' }}</el-descriptions-item>
                <el-descriptions-item label="材质">优质面料</el-descriptions-item>
                <el-descriptions-item label="产地">中国</el-descriptions-item>
                <el-descriptions-item label="包装">精美包装</el-descriptions-item>
              </el-descriptions>
            </div>
          </el-tab-pane>

          <!-- 用户评价 -->
          <el-tab-pane label="用户评价" name="reviews">
            <div class="tab-content">
              <div class="reviews-section">
                <div class="reviews-summary">
                  <div class="rating-overview">
                    <div class="rating-score">4.8</div>
                    <div class="rating-stars">
                      <el-rate
                        v-model="averageRating"
                        disabled
                        show-score
                        text-color="#ff9900"
                        score-template="{value}"
                      />
                    </div>
                    <div class="rating-count">共 128 条评价</div>
                  </div>
                </div>

                <div class="reviews-list">
                  <!-- 评价列表 -->
                  <div
                    v-for="review in reviews"
                    :key="review.id"
                    class="review-item"
                  >
                    <div class="review-header">
                      <span class="reviewer">{{ review.user }}</span>
                      <el-rate
                        :model-value="review.rating"
                        disabled
                        size="small"
                      />
                      <span class="review-date">{{ review.date }}</span>
                    </div>
                    <div class="review-content">
                      {{ review.content }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- 错误状态 -->
    <div
      v-else-if="!loading"
      class="error-state"
    >
      <el-result
        icon="warning"
        title="商品不存在"
        sub-title="抱歉，您访问的商品不存在或已下架"
      >
        <template #extra>
          <el-button type="primary" @click="handleBack">
            返回商品列表
          </el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, ShoppingCart } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import PriceDisplay from '@/components/PriceDisplay.vue'
import StockIndicator from '@/components/StockIndicator.vue'
import type { Product } from '@/types/product'

// 路由相关
const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref<boolean>(true)
const product = ref<Product | null>(null)
const quantity = ref<number>(1)
const selectedColor = ref<string>('')
const selectedSize = ref<string>('')
const activeTab = ref<string>('detail')
const addingToCart = ref<boolean>(false)
const showZoom = ref<boolean>(false)
const zoomPosition = ref({ x: 0, y: 0 })

// 图片相关
const currentImageIndex = ref<number>(0)
const productImages = ref<string[]>([])

// 商品规格数据
const productColors = ref<Array<{ value: string; name: string }>>([])
const productSizes = ref<Array<{ value: string; label: string; available: boolean }>>([])

// 评价数据
const reviews = ref([
  {
    id: 1,
    user: '张三',
    rating: 5,
    content: '质量很好，穿着舒适，推荐购买！',
    date: '2024-01-15'
  },
  {
    id: 2,
    user: '李四',
    rating: 4,
    content: '整体不错，就是稍微有点小',
    date: '2024-01-14'
  }
])

// 计算属性
const productId = computed<string>(() => route.params.id as string)

const currentImage = computed<string>(() => {
  return productImages.value[currentImageIndex.value] || ''
})

const currentPrice = computed<number>(() => {
  return product.value?.price || 0
})

const currentStock = computed<number>(() => {
  return product.value?.stockQuantity || 0
})

const averageRating = computed<number>(() => 4.8)

const canAddToCart = computed<boolean>(() => {
  return !!(product.value && currentStock.value > 0 && !addingToCart.value)
})

const zoomLensStyle = computed(() => ({
  left: `${zoomPosition.value.x}px`,
  top: `${zoomPosition.value.y}px`
}))

// 模拟商品数据
const mockProduct: Product = {
  id: 1,
  name: '时尚运动鞋 - 2024新款',
  description: '这款运动鞋采用最新科技材料制作，轻便透气，适合跑步和日常穿着。鞋底采用耐磨橡胶，提供良好的抓地力。鞋面采用网眼设计，确保足部透气舒适。',
  price: 299.99,
  originalPrice: 399.99,
  stockQuantity: 15,
  isAvailable: true,
  createdAt: '2024-01-15T10:00:00Z',
  updatedAt: '2024-01-15T10:00:00Z',
  image: '/shoe1.jpg',
  userId: 1,
  brand: '运动品牌',
  category: '运动鞋'
}

// 初始化商品图片
const initProductImages = () => {
  if (product.value) {
    productImages.value = [
      product.value.image || '/placeholder-product.png',
      '/shoe1-2.jpg',
      '/shoe1-3.jpg',
      '/shoe1-4.jpg'
    ]
  }
}

// 初始化商品规格
const initProductSpecs = () => {
  // 颜色规格
  productColors.value = [
    { value: '#000000', name: '黑色' },
    { value: '#ffffff', name: '白色' },
    { value: '#ff0000', name: '红色' },
    { value: '#0000ff', name: '蓝色' }
  ]

  // 尺寸规格
  productSizes.value = [
    { value: 'S', label: 'S', available: true },
    { value: 'M', label: 'M', available: true },
    { value: 'L', label: 'L', available: false },
    { value: 'XL', label: 'XL', available: true }
  ]

  // 默认选择
  if (productColors.value.length > 0) {
    selectedColor.value = productColors.value[0].value
  }
  if (productSizes.value.length > 0) {
    const availableSize = productSizes.value.find(size => size.available)
    if (availableSize) {
      selectedSize.value = availableSize.value
    }
  }
}

// 图片处理函数
const selectImage = (index: number) => {
  currentImageIndex.value = index
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.src = '/placeholder-product.png'
}

const handleThumbnailError = (index: number) => {
  productImages.value[index] = '/placeholder-product.png'
}

// 图片放大镜功能
const handleImageZoom = (event: MouseEvent) => {
  const rect = event.currentTarget.getBoundingClientRect()
  const x = event.clientX - rect.left
  const y = event.clientY - rect.top

  zoomPosition.value = { x, y }
  showZoom.value = true
}

const handleImageZoomLeave = () => {
  showZoom.value = false
}

// 规格选择函数
const selectColor = (color: string) => {
  selectedColor.value = color
}

const selectSize = (size: string, available: boolean) => {
  if (available) {
    selectedSize.value = size
  } else {
    ElMessage.warning('该尺寸暂时缺货')
  }
}

// 业务操作函数
const handleAddToCart = async () => {
  if (!canAddToCart.value) return

  addingToCart.value = true

  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    ElMessage.success(`已将 ${quantity.value} 件商品加入购物车`)
    quantity.value = 1
  } catch (error) {
    ElMessage.error('加入购物车失败')
  } finally {
    addingToCart.value = false
  }
}

const handleBuyNow = async () => {
  if (!canAddToCart.value) return

  // 先加入购物车，然后跳转到购物车页面
  await handleAddToCart()
  router.push('/cart')
}

const handleBack = () => {
  router.back()
}

// 工具函数
const formatDate = (dateString: string) => {
  return dayjs(dateString).format('YYYY-MM-DD')
}

// 加载商品数据
const loadProduct = async () => {
  loading.value = true

  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 使用模拟数据
    product.value = mockProduct

    // 初始化图片和规格
    initProductImages()
    initProductSpecs()
  } catch (error) {
    ElMessage.error('加载商品信息失败')
    console.error('Load product error:', error)
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(() => {
  loadProduct()
})

// 监听路由变化
watch(() => route.params.id, () => {
  if (route.params.id) {
    loadProduct()
  }
})
</script>

<style scoped>
.product-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.back-button {
  margin-bottom: 20px;
}

.loading-container,
.error-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

.detail-skeleton {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
}

.detail-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.detail-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  padding: 30px;
}

/* 图片区域 */
.product-images {
  position: sticky;
  top: 20px;
  height: fit-content;
}

.main-image-container {
  position: relative;
  width: 100%;
  height: 400px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 16px;
  cursor: zoom-in;
}

.main-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.zoom-lens {
  position: absolute;
  width: 100px;
  height: 100px;
  border: 2px solid #409eff;
  border-radius: 50%;
  pointer-events: none;
  opacity: 0.3;
  transform: translate(-50%, -50%);
}

.thumbnail-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
}

.thumbnail-item {
  width: 100%;
  height: 60px;
  border: 2px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.thumbnail-item:hover {
  border-color: #409eff;
}

.thumbnail-item.active {
  border-color: #409eff;
}

.thumbnail-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 商品信息区域 */
.product-info {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.product-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
}

.price-stock-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.price-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}

/* 规格选择 */
.spec-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.spec-group h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}

.spec-options {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.spec-option {
  padding: 8px 16px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  position: relative;
}

.spec-option:hover {
  border-color: #409eff;
}

.spec-option.active {
  border-color: #409eff;
  background-color: #ecf5ff;
  color: #409eff;
}

.spec-option.disabled {
  border-color: #f5f5f5;
  color: #c0c4cc;
  cursor: not-allowed;
}

.color-option {
  min-width: 60px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.color-name {
  color: #fff;
  font-size: 12px;
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

.out-of-stock {
  font-size: 10px;
  color: #909399;
  font-weight: 500;
  margin-left: 4px;
}

/* 数量选择 */
.quantity-section h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}

.quantity-controls {
  width: 120px;
}

/* 操作按钮 */
.action-section {
  display: flex;
  gap: 16px;
}

.action-section .el-button {
  flex: 1;
  height: 48px;
  font-size: 16px;
}

/* 描述和属性 */
.description-section,
.attributes-section {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.description-section h4,
.attributes-section h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}

.description-content {
  color: #606266;
  line-height: 1.6;
}

.attributes-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.attribute-item {
  display: flex;
  gap: 8px;
}

.attribute-label {
  min-width: 80px;
  color: #909399;
  font-size: 14px;
}

.attribute-value {
  color: #606266;
  font-size: 14px;
}

/* 标签页 */
.detail-tabs {
  padding: 0 30px 30px;
}

.tab-content {
  padding: 20px 0;
}

.detail-description h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.detail-description ul {
  margin: 0 0 24px 0;
  padding-left: 20px;
}

.detail-description li {
  margin-bottom: 8px;
  color: #606266;
  line-height: 1.6;
}

/* 评价区域 */
.reviews-summary {
  margin-bottom: 24px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.rating-overview {
  display: flex;
  align-items: center;
  gap: 16px;
}

.rating-score {
  font-size: 32px;
  font-weight: 600;
  color: #ff9900;
}

.rating-stars {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.rating-count {
  font-size: 12px;
  color: #909399;
}

.review-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.reviewer {
  font-weight: 500;
  color: #303133;
}

.review-date {
  font-size: 12px;
  color: #909399;
  margin-left: auto;
}

.review-content {
  color: #606266;
  line-height: 1.6;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-detail {
    padding: 10px;
  }

  .detail-main {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 20px;
  }

  .product-images {
    position: static;
  }

  .main-image-container {
    height: 300px;
  }

  .product-title {
    font-size: 20px;
  }

  .price-stock-section {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .action-section {
    flex-direction: column;
  }

  .detail-tabs {
    padding: 0 20px 20px;
  }

  .thumbnail-list {
    grid-template-columns: repeat(4, 1fr);
    gap: 6px;
  }

  .thumbnail-item {
    height: 50px;
  }
}
</style>