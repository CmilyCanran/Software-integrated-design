# Vue 3 核心函数详解

## 🎯 本节目标

深入理解Vue 3的三个核心函数：`defineStore`、`ref` 和 `computed`，掌握它们在服装销售系统中的具体应用。

---

## 📚 函数概览

| 函数 | 来源 | 作用 | 在服装销售系统中的应用 |
|------|------|------|----------------------|
| `defineStore` | Pinia | 创建状态存储 | 用户认证、购物车、商品管理 |
| `ref` | Vue 3 | 创建响应式数据 | 表单输入、状态变量 |
| `computed` | Vue 3 | 创建计算属性 | 总价计算、状态判断 |

---

## 🏪 defineStore - Pinia的核心函数

### 🎯 **什么是defineStore？**

`defineStore` 是Pinia的核心函数，用于创建**状态存储**（Store）。可以把它想象成一个**全局数据仓库**。

### 🏗️ **基本语法**

```javascript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// 🏪 创建一个Store
export const useStoreName = defineStore('storeId', () => {
  // Store 的内容
})
```

### 🛍️ **在服装销售系统中的实际应用**

#### **1. 用户认证Store**

```javascript
// stores/auth.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  // 🔸 ref - 创建基础状态
  const token = ref('')
  const userInfo = ref({})
  const loading = ref(false)

  // 🔸 computed - 创建计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value.role === 'admin')
  const username = computed(() => userInfo.value.username || '')

  // 🔸 方法 - 创建操作函数
  const login = (userData) => {
    token.value = userData.token
    userInfo.value = userData.user
    localStorage.setItem('token', userData.token)
    localStorage.setItem('userInfo', JSON.stringify(userData.user))
  }

  const logout = () => {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  // 🔸 返回Store的公开接口
  return {
    // 状态
    token,
    userInfo,
    loading,

    // 计算属性
    isLoggedIn,
    isAdmin,
    username,

    // 方法
    login,
    logout
  }
})
```

#### **2. 购物车Store**

```javascript
// stores/cart.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCartStore = defineStore('cart', () => {
  // 🔸 ref - 购物车商品列表
  const items = ref([])

  // 🔸 computed - 计算总价
  const totalPrice = computed(() => {
    return items.value.reduce((sum, item) => {
      return sum + (item.price * item.quantity)
    }, 0)
  })

  // 🔸 computed - 计算总数量
  const totalCount = computed(() => {
    return items.value.reduce((sum, item) => {
      return sum + item.quantity
    }, 0)
  })

  // 🔸 computed - 格式化价格
  const formattedTotalPrice = computed(() => {
    return `¥${totalPrice.value.toFixed(2)}`
  })

  // 🔸 方法 - 添加商品
  const addToCart = (product) => {
    const existingItem = items.value.find(item => item.id === product.id)

    if (existingItem) {
      existingItem.quantity += 1
    } else {
      items.value.push({
        ...product,
        quantity: 1
      })
    }
  }

  return {
    items,
    totalPrice,
    totalCount,
    formattedTotalPrice,
    addToCart
  }
})
```

### 🎯 **defineStore的关键特点**

1. **唯一标识** - 每个Store都有唯一的ID
2. **响应式** - Store中的数据变化会自动更新UI
3. **全局访问** - 任何组件都可以访问同一个Store
4. **类型安全** - 支持TypeScript（虽然我们用JavaScript）

---

## 🔧 ref - Vue 3的响应式引用

### 🎯 **什么是ref？**

`ref` 是Vue 3的响应式函数，用于创建**响应式数据**。当ref的数据变化时，使用它的地方会自动更新。

### 🏗️ **基本语法**

```javascript
import { ref } from 'vue'

// 🔸 创建响应式数据
const count = ref(0)
const message = ref('Hello')
const isActive = ref(false)

// 🔸 访问值
console.log(count.value)  // 0

// 🔸 修改值
count.value = 10
message.value = 'World'
isActive.value = true
```

### 🛍️ **在服装销售系统中的实际应用**

#### **1. 表单数据管理**

```vue
<!-- Login.vue -->
<script setup>
import { ref } from 'vue'

// 🔸 ref 创建表单数据
const loginForm = ref({
  username: '',
  password: '',
  rememberMe: false
})

// 🔸 ref 创建状态
const loading = ref(false)
const errorMessage = ref('')

// 🔸 使用ref数据
const handleLogin = async () => {
  loading.value = true  // 开始加载

  try {
    // 登录逻辑
    await authAPI.login(loginForm.value)  // 使用表单数据
  } catch (error) {
    errorMessage.value = error.message  // 设置错误信息
  } finally {
    loading.value = false  // 结束加载
  }
}
</script>

<template>
  <!-- 🔸 在模板中使用ref -->
  <el-input v-model="loginForm.username" />
  <el-input v-model="loginForm.password" />

  <el-button :loading="loading" @click="handleLogin">
    登录
  </el-button>

  <div v-if="errorMessage" class="error">
    {{ errorMessage }}
  </div>
</template>
```

#### **2. 组件状态管理**

