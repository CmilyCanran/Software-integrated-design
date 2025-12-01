/**
 * 🔧 统一错误处理工具 - 初学者版本
 *
 * 💡 学习目标：
 * 1. 理解错误处理的重要性
 * 2. 学习如何封装可复用的工具函数
 * 3. 掌握TypeScript类型守卫的概念
 *
 * 📚 相关概念：
 * - 错误处理：捕获和处理程序运行中的异常情况
 * - 类型守卫：TypeScript中用于类型判断的函数
 * - 封装：将复杂逻辑包装成简单易用的函数
 */

import { ElMessage } from 'element-plus'
import { isAxiosError, type AppError, type ErrorHandlerOptions } from '@/types/error'

/**
 * 🔧 错误处理工具类
 *
 * 💡 设计思路：
 * 1. 统一所有错误处理逻辑，避免重复代码
 * 2. 提供清晰的错误分类和用户友好的消息
 * 3. 支持灵活的配置选项
 *
 * 🚀 使用示例：
 * ```typescript
 * // 基本使用
 * handleError(error)
 *
 * // 自定义选项
 * handleError(error, {
 *   showToast: false,
 *   customMessage: '登录失败，请检查用户名和密码'
 * })
 * ```
 */
export class ErrorHandler {
  // 默认配置
  private static defaultOptions: ErrorHandlerOptions = {
    showToast: true,      // 默认显示提示
    logError: true        // 默认记录日志
  }

  /**
   * 🎯 主要的错误处理函数
   *
   * @param error - 捕获到的错误对象
   * @param options - 处理选项
   * @returns 标准化的错误对象
   *
   * 🔍 处理流程：
   * 1. 标准化错误格式
   * 2. 显示用户提示（如需要）
   * 3. 记录错误日志（如需要）
   * 4. 返回标准化的错误对象
   */
  static handleError(error: unknown, options: ErrorHandlerOptions = {}): AppError {
    // 合并配置选项
    const opts = { ...this.defaultOptions, ...options }

    // 将各种错误类型标准化为AppError
    const appError = this.normalizeError(error)

    // 显示用户提示
    if (opts.showToast) {
      ElMessage.error(opts.customMessage || appError.message)
    }

    // 记录开发日志
    if (opts.logError) {
      console.error('❌ [应用程序错误]', appError)
    }

    return appError
  }

  /**
   * 🔧 错误标准化函数
   * 将不同类型的错误转换为统一的AppError格式
   */
  private static normalizeError(error: unknown): AppError {
    // 处理 Axios 错误
    if (isAxiosError(error)) {
      return this.handleAxiosError(error)
    }

    // 处理普通的 JavaScript Error
    if (error instanceof Error) {
      return {
        code: 'GENERAL_ERROR',              // 通用错误代码
        message: error.message,             // 错误消息
        timestamp: new Date().toISOString() // 时间戳
      }
    }

    // 处理其他未知类型的错误
    return {
      code: 'UNKNOWN_ERROR',
      message: '发生未知错误，请稍后重试',
      timestamp: new Date().toISOString()
    }
  }

  /**
   * 🔧 处理 Axios 特定的错误
   *
   * 💡 为什么需要特殊处理：
   * Axios错误包含HTTP状态码、响应数据等网络请求特有的信息
   * 需要根据这些信息提供更准确的错误消息
   */
  private static handleAxiosError(error: any): AppError {
    const status = error.response?.status    // HTTP状态码
    const data = error.response?.data as any  // 服务器返回的数据

    // 根据HTTP状态码分类处理
    switch (status) {
      case 401:
        return {
          code: 'UNAUTHORIZED',
          message: '认证失败，请重新登录',
          details: data,
          timestamp: new Date().toISOString()
        }

      case 403:
        return {
          code: 'FORBIDDEN',
          message: '权限不足，无法访问此资源',
          details: data,
          timestamp: new Date().toISOString()
        }

      case 404:
        return {
          code: 'NOT_FOUND',
          message: '请求的资源不存在',
          details: data,
          timestamp: new Date().toISOString()
        }

      case 500:
        return {
          code: 'SERVER_ERROR',
          message: '服务器内部错误，请稍后重试',
          details: data,
          timestamp: new Date().toISOString()
        }

      default:
        // 其他状态码的通用处理
        return {
          code: data?.code || 'REQUEST_ERROR',
          message: data?.message || error.message || '请求失败',
          details: data,
          timestamp: new Date().toISOString()
        }
    }
  }
}

/**
 * 🚀 便捷导出函数
 * 提供简洁的API，方便在组件中使用
 */
export const handleError = ErrorHandler.handleError.bind(ErrorHandler)