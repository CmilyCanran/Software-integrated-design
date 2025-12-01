/**
 * 🎯 错误类型定义模块
 *
 * 💡 学习目标：
 * 1. 学习TypeScript接口设计的最佳实践
 * 2. 理解错误分类和层次结构
 * 3. 掌握类型守卫的编写技巧
 *
 * 📚 相关概念：
 * - 接口继承：TypeScript中接口可以继承其他接口
 * - 类型守卫：运行时类型检查函数
 * - 错误层次：不同类型的错误形成层次结构
 */

import type { AxiosError } from 'axios'

/**
 * 🎯 错误处理选项接口
 *
 * 定义错误处理工具的配置选项
 */
export interface ErrorHandlerOptions {
  showToast?: boolean        // 是否显示提示消息
  logError?: boolean         // 是否记录到控制台
  customMessage?: string     // 自定义错误消息
}

/**
 * 📋 基础错误接口
 *
 * 所有错误类型的基础接口，定义了错误的基本结构
 *
 * 💡 设计思路：
 * - code: 错误代码，用于程序识别和处理
 * - message: 用户友好的错误消息
 * - details: 详细的错误信息（调试用）
 * - timestamp: 错误发生时间，便于调试和日志分析
 */
export interface AppError {
  code: string | number      // 错误代码，用于分类处理
  message: string            // 用户友好的错误消息
  details?: any              // 详细的错误信息（调试用）
  timestamp?: string         // 错误发生时间（ISO格式）
}

/**
 * 🔌 API错误接口
 *
 * 扩展基础错误，添加HTTP请求相关的错误信息
 *
 * 💡 为什么需要这个接口：
 * - API错误通常包含HTTP状态码
 * - 可能有服务器返回的响应数据
 * - 需要与普通的应用程序错误区分开来
 */
export interface ApiError extends AppError {
  status?: number            // HTTP状态码
  response?: any              // 服务器响应数据
  url?: string               // 请求的URL
  method?: string            // 请求方法
}

/**
 * 📝 表单错误接口
 *
 * 专门处理表单验证错误
 *
 * 💡 特点：
 * - 与具体的表单字段关联
 * - 支持多个错误消息
 * - 便于在前端表单中显示
 */
export interface FormError {
  field: string              // 出错的字段名
  message: string            // 错误消息
  code?: string              // 错误代码（可选）
}

/**
 * 📊 表单错误集合
 *
 * 一个表单可能有多个字段出错
 * 使用对象格式，便于按字段查找错误
 */
export interface FormErrors {
  [field: string]: string | string[]  // 字段名 -> 错误消息（单个或多个）
}

/**
 * ⚡ 验证错误接口
 *
 * 后端返回的验证错误格式
 */
export interface ValidationError extends ApiError {
  fieldErrors?: FormErrors     // 字段级别的错误
  globalErrors?: string[]      // 全局错误消息
}

/**
 * 🔐 认证错误接口
 *
 * 专门处理认证相关的错误
 */
export interface AuthError extends ApiError {
  authErrorType: 'INVALID_CREDENTIALS' | 'TOKEN_EXPIRED' | 'INSUFFICIENT_PERMISSIONS' | 'ACCOUNT_LOCKED'
  requiresLogin?: boolean      // 是否需要重新登录
  loginUrl?: string            // 登录页面URL
}

/**
 * 🌐 网络错误接口
 *
 * 处理网络连接相关的错误
 */
export interface NetworkError extends AppError {
  networkErrorType: 'TIMEOUT' | 'OFFLINE' | 'DNS_ERROR' | 'CONNECTION_REFUSED'
  retryable?: boolean          // 是否可以重试
  retryAfter?: number          // 多久后可以重试（毫秒）
}

/**
 * 🗄️ 业务逻辑错误接口
 *
 * 处理业务规则违反的错误
 */
export interface BusinessError extends AppError {
  businessErrorType: 'VALIDATION_FAILED' | 'INSUFFICIENT_BALANCE' | 'RESOURCE_NOT_FOUND' | 'OPERATION_NOT_ALLOWED'
  context?: Record<string, any>  // 业务上下文信息
}

/**
 * 🔍 Axios错误类型守卫
 *
 * 💡 类型守卫的作用：
 * - 在运行时判断一个值是否为特定类型
 * - 帮助TypeScript编译器理解代码中的类型
 * - 提供类型安全的错误处理
 *
 * 🔧 实现原理：
 * - 检查对象是否存在AxiosError特有的属性
 * - 如果存在，则确定这是Axios错误
 *
 * @param error - 要检查的错误对象
 * @returns 是否为Axios错误
 */
export const isAxiosError = (error: unknown): error is AxiosError => {
  return error !== null &&
         typeof error === 'object' &&
         'response' in error &&     // Axios错误特有的属性
         'config' in error &&       // Axios错误特有的属性
         'isAxiosError' in error    // Axios错误特有的标识
}

/**
 * 🎯 应用程序错误类型守卫
 *
 * 判断一个值是否符合AppError接口
 */
export const isAppError = (error: unknown): error is AppError => {
  return error !== null &&
         typeof error === 'object' &&
         'code' in error &&         // AppError必需的属性
         'message' in error          // AppError必需的属性
}

/**
 * 🔌 API错误类型守卫
 *
 * 判断是否为API错误（包含HTTP状态码）
 */
export const isApiError = (error: unknown): error is ApiError => {
  return isAppError(error) && (
    'status' in error ||         // 包含HTTP状态码
    'response' in error          // 或包含响应数据
  )
}

/**
 * 📝 表单错误类型守卫
 *
 * 判断是否为表单验证错误
 */
