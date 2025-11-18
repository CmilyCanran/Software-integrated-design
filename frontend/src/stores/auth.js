// ============================================================================
// 认证状态管理 Store - 管理用户登录状态、信息和认证逻辑
// ============================================================================

// 🔥 依赖导入：Pinia状态管理和Vue响应式API
import { defineStore } from 'pinia'                        // 导入Pinia状态定义函数
import { ref, computed } from 'vue'                        // 导入Vue响应式API：ref和computed

// ============================================================================
// 认证Store定义：使用Composition API模式
// ============================================================================
export const useAuthStore = defineStore('auth', () => {
  // ============================================================================
  // 🔥 状态定义：响应式数据存储
  // ============================================================================
  const token = ref(localStorage.getItem('token') || '')    // 🔐 JWT认证令牌，从localStorage读取，无默认值
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))  // 👤 用户信息对象，包含用户详情
  const loading = ref(false)                                // ⏳ 加载状态，用于显示加载动画或禁用操作

  // ============================================================================
  // 🔥 计算属性：基于状态的派生数据
  // ============================================================================
  const isLoggedIn = computed(() => !!token.value)          // 🔐 登录状态：检查token是否存在（双感叹号转为布尔值）
  const isAdmin = computed(() => userInfo.value.role === 'admin')  // 👑 管理员状态：检查用户角色是否为admin
  const username = computed(() => userInfo.value.username || '')  // 📝 用户名：从用户信息中获取，空字符串作为默认值
  const userId = computed(() => userInfo.value.id || null)     // 🆔 用户ID：从用户信息中获取，null作为默认值

  // ============================================================================
  // 🔥 方法定义：用户认证相关的操作函数
  // ============================================================================

  /**
   * 用户登录方法
   * @param {Object} loginData - 登录响应数据
   * @param {string} loginData.token - JWT认证令牌
   * @param {Object} loginData.user - 用户信息对象
   * @description 更新认证状态并持久化到localStorage
   */
  const login = (loginData) => {
    // 更新响应式状态
    token.value = loginData.token                          // 设置JWT令牌到响应式状态
    userInfo.value = loginData.user                        // 设置用户信息到响应式状态

    // 🔥 持久化存储：保存到localStorage实现页面刷新后状态保持
    localStorage.setItem('token', loginData.token)         // 保存令牌到浏览器本地存储
    localStorage.setItem('userInfo', JSON.stringify(loginData.user))  // 保存用户信息（JSON字符串格式）
  }

  /**
   * 用户登出方法
   * @description 清除所有认证状态和本地存储数据
   */
  const logout = () => {
    // 清空响应式状态
    token.value = ''                                       // 清空JWT令牌
    userInfo.value = {}                                    // 重置用户信息为空对象

    // 🔥 清除持久化数据：从localStorage移除认证信息
    localStorage.removeItem('token')                       // 移除令牌
    localStorage.removeItem('userInfo')                    // 移除用户信息
  }

  /**
   * 更新用户信息方法
   * @param {Object} newUserInfo - 新的用户信息数据
   * @description 合并更新用户信息，支持部分更新
   */
  const updateUserInfo = (newUserInfo) => {
    // 使用对象展开运算符合并新旧用户信息
    userInfo.value = { ...userInfo.value, ...newUserInfo }  // 保留原有属性，覆盖新属性
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))  // 同步更新本地存储
  }

  /**
   * 设置加载状态方法
   * @param {boolean} status - 加载状态值
   * @description 控制全局加载状态，用于UI反馈
   */
  const setLoading = (status) => {
    loading.value = status                                 // 更新加载状态
  }

  // ============================================================================
  // 🔥 Store导出：暴露状态、计算属性和方法
  // ============================================================================
  return {
    // ┌─ 响应式状态
    token,                                                // 🔐 JWT认证令牌
    userInfo,                                             // 👤 用户信息对象
    loading,                                              // ⏳ 加载状态

    // ┌─ 计算属性
    isLoggedIn,                                           // 🔐 登录状态（布尔值）
    isAdmin,                                              // 👑 管理员状态（布尔值）
    username,                                             // 📝 用户名（字符串）
    userId,                                               // 🆔 用户ID（数字或字符串）

    // ┌─ 操作方法
    login,                                                // 🔑 登录方法
    logout,                                               // 🚪 登出方法
    updateUserInfo,                                       // 📝 更新用户信息方法
    setLoading                                            // ⏳ 设置加载状态方法
  }
})
