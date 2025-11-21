<template>
  <div class="component-test">
    <!-- 页面标题 -->
    <div class="test-header">
      <h1>🧪 组件测试页面</h1>
      <p>这是一个用于测试所有可复用组件的页面，只能通过手动输入URL访问</p>
    </div>

    <!-- StatusTag 组件测试 -->
    <div class="test-section">
      <h2>📋 StatusTag 状态标签组件</h2>
      <div class="component-showcase">
        <div class="showcase-item">
          <h4>在售状态</h4>
          <StatusTag status="available" />
        </div>
        <div class="showcase-item">
          <h4>下架状态</h4>
          <StatusTag status="unavailable" />
        </div>
        <div class="showcase-item">
          <h4>缺货状态</h4>
          <StatusTag status="out_of_stock" />
        </div>
      </div>
    </div>

    <!-- PriceDisplay 组件测试 -->
    <div class="test-section">
      <h2>💰 PriceDisplay 价格显示组件</h2>
      <div class="component-showcase">
        <div class="showcase-item">
          <h4>标准价格</h4>
          <PriceDisplay :price="99.99" />
        </div>
        <div class="showcase-item">
          <h4>大尺寸</h4>
          <PriceDisplay :price="199.99" size="large" />
        </div>
        <div class="showcase-item">
          <h4>小尺寸</h4>
          <PriceDisplay :price="29.99" size="small" />
        </div>
        <div class="showcase-item">
          <h4>美元价格</h4>
          <PriceDisplay :price="49.99" currency="$" />
        </div>
      </div>
    </div>

    <!-- StockIndicator 组件测试 -->
    <div class="test-section">
      <h2>📦 StockIndicator 库存指示器组件</h2>
      <div class="component-showcase">
        <div class="showcase-item">
          <h4>库存充足</h4>
          <StockIndicator :stock-quantity="50" />
        </div>
        <div class="showcase-item">
          <h4>库存紧张</h4>
          <StockIndicator :stock-quantity="5" />
        </div>
        <div class="showcase-item">
          <h4>缺货</h4>
          <StockIndicator :stock-quantity="0" />
        </div>
        <div class="showcase-item">
          <h4>显示数量</h4>
          <StockIndicator :stock-quantity="15" :show-count="true" />
        </div>
      </div>
    </div>

    <!-- SearchBox 组件测试 -->
    <div class="test-section">
      <h2>🔍 SearchBox 搜索框组件</h2>
      <div class="component-showcase">
        <div class="showcase-item full-width">
          <h4>基础搜索框</h4>
          <SearchBox
            placeholder="搜索商品名称..."
            @search="handleSearch"
            @input="handleSearchInput"
          />
        </div>
        <div class="showcase-item full-width">
          <h4>带建议的搜索框</h4>
          <SearchBox
            placeholder="搜索..."
            :suggestions="searchSuggestions"
            :show-suggestions="true"
            @search="handleSearch"
            @suggestion-select="handleSuggestionSelect"
          />
        </div>
      </div>
    </div>

    <!-- PaginationBar 组件测试 -->
    <div class="test-section">
      <h2>📄 PaginationBar 分页器组件</h2>
      <div class="component-showcase">
        <div class="showcase-item full-width">
          <h4>标准分页器</h4>
          <PaginationBar
            :total="100"
            :current-page="currentPage"
            :page-size="10"
            @update:current-page="handlePageChange"
            @update:page-size="handlePageSizeChange"
          />
        </div>
        <div class="showcase-item full-width">
          <h4>小尺寸分页器</h4>
          <PaginationBar
            :total="50"
            :current-page="2"
            :page-size="5"
            :small="true"
            @update:current-page="handlePageChange"
          />
        </div>
      </div>
    </div>

    <!-- ProductCard 组件测试 -->
    <div class="test-section">
      <h2>🛍️ ProductCard 商品卡片组件</h2>
      <div class="component-showcase">
        <div class="showcase-item" v-for="product in testProducts" :key="product.id">
          <ProductCard
            :product="product"
            :can-edit="true"
            :can-delete="true"
            :show-actions="true"
            @click="handleProductClick"
            @edit="handleProductEdit"
            @delete="handleProductDelete"
            @add-to-cart="handleAddToCart"
          />
        </div>
      </div>
    </div>

    <!-- 交互结果展示 -->
    <div class="test-section">
      <h2>📊 交互日志</h2>
      <div class="interaction-log">
        <el-button
          type="primary"
          size="small"
          @click="clearLog"
        >
          清空日志
        </el-button>
        <div class="log-container">
          <div
            v-for="(log, index) in interactionLogs"
            :key="index"
            class="log-item"
          >
            <span class="log-time">{{ log.time }}</span>
            <span class="log-event">{{ log.event }}</span>
            <span class="log-detail">{{ log.detail }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import StatusTag from '@/components/StatusTag.vue'
import PriceDisplay from '@/components/PriceDisplay.vue'
import StockIndicator from '@/components/StockIndicator.vue'
import SearchBox from '@/components/SearchBox.vue'
import PaginationBar from '@/components/PaginationBar.vue'
import ProductCard from '@/components/ProductCard.vue'
import type { Product } from '@/types/product'

// 响应式数据
const currentPage = ref<number>(1)
const interactionLogs = ref<Array<{
  time: string
  event: string
  detail: string
}>>([])

// 搜索建议
const searchSuggestions = ref<string[]>([
  '运动鞋',
  '休闲裤',
  'T恤',
  '连衣裙',
  '背包',
  '手表',
  '手机壳',
  '耳机'
])

// 测试商品数据
const testProducts = ref<Product[]>([
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
    createdAt: '2024-01-15T10:00:00Z',
    updatedAt: '2024-01-15T10:00:00Z',
    images: [
      { id: 1, productId: 1, imageUrl: '/shoe1.jpg', isMain: true, orderIndex: 1 }
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
    createdAt: '2024-01-10T15:30:00Z',
    updatedAt: '2024-01-10T15:30:00Z',
    images: [
      { id: 2, productId: 2, imageUrl: '/tshirt1.jpg', isMain: true, orderIndex: 1 }
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
    createdAt: '2024-01-05T09:20:00Z',
    updatedAt: '2024-01-05T09:20:00Z',
    images: [
      { id: 3, productId: 3, imageUrl: '/backpack1.jpg', isMain: true, orderIndex: 1 }
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
    createdAt: '2024-01-20T14:15:00Z',
    updatedAt: '2024-01-20T14:15:00Z',
    images: [
      { id: 4, productId: 4, imageUrl: '/watch1.jpg', isMain: true, orderIndex: 1 }
    ]
  }
])

// 添加日志
const addLog = (event: string, detail: string): void => {
  const now = new Date()
  const timeString = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}:${now.getSeconds().toString().padStart(2, '0')}`

  interactionLogs.value.unshift({
    time: timeString,
    event,
    detail
  })

  // 保持最多50条日志
  if (interactionLogs.value.length > 50) {
    interactionLogs.value = interactionLogs.value.slice(0, 50)
  }
}

// 清空日志
const clearLog = (): void => {
  interactionLogs.value = []
}

// 搜索相关事件
const handleSearch = (query: string): void => {
  addLog('搜索', `关键词: "${query}"`)
}

const handleSearchInput = (query: string): void => {
  // 实时搜索输入事件
}

const handleSuggestionSelect = (suggestion: string): void => {
  addLog('选择建议', `建议: "${suggestion}"`)
}

// 分页相关事件
const handlePageChange = (page: number): void => {
  currentPage.value = page
  addLog('页面切换', `第 ${page} 页`)
}

const handlePageSizeChange = (size: number): void => {
  addLog('每页条数', `${size} 条/页`)
}

// 商品卡片相关事件
const handleProductClick = (product: Product): void => {
  addLog('商品点击', product.productName)
}

const handleProductEdit = (product: Product): void => {
  addLog('编辑商品', product.productName)
}

const handleProductDelete = (product: Product): void => {
  addLog('删除商品', product.productName)
}

const handleAddToCart = (product: Product): void => {
  addLog('加入购物车', product.productName)
}
</script>

<style scoped>
.component-test {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.test-header {
  text-align: center;
  margin-bottom: 40px;
  padding: 30px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.test-header h1 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 28px;
}

.test-header p {
  margin: 0;
  color: #606266;
  font-size: 16px;
}

.test-section {
  margin-bottom: 30px;
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.test-section h2 {
  margin: 0 0 20px 0;
  color: #303133;
  font-size: 20px;
  border-bottom: 2px solid #409eff;
  padding-bottom: 8px;
}

.component-showcase {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.showcase-item {
  padding: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  text-align: center;
}

.showcase-item.full-width {
  grid-column: 1 / -1;
  text-align: left;
}

.showcase-item h4 {
  margin: 0 0 10px 0;
  color: #606266;
  font-size: 14px;
  font-weight: 500;
}

.interaction-log {
  max-height: 400px;
}

.log-container {
  margin-top: 15px;
  max-height: 350px;
  overflow-y: auto;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 10px;
  background: #fafafa;
}

.log-item {
  display: flex;
  gap: 10px;
  padding: 5px 0;
  border-bottom: 1px solid #f0f0f0;
  font-size: 12px;
}

.log-item:last-child {
  border-bottom: none;
}

.log-time {
  color: #909399;
  min-width: 60px;
  font-family: monospace;
}

.log-event {
  color: #409eff;
  min-width: 80px;
  font-weight: 500;
}

.log-detail {
  color: #606266;
  flex: 1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .component-test {
    padding: 10px;
  }

  .test-header {
    padding: 20px;
  }

  .test-header h1 {
    font-size: 24px;
  }

  .test-header p {
    font-size: 14px;
  }

  .component-showcase {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .showcase-item {
    padding: 12px;
  }
}
</style>