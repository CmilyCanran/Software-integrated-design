// ============================================================================
// 异步操作工具函数 - 统一处理loading状态和错误处理
// ============================================================================

import type { Ref } from 'vue'

/**
 * 异步操作执行器配置
 */
export interface AsyncOperationConfig {
  loading: Ref<boolean>
  error: Ref<string | null>
}

/**
 * 异步操作执行器 - 统一处理loading状态和错误处理
 * @param config - 包含loading和error状态的配置对象
 * @param operation - 要执行的异步操作
 * @param errorMessage - 错误消息
 * @returns 操作结果或null
 * @description 消除重复的loading/error处理代码
 */
export const executeAsyncOperation = async <T>(
  config: AsyncOperationConfig,
  operation: () => Promise<T>,
  errorMessage: string
): Promise<T | null> => {
  config.loading.value = true
  config.error.value = null
  try {
    return await operation()
  } catch (err: any) {
    config.error.value = err.message || errorMessage
    console.error(`❌ ${errorMessage}:`, err)
    return null
  } finally {
    config.loading.value = false
  }
}

/**
 * 细粒度异步操作执行器 - 支持特定操作的加载状态
 * @param loadingStates - 加载状态对象
 * @param operationKey - 操作类型键
 * @param operation - 要执行的异步操作
 * @param errorMessage - 错误消息
 * @returns 操作结果或null
 */
export const executeGranularAsyncOperation = async <T>(
  loadingStates: Record<string, boolean>,
  operationKey: string,
  operation: () => Promise<T>,
  errorMessage: string
): Promise<T | null> => {
  loadingStates[operationKey] = true
  try {
    return await operation()
  } catch (err: any) {
    console.error(`❌ ${errorMessage}:`, err)
    return null
  } finally {
    loadingStates[operationKey] = false
  }
}