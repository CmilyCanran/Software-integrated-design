import request from './request'
import type { ApiResponse } from './types'

/**
 * 用户查询请求参数接口
 */
export interface UserQueryRequest {
  username?: string
  email?: string
  role?: string
  enabled?: boolean
  page?: number
  size?: number
  sortBy?: string
  sortDirection?: string
}

/**
 * 用户管理项接口
 */
export interface UserManagementItem {
  id: number
  username: string
  email: string
  role: string
  enabled: boolean
  createdAt: string
  updatedAt: string
}

/**
 * 用户统计信息接口
 */
export interface UserStatistics {
  totalUsers: number
  enabledUsers: number
  disabledUsers: number
  userCount: number
  shoperCount: number
  adminCount: number
}

/**
 * 分页数据接口
 */
export interface PageData<T> {
  content: T[]
  pageable: {
    pageNumber: number
    pageSize: number
    sort: {
      empty: boolean
      sorted: boolean
      unsorted: boolean
    }
    offset: number
    paged: boolean
    unpaged: boolean
  }
  totalPages: number
  totalElements: number
  last: boolean
  size: number
  number: number
  sort: {
    empty: boolean
    sorted: boolean
    unsorted: boolean
  }
  numberOfElements: number
  first: boolean
  empty: boolean
}

/**
 * 管理员相关API
 */
export const adminApi = {
  /**
   * 获取用户列表（分页）
   * @param query 查询参数
   * @returns 分页的用户数据
   */
  getUserList: (query: UserQueryRequest) =>
    request.get<ApiResponse<PageData<UserManagementItem>>>('/admin/users/list', { params: query }),

  /**
   * 获取用户统计信息
   * @returns 用户统计信息
   */
  getUserStatistics: () =>
    request.get<ApiResponse<UserStatistics>>('/admin/users/statistics'),

  /**
   * 切换用户启用状态
   * @param userId 用户ID
   * @returns 操作结果
   */
  toggleUserEnabled: (userId: number) =>
    request.put<ApiResponse<string>>(`/admin/users/${userId}/toggle-enabled`),

  /**
   * 更新用户角色
   * @param userId 用户ID
   * @param role 新角色
   * @returns 操作结果
   */
  updateUserRole: (userId: number, role: string) =>
    request.put<ApiResponse<string>>(`/admin/users/${userId}/role`, role)
}