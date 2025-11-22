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

// 分页查询类型
export interface PaginationParams {
  page: number
  size: number
  keyword?: string
}

// 分页响应类型
export interface PaginatedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

// 商品筛选类型
export interface ProductFilters {
  keyword?: string
  isAvailable?: boolean
  minPrice?: number
  maxPrice?: number
}

// 表单验证规则类型
export interface FormRule {
  required?: boolean
  message?: string
  trigger?: string | string[]
  min?: number
  max?: number
  pattern?: RegExp
  validator?: (rule: any, value: any, callback: any) => void
}

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