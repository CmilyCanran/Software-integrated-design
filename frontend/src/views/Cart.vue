<template>
  <!-- ============================================================================
  购物车页面组件 (Vue 3 + TypeScript + Element Plus)
  负责展示用户购物车内容、编辑数量、删除商品、结算等功能
  ============================================================================ -->
  <div class="cart-container">
    <!-- 页面标题 -->
    <div class="cart-header">
      <h1 class="cart-title">🛒 购物车</h1>
      <div class="cart-summary">
        <span class="item-count">共 {{ cartStore.totalItems }} 件商品</span>
      </div>
    </div>

    <!-- 加载状态 -->
    <el-skeleton v-if="cartStore.loading" :rows="4" animated />

    <!-- 错误提示 -->
    <el-alert
      v-if="cartStore.error"
      :title="cartStore.error"
      type="error"
      show-icon
      :closable="false"
      class="error-alert"
    />

    <!-- 购物车内容 -->
    <div v-else-if="!cartStore.isEmpty && !productStore.loading" class="cart-content">
      <!-- 购物车商品列表 -->
      <el-card class="cart-list-card">
        <div
          v-for="item in cartStore.items"
          :key="item.productId"
          class="cart-item"
        >
          <!-- 商品信息 -->
          <div class="item-info">
            <el-avatar
              :size="60"
              :src="getProductImage(item.productId)"
              class="item-image"
              shape="square"
            >
              {{ getProductInitial(item.productId) }}
            </el-avatar>
            <div class="item-details">
              <h3 class="item-name">{{ getProductName(item.productId) }}</h3>
              <p class="item-price">¥{{ typeof getProductPrice(item.productId) === 'number' ? getProductPrice(item.productId).toFixed(2) : '0.00' }}</p>
            </div>
          </div>

          <!-- 商品数量控制 -->
          <div class="item-quantity">
            <el-input-number
              v-model="item.quantity"
              :min="1"
              :max="getProductStock(item.productId)"
              @change="handleQuantityChange(item.productId, $event)"
              size="small"
            />
            <span class="quantity-label">数量</span>
          </div>

          <!-- 商品小计 -->
          <div class="item-subtotal">
            <span class="subtotal-text">¥{{ typeof calculateSubtotal(item) === 'number' ? calculateSubtotal(item).toFixed(2) : '0.00' }}</span>
          </div>

          <!-- 删除按钮 -->
          <div class="item-actions">
            <el-button
              type="danger"
              size="small"
              icon="Delete"
              @click="handleRemoveItem(item.productId)"
              :loading="removingItems.has(item.productId)"
            >
              删除
            </el-button>
          </div>
        </div>
      </el-card>

      <!-- 购物车操作栏 -->
      <el-card class="cart-actions-card">
        <div class="cart-actions">
          <div class="actions-left">
            <el-button @click="handleClearCart" :disabled="cartStore.loading">
              清空购物车
            </el-button>
          </div>
          <div class="actions-right">
            <div class="total-info">
              <span class="total-label">总计:</span>
              <span class="total-amount">¥{{ typeof calculateTotalAmount === 'number' ? calculateTotalAmount.toFixed(2) : '0.00' }}</span>
            </div>
            <el-button
              type="primary"
              size="large"
              @click="handleCheckout"
              :disabled="cartStore.loading || cartStore.isEmpty"
            >
              去结算
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 购物车为空 -->
    <div v-else-if="cartStore.isEmpty && !productStore.loading" class="cart-empty">
      <el-empty
        description="购物车空空如也"
        :image-size="200"
      >
        <el-button type="primary" @click="handleGoShopping">
          去逛逛
        </el-button>
      </el-empty>
    </div>

    <!-- 产品加载状态 -->
    <div v-else class="loading-container">
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
  </div>
</template>

<script setup lang="ts">
// ============================================================================
// 购物车页面组件 (TypeScript版本)
// 负责展示用户购物车内容、编辑数量、删除商品、结算等功能
// ============================================================================

