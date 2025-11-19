---
tags:
  - Vue计算属性
  - computed
  - 侦听器
  - watch
  - watchEffect
  - 性能优化
  - Vue3
created: 2025-11-18
modified: 2025-11-18
category: Vue核心概念
difficulty: beginner
---

# Vue计算属性与侦听器

> **学习目标**：掌握Vue计算属性的使用方法，理解计算属性的优势，学会使用侦听器处理副作用

## 🎯 本章概览

| 内容 | 预计时间 | 难度 | 状态 |
|------|----------|------|------|
| 计算属性基础 | 15分钟 | ⭐⭐ | ⏳ |
| 计算属性vs方法 | 10分钟 | ⭐⭐ | ⏳ |
| 计算属性vs侦听器 | 15分钟 | ⭐⭐⭐ | ⏳ |
| 高级计算属性 | 20分钟 | ⭐⭐⭐ | ⏳ |
| 实践练习 | 20分钟 | ⭐⭐⭐ | ⏳ |

---

## 🧮 什么是计算属性？

### 计算属性的定义

**计算属性** = **基于其他数据计算得出的属性**

就像数学中的函数：`y = f(x)`，其中 `y` 是计算属性，`x` 是响应式数据。

```javascript
// 基础数据
const firstName = ref('张')
const lastName = ref('三')

// 计算属性
const fullName = computed(() => {
  return firstName.value + lastName.value
})
```

### 为什么需要计算属性？

**没有计算属性的问题：**
```vue
<template>
  <!-- 在模板中写复杂逻辑 -->
  <div>
    {{ firstName + ' ' + lastName + ' (' + age.value + '岁)' }}
  </div>

  <!-- 多处使用需要重复写逻辑 -->
  <div>
    {{ firstName + ' ' + lastName + ' (' + age.value + '岁)' }}
  </div>

  <!-- 又一处使用 -->
  <div>
    {{ firstName + ' ' + lastName + ' (' + age.value + '岁)' }}
  </div>
</template>
```

**使用计算属性的优势：**
```vue
<template>
  <!-- 清晰简洁 -->
  <div>{{ userInfo }}</div>
  <div>{{ userInfo }}</div>
  <div>{{ userInfo }}</div>
</template>

<script setup>
const userInfo = computed(() => {
  return `${firstName.value} ${lastName.value} (${age.value}岁)`
})
</script>
```

---

## ⚡ 计算属性的核心特性

### 1. 缓存机制

计算属性**基于依赖进行缓存**：

```vue
<!-- ComputedCache.vue -->
<template>
  <div class="computed-cache">
    <h2>计算属性缓存演示</h2>

    <div class="inputs">
      <input v-model="message" placeholder="输入消息">
      <input v-model.number="repeat" type="number" placeholder="重复次数" min="1" max="5">
    </div>

    <div class="output">
      <p><strong>重复消息：</strong>{{ repeatedMessage }}</p>
      <p><strong>消息长度：</strong>{{ messageLength }}</p>
      <p><strong>计算次数：</strong>{{ calculationCount }}</p>
    </div>

    <div class="logs">
      <h3>计算日志：</h3>
      <ul>
        <li v-for="(log, index) in logs" :key="index">
          {{ log }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const message = ref('Hello')
const repeat = ref(2)
const calculationCount = ref(0)
const logs = ref([])

// 计算属性：重复消息
const repeatedMessage = computed(() => {
  calculationCount.value++
  logs.value.unshift(`计算 repeatedMessage: "${message.value}" x${repeat.value}`)

  return message.value.repeat(repeat.value)
})

// 计算属性：消息长度
const messageLength = computed(() => {
  // 注意：这里也会触发计算，因为有独立的依赖
  return message.value.length
})
</script>

<style scoped>
.computed-cache {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  max-width: 600px;
}

.inputs {
  margin: 15px 0;
  display: flex;
  gap: 10px;
}

.inputs input {
  flex: 1;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.output {
  margin: 20px 0;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.logs {
  margin-top: 20px;
}

.logs h3 {
  margin-bottom: 10px;
}

.logs ul {
  list-style: none;
  padding: 0;
  max-height: 200px;
  overflow-y: auto;
}

.logs li {
  padding: 4px 0;
  font-size: 14px;
  color: #666;
  border-bottom: 1px solid #eee;
}
</style>
```

