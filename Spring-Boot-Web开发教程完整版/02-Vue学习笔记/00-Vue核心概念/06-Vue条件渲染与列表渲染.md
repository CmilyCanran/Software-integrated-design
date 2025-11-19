---
tags:
  - Vue条件渲染
  - 列表渲染
  - v-if
  - v-show
  - v-for
  - Vue3
created: 2025-11-18
modified: 2025-11-18
category: Vue核心概念
difficulty: beginner
---

# Vue条件渲染与列表渲染

> **学习目标**：掌握Vue的条件渲染和列表渲染机制，理解如何根据数据状态动态控制界面显示

## 🎯 条件渲染与列表渲染的意义

条件渲染和列表渲染是Vue中最常用的两种动态渲染方式：

**条件渲染**：
- 根据条件决定是否显示某个元素
- 优化用户体验，避免显示不相关的内容
- 节省资源，按需渲染

**列表渲染**：
- 将数组数据转换为DOM元素列表
- 自动处理数据变化，更新界面
- 支持复杂的列表操作和交互

---

## 🔄 条件渲染指令

### v-if、v-else-if、v-else

条件渲染会根据条件的真假，决定是否创建或销毁DOM元素：

```vue
<template>
  <div class="conditional-demo">
    <h3>用户登录状态</h3>

    <!-- v-if - 条件性渲染（完全销毁/创建） -->
    <div v-if="isLoggedIn" class="logged-in">
      <h2>欢迎回来，{{ userName }}!</h2>
      <p>您有 {{ unreadCount }} 条未读消息</p>
      <button @click="logout">退出登录</button>
    </div>

    <!-- v-else-if - 多条件判断 -->
    <div v-else-if="isLoggingIn" class="logging-in">
      <div class="loading-spinner"></div>
      <p>正在登录中...</p>
    </div>

    <!-- v-else - 默认条件 -->
    <div v-else class="not-logged-in">
      <h2>请登录</h2>
      <input v-model="loginForm.username" placeholder="用户名">
      <input v-model="loginForm.password" type="password" placeholder="密码">
      <button @click="login" :disabled="!canLogin">登录</button>
    </div>

    <!-- 复杂条件示例 -->
    <div class="user-status">
      <h3>用户状态详情</h3>

      <!-- VIP用户显示特权信息 -->
      <div v-if="userLevel === 'vip'" class="vip-info">
        <p>👑 VIP用户特权：</p>
        <ul>
          <li>无限下载</li>
          <li>专属客服</li>
          <li>提前体验新功能</li>
        </ul>
      </div>

      <!-- 普通用户显示升级提示 -->
      <div v-else-if="userLevel === 'regular'" class="regular-info">
        <p>您是普通用户</p>
        <button @click="showUpgradeDialog">升级为VIP</button>
      </div>

      <!-- 新用户显示引导 -->
      <div v-else class="new-user-info">
        <p>🎉 欢迎新用户！</p>
        <button @click="startTour">开始新手引导</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const isLoggedIn = ref(false)
const isLoggingIn = ref(false)
const userName = ref('')
const unreadCount = ref(3)
const userLevel = ref('new') // 'vip', 'regular', 'new'

const loginForm = ref({
  username: '',
  password: ''
})

const canLogin = computed(() => {
  return loginForm.value.username && loginForm.value.password
})

function login() {
  if (!canLogin.value) return

  isLoggingIn.value = true
  console.log('开始登录...')

  // 模拟登录过程
  setTimeout(() => {
    isLoggedIn.value = true
    isLoggingIn.value = false
    userName.value = loginForm.value.username
    userLevel.value = 'regular'
    loginForm.value = { username: '', password: '' }
    console.log('登录成功！')
  }, 2000)
}

function logout() {
  isLoggedIn.value = false
  userName.value = ''
  unreadCount.value = 0
  console.log('已退出登录')
}

function showUpgradeDialog() {
  alert('VIP功能即将上线！')
}

function startTour() {
  alert('新手引导即将开始！')
}
</script>

<style scoped>
.conditional-demo {
  max-width: 600px;
  margin: 20px auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.logged-in {
  background-color: #d4edda;
  padding: 15px;
  border-radius: 4px;
}

.logging-in {
  background-color: #fff3cd;
  padding: 15px;
  border-radius: 4px;
  text-align: center;
}

.not-logged-in {
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 4px;
}

.loading-spinner {
  border: 2px solid #f3f3f3;
  border-top: 2px solid #3498db;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  animation: spin 1s linear infinite;
  margin: 0 auto 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.user-status {
  margin-top: 20px;
  padding: 15px;
  background-color: #e9ecef;
  border-radius: 4px;
}

.vip-info {
  background-color: #fff3cd;
  padding: 15px;
  border-radius: 4px;
}

.regular-info, .new-user-info {
  background-color: #d1ecf1;
  padding: 15px;
  border-radius: 4px;
}

input {
  display: block;
  width: 100%;
  padding: 8px;
  margin: 5px 0 15px 0;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  background-color: #007bff;
  color: white;
  cursor: pointer;
}

button:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
}

button:hover:not(:disabled) {
  background-color: #0056b3;
}
</style>
```

