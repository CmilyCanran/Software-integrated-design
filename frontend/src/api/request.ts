// ============================================================================
// HTTP请求封装文件 (TypeScript版本) - 基于axios的统一API请求处理
// ============================================================================

import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import type { ApiResponse } from '@/types'

// ============================================================================
// Axios实例创建：配置默认请求参数
// ============================================================================
const request: AxiosInstance = axios.create({
  baseURL: 'http://localhost:8080/api', // 后端API基础地址
  timeout: 10000,                      // 请求超时时间：10秒
  headers: {
    'Content-Type': 'application/json'
  }
})

// ============================================================================
// 请求拦截器：在发送请求前统一处理
// ============================================================================
request.interceptors.request.use(
  // 请求成功拦截
  (config: InternalAxiosRequestConfig) => {
    const authStore = useAuthStore()

    // 自动添加认证头
    if (authStore.token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${authStore.token}`
    }

    // 开发环境日志
    console.log(`🚀 发送请求: ${config.method?.toUpperCase()} ${config.url}`)

    return config
  },
  // 请求失败拦截
  (error) => {
    console.error('❌ 请求配置错误:', error)
    return Promise.reject(error)
  }
)

// ============================================================================
// 响应拦截器：统一处理API响应和错误
// ============================================================================
request.interceptors.response.use(
  // 响应成功拦截
  (response: AxiosResponse<ApiResponse>) => {
    const responseData = response.data

    // 处理统一格式 (success 字段)
    if (responseData.success) {
      console.log(`✅ 请求成功: ${response.config.url}`)
      return responseData.data
    } else {
      ElMessage.error(responseData.message || '请求失败')
      return Promise.reject(new Error(responseData.message))
    }
  },
  // 响应失败拦截
  (error) => {
    // HTTP状态码处理
    if (error.response?.status === 401) {
      console.warn('⚠️ 认证失效，需要重新登录')
      const authStore = useAuthStore()
      authStore.logout()
      window.location.href = '/login'
    } else if (error.response?.status === 403) {
      ElMessage.error('权限不足，无法访问此资源')
    } else if (error.response?.status === 404) {
      ElMessage.error('请求的资源不存在')
    } else if (error.response?.status >= 500) {
      ElMessage.error('服务器内部错误，请稍后重试')
    } else {
      ElMessage.error(error.message || '网络错误')
    }

    // 错误日志记录
    console.error('❌ 请求失败:', {
      url: error.config?.url,
      method: error.config?.method,
      status: error.response?.status,
      message: error.message
    })

    return Promise.reject(error)
  }
)

// ============================================================================
// 导出配置好的axios实例和类型
// ============================================================================
export default request

// 导出常用请求方法，提供更好的类型推断
export const api = {
  get: <T = any>(url: string, config?: AxiosRequestConfig): Promise<T> => {
    return request.get(url, config)
  },

  post: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> => {
    return request.post(url, data, config)
  },

  put: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> => {
    return request.put(url, data, config)
  },

  delete: <T = any>(url: string, config?: AxiosRequestConfig): Promise<T> => {
    return request.delete(url, config)
  }
}