/**
 * 📄 分页管理组合式函数
 *
 * 💡 学习目标：
 * 1. 学习如何封装分页逻辑为可复用的组合式函数
 * 2. 理解分页状态管理的最佳实践
 * 3. 掌握响应式数据在分页场景中的应用
 *
 * 📚 相关概念：
 * - 分页：将大量数据分成多个页面显示
 * - 响应式状态：Vue的响应式数据管理机制
 * - 计算属性：基于响应式数据的派生值
 */

import { ref, computed, type Ref } from 'vue'
import type { PaginatedResponse } from '@/types'

/**
 * 📊 分页状态接口
 *
 * 定义分页功能需要的所有状态：
 * - 当前页码
 * - 每页大小
 * - 总记录数
 * - 总页数
 */
export interface PaginationState {
  currentPage: Ref<number>      // 当前页码（从1开始）
  pageSize: Ref<number>         // 每页显示条数
  totalItems: Ref<number>       // 总记录数
  totalPages: Ref<number>       // 总页数
}

/**
 * 🧮 分页计算属性接口
 *
 * 基于分页状态计算的派生值：
 * - 是否有上一页
 * - 是否有下一页
 * - 起始记录索引
 * - 结束记录索引
 */
export interface PaginationComputed {
  hasPreviousPage: Ref<boolean>   // 是否有上一页
  hasNextPage: Ref<boolean>       // 是否有下一页
  startIndex: Ref<number>         // 起始记录索引
  endIndex: Ref<number>           // 结束记录索引
  pageSizes: Ref<number[]>        // 可选的每页大小
  visiblePageNumbers: Ref<number[]> // 可见的页码列表
}

/**
 * 🎯 分页操作方法接口
 *
 * 分页相关的所有操作：
 * - 页码变更
 * - 每页大小变更
 * - 跳转到指定页
 * - 重置分页
 */
export interface PaginationActions {
  handlePageChange: (page: number) => Promise<void>
  handleSizeChange: (size: number) => Promise<void>
  goToPage: (page: number) => Promise<void>
  goToFirstPage: () => Promise<void>
  goToLastPage: () => Promise<void>
  goToPreviousPage: () => Promise<void>
  goToNextPage: () => Promise<void>
  resetPagination: () => void
}

/**
 * 📦 完整的分页功能接口
 */
export interface UseProductPaginationReturn extends PaginationState, PaginationComputed, PaginationActions {
  // 组合所有接口
}

/**
 * 📄 分页配置选项
 */
export interface PaginationOptions {
  pageSize?: number              // 初始每页大小
  pageSizes?: number[]           // 可选的每页大小列表
  maxVisiblePages?: number       // 最多显示多少个页码按钮
  onPageChange?: (page: number) => Promise<void>  // 页码变化回调
  onSizeChange?: (size: number) => Promise<void>  // 大小变化回调
}

/**
 * 📄 分页管理组合式函数
 *
 * 💡 设计思路：
 * 1. 将分页逻辑完全封装，简化组件代码
 * 2. 提供直观的页码导航功能
 * 3. 支持灵活的配置选项
 * 4. 自动计算分页相关的派生状态
 *
 * 🚀 使用示例：
 * ```typescript
 * // 基本使用
 * const {
 *   currentPage,
 *   pageSize,
 *   totalPages,
 *   handlePageChange,
 *   handleSizeChange
 * } = useProductPagination(loadProducts)
 *
 * // 高级配置
 * const pagination = useProductPagination(loadProducts, {
 *   pageSize: 20,
 *   pageSizes: [10, 20, 50, 100],
 *   maxVisiblePages: 7
 * })
 * ```
 *
 * @param loadData - 数据加载函数
 * @param options - 分页配置选项
 * @returns 分页状态和方法
 */