### v-show vs v-if

理解两者的区别，选择合适的指令：

```vue
<template>
  <div class="comparison-demo">
    <h3>v-if vs v-show 对比</h3>

    <!-- 控制按钮 -->
    <div class="controls">
      <button @click="toggleIfContent">切换 v-if 内容</button>
      <button @click="toggleShowContent">切换 v-show 内容</button>
      <button @click="toggleBoth">同时切换</button>
    </div>

    <!-- v-if 示例 -->
    <div class="example-section">
      <h4>v-if 示例（条件渲染）</h4>
      <div v-if="showIfContent" class="content-box if-box">
        <p>这是通过 v-if 控制的内容</p>
        <p>每次切换都会创建/销毁 DOM 元素</p>
        <p>适合切换频率较低的场景</p>
        <input v-model="ifInput" placeholder="v-if 输入框">
      </div>
      <div v-else class="placeholder-box">
        <p>v-if 内容已销毁</p>
      </div>
    </div>

    <!-- v-show 示例 -->
    <div class="example-section">
      <h4>v-show 示例（显示/隐藏）</h4>
      <div v-show="showShowContent" class="content-box show-box">
        <p>这是通过 v-show 控制的内容</p>
        <p>始终存在于 DOM 中，只是通过 CSS display 控制</p>
        <p>适合频繁切换的场景</p>
        <input v-model="showInput" placeholder="v-show 输入框">
      </div>
      <div v-show="!showShowContent" class="placeholder-box">
        <p>v-show 内容已隐藏（display: none）</p>
      </div>
    </div>

    <!-- 性能对比 -->
    <div class="performance-info">
      <h4>性能对比</h4>
      <div class="comparison-table">
        <div class="table-row header">
          <div>特性</div>
          <div>v-if</div>
          <div>v-show</div>
        </div>
        <div class="table-row">
          <div>初始渲染</div>
          <div>条件为假时不渲染</div>
          <div>总是渲染</div>
        </div>
        <div class="table-row">
          <div>切换开销</div>
          <div>较高（创建/销毁）</div>
          <div>较低（CSS切换）</div>
        </div>
        <div class="table-row">
          <div>内存占用</div>
          <div>条件为假时为0</div>
          <div>始终占用</div>
        </div>
        <div class="table-row">
          <div>适用场景</div>
          <div>很少切换</div>
          <div>频繁切换</div>
        </div>
      </div>
    </div>

    <!-- 实际应用示例 -->
    <div class="real-world-example">
      <h4>实际应用示例</h4>

      <!-- 用户权限控制 - 使用 v-if -->
      <div v-if="userRole === 'admin'" class="admin-panel">
        <h5>管理员面板</h5>
        <button>删除用户</button>
        <button>系统设置</button>
        <button>查看日志</button>
      </div>

      <!-- 错误提示 - 使用 v-show -->
      <div v-show="hasError" class="error-message">
        <p>⚠️ {{ errorMessage }}</p>
        <button @click="clearError">×</button>
      </div>

      <!-- 加载状态 - 使用 v-if（避免不必要的渲染） -->
      <div v-if="isLoading" class="loading-overlay">
        <div class="spinner"></div>
        <p>正在加载...</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const showIfContent = ref(true)
const showShowContent = ref(true)
const ifInput = ref('')
const showInput = ref('')
const userRole = ref('user') // 'admin', 'user'
const hasError = ref(false)
const errorMessage = ref('网络连接失败')
const isLoading = ref(false)

function toggleIfContent() {
  showIfContent.value = !showIfContent.value
  console.log('v-if 切换，内容会被', showIfContent.value ? '创建' : '销毁')
}

function toggleShowContent() {
  showShowContent.value = !showShowContent.value
  console.log('v-show 切换，内容只是', showShowContent.value ? '显示' : '隐藏')
}

function toggleBoth() {
  toggleIfContent()
  toggleShowContent()
}

function clearError() {
  hasError.value = false
}

// 模拟加载状态
function simulateLoading() {
  isLoading.value = true
  setTimeout(() => {
    isLoading.value = false
  }, 2000)
}
</script>

<style scoped>
.comparison-demo {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}

.controls {
  margin-bottom: 20px;
}

.controls button {
  margin-right: 10px;
  margin-bottom: 10px;
}

.example-section {
  margin: 20px 0;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.content-box {
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.if-box {
  border-left: 4px solid #28a745;
}

.show-box {
  border-left: 4px solid #17a2b8;
}

.placeholder-box {
  padding: 15px;
  background-color: #e9ecef;
  border-radius: 4px;
  text-align: center;
  color: #6c757d;
}

.performance-info {
  margin: 20px 0;
  padding: 15px;
  background-color: #fff3cd;
  border-radius: 8px;
}

.comparison-table {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 1px;
  background-color: #dee2e6;
  border-radius: 4px;
  overflow: hidden;
}

.table-row {
  display: contents;
}

.table-row > div {
  padding: 10px;
  background-color: white;
}

.table-row.header > div {
  background-color: #007bff;
  color: white;
  font-weight: bold;
}

.real-world-example {
  margin-top: 20px;
  padding: 15px;
  background-color: #d1ecf1;
  border-radius: 8px;
}

.admin-panel {
  background-color: #f8d7da;
  padding: 15px;
  border-radius: 4px;
  margin: 10px 0;
}

.error-message {
  background-color: #f8d7da;
  color: #721c24;
  padding: 10px;
  border-radius: 4px;
  margin: 10px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  z-index: 1000;
}

.spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin-bottom: 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
```

