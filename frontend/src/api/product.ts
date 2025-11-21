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
    return api.get('/products', { params })
  },

  // 获取商家商品列表（根据当前登录用户过滤）
  getMerchantProducts: (params: ProductQueryParams): Promise<PaginatedResponse<Product>> => {
    return api.get('/products/merchant', { params })
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
    return api.put(`/products/${id}`, data)
  },

  // 删除商品
  deleteProduct: (id: number): Promise<void> => {
    return api.delete(`/products/${id}`)
  },

  // 商品上架/下架
  toggleProductStatus: (id: number, isAvailable: boolean): Promise<Product> => {
    return api.put(`/products/${id}/status`, { isAvailable })
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
    return api.get('/products/stats')
  },

  // 获取商家商品统计信息
  getMerchantProductStats: (): Promise<ProductStats> => {
    return api.get('/products/merchant/stats')
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