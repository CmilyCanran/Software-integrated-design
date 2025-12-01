/**
 * 🔍 商品搜索和筛选组合式函数
 *
 * 💡 学习目标：
 * 1. 学习Vue 3 Composition API的高级用法
 * 2. 理解如何将复杂逻辑提取为可复用的组合式函数
 * 3. 掌握防抖(debounce)技术的实际应用
 *
 * 📚 相关概念：
 * - 组合式函数：将相关逻辑组合在一起的复用单元
 * - 防抖：延迟执行函数，避免频繁触发
 * - 响应式数据：Vue 3的响应式系统原理
 */

import { ref, watch, type Ref } from 'vue'
import { useProductStore } from '@/stores/product'
import type { ProductQueryParams } from '@/types/product'

/**
 * 🎯 搜索和筛选状态管理
 *
 * 包含所有与搜索相关的状态：
 * - 搜索关键词
 * - 分类筛选
 * - 状态筛选
 */
export interface SearchState {
  searchQuery: Ref<string>
  categoryFilter: Ref<string>
  statusFilter: Ref<string>
}

/**
 * 🚀 搜索操作方法
 *
 * 提供搜索相关的所有操作：
 * - 处理搜索输入
 * - 清除搜索条件
 * - 处理筛选变化
 */
export interface SearchActions {
  handleSearch: () => void
  handleSearchClear: () => Promise<void>
  handleFilterChange: () => Promise<void>
  resetAllFilters: () => Promise<void>
}

/**
 * 📊 组合式函数返回值
 *
 * 包含状态和方法的完整接口
 */
export interface UseProductSearchReturn {
  // 状态
  searchQuery: Ref<string>
  categoryFilter: Ref<string>
  statusFilter: Ref<string>

  // 方法
  handleSearch: () => void
  handleSearchClear: () => Promise<void>
  handleFilterChange: () => Promise<void>
  resetAllFilters: () => Promise<void>
}

/**
 * 🔍 商品搜索和筛选组合式函数
 *
 * 💡 设计思路：
 * 1. 将搜索相关的所有逻辑集中管理
 * 2. 提供防抖功能，避免频繁API调用
 * 3. 支持灵活的筛选条件组合
 * 4. 提供清晰的接口，便于组件使用
 *
 * 🚀 使用示例：
 * ```typescript
 * // 在组件中使用
 * const {
 *   searchQuery,
 *   categoryFilter,
 *   statusFilter,
 *   handleSearch,
 *   handleSearchClear
 * } = useProductSearch(loadProducts)
 * ```
 *
 * @param loadProducts - 加载商品列表的函数
 * @param options - 配置选项
 * @returns 搜索状态和方法
 */