### 2. 只读计算属性

```javascript
// 只读计算属性
const fullName = computed(() => {
  return firstName.value + ' ' + lastName.value
})

// ❌ 尝试修改只读计算属性会报错
// fullName.value = '新名称'  // 错误！
```

### 3. 可写计算属性

```vue
<!-- WritableComputed.vue -->
<template>
  <div class="writable-computed">
    <h2>可写计算属性演示</h2>

    <div class="form">
      <div class="input-group">
        <label>姓氏：</label>
        <input v-model="lastName" placeholder="输入姓氏">
      </div>
      <div class="input-group">
        <label>名字：</label>
        <input v-model="firstName" placeholder="输入名字">
      </div>
      <div class="input-group">
        <label>全名：</label>
        <input v-model="fullName" placeholder="输入全名">
      </div>
    </div>

    <div class="result">
      <p><strong>firstName:</strong> {{ firstName }}</p>
      <p><strong>lastName:</strong> {{ lastName }}</p>
      <p><strong>fullName:</strong> {{ fullName }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const firstName = ref('三')
const lastName = ref('张')

// 可写计算属性
const fullName = computed({
  // getter：读取时调用
  get() {
    return lastName.value + firstName.value
  },

  // setter：写入时调用
  set(newValue) {
    console.log('设置新值:', newValue)

    // 简单的分割逻辑
    if (typeof newValue === 'string' && newValue.length >= 2) {
      lastName.value = newValue.substring(0, 1)
      firstName.value = newValue.substring(1)
    }
  }
})
</script>

<style scoped>
.writable-computed {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  max-width: 500px;
}

.form {
  margin: 20px 0;
}

.input-group {
  margin: 10px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.input-group label {
  min-width: 60px;
  font-weight: bold;
}

.input-group input {
  flex: 1;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.result {
  margin-top: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.result p {
  margin: 5px 0;
}
</style>
```

---

## 🆚 计算属性 vs 方法

### 对比演示

```vue
<!-- ComputedVsMethod.vue -->
<template>
  <div class="computed-vs-method">
    <h2>计算属性 vs 方法对比</h2>

    <div class="controls">
      <input v-model="message" placeholder="输入消息">
      <button @click="increment">增加计数: {{ count }}</button>
    </div>

    <div class="comparison">
      <div class="section">
        <h3>使用计算属性</h3>
        <p><strong>结果：</strong>{{ computedResult }}</p>
        <p><strong>计算次数：</strong>{{ computedCount }}</p>
      </div>

      <div class="section">
        <h3>使用方法</h3>
        <p><strong>结果：</strong>{{ methodResult() }}</p>
        <p><strong>调用次数：</strong>{{ methodCount }}</p>
      </div>
    </div>

    <div class="explanation">
      <h3>区别说明：</h3>
      <ul>
        <li><strong>计算属性</strong>：基于依赖缓存，只有依赖变化时才重新计算</li>
        <li><strong>方法</strong>：每次重新渲染都会调用</li>
        <li>修改消息时，两者都会重新计算</li>
        <li>点击增加计数时，只有方法会重新调用（因为重新渲染）</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const message = ref('Hello')
const count = ref(0)
const computedCount = ref(0)
const methodCount = ref(0)

// 计算属性
const computedResult = computed(() => {
  computedCount.value++
  return message.value.toUpperCase() + ' (计算 #' + computedCount.value + ')'
})

// 方法
function methodResult() {
  methodCount.value++
  return message.value.toUpperCase() + ' (调用 #' + methodCount.value + ')'
}

function increment() {
  count.value++
}
</script>

<style scoped>
.computed-vs-method {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  max-width: 700px;
}

.controls {
  margin: 15px 0;
  display: flex;
  gap: 10px;
  align-items: center;
}

.controls input {
  flex: 1;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.controls button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  background-color: #42b883;
  color: white;
  cursor: pointer;
}

.comparison {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin: 20px 0;
}

.section {
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 4px;
}

.section h3 {
  margin-top: 0;
  color: #42b883;
}

.explanation {
  margin-top: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.explanation ul {
  margin: 10px 0;
  padding-left: 20px;
}

.explanation li {
  margin: 5px 0;
}
</style>
```

