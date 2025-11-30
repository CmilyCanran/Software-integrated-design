// ============================================================================
// 商品API服务封装 (TypeScript版本) - 基于后端products表的RESTful API
// ============================================================================

import { api } from './request'
import type { Product, ProductCreateRequest, ProductUpdateRequest, ProductQueryParams, PaginatedResponse, ProductStats } from '@/types/product'

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
    return api.get(`/products/${id}`)
  },

  // 创建商品
  createProduct: (data: ProductCreateRequest): Promise<Product> => {
    return api.post('/products', data)
  },

  // 更新商品
  updateProduct: (id: number, data: ProductUpdateRequest): Promise<Product> => {
    console.log('🔍 [DEBUG] ProductAPI - 开始更新商品请求')
    console.log('🔍 [DEBUG] ProductAPI - 请求URL:', `/products/${id}`)
    console.log('🔍 [DEBUG] ProductAPI - 商品ID:', id)
    console.log('🔍 [DEBUG] ProductAPI - 请求数据:', data)
    console.log('🔍 [DEBUG] ProductAPI - 请求数据详情:')
    console.log('  - 商品名称:', data.productName)
    console.log('  - 价格:', data.price, '(类型:', typeof data.price, ')')
    console.log('  - 库存:', data.stockQuantity, '(类型:', typeof data.stockQuantity, ')')
    console.log('  - 折扣:', data.discount, '(类型:', typeof data.discount, ')')
    console.log('  - 是否上架:', data.isAvailable, '(类型:', typeof data.isAvailable, ')')
    console.log('  - 规格:', data.specifications)
    console.log('  - 主图URL:', data.mainImageUrl)
    console.log('  - 描述:', data.description)

    return api.put(`/products/${id}`, data)
      .then((response) => {
        console.log('🔍 [DEBUG] ProductAPI - 更新商品请求成功')
        console.log('🔍 [DEBUG] ProductAPI - 响应数据:', response)
        return response
      })
      .catch((error) => {
        console.error('❌ [DEBUG] ProductAPI - 更新商品请求失败:', error)
        console.error('❌ [DEBUG] ProductAPI - 错误详情:', {
          message: error?.message,
          stack: error?.stack,
          response: error?.response?.data,
          status: error?.response?.status
        })
        throw error
      })
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
    formData.append('image', file)

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
// 导出便捷方法
// ============================================================================
export default productAPI