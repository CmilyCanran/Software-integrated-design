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

    <!-- 规格筛选器 -->
    <div
      v-if="availableSpecifications && Object.keys(availableSpecifications).length > 0"
      class="specification-filters"
    >
      <div
        v-for="(values, attributeName) in availableSpecifications"
        :key="attributeName"
        class="spec-filter-group"
      >
        <span class="filter-label">{{ attributeName }}:</span>
        <el-checkbox-group
          v-model="selectedSpecifications[attributeName]"
          @change="handleSpecificationFilterChange"
        >
          <el-checkbox
            v-for="value in values"
            :key="value"
            :label="value"
            class="spec-checkbox"
          >
            {{ value }}
          </el-checkbox>
        </el-checkbox-group>
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

// 规格筛选相关
const selectedSpecifications = ref<Record<string, string[]>>({})

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

// 计算属性：所有商品的可用规格
const availableSpecifications = computed(() => {
  const specs: Record<string, string[]> = {}

  // 遍历所有商品，收集规格信息
  products.value.forEach(product => {
    const productSpecs = product.productData?.specifications
    if (productSpecs && typeof productSpecs === 'object') {
      Object.entries(productSpecs).forEach(([name, values]) => {
        if (Array.isArray(values)) {
          if (!specs[name]) {
            specs[name] = []
          }
          // 添加不重复的值
          values.forEach(value => {
            if (specs[name] && !specs[name]!.includes(value)) {
              specs[name]!.push(value)
            }
          })
        }
      })
    }
  })

  return specs
})

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

  // 规格筛选
  Object.entries(selectedSpecifications.value).forEach(([attributeName, selectedValues]) => {
    if (selectedValues.length > 0) {
      result = result.filter(product => {
        const productSpecs = product.productData?.specifications
        if (!productSpecs || typeof productSpecs !== 'object') return false

        const attributeValues = productSpecs[attributeName]
        if (!Array.isArray(attributeValues)) return false

        // 检查是否有匹配的值
        return selectedValues.some(selectedValue =>
          attributeValues.includes(selectedValue)
        )
      })
    }
  })

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
const mockProducts: Product[] = [
  {
    id: 1,
    productName: '时尚运动鞋 - 2024新款',
    description: '轻便透气，适合跑步和日常穿着，采用最新科技材料制作',
    price: 299.99,
    salesCount: 15,
    discount: 0,
    stockQuantity: 15,
    isAvailable: true,
    creatorId: 1,
    category: '鞋类',
    brand: '运动品牌',
    productData: {
      specifications: {
        '颜色': ['红色', '蓝色', '黑色', '白色'],
        '尺寸': ['36', '37', '38', '39', '40', '41', '42', '43', '44'],
        '材质': ['运动鞋专用材料', '透气网面']
      }
    },
    createdAt: '2024-01-15T10:00:00Z',
    updatedAt: '2024-01-15T10:00:00Z',
    images: [
      { id: 1, productId: 1, imageUrl: '/images/placeholder-product.png', isMain: true, orderIndex: 1 }
    ]
  },
  {
    id: 2,
    productName: '纯棉T恤',
    description: '100%纯棉材质，舒适透气，多色可选',
    price: 59.99,
    salesCount: 50,
    discount: 10,
    stockQuantity: 50,
    isAvailable: true,
    creatorId: 1,
    category: '服装',
    brand: '休闲品牌',
    productData: {
      specifications: {
        '颜色': ['白色', '黑色', '灰色', '蓝色', '红色'],
        '尺寸': ['S', 'M', 'L', 'XL', 'XXL'],
        '材质': ['纯棉']
      }
    },
    createdAt: '2024-01-10T15:30:00Z',
    updatedAt: '2024-01-10T15:30:00Z',
    images: [
      { id: 2, productId: 2, imageUrl: '/images/placeholder-product.png', isMain: true, orderIndex: 1 }
    ]
  },
  {
    id: 3,
    productName: '商务双肩包',
    description: '大容量设计，防水面料，适合商务和旅行使用',
    price: 199.99,
    salesCount: 25,
    discount: 5,
    stockQuantity: 0,
    isAvailable: false,
    creatorId: 1,
    category: '箱包',
    brand: '商务品牌',
    productData: {
      specifications: {
        '颜色': ['黑色', '灰色', '蓝色'],
        '尺寸': ['40L', '50L'],
        '材质': ['防水尼龙', '涤纶']
      }
    },
    createdAt: '2024-01-05T09:20:00Z',
    updatedAt: '2024-01-05T09:20:00Z',
    images: [
      { id: 3, productId: 3, imageUrl: '/images/placeholder-product.png', isMain: true, orderIndex: 1 }
    ]
  },
  {
    id: 4,
    productName: '智能手表',
    description: '多功能运动监测，心率监测，消息提醒，长续航',
    price: 899.99,
    salesCount: 8,
    discount: 0,
    stockQuantity: 8,
    isAvailable: true,
    creatorId: 1,
    category: '配饰',
    brand: '科技品牌',
    productData: {
      specifications: {
        '颜色': ['黑色', '银色', '金色'],
        '表带材质': ['硅胶', '金属', '皮革'],
        '屏幕尺寸': ['1.3英寸', '1.5英寸']
      }
    },
    createdAt: '2024-01-20T14:15:00Z',
    updatedAt: '2024-01-20T14:15:00Z',
    images: [
      { id: 4, productId: 4, imageUrl: '/images/placeholder-product.png', isMain: true, orderIndex: 1 }
    ]
  },
  {
    id: 5,
    productName: '连衣裙',
    description: '优雅设计，舒适面料，适合多种场合穿着',
    price: 159.99,
    salesCount: 25,
    discount: 15,
    stockQuantity: 25,
    isAvailable: true,
    creatorId: 1,
    category: '服装',
    brand: '时尚品牌',
    productData: {
      specifications: {
        '颜色': ['红色', '黑色', '蓝色', '白色'],
        '尺寸': ['S', 'M', 'L', 'XL'],
        '材质': ['雪纺', '棉质']
      }
    },
    createdAt: '2024-01-18T11:00:00Z',
    updatedAt: '2024-01-18T11:00:00Z',
    images: [
      { id: 5, productId: 5, imageUrl: '/images/placeholder-product.png', isMain: true, orderIndex: 1 }
    ]
  },
  {
    id: 6,
    productName: '无线耳机',
    description: '蓝牙5.0连接，降噪功能，长续航时间',
    price: 299.99,
    salesCount: 12,
    discount: 20,
    stockQuantity: 12,
    isAvailable: true,
    creatorId: 1,
    category: '配饰',
    brand: '音频品牌',
    productData: {
      specifications: {
        '颜色': ['黑色', '白色'],
        '连接方式': ['蓝牙5.0', '蓝牙5.2'],
        '续航时间': ['20小时', '30小时']
      }
    },
    createdAt: '2024-01-22T16:30:00Z',
    updatedAt: '2024-01-22T16:30:00Z',
    images: [
      { id: 6, productId: 6, imageUrl: '/images/placeholder-product.png', isMain: true, orderIndex: 1 }
    ]
  }
]

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

const handleSpecificationFilterChange = (): void => {
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
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    products.value = mockProducts
    totalProducts.value = mockProducts.length
  } catch (error) {
    ElMessage.error('加载商品数据失败')
    console.error('Load products error:', error)
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

/* 规格筛选器样式 */
.specification-filters {
  margin-bottom: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.spec-filter-group {
  margin-bottom: 12px;
}

.spec-filter-group:last-child {
  margin-bottom: 0;
}

.filter-label {
  display: inline-block;
  margin-right: 12px;
  font-weight: 500;
  color: #495057;
  min-width: 60px;
}

.spec-checkbox {
  margin-right: 16px;
  margin-bottom: 8px;
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
