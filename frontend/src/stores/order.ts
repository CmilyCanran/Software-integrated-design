// ============================================================================
// 订单状态管理 Store (TypeScript版本) - 管理订单相关状态、操作和响应逻辑
// ============================================================================

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { orderApi } from '@/api/order'
import type {
  Order,
  OrderStatus,
  OrderQueryParams,
  OrderPageResult,
  OrderStatistics
} from '@/types/order'

// ============================================================================
// 📋 状态定义
// ============================================================================

export const useOrderStore = defineStore('order', () => {
  // 订单列表状态
  const orders = ref<Order[]>([])
  const loading = ref<boolean>(false)
  const error = ref<string | null>(null)

  // 当前订单详情状态
  const currentOrder = ref<Order | null>(null)
  const currentLoading = ref<boolean>(false)
  const currentError = ref<string | null>(null)

  // 分页和筛选状态
  const currentPage = ref<number>(1)
  const pageSize = ref<number>(10)
  const totalElements = ref<number>(0)
  const totalPages = ref<number>(0)
  const selectedStatus = ref<string>('')

  // 订单统计状态
  const statistics = ref<OrderStatistics | null>(null)
  const statisticsLoading = ref<boolean>(false)

  // ============================================================================
// 🔥 计算属性
// ============================================================================

  // 订单统计计算
  const orderStats = computed(() => {
    if (!statistics.value) return null
    return {
      total: statistics.value.totalOrders,
      pending: statistics.value.pendingOrders,
      paid: 0, // 后端未提供此字段，保持为0
      shipped: 0, // 后端未提供此字段，保持为0
      completed: statistics.value.completedOrders,
      cancelled: 0, // 后端未提供此字段，保持为0
      totalAmount: statistics.value.totalAmount ?? 0
    }
  })

  // 按状态分组的订单数量
  const ordersByStatus = computed(() => {
    const result: Record<string, number> = {
      '': 0,
      PENDING: 0,
      PAID: 0,
      SHIPPED: 0,
      COMPLETED: 0,
      CANCELLED: 0
    }

    const ordersArray = orders.value || []
    if (Array.isArray(ordersArray)) {
      ordersArray.forEach(order => {
        result[order.status] = (result[order.status] || 0) + 1
        result[''] += 1 // 全部计数
      })
    }

    return result
  })

  // 筛选后的订单
  const filteredOrders = computed(() => {
    if (!selectedStatus.value) return orders.value || []
    return (orders.value || []).filter(order => order.status === selectedStatus.value)
  })

  // ============================================================================
// 🔥 方法定义：订单相关的操作函数
// ============================================================================

  /**
   * 获取订单列表
   */
  const fetchOrders = async (params?: OrderQueryParams): Promise<OrderPageResult | null> => {
    loading.value = true
    error.value = null

    try {
      const response = await orderApi.getMyOrders({
        page: params?.page ?? currentPage.value - 1,
        size: params?.size ?? pageSize.value,
        status: params?.status ?? selectedStatus.value
      })

      // 更新订单列表 (添加更全面的防御性检查)
      if (response && typeof response === 'object') {
        orders.value = response.orders || []
        totalElements.value = response.totalElements || 0
        totalPages.value = response.totalPages || 0
        currentPage.value = (response.currentPage || 0) + 1
      } else {
        // 如果响应无效，重置为默认值
        orders.value = []
        totalElements.value = 0
        totalPages.value = 0
        currentPage.value = 1
      }

      return response
    } catch (err: any) {
      error.value = err.message || '获取订单列表失败'
      ElMessage.error(error.value)
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取订单详情
   */
  const fetchOrderDetail = async (orderId: number): Promise<Order | null> => {
    currentLoading.value = true
    currentError.value = null

    try {
      const order = await orderApi.getOrderById(orderId)
      currentOrder.value = order
      return order
    } catch (err: any) {
      currentError.value = err.message || '获取订单详情失败'
      ElMessage.error(currentError.value)
      return null
    } finally {
      currentLoading.value = false
    }
  }

  /**
   * 获取订单统计信息
   */
  const fetchStatistics = async (): Promise<OrderStatistics | null> => {
    statisticsLoading.value = true

    try {
      const stats = await orderApi.getOrderStatistics()
      statistics.value = stats
      return stats
    } catch (err: any) {
      ElMessage.error('获取订单统计失败：' + err.message)
      return null
    } finally {
      statisticsLoading.value = false
    }
  }

  /**
   * 取消订单
   */
  const cancelOrder = async (orderId: number): Promise<boolean> => {
    loading.value = true

    try {
      await orderApi.cancelOrder(orderId)

      // 更新本地订单状态
      const order = orders.value.find(o => o.id === orderId)
      if (order) {
        order.status = 'CANCELLED'
        order.statusDescription = '已取消'
        order.updatedAt = new Date().toISOString()
      }

      // 更新当前订单（如果是同一个）
      if (currentOrder.value && currentOrder.value.id === orderId) {
        currentOrder.value.status = 'CANCELLED'
        currentOrder.value.statusDescription = '已取消'
        currentOrder.value.updatedAt = new Date().toISOString()
      }

      ElMessage.success('订单取消成功')
      return true
    } catch (err: any) {
      ElMessage.error('订单取消失败：' + err.message)
      return false
    } finally {
      loading.value = false
    }
  }

  /**
   * 更新订单状态
   */
  const updateOrderStatus = async (orderId: number, status: OrderStatus): Promise<boolean> => {
    loading.value = true

    try {
      await orderApi.updateOrderStatus(orderId, status)

      // 更新本地订单状态
      const order = orders.value.find(o => o.id === orderId)
      if (order) {
        order.status = status
        order.statusDescription = ORDER_STATUS_DESCRIPTIONS[status]
        order.updatedAt = new Date().toISOString()
      }

      // 更新当前订单（如果是同一个）
      if (currentOrder.value && currentOrder.value.id === orderId) {
        currentOrder.value.status = status
        currentOrder.value.statusDescription = ORDER_STATUS_DESCRIPTIONS[status]
        currentOrder.value.updatedAt = new Date().toISOString()
      }

      ElMessage.success('订单状态更新成功')
      return true
    } catch (err: any) {
      ElMessage.error('订单状态更新失败：' + err.message)
      return false
    } finally {
      loading.value = false
    }
  }

  /**
   * 检查商品购买状态
   */
  const checkPurchaseStatus = async (productId: number): Promise<boolean> => {
    try {
      const result = await orderApi.checkPurchaseStatus(productId)
      return result.hasPurchased
    } catch (err: any) {
      console.error('检查购买状态失败:', err)
      return false
    }
  }

  /**
   * 设置当前页
   */
  const setCurrentPage = (page: number) => {
    currentPage.value = page
  }

  /**
   * 设置每页大小
   */
  const setPageSize = (size: number) => {
    pageSize.value = size
    currentPage.value = 1 // 重置到第一页
  }

  /**
   * 设置筛选状态
   */
  const setSelectedStatus = (status: string) => {
    selectedStatus.value = status
    currentPage.value = 1 // 重置到第一页
  }

  /**
   * 重置状态
   */
  const reset = () => {
    orders.value = []
    currentOrder.value = null
    statistics.value = null
    selectedStatus.value = ''
    currentPage.value = 1
    totalElements.value = 0
    totalPages.value = 0
    error.value = null
    currentError.value = null
  }

  // ============================================================================
// 🔥 Store导出：暴露状态、计算属性和方法
// ============================================================================
  return {
    // 状态
    orders,
    loading,
    error,
    currentOrder,
    currentLoading,
    currentError,
    currentPage,
    pageSize,
    totalElements,
    totalPages,
    selectedStatus,
    statistics,
    statisticsLoading,

    // 计算属性
    orderStats,
    ordersByStatus,
    filteredOrders,

    // 方法
    fetchOrders,
    fetchOrderDetail,
    fetchStatistics,
    cancelOrder,
    updateOrderStatus,
    checkPurchaseStatus,
    setCurrentPage,
    setPageSize,
    setSelectedStatus,
    reset
  }
})