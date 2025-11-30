<template>
  <div class="product-management">
    <!-- 商家页面头部 -->
    <Header page-title="商品管理" />

    <el-container>
      <el-main>
        <!-- 操作工具栏 -->
        <div class="toolbar">
          <!-- 搜索框 -->
          <el-input
            v-model="searchQuery"
            placeholder="搜索商品名称、描述或分类"
            clearable
            style="width: 300px"
            @input="handleSearch"
            @clear="handleSearchClear"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>

          <!-- 筛选器组 -->
          <div class="filter-group">
            <el-select
              v-model="categoryFilter"
              placeholder="按分类筛选"
              clearable
              style="width: 150px"
              @change="handleFilterChange"
            >
              <el-option label="全部" value="" />
              <el-option label="服装" value="服装" />
              <el-option label="鞋类" value="鞋类" />
              <el-option label="配饰" value="配饰" />
              <el-option label="箱包" value="箱包" />
            </el-select>

            <el-select
              v-model="statusFilter"
              placeholder="按状态筛选"
              clearable
              style="width: 120px"
              @change="handleFilterChange"
            >
              <el-option label="全部" value="" />
              <el-option label="在售" value="available" />
              <el-option label="下架" value="unavailable" />
            </el-select>

            <!-- 新增商品按钮 -->
            <el-button
              type="primary"
              @click="openCreateDialog"
            >
              <el-icon><Plus /></el-icon>
              新增商品
            </el-button>

            <!-- 布局切换按钮 -->
            <el-button-group>
              <el-button
                :type="productStore.displayMode === 'grid' ? 'primary' : ''"
                @click="productStore.displayMode = 'grid'"
              >
                <el-icon><Grid /></el-icon>
                网格
              </el-button>
              <el-button
                :type="productStore.displayMode === 'table' ? 'primary' : ''"
                @click="productStore.displayMode = 'table'"
              >
                列表
              </el-button>
            </el-button-group>
          </div>
        </div>

        <!-- 统计信息卡片 -->
        <div class="stats-cards" v-if="productStats">
          <el-card class="stat-card">
            <div class="stat-content">
              <el-icon class="stat-icon"><DataAnalysis /></el-icon>
              <div class="stat-info">
                <h3>总商品数</h3>
                <span class="stat-value">{{ productStats.totalProducts }}</span>
              </div>
            </div>
          </el-card>

          <el-card class="stat-card">
            <div class="stat-content">
              <el-icon class="stat-icon"><ShoppingCart /></el-icon>
              <div class="stat-info">
                <h3>在售商品</h3>
                <span class="stat-value">{{ productStats.availableProducts }}</span>
              </div>
            </div>
          </el-card>

          <el-card class="stat-card">
            <div class="stat-content">
              <el-icon class="stat-icon"><ShoppingCart /></el-icon>
              <div class="stat-info">
                <h3>下架商品</h3>
                <span class="stat-value">{{ productStats.unavailableProducts }}</span>
              </div>
            </div>
          </el-card>

          <el-card class="stat-card">
            <div class="stat-content">
              <el-icon class="stat-icon"><Money /></el-icon>
              <div class="stat-info">
                <h3>总销售额</h3>
                <span class="stat-value">¥{{ productStats.totalRevenue?.toLocaleString() || 0 }}</span>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 商品展示区域 -->
        <div class="products-area">
          <!-- 错误提示 -->
          <el-alert
            v-if="productStore.error"
            :title="productStore.error"
            type="error"
            show-icon
            closable
            @close="productStore.clearError"
            class="error-alert"
          />

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="5" animated />
          </div>

          <!-- 空状态 -->
          <el-empty
            v-else-if="!products || products.length === 0"
            description="暂无商品数据"
          >
            <el-button type="primary" @click="openCreateDialog">
              新增商品
            </el-button>
          </el-empty>

          <!-- 商品内容 -->
          <div v-else>
            <!-- 网格布局模式 -->
            <div v-if="productStore.displayMode === 'grid'" class="product-grid">
              <ProductCard
                v-for="product in products || []"
                :key="product.id"
                :product="product"
                :can-edit="true"
                :can-delete="true"
                :show-quick-actions="false"
                :quick-actions="merchantQuickActions"
                @click="viewProductDetail(product)"
                @edit="openEditDialog(product)"
                @delete="confirmDelete(product)"
              />
            </div>

            <!-- 表格布局模式 -->
            <div v-else class="product-table">
              <el-table :data="products || []" stripe>
                <el-table-column prop="productName" label="商品名称" width="200" />
                <el-table-column prop="category" label="分类" width="100" />
                <el-table-column prop="price" label="价格" width="120">
                  <template #default="scope">
                    ¥{{ scope.row.price.toFixed(2) }}
                  </template>
                </el-table-column>
                <el-table-column prop="stockQuantity" label="库存" width="100" />
                <el-table-column prop="salesCount" label="销量" width="100" />
                <el-table-column prop="isAvailable" label="状态" width="100">
                  <template #default="scope">
                    <el-tag :type="scope.row.isAvailable ? 'success' : 'danger'">
                      {{ scope.row.isAvailable ? '在售' : '下架' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createdAt" label="创建时间" width="180">
                  <template #default="scope">
                    {{ formatDate(scope.row.createdAt) }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" fixed="right" width="200">
                  <template #default="scope">
                    <el-button
                      size="small"
                      type="primary"
                      @click="openEditDialog(scope.row)"
                    >
                      编辑
                    </el-button>
                    <el-button
                      size="small"
                      @click="toggleProductStatus(scope.row)"
                      :type="scope.row.isAvailable ? 'warning' : 'success'"
                    >
                      {{ scope.row.isAvailable ? '下架' : '上架' }}
                    </el-button>
                    <el-button
                      size="small"
                      type="danger"
                      @click="confirmDelete(scope.row)"
                    >
                      删除
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>

          <!-- 分页组件 -->
          <div v-if="!loading && products && products.length > 0" class="pagination-section">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="pagination?.total || 0"
              :page-sizes="[12, 24, 48, 96]"
              layout="total, sizes, prev, pager, next, jumper"
              @current-change="handlePageChange"
              @size-change="handleSizeChange"
            />
          </div>
        </div>

        <!-- 商品创建/编辑对话框 -->
        <el-dialog
          v-model="formDialogVisible"
          :title="isEditing ? '编辑商品' : '新增商品'"
          width="600px"
        >
          <ProductForm
            :product="editingProduct"
            :is-edit="isEditing"
            @save="handleFormSave"
            @cancel="closeFormDialog"
          />
        </el-dialog>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useProductStore } from '@/stores/product'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Grid, DataAnalysis, ShoppingCart, Money } from '@element-plus/icons-vue'
import ProductCard from '@/components/ProductCard.vue'
import ProductForm from '@/components/ProductForm.vue'
import Header from '@/components/Header.vue'
import type { Product, ProductCreateRequest, ProductUpdateRequest } from '@/types/product'
import dayjs from 'dayjs'

// 状态管理
const productStore = useProductStore()

// 使用真实的产品store数据
const products = computed(() => productStore.products)
const productStats = computed(() => productStore.productStats)
const pagination = computed(() => productStore.pagination)
const loading = computed(() => productStore.loading)

// 搜索和筛选状态
const searchQuery = ref('')
const categoryFilter = ref('')
const statusFilter = ref('')

// 分页状态
const currentPage = ref(1)
const pageSize = ref(12)

// 对话框状态
const formDialogVisible = ref(false)
const isEditing = ref(false)
const editingProduct = ref<Product | null>(null)

// 快速操作按钮配置接口
interface QuickAction {
  icon: string                      // 图标名称
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'default'  // 按钮类型
  event: string                     // 事件名称
  tooltip?: string                  // 提示文本
  disabled?: boolean                // 是否禁用
  condition?: (product: Product) => boolean  // 显示条件函数，接收product参数
}

// 商家模式的快速操作配置
const merchantQuickActions: QuickAction[] = [
  {
    icon: 'Edit',
    type: 'primary',
    event: 'edit',
    tooltip: '编辑商品'
  },
  {
    icon: 'Delete',
    type: 'danger',
    event: 'delete',
    tooltip: '删除商品'
  }
]

// 数据加载方法
const loadProducts = async () => {
  try {
    await productStore.fetchMerchantProducts({
      page: currentPage.value - 1, // Convert to 0-based for backend
      size: pageSize.value,
      keyword: searchQuery.value,
      isAvailable: statusFilter.value ?
        (statusFilter.value === 'available') : undefined,
      category: categoryFilter.value || undefined
    })
  } catch (error) {
    console.error('加载商品列表失败:', error)
  }
}

const loadProductStats = async () => {
  try {
    await productStore.fetchProductStats()
  } catch (error) {
    console.error('加载商品统计失败:', error)
  }
}

// 日期格式化
const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

// 防抖搜索实现
let searchTimer: NodeJS.Timeout | null = null

const handleSearch = () => {
  currentPage.value = 1 // 重置到第一页

  // 清除之前的定时器
  if (searchTimer) {
    clearTimeout(searchTimer)
  }

  // 设置新的定时器
  searchTimer = setTimeout(async () => {
    await loadProducts()
  }, 300) // 300ms防抖延迟
}

// 清除搜索
const handleSearchClear = async () => {
  searchQuery.value = ''
  currentPage.value = 1
  await loadProducts()
}

// 筛选条件变化处理
const handleFilterChange = async () => {
  currentPage.value = 1 // 重置到第一页
  await loadProducts()
}

// 页面变化处理
const handlePageChange = async (page: number) => {
  currentPage.value = page
  await loadProducts()
}

// 页面大小变化处理
const handleSizeChange = async (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  await loadProducts()
}

// 查看商品详情
const viewProductDetail = (product: Product) => {
  console.log('查看商品详情:', product)
  ElMessage.info(`查看商品: ${product.productName}`)
}

// 打开创建商品对话框
const openCreateDialog = () => {
  isEditing.value = false
  editingProduct.value = null
  formDialogVisible.value = true
}

// 打开编辑商品对话框
const openEditDialog = (product: Product) => {
  isEditing.value = true
  editingProduct.value = { ...product }
  formDialogVisible.value = true
}

// 关闭表单对话框
const closeFormDialog = () => {
  formDialogVisible.value = false
  isEditing.value = false
  editingProduct.value = null
}

// 确认删除商品
const confirmDelete = async (product: Product) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除商品 "${product.productName}" 吗？此操作不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await productStore.deleteProduct(product.id)
    ElMessage.success('商品删除成功')
    await loadProducts() // 重新加载数据
    await loadProductStats() // 重新加载统计数据
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除商品失败:', error)
      ElMessage.error('删除失败，请重试')
    } else {
      ElMessage.info('已取消删除')
    }
  }
}

