# 第2章：Vue核心概念学习

> **学习目标**：掌握Vue 3 Composition API的核心概念，为开发用户认证功能打下基础

## 🎯 本章概览

| 概念 | 预计时间 | 难度 | 状态 |
|------|----------|------|------|
| ref响应式数据 | 15分钟 | ⭐⭐ | ⏳ |
| reactive响应式对象 | 10分钟 | ⭐⭐ | ⏳ |
| computed计算属性 | 10分钟 | ⭐⭐ | ⏳ |
| 组件生命周期 | 5分钟 | ⭐ | ⏳ |
| 实践练习 | 20分钟 | ⭐⭐⭐ | ⏳ |

---

## 📚 Vue 3 Composition API 简介

Vue 3引入了Composition API，它提供了更灵活的代码组织方式，特别适合复杂应用的开发。

### 为什么选择Composition API？
- **更好的逻辑复用** - 可以将相关逻辑组合在一起
- **更好的TypeScript支持** - 类型推导更准确
- **更清晰的代码组织** - 按功能而非选项组织代码

---

## 🔹 ref：响应式数据

### 基本概念
`ref`用于创建响应式的数据，它返回一个包含`.value`属性的对象。

### 语法示例
```javascript
import { ref } from 'vue'

// 创建响应式数据
const count = ref(0)
const message = ref('Hello Vue')
const isVisible = ref(true)

// 在JavaScript中访问
console.log(count.value)  // 输出: 0
count.value = 1           // 修改值

// 在模板中直接使用（不需要.value）
<template>
  <div>{{ count }}</div>     <!-- 直接显示值 -->
  <button @click="count++">增加</button>
</template>
```

### 实际应用场景
```javascript
// 用户表单数据
const username = ref('')
const password = ref('')
const loading = ref(false)

// 错误信息
const errorMessage = ref('')

// 使用示例
const handleLogin = () => {
  if (!username.value) {
    errorMessage.value = '请输入用户名'
    return
  }

  loading.value = true
  // 登录逻辑...
}
```

### ref vs 原始值对比
```javascript
// ❌ 错误：普通变量不是响应式的
let count = 0
const increment = () => {
  count = count + 1  // 界面不会更新
}

// ✅ 正确：使用ref创建响应式数据
const count = ref(0)
const increment = () => {
  count.value = count.value + 1  // 界面会更新
}
```

---

## 🔹 reactive：响应式对象

### 基本概念
`reactive`用于创建响应式对象，适用于复杂的数据结构。

### 语法示例
```javascript
import { reactive } from 'vue'

// 创建响应式对象
const user = reactive({
  username: '',
  email: '',
  profile: {
    age: 0,
    avatar: ''
  }
})

// 直接访问属性（不需要.value）
console.log(user.username)  // 输出: ''
user.username = 'admin'     // 修改属性

// 嵌套对象也是响应式的
user.profile.age = 25
```

### 实际应用场景
```javascript
// 登录表单对象
const loginForm = reactive({
  username: '',
  password: '',
  rememberMe: false
})

// 表单验证
const validateForm = () => {
  if (!loginForm.username) {
    return '请输入用户名'
  }
  if (!loginForm.password) {
    return '请输入密码'
  }
  return null
}

// 提交表单
const handleSubmit = () => {
  const error = validateForm()
  if (error) {
    console.log(error)
    return
  }

  console.log('提交表单:', loginForm)
}
```

### reactive vs ref 对比
```javascript
// 使用ref
const user = ref({
  username: 'admin',
  email: 'admin@example.com'
})

// 访问时需要.value
console.log(user.value.username)
user.value.email = 'new@example.com'

// 使用reactive
const user = reactive({
  username: 'admin',
  email: 'admin@example.com'
})

// 直接访问
console.log(user.username)
user.email = 'new@example.com'
```

---

## 🔹 computed：计算属性

### 基本概念
`computed`用于创建基于其他响应式数据计算得出的值，具有缓存特性。

### 语法示例
```javascript
import { ref, computed } from 'vue'

const firstName = ref('John')
const lastName = ref('Doe')

// 创建计算属性
const fullName = computed(() => {
  return `${firstName.value} ${lastName.value}`
})

// 使用计算属性
console.log(fullName.value)  // 输出: "John Doe"
```

### 实际应用场景
```javascript
// 购物车总价计算
const cartItems = ref([
  { name: '商品A', price: 100, quantity: 2 },
  { name: '商品B', price: 50, quantity: 3 }
])

const totalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    return sum + (item.price * item.quantity)
  }, 0)
})

const itemCount = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    return sum + item.quantity
  }, 0)
})

// 当cartItems变化时，totalPrice和itemCount会自动重新计算
```

### 计算属性的缓存特性
```javascript
const count = ref(0)
const expensiveValue = computed(() => {
  console.log('执行复杂计算...')
  return count.value * 1000
})

// 第一次访问时会执行计算
console.log(expensiveValue.value)  // 输出日志和结果

// 再次访问时，如果依赖没变，直接返回缓存值
console.log(expensiveValue.value)  // 不输出日志，直接返回缓存

// 依赖变化后，会重新计算
count.value = 1
console.log(expensiveValue.value)  // 再次输出日志和新结果
```

---

## 🔹 组件生命周期

### Vue 3生命周期钩子
在Composition API中，生命周期钩子以`on`开头：

