// ============================================================================
// 订单相关类型定义 (TypeScript版本)
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
 * 订单状态颜色映射
 */
export const ORDER_STATUS_COLORS: Record<OrderStatus, string> = {
  [OrderStatus.PENDING]: '#E6A23C',    // 橙色 - 待处理
  [OrderStatus.PAID]: '#409EFF',       // 蓝色 - 已支付
  [OrderStatus.SHIPPED]: '#67C23A',    // 绿色 - 已发货
  [OrderStatus.COMPLETED]: '#67C23A',  // 绿色 - 已完成
  [OrderStatus.CANCELLED]: '#F56C6C'   // 红色 - 已取消
}

/**
 * 订单状态图标映射
 */
export const ORDER_STATUS_ICONS: Record<OrderStatus, string> = {
  [OrderStatus.PENDING]: 'Clock',      // 时钟 - 待处理
  [OrderStatus.PAID]: 'Money',         // 钱币 - 已支付
  [OrderStatus.SHIPPED]: 'Document',   // 文档 - 已发货
  [OrderStatus.COMPLETED]: 'CircleCheck', // 勾选 - 已完成
  [OrderStatus.CANCELLED]: 'CircleClose'  // 关闭 - 已取消
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
 * 订单项接口（用于订单详情中的商品列表）
 */
export interface OrderItem {
  productId: number
  productName: string
  productImage?: string
  quantity: number
  unitPrice: number
  subtotal: number
  specifications?: Record<string, any>
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
  startDate?: string
  endDate?: string
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

/**
 * 订单分页结果
 */
export interface OrderPageResult {
  orders: Order[]  // 后端实际返回字段名
  currentPage: number  // 后端实际返回字段名
  totalElements: number
  totalPages: number
  hasNext: boolean  // 后端实际返回字段名
  hasPrevious: boolean  // 后端实际返回字段名
}

/**
 * 订单状态历史记录
 */
export interface OrderStatusHistory {
  status: OrderStatus
  timestamp: string
  description: string
}

/**
 * 订单基本信息（用于订单卡片等简洁展示）
 */
export interface OrderBasicInfo {
  id: number
  orderNumber: string
  createdAt: string
  totalAmount: number
  status: OrderStatus
  itemCount: number
  firstItemImage?: string
}

/**
 * 订单筛选选项
 */
export interface OrderFilterOption {
  value: string
  label: string
  count?: number
}

/**
 * 订单操作权限
 */
export interface OrderPermissions {
  canCancel: boolean
  canUpdateStatus: boolean
  canDelete: boolean
  canViewDetails: boolean
}