import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCartStore } from '@/stores/cart'
import { useProductStore } from '@/stores/product'
import type { CartItem } from '@/types'

// ============================================================================
// 引入Pinia Store和Router
// ============================================================================
const cartStore = useCartStore()
const productStore = useProductStore()
const router = useRouter()

// ============================================================================
// 响应式数据
// ============================================================================
// 正在删除的商品ID集合
const removingItems = ref<Set<number>>(new Set())

// ============================================================================
// 计算属性
// ============================================================================

// 计算商品小计
const calculateSubtotal = (item: CartItem): number => {
  if (!item || typeof item.quantity !== 'number' || item.quantity <= 0) {
    return 0
  }
  const price = getProductPrice(item.productId)
  return (price || 0) * item.quantity
}

// 计算购物车总金额
const calculateTotalAmount = computed((): number => {
  if (!cartStore.items || !Array.isArray(cartStore.items)) {
    return 0
  }
  // 强制依赖 productStore.products，确保价格变化能触发重新计算
  const products = productStore.products;
  return cartStore.items.reduce((total, item) => {
    const subtotal = calculateSubtotal(item)
    return total + (typeof subtotal === 'number' ? subtotal : 0)
  }, 0)
})

// ============================================================================
// 商品信息获取方法
// ============================================================================

// 获取商品名称
const getProductName = (productId: number): string => {
  if (!productId || typeof productId !== 'number') {
    return `商品${productId || '未知'}`
  }
  const product = productStore.products.find(p => p.id === productId)
  // 兼容Product和ProductListItemDTO类型
  return product?.productName || product?.name || `商品${productId}`
}

// 获取商品价格
const getProductPrice = (productId: number): number => {
  if (!productId || typeof productId !== 'number') {
    return 0
  }
  const product = productStore.products.find(p => p.id === productId)
  // 兼容Product和ProductListItemDTO类型
  return product?.price || product?.unitPrice || 0
}

// 获取商品库存
const getProductStock = (productId: number): number => {
  if (!productId || typeof productId !== 'number') {
    return 999 // 默认库存
  }
  const product = productStore.products.find(p => p.id === productId)
  // 兼容Product和ProductListItemDTO类型
  return product?.stockQuantity || product?.quantity || 999 // 默认库存999
}

// 获取商品图片
const getProductImage = (productId: number): string => {
  if (!productId || typeof productId !== 'number') {
    return ''
  }
  const product = productStore.products.find(p => p.id === productId)
  // 兼容Product和ProductListItemDTO类型
  return product?.productImage || product?.imageUrl || product?.mainImage || ''
}

// 获取商品名称首字母（用于占位符）
const getProductInitial = (productId: number): string => {
  if (!productId || typeof productId !== 'number') {
    return '?'
  }
  const name = getProductName(productId)
  return name.charAt(0).toUpperCase()
}

// ============================================================================
// 事件处理方法
// ============================================================================

// 处理数量变化
const handleQuantityChange = async (productId: number, newQuantity: number) => {
  try {
    // 创建更新请求对象
    const request = {
      productQuantities: {
        ...cartStore.cartData.productQuantities,
        [productId]: newQuantity
      }
    }

    // 更新购物车
    const success = await cartStore.updateCart(request)

    if (success) {
      ElMessage.success('购物车更新成功')
    } else {
      ElMessage.error('更新购物车失败')
    }
  } catch (error) {
    console.error('更新商品数量失败:', error)
    ElMessage.error('更新商品数量失败')
  }
}

// 处理删除商品
const handleRemoveItem = async (productId: number) => {
  try {
    // 添加到正在删除的集合
    removingItems.value.add(productId)

    // 从购物车中删除商品
    const success = await cartStore.removeFromCart(productId)

    if (success) {
      ElMessage.success('商品已从购物车中删除')
    } else {
      ElMessage.error('删除商品失败')
    }
  } catch (error) {
    console.error('删除商品失败:', error)
    ElMessage.error('删除商品失败')
  } finally {
    // 从正在删除的集合中移除
    removingItems.value.delete(productId)
  }
}

