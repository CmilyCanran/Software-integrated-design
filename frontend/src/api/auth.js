import request from './request'

export const authAPI = {
  // 登录
  login(data) {
    return request({
      url: '/auth/login',
      method: 'post',
      data
    })
  },

  // 注册
  register(data) {
    return request({
      url: '/auth/register',
      method: 'post',
      data
    })
  },

  // 获取用户信息
  getUserInfo() {
    return request({
      url: '/auth/userinfo',
      method: 'get'
    })
  },

  // 退出登录
  logout() {
    return request({
      url: '/auth/logout',
      method: 'post'
    })
  }
}
