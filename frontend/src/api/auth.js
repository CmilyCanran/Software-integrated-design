import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 添加token到请求头
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      window.location.href = '/login'
      ElMessage.error('登录已过期，请重新登录')
    } else if (error.response?.status >= 500) {
      ElMessage.error('服务器错误，请稍后重试')
    } else if (error.response?.status === 400) {
      ElMessage.error(error.response.data.message || '请求参数错误')
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

// 认证相关API
export const authAPI = {
  // 用户登录
  login(credentials) {
    // 模拟登录API
    return new Promise((resolve) => {
      setTimeout(() => {
        if (credentials.username === 'admin' && credentials.password === '123456') {
          resolve({
            data: {
              token: 'mock-admin-token-' + Date.now(),
              user: {
                id: 1,
                username: 'admin',
                email: 'admin@example.com',
                role: 'admin',
                avatar: 'https://via.placeholder.com/100x100?text=Admin'
              }
            }
          })
        } else if (credentials.username === 'user' && credentials.password === '123456') {
          resolve({
            data: {
              token: 'mock-user-token-' + Date.now(),
              user: {
                id: 2,
                username: 'user',
                email: 'user@example.com',
                role: 'user',
                avatar: 'https://via.placeholder.com/100x100?text=User'
              }
            }
          })
        } else {
          const error = new Error('用户名或密码错误')
          error.response = { status: 400, data: { message: '用户名或密码错误' } }
          throw error
        }
      }, 1000)
    })
  },

  // 用户注册
  register(userData) {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          data: {
            message: '注册成功',
            user: {
              id: Date.now(),
              username: userData.username,
              email: userData.email,
              role: 'user'
            }
          }
        })
      }, 1000)
    })
  }
}

export default api