// ============================================================================
// Vue Router 路由配置文件 - 负责页面导航和权限控制
// ============================================================================

// 🔥 依赖导入：Vue Router核心功能
import { createRouter, createWebHistory } from 'vue-router'  // 导入路由创建函数和历史模式
import { useAuthStore } from '@/stores/auth'                  // 导入认证状态管理，用于权限检查

// ============================================================================
// 路由实例创建：配置路由规则和历史模式
// ============================================================================
const router = createRouter({
  // 🔥 路由历史模式配置
  history: createWebHistory(import.meta.env.BASE_URL),        // 使用HTML5 History API，BASE_URL来自环境变量

  // 🔥 路由规则配置数组
  routes: [
    // ┌─ 根路径重定向路由
    {
      path: '/',                                           // 根路径，用户访问网站首页
      redirect: '/dashboard'                               // 自动重定向到仪表板页面
    },

    // ┌─ 仪表板路由（需要认证）
    {
      path: '/dashboard',                                  // 仪表板页面路径
      name: 'dashboard',                                   // 路由名称，用于编程式导航
      component: () => import('../views/Dashboard.vue'),    // 懒加载仪表板组件，提升初始加载性能
      meta: {
        requiresAuth: true                               // 🔐 元数据：需要用户登录认证才能访问
      }
    },

    // ┌─ 登录页面路由（访客专用）
    {
      path: '/login',                                      // 登录页面路径
      name: 'login',                                       // 路由名称
      component: () => import('../views/Login.vue'),        // 懒加载登录组件
      meta: {
        guest: true                                       // 👤 元数据：仅限未登录用户访问
      }
    },

    // ┌─ 注册页面路由（访客专用）
    {
      path: '/register',                                   // 注册页面路径
      name: 'register',                                    // 路由名称
      component: () => import('../views/Register.vue'),     // 懒加载注册组件
      meta: {
        guest: true                                       // 👤 元数据：仅限未登录用户访问
      }
    }
  ]
})

// ============================================================================
// 路由守卫：全局前置守卫，处理页面访问权限控制
// ============================================================================
router.beforeEach((to, from, next) => {
  // 🔥 获取认证状态管理实例
  const authStore = useAuthStore()                         // 获取全局认证状态，用于检查用户登录状态

  // ┌─ 第一层权限检查：需要认证的页面
  // 检查目标路由是否需要登录认证，且用户当前未登录
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next('/login')                                         // 重定向到登录页面
    return                                                 // 提前返回，阻止后续检查
  }

  // ┌─ 第二层权限检查：访客专用页面
  // 检查目标路由是否为访客专用，且用户当前已登录
  if (to.meta.guest && authStore.isLoggedIn) {
    next('/dashboard')                                     // 重定向到仪表板页面
    return                                                 // 提前返回，阻止后续检查
  }

  // ┌─ 权限检查通过：允许访问
  next()                                                   // 允许导航到目标路由
})

// ============================================================================
// 导出路由实例：供Vue应用使用
// ============================================================================
export default router                                      // 导出配置好的路由实例
