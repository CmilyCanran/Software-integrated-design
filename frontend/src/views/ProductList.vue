<template>
  <Header page-title="商品列表" />
  <div class="product-list">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-actions">
        <el-button
          v-if="authStore.canManageProducts"
          type="primary"
          @click="handleAddProduct"
        >
          添加商品
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="search-filter-section">
      <!-- 搜索框 -->
      <div class="search-container">
        <SearchBox
          placeholder="搜索商品名称..."
          :show-suggestions="true"
          :suggestions="searchSuggestions"
          @search="handleSearch"
          @input="handleSearchInput"
        />
      </div>

      <!-- 筛选和排序 -->
      <div class="filter-controls">
        <!-- 排序选择 -->
        <el-select
          v-model="sortBy"
          placeholder="排序方式"
          class="sort-select"
          @change="handleSortChange"
        >
          <el-option
            v-for="option in sortOptions"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </el-select>

        <!-- 视图切换 -->
        <el-button-group class="view-toggle">
          <el-button
            :type="viewMode === 'grid' ? 'primary' : 'default'"
            @click="viewMode = 'grid'"
          >
            <el-icon><Grid /></el-icon>
          </el-button>
          <el-button
            :type="viewMode === 'list' ? 'primary' : 'default'"
            @click="viewMode = 'list'"
          >
            <el-icon><List /></el-icon>
          </el-button>
        </el-button-group>
      </div>
    </div>


    <!-- 商品统计信息 -->
    <div class="product-stats">
      <span class="total-count">
        共找到 {{ totalProducts }} 件商品
      </span>
      <span
        v-if="selectedCategory"
        class="category-filter"
      >
        分类：{{ selectedCategory }}
        <el-button
          type="text"
          size="small"
          @click="clearCategoryFilter"
        >
          <el-icon><Close /></el-icon>
        </el-button>
      </span>
    </div>

    <!-- 商品展示区域 -->
    <div class="products-container">
      <!-- 加载状态 -->
      <div
        v-if="loading"
        class="loading-container"
      >
        <el-skeleton
          v-for="i in 8"
          :key="i"
          animated
          class="product-skeleton"
        >
          <template #template>
            <el-skeleton-item variant="image" style="width: 100%; height: 200px;" />
            <div style="padding: 14px;">
              <el-skeleton-item variant="h3" style="width: 50%;" />
              <el-skeleton-item variant="text" style="width: 70%; margin-top: 10px;" />
              <el-skeleton-item variant="text" style="width: 40%; margin-top: 10px;" />
            </div>
          </template>
        </el-skeleton>
      </div>

      <!-- 网格视图 -->
      <div
        v-else-if="viewMode === 'grid' && filteredProducts.length > 0"
        class="products-grid"
      >
        <ProductCard
          v-for="product in filteredProducts"
          :key="product.id"
          :product="product"
          :can-edit="authStore.canManageProducts"
          :can-delete="authStore.canManageProducts"
          :show-actions="authStore.canManageProducts"
          :show-quick-actions="!authStore.canManageProducts"
          :quick-actions="authStore.canManageProducts ? undefined : customerQuickActions"
          @click="handleProductClick"
          @edit="handleEditProduct"
          @add-to-cart="handleAddToCart"
          @quick-view="handleQuickView"
        />
      </div>

      <!-- 列表视图 -->
      <div
        v-else-if="viewMode === 'list' && filteredProducts.length > 0"
        class="products-list"
      >
        <ProductListItem
          v-for="product in filteredProducts"
          :key="product.id"
          :product="product"
          :show-edit-button="authStore.canManageProducts"
          :show-delete-button="authStore.canManageProducts"
          @click="handleProductClick"
          @view-details="handleViewDetails"
          @edit="handleEditProduct"
        />
      </div>

      <!-- 空状态 -->
      <div
        v-else-if="!loading && filteredProducts.length === 0"
        class="empty-state"
      >
        <el-empty
          :image-size="200"
          description="暂无商品数据"
        >
          <el-button
            v-if="authStore.canManageProducts"
            type="primary"
            @click="handleAddProduct"
          >
            添加第一个商品
          </el-button>
        </el-empty>
      </div>
    </div>

    <!-- 分页组件 -->
    <div
      v-if="filteredProducts.length > 0"
      class="pagination-container"
    >
      <PaginationBar
        :total="totalProducts"
        :current-page="currentPage"
        :page-size="pageSize"
        @update:current-page="handlePageChange"
        @update:page-size="handlePageSizeChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Grid, List, Close } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import SearchBox from '@/components/SearchBox.vue'
import ProductCard from '@/components/ProductCard.vue'
import ProductListItem from '@/components/ProductListItem.vue'
import PaginationBar from '@/components/PaginationBar.vue'
import type { Product } from '@/types/product'
import Header from '@/components/Header.vue'
import { productAPI } from '@/api/product'

// 响应式数据
const router = useRouter()
const authStore = useAuthStore()

const loading = ref<boolean>(false)
const products = ref<Product[]>([])
const searchQuery = ref<string>('')
const sortBy = ref<string>('createdAt')
const viewMode = ref<'grid' | 'list'>('grid')
const currentPage = ref<number>(1)
const pageSize = ref<number>(12)
const totalProducts = ref<number>(0)
const selectedCategory = ref<string>('')
const productsResponse = ref<PaginatedResponse<Product> | null>(null)


// 搜索建议
const searchSuggestions = ref<string[]>([
  '运动鞋', 'T恤', '连衣裙', '背包', '手表', '耳机', '手机壳', '休闲裤'
])