---

## 📋 列表渲染指令

### v-for 数组遍历

循环渲染数组数据是Vue中常用的功能：

```vue
<template>
  <div class="list-rendering-demo">
    <h3>商品列表</h3>

    <!-- 添加新商品 -->
    <div class="add-product">
      <h4>添加商品</h4>
      <input v-model="newProduct.name" placeholder="商品名称">
      <input v-model.number="newProduct.price" type="number" placeholder="价格">
      <select v-model="newProduct.category">
        <option value="">选择分类</option>
        <option value="electronics">电子产品</option>
        <option value="clothing">服装</option>
        <option value="food">食品</option>
      </select>
      <button @click="addProduct" :disabled="!canAddProduct">添加商品</button>
    </div>

    <!-- 筛选和排序 -->
    <div class="filters">
      <h4>筛选和排序</h4>
      <input v-model="searchQuery" placeholder="搜索商品...">
      <select v-model="sortBy">
        <option value="name">按名称排序</option>
        <option value="price-asc">价格从低到高</option>
        <option value="price-desc">价格从高到低</option>
      </select>
      <select v-model="filterCategory">
        <option value="">所有分类</option>
        <option value="electronics">电子产品</option>
        <option value="clothing">服装</option>
        <option value="food">食品</option>
      </select>
    </div>

    <!-- 基本数组遍历 -->
    <div class="product-list">
      <h4>商品列表 ({{ filteredProducts.length }} 件)</h4>

      <!-- 使用 v-for 遍历数组 -->
      <div
        v-for="(product, index) in filteredProducts"
        :key="product.id"
        class="product-card"
        :class="{ 'out-of-stock': product.stock === 0 }"
      >
        <div class="product-header">
          <h5>{{ index + 1 }}. {{ product.name }}</h5>
          <span class="price">¥{{ product.price }}</span>
        </div>

        <div class="product-details">
          <p>分类: {{ getCategoryName(product.category) }}</p>
          <p>库存: {{ product.stock }} 件</p>
          <p>添加时间: {{ formatDate(product.addedAt) }}</p>
        </div>

        <div class="product-actions">
          <button
            @click="addToCart(product)"
            :disabled="product.stock === 0"
          >
            {{ product.stock === 0 ? '缺货' : '加入购物车' }}
          </button>
          <button @click="editProduct(product)" class="edit-btn">编辑</button>
          <button @click="removeProduct(product.id)" class="remove-btn">删除</button>
        </div>
      </div>

      <!-- 空状态显示 -->
      <div v-if="filteredProducts.length === 0" class="empty-state">
        <p>😢 没有找到符合条件的商品</p>
        <button @click="resetFilters">重置筛选条件</button>
      </div>
    </div>

    <!-- 购物车 -->
    <div class="shopping-cart">
      <h4>购物车 ({{ cart.length }} 件)</h4>
      <div v-for="(item, cartIndex) in cart" :key="'cart-' + item.id" class="cart-item">
        <span>{{ item.name }} × {{ item.quantity }}</span>
        <span>¥{{ item.price * item.quantity }}</span>
        <button @click="removeFromCart(cartIndex)">移除</button>
      </div>
      <div v-if="cart.length > 0" class="cart-total">
        <strong>总计: ¥{{ cartTotal }}</strong>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const products = ref([
  {
    id: 1,
    name: 'iPhone 15 Pro',
    price: 8999,
    category: 'electronics',
    stock: 10,
    addedAt: new Date('2024-01-15')
  },
  {
    id: 2,
    name: '纯棉T恤',
    price: 99,
    category: 'clothing',
    stock: 50,
    addedAt: new Date('2024-01-16')
  },
  {
    id: 3,
    name: '进口巧克力',
    price: 59,
    category: 'food',
    stock: 0,
    addedAt: new Date('2024-01-17')
  }
])

const newProduct = ref({
  name: '',
  price: null,
  category: ''
})

const searchQuery = ref('')
const sortBy = ref('name')
const filterCategory = ref('')
const cart = ref([])

// 计算属性：筛选和排序
const filteredProducts = computed(() => {
  let result = products.value

  // 搜索筛选
  if (searchQuery.value) {
    result = result.filter(product =>
      product.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }

  // 分类筛选
  if (filterCategory.value) {
    result = result.filter(product => product.category === filterCategory.value)
  }

  // 排序
  result.sort((a, b) => {
    switch (sortBy.value) {
      case 'name':
        return a.name.localeCompare(b.name)
      case 'price-asc':
        return a.price - b.price
      case 'price-desc':
        return b.price - a.price
      default:
        return 0
    }
  })

  return result
})

const canAddProduct = computed(() => {
  return newProduct.value.name &&
         newProduct.value.price > 0 &&
         newProduct.value.category
})

const cartTotal = computed(() => {
  return cart.value.reduce((total, item) => total + item.price * item.quantity, 0)
})

// 方法
function addProduct() {
  if (!canAddProduct.value) return

  const product = {
    id: Date.now(),
    ...newProduct.value,
    stock: 100,
    addedAt: new Date()
  }

  products.value.push(product)
  newProduct.value = { name: '', price: null, category: '' }
}

function removeProduct(id) {
  const index = products.value.findIndex(p => p.id === id)
  if (index > -1) {
    products.value.splice(index, 1)
  }
}

function editProduct(product) {
  const newName = prompt('编辑商品名称:', product.name)
  if (newName && newName.trim()) {
    product.name = newName.trim()
  }
}

function addToCart(product) {
  if (product.stock === 0) return

  const existingItem = cart.value.find(item => item.id === product.id)
  if (existingItem) {
    existingItem.quantity++
  } else {
    cart.value.push({
      id: product.id,
      name: product.name,
      price: product.price,
      quantity: 1
    })
  }

  product.stock--
}

function removeFromCart(index) {
  const item = cart.value[index]
  const product = products.value.find(p => p.id === item.id)
  if (product) {
    product.stock += item.quantity
  }
  cart.value.splice(index, 1)
}

function getCategoryName(category) {
  const names = {
    electronics: '电子产品',
    clothing: '服装',
    food: '食品'
  }
  return names[category] || category
}

function formatDate(date) {
  return date.toLocaleDateString('zh-CN')
}

function resetFilters() {
  searchQuery.value = ''
  sortBy.value = 'name'
  filterCategory.value = ''
}
</script>

<style scoped>
.list-rendering-demo {
  max-width: 1000px;
  margin: 20px auto;
  padding: 20px;
}

.add-product, .filters {
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.add-product input, .filters input, .filters select {
  margin: 5px;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.product-list {
  margin-bottom: 20px;
}

.product-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
  transition: transform 0.2s;
}

.product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.product-card.out-of-stock {
  opacity: 0.6;
  background-color: #f8f9fa;
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.price {
  font-size: 18px;
  font-weight: bold;
  color: #e74c3c;
}

.product-details {
  margin-bottom: 15px;
  color: #666;
}

.product-actions {
  display: flex;
  gap: 10px;
}

.product-actions button {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.product-actions button:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
}

.edit-btn {
  background-color: #ffc107;
  color: #212529;
}

.remove-btn {
  background-color: #dc3545;
  color: white;
}

.empty-state {
  text-align: center;
  padding: 40px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.shopping-cart {
  background-color: #e9ecef;
  padding: 15px;
  border-radius: 8px;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
  border-bottom: 1px solid #dee2e6;
}

.cart-total {
  margin-top: 15px;
  text-align: right;
  font-size: 18px;
}
</style>
```