export const isFormError = (error: unknown): error is FormError => {
  return error !== null &&
         typeof error === 'object' &&
         'field' in error &&        // 必需有字段名
         'message' in error          // 必需有错误消息
}

/**
 * ⚡ 验证错误类型守卫
 *
 * 判断是否为后端返回的验证错误
 */
export const isValidationError = (error: unknown): error is ValidationError => {
  return isApiError(error) && (
    'fieldErrors' in error ||      // 包含字段错误
    'globalErrors' in error        // 或包含全局错误
  )
}

/**
 * 🔐 认证错误类型守卫
 *
 * 判断是否为认证相关的错误
 */
export const isAuthError = (error: unknown): error is AuthError => {
  return isApiError(error) &&
         'authErrorType' in error   // 包含认证错误类型
}

/**
 * 🌐 网络错误类型守卫
 *
 * 判断是否为网络连接错误
 */
export const isNetworkError = (error: unknown): error is NetworkError => {
  return isAppError(error) &&
         'networkErrorType' in error  // 包含网络错误类型
}

/**
 * 🗄️ 业务逻辑错误类型守卫
 *
 * 判断是否为业务逻辑错误
 */
export const isBusinessError = (error: unknown): error is BusinessError => {
  return isAppError(error) &&
         'businessErrorType' in error  // 包含业务错误类型
}

/**
 * 🎯 错误代码常量
 *
 * 统一定义错误代码，避免硬编码字符串
 */
export const ErrorCodes = {
  // 通用错误
  UNKNOWN_ERROR: 'UNKNOWN_ERROR',
  GENERAL_ERROR: 'GENERAL_ERROR',
  VALIDATION_ERROR: 'VALIDATION_ERROR',

  // HTTP状态码相关
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  METHOD_NOT_ALLOWED: 405,
  CONFLICT: 409,
  UNPROCESSABLE_ENTITY: 422,
  TOO_MANY_REQUESTS: 429,
  INTERNAL_SERVER_ERROR: 500,
  BAD_GATEWAY: 502,
  SERVICE_UNAVAILABLE: 503,
  GATEWAY_TIMEOUT: 504,

  // 网络相关
  NETWORK_ERROR: 'NETWORK_ERROR',
  TIMEOUT_ERROR: 'TIMEOUT_ERROR',
  OFFLINE_ERROR: 'OFFLINE_ERROR',

  // 业务相关
  INSUFFICIENT_BALANCE: 'INSUFFICIENT_BALANCE',
  RESOURCE_NOT_FOUND: 'RESOURCE_NOT_FOUND',
  OPERATION_NOT_ALLOWED: 'OPERATION_NOT_ALLOWED',
  ACCOUNT_LOCKED: 'ACCOUNT_LOCKED',
  INVALID_CREDENTIALS: 'INVALID_CREDENTIALS',
  TOKEN_EXPIRED: 'TOKEN_EXPIRED',
  INSUFFICIENT_PERMISSIONS: 'INSUFFICIENT_PERMISSIONS'
} as const

/**
 * 📋 用户友好的错误消息映射
 *
 * 将技术性的错误代码转换为普通用户能理解的错误消息
 */
export const ErrorMessages: Record<string, string> = {
  // 通用错误
  [ErrorCodes.UNKNOWN_ERROR]: '发生未知错误，请稍后重试',
  [ErrorCodes.GENERAL_ERROR]: '操作失败，请重试',
  [ErrorCodes.VALIDATION_ERROR]: '输入数据验证失败',

  // HTTP错误
  [ErrorCodes.UNAUTHORIZED]: '认证失败，请重新登录',
  [ErrorCodes.FORBIDDEN]: '权限不足，无法访问此资源',
  [ErrorCodes.NOT_FOUND]: '请求的资源不存在',
  [ErrorCodes.INTERNAL_SERVER_ERROR]: '服务器内部错误，请稍后重试',
  [ErrorCodes.SERVICE_UNAVAILABLE]: '服务暂时不可用，请稍后重试',
  [ErrorCodes.TOO_MANY_REQUESTS]: '请求过于频繁，请稍后再试',

  // 网络错误
  [ErrorCodes.NETWORK_ERROR]: '网络连接失败，请检查网络',
  [ErrorCodes.TIMEOUT_ERROR]: '请求超时，请稍后重试',
  [ErrorCodes.OFFLINE_ERROR]: '网络连接已断开',

  // 业务错误
  [ErrorCodes.INSUFFICIENT_BALANCE]: '余额不足',
  [ErrorCodes.RESOURCE_NOT_FOUND]: '找不到指定的资源',
  [ErrorCodes.OPERATION_NOT_ALLOWED]: '不允许执行此操作',
  [ErrorCodes.ACCOUNT_LOCKED]: '账户已被锁定',
  [ErrorCodes.INVALID_CREDENTIALS]: '用户名或密码错误',
  [ErrorCodes.TOKEN_EXPIRED]: '登录已过期，请重新登录',
  [ErrorCodes.INSUFFICIENT_PERMISSIONS]: '权限不足'
}

/**
 * 💡 进阶思考：
 *
 * 1. 🎯 设计原则
 *    - 接口隔离：每个接口只定义必要的属性
 *    - 开闭原则：易于扩展新的错误类型
 *    - 单一职责：每种错误类型有明确的责任
 *
 * 2. 🔧 技术亮点
 *    - 使用TypeScript的接口继承机制
 *    - 提供完整的类型守卫函数
 *    - 统一定义错误代码和消息
 *    - 支持运行时类型检查
 *
 * 3. 🎓 学习价值
 *    - 展示了TypeScript类型系统的高级用法
 *    - 演示了错误处理的最佳实践
 *    - 体现了良好的API设计原则
 *    - 提供了完整的错误处理解决方案
 *
 */