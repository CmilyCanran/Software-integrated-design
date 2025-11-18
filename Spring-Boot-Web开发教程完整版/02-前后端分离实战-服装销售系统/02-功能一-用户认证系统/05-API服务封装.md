# 第5章：API服务封装

> **学习目标**：封装HTTP请求服务，实现前后端数据交互

## 🎯 本章概览

| 内容 | 预计时间 | 难度 | 状态 |
|------|----------|------|------|
| Axios基础配置 | 10分钟 | ⭐⭐ | ⏳ |
| 请求拦截器 | 10分钟 | ⭐⭐ | ⏳ |
| 响应拦截器 | 10分钟 | ⭐⭐ | ⏳ |

---

## 📡 创建API服务

### 创建 `src/api/auth.js`

```javascript
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

// 认证API
export const authAPI = {
  // 用户登录
  login(credentials) {
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
                role: 'admin'
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
                role: 'user'
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
```

---

## 📝 本章小结

### ✅ 掌握技能
- [x] Axios配置
- [x] 拦截器实现
- [x] 模拟API开发

### 🚀 下一步
API服务完成，下一章我们将开发登录页面界面。

---

**下一章：[06-登录页面开发](06-登录页面开发.md)**