// 处理清空购物车
const handleClearCart = async () => {
  try {
    // 确认清空操作
    await ElMessageBox.confirm(
      '确定要清空购物车吗？此操作不可撤销。',
      '清空购物车',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 清空购物车
    const success = await cartStore.clearCart()

    if (success) {
      ElMessage.success('购物车已清空')
    } else {
      ElMessage.error('清空购物车失败')
    }
  } catch (error) {
    // 用户取消操作或出现错误
    if (error !== 'cancel') {
      console.error('清空购物车失败:', error)
      ElMessage.error('清空购物车失败')
    }
  }
}

// 处理结算
const handleCheckout = () => {
  // TODO: 实现结算逻辑
  ElMessage.success('结算功能开发中...')
  console.log('结算购物车:', cartStore.cartData)
}

// 去逛逛
const handleGoShopping = () => {
  router.push('/products')
}

// ============================================================================
// 生命周期钩子
// ============================================================================

// 组件挂载时获取购物车数据
onMounted(async () => {
  // 先获取购物车数据
  await cartStore.fetchCart()

  // 根据购物车中的商品ID获取对应的商品详情
  if (cartStore.items.length > 0) {
    // 获取购物车中的所有商品ID
    const productIds = cartStore.items.map(item => item.productId);

    // 使用优化的API批量获取商品详情
    try {
      await productStore.fetchProductsByIds(productIds);
    } catch (error) {
      console.error('按ID列表获取商品失败:', error);
      // 如果按ID列表获取失败，回退到获取所有商品
      await productStore.fetchProducts({ page: 1, size: 100 });
    }
  }
})
</script>

<style scoped>
/* ============================================================================
购物车页面样式 (CSS Modules)
负责购物车页面的布局和视觉效果
============================================================================ */

.cart-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: calc(100vh - 120px);
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.cart-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.cart-summary {
  display: flex;
  align-items: center;
  gap: 15px;
}

.item-count {
  font-size: 14px;
  color: #909399;
}

.error-alert {
  margin-bottom: 20px;
}

.cart-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.cart-list-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.cart-item:last-child {
  border-bottom: none;
}

.item-info {
  display: flex;
  align-items: center;
  flex: 1;
  min-width: 200px;
}

.item-image {
  margin-right: 15px;
}

.item-details {
  flex: 1;
}

.item-name {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.item-price {
  margin: 0;
  font-size: 14px;
  color: #e74c3c;
  font-weight: 600;
}

.item-quantity {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 150px;
}

.quantity-label {
  margin-top: 5px;
  font-size: 12px;
  color: #909399;
}

.item-subtotal {
  width: 120px;
  text-align: center;
}

.subtotal-text {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.item-actions {
  width: 100px;
  display: flex;
  justify-content: flex-end;
}

.cart-actions-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.cart-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.actions-left {
  display: flex;
  gap: 10px;
}

.actions-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.total-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  margin-right: 20px;
}

.total-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 2px;
}

.total-amount {
  font-size: 24px;
  font-weight: 700;
  color: #e74c3c;
}

.cart-empty {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  padding: 60px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .cart-container {
    padding: 10px;
  }

  .cart-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .cart-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .item-info {
    width: 100%;
  }

  .item-quantity,
  .item-subtotal,
  .item-actions {
    width: 100%;
    text-align: left;
  }

  .item-quantity {
    align-items: flex-start;
  }

  .cart-actions {
    flex-direction: column;
    gap: 15px;
  }

  .actions-right {
    width: 100%;
    flex-direction: column;
    align-items: flex-end;
    gap: 10px;
  }
}
</style>