```vue
<!-- ProductCard.vue -->
<script setup>
import { ref } from 'vue'

// 🔸 ref 创建组件状态
const isExpanded = ref(false)
const selectedSize = ref('')
const selectedColor = ref('')
const quantity = ref(1)

// 🔸 ref 创建商品数据
const product = ref({
  id: 1,
  name: '时尚T恤',
  price: 99,
  sizes: ['S', 'M', 'L', 'XL'],
  colors: ['白色', '黑色', '灰色'],
  stock: 100
})

// 🔸 方法中使用ref
const toggleExpanded = () => {
  isExpanded.value = !isExpanded.value
}

const addToCart = () => {
  if (!selectedSize.value) {
    alert('请选择尺码')
    return
  }

  // 添加到购物车逻辑
  cartStore.addToCart({
    ...product.value,
    size: selectedSize.value,
    color: selectedColor.value,
    quantity: quantity.value
  })
}
</script>

<template>
  <div class="product-card">
    <h3>{{ product.name }}</h3>
    <p>¥{{ product.price }}</p>

    <!-- 🔸 使用ref数据 -->
    <div v-if="isExpanded">
      <select v-model="selectedSize">
        <option v-for="size in product.sizes" :key="size">
          {{ size }}
        </option>
      </select>

      <input-number v-model="quantity" :min="1" />

      <el-button @click="addToCart">加入购物车</el-button>
    </div>

    <el-button @click="toggleExpanded">
      {{ isExpanded ? '收起' : '展开' }}
    </el-button>
  </div>
</template>
```

### 🎯 **ref的关键特点**

1. **响应式** - 数据变化自动更新UI
2. **.value访问** - 在脚本中需要用`.value`访问值
3. **模板自动解包** - 在模板中不需要`.value`
4. **支持所有类型** - 数字、字符串、对象、数组等

---

## 🧮 computed - Vue 3的计算属性

### 🎯 **什么是computed？**

`computed` 是Vue 3的计算属性函数，用于创建**基于其他数据计算得出的值**。当依赖的数据变化时，计算属性会自动重新计算。

### 🏗️ **基本语法**

```javascript
import { ref, computed } from 'vue'

const firstName = ref('John')
const lastName = ref('Doe')

// 🔸 computed 创建计算属性
const fullName = computed(() => {
  return `${firstName.value} ${lastName.value}`
})

// 🔸 使用计算属性
console.log(fullName.value)  // "John Doe"

// 🔸 当依赖变化时，自动重新计算
firstName.value = 'Jane'
console.log(fullName.value)  // "Jane Doe"
```

### 🛍️ **在服装销售系统中的实际应用**

#### **1. 购物车计算**

```javascript
// stores/cart.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCartStore = defineStore('cart', () => {
  const items = ref([])
  const discountRate = ref(0.1)  // 10%折扣

  // 🔸 computed - 计算商品总价
  const subtotal = computed(() => {
    return items.value.reduce((sum, item) => {
      return sum + (item.price * item.quantity)
    }, 0)
  })

  // 🔸 computed - 计算折扣金额
  const discountAmount = computed(() => {
    return subtotal.value * discountRate.value
  })

  // 🔸 computed - 计算最终价格
  const finalPrice = computed(() => {
    return subtotal.value - discountAmount.value
  })

  // 🔸 computed - 格式化显示
  const formattedPrice = computed(() => {
    return `¥${finalPrice.value.toFixed(2)}`
  })

  // 🔸 computed - 判断是否可以结算
  const canCheckout = computed(() => {
    return items.value.length > 0 && finalPrice.value > 0
  })

  return {
    items,
    discountRate,
    subtotal,
    discountAmount,
    finalPrice,
    formattedPrice,
    canCheckout
  }
})
```

#### **2. 用户权限计算**

```javascript
// stores/auth.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const userInfo = ref({})

  // 🔸 computed - 判断是否登录
  const isLoggedIn = computed(() => {
    return !!userInfo.value.id
  })

  // 🔸 computed - 判断是否管理员
  const isAdmin = computed(() => {
    return userInfo.value.role === 'admin'
  })

  // 🔸 computed - 判断是否可以管理商品
  const canManageProducts = computed(() => {
    return isAdmin.value || userInfo.value.permissions?.includes('product:manage')
  })

  // 🔸 computed - 获取用户显示名称
  const displayName = computed(() => {
    return userInfo.value.nickname || userInfo.value.username || '用户'
  })

  // 🔸 computed - 获取用户头像
  const userAvatar = computed(() => {
    return userInfo.value.avatar || '/default-avatar.png'
  })

  return {
    userInfo,
    isLoggedIn,
    isAdmin,
    canManageProducts,
    displayName,
    userAvatar
  }
})
```

#### **3. 商品筛选和搜索**

