<template>
  <el-header class="merchant-header">
    <div class="header-content">
      <!-- 左侧-->
      <div class="header-left">
        <div class="back-button">
          <el-button type="default" circle icon="ArrowLeft" @click="handleBack"/>
        </div>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/dashboard' }">
            <el-icon>
              <HomeFilled />
            </el-icon>
            仪表板
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
        <!-- 通知图标 -->
        <el-badge :value="notificationCount" class="notification-badge">
          <el-button circle icon="Bell" @click="handleNotifications" />
        </el-badge>

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
              <el-dropdown-item v-if="isMerchant" command="dashboard">
                <el-icon>
                  <House />
                </el-icon>
                仪表板
              </el-dropdown-item>
              <el-dropdown-item v-if="isMerchant" command="products">
                <el-icon>
                  <ShoppingCart />
                </el-icon>
                商品管理
              </el-dropdown-item>
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
import { ElMessage } from 'element-plus'
import {
  HomeFilled,
  User,
  ArrowDown,
  House,
  ShoppingCart,
  SwitchButton
} from '@element-plus/icons-vue'

// Props 定义
interface Props {
  pageTitle?: string
  notificationCount?: number
}

const props = withDefaults(defineProps<Props>(), {
  pageTitle: '',
  notificationCount: 0
})

// 显式引用 props 以满足 TypeScript 检查
// 这些值在模板中被使用
const pageTitle = props.pageTitle
const notificationCount = props.notificationCount

// 状态管理
const router = useRouter()
const authStore = useAuthStore()

// 计算属性
const username = computed(() => authStore.userInfo?.username || '未知用户')
const isMerchant = computed(() => {
  return authStore.userInfo?.role === 'SHOPER' || authStore.userInfo?.role === 'ADMIN'
})
const UserIcon = User

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
    case 'dashboard':
      router.push('/dashboard')
      break
    case 'products':
      router.push('/merchant/products')
      break
    case 'logout':
      authStore.logout()
      router.push('/login')
      break
  }
}

const handleNotifications = () => {
  ElMessage.info('通知功能开发中...')
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
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
  padding: 0 20px;
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

.notification-badge {
  margin-right: 8px;
}
  .back-button {
  margin-right: 16px;;
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

}

@media (max-width: 480px) {
  .notification-badge {
    display: none;
  }
}
</style>
