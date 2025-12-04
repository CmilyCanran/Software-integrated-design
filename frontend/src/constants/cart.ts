// ============================================================================
// 购物车相关常量定义
// ============================================================================

/**
 * 购物车默认配置
 */
export const CART_CONFIG = {
  // 默认库存数量（当商品库存信息不可用时）
  DEFAULT_STOCK: 999,

  // 商品数量限制
  MIN_QUANTITY: 1,
  MAX_QUANTITY: 999,

  // 错误消息
  ERROR_MESSAGES: {
    FETCH_FAILED: '获取购物车失败',
    ADD_FAILED: '添加商品到购物车失败',
    UPDATE_FAILED: '更新购物车失败',
    REMOVE_FAILED: '删除商品失败',
    CLEAR_FAILED: '清空购物车失败',
    STATISTICS_FAILED: '获取购物车统计失败',
    EMPTY_CART: '购物车为空，无法创建订单',
    CREATE_ORDER_FAILED: '创建订单失败'
  },

  // 成功消息
  SUCCESS_MESSAGES: {
    ADD_SUCCESS: '商品已添加到购物车',
    UPDATE_SUCCESS: '购物车更新成功',
    REMOVE_SUCCESS: '商品已从购物车中删除',
    CLEAR_SUCCESS: '购物车已清空',
    CREATE_ORDER_SUCCESS: '订单创建成功！'
  },

  // 确认消息
  CONFIRM_MESSAGES: {
    CLEAR_CART: '确定要清空购物车吗？此操作不可撤销。'
  }
} as const

/**
 * 购物车本地存储键名
 */
export const CART_STORAGE_KEYS = {
  CART_DATA: 'cart-data',
  CART_TIMESTAMP: 'cart-timestamp'
} as const