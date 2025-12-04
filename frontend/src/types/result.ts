// ============================================================================
// 统一结果类型定义
// ============================================================================

/**
 * 异步操作结果
 */
export interface AsyncResult<T> {
  success: boolean
  data?: T
  error?: string
}

/**
 * 购物车操作结果
 */
export type CartOperationResult = AsyncResult<CartResponse>

/**
 * 商品操作结果
 */
export type ProductOperationResult = AsyncResult<any>

// 导入相关类型
import type { CartResponse } from './cart'