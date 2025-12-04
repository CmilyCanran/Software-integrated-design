<template>
  <div class="user-management-container">
    <Header
      page-title="用户管理"
      :showCart="false"
    />
    <el-container>
      <el-main>
        <!-- 用户统计卡片 -->
        <el-row :gutter="20" class="stats-row">
          <el-col :span="6">
            <el-card class="stats-card">
              <div class="stats-content">
                <el-icon class="stats-icon"><User /></el-icon>
                <div class="stats-info">
                  <h3>总用户数</h3>
                  <p class="stats-number">{{ statistics.totalUsers }}</p>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stats-card">
              <div class="stats-content">
                <el-icon class="stats-icon" style="color: #67c23a;"><CircleCheck /></el-icon>
                <div class="stats-info">
                  <h3>启用用户</h3>
                  <p class="stats-number">{{ statistics.enabledUsers }}</p>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stats-card">
              <div class="stats-content">
                <el-icon class="stats-icon" style="color: #f56c6c;"><CircleClose /></el-icon>
                <div class="stats-info">
                  <h3>禁用用户</h3>
                  <p class="stats-number">{{ statistics.disabledUsers }}</p>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stats-card">
              <div class="stats-content">
                <el-icon class="stats-icon" style="color: #e6a23c;"><Avatar /></el-icon>
                <div class="stats-info">
                  <h3>管理员</h3>
                  <p class="stats-number">{{ statistics.adminCount }}</p>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 搜索和筛选工具栏 -->
        <el-card class="filter-card">
          <el-form :inline="true" :model="queryForm" class="filter-form">
            <el-form-item label="用户名" style="width: 200px;">
              <el-input
                v-model="queryForm.username"
                placeholder="输入用户名"
                clearable
                @clear="handleSearch"
                @keyup.enter="handleSearch"
              />
            </el-form-item>
            <el-form-item label="邮箱" style="width: 220px;">
              <el-input
                v-model="queryForm.email"
                placeholder="输入邮箱"
                clearable
                @clear="handleSearch"
                @keyup.enter="handleSearch"
              />
            </el-form-item>
            <el-form-item label="角色" style="width: 160px;">
              <el-select
                v-model="queryForm.role"
                placeholder="选择角色"
                clearable
                @clear="handleSearch"
                @change="handleSearch"
              >
                <el-option label="全部" value="" />
                <el-option label="普通用户" value="USER" />
                <el-option label="商家" value="SHOPER" />
                <el-option label="管理员" value="ADMIN" />
              </el-select>
            </el-form-item>
            <el-form-item label="状态" style="width: 140px;">
              <el-select
                v-model="queryForm.enabled"
                placeholder="选择状态"
                clearable
                @clear="handleSearch"
                @change="handleSearch"
              >
                <el-option label="全部" value="" />
                <el-option label="启用" :value="true" />
                <el-option label="禁用" :value="false" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
              <el-button @click="handleReset">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 用户列表表格 -->
        <el-card class="table-card">
          <el-table
            :data="userList"
            v-loading="loading"
            stripe
            style="width: 100%"
          >
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="username" label="用户名" min-width="120" />
            <el-table-column prop="email" label="邮箱" min-width="180" />
            <el-table-column prop="role" label="角色" width="100">
              <template #default="{ row }">
                <el-tag :type="getRoleType(row.role)">
                  {{ getRoleLabel(row.role) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="enabled" label="状态" width="100">
              <template #default="{ row }">
                <el-switch
                  v-model="row.enabled"
                  @change="handleToggleEnabled(row)"
                  :loading="row.updating"
                />
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="创建时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button
                  type="primary"
                  link
                  @click="handleEditRole(row)"
                  :disabled="row.updating"
                >
                  编辑角色
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            class="pagination"
          />
        </el-card>
      </el-main>
    </el-container>

    <!-- 编辑角色对话框 -->
    <el-dialog
      v-model="roleDialogVisible"
      title="编辑用户角色"
      width="400px"
    >
      <el-form :model="roleForm">
        <el-form-item label="当前角色">
          <el-tag :type="getRoleType(currentRole)">
            {{ getRoleLabel(currentRole) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="新角色">
          <el-select v-model="roleForm.newRole" placeholder="选择新角色">
            <el-option label="普通用户" value="USER" />
            <el-option label="商家" value="SHOPER" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateRole" :loading="roleUpdating">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User, CircleCheck, CircleClose, Avatar, Search, Refresh
} from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import { adminApi, type UserManagementItem, type UserStatistics, type UserQueryRequest } from '@/api/admin'
import dayjs from 'dayjs'

// 响应式数据
const loading = ref(false)
const userList = ref<UserManagementItem[]>([])
const statistics = ref<UserStatistics>({
  totalUsers: 0,
  enabledUsers: 0,
  disabledUsers: 0,
  userCount: 0,
  shoperCount: 0,
  adminCount: 0
})

const queryForm = reactive<UserQueryRequest>({
  username: '',
  email: '',
  role: '',
  enabled: undefined,
  page: 0,
  size: 10,
  sortBy: 'createdAt',
  sortDirection: 'desc'
})

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 角色编辑对话框
const roleDialogVisible = ref(false)
const roleUpdating = ref(false)
const currentRole = ref('')
const currentUserId = ref(0)
const roleForm = reactive({
  newRole: ''
})

// 工具方法
const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

const getRoleType = (role: string) => {
  switch (role) {
    case 'USER': return 'info'
    case 'SHOPER': return 'success'
    case 'ADMIN': return 'danger'
    default: return 'info'
  }
}

const getRoleLabel = (role: string) => {
  switch (role) {
    case 'USER': return '普通用户'
    case 'SHOPER': return '商家'
    case 'ADMIN': return '管理员'
    default: return role
  }
}

// API调用方法
const loadStatistics = async () => {
  try {
    const data = await adminApi.getUserStatistics()
    statistics.value = data
  } catch (error) {
    console.error('获取用户统计失败:', error)
  }
}

const loadUserList = async () => {
  loading.value = true
  try {
    // 构建查询参数，过滤掉空字符串
    const queryParams: UserQueryRequest = {
      page: queryForm.page,
      size: queryForm.size,
      sortBy: queryForm.sortBy,
      sortDirection: queryForm.sortDirection
    }

    // 只添加非空的参数
    if (queryForm.username?.trim()) {
      queryParams.username = queryForm.username.trim()
    }
    if (queryForm.email?.trim()) {
      queryParams.email = queryForm.email.trim()
    }
    if (queryForm.role) {
      queryParams.role = queryForm.role
    }
    if (queryForm.enabled !== undefined) {
      queryParams.enabled = queryForm.enabled
    }

    const pageData = await adminApi.getUserList(queryParams)
    userList.value = pageData.content.map(item => ({
      ...item,
      updating: false
    }))
    total.value = pageData.totalElements
    currentPage.value = pageData.number + 1  // Backend returns 0-based, Element Plus expects 1-based
    pageSize.value = pageData.size
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 事件处理方法
const handleSearch = () => {
  queryForm.page = 0
  loadUserList()
}

const handleReset = () => {
  queryForm.username = ''
  queryForm.email = ''
  queryForm.role = ''
  queryForm.enabled = undefined
  queryForm.page = 0
  handleSearch()
}

const handleSizeChange = (size: number) => {
  queryForm.size = size
  queryForm.page = 0
  loadUserList()
}

const handleCurrentChange = (page: number) => {
  queryForm.page = page - 1  // Element Plus pagination is 1-based, backend expects 0-based
  loadUserList()
}

const handleToggleEnabled = async (row: UserManagementItem & { updating?: boolean }) => {
  try {
    // 确认操作
    await ElMessageBox.confirm(
      `确定要${row.enabled ? '启用' : '禁用'}用户 "${row.username}" 吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    row.updating = true
    await adminApi.toggleUserEnabled(row.id)
    ElMessage.success(`用户已${row.enabled ? '启用' : '禁用'}`)
    // 重新加载统计数据
    loadStatistics()
  } catch (error) {
    // 用户取消或操作失败，恢复状态
    row.enabled = !row.enabled
    if (error !== 'cancel') {
      console.error('切换用户状态失败:', error)
      ElMessage.error('切换用户状态失败')
    }
  } finally {
    row.updating = false
  }
}

const handleEditRole = (row: UserManagementItem) => {
  currentUserId.value = row.id
  currentRole.value = row.role
  roleForm.newRole = row.role
  roleDialogVisible.value = true
}

const handleUpdateRole = async () => {
  if (!roleForm.newRole) {
    ElMessage.warning('请选择新角色')
    return
  }

  if (roleForm.newRole === currentRole.value) {
    ElMessage.warning('新角色不能与当前角色相同')
    return
  }

  roleUpdating.value = true
  try {
    await adminApi.updateUserRole(currentUserId.value, roleForm.newRole)
    ElMessage.success('角色更新成功')
    roleDialogVisible.value = false
    // 重新加载数据
    loadUserList()
    loadStatistics()
  } catch (error) {
    console.error('更新角色失败:', error)
    ElMessage.error('更新角色失败')
  } finally {
    roleUpdating.value = false
  }
}

// 生命周期
onMounted(() => {
  loadStatistics()
  loadUserList()
})
</script>

<style scoped>
.user-management-container {
  min-height: 100vh;
  background-color: #f5f5f5;
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
  height: 100%;
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

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .stats-row {
    display: none;
  }

  .filter-form {
    flex-direction: column;
  }

  .filter-form .el-form-item {
    width: 100% !important;
    margin-right: 0;
  }
}
</style>