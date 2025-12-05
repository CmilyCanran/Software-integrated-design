<template>
  <el-header class="merchant-header">
    <div class="header-content">
      <!-- 左侧-->
      <div class="header-left">
        <div class="back-button">
          <el-button type="default" circle icon="ArrowLeft" @click="handleBack"/>
        </div>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/home' }">
            <el-icon>
              <HomeFilled />
            </el-icon>
            首页
          </el-breadcrumb-item>
          <el-breadcrumb-item v-if="pageTitle">
            {{ pageTitle }}
          </el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <!-- 中间：页面标题 -->
      <div class="header-center">
        <h2>{{ pageTitle || '服装销售系统' }}</h2>
      </div>

      <!-- 右侧：用户信息和操作 -->
      <div class="header-right">
        <!-- 快捷操作按钮 -->
        <div v-if="isUser || isShoper || isAdmin" class="quick-actions">
          <!-- USER: Browse Products -->
          <el-button
            v-if="isUser"
            type="default"
            size="small"
            @click="handleBrowseProducts"
          >
            <el-icon><Document /></el-icon>
            浏览商品
          </el-button>
          <!-- USER: My Orders -->
          <el-button
            v-if="isUser"
            type="default"
            size="small"
            @click="handleMyOrders"
          >
            <el-icon><Document /></el-icon>
            我的订单
          </el-button>
          <!-- USER: Cart -->
          <el-badge v-if="isUser && shouldShowCart" :value="actualCartItemCount" class="cart-badge">
            <el-button
              type="default"
              size="small"
              @click="handleCart"
            >
              <el-icon><ShoppingCart /></el-icon>
              购物车
            </el-button>
          </el-badge>
          <!-- SHOPER: Product Management -->
          <el-button
            v-if="isShoper"
            type="primary"
            size="small"
            @click="handleProductManagement"
          >
            <el-icon><ShoppingCart /></el-icon>
            商品管理
          </el-button>
          <!-- SHOPER: Order Management -->
          <el-button
            v-if="isShoper"
            type="success"
            size="small"
            @click="handleOrderManagement"
          >
            <el-icon><Document /></el-icon>
            订单管理
          </el-button>
          <!-- ADMIN: User Management -->
          <el-button
            v-if="isAdmin"
            type="warning"
            size="small"
            @click="handleUserManagement"
          >
            <el-icon><User /></el-icon>
            用户管理
          </el-button>
        </div>

        <!-- 用户信息下拉菜单 -->
        <el-dropdown trigger="click" @command="handleCommand" placement="bottom-end">
          <span class="user-dropdown">
            <el-avatar :size="32" :icon="UserIcon" />
            <span class="username">{{ username }}</span>
            <el-icon class="dropdown-icon">
              <ArrowDown />
            </el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon>
                  <User />
                </el-icon>
                个人资料
              </el-dropdown-item>

              <!-- USER specific -->
              <el-dropdown-item v-if="isUser" command="products">
                <el-icon>
                  <Document />
                </el-icon>
                浏览商品
              </el-dropdown-item>
              <el-dropdown-item v-if="isUser" command="cart">
                <el-icon>
                  <ShoppingCart />
                </el-icon>
                购物车
              </el-dropdown-item>
              <el-dropdown-item v-if="isUser" command="orders">
                <el-icon>
                  <Document />
                </el-icon>
                我的订单
              </el-dropdown-item>

              <el-dropdown-item v-if="isShoper" command="products">
                <el-icon>
                  <ShoppingCart />
                </el-icon>
                商品管理
              </el-dropdown-item>
              <el-dropdown-item v-if="isShoper" command="seller-orders">
                <el-icon>
                  <Document />
                </el-icon>
                订单管理
              </el-dropdown-item>

              <!-- ADMIN specific -->
              <el-dropdown-item v-if="isAdmin" command="products">
                <el-icon>
                  <ShoppingCart />
                </el-icon>
                商品管理
              </el-dropdown-item>
              <el-dropdown-item v-if="isAdmin" command="seller-orders">
                <el-icon>
                  <Document />
                </el-icon>
                订单管理
              </el-dropdown-item>
              <el-dropdown-item v-if="isAdmin" command="users">
                <el-icon>
                  <User />
                </el-icon>
                用户管理
              </el-dropdown-item>

              <!-- Common -->
              <el-dropdown-item divided command="logout">
                <el-icon>
                  <SwitchButton />
                </el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </el-header>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { ElMessage } from 'element-plus'
