// ============================================================================
// 购物车API服务 - 统一的购物车相关API请求处理
// ============================================================================

import { api } from './request'
import type {
  CartResponse,
  AddToCartRequest,
  UpdateCartRequest,
  CartApiResponse
} from '@/types'

// ============================================================================
// 购物车API接口定义
// ============================================================================

class CartApi {
  /**
   * 获取当前用户的购物车内容
   * @returns 购物车响应数据
   */
  async getCart(): Promise<CartResponse> {
    return api.get<CartResponse>('/cart/get')
  }

  /**
   * 添加商品到购物车
   * @param request - 添加商品请求参数
   * @returns 更新后的购物车数据
   */
  async addToCart(request: AddToCartRequest): Promise<CartResponse> {
    return api.post<CartResponse>('/cart/items', request)
  }

  /**
   * 批量更新购物车中的商品数量
   * @param request - 更新请求参数
   * @returns 更新后的购物车数据
   */
  async updateCart(request: UpdateCartRequest): Promise<CartResponse> {
    return api.put<CartResponse>('/cart/items', request)
  }

  /**
   * 从购物车删除指定商品
   * @param productId - 商品ID
   * @returns 更新后的购物车数据
   */
  async removeFromCart(productId: number): Promise<CartResponse> {
    return api.delete<CartResponse>(`/cart/items/${productId}`)
  }

  /**
   * 清空整个购物车
   * @returns 清空后的购物车数据
   */
  async clearCart(): Promise<CartResponse> {
    return api.delete<CartResponse>('/cart')
  }

  /**
   * 获取购物车统计信息
   * @returns 购物车统计数据
   */
  async getCartStatistics(): Promise<CartResponse> {
    return api.get<CartResponse>('/cart/statistics')
  }
}

// ============================================================================
// 创建购物车API实例并导出
// ============================================================================

export const cartApi = new CartApi()

// 默认导出，支持按需导入
export default cartApi