/**
 * 🛠️ 商品CRUD操作组合式函数
 *
 * 💡 学习目标：
 * 1. 学习如何封装复杂的业务逻辑为组合式函数
 * 2. 理解CRUD操作的最佳实践模式
 * 3. 掌握异步操作和错误处理的组合使用
 *
 * 📚 相关概念：
 * - CRUD：创建(Create)、读取(Read)、更新(Update)、删除(Delete)
 * - 组合式函数：将相关逻辑组合在一起的复用单元
 * - 异步操作：Promise和async/await的使用
 * - 错误处理：统一的错误处理策略
 */

import { ref, type Ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useProductStore } from '@/stores/product'
import { handleError } from '@/utils/errorHandler'
import type { Product, ProductCreateRequest, ProductUpdateRequest } from '@/types/product'
import type { AppError } from '@/types/error'

/**
 * 🎯 CRUD操作状态接口
 *
 * 管理CRUD操作过程中的各种状态：
 * - 加载状态
 * - 对话框状态
 * - 当前操作的产品
 */
export interface CrudState {
  loading: Ref<boolean>           // 操作加载状态
  formDialogVisible: Ref<boolean> // 表单对话框显示状态
  isEditing: Ref<boolean>         // 是否为编辑模式
  editingProduct: Ref<Product | null> // 正在编辑的产品
}

/**
 * 🚀 CRUD操作方法接口
 *
 * 提供所有CRUD相关操作：
 * - 创建产品
 * - 更新产品
 * - 删除产品
 * - 切换产品状态
 */
export interface CrudActions {
  // 创建操作
  openCreateDialog: () => void
  handleCreate: (formData: ProductCreateRequest) => Promise<void>

  // 更新操作
  openEditDialog: (product: Product) => Promise<void>
  handleUpdate: (id: number, formData: ProductUpdateRequest) => Promise<void>

  // 删除操作
  confirmDelete: (product: Product) => Promise<void>
  handleDelete: (id: number) => Promise<void>

  // 状态切换
  toggleProductStatus: (product: Product) => Promise<void>

  // 对话框管理
  closeFormDialog: () => void
  resetFormState: () => void
}

/**
 * 📊 CRUD事件接口
 *
 * 定义CRUD操作完成后可以触发的事件
 */
export interface CrudEvents {
  onCreateSuccess?: (product: Product) => void
  onUpdateSuccess?: (product: Product) => void
  onDeleteSuccess?: (id: number) => void
  onStatusChangeSuccess?: (product: Product) => void
  onError?: (error: AppError) => void
}

/**
 * 📦 完整的CRUD功能接口
 */
export interface UseProductCrudReturn extends CrudState, CrudActions {
  // 组合所有接口
}

/**
 * 🛠️ 商品CRUD操作组合式函数
 *
 * 💡 设计思路：
 * 1. 将产品管理的所有CRUD操作集中管理
 * 2. 提供统一的错误处理和用户反馈
 * 3. 支持灵活的事件回调机制
 * 4. 提供清晰的接口，简化组件代码
 *
 * 🚀 使用示例：
 * ```typescript
 * // 基本使用
 * const {
 *   loading,
 *   formDialogVisible,
 *   isEditing,
 *   editingProduct,
 *   openCreateDialog,
 *   handleCreate,
 *   openEditDialog,
 *   handleUpdate,
 *   confirmDelete,
 *   closeFormDialog
 * } = useProductCrud()
 *
 * // 高级配置
 * const crud = useProductCrud({
 *   onCreateSuccess: (product) => {
 *     console.log('产品创建成功:', product)
 *   },
 *   onError: (error) => {
 *     console.error('操作失败:', error)
 *   }
 * })
 * ```
 *
 * @param events - CRUD事件回调
 * @returns CRUD状态和方法
 */