import {
  HomeFilled,
  User,
  ArrowDown,
  House,
  ShoppingCart,
  SwitchButton,
  Document
} from '@element-plus/icons-vue'

// Props 定义
interface Props {
  pageTitle?: string
  showCart?: boolean
  cartItemCount?: number
}

const props = withDefaults(defineProps<Props>(), {
  pageTitle: '',
  showCart: false,
  cartItemCount: 0
})

// 显式引用 props 以满足 TypeScript 检查
// 这些值在模板中被使用
const pageTitle = props.pageTitle

// 状态管理
const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()

// 计算属性
const username = computed(() => authStore.userInfo?.username || '未知用户')
const isUser = computed(() => authStore.userInfo?.role === 'USER')
const isShoper = computed(() => authStore.userInfo?.role === 'SHOPER')
const isAdmin = computed(() => authStore.userInfo?.role === 'ADMIN')
const UserIcon = User

// 购物车相关计算属性
const actualCartItemCount = computed(() => cartStore.totalItems)
const shouldShowCart = computed(() => {
  return props.showCart || isUser.value
})

const handleBack = () => {
  router.back()
}

// 事件处理函数
const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      // TODO: 跳转到个人资料页面
      ElMessage.info('个人资料功能开发中...')
      break
    case 'products':
      router.push('/products')
      break
    case 'orders':
      router.push('/orders')
      break
    case 'cart':
      router.push('/cart')
      break
    case 'seller-orders':
      router.push('/seller-orders')
      break
    case 'users':
      router.push('/admin/users')
      break
    case 'logout':
      authStore.logout()
      router.push('/login')
      break
  }
}

const handleProductManagement = () => {
  router.push('/merchant/products')
}

const handleOrderManagement = () => {
  router.push('/seller-orders')
}

const handleUserManagement = () => {
  router.push('/admin/users')
}

const handleBrowseProducts = () => {
  router.push('/products')
}

const handleMyOrders = () => {
  router.push('/orders')
}

const handleCart = async () => {
  try {
    // 尝试获取最新的购物车数据
    await cartStore.fetchCart()
    // 跳转到购物车页面
    router.push('/cart')
  } catch (error) {
    console.error('获取购物车数据失败:', error)
    ElMessage.error('获取购物车数据失败')
    // 仍然跳转到购物车页面，即使获取数据失败
    router.push('/cart')
  }
}
</script>

<style scoped>
.merchant-header {
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0;
  height: 60px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.header-content {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 20px;
  position: relative;
}

.header-left {
  display: flex;
  align-items: center;
  z-index: 2;
}

.header-center {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  justify-content: center;
  z-index: 1;
}

.header-right {
  display: flex;
  align-items: center;
  margin-left: auto;
  z-index: 2;
}

.header-center h2 {
  margin: 0;
  color: #303133;
  font-size: 18px;
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-button {
  margin-right: 16px;
}

.quick-actions {
  display: flex;
  gap: 8px;
  margin-right: 16px;
}

/* Normalize spacing for badge-wrapped buttons */
.cart-badge {
  display: inline-flex;
  align-self: center;
  align-items: center;
  margin: 0;
  flex-shrink: 0;  /* Prevents flex item from shrinking */
}

/* Ensure the badge content doesn't affect alignment */
.cart-badge .el-badge__content {
  z-index: 10;
}

/* Ensure consistent sizing with regular buttons */
.cart-badge .el-button {
  margin: 0;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.user-dropdown:hover {
  background-color: #f5f7fa;
}

.username {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.dropdown-icon {
  font-size: 12px;
  color: #909399;
  transition: transform 0.3s;
}

.user-dropdown:hover .dropdown-icon {
  transform: rotate(180deg);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    padding: 0 12px;
  }

  .header-center {
    display: none;
  }

  .header-right {
    gap: 8px;
  }

  .username {
    display: none;
  }

  .quick-actions {
    display: none;
  }
}

@media (max-width: 480px) {
  .right-badge {
    display: none;
  }
}
</style>
