// ============================================================================
// 认证状态管理 Store (TypeScript版本) - 管理用户登录状态、信息和认证逻辑
// ============================================================================

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, LoginResponse } from '@/types'

// ============================================================================
// 认证Store定义：使用Composition API模式 + TypeScript
// ============================================================================
export const useAuthStore = defineStore('auth', () => {
  // ============================================================================
  // 🔥 状态定义：强类型响应式数据存储
  // ============================================================================
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<User>(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const loading = ref<boolean>(false)

  // ============================================================================
  // 🔥 计算属性：基于状态的派生数据
  // ============================================================================
  const isLoggedIn = computed<boolean>(() => !!token.value)
  const isAdmin = computed<boolean>(() => userInfo.value.role === 'ADMIN')
  const isShoper = computed<boolean>(() => userInfo.value.role === 'SHOPER')
  const username = computed<string>(() => userInfo.value.username || '')
  const userId = computed<number | null>(() => userInfo.value.id || null)

  // ============================================================================
  // 🔥 方法定义：用户认证相关的操作函数
  // ============================================================================

  /**
   * 用户登录方法
   * @param loginData - 登录响应数据
   * @description 更新认证状态并持久化到localStorage
   */
  const login = (loginData: LoginResponse): void => {
    // 更新响应式状态
    token.value = loginData.token
    userInfo.value = loginData.user

    // 持久化存储：保存到localStorage
    localStorage.setItem('token', loginData.token)
    localStorage.setItem('userInfo', JSON.stringify(loginData.user))
  }

  /**
   * 用户登出方法
   * @description 清除所有认证状态和本地存储数据
   */
  const logout = (): void => {
    // 清空响应式状态
    token.value = ''
    userInfo.value = {} as User

    // 清除持久化数据
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  /**
   * 更新用户信息方法
   * @param newUserInfo - 新的用户信息数据
   * @description 合并更新用户信息，支持部分更新
   */
  const updateUserInfo = (newUserInfo: Partial<User>): void => {
    userInfo.value = { ...userInfo.value, ...newUserInfo }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  /**
   * 设置加载状态方法
   * @param status - 加载状态值
   * @description 控制全局加载状态，用于UI反馈
   */
  const setLoading = (status: boolean): void => {
    loading.value = status
  }

  /**
   * 检查用户权限
   * @param requiredRole - 需要的角色
   * @description 检查用户是否具有指定角色
   */
  const hasRole = (requiredRole: 'USER' | 'SHOPER' | 'ADMIN'): boolean => {
    return userInfo.value.role === requiredRole
  }

  /**
   * 检查用户是否可以管理商品
   * @description 商家和管理员可以管理商品
   */
  const canManageProducts = computed<boolean>(() => {
    return isAdmin.value || isShoper.value
  })

  // ============================================================================
  // 🔥 Store导出：暴露状态、计算属性和方法
  // ============================================================================
  return {
    // 响应式状态
    token,
    userInfo,
    loading,

    // 计算属性
    isLoggedIn,
    isAdmin,
    isShoper,
    username,
    userId,
    canManageProducts,

    // 操作方法
    login,
    logout,
    updateUserInfo,
    setLoading,
    hasRole
  }
})