```vue
<!-- ProductList.vue -->
<script setup>
import { ref, computed } from 'vue'

const products = ref([
  { name: '时尚T恤', price: 99, category: 'clothing', color: 'white' },
  { name: '牛仔裤', price: 299, category: 'clothing', color: 'blue' },
  { name: '运动鞋', price: 599, category: 'shoes', color: 'black' }
])

const searchQuery = ref('')
const selectedCategory = ref('')
const maxPrice = ref(1000)

// 🔸 computed - 筛选商品
const filteredProducts = computed(() => {
  return products.value.filter(product => {
    // 搜索筛选
    const matchesSearch = product.name.toLowerCase().includes(searchQuery.value.toLowerCase())

    // 分类筛选
    const matchesCategory = !selectedCategory.value || product.category === selectedCategory.value

    // 价格筛选
    const matchesPrice = product.price <= maxPrice.value

    return matchesSearch && matchesCategory && matchesPrice
  })
})

// 🔸 computed - 计算筛选结果数量
const filteredCount = computed(() => {
  return filteredProducts.value.length
})

// 🔸 computed - 判断是否有筛选结果
const hasResults = computed(() => {
  return filteredCount.value > 0
})

// 🔸 computed - 获取价格范围
const priceRange = computed(() => {
  const prices = products.value.map(p => p.price)
  return {
    min: Math.min(...prices),
    max: Math.max(...prices)
  }
})
</script>

<template>
  <div>
    <!-- 搜索输入 -->
    <el-input v-model="searchQuery" placeholder="搜索商品" />

    <!-- 分类筛选 -->
    <select v-model="selectedCategory">
      <option value="">全部分类</option>
      <option value="clothing">服装</option>
      <option value="shoes">鞋子</option>
    </select>

    <!-- 价格筛选 -->
    <el-slider v-model="maxPrice" :max="priceRange.max" />

    <!-- 🔸 使用计算属性 -->
    <div v-if="hasResults">
      <p>找到 {{ filteredCount }} 件商品</p>

      <div v-for="product in filteredProducts" :key="product.name">
        {{ product.name }} - ¥{{ product.price }}
      </div>
    </div>

    <div v-else>
      <p>没有找到符合条件的商品</p>
    </div>
  </div>
</template>
```

### 🎯 **computed的关键特点**

1. **自动缓存** - 只有依赖变化时才重新计算
2. **响应式** - 计算结果会自动更新UI
3. **只读保护** - 默认是只读的，防止意外修改
4. **性能优化** - 避免重复计算，提高性能

---

## 🔄 三个函数的协作

### 🛍️ **在登录功能中的完整示例**

```javascript
// stores/auth.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  // 🔸 ref - 创建基础状态
  const token = ref('')
  const userInfo = ref({})
  const loading = ref(false)

  // 🔸 computed - 创建计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value.role === 'admin')
  const displayName = computed(() => userInfo.value.username || '用户')

  // 🔸 方法 - 登录操作
  const login = async (credentials) => {
    loading.value = true  // ref 状态更新
    try {
      const response = await authAPI.login(credentials)
      token.value = response.data.token  // ref 状态更新
      userInfo.value = response.data.user  // ref 状态更新
    } catch (error) {
      throw error
    } finally {
      loading.value = false  // ref 状态更新
    }
  }

  return {
    // ref 状态
    token,
    userInfo,
    loading,

    // computed 计算属性
    isLoggedIn,
    isAdmin,
    displayName,

    // 方法
    login
  }
})
```

```vue
<!-- Login.vue -->
<script setup>
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'

// 🔸 ref - 创建表单数据
const loginForm = ref({
  username: '',
  password: ''
})

// 🔸 使用 defineStore 创建的 Store
const authStore = useAuthStore()

const handleLogin = async () => {
  await authStore.login(loginForm.value)

  // 🔸 computed 会自动更新，无需手动操作
  // authStore.isLoggedIn 自动变为 true
  // authStore.displayName 自动显示用户名
}
</script>

<template>
  <!-- 🔸 ref 数据双向绑定 -->
  <el-input v-model="loginForm.username" />
  <el-input v-model="loginForm.password" />

  <!-- 🔸 ref 状态控制按钮 -->
  <el-button :loading="authStore.loading" @click="handleLogin">
    登录
  </el-button>

  <!-- 🔸 computed 自动显示 -->
  <div v-if="authStore.isLoggedIn">
    欢迎，{{ authStore.displayName }}！
  </div>
</template>
```

---

## 📝 小结

### ✅ **三个函数的核心作用**

| 函数 | 核心作用 | 在服装销售系统中的应用 |
|------|----------|----------------------|
| `defineStore` | 创建全局状态存储 | 用户认证、购物车、商品管理 |
| `ref` | 创建响应式数据 | 表单输入、组件状态、临时数据 |
| `computed` | 创建计算属性 | 总价计算、状态判断、数据筛选 |

### 🎯 **使用原则**

1. **defineStore** - 用于需要在多个组件间共享的数据
2. **ref** - 用于组件内部的响应式数据
3. **computed** - 用于基于其他数据计算得出的值

### 🚀 **最佳实践**

1. **合理分工** - Store管全局，ref管局部，computed管计算
2. **避免过度使用** - 不是所有数据都需要放进Store
3. **性能考虑** - computed有缓存，适合复杂计算
4. **代码组织** - 相关功能放在同一个Store中

掌握这三个核心函数，您就能高效地开发Vue 3应用了！🎉