// 切换商品状态
const toggleProductStatus = async (product: Product) => {
  try {
    const newStatus = !product.isAvailable
    const action = newStatus ? '上架' : '下架'

    await ElMessageBox.confirm(
      `确定要${action}商品 "${product.productName}" 吗？`,
      `${action}确认`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await productStore.toggleProductStatus(product.id, newStatus)
    ElMessage.success(`商品${action}成功`)
    await loadProducts() // 重新加载数据
    await loadProductStats() // 重新加载统计数据
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error(`${action}商品失败:`, error)
      ElMessage.error(`${action}失败，请重试`)
    } else {
      ElMessage.info(`已取消${action}`)
    }
  }
}

// 处理表单保存
const handleFormSave = async (formData: ProductCreateRequest | ProductUpdateRequest) => {
  console.log('🔍 [DEBUG] ProductManagement - 开始处理表单保存')
  console.log('🔍 [DEBUG] ProductManagement - 编辑模式:', isEditing.value)
  console.log('🔍 [DEBUG] ProductManagement - 编辑商品ID:', editingProduct.value?.id)
  console.log('🔍 [DEBUG] ProductManagement - 接收到的表单数据:', formData)
  console.log('🔍 [DEBUG] ProductManagement - 表单数据关键字段:')
  console.log('  - 商品名称:', formData.productName)
  console.log('  - 价格:', formData.price, '(类型:', typeof formData.price, ')')
  console.log('  - 库存:', formData.stockQuantity, '(类型:', typeof formData.stockQuantity, ')')
  console.log('  - 折扣:', formData.discount, '(类型:', typeof formData.discount, ')')
  console.log('  - 是否上架:', formData.isAvailable, '(类型:', typeof formData.isAvailable, ')')
  console.log('  - 规格:', formData.specifications)
  console.log('  - 主图URL:', formData.mainImageUrl)
  console.log('  - 描述:', formData.description)

  try {
    if (isEditing.value && editingProduct.value) {
      // 编辑模式
      console.log('🔍 [DEBUG] ProductManagement - 开始更新商品, ID:', editingProduct.value.id)
      console.log('🔍 [DEBUG] ProductManagement - 调用productStore.updateProduct')

      await productStore.updateProduct(
        editingProduct.value.id,
        formData as ProductUpdateRequest
      )

      console.log('🔍 [DEBUG] ProductManagement - productStore.updateProduct调用成功')
      ElMessage.success('商品更新成功')
    } else {
      // 创建模式
      console.log('🔍 [DEBUG] ProductManagement - 开始创建商品')
      console.log('🔍 [DEBUG] ProductManagement - 调用productStore.createProduct')

      await productStore.createProduct(formData as ProductCreateRequest)

      console.log('🔍 [DEBUG] ProductManagement - productStore.createProduct调用成功')
      ElMessage.success('商品创建成功')
    }

    console.log('🔍 [DEBUG] ProductManagement - 关闭表单对话框')
    closeFormDialog()

    console.log('🔍 [DEBUG] ProductManagement - 重新加载产品列表')
    await loadProducts() // 重新加载数据

    console.log('🔍 [DEBUG] ProductManagement - 重新加载统计数据')
    await loadProductStats() // 重新加载统计数据

    console.log('🔍 [DEBUG] ProductManagement - 表单保存处理完成')
  } catch (error) {
    console.error('❌ [DEBUG] ProductManagement - 保存失败:', error)
    console.error('❌ [DEBUG] ProductManagement - 错误详情:', {
      message: error?.message,
      stack: error?.stack,
      response: error?.response?.data,
      status: error?.response?.status
    })
    ElMessage.error('保存失败，请重试')
  }
}

// 页面挂载时初始化
onMounted(async () => {
  console.log('商品管理页面已加载')
  await loadProducts()
  await loadProductStats()
})
</script>

<style scoped>
.product-management {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.filter-group {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-left: auto;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border-radius: 8px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px;
}

.stat-icon {
  font-size: 32px;
  color: #409eff;
  opacity: 0.8;
}

.stat-info h3 {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #909399;
  font-weight: normal;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  display: block;
}

.products-area {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  min-height: 500px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.product-table {
  margin-top: 0;
}

.error-alert {
  margin-bottom: 16px;
}

.loading-container {
  padding: 20px;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
}

@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .filter-group {
    margin-left: 0;
    justify-content: center;
    flex-wrap: wrap;
  }

  .stats-cards {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .product-grid {
    grid-template-columns: 1fr;
  }

  .products-area {
    padding: 12px;
  }
}

@media (max-width: 480px) {
  .filter-group {
    flex-direction: column;
    gap: 8px;
  }

  .el-select {
    width: 100% !important;
  }
}
</style>
