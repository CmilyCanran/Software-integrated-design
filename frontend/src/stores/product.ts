// ============================================================================
// 商品状态管理 (Pinia Store) - 负责管理商品相关状态和数据
// ============================================================================

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type {
  Product,
  ProductQueryParams,
  ProductStats,
  ProductCreateRequest,
  ProductUpdateRequest
} from '@/types/product'
import { productAPI } from '@/api/product'
import { ElMessage } from 'element-plus'

export const useProductStore = defineStore('product', () => {
  // ============================================================================
  // 状态定义
  // ============================================================================

  // 商品列表
  const products = ref<Product[]>([])

  // 当前选中的商品
  const currentProduct = ref<Product | null>(null)

  // 加载状态
  const loading = ref(false)

  // 分页信息
  const pagination = ref({
    page: 1,
    size: 12,
    total: 0,
    totalPages: 0
  })

  // 统计信息
  const productStats = ref<ProductStats | null>(null)

  // 当前显示模式（网格/表格）
  const displayMode = ref<'grid' | 'table'>('grid')

  // ============================================================================
  // 计算属性
  // ============================================================================

  // 商品总数
  const totalProducts = computed(() => pagination.value.total)

  // 是否有下一页
  const hasNext = computed(() => pagination.value.page < pagination.value.totalPages)

  // 是否有上一页
  const hasPrev = computed(() => pagination.value.page > 1)

  // ============================================================================
  // 操作方法
  // ============================================================================

  // 设置加载状态
  const setLoading = (value: boolean) => {
    loading.value = value
  }

  // 设置商品列表
  const setProducts = (data: Product[], paginationInfo?: any) => {
    products.value = data
    if (paginationInfo) {
      pagination.value = {
        ...paginationInfo,
        page: paginationInfo.page || 1,
        size: paginationInfo.size || 12,
        total: paginationInfo.total || 0,
        totalPages: paginationInfo.totalPages || 0
      }
    }
  }

  // 设置当前商品
  const setCurrentProduct = (product: Product | null) => {
    currentProduct.value = product
  }

  // 获取商家商品列表
  const fetchMerchantProducts = async (params?: ProductQueryParams) => {
    try {
      setLoading(true)

      const response = await productAPI.getMerchantProducts(params)

      setProducts(response.data, {
        total: response.total,
        totalPages: response.totalPages,
        page: response.page,
        size: response.size
      })

      return response
    } catch (error) {
      console.error('❌ 获取商品列表失败:', error)
      ElMessage.error('获取商品列表失败，请重试')
      throw error
    } finally {
      setLoading(false)
    }
  }

  // 获取商品详情
  const fetchProduct = async (id: number) => {
    try {
      setLoading(true)

      const product = await productAPI.getProduct(id)

      setCurrentProduct(product)
      return product
    } catch (error) {
      console.error('❌ 获取商品详情失败:', error)
      ElMessage.error('获取商品详情失败，请重试')
      throw error
    } finally {
      setLoading(false)
    }
  }

  // 创建商品
  const createProduct = async (data: ProductCreateRequest): Promise<Product> => {
    try {
      setLoading(true)

      const product = await productAPI.createProduct(data)

      // 将新创建的商品添加到列表开头
      products.value = [product, ...products.value]

      ElMessage.success('商品创建成功')
      return product
    } catch (error) {
      console.error('❌ 创建商品失败:', error)
      ElMessage.error('创建商品失败，请重试')
      throw error
    } finally {
      setLoading(false)
    }
  }

  // 更新商品
  const updateProduct = async (id: number, data: ProductUpdateRequest): Promise<Product> => {
    try {
      setLoading(true)

      const updatedProduct = await productAPI.updateProduct(id, data)

      // 更新列表中的商品
      const index = products.value.findIndex(p => p.id === id)
      if (index !== -1) {
        products.value[index] = updatedProduct
      }

      // 如果当前商品被更新，也更新当前商品
      if (currentProduct.value?.id === id) {
        setCurrentProduct(updatedProduct)
      }

      ElMessage.success('商品更新成功')
      return updatedProduct
    } catch (error) {
      console.error('❌ 更新商品失败:', error)
      ElMessage.error('更新商品失败，请重试')
      throw error
    } finally {
      setLoading(false)
    }
  }

  // 删除商品
  const deleteProduct = async (id: number) => {
    try {
      setLoading(true)

      await productAPI.deleteProduct(id)

      // 从列表中移除商品
      products.value = products.value.filter(p => p.id !== id)

      ElMessage.success('商品删除成功')

      // 如果删除的是当前选中的商品，重置当前商品
      if (currentProduct.value?.id === id) {
        setCurrentProduct(null)
      }
    } catch (error) {
      console.error('❌ 删除商品失败:', error)
      ElMessage.error('删除商品失败，请重试')
    } finally {
      setLoading(false)
    }
  }

  // 切换商品上架/下架状态
  const toggleProductStatus = async (id: number, isAvailable: boolean): Promise<Product> => {
    try {
      setLoading(true)

      const product = await productAPI.toggleProductStatus(id, isAvailable)

      // 更新列表中的商品状态
      const index = products.value.findIndex(p => p.id === id)
      if (index !== -1) {
        products.value[index] = product
      }

      // 如果当前商品被更新，也更新当前商品
      if (currentProduct.value?.id === id) {
        setCurrentProduct(product)
      }

      ElMessage.success(isAvailable ? '商品上架成功' : '商品下架成功')
      return product
    } catch (error) {
      console.error('❌ 切换商品状态失败:', error)
      ElMessage.error('切换商品状态失败，请重试')
      throw error
    } finally {
      setLoading(false)
    }
  }

  // 更新商品库存
  const updateStock = async (id: number, stockQuantity: number): Promise<Product> => {
    try {
      setLoading(true)

      const product = await productAPI.updateStock(id, stockQuantity)

      // 更新列表中的商品库存
      const index = products.value.findIndex(p => p.id === id)
      if (index !== -1) {
        products.value[index] = product
      }

      // 如果当前商品被更新，也更新当前商品
      if (currentProduct.value?.id === id) {
        setCurrentProduct(product)
      }

      ElMessage.success('库存更新成功')
      return product
    } catch (error) {
      console.error('❌ 更新库存失败:', error)
      ElMessage.error('更新库存失败，请重试')
      throw error
    } finally {
      setLoading(false)
    }
  }

  // 获取商品统计信息
  const fetchProductStats = async () => {
    try {
      setLoading(true)

      const stats = await productAPI.getProductStats()

      productStats.value = stats
      return stats
    } catch (error) {
      console.error('❌ 获取商品统计失败:', error)
      ElMessage.error('获取统计信息失败，请重试')
      throw error
    } finally {
      setLoading(false)
    }
  }

  // 搜索商品
  const searchProducts = async (query: string, params?: ProductQueryParams) => {
    try {
      setLoading(true)

      const response = await productAPI.searchProducts(query, params)

      setProducts(response.data, {
        total: response.total,
        totalPages: response.totalPages,
        page: response.page,
        size: response.size
      })

      return response
    } catch (error) {
      console.error('❌ 搜索商品失败:', error)
      ElMessage.error('搜索商品失败，请重试')
      throw error
    } finally {
      setLoading(false)
    }
  }

  // 按分类查询商品
  const fetchProductsByCategory = async (category: string, params?: ProductQueryParams) => {
    try {
      setLoading(true)

      const response = await productAPI.getProductsByCategory(category, params)

      setProducts(response.data, {
        total: response.total,
        totalPages: response.totalPages,
        page: response.page,
        size: response.size
      })

      return response
    } catch (error) {
      console.error('❌ 按分类查询失败:', error)
      ElMessage.error('按分类查询商品失败，请重试')
      throw error
    } finally {
      setLoading(false)
    }
  }

  // 清空商品状态
  const clearProducts = () => {
    products.value = []
    currentProduct.value = null
    pagination.value = {
      page: 1,
      size: 12,
      total: 0,
      totalPages: 0
    }
  }

  // ============================================================================
  // 返回状态和方法
  // ============================================================================
  return {
    // 状态
    products,
    currentProduct,
    loading,
    pagination,
    productStats,

    // 计算属性
    totalProducts,
    hasNext,
    hasPrev,
    displayMode,

    // 方法
    setLoading,
    setProducts,
    setCurrentProduct,
    fetchMerchantProducts,
    fetchProduct,
    createProduct,
    updateProduct,
    deleteProduct,
    toggleProductStatus,
    updateStock,
    fetchProductStats,
    searchProducts,
    fetchProductsByCategory,
    clearProducts
  }
})

export default useProductStore