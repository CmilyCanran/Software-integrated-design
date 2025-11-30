// ============================================================================
// 通用类型定义 - 全项目共享
// ============================================================================

// 用户相关类型
export interface User {
  id: number
  username: string
  email: string
  role: 'USER' | 'SHOPER' | 'ADMIN'
  createdAt: string
  updatedAt: string
}

// API响应类型
export interface ApiResponse<T = any> {
  success: boolean
  data: T
  message: string
}

// 登录请求类型
export interface LoginRequest {
  username: string
  password: string
}

// 登录响应类型
export interface LoginResponse {
  token: string
  user: User
  expiresIn: number
}

// 注册请求类型
export interface RegisterRequest {
  username: string
  email: string
  password: string
  confirmPassword: string
}

// ============================================================================
// 分页相关类型 - 统一的分页结构
// ============================================================================

// 分页查询参数类型
export interface PaginationParams {
  page?: number
  size?: number
  keyword?: string
  sort?: string
  order?: 'asc' | 'desc'
}

// 统一的分页响应类型 - 与后端Spring Boot Page响应格式对齐
export interface PaginatedResponse<T> {
  data: T[]           // 数据列表
  total: number         // 总记录数
  page: number          // 当前页码
  size: number          // 每页大小
  totalPages: number    // 总页数
  hasNext: boolean      // 是否有下一页
  hasPrev: boolean      // 是否有上一页
}

// 分页统计信息
export interface PaginationStats {
  currentPage: number
  pageSize: number
  totalItems: number
  totalPages: number
  hasNextPage: boolean
  hasPreviousPage: boolean
}

// ============================================================================
// 表单和验证相关类型
// ============================================================================

// 表单验证规则类型
export interface FormRule {
  required?: boolean
  message?: string
  trigger?: string | string[]
  min?: number
  max?: number
  pattern?: RegExp
  validator?: (rule: any, value: any, callback: any) => void
  type?: string
}

// 表单字段验证配置
export interface FieldValidation {
  required?: boolean
  message?: string
  trigger?: 'blur' | 'change' | 'submit'
  min?: number
  max?: number
  type?: string
  validator?: (rule: any, value: any, callback: any) => void
}

// ============================================================================
// 通用业务类型
// ============================================================================

// 用户角色枚举
export enum UserRole {
  USER = 'USER',
  SHOPER = 'SHOPER',
  ADMIN = 'ADMIN'
}

// 操作状态枚举
export enum OperationStatus {
  PENDING = 'pending',
  SUCCESS = 'success',
  FAILED = 'failed',
  CANCELLED = 'cancelled'
}

// 通用筛选条件
export interface FilterCondition {
  keyword?: string
  dateRange?: [string, string]
  status?: string | string[]
  min?: number
  max?: number
}

// 排序配置
export interface SortConfig {
  field: string
  order: 'asc' | 'desc'
  label: string
}

// ============================================================================
// 导航和UI相关类型
// ============================================================================

// 菜单项类型
export interface MenuItem {
  path: string
  name: string
  icon?: string
  children?: MenuItem[]
  meta?: {
    title: string
    requiresAuth?: boolean
    roles?: string[]
  }
}

// 路由元信息
export interface RouteMeta {
  title: string
  requiresAuth?: boolean
  roles?: string[]
  icon?: string
  hidden?: boolean
  activeMenu?: string
}

// 面包状态类型
export interface LoadingState {
  loading: boolean
  message?: string
}

// ============================================================================
// 错误处理类型
// ============================================================================

// API错误类型
export interface ApiError {
  code: number | string
  message: string
  details?: any
  timestamp?: string
}

// 表单错误类型
export interface FormErrors {
  [field: string]: string | string[]
}

// ============================================================================
// 时间和日期类型
// ============================================================================

// 日期范围类型
export interface DateRange {
  start: string
  end: string
}

// 时间戳类型
export type Timestamp = string | number

// ============================================================================
// 工具类型
// ============================================================================

// 键值对类型
export type KeyValuePair<T = any> = {
  [key: string]: T
}

// 标签类型
export interface Tag {
  id: string | number
  name: string
  color?: string
  type?: string
}

// ID类型
export type ID = string | number

// 可选类型工具
export type Optional<T> = T | null | undefined