// 排序选项
const sortOptions = ref([
  { label: '最新上架', value: 'createdAt' },
  { label: '价格从低到高', value: 'price-asc' },
  { label: '价格从高到低', value: 'price-desc' },
  { label: '销量最高', value: 'sales' },
  { label: '库存最多', value: 'stock' }
])


// 计算属性：筛选后的商品
const filteredProducts = computed<Product[]>(() => {
  let result = products.value

  // 搜索筛选
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(product =>
      product.productName.toLowerCase().includes(query) ||
      product.description?.toLowerCase().includes(query)
    )
  }

  // 排序
  result.sort((a, b) => {
    switch (sortBy.value) {
      case 'price-asc':
        return a.price - b.price
      case 'price-desc':
        return b.price - a.price
      case 'sales':
        return b.salesCount - a.salesCount
      case 'stock':
        return b.stockQuantity - a.stockQuantity
      case 'createdAt':
      default:
        return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
    }
  })

  return result
})

// 客户模式的快速操作配置
const customerQuickActions = [
  {
    icon: 'View',
    type: 'primary' as const,
    event: 'quick-view',
    tooltip: '快速预览'
  },
  {
    icon: 'ShoppingCart',
    type: 'danger' as const,
    event: 'add-to-cart',
    tooltip: '加入购物车',
    condition: (product: Product) => product.stockQuantity > 0
  }
]

// 模拟商品数据
// 移除mockProducts，使用后端API获取真实数据

// 事件处理函数
const handleSearch = (query: string): void => {
  searchQuery.value = query
  currentPage.value = 1
}

const handleSearchInput = (_query: string): void => {
  // 实时搜索处理 - 暂时留空，为将来功能预留
}

const handleSortChange = (): void => {
  currentPage.value = 1
}

const handlePageChange = (page: number): void => {
  currentPage.value = page
}

const handlePageSizeChange = (size: number): void => {
  pageSize.value = size
  currentPage.value = 1
}


const handleProductClick = (product: Product): void => {
  router.push(`/products/${product.id}`)
}

const handleViewDetails = (product: Product): void => {
  router.push(`/products/${product.id}`)
}

const handleAddProduct = (): void => {
  router.push('/products/add')
}

const handleEditProduct = (product: Product): void => {
  router.push(`/products/${product.id}/edit`)
}

const handleAddToCart = (product: Product): void => {
  if (product.stockQuantity === 0) {
    ElMessage.warning('商品库存不足')
    return
  }
  ElMessage.success(`"${product.productName}" 已加入购物车`)
}

const handleQuickView = (product: Product): void => {
  ElMessage.info(`快速预览商品: ${product.productName}`)
}


const clearCategoryFilter = (): void => {
  selectedCategory.value = ''
}

// 加载商品数据
const loadProducts = async (): Promise<void> => {
  loading.value = true
  try {
    // 调用后端API获取产品列表
    const params = {
      page: currentPage.value - 1, // Spring Boot分页从0开始
      size: pageSize.value,
      sort: sortBy.value,
      keyword: searchQuery.value,
      isAvailable: true // 只获取上架商品
    }

    productsResponse.value = await productAPI.getProducts(params)
    products.value = productsResponse.value?.data || []
    totalProducts.value = productsResponse.value?.total || 0
  } catch (error) {
    ElMessage.error('加载商品数据失败')
    console.error('Load products error:', error)
    products.value = []
    totalProducts.value = 0
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(() => {
  loadProducts()
})

// 监听搜索变化
watch(searchQuery, () => {
  currentPage.value = 1
  loadProducts()
})

// 监听排序变化
watch(sortBy, () => {
  currentPage.value = 1
  loadProducts()
})

// 监听分页变化
watch([currentPage, pageSize], () => {
  loadProducts()
})

// 监听分类筛选变化
watch(selectedCategory, () => {
  currentPage.value = 1
  loadProducts()
})
</script>

<style scoped>
.product-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.page-header h1 {
  margin: 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.search-filter-section {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.search-container {
  flex: 1;
  min-width: 300px;
}

.filter-controls {
  display: flex;
  gap: 12px;
  align-items: center;
}

.sort-select {
  width: 140px;
}

.view-toggle .el-button {
  padding: 8px 12px;
}

.product-stats {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
  font-size: 14px;
  color: #606266;
}


.total-count {
  font-weight: 500;
}

.category-filter {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  background: #f0f9ff;
  border: 1px solid #bae6fd;
  border-radius: 4px;
  color: #0369a1;
}

.products-container {
  min-height: 400px;
}

.loading-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.product-skeleton {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.products-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.product-list-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.product-list-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.product-list-item .product-image {
  width: 120px;
  height: 120px;
  position: relative;
  flex-shrink: 0;
}

.product-list-item .product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
}

.product-list-item .product-info {
  flex: 1;
  min-width: 0;
}

.product-list-item .product-info h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.product-list-item .description {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  display: -webkit-box;
  display: box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-list-item .product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.product-list-item .product-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.pagination-container {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-list {
    padding: 10px;
  }

  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .search-filter-section {
    flex-direction: column;
  }

  .search-container {
    min-width: auto;
  }

  .filter-controls {
    justify-content: space-between;
  }

  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 12px;
  }

  .product-list-item {
    flex-direction: column;
  }

  .product-list-item .product-image {
    width: 100%;
    height: 200px;
  }

  .product-list-item .product-actions {
    justify-content: stretch;
  }

  .product-list-item .product-actions .el-button {
    flex: 1;
  }
}
</style>
