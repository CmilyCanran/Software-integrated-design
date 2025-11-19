<template>
  <el-header class="app-header" height="auto">
    <div class="header-content">
      <div class="left-section">
        <div class="system-title">
          <h2>{{ title }}</h2>
        </div>

        <!-- 欢迎信息 -->
        <div class="welcome-info" v-if="showWelcome">
          <h1>欢迎回来，{{ authStore.username }}！</h1>
          <p class="user-role">
            <el-tag :type="authStore.isAdmin ? 'danger' : 'success'">
              {{ authStore.isAdmin ? '管理员' : '普通用户' }}
            </el-tag>
          </p>
        </div>
      </div>

      <div class="user-info">
        <el-dropdown @command="handleCommand" placement="bottom-end">
          <span class="user-dropdown">
            <el-avatar :size="32" icon="User" />
            <span class="username">{{ authStore.username }}</span>
            <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>
                个人资料
              </el-dropdown-item>
              <el-dropdown-item command="settings">
                <el-icon><Setting /></el-icon>
                设置
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon><SwitchButton /></el-icon>
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
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage, ElMessageBox } from 'element-plus'

const props = defineProps({
  title: {
    type: String,
    default: '服装销售系统'
  },
  showWelcome: {
    type: Boolean,
    default: true
  }
})

const router = useRouter()
const authStore = useAuthStore()

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      ElMessage.info('个人资料功能开发中...')
      break
    case 'settings':
      ElMessage.info('设置功能开发中...')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm(
          '确定要退出登录吗？',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )

        authStore.logout()
        ElMessage.success('已退出登录')
        router.push('/login')

      } catch {
        // 用户取消
      }
      break
  }
}
</script>

<style scoped>
.app-header {
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
  padding: 12px 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  height: 60px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.left-section {
  display: flex;
  align-items: center;
  gap: 30px;
}

.system-title h2 {
  margin: 0;
  color: #333;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 1px;
}

/* 欢迎信息样式 */
.welcome-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.welcome-info h1 {
  margin: 0;
  color: #333;
  font-size: 16px;
  font-weight: 500;
}

.user-role {
  margin: 0;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s ease;
}

.user-dropdown:hover {
  background-color: #f5f5f5;
}

.username {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.dropdown-icon {
  font-size: 12px;
  color: #666;
  transition: transform 0.3s ease;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
}

@media (max-width: 768px) {
  .header-content {
    padding: 0 16px;
  }

  .system-title h2 {
    font-size: 18px;
  }

  .username {
    display: none;
  }
}

@media (max-width: 1024px) {
  .left-section {
    gap: 20px;
  }

  .welcome-info h1 {
    font-size: 15px;
  }
}

@media (max-width: 768px) {
  .header-content {
    padding: 0 16px;
  }

  .left-section {
    gap: 15px;
    flex-direction: column;
    align-items: flex-start;
  }

  .system-title h2 {
    font-size: 18px;
  }

  .welcome-info h1 {
    font-size: 15px;
  }

  .username {
    display: none;
  }
}

@media (max-width: 480px) {
  .header-content {
    padding: 0 12px;
  }

  .system-title h2 {
    font-size: 14px;
  }

  .welcome-info h1 {
    font-size: 14px;
  }

  .welcome-info {
    gap: 8px;
  }

  .left-section {
    gap: 12px;
  }
}
</style>

<style>
/* 全局样式：为固定头部预留空间 */
body {
  padding-top: 60px;
}

/* 确保容器正确处理固定头部 */
.el-container {
  margin-top: 0;
}
</style>