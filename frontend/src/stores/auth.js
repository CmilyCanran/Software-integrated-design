import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const loading = ref(false)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value.role === 'admin')
  const username = computed(() => userInfo.value.username || '')
  const userId = computed(() => userInfo.value.id || null)

  // 方法
  const login = (loginData) => {
    token.value = loginData.token
    userInfo.value = loginData.user

    // 保存到localStorage
    localStorage.setItem('token', loginData.token)
    localStorage.setItem('userInfo', JSON.stringify(loginData.user))
  }

  const logout = () => {
    token.value = ''
    userInfo.value = {}

    // 清除localStorage
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  const updateUserInfo = (newUserInfo) => {
    userInfo.value = { ...userInfo.value, ...newUserInfo }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  const setLoading = (status) => {
    loading.value = status
  }

  return {
    // 状态
    token,
    userInfo,
    loading,

    // 计算属性
    isLoggedIn,
    isAdmin,
    username,
    userId,

    // 方法
    login,
    logout,
    updateUserInfo,
    setLoading
  }
})
