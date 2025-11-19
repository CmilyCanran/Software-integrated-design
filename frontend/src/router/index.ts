// ============================================================================
// Vue Router 路由配置文件 (TypeScript版本) - 负责页面导航和权限控制
// ============================================================================

import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// ============================================================================
// 路由元数据类型定义
// ============================================================================
interface RouteMeta {
  requiresAuth?: boolean
  guest?: boolean
  title?: string
  roles?: string[]
}

// ============================================================================
// 路由实例创建：配置路由规则和历史模式
// ============================================================================
const router = createRouter({
  // 路由历史模式配置
  history: createWebHistory(import.meta.env.BASE_URL),

  // 路由规则配置数组
  routes: [
    // 根路径重定向路由
    {
      path: '/',
      redirect: '/dashboard'
    },

    // 仪表板路由（需要认证）
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('../views/Dashboard.vue'),
      meta: {
        requiresAuth: true,
        title: '仪表板'
      } as RouteMeta
    },

    // 登录页面路由（访客专用）
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue'),
      meta: {
        guest: true,
        title: '登录'
      } as RouteMeta
    },

    // 注册页面路由（访客专用）
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue'),
      meta: {
        guest: true,
        title: '注册'
      } as RouteMeta
    },

    // 组件测试页面路由（隐藏页面，只能手动访问）
    {
      path: '/component-test',
      name: 'component-test',
      component: () => import('../views/ComponentTest.vue'),
      meta: {
        requiresAuth: true,
        title: '组件测试',
        hidden: true  // 标记为隐藏页面，不在导航中显示
      } as RouteMeta
    }
  ] as RouteRecordRaw[]
})

// ============================================================================
// 路由守卫：全局前置守卫，处理页面访问权限控制
// ============================================================================
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  // 第一层权限检查：需要认证的页面
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next('/login')
    return
  }

  // 第二层权限检查：访客专用页面
  if (to.meta.guest && authStore.isLoggedIn) {
    next('/dashboard')
    return
  }

  // 权限检查通过：允许访问
  next()
})

// ============================================================================
// 导出路由实例：供Vue应用使用
// ============================================================================
export default router