### v-for 对象遍历

遍历对象的属性和值：

```vue
<template>
  <div class="object-rendering-demo">
    <h3>用户信息展示</h3>

    <!-- 基本对象遍历 -->
    <div class="user-profile">
      <h4>基本信息</h4>
      <div v-for="(value, key, index) in userInfo" :key="key" class="info-item">
        <span class="info-index">{{ index + 1 }}.</span>
        <span class="info-key">{{ formatKey(key) }}:</span>
        <span class="info-value">{{ formatValue(value) }}</span>
      </div>
    </div>

    <!-- 嵌套对象遍历 -->
    <div class="user-details">
      <h4>详细信息</h4>
      <div v-for="(section, sectionName) in userDetails" :key="sectionName" class="detail-section">
        <h5>{{ formatSectionName(sectionName) }}</h5>
        <div v-for="(value, key) in section" :key="key" class="detail-item">
          <span class="detail-key">{{ formatKey(key) }}:</span>
          <span class="detail-value">{{ formatValue(value) }}</span>
        </div>
      </div>
    </div>

    <!-- 动态表单生成 -->
    <div class="dynamic-form">
      <h4>动态表单</h4>
      <form @submit.prevent="submitForm">
        <div v-for="(field, fieldName) in formFields" :key="fieldName" class="form-field">
          <label :for="fieldName">{{ field.label }}:</label>

          <!-- 根据字段类型动态生成输入控件 -->
          <input
            v-if="field.type === 'text'"
            :type="field.type"
            :id="fieldName"
            v-model="formData[fieldName]"
            :placeholder="field.placeholder"
            :required="field.required"
          >

          <input
            v-else-if="field.type === 'number'"
            :type="field.type"
            :id="fieldName"
            v-model.number="formData[fieldName]"
            :placeholder="field.placeholder"
            :min="field.min"
            :max="field.max"
          >

          <select
            v-else-if="field.type === 'select'"
            :id="fieldName"
            v-model="formData[fieldName]"
            :required="field.required"
          >
            <option value="">请选择...</option>
            <option v-for="option in field.options" :key="option.value" :value="option.value">
              {{ option.label }}
            </option>
          </select>

          <textarea
            v-else-if="field.type === 'textarea'"
            :id="fieldName"
            v-model="formData[fieldName]"
            :placeholder="field.placeholder"
            :rows="field.rows"
          ></textarea>
        </div>

        <button type="submit">提交表单</button>
      </form>
    </div>

    <!-- 配置对象遍历 -->
    <div class="config-display">
      <h4>系统配置</h4>
      <div class="config-grid">
        <div v-for="(config, category) in systemConfig" :key="category" class="config-category">
          <h5>{{ formatKey(category) }}</h5>
          <div v-for="(value, key) in config" :key="key" class="config-item">
            <span class="config-key">{{ formatKey(key) }}:</span>
            <span class="config-value" :class="getConfigValueClass(value)">
              {{ formatConfigValue(value) }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'

// 用户基本信息
const userInfo = reactive({
  name: '张三',
  age: 28,
  gender: 'male',
  email: 'zhangsan@example.com',
  phone: '13800138000',
  isActive: true,
  registeredAt: new Date('2020-01-15'),
  lastLogin: new Date()
})

// 用户详细信息（嵌套对象）
const userDetails = reactive({
  personal: {
    firstName: '三',
    lastName: '张',
    birthday: '1996-05-15',
    address: '北京市朝阳区',
    zipCode: '100000'
  },
  professional: {
    company: '科技有限公司',
    position: '前端工程师',
    department: '技术部',
    experience: 5
  },
  preferences: {
    language: 'zh-CN',
    timezone: 'Asia/Shanghai',
    theme: 'dark',
    notifications: true
  }
})

// 动态表单字段配置
const formFields = reactive({
  username: {
    label: '用户名',
    type: 'text',
    placeholder: '请输入用户名',
    required: true
  },
  age: {
    label: '年龄',
    type: 'number',
    placeholder: '请输入年龄',
    min: 1,
    max: 120,
    required: true
  },
  city: {
    label: '城市',
    type: 'select',
    required: true,
    options: [
      { value: 'beijing', label: '北京' },
      { value: 'shanghai', label: '上海' },
      { value: 'guangzhou', label: '广州' },
      { value: 'shenzhen', label: '深圳' }
    ]
  },
  bio: {
    label: '个人简介',
    type: 'textarea',
    placeholder: '请输入个人简介',
    rows: 4
  }
})

const formData = reactive({
  username: '',
  age: null,
  city: '',
  bio: ''
})

// 系统配置
const systemConfig = reactive({
  database: {
    host: 'localhost',
    port: 3306,
    username: 'admin',
    sslEnabled: true,
    maxConnections: 100
  },
  server: {
    host: '0.0.0.0',
    port: 8080,
    environment: 'production',
    debugMode: false,
    corsEnabled: true
  },
  features: {
    userAuth: true,
    fileUpload: true,
    notifications: false,
    analytics: true,
    logging: true
  }
})

// 方法
function formatKey(key) {
  // 将驼峰命名转换为中文显示
  const keyMap = {
    name: '姓名',
    age: '年龄',
    gender: '性别',
    email: '邮箱',
    phone: '电话',
    isActive: '是否激活',
    registeredAt: '注册时间',
    lastLogin: '最后登录',
    firstName: '名',
    lastName: '姓',
    birthday: '生日',
    address: '地址',
    zipCode: '邮编',
    company: '公司',
    position: '职位',
    department: '部门',
    experience: '工作经验',
    language: '语言',
    timezone: '时区',
    theme: '主题',
    notifications: '通知'
  }
  return keyMap[key] || key
}

function formatSectionName(sectionName) {
  const sectionMap = {
    personal: '个人信息',
    professional: '职业信息',
    preferences: '偏好设置'
  }
  return sectionMap[sectionName] || sectionName
}

function formatValue(value) {
  if (value instanceof Date) {
    return value.toLocaleString('zh-CN')
  }
  if (typeof value === 'boolean') {
    return value ? '是' : '否'
  }
  if (typeof value === 'number') {
    return value.toString()
  }
  return value || '未设置'
}

function formatConfigValue(value) {
  if (typeof value === 'boolean') {
    return value ? '启用' : '禁用'
  }
  return value.toString()
}

function getConfigValueClass(value) {
  if (typeof value === 'boolean') {
    return value ? 'value-enabled' : 'value-disabled'
  }
  if (typeof value === 'number') {
    return 'value-number'
  }
  return 'value-string'
}

function submitForm() {
  console.log('表单数据:', formData)
  alert('表单提交成功！')
}
</script>

<style scoped>
.object-rendering-demo {
  max-width: 1000px;
  margin: 20px auto;
  padding: 20px;
}

.user-profile, .user-details, .dynamic-form, .config-display {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.info-item, .detail-item, .config-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.info-index {
  width: 30px;
  color: #666;
  font-weight: bold;
}

.info-key, .detail-key, .config-key {
  width: 120px;
  font-weight: bold;
  color: #333;
}

.info-value, .detail-value, .config-value {
  flex: 1;
  color: #555;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h5 {
  color: #007bff;
  margin-bottom: 10px;
}

.form-field {
  margin-bottom: 15px;
}

.form-field label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-field input,
.form-field select,
.form-field textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.config-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.config-category {
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
}

.config-category h5 {
  margin-top: 0;
  color: #495057;
}

.value-enabled {
  color: #28a745;
  font-weight: bold;
}

.value-disabled {
  color: #dc3545;
  font-weight: bold;
}

.value-number {
  color: #007bff;
  font-family: monospace;
}

.value-string {
  color: #6c757d;
}

button[type="submit"] {
  background-color: #007bff;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

button[type="submit"]:hover {
  background-color: #0056b3;
}
</style>
```

