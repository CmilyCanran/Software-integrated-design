<template>
  <div class="dashboard-container">
    <el-container>
      <el-header>
        <div class="header-content">
          <h2>服装销售系统</h2>
          <div class="user-info">
            <el-dropdown @command="handleCommand">
              <span class="user-dropdown">
                <el-avatar :size="32" icon="User" />
                <span class="username">{{ authStore.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                  <el-dropdown-item command="settings">设置</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>

      <el-main>
        <div class="welcome-section">
          <h1>欢迎回来，{{ authStore.username }}！</h1>
          <p class="user-role">
            <el-tag :type="authStore.isAdmin ? 'danger' : 'success'">
              {{ authStore.isAdmin ? '管理员' : '普通用户' }}
            </el-tag>
          </p>
        </div>

        <el-row :gutter="20" class="stats-row">
          <el-col :span="6">
            <el-card class="stats-card">
              <div class="stats-content">
                <el-icon class="stats-icon"><User /></el-icon>
                <div class="stats-info">
                  <h3>用户总数</h3>
                  <p class="stats-number">1,234</p>
                </div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="6">
            <el-card class="stats-card">
              <div class="stats-content">
                <el-icon class="stats-icon"><ShoppingCart /></el-icon>
                <div class="stats-info">
                  <h3>商品数量</h3>
                  <p class="stats-number">567</p>
                </div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="6">
            <el-card class="stats-card">
              <div class="stats-content">
                <el-icon class="stats-icon"><Document /></el-icon>
                <div class="stats-info">
                  <h3>订单总数</h3>
                  <p class="stats-number">890</p>
                </div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="6">
            <el-card class="stats-card">
              <div class="stats-content">
                <el-icon class="stats-icon"><Money /></el-icon>
                <div class="stats-info">
                  <h3>销售额</h3>
                  <p class="stats-number">¥12,345</p>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="20" class="content-row">
          <el-col :span="16">
            <el-card>
              <template #header>
                <h3>快速操作</h3>
              </template>
              <div class="quick-actions">
                <el-button type="primary" size="large" icon="Plus">
                  添加商品
                </el-button>
                <el-button type="success" size="large" icon="ShoppingCart">
                  查看订单
                </el-button>
                <el-button type="info" size="large" icon="User">
                  用户管理
                </el-button>
                <el-button type="warning" size="large" icon="Setting">
                  系统设置
                </el-button>
              </div>
            </el-card>
          </el-col>

          <el-col :span="8">
            <el-card>
              <template #header>
                <h3>系统信息</h3>
              </template>
              <div class="system-info">
                <p><strong>版本:</strong> v1.0.0</p>
                <p><strong>环境:</strong> 开发环境</p>
                <p><strong>最后更新:</strong> {{ formatDate(new Date()) }}</p>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const router = useRouter()
const authStore = useAuthStore()

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

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
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

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
.dashboard-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.el-header {
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
  height: 60px;
  display: flex;
  align-items: center;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h2 {
  margin: 0;
  color: #333;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-dropdown:hover {
  background-color: #f5f5f5;
}

.username {
  font-size: 14px;
  color: #333;
}

.el-main {
  padding: 20px;
}

.welcome-section {
  margin-bottom: 30px;
}

.welcome-section h1 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 28px;
}

.user-role {
  margin: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  height: 120px;
}

.stats-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stats-icon {
  font-size: 32px;
  color: #409eff;
}

.stats-info h3 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #666;
}

.stats-number {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.content-row {
  margin-top: 20px;
}

.quick-actions {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.system-info p {
  margin: 8px 0;
  color: #666;
}
</style>
