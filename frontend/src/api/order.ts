// ============================================================================
// 订单API服务 (TypeScript版本) - 提供订单相关的HTTP请求接口
// ============================================================================

import request from './request'
import type { ApiResponse, PageResult } from '@/types'

// ============================================================================
// 📋 类型定义
// ============================================================================

/**
 * 订单状态枚举
 */
export enum OrderStatus {
  PENDING = 'PENDING',      // 待处理
  PAID = 'PAID',            // 已支付
  SHIPPED = 'SHIPPED',      // 已发货
  COMPLETED = 'COMPLETED',  // 已完成
  CANCELLED = 'CANCELLED'   // 已取消
}

/**
 * 订单状态描述映射
 */
export const ORDER_STATUS_DESCRIPTIONS: Record<OrderStatus, string> = {
  [OrderStatus.PENDING]: '待处理',
  [OrderStatus.PAID]: '已支付',
  [OrderStatus.SHIPPED]: '已发货',
  [OrderStatus.COMPLETED]: '已完成',
  [OrderStatus.CANCELLED]: '已取消'
}

/**
 * 订单接口定义
 */
export interface Order {
  id: number
  userId: number
  username: string
  productId: number
  productName: string
  productDescription?: string
  productImage?: string
  sellerId: number
  sellerName: string
  quantity: number
  unitPrice: number
  totalAmount: number
  status: OrderStatus
  statusDescription: string
  createdAt: string
  updatedAt: string
  remarks?: string
  shippingInfo?: string
  paymentInfo?: string
  shippingAddress?: string
  contactPhone?: string
}

/**
 * 创建订单请求
 */
export interface CreateOrderRequest {
  productId: number
  quantity: number
  remarks?: string
  shippingAddress?: string
  contactPhone?: string
}

/**
 * 订单查询参数
 */
export interface OrderQueryParams {
  page?: number
  size?: number
  status?: OrderStatus
}

/**
 * 订单统计信息
 */
export interface OrderStatistics {
  totalOrders: number
  pendingOrders: number
  completedOrders: number
  totalAmount: number
}

/**
 * 购买状态检查结果
 */
export interface PurchaseStatusResult {
  hasPurchased: boolean
  productId: number
}

// ============================================================================
// 🛒 订单API服务类
// ============================================================================

/**
 * 订单API服务
 * 提供所有订单相关的HTTP请求方法
 */
class OrderApi {
  // ==================== 🛒 订单创建 ====================

  /**
   * 创建单个商品订单
   * @param request 创建订单请求参数
   * @returns Promise<Order>
   */
  async createOrder(request: CreateOrderRequest): Promise<Order> {
    const response = await request.post<ApiResponse<Order>>('/orders/create', null, {
      params: request
    })
    return response.data
  }

  /**
   * 从购物车创建订单
   * @returns Promise<Order[]>
   */
  async createOrdersFromCart(): Promise<Order[]> {
    try {
      console.log('开始调用创建订单API')
      const response = await request.post<ApiResponse<Order[]>>('/orders/create-from-cart')
      console.log('API响应:', response)

      // 检查是否是期望的ApiResponse格式
      if (response && typeof response === 'object') {
        // 如果response.data是期望的订单数组，直接返回
        if (response.data && Array.isArray(response.data)) {
          console.log('成功获取订单数组，长度:', response.data.length)
          return response.data
        }
        // 如果response本身包含data属性，检查data属性
        else if (response.data && response.data.data && Array.isArray(response.data.data)) {
          console.log('从嵌套data属性获取订单数组，长度:', response.data.data.length)
          return response.data.data
        }
        // 如果response直接包含订单数组（可能后端返回格式不同）
        else if (Array.isArray(response)) {
          console.log('API直接返回数组格式，长度:', response.length)
          return response
        }
      }

      console.error('订单API返回的数据格式不正确:', response)
      throw new Error('订单创建失败：返回的数据格式不正确')
    } catch (error) {
      console.error('创建订单API调用失败:', error)
      // 重新抛出错误，让调用方处理
      throw error
    }
  }

  // ==================== 🔍 订单查询 ====================