### 使用场景建议

| 场景 | 推荐使用 | 原因 |
|------|----------|------|
| **数据转换和格式化** | 计算属性 | 自动缓存，性能更好 |
| **复杂业务逻辑** | 计算属性 | 代码更清晰，可复用 |
| **需要实时计算** | 计算属性 | 响应式更新 |
| **主动触发操作** | 方法 | 需要调用才执行 |
| **有副作用操作** | 方法 | 计算属性应该是纯函数 |

---

## 👂 侦听器进阶

### watch vs watchEffect 对比

```vue
<!-- WatchComparison.vue -->
<template>
  <div class="watch-comparison">
    <h2>watch vs watchEffect 对比</h2>

    <div class="inputs">
      <div class="input-group">
        <label>用户名：</label>
        <input v-model="user.name" placeholder="输入用户名">
      </div>
      <div class="input-group">
        <label>年龄：</label>
        <input v-model.number="user.age" type="number" placeholder="输入年龄">
      </div>
      <div class="input-group">
        <label>搜索：</label>
        <input v-model="searchQuery" placeholder="输入搜索内容">
      </div>
    </div>

    <div class="outputs">
      <div class="section">
        <h3>watch 监听特定数据</h3>
        <ul>
          <li v-for="(log, index) in watchLogs" :key="index">{{ log }}</li>
        </ul>
      </div>

      <div class="section">
        <h3>watchEffect 自动收集依赖</h3>
        <ul>
          <li v-for="(log, index) in effectLogs" :key="index">{{ log }}</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, watchEffect } from 'vue'

const user = reactive({
  name: '张三',
  age: 25
})

const searchQuery = ref('')
const watchLogs = ref([])
const effectLogs = ref([])

// 1. watch - 明确监听特定数据
watch(() => user.name, (newName, oldName) => {
  watchLogs.value.unshift(`watch: 用户名变化 ${oldName} → ${newName}`)
})

watch(() => user.age, (newAge, oldAge) => {
  watchLogs.value.unshift(`watch: 年龄变化 ${oldAge} → ${newAge}`)
})

// 2. watch - 监听多个数据
watch([() => user.name, () => user.age], ([newName, newAge], [oldName, oldAge]) => {
  watchLogs.value.unshift(`watch: 用户信息变化 ${oldName}(${oldAge}) → ${newName}(${newAge})`)
})

// 3. watchEffect - 自动收集依赖
watchEffect(() => {
  // 自动监听 user.name, user.age, searchQuery
  if (user.name && user.age) {
    effectLogs.value.unshift(`watchEffect: ${user.name} (${user.age}岁) 在搜索 "${searchQuery}"`)
  }
})

// 4. watchEffect 的清理副作用
watchEffect((onCleanup) => {
  if (searchQuery.value) {
    const timer = setTimeout(() => {
      effectLogs.value.unshift(`watchEffect: 搜索 "${searchQuery.value}" 完成`)
    }, 1000)

    // 清理定时器
    onCleanup(() => {
      clearTimeout(timer)
    })
  }
})
</script>

<style scoped>
.watch-comparison {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  max-width: 800px;
}

.inputs {
  margin: 20px 0;
}

.input-group {
  margin: 10px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.input-group label {
  min-width: 80px;
  font-weight: bold;
}

.input-group input {
  flex: 1;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.outputs {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 20px;
}

.section {
  border: 1px solid #eee;
  border-radius: 4px;
  padding: 15px;
}

.section h3 {
  margin-top: 0;
  color: #42b883;
}

.section ul {
  list-style: none;
  padding: 0;
  max-height: 200px;
  overflow-y: auto;
}

.section li {
  padding: 4px 0;
  font-size: 14px;
  color: #666;
  border-bottom: 1px solid #eee;
}
</style>
```

---

## 🎯 实践练习：购物车计算

### 练习目标
创建一个购物车，使用计算属性处理价格计算，使用侦听器处理库存检查。