export function useProductPagination(
  loadData: () => Promise<void>,
  options: PaginationOptions = {}
): UseProductPaginationReturn {
  // 解构配置选项，提供默认值
  const {
    pageSize: initialPageSize = 12,                    // 默认每页12条
    pageSizes = [12, 24, 48, 96],                       // 默认可选大小
    maxVisiblePages = 7,                                // 默认显示7个页码
    onPageChange,                                       // 页码变化回调
    onSizeChange                                        // 大小变化回调
  } = options

  // ==========================================
  // 🔥 响应式状态定义
  // ==========================================

  const currentPage = ref<number>(1)      // 当前页码（从1开始）
  const pageSize = ref<number>(initialPageSize)  // 每页大小
  const totalItems = ref<number>(0)       // 总记录数
  const totalPages = ref<number>(1)       // 总页数

  // ==========================================
  // 🧮 计算属性 - 派生状态
  // ==========================================

  /**
   * 📊 是否有上一页
   *
   * 计算逻辑：当前页 > 1
   */
  const hasPreviousPage = computed<boolean>(() => {
    return currentPage.value > 1
  })

  /**
   * 📊 是否有下一页
   *
   * 计算逻辑：当前页 < 总页数
   */
  const hasNextPage = computed<boolean>(() => {
    return currentPage.value < totalPages.value
  })

  /**
   * 📊 起始记录索引
   *
   * 计算逻辑：(当前页 - 1) * 每页大小 + 1
   *
   * 💡 为什么要计算这个：
   * - 用于显示"显示第X到Y条记录"
   * - 帮助用户了解当前查看的数据范围
   */
  const startIndex = computed<number>(() => {
    if (totalItems.value === 0) return 0
    return (currentPage.value - 1) * pageSize.value + 1
  })

  /**
   * 📊 结束记录索引
   *
   * 计算逻辑：起始索引 + 当前页记录数 - 1
   */
  const endIndex = computed<number>(() => {
    if (totalItems.value === 0) return 0
    const end = startIndex.value + Math.min(pageSize.value, totalItems.value - startIndex.value + 1) - 1
    return Math.min(end, totalItems.value)
  })

  /**
   * 📊 可选的每页大小列表
   *
   * 用于Element Plus的分页组件
   */
  const pageSizesList = computed<number[]>(() => {
    return pageSizes
  })

  /**
   * 📊 可见的页码列表
   *
   * 💡 智能页码显示算法：
   * - 始终显示第一页和最后一页
   * - 在当前页附近显示指定数量的页码
   * - 使用省略号表示被隐藏的页码
   *
   * 🎯 这样做的好处：
   * - 避免页码过多导致界面混乱
   * - 用户可以快速跳转到附近的页面
   * - 保持界面的整洁和可用性
   */
  const visiblePageNumbers = computed<number[]>(() => {
    const pages: number[] = []
    const total = totalPages.value
    const current = currentPage.value
    const maxVisible = maxVisiblePages

    // 如果总页数很少，显示所有页码
    if (total <= maxVisible) {
      for (let i = 1; i <= total; i++) {
        pages.push(i)
      }
      return pages
    }

    // 计算起始和结束页码
    let startPage = Math.max(1, current - Math.floor(maxVisible / 2))
    let endPage = Math.min(total, startPage + maxVisible - 1)

    // 调整起始页码，确保显示指定数量的页码
    if (endPage - startPage + 1 < maxVisible) {
      startPage = Math.max(1, endPage - maxVisible + 1)
    }

    // 始终显示第一页
    if (startPage > 1) {
      pages.push(1)
      if (startPage > 2) {
        pages.push(-1) // -1 表示省略号
      }
    }

    // 添加中间的页码
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i)
    }

    // 始终显示最后一页
    if (endPage < total) {
      if (endPage < total - 1) {
        pages.push(-1) // -1 表示省略号
      }
      pages.push(total)
    }

    return pages
  })

  // ==========================================
  // 🎯 分页操作方法
  // ==========================================

  /**
   * 📄 处理页码变化
   *
   * 当用户点击页码或输入页码时调用
   *
   * @param page - 目标页码
   */
  const handlePageChange = async (page: number): Promise<void> => {
    // 验证页码有效性
    if (page < 1 || page > totalPages.value) {
      console.warn(`无效的页码: ${page}，有效范围: 1-${totalPages.value}`)
      return
    }

    // 更新当前页码
    currentPage.value = page

    try {
      // 执行页码变化回调（如果提供）
      if (onPageChange) {
        await onPageChange(page)
      } else {
        // 默认行为：重新加载数据
        await loadData()
      }
    } catch (error) {
      console.error('页码变化处理失败:', error)
      // 出错时回滚页码
      currentPage.value = Math.max(1, Math.min(page, totalPages.value))
    }
  }

  /**
   * 📄 处理每页大小变化
   *
   * 当用户改变每页显示条数时调用
   *
   * @param size - 新的每页大小
   */
  const handleSizeChange = async (size: number): Promise<void> => {
    // 验证每页大小的有效性
    if (!pageSizes.value.includes(size)) {
      console.warn(`无效的每页大小: ${size}，可选值: ${pageSizes.value.join(', ')}`)
      return
    }

    // 更新每页大小
    pageSize.value = size

    // 重置到第一页，因为每页大小变化会影响总页数
    currentPage.value = 1

    try {
      // 执行大小变化回调（如果提供）
      if (onSizeChange) {
        await onSizeChange(size)
      } else {
        // 默认行为：重新加载数据
        await loadData()
      }
    } catch (error) {
      console.error('每页大小变化处理失败:', error)
    }
  }

  /**
   * 🎯 跳转到指定页
   *
   * 提供直接跳转到指定页的功能
   *
   * @param page - 目标页码
   */
  const goToPage = async (page: number): Promise<void> => {
    await handlePageChange(page)
  }

  /**
   * ⏮️ 跳转到第一页
   */
  const goToFirstPage = async (): Promise<void> => {
    await handlePageChange(1)
  }

  /**
   * ⏭️ 跳转到最后一页
   */
  const goToLastPage = async (): Promise<void> => {
    await handlePageChange(totalPages.value)
  }

  /**
   * ◀️ 跳转到上一页
   */
  const goToPreviousPage = async (): Promise<void> => {
    if (hasPreviousPage.value) {
      await handlePageChange(currentPage.value - 1)
    }
  }

  /**
   * ▶️ 跳转到下一页
   */
  const goToNextPage = async (): Promise<void> => {
    if (hasNextPage.value) {
      await handlePageChange(currentPage.value + 1)
    }
  }

  /**
   * 🔄 重置分页状态
   *
   * 将分页状态重置为初始值
   */
  const resetPagination = (): void => {
    currentPage.value = 1
    pageSize.value = initialPageSize
    totalItems.value = 0
    totalPages.value = 1
  }

  // ==========================================
  // 🔧 工具方法
  // ==========================================

  /**
   * 📊 更新分页数据
   *
   * 从服务器响应中提取分页信息并更新状态
   *
   * @param paginationData - 服务器返回的分页数据
   */
  const updatePaginationData = (paginationData: {
    total: number
    page: number
    size: number
    totalPages: number
  }): void => {
    totalItems.value = paginationData.total || 0
    currentPage.value = paginationData.page || 1
    pageSize.value = paginationData.size || initialPageSize
    totalPages.value = paginationData.totalPages || 1
  }

  /**
   * 🧹 清理函数
   *
   * 组件卸载时清理资源
   */
  const cleanup = (): void => {
    // 当前没有需要清理的资源，但预留接口
    // 如果有定时器或事件监听器，在这里清理
  }

  // ==========================================
  // 📦 返回值 - 暴露所有功能
  // ==========================================

  return {
    // 基础状态
    currentPage,
    pageSize,
    totalItems,
    totalPages,

    // 计算属性
    hasPreviousPage,
    hasNextPage,
    startIndex,
    endIndex,
    pageSizes,
    visiblePageNumbers,

    // 操作方法
    handlePageChange,
    handleSizeChange,
    goToPage,
    goToFirstPage,
    goToLastPage,
    goToPreviousPage,
    goToNextPage,
    resetPagination,

    // 工具方法
    updatePaginationData,
    cleanup
  }
}

/**
 * 💡 进阶思考：
 *
 * 1. 🎯 设计原则
 *    - 单一职责：每个函数只负责一个功能
 *    - 高内聚：相关的逻辑组织在一起
 *    - 低耦合：不依赖具体的组件实现
 *
 * 2. 🔧 技术亮点
 *    - 智能页码算法，避免显示过多页码
 *    - 完善的边界情况处理
 *    - 灵活的配置选项
 *    - TypeScript完整的类型支持
 *
 * 3. 🎓 学习价值
 *    - 展示了组合式函数的设计模式
 *    - 演示了计算属性的高级用法
 *    - 体现了异步操作的最佳实践
 *    - 提供了完整的错误处理机制
 *
 * 4. 🚀 扩展方向
 *    - 添加跳转到指定页的功能
 *    - 支持自定义页码显示算法
 *    - 添加分页状态持久化
 *    - 支持虚拟滚动（大数据量场景）
 */