### v-for 数字遍历

```vue
<template>
  <div class="number-rendering-demo">
    <h3>数字遍历示例</h3>

    <!-- 基本数字遍历 -->
    <div class="basic-number">
      <h4>基本数字遍历</h4>
      <div v-for="n in 5" :key="n" class="number-item">
        第 {{ n }} 个项目
      </div>
    </div>

    <!-- 动态数字范围 -->
    <div class="dynamic-range">
      <h4>动态数字范围</h4>
      <div class="controls">
        <label>起始数字:</label>
        <input v-model.number="startNum" type="number" min="1" max="20">
        <label>结束数字:</label>
        <input v-model.number="endNum" type="number" min="1" max="20">
      </div>

      <div class="number-grid">
        <div
          v-for="n in (endNum - startNum + 1)"
          :key="n"
          class="grid-item"
          :style="{ backgroundColor: getColorForNumber(startNum + n - 1) }"
        >
          {{ startNum + n - 1 }}
        </div>
      </div>
    </div>

    <!-- 分页组件 -->
    <div class="pagination">
      <h4>分页组件</h4>
      <div class="pagination-controls">
        <button @click="prevPage" :disabled="currentPage === 1">上一页</button>

        <div class="page-numbers">
          <button
            v-for="page in totalPages"
            :key="page"
            @click="goToPage(page)"
            :class="{ active: page === currentPage }"
          >
            {{ page }}
          </button>
        </div>

        <button @click="nextPage" :disabled="currentPage === totalPages">下一页</button>
      </div>

      <div class="page-content">
        <p>当前显示第 {{ currentPage }} 页，共 {{ totalPages }} 页</p>
        <div v-for="item in currentPageItems" :key="item.id" class="content-item">
          {{ item.text }}
        </div>
      </div>
    </div>

    <!-- 进度条 -->
    <div class="progress-demo">
      <h4>进度条</h4>
      <div class="progress-bar">
        <div
          v-for="step in totalSteps"
          :key="step"
          class="progress-step"
          :class="{
            completed: step <= currentStep,
            current: step === currentStep
          }"
        >
          <div class="step-number">{{ step }}</div>
          <div class="step-label">步骤{{ step }}</div>
        </div>
      </div>

      <div class="progress-controls">
        <button @click="prevStep" :disabled="currentStep === 1">上一步</button>
        <button @click="nextStep" :disabled="currentStep === totalSteps">下一步</button>
        <button @click="resetProgress">重置</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const startNum = ref(1)
const endNum = ref(10)

// 分页相关
const currentPage = ref(1)
const itemsPerPage = ref(5)
const totalItems = ref(23) // 模拟总数据量

const totalPages = computed(() => Math.ceil(totalItems.value / itemsPerPage.value))

const currentPageItems = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value
  const end = start + itemsPerPage.value

  return Array.from({ length: Math.min(itemsPerPage.value, totalItems.value - start) }, (_, i) => ({
    id: start + i + 1,
    text: `这是第 ${start + i + 1} 条内容`
  }))
})

// 进度条相关
const currentStep = ref(1)
const totalSteps = ref(5)

// 方法
function getColorForNumber(num) {
  const hue = (num * 36) % 360
  return `hsl(${hue}, 70%, 60%)`
}

function prevPage() {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

function nextPage() {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

function goToPage(page) {
  currentPage.value = page
}

function prevStep() {
  if (currentStep.value > 1) {
    currentStep.value--
  }
}

function nextStep() {
  if (currentStep.value < totalSteps.value) {
    currentStep.value++
  }
}

function resetProgress() {
  currentStep.value = 1
}
</script>

<style scoped>
.number-rendering-demo {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}

.basic-number, .dynamic-range, .pagination, .progress-demo {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.number-item {
  padding: 10px;
  margin: 5px 0;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.controls {
  margin-bottom: 20px;
}

.controls label {
  margin-right: 10px;
}

.controls input {
  margin-right: 20px;
  padding: 5px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.number-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(40px, 1fr));
  gap: 5px;
}

.grid-item {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  border-radius: 4px;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.page-numbers {
  display: flex;
  gap: 5px;
}

.page-numbers button {
  width: 35px;
  height: 35px;
  border: 1px solid #ddd;
  background-color: white;
  cursor: pointer;
  border-radius: 4px;
}

.page-numbers button.active {
  background-color: #007bff;
  color: white;
  border-color: #007bff;
}

.page-content {
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.content-item {
  padding: 8px;
  margin: 5px 0;
  background-color: white;
  border-radius: 4px;
}

.progress-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  position: relative;
}

.progress-bar::before {
  content: '';
  position: absolute;
  top: 20px;
  left: 20px;
  right: 20px;
  height: 2px;
  background-color: #dee2e6;
  z-index: 1;
}

.progress-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  z-index: 2;
}

.step-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #dee2e6;
  color: #6c757d;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-bottom: 5px;
}

.progress-step.completed .step-number {
  background-color: #28a745;
  color: white;
}

.progress-step.current .step-number {
  background-color: #007bff;
  color: white;
  box-shadow: 0 0 0 4px rgba(0, 123, 255, 0.2);
}

.step-label {
  font-size: 12px;
  color: #6c757d;
}

.progress-step.completed .step-label,
.progress-step.current .step-label {
  color: #333;
  font-weight: bold;
}

.progress-controls {
  display: flex;
  gap: 10px;
}

button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background-color: white;
  cursor: pointer;
  border-radius: 4px;
}

button:hover:not(:disabled) {
  background-color: #f8f9fa;
}

button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
```