```vue
<!-- ShoppingCart.vue -->
<template>
  <div class="shopping-cart">
    <h2>购物车计算练习</h2>

    <div class="products">
      <h3>商品列表</h3>
      <div class="product-list">
        <div
          v-for="product in products"
          :key="product.id"
          class="product-item"
        >
          <div class="product-info">
            <h4>{{ product.name }}</h4>
            <p>单价：¥{{ product.price }}</p>
            <p :class="{ 'low-stock': product.stock <= 5 }">
              库存：{{ product.stock }}
            </p>
          </div>
          <div class="product-actions">
            <button
              @click="addToCart(product)"
              :disabled="product.stock <= 0"
            >
              加入购物车
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="cart">
      <h3>购物车</h3>
      <div v-if="cartItems.length === 0" class="empty-cart">
        购物车是空的
      </div>
      <div v-else>
        <div class="cart-items">
          <div
            v-for="item in cartItems"
            :key="item.id"
            class="cart-item"
          >
            <span class="item-name">{{ item.name }}</span>
            <div class="item-controls">
              <button @click="updateQuantity(item.id, item.quantity - 1)">-</button>
              <span>{{ item.quantity }}</span>
              <button @click="updateQuantity(item.id, item.quantity + 1)">+</button>
            </div>
            <span class="item-total">¥{{ item.total }}</span>
          </div>
        </div>

        <div class="cart-summary">
          <div class="summary-row">
            <span>商品数量：</span>
            <span>{{ totalItems }}</span>
          </div>
          <div class="summary-row">
            <span>商品总价：</span>
            <span>¥{{ subtotal }}</span>
          </div>
          <div class="summary-row">
            <span>运费：</span>
            <span>{{ shippingFee > 0 ? `¥${shippingFee}` : '免费' }}</span>
          </div>
          <div class="summary-row discount" v-if="discount > 0">
            <span>优惠：</span>
            <span>-¥{{ discount }}</span>
          </div>
          <div class="summary-row total">
            <span>总计：</span>
            <span>¥{{ total }}</span>
          </div>
        </div>

        <div class="notifications">
          <div
            v-for="notification in notifications"
            :key="notification.id"
            :class="['notification', notification.type]"
          >
            {{ notification.message }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'

// 商品数据
const products = ref([
  { id: 1, name: 'Vue.js T恤', price: 99, stock: 10 },
  { id: 2, name: 'React马克杯', price: 45, stock: 3 },
  { id: 3, name: 'JavaScript书籍', price: 89, stock: 15 },
  { id: 4, name: '编程键盘', price: 299, stock: 2 }
])

// 购物车数据
const cartItems = ref([])
const notifications = ref([])

// 计算属性：购物车商品数量
const totalItems = computed(() => {
  return cartItems.value.reduce((total, item) => total + item.quantity, 0)
})

// 计算属性：商品总价
const subtotal = computed(() => {
  return cartItems.value.reduce((total, item) => total + item.total, 0)
})

// 计算属性：运费
const shippingFee = computed(() => {
  return subtotal.value >= 200 ? 0 : 10
})

// 计算属性：优惠
const discount = computed(() => {
  if (totalItems.value >= 5) return 20
  if (subtotal.value >= 300) return 30
  if (totalItems.value >= 3) return 10
  return 0
})

// 计算属性：总计
const total = computed(() => {
  return Math.max(0, subtotal.value + shippingFee.value - discount.value)
})

// 为购物车商品添加计算属性
cartItems.value.forEach(item => {
  item.total = computed(() => item.price * item.quantity)
})

// 方法：添加到购物车
function addToCart(product) {
  if (product.stock <= 0) return

  const existingItem = cartItems.value.find(item => item.id === product.id)

  if (existingItem) {
    existingItem.quantity++
  } else {
    cartItems.value.push({
      id: product.id,
      name: product.name,
      price: product.price,
      quantity: 1,
      total: computed(() => product.price * 1)
    })
  }

  product.stock--
  addNotification(`${product.name} 已加入购物车`, 'success')
}

// 方法：更新数量
function updateQuantity(productId, newQuantity) {
  if (newQuantity <= 0) {
    removeFromCart(productId)
    return
  }

  const item = cartItems.value.find(item => item.id === productId)
  const product = products.value.find(p => p.id === productId)

  if (item && product) {
    const quantityDiff = newQuantity - item.quantity

    if (quantityDiff > 0 && product.stock < quantityDiff) {
      addNotification(`${product.name} 库存不足！`, 'error')
      return
    }

    item.quantity = newQuantity
    product.stock -= quantityDiff
  }
}

// 方法：从购物车移除
function removeFromCart(productId) {
  const itemIndex = cartItems.value.findIndex(item => item.id === productId)
  if (itemIndex > -1) {
    const item = cartItems.value[itemIndex]
    const product = products.value.find(p => p.id === productId)

    if (product) {
      product.stock += item.quantity
    }

    cartItems.value.splice(itemIndex, 1)
    addNotification(`商品已从购物车移除`, 'info')
  }
}

// 方法：添加通知
function addNotification(message, type) {
  const id = Date.now()
  notifications.value.push({ id, message, type })

  setTimeout(() => {
    const index = notifications.value.findIndex(n => n.id === id)
    if (index > -1) {
      notifications.value.splice(index, 1)
    }
  }, 3000)
}

// 侦听器：库存警告
watch(
  products,
  (newProducts) => {
    newProducts.forEach(product => {
      if (product.stock <= 5 && product.stock > 0) {
        addNotification(`${product.name} 库存仅剩 ${product.stock} 件！`, 'warning')
      }
    })
  },
  { deep: true }
)

// 侦听器：购物车变化
watch(
  cartItems,
  () => {
    console.log('购物车已更新，当前商品数量：', totalItems.value)
  },
  { deep: true }
)
</script>

<style scoped>
.shopping-cart {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  max-width: 800px;
}

.products {
  margin-bottom: 30px;
}

.product-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.product-item {
  border: 1px solid #eee;
  border-radius: 4px;
  padding: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-info h4 {
  margin: 0 0 8px 0;
}

.product-info p {
  margin: 4px 0;
  font-size: 14px;
}

.low-stock {
  color: #ff6b6b;
  font-weight: bold;
}

.product-actions button {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  background-color: #42b883;
  color: white;
  cursor: pointer;
}

.product-actions button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.cart {
  border-top: 2px solid #eee;
  padding-top: 20px;
}

.empty-cart {
  text-align: center;
  color: #666;
  font-style: italic;
  padding: 40px;
}

.cart-items {
  margin-bottom: 20px;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 10px;
  border-bottom: 1px solid #eee;
}

.item-name {
  flex: 1;
  font-weight: bold;
}

.item-controls {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0 20px;
}

.item-controls button {
  width: 30px;
  height: 30px;
  border: 1px solid #ddd;
  background-color: white;
  cursor: pointer;
}

.item-total {
  font-weight: bold;
  color: #42b883;
  min-width: 80px;
  text-align: right;
}

.cart-summary {
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 4px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin: 8px 0;
}

.summary-row.total {
  font-size: 18px;
  font-weight: bold;
  color: #42b883;
  border-top: 1px solid #ddd;
  padding-top: 10px;
}

.discount {
  color: #ff6b6b;
}

.notifications {
  margin-top: 20px;
}

.notification {
  padding: 10px;
  margin: 5px 0;
  border-radius: 4px;
}

.notification.success {
  background-color: #d4edda;
  color: #155724;
}

.notification.error {
  background-color: #f8d7da;
  color: #721c24;
}

.notification.warning {
  background-color: #fff3cd;
  color: #856404;
}

.notification.info {
  background-color: #d1ecf1;
  color: #0c5460;
}
</style>
```

---

## 📝 本章小结

### ✅ 掌握的核心概念

1. **计算属性** - 基于依赖缓存的派生状态
2. **缓存机制** - 计算属性的性能优势
3. **可写计算属性** - 自定义getter和setter
4. **计算属性vs方法** - 使用场景和性能差异
5. **侦听器进阶** - watch和watchEffect的高级用法

### 🎯 实践能力

- [ ] 能够正确使用计算属性处理数据转换
- [ ] 能够创建可写计算属性
- [ ] 能够选择合适的侦听器
- [ ] 能够处理复杂的计算场景

### 🚀 下一步学习

掌握了计算属性和侦听器后，继续学习：
- [[05-Vue指令与CompositionAPI体系概览.md|Vue指令与API体系]]
- [[06-Vue快速入门指南.md|Vue快速入门]]

---

**记住：计算属性是Vue性能优化的关键，善用计算属性能让你的应用更高效！** 🎉