```javascript
import { onMounted, onUnmounted, onUpdated } from 'vue'

export default {
  setup() {
    // 组件挂载后执行
    onMounted(() => {
      console.log('组件已挂载')
      // 适合：获取数据、设置定时器、添加事件监听
    })

    // 组件更新后执行
    onUpdated(() => {
      console.log('组件已更新')
    })

    // 组件卸载前执行
    onUnmounted(() => {
      console.log('组件即将卸载')
      // 适合：清理定时器、移除事件监听、取消网络请求
    })

    return {}
  }
}
```

### 在`<script setup>`中使用
```vue
<script setup>
import { onMounted, onUnmounted, ref } from 'vue'

const timer = ref(null)

onMounted(() => {
  console.log('页面加载完成')
  // 设置定时器
  timer.value = setInterval(() => {
    console.log('定时执行...')
  }, 1000)
})

onUnmounted(() => {
  console.log('页面即将销毁')
  // 清理定时器
  if (timer.value) {
    clearInterval(timer.value)
  }
})
</script>
```

---

## 🛠️ 实践练习

### 练习1：创建一个简单的计数器
```vue
<template>
  <div class="counter">
    <h2>计数器：{{ count }}</h2>
    <p>是否为偶数：{{ isEven }}</p>
    <button @click="increment">+1</button>
    <button @click="decrement">-1</button>
    <button @click="reset">重置</button>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// 响应式数据
const count = ref(0)

// 计算属性
const isEven = computed(() => count.value % 2 === 0)

// 方法
const increment = () => {
  count.value++
}

const decrement = () => {
  count.value--
}

const reset = () => {
  count.value = 0
}
</script>

<style scoped>
.counter {
  text-align: center;
  padding: 20px;
}

button {
  margin: 0 5px;
  padding: 5px 10px;
}
</style>
```

### 练习2：创建一个待办事项列表
```vue
<template>
  <div class="todo-app">
    <h2>待办事项</h2>

    <!-- 添加新待办 -->
    <div class="add-todo">
      <input
        v-model="newTodo"
        @keyup.enter="addTodo"
        placeholder="输入待办事项..."
      >
      <button @click="addTodo">添加</button>
    </div>

    <!-- 待办列表 -->
    <ul class="todo-list">
      <li v-for="todo in todos" :key="todo.id">
        <input
          type="checkbox"
          v-model="todo.completed"
        >
        <span :class="{ completed: todo.completed }">
          {{ todo.text }}
        </span>
        <button @click="removeTodo(todo.id)">删除</button>
      </li>
    </ul>

    <!-- 统计信息 -->
    <div class="stats">
      <p>总数：{{ todos.length }}</p>
      <p>已完成：{{ completedCount }}</p>
      <p>未完成：{{ uncompletedCount }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'

// 响应式数据
const newTodo = ref('')
const todos = ref([
  { id: 1, text: '学习Vue 3', completed: false },
  { id: 2, text: '完成项目', completed: true }
])

// 计算属性
const completedCount = computed(() => {
  return todos.value.filter(todo => todo.completed).length
})

const uncompletedCount = computed(() => {
  return todos.value.filter(todo => !todo.completed).length
})

// 方法
const addTodo = () => {
  if (newTodo.value.trim()) {
    todos.value.push({
      id: Date.now(),
      text: newTodo.value,
      completed: false
    })
    newTodo.value = ''
  }
}

const removeTodo = (id) => {
  todos.value = todos.value.filter(todo => todo.id !== id)
}
</script>

<style scoped>
.todo-app {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
}

.add-todo {
  display: flex;
  margin-bottom: 20px;
}

.add-todo input {
  flex: 1;
  padding: 8px;
  margin-right: 10px;
}

.todo-list {
  list-style: none;
  padding: 0;
}

.todo-list li {
  display: flex;
  align-items: center;
  padding: 8px;
  border-bottom: 1px solid #eee;
}

.todo-list li .completed {
  text-decoration: line-through;
  color: #999;
}

.todo-list li button {
  margin-left: auto;
}

.stats {
  margin-top: 20px;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 4px;
}
</style>
```

---

## 📝 本章小结

### ✅ 掌握概念
- [x] **ref** - 创建响应式基础数据
- [x] **reactive** - 创建响应式对象
- [x] **computed** - 创建计算属性
- [x] **生命周期** - 组件生命周期钩子

### 🎯 实践能力
- [x] 能够创建响应式数据
- [x] 能够使用计算属性优化性能
- [x] 能够组织组件逻辑
- [x] 能够处理组件生命周期

### 🚀 下一步
现在您已经掌握了Vue 3的核心概念，下一章我们将学习用户状态管理，使用Pinia来管理用户的登录状态。

---

## ❓ 常见问题

### Q1: 什么时候使用ref，什么时候使用reactive？
**A**:
- **基本数据类型**（string、number、boolean）→ 使用`ref`
- **对象类型** → 使用`reactive`
- **需要整体替换对象** → 使用`ref`

### Q2: computed和methods有什么区别？
**A**:
- **computed** 有缓存，依赖不变不会重新计算
- **methods** 每次调用都会执行
- **computed** 适合计算衍生数据
- **methods** 适合执行动作

### Q3: 为什么在模板中不需要.value？
**A**: Vue会自动解包ref，所以在模板中可以直接使用，但在JavaScript中必须使用.value

---

**恭喜！您已经掌握了Vue 3的核心概念。** 🎉

**下一章：[03-用户状态管理](03-用户状态管理.md)**