---

## 🎯 最佳实践与注意事项

### ✅ 推荐做法

1. **正确的key使用**
```vue
<!-- ✅ 使用唯一的稳定值作为key -->
<li v-for="user in users" :key="user.id">

<!-- ✅ 当没有唯一ID时，使用内容哈希 -->
<li v-for="item in items" :key="item.text + item.date">

<!-- ❌ 不要使用index作为key（如果列表会变化） -->
<li v-for="(user, index) in users" :key="index">
```

2. **v-if vs v-show选择**
```vue
<!-- ✅ 频繁切换用v-show -->
<div v-show="isVisible">经常显示隐藏的内容</div>

<!-- ✅ 很少切换用v-if -->
<heavy-component v-if="showComponent" />
```

3. **避免v-if和v-for同时使用**
```vue
<!-- ❌ 不推荐 -->
<li v-for="user in users" v-if="user.isActive" :key="user.id">

<!-- ✅ 推荐：使用计算属性 -->
<li v-for="user in activeUsers" :key="user.id">
```

### ❌ 避免做法

1. **不要在同一个元素上同时使用v-if和v-for**
2. **不要在v-for中使用非原始类型的key**
3. **避免在模板中写复杂的条件判断**

---

## 📋 指令速查表

| 指令 | 作用 | 使用场景 | 特点 |
|------|------|----------|------|
| **v-if** | 条件渲染 | 很少切换的场景 | 完全创建/销毁DOM |
| **v-else-if** | 多条件判断 | 复杂条件分支 | 必须跟在v-if后面 |
| **v-else** | 默认条件 | 条件分支的默认项 | 必须跟在v-if/v-else-if后面 |
| **v-show** | 显示隐藏 | 频繁切换的场景 | 基于CSS display |
| **v-for** | 列表渲染 | 数组、对象、数字遍历 | 必须指定key |

---

## 🚀 下一步学习

掌握条件渲染和列表渲染后，继续学习：

- [[02-Vue核心概念/07-Vue事件处理与表单绑定.md|事件处理与表单绑定]]
- [[02-Vue核心概念/08-Vue指令与CompositionAPI体系概览.md|指令与CompositionAPI体系]]
- [[02-Vue核心概念/04-Vue计算属性与侦听器.md|计算属性与侦听器]]

---

**记住：条件渲染和列表渲染是构建动态界面的核心，掌握它们就能创建出丰富多样的用户界面！** 🎉