// ============================================================================
// 商品API服务封装 (TypeScript版本) - 基于后端products表的RESTful API
// ============================================================================

import { api } from './request'
import type { Product, ProductCreateRequest, ProductUpdateRequest, ProductQueryParams, PaginatedResponse, ProductStats, ProductSpecifications } from '@/types/product'

// ============================================================================
// 🔧 DTO对齐的数据预处理和错误处理工具函数
// ============================================================================

/**
 * 🔧 DTO对齐的数据预处理函数
 * 确保前端数据完全符合后端DTO格式要求，特别是动态规格系统
 *
 * 🔧 重要修正：扩展属性也是规格！但前端的预处理是数据格式标准化，不是业务逻辑合并
 * 后端会负责将 category、brand、color、size、extendedAttributes 都合并到统一的规格系统中
 */
function preprocessProductData(data: ProductCreateRequest | ProductUpdateRequest): ProductCreateRequest | ProductUpdateRequest {
  const processedData = { ...data }

  // 处理规格数据格式：确保所有规格值都是字符串数组
  if (processedData.specifications) {
    const normalizedSpecs: ProductSpecifications = {}

    Object.entries(processedData.specifications).forEach(([key, values]) => {
      if (values !== null && values !== undefined) {
        // 确保规格值是字符串数组
        if (Array.isArray(values)) {
          normalizedSpecs[key] = values.map(v => String(v)).filter(v => v !== '')
        } else {
          normalizedSpecs[key] = [String(values)].filter(v => v !== '')
        }
      }
    })

    processedData.specifications = normalizedSpecs
  }

  // 处理扩展属性格式：确保扩展属性值格式正确
  if (processedData.extendedAttributes) {
    const normalizedExtended: Record<string, any> = {}

    Object.entries(processedData.extendedAttributes).forEach(([key, value]) => {
      if (value !== null && value !== undefined && value !== '') {
        // 扩展属性保持原格式，但确保非空
        normalizedExtended[key] = value
      }
    })

    processedData.extendedAttributes = normalizedExtended
  }

  // 商品图片简化：只处理主图URL
  if (processedData.mainImageUrl && typeof processedData.mainImageUrl === 'string') {
    processedData.mainImageUrl = processedData.mainImageUrl.trim()
  } else {
    processedData.mainImageUrl = ''
  }

  // 处理productData：构建后端JSONB数据结构
  const productData: Record<string, any> = {}

  // 简化图片数据结构：只有主图
  if (processedData.mainImageUrl) {
    productData.image_data = {
      main_image: processedData.mainImageUrl
    }
  }

  // 重要：不在这里合并规格！让后端处理规格系统的统一合并
  // 前端只需要确保数据格式正确，业务逻辑合并由后端负责
  if (processedData.specifications && Object.keys(processedData.specifications).length > 0) {
    productData.specifications = processedData.specifications
  }

  processedData.productData = productData

  return processedData
}

/**
 * 🔧 智能后端验证错误处理
 * 将后端验证错误映射为用户友好的错误信息
 */
function handleBackendValidationError(error: any): { field: string; message: string }[] {
  const errors: { field: string; message: string }[] = []

  if (error?.response?.data) {
    const errorData = error.response.data

    // 处理Spring Boot验证错误格式
    if (errorData.errors && Array.isArray(errorData.errors)) {
      errorData.errors.forEach((err: any) => {
        const field = err.field || 'unknown'
        const message = err.defaultMessage || err.message || '验证失败'

        // 映射后端字段名为前端显示名称
        const fieldMappings: Record<string, string> = {
          productName: '商品名称',
          price: '商品价格',
          stockQuantity: '库存数量',
          discount: '折扣率',
          description: '商品描述',
          isAvailable: '上架状态',
          specifications: '商品规格',
          category: '商品类别',
          brand: '商品品牌',
          color: '商品颜色',
          size: '商品尺寸',
          mainImageUrl: '主图',
          imageUrls: '商品图片'
        }

        const displayName = fieldMappings[field] || field
        errors.push({ field, message: `${displayName}: ${message}` })
      })
    }

    // 处理自定义业务错误
    if (errorData.message) {
      errors.push({ field: 'general', message: errorData.message })
    }
  }

  // 处理网络错误等其他情况
  if (errors.length === 0) {
    if (error?.response?.status === 400) {
      errors.push({ field: 'general', message: '请求数据格式错误，请检查输入' })
    } else if (error?.response?.status === 401) {
      errors.push({ field: 'general', message: '请先登录后再操作' })
    } else if (error?.response?.status === 403) {
      errors.push({ field: 'general', message: '权限不足，无法执行此操作' })
    } else if (error?.response?.status === 404) {
      errors.push({ field: 'general', message: '商品不存在或已被删除' })
    } else {
      errors.push({ field: 'general', message: error?.message || '操作失败，请稍后重试' })
    }
  }

  return errors
}