  /**
   * 获取订单详情
   * @param orderId 订单ID
   * @returns Promise<Order>
   */
  async getOrderById(orderId: number): Promise<Order> {
    const order = await request.get<Order>(`/orders/${orderId}`)
    return order
  }

  /**
   * 获取我的订单列表
   * @param params 查询参数
   * @returns Promise<OrderPageResult>
   */
  async getMyOrders(params: OrderQueryParams = {}): Promise<OrderPageResult> {
    // 由于响应拦截器已处理响应格式，直接获取分页对象
    const pageResult = await request.get<OrderPageResult>('/orders/my-orders', {
      params: {
        page: params.page || 0,
        size: params.size || 10,
        status: params.status
      }
    })

    // 添加防御性检查，确保返回的对象结构正确
    if (!pageResult || typeof pageResult !== 'object') {
      // 如果响应为空，返回默认的空分页对象
      return {
        orders: [],
        currentPage: 0,
        totalElements: 0,
        totalPages: 0,
        hasNext: false,
        hasPrevious: false
      }
    }

    return pageResult
  }

  /**
   * 获取商家订单列表
   * @param params 查询参数
   * @returns Promise<OrderPageResult>
   */
  async getSellerOrders(params: OrderQueryParams = {}): Promise<OrderPageResult> {
    // 由于响应拦截器已处理响应格式，直接获取分页对象
    const pageResult = await request.get<OrderPageResult>('/orders/seller-orders', {
      params: {
        page: params.page || 0,
        size: params.size || 10,
        status: params.status
      }
    })

    // 添加防御性检查，确保返回的对象结构正确
    if (!pageResult || typeof pageResult !== 'object') {
      // 如果响应为空，返回默认的空分页对象
      return {
        orders: [],
        currentPage: 0,
        totalElements: 0,
        totalPages: 0,
        hasNext: false,
        hasPrevious: false
      }
    }

    return pageResult
  }

  // ==================== 🔄 订单状态管理 ====================

  /**
   * 更新订单状态
   * @param orderId 订单ID
   * @param newStatus 新状态
   * @returns Promise<Order>
   */
  async updateOrderStatus(orderId: number, newStatus: OrderStatus): Promise<Order> {
    const response = await request.put<ApiResponse<Order>>(`/orders/${orderId}/status`, null, {
      params: { newStatus }
    })
    return response
  }

  /**
   * 取消订单
   * @param orderId 订单ID
   * @returns Promise<Order>
   */
  async cancelOrder(orderId: number): Promise<Order> {
    const response = await request.put<ApiResponse<Order>>(`/orders/${orderId}/cancel`)
    return response
  }

  // ==================== 📊 订单统计 ====================

  /**
   * 获取订单统计信息
   * @returns Promise<OrderStatistics>
   */
  async getOrderStatistics(): Promise<OrderStatistics> {
    const statistics = await request.get<OrderStatistics>('/orders/statistics')
    return statistics
  }

  // ==================== 🔍 辅助查询 ====================

  /**
   * 检查商品购买状态
   * @param productId 商品ID
   * @returns Promise<PurchaseStatusResult>
   */
  async checkPurchaseStatus(productId: number): Promise<PurchaseStatusResult> {
    const result = await request.get<PurchaseStatusResult>(`/orders/check-purchase/${productId}`)
    return result
  }

  // ==================== 🛠️ 批量操作 ====================

  /**
   * 批量创建订单（从购物车商品列表）
   * @param items 购物车商品列表
   * @returns Promise<Order[]>
   */
  async createBatchOrders(items: Array<{ productId: number; quantity: number }>): Promise<Order[]> {
    const orders: Order[] = []

    // 逐个创建订单（因为采用"一个商品一个订单"的设计）
    for (const item of items) {
      try {
        const order = await this.createOrder({
          productId: item.productId,
          quantity: item.quantity
        })
        orders.push(order)
      } catch (error) {
        console.error(`创建订单失败: productId=${item.productId}, quantity=${item.quantity}`, error)
        throw error
      }
    }

    return orders
  }
}

// ============================================================================
// 📤 导出单例实例
// ============================================================================

export const orderApi = new OrderApi()

// ============================================================================
// 📤 默认导出
// ============================================================================

export default orderApi