export function useProductSearch(
  loadProducts: () => Promise<void>,
  options: {
    debounceDelay?: number  // 防抖延迟时间（毫秒）
    autoSearch?: boolean    // 是否自动触发搜索
  } = {}
): UseProductSearchReturn {
  // 解构配置选项，提供默认值
  const {
    debounceDelay = 300,  // 默认300ms防抖
    autoSearch = true     // 默认启用自动搜索
  } = options

  // 初始化产品存储
  const productStore = useProductStore()

  // ==========================================
  // 🔥 响应式状态定义
  // ==========================================

  const searchQuery = ref<string>('')        // 搜索关键词
  const categoryFilter = ref<string>('')     // 分类筛选
  const statusFilter = ref<string>('')       // 状态筛选

  // 防抖定时器
  let searchTimer: ReturnType<typeof setTimeout> | null = null

  // ==========================================
  // 🎯 核心搜索逻辑
  // ==========================================

  /**
   * 🔍 处理搜索输入 - 带防抖功能
   *
   * 💡 防抖原理：
   * 1. 用户输入时，清除之前的定时器
   * 2. 设置新的定时器，延迟执行搜索
   * 3. 如果在延迟时间内再次输入，重复步骤1-2
   * 4. 直到用户停止输入超过延迟时间，才执行搜索
   *
   * 🎯 这样做的好处：
   * - 减少不必要的API调用
   * - 提升用户体验
   * - 降低服务器压力
   */
  const handleSearch = (): void => {
    // 重置到第一页，因为搜索条件变化了
    if (productStore.pagination) {
      productStore.pagination.page = 1
    }

    // 如果启用了自动搜索，使用防抖机制
    if (autoSearch) {
      // 清除之前的定时器（如果有）
      if (searchTimer) {
        clearTimeout(searchTimer)
      }

      // 设置新的定时器，延迟执行搜索
      searchTimer = setTimeout(async () => {
        try {
          await loadProducts()
        } catch (error) {
          console.error('搜索商品失败:', error)
        }
      }, debounceDelay)
    } else {
      // 如果不启用自动搜索，需要手动调用loadProducts
      console.log('搜索条件已更新，请手动触发搜索')
    }
  }

  /**
   * 🧹 清除搜索条件
   *
   * 重置所有搜索相关的状态，并重新加载数据
   */
  const handleSearchClear = async (): Promise<void> => {
    // 清除搜索关键词
    searchQuery.value = ''

    // 重置分页到第一页
    if (productStore.pagination) {
      productStore.pagination.page = 1
    }

    // 清除防抖定时器
    if (searchTimer) {
      clearTimeout(searchTimer)
      searchTimer = null
    }

    try {
      // 重新加载商品列表（无搜索条件）
      await loadProducts()
    } catch (error) {
      console.error('清除搜索后重新加载商品失败:', error)
    }
  }

  /**
   * 🎛️ 处理筛选条件变化
   *
   * 当分类或状态筛选条件变化时调用
   */
  const handleFilterChange = async (): Promise<void> => {
    // 重置到第一页，因为筛选条件变化了
    if (productStore.pagination) {
      productStore.pagination.page = 1
    }

    // 清除防抖定时器，避免冲突
    if (searchTimer) {
      clearTimeout(searchTimer)
      searchTimer = null
    }

    try {
      // 立即重新加载商品列表
      await loadProducts()
    } catch (error) {
      console.error('筛选商品失败:', error)
    }
  }

  /**
   * 🔄 重置所有筛选条件
   *
   * 一键重置所有搜索和筛选条件
   */
  const resetAllFilters = async (): Promise<void> => {
    // 重置所有筛选状态
    searchQuery.value = ''
    categoryFilter.value = ''
    statusFilter.value = ''

    // 重置分页
    if (productStore.pagination) {
      productStore.pagination.page = 1
    }

    // 清除定时器
    if (searchTimer) {
      clearTimeout(searchTimer)
      searchTimer = null
    }

    try {
      // 重新加载无筛选的商品列表
      await loadProducts()
    } catch (error) {
      console.error('重置筛选条件后重新加载商品失败:', error)
    }
  }

  /**
   * 🧹 清理函数
   *
   * 组件卸载时清理资源，防止内存泄漏
   */
  const cleanup = (): void => {
    if (searchTimer) {
      clearTimeout(searchTimer)
      searchTimer = null
    }
  }

  // ==========================================
  // 👀 监听状态变化（可选）
  // ==========================================

  // 如果需要在组合式函数外部监听状态变化，可以添加watch
  // watch([searchQuery, categoryFilter, statusFilter], () => {
  //   console.log('搜索条件发生变化:', {
  //     search: searchQuery.value,
  //     category: categoryFilter.value,
  //     status: statusFilter.value
  //   })
  // })

  // ==========================================
  // 📦 返回值 - 暴露状态和方法
  // ==========================================

  // 🎯 返回组合式函数的结果
  // 包含所有状态和方法，供组件使用
  return {
    // 状态
    searchQuery,
    categoryFilter,
    statusFilter,

    // 方法
    handleSearch,
    handleSearchClear,
    handleFilterChange,
    resetAllFilters,

    // 工具方法（可选暴露）
    cleanup
  }
}

/**
 * 💡 进阶思考：
 *
 * 1. 🔍 性能优化
 *    - 防抖机制减少了不必要的API调用
 *    - 清理机制防止了内存泄漏
 *    - 状态管理集中化，减少重复渲染
 *
 * 2. 🎯 可扩展性
 *    - 支持自定义防抖延迟时间
 *    - 支持配置是否自动搜索
 *    - 易于添加新的筛选条件
 *
 * 3. 🛠️ 可维护性
 *    - 逻辑集中管理，便于修改和维护
 *    - 清晰的接口定义，便于理解和使用
 *    - 完善的错误处理和边界情况处理
 *
 * 4. 📚 教学价值
 *    - 展示了Vue 3 Composition API的实际应用
 *    - 演示了防抖技术的实现原理
 *    - 体现了良好的代码组织和架构设计
 */