export function useProductCrud(events: CrudEvents = {}): UseProductCrudReturn {
  // 解构事件回调，提供默认值
  const {
    onCreateSuccess,
    onUpdateSuccess,
    onDeleteSuccess,
    onStatusChangeSuccess,
    onError
  } = events

  // 初始化产品存储
  const productStore = useProductStore()

  // ==========================================
  // 🔥 响应式状态定义
  // ==========================================

  const loading = ref<boolean>(false)                    // 操作加载状态
  const formDialogVisible = ref<boolean>(false)         // 表单对话框显示状态
  const isEditing = ref<boolean>(false)                  // 是否为编辑模式
  const editingProduct = ref<Product | null>(null)      // 正在编辑的产品

  // ==========================================
  // 🎯 通用工具函数
  // ==========================================

  /**
   * 🔄 重置表单状态
   *
   * 关闭对话框并重置所有编辑相关状态
   */
  const resetFormState = (): void => {
    formDialogVisible.value = false
    isEditing.value = false
    editingProduct.value = null
    loading.value = false
  }

  /**
   * 🎯 显示操作成功消息
   *
   * 统一的成切消息显示
   *
   * @param message - 成功消息
   */
  const showSuccessMessage = (message: string): void => {
    ElMessage.success(message)
  }

  /**
   * ❌ 处理操作错误
   *
   * 统一的错误处理，支持自定义错误处理
   *
   * @param error - 错误对象
   * @param defaultMessage - 默认错误消息
   */
  const handleOperationError = (error: unknown, defaultMessage: string): void => {
    // 使用统一的错误处理工具
    const appError = handleError(error, {
      customMessage: defaultMessage,
      showToast: true
    })

    // 触发错误事件回调（如果提供）
    if (onError) {
      onError(appError)
    }
  }

  /**
   * ⚡ 执行异步操作
   *
   * 通用的异步操作封装，包含加载状态和错误处理
   *
   * @param operation - 异步操作函数
   * @param successMessage - 成功消息
   * @param errorMessage - 错误消息
   * @returns 操作结果
   */
  const executeAsyncOperation = async <T>(
    operation: () => Promise<T>,
    successMessage?: string,
    errorMessage?: string
  ): Promise<T | null> => {
    try {
      // 设置加载状态
      loading.value = true

      // 执行异步操作
      const result = await operation()

      // 显示成功消息（如果提供）
      if (successMessage) {
        showSuccessMessage(successMessage)
      }

      return result
    } catch (error) {
      // 处理错误
      handleOperationError(error, errorMessage || '操作失败')
      return null
    } finally {
      // 重置加载状态
      loading.value = false
    }
  }

  // ==========================================
  // 🆕 创建操作相关方法
  // ==========================================

  /**
   * 📖 打开创建产品对话框
   *
   * 初始化创建模式的状态
   */
  const openCreateDialog = (): void => {
    isEditing.value = false
    editingProduct.value = null
    formDialogVisible.value = true
  }

  /**
   * 💾 处理产品创建
   *
   * 创建新产品，包含完整的错误处理和用户反馈
   *
   * @param formData - 产品创建数据
   */
  const handleCreate = async (formData: ProductCreateRequest): Promise<void> => {
    const result = await executeAsyncOperation<Product>(
      async () => {
        // 调用存储的创建方法
        return await productStore.createProduct(formData)
      },
      '商品创建成功',                                    // 成功消息
      '商品创建失败，请重试'                              // 错误消息
    )

    // 如果创建成功
    if (result) {
      // 关闭对话框
      resetFormState()

      // 触发成功事件回调
      if (onCreateSuccess) {
        onCreateSuccess(result)
      }

      // 重新加载数据（由调用者负责）
      // loadProducts()
    }
  }

  // ==========================================
  // ✏️ 更新操作相关方法
  // ==========================================

  /**
   * 📖 打开编辑产品对话框
   *
   * 加载完整的产品数据并初始化编辑模式
   *
   * @param product - 要编辑的产品
   */
  const openEditDialog = async (product: Product): Promise<void> => {
    try {
      // 设置加载状态
      loading.value = true

      // 通过API获取完整的产品详情（包括规格数据）
      const fullProduct = await productStore.fetchProduct(product.id)

      // 设置编辑状态
      isEditing.value = true
      editingProduct.value = fullProduct
      formDialogVisible.value = true
    } catch (error) {
      handleOperationError(error, '获取商品详情失败，请重试')
    } finally {
      loading.value = false
    }
  }

  /**
   * 💾 处理产品更新
   *
   * 更新现有产品，包含完整的错误处理和用户反馈
   *
   * @param id - 产品ID
   * @param formData - 产品更新数据
   */
  const handleUpdate = async (id: number, formData: ProductUpdateRequest): Promise<void> => {
    const result = await executeAsyncOperation<Product>(
      async () => {
        // 调用存储的更新方法
        return await productStore.updateProduct(id, formData)
      },
      '商品更新成功',                                    // 成功消息
      '商品更新失败，请重试'                              // 错误消息
    )

    // 如果更新成功
    if (result) {
      // 关闭对话框
      resetFormState()

      // 触发成功事件回调
      if (onUpdateSuccess) {
        onUpdateSuccess(result)
      }

      // 重新加载数据（由调用者负责）
      // loadProducts()
    }
  }

  // ==========================================
  // 🗑️ 删除操作相关方法
  // ==========================================

  /**
   * ❓ 确认删除产品
   *
   * 显示确认对话框，用户确认后执行删除
   *
   * @param product - 要删除的产品
   */
  const confirmDelete = async (product: Product): Promise<void> => {
    try {
      // 显示确认对话框
      await ElMessageBox.confirm(
        `确定要删除商品 "${product.productName}" 吗？此操作不可恢复。`,
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )

      // 用户确认后，执行删除操作
      await handleDelete(product.id)
    } catch (error: any) {
      // 用户取消删除
      if (error !== 'cancel') {
        handleOperationError(error, '删除确认失败')
      } else {
        // 显示取消消息
        ElMessage.info('已取消删除')
      }
    }
  }

  /**
   * 🗑️ 处理产品删除
   *
   * 执行产品删除操作
   *
   * @param id - 产品ID
   */
  const handleDelete = async (id: number): Promise<void> => {
    const result = await executeAsyncOperation<boolean>(
      async () => {
        // 调用存储的删除方法
        await productStore.deleteProduct(id)
        return true // 表示删除成功
      },
      '商品删除成功',                                    // 成功消息
      '删除失败，请重试'                                  // 错误消息
    )

    // 如果删除成功
    if (result) {
      // 触发成功事件回调
      if (onDeleteSuccess) {
        onDeleteSuccess(id)
      }

      // 重新加载数据（由调用者负责）
      // loadProducts()
    }
  }

  // ==========================================
  // 🔄 状态切换相关方法
  // ==========================================

  /**
   * 🔄 切换产品上架/下架状态
   *
   * 显示确认对话框，用户确认后执行状态切换
   *
   * @param product - 要切换状态的产品
   */
  const toggleProductStatus = async (product: Product): Promise<void> => {
    try {
      // 计算新状态
      const newStatus = !product.isAvailable
      const action = newStatus ? '上架' : '下架'

      // 显示确认对话框
      await ElMessageBox.confirm(
        `确定要${action}商品 "${product.productName}" 吗？`,
        `${action}确认`,
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )

      // 执行状态切换
      const result = await executeAsyncOperation<Product>(
        async () => {
          // 调用存储的状态切换方法
          return await productStore.toggleProductStatus(product.id, newStatus)
        },
        `商品${action}成功`,                              // 成功消息
        `${action}失败，请重试`                            // 错误消息
      )

      // 如果状态切换成功
      if (result) {
        // 触发成功事件回调
        if (onStatusChangeSuccess) {
          onStatusChangeSuccess(result)
        }

        // 重新加载数据（由调用者负责）
        // loadProducts()
      }
    } catch (error: any) {
      // 用户取消操作
      if (error !== 'cancel') {
        handleOperationError(error, '状态切换失败')
      } else {
        // 显示取消消息
        ElMessage.info('已取消操作')
      }
    }
  }

  // ==========================================
  // 🎛️ 对话框管理方法
  // ==========================================

  /**
   * ❌ 关闭表单对话框
   *
   * 关闭对话框并重置相关状态
   */
  const closeFormDialog = (): void => {
    resetFormState()
  }

  // ==========================================
  // 📦 返回值 - 暴露所有功能
  // ==========================================

  return {
    // 状态
    loading,
    formDialogVisible,
    isEditing,
    editingProduct,

    // 创建操作
    openCreateDialog,
    handleCreate,

    // 更新操作
    openEditDialog,
    handleUpdate,

    // 删除操作
    confirmDelete,
    handleDelete,

    // 状态切换
    toggleProductStatus,

    // 对话框管理
    closeFormDialog,
    resetFormState
  }
}

/**
 * 💡 进阶思考：
 *
 * 1. 🎯 设计原则
 *    - 单一职责：每个函数只负责一个功能
 *    - 开闭原则：易于扩展新的CRUD操作
 *    - 依赖倒置：依赖抽象（事件回调）而不是具体实现
 *    - 接口隔离：提供清晰的最小接口
 *
 * 2. 🔧 技术亮点
 *    - 统一的异步操作封装
 *    - 完整的错误处理机制
 *    - 灵活的事件回调系统
 *    - TypeScript完整的类型支持
 *
 * 3. 🎓 学习价值
 *    - 展示了复杂业务逻辑的封装方法
 *    - 演示了异步操作的最佳实践
 *    - 体现了错误处理的重要性
 *    - 提供了可复用的CRUD解决方案
 *
 * 4. 🚀 扩展方向
 *    - 支持批量操作（批量删除、批量更新）
 *    - 添加操作历史记录
 *    - 支持撤销/重做功能
 *    - 集成权限控制
 */