// ============================================================================
// 商品API服务对象
// ============================================================================
export const productAPI = {
  // 获取商品列表（支持分页、筛选、排序）
  getProducts: (params: ProductQueryParams): Promise<PaginatedResponse<Product>> => {
    return api.get('/products', { params }).then((response: any) => {
      // 处理后端Spring Boot分页响应格式
      return {
        data: response.content || [],
        total: response.totalElements || 0,
        page: response.number || 0,
        size: response.size || 10,
        totalPages: response.totalPages || 0,
        hasNext: !response.last,
        hasPrev: !response.first
      }
    })
  },

  // 获取商家商品列表（根据当前登录用户过滤）
  getMerchantProducts: (params?: ProductQueryParams): Promise<PaginatedResponse<Product>> => {
    return api.get('/products/merchant', { params }).then((response: any) => {
      // 处理后端Spring Boot分页响应格式
      return {
        data: response.content || [],
        total: response.totalElements || 0,
        page: response.number || 0,
        size: response.size || 12,
        totalPages: response.totalPages || 0,
        hasNext: !response.last,
        hasPrev: !response.first
      }
    })
  },

  // 获取商品详情
  getProduct: (id: number): Promise<Product> => {
    return api.get(`/products/${id}`).then((product) => {
      return product
    })
  },

  // 创建商品
  createProduct: (data: ProductCreateRequest): Promise<Product> => {
    // DTO对齐的数据预处理
    const processedData = preprocessProductData(data) as ProductCreateRequest

    return api.post('/products', processedData)
  },

  // 更新商品
  updateProduct: (id: number, data: ProductUpdateRequest): Promise<Product> => {
    // DTO对齐的数据预处理
    const processedData = preprocessProductData(data) as ProductUpdateRequest

    return api.put(`/products/${id}`, processedData)
  },

  // 删除商品
  deleteProduct: (id: number): Promise<void> => {
    return api.delete(`/products/${id}`)
  },

  // 商品上架/下架
  toggleProductStatus: (id: number, isAvailable: boolean): Promise<Product> => {
    return api.post(`/products/${id}/toggle-availability`)
  },

  // 更新商品库存
  updateStock: (id: number, stockQuantity: number): Promise<Product> => {
    return api.put(`/products/${id}/stock`, { stockQuantity })
  },

  // 更新商品价格
  updatePrice: (id: number, price: number): Promise<Product> => {
    return api.put(`/products/${id}/price`, { price })
  },

  // 更新商品折扣
  updateDiscount: (id: number, discount: number): Promise<Product> => {
    return api.put(`/products/${id}/discount`, { discount })
  },

  // 上传商品图片
  uploadProductImage: (id: number, file: File): Promise<{ imageUrl: string }> => {
    const formData = new FormData()
    formData.append('file', file)

    return api.post(`/products/${id}/image`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 删除商品图片
  deleteProductImage: (id: number, imageUrl: string): Promise<void> => {
    return api.delete(`/products/${id}/image`, { params: { imageUrl } })
  },

  // 搜索商品
  searchProducts: (query: string, params?: ProductQueryParams): Promise<PaginatedResponse<Product>> => {
    return api.get('/products/search', {
      params: {
        query,
        ...params
      }
    })
  },

  // 按分类查询商品
  getProductsByCategory: (category: string, params?: ProductQueryParams): Promise<PaginatedResponse<Product>> => {
    return api.get(`/products/category/${category}`, { params })
  },

  // 获取热销商品
  getTopSellingProducts: (params?: ProductQueryParams): Promise<PaginatedResponse<Product>> => {
    return api.get('/products/top-selling', { params })
  },

  // 获取新品商品
  getNewestProducts: (params?: ProductQueryParams): Promise<PaginatedResponse<Product>> => {
    return api.get('/products/newest', { params })
  },

  // 获取商品统计信息
  getProductStats: (): Promise<ProductStats> => {
    return api.get('/products/statistics')
  },

  // 获取商家商品统计信息（复用通用统计接口，后端会根据当前用户过滤）
  getMerchantProductStats: (): Promise<ProductStats> => {
    return api.get('/products/statistics')
  },

  // 批量操作 - 批量上架
  batchPublishProducts: (productIds: number[]): Promise<void> => {
    return api.post('/products/batch/publish', { productIds })
  },

  // 批量操作 - 批量下架
  batchUnpublishProducts: (productIds: number[]): Promise<void> => {
    return api.post('/products/batch/unpublish', { productIds })
  },

  // 批量操作 - 批量删除
  batchDeleteProducts: (productIds: number[]): Promise<void> => {
    return api.post('/products/batch/delete', { productIds })
  }
}

// ============================================================================
// 导出便捷方法和工具函数
// ============================================================================

/**
 * 🔧 导出数据处理和错误处理工具函数供组件使用
 */
export const productAPIUtils = {
  /**
   * 数据预处理函数
   */
  preprocessProductData,

  /**
   * 错误处理函数
   */
  handleBackendValidationError,

  /**
   * 验证商品数据格式
   */
  validateProductData: (data: ProductCreateRequest | ProductUpdateRequest): { isValid: boolean; errors: string[] } => {
    const errors: string[] = []

    // 基本字段验证
    if (!data.productName || data.productName.trim().length === 0) {
      errors.push('商品名称不能为空')
    }
    if (data.productName && data.productName.length > 50) {
      errors.push('商品名称长度不能超过50个字符')
    }
    if (data.price === null || data.price === undefined || data.price <= 0) {
      errors.push('商品价格必须大于0')
    }
    if (data.stockQuantity === null || data.stockQuantity === undefined || data.stockQuantity < 0) {
      errors.push('库存数量不能为负数')
    }
    if (data.discount !== undefined && (data.discount < 0 || data.discount > 100)) {
      errors.push('折扣率必须在0到100之间')
    }

    return {
      isValid: errors.length === 0,
      errors
    }
  }
}

export default productAPI