// ============================================================================
// 购物车状态管理 Store (TypeScript版本) - 管理用户购物车状态、操作和响应逻辑
// ============================================================================

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { cartApi } from '@/api/cart'
import { orderApi } from '@/api/order'
import { executeAsyncOperation, executeGranularAsyncOperation } from '@/utils/asyncOperation'
import { toNumber, toString } from '@/utils/typeConversion'
import { isJwtTokenValid } from '@/utils/jwtUtils'
import { useProductStore } from '@/stores/product'
import { useAuthStore } from '@/stores/auth'
import { CART_CONFIG } from '@/constants/cart'
import type {
  CartItem,
  CartResponse,
  AddToCartRequest,
  UpdateCartRequest,
  CartState
} from '@/types'
import type { AsyncResult } from '@/types/result'

// ============================================================================
// 购物车Store定义：使用Composition API模式 + TypeScript
// ============================================================================

export const useCartStore = defineStore('cart', () => {
  // ============================================================================
  // 🔥 引入其他Store
  // ============================================================================
  const productStore = useProductStore()

  // ============================================================================
  // 🔥 状态定义：强类型响应式数据存储
  // ============================================================================
  const cartData = ref<CartResponse>({
    userId: 0,
    productQuantities: {}
  })
  const loading = ref<boolean>(false)
  const error = ref<string | null>(null)

  // 细粒度加载状态
  const loadingStates = ref({
    fetch: false,
    add: false,
    update: false,
    remove: false,
    clear: false,
    statistics: false
  })

  // ============================================================================
  // 🔥 计算属性：基于状态的派生数据
  // ============================================================================
  const items = computed<CartItem[]>(() => {
    const productQuantities = cartData.value?.productQuantities || {}
    return Object.entries(productQuantities).map(([productId, quantity]) => ({
      productId: Number(productId),
      quantity
    }))
  })

  const totalItems = computed<number>(() => {
    const productQuantities = cartData.value?.productQuantities || {}
    return Object.values(productQuantities).reduce((sum, qty) => sum + qty, 0)
  })

  const isEmpty = computed<boolean>(() => {
    return items.value.length === 0
  })

  const hasItems = computed<boolean>(() => {
    return !isEmpty.value
  })

  // 商品Map - 优化查找性能
  const productMap = computed(() => {
    const map = new Map<number, any>()
    productStore.products.forEach(product => {
      map.set(product.id, product)
    })
    return map
  })

  // 获取商品信息
  const getProductById = (productId: number) => {
    return productMap.value.get(productId)
  }

  // 购物车商品详情（包含商品信息）
  const cartItemsWithDetails = computed(() => {
    return items.value.map(item => {
      const product = getProductById(item.productId)
      return {
        ...item,
        product,
        subtotal: (product?.price || product?.unitPrice || 0) * item.quantity
      }
    })
  })

  // 购物车总金额
  const totalAmount = computed(() => {
    return cartItemsWithDetails.value.reduce((total, item) => {
      return total + item.subtotal
    }, 0)
  })

  // ============================================================================
  // 🔥 方法定义：购物车相关的操作函数
  // ============================================================================

  /**
   * 获取当前购物车内容
   * @description 从后端API获取购物车数据并更新本地状态
   */
  const fetchCart = async (): Promise<void> => {
    const result = await executeGranularAsyncOperation(
      loadingStates.value,
      'fetch',
      () => cartApi.getCart(),
      CART_CONFIG.ERROR_MESSAGES.FETCH_FAILED
    )
    if (result) {
      cartData.value = result
    }
  }

  /**
   * 添加商品到购物车
   * @param request - 添加商品请求参数
   * @description 向后端API添加商品，更新本地状态并返回操作结果
   */
  const addToCart = async (request: AddToCartRequest): Promise<boolean> => {
    const result = await executeGranularAsyncOperation(
      loadingStates.value,
      'add',
      () => cartApi.addToCart(request),
      CART_CONFIG.ERROR_MESSAGES.ADD_FAILED
    )
    if (result) {
      cartData.value = result
      return true
    }
    return false
  }

  /**
   * 批量更新购物车中的商品数量
   * @param request - 更新请求参数
   * @description 更新购物车中的商品数量并同步本地状态
   */
  const updateCart = async (request: UpdateCartRequest): Promise<boolean> => {
    const result = await executeGranularAsyncOperation(
      loadingStates.value,
      'update',
      () => cartApi.updateCart(request),
      CART_CONFIG.ERROR_MESSAGES.UPDATE_FAILED
    )
    if (result) {
      cartData.value = result
      return true
    }
    return false
  }

  /**
   * 从购物车删除指定商品
   * @param productId - 商品ID
   * @description 从后端API删除商品，更新本地状态并返回操作结果
   */
  const removeFromCart = async (productId: number): Promise<boolean> => {
    const result = await executeGranularAsyncOperation(
      loadingStates.value,
      'remove',
      () => cartApi.removeFromCart(productId),
      CART_CONFIG.ERROR_MESSAGES.REMOVE_FAILED
    )
    if (result) {
      cartData.value = result
      return true
    }
    return false
  }

  /**
   * 清空整个购物车
   * @description 清空后端购物车数据并更新本地状态
   */
  const clearCart = async (): Promise<boolean> => {
    const result = await executeGranularAsyncOperation(
      loadingStates.value,
      'clear',
      () => cartApi.clearCart(),
      CART_CONFIG.ERROR_MESSAGES.CLEAR_FAILED
    )
    if (result) {
      cartData.value = result
      return true
    }
    return false
  }

  /**
   * 获取购物车统计信息
   * @description 获取购物车统计并更新本地状态
   */
  const getCartStatistics = async (): Promise<CartResponse | null> => {
    return await executeGranularAsyncOperation(
      loadingStates.value,
      'statistics',
      () => cartApi.getCartStatistics(),
      CART_CONFIG.ERROR_MESSAGES.STATISTICS_FAILED
    )
  }

  /**
   * 检查商品是否已在购物车中
   * @param productId - 商品ID
   * @returns 商品是否已在购物车中
   */
  const isProductInCart = (productId: number): boolean => {
    const productQuantities = cartData.value?.productQuantities || {}
    return productQuantities[toString(productId)] !== undefined
  }

  /**
   * 获取购物车中指定商品的数量
   * @param productId - 商品ID
   * @returns 商品数量，如果不存在则返回0
   */
  const getProductQuantity = (productId: number): number => {
    const productQuantities = cartData.value?.productQuantities || {}
    return productQuantities[toString(productId)] || 0
  }


  /**
   * 更新加载状态
   * @param status - 加载状态值
   * @description 控制全局加载状态，用于UI反馈
   */
  const setLoading = (status: boolean): void => {
    loading.value = status
  }

  /**
   * 更新错误信息
   * @param message - 错误消息
   * @description 设置错误消息，用于错误处理和用户反馈
   */
  const setError = (message: string | null): void => {
    error.value = message
  }

  /**
   * 创建订单
   * @description 将购物车中的商品创建为订单
   */
  const createOrder = async (): Promise<boolean> => {
    // 检查用户是否已登录
    const authStore = useAuthStore()
    if (!authStore.isLoggedIn) {
      error.value = '请先登录后再创建订单'
      ElMessage.error('请先登录后再创建订单')
      return false
    }

    // 额外验证JWT token是否有效（检查是否过期）
    if (!isJwtTokenValid(authStore.token)) {
      error.value = '登录已过期，请重新登录'
      ElMessage.error('登录已过期，请重新登录')
      authStore.logout()
      return false
    }

    // 先检查购物车是否为空
    const cartItems = items.value
    if (cartItems.length === 0) {
      error.value = CART_CONFIG.ERROR_MESSAGES.EMPTY_CART
      return false
    }

    try {
      // 调用订单API创建订单
      const orders = await orderApi.createOrdersFromCart()

      // 明确检查是否为 undefined，并提供更详细的错误信息
      if (orders === undefined || orders === null) {
        error.value = '订单创建失败：API响应无效，请检查登录状态'
        console.error('订单API返回undefined或null:', orders)
        return false
      }

      // 检查订单数组 - 使用更灵活的验证
      if (Array.isArray(orders)) {
        if (orders.length > 0) {
          ElMessage.success(`订单创建成功！共创建 ${orders.length} 个订单`)
          // 购物车会在后端自动清空，我们只需要刷新本地状态
          await fetchCart()
          return true
        } else {
          // 数组存在但为空
          error.value = '订单创建失败：购物车中没有商品或创建了空订单'
          return false
        }
      } else {
        // orders不是数组
        error.value = '订单创建失败：返回的数据格式不正确'
        console.error('订单API返回非数组格式:', typeof orders, orders)
        return false
      }
    } catch (error: any) {
      console.error('创建订单失败:', error)
      // 检查是否是认证相关的错误
      if (error?.response?.status === 401) {
        error.value = '登录已过期，请重新登录'
        authStore.logout() // 登出用户
        // 不在这里重定向，让响应拦截器处理
      } else {
        const errorMessage = error.response?.data?.message || error.message || CART_CONFIG.ERROR_MESSAGES.CREATE_ORDER_FAILED
        error.value = errorMessage
      }
      ElMessage.error(error.value)
      return false
    }
  }

  // ============================================================================
  // 🔥 Store导出：暴露状态、计算属性和方法
  // ============================================================================
  return {
    // 响应式状态
    cartData,
    loading,
    error,
    loadingStates,

    // 计算属性
    items,
    totalItems,
    isEmpty,
    hasItems,
    productMap,
    cartItemsWithDetails,
    totalAmount,
    getProductById,

    // 操作方法
    fetchCart,
    addToCart,
    updateCart,
    removeFromCart,
    clearCart,
    getCartStatistics,
    isProductInCart,
    getProductQuantity,
    setLoading,
    setError,
    createOrder
  }
})