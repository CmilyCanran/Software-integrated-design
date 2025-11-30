// ============================================================================
// 商品相关TypeScript类型定义 - 与后端DTO完全对齐
// ============================================================================

// 导入共享类型定义
import type {
  PaginatedResponse,
  FormRule,
  FieldValidation,
  SortConfig,
  Tag,
  KeyValuePair
} from './index'

// 🔧 强化的规格类型定义 - 与后端Map<String, List<String>>对应
export interface ProductSpecifications {
  [key: string]: string[]  // 明确值为字符串数组
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
  specifications?: Record<string, string[]>  // 统一的动态规格字段，键为规格名，值为规格值数组
  category?: string
  brand?: string
  color?: string
  size?: string
  tags?: string[]
}

// 🔧 完全对齐后端DTO的商品创建请求接口
export interface ProductCreateRequest {
  // 基本字段 - 与ProductCreateRequestDTO完全匹配
  productName: string                    // @NotBlank @Size(max=50)
  description?: string                    // 可选字段
  price: number                           // @NotNull @DecimalMin("0.0")
  discount?: number                       // @DecimalMin("0.0") @DecimalMax("100.0")
  stockQuantity: number                   // @NotNull @Min(0)
  isAvailable: boolean                    // @NotNull

  // 扩展字段 - 与后端DTO完全对应
  productData?: Record<string, any>       // Map<String, Object>
  mainImageUrl?: string                   // 单张主图
  specifications?: ProductSpecifications  // Map<String, Object>
  category?: string                       // 商品类别
  brand?: string                          // 商品品牌
  color?: string                          // 商品颜色
  size?: string                           // 商品尺寸
  extendedAttributes?: Record<string, any> // Map<String, Object>
}

// 🔧 完全对齐后端DTO的商品更新请求接口
export interface ProductUpdateRequest {
  // 所有字段可选，与ProductUpdateRequestDTO匹配
  productName?: string                    // @Size(max=50)
  description?: string
  price?: number                          // @DecimalMin("0.0")
  discount?: number                       // @DecimalMin("0.0") @DecimalMax("100.0")
  stockQuantity?: number                  // @Min(0)
  isAvailable?: boolean
  productData?: Record<string, any>
  mainImageUrl?: string
  specifications?: ProductSpecifications
  category?: string
  brand?: string
  color?: string
  size?: string
  extendedAttributes?: Record<string, any>
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

// 注意：PaginatedResponse<T> 已从 './index' 导入

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

// 🔧 与后端验证注解完全对应的接口
// 注意：ValidationRule 已从 './index' 导入为 FormRule

export interface ProductValidationRules {
  productName: FormRule & { max: 50, required: true }
  price: FormRule & { min: 0.01, required: true }
  discount: FormRule & { min: 0, max: 100 }
  stockQuantity: FormRule & { min: 0, required: true }
}

// 🔧 导出验证规则常量 - 完全映射后端注解
export const PRODUCT_VALIDATION_RULES: ProductValidationRules = {
  productName: {
    required: true,
    max: 50,
    message: '商品名称长度在3到50个字符',
    trigger: 'blur'
  },
  price: {
    required: true,
    min: 0.01,
    message: '商品价格必须大于0',
    trigger: 'blur',
    type: 'number'
  },
  discount: {
    min: 0,
    max: 100,
    message: '折扣率范围在0到100',
    trigger: 'blur',
    type: 'number'
  },
  stockQuantity: {
    required: true,
    min: 0,
    message: '库存数量不能为负数',
    trigger: 'blur',
    type: 'number'
  }
}

// 🔧 新增：扩展字段类型定义
export interface ProductExtendedFields {
  category?: string      // 商品类别
  brand?: string         // 商品品牌
  color?: string         // 商品颜色
  size?: string          // 商品尺寸
  extendedAttributes?: Record<string, any>  // 其他扩展属性
}

// 商品表单验证规则接口（基于共享的FieldValidation）
// 注意：FieldValidation 已从 './index' 导入
export interface ProductFormRules {
  productName: FieldValidation & { max: 50 }
  price: FieldValidation & { type: 'number', min: number }
  stockQuantity: FieldValidation & { type: 'number', min: number }
  discount: FieldValidation & { type: 'number', min: number, max: number }
}

// 商品排序选项（基于共享的SortConfig）
// 注意：SortConfig 已从 './index' 导入
export type ProductSortOption = SortConfig & {
  category: 'price' | 'salesCount' | 'createdAt' | 'updatedAt' | 'stockQuantity'
}