// ============================================================================
// 商品相关TypeScript类型定义
// ============================================================================

// 商品规格接口
export interface ProductSpecifications {
  [key: string]: string[]
}

// 商品数据接口
export interface ProductData {
  specifications?: ProductSpecifications
  [key: string]: any
}

// 基础商品接口
export interface Product {
  id: number
  productName: string
  description?: string
  price: number
  salesCount: number
  discount: number
  stockQuantity: number
  isAvailable: boolean
  creatorId: number
  productData?: ProductData
  createdAt: string
  updatedAt: string
  // 扩展字段（根据后端数据结构）
  originalPrice?: number
  // 后端DTO字段 - 单张主图
  mainImageUrl?: string
  specifications?: { [key: string]: any }
  category?: string
  brand?: string
  color?: string
  size?: string
  extendedAttributes?: { [key: string]: any }
  tags?: string[]
}


// 商品创建请求接口
export interface ProductCreateRequest {
  productName: string
  description?: string
  price: number
  discount?: number
  stockQuantity: number
  isAvailable: boolean
  image?: File
  productData?: ProductData
  // 后端DTO字段 - 单张主图
  mainImageUrl?: string
  specifications?: { [key: string]: any }
  category?: string
  brand?: string
  color?: string
  size?: string
  extendedAttributes?: { [key: string]: any }
  tags?: string[]
  originalPrice?: number
}

// 商品更新请求接口
export interface ProductUpdateRequest extends Partial<ProductCreateRequest> {
  // 可以更新所有商品字段
  productName?: string
  description?: string
  price?: number
  discount?: number
  stockQuantity?: number
  isAvailable?: boolean
}

// 商品查询参数接口
export interface ProductQueryParams {
  // 分页参数
  page?: number
  size?: number
  sort?: string
  order?: 'asc' | 'desc'

  // 筛选参数
  minPrice?: number
  maxPrice?: number
  minStock?: number
  maxStock?: number
  isAvailable?: boolean
  creatorId?: number
  hasStock?: boolean

  // 搜索参数
  keyword?: string

  // 排序参数
  sortBy?: 'price' | 'salesCount' | 'createdAt' | 'updatedAt' | 'stockQuantity'
  sortOrder?: 'asc' | 'desc'
}

// 分页响应接口
export interface PaginatedResponse<T> {
  data: T[]
  total: number
  page: number
  size: number
  totalPages: number
  hasNext: boolean
  hasPrev: boolean
}

// 商品统计信息接口
export interface ProductStats {
  totalProducts: number
  availableProducts: number
  unavailableProducts: number
  totalSales: number
  totalRevenue: number
  averagePrice: number
  lowStockCount: number
  outOfStockCount: number
  salesTrend: Array<{
    date: string
    sales: number
    revenue: number
  }>
}

// 商品状态枚举
export enum ProductStatus {
  AVAILABLE = 'available',        // 在售
  UNAVAILABLE = 'unavailable',  // 下架
  OUT_OF_STOCK = 'out_of_stock' // 缺货
}

// 商品状态标签类型
export type ProductStatusType = keyof typeof ProductStatus

// 商品操作类型
export type ProductAction = 'create' | 'update' | 'delete' | 'publish' | 'unpublish'

// 商品表单验证规则接口
export interface ProductFormRules {
  productName: {
    required: boolean
    message: string
    trigger: 'blur' | 'change' | 'submit'
    min?: number
    max?: number
  }
  price: {
    required: boolean
    message: string
    trigger: 'blur' | 'change' | 'submit'
    type: 'number'
    min?: number
    validator?: (rule: any, value: any, callback: any) => void
  }
  stockQuantity: {
    required: boolean
    message: string
    trigger: 'blur' | 'change' | 'submit'
    type: 'number'
    min?: number
    validator?: (rule: any, value: any, callback: any) => void
  }
  discount: {
    required: boolean
    message: string
    trigger: 'blur' | 'change' | 'submit'
    type: 'number'
    min?: number
    max?: number
    validator?: (rule: any, value: any, callback: any) => void
  }
}

// 商品排序选项
export interface ProductSortOption {
  key: string
  label: string
  value: string
}