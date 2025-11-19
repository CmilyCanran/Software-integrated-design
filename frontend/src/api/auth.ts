// ============================================================================
// 认证相关API接口封装 (TypeScript版本) - 处理用户登录、注册等认证操作
// ============================================================================

import { api } from './request'
import type { LoginRequest, RegisterRequest, LoginResponse, User } from '@/types'

// ============================================================================
// 认证API对象：封装所有认证相关的接口调用
// ============================================================================
export const authAPI = {
  /**
   * 用户登录接口
   * @param data - 登录凭据数据
   * @returns 返回包含token和用户信息的Promise
   */
  login(data: LoginRequest): Promise<LoginResponse> {
    return api.post('/auth/login', data)
  },

  /**
   * 用户注册接口
   * @param data - 用户注册数据
   * @returns 返回注册结果的Promise
   */
  register(data: RegisterRequest): Promise<User> {
    return api.post('/auth/register', data)
  },

  /**
   * 获取当前用户信息接口
   * @returns 返回用户详细信息的Promise
   */
  getUserInfo(): Promise<User> {
    return api.get('/auth/userinfo')
  },

  /**
   * 用户退出登录接口
   * @returns 返回退出操作结果的Promise
   */
  logout(): Promise<void> {
    return api.post('/auth/logout')
  }
}

// ============================================================================
// 导出类型定义，方便其他模块使用
// ============================================================================
export type { LoginRequest, RegisterRequest, LoginResponse, User }