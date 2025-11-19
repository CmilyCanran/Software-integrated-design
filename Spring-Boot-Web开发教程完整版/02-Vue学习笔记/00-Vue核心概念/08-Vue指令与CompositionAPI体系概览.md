---
tags:
  - Vue指令
  - CompositionAPI
  - Vue3
  - 模板语法
  - 响应式API
created: 2025-11-18
modified: 2025-11-18
category: Vue核心概念
difficulty: beginner
---

# Vue指令与Composition API体系概览

> **学习目标**：掌握Vue的"语言" - 指令系统和Composition API，理解每个API的作用和适用场景

## 🎯 指令和API的重要性

Vue指令和API是Vue框架的"语法"，通过它们告诉Vue：
- 如何处理模板和数据
- 如何管理组件状态
- 如何响应用户交互
- 如何组织组件逻辑

**指令和API的好处：**
- 声明式模板，易于理解
- 强大的响应式能力
- 灵活的组合API设计
- 优秀的类型推导支持

---

## 🏷️ 模板指令体系

### 核心指令：数据绑定

这些指令告诉Vue如何将数据渲染到模板：

```vue
<template>
  <!-- 1. 文本插值 - 显示数据 -->
  <h1>{{ message }}</h1>

  <!-- 2. v-bind - 绑定HTML属性 -->
  <img :src="avatarUrl" :alt="userName">
  <div :class="{ active: isActive }" :style="{ color: textColor }">

  <!-- 3. v-model - 双向数据绑定 -->
  <input v-model="username" placeholder="请输入用户名">
  <textarea v-model="description"></textarea>

  <!-- 4. v-html - 渲染HTML内容 -->
  <div v-html="richContent"></div>
</template>

<script setup>
import { ref } from 'vue'

const message = ref('Hello Vue!')
const avatarUrl = ref('/images/avatar.png')
const userName = ref('张三')
const isActive = ref(true)
const textColor = ref('blue')
const username = ref('')
const description = ref('')
const richContent = ref('<strong>粗体文本</strong>')
</script>
```

### 条件渲染指令

根据条件决定是否渲染元素：

```vue
<template>
  <!-- v-if - 条件性渲染（完全销毁/创建） -->
  <div v-if="isLoggedIn">
    <h2>欢迎回来，{{ userName }}!</h2>
    <button @click="logout">退出登录</button>
  </div>

  <!-- v-else-if - 多条件判断 -->
  <div v-else-if="isLoggingIn">
    <p>正在登录中...</p>
  </div>

  <!-- v-else - 默认条件 -->
  <div v-else>
    <h2>请登录</h2>
    <button @click="login">登录</button>
  </div>

  <!-- v-show - 显示/隐藏（基于CSS display） -->
  <div v-show="showDetails">
    这里是详细信息（通过display控制）
  </div>
</template>

<script setup>
import { ref } from 'vue'

const isLoggedIn = ref(false)
const isLoggingIn = ref(false)
const userName = ref('')
const showDetails = ref(true)

function login() {
  isLoggingIn.value = true
  setTimeout(() => {
    isLoggedIn.value = true
    isLoggingIn.value = false
    userName.value = '张三'
  }, 1000)
}

function logout() {
  isLoggedIn.value = false
}
</script>
```

### 列表渲染指令

循环渲染列表数据：

```vue
<template>
  <!-- v-for - 数组遍历 -->
  <ul>
    <li v-for="(user, index) in users" :key="user.id">
      {{ index + 1 }}. {{ user.name }} - {{ user.email }}
    </li>
  </ul>

  <!-- v-for - 对象遍历 -->
  <div v-for="(value, key, index) in userInfo" :key="key">
    {{ index }}. {{ key }}: {{ value }}
  </div>

  <!-- v-for - 数字遍历 -->
  <div v-for="n in 5" :key="n">
    第 {{ n }} 个项目
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const users = ref([
  { id: 1, name: '张三', email: 'zhangsan@example.com' },
  { id: 2, name: '李四', email: 'lisi@example.com' },
  { id: 3, name: '王五', email: 'wangwu@example.com' }
])

const userInfo = reactive({
  name: '张三',
  age: 25,
  city: '北京'
})
</script>
```

### 事件处理指令

响应用户交互：

```vue
<template>
  <!-- v-on - 事件绑定（简写@） -->
  <button @click="handleClick">点击我</button>

  <!-- 事件传参 -->
  <button @click="sayHello('Vue')">问候Vue</button>

  <!-- 事件对象 -->
  <button @click="handleEvent">获取事件信息</button>

  <!-- 修饰符 -->
  <form @submit.prevent="handleSubmit"> preventDefault
    <input type="text" v-model="formData">
    <button type="submit">提交</button>
  </form>

  <!-- 键盘修饰符 -->
  <input @keyup.enter="onEnter" placeholder="按回车提交">

  <!-- 鼠标修饰符 -->
  <div @click.left="leftClick" @click.right="rightClick">
    左键和右键点击
  </div>
</template>

<script setup>
import { ref } from 'vue'

const formData = ref('')

function handleClick() {
  console.log('按钮被点击了!')
}

function sayHello(name) {
  alert(`Hello, ${name}!`)
}

function handleEvent(event) {
  console.log('事件对象:', event)
  console.log('点击位置:', event.clientX, event.clientY)
}

function handleSubmit() {
  console.log('表单提交:', formData.value)
  formData.value = ''
}

function onEnter() {
  console.log('回车键被按下!')
}

function leftClick() {
  console.log('左键点击')
}

function rightClick() {
  console.log('右键点击')
}
</script>
```

---

## 💉 Composition API 核心函数

### 响应式数据创建

这些API用于创建响应式数据：

```vue
<script setup>
import { ref, reactive, readonly, computed } from 'vue'

// 1. ref - 创建响应式引用（基本类型）
const count = ref(0)
const message = ref('Hello')

// 2. reactive - 创建响应式对象
const user = reactive({
  name: '张三',
  age: 25,
  profile: {
    email: 'zhangsan@example.com',
    phone: '13800138000'
  }
})

// 3. readonly - 创建只读响应式对象
const config = readonly({
  apiUrl: 'https://api.example.com',
  timeout: 5000
})

// 4. computed - 计算属性
const doubleCount = computed(() => count.value * 2)

const userStatus = computed(() => {
  if (user.age < 18) return '未成年'
  if (user.age < 30) return '青年'
  return '成年'
})

// 5. 使用响应式数据
function increment() {
  count.value++         // ref需要.value访问
  user.age++           // reactive直接访问
}

// 6. readonly对象的访问尝试
function tryModifyConfig() {
  // config.apiUrl = 'new-url'  // 警告：不能修改只读对象
  console.log('API URL:', config.apiUrl)  // 只能读取
}
</script>
```

### 响应式工具函数

用于监控和操作响应式数据：

```vue
<script setup>
import { ref, watch, watchEffect, nextTick } from 'vue'

const count = ref(0)
const message = ref('')
const messageHistory = ref([])

// 1. watch - 监听特定数据变化
watch(count, (newValue, oldValue) => {
  console.log(`count 从 ${oldValue} 变为 ${newValue}`)
})

// 2. watch - 监听多个数据
watch([count, message], ([newCount, newMessage], [oldCount, oldMessage]) => {
  console.log('数据变化:', { newCount, newMessage, oldCount, oldMessage })
})

// 3. watch - 深度监听
const user = ref({
  profile: {
    name: '张三',
    details: {
      age: 25
    }
  }
})

watch(user, (newValue) => {
  console.log('user对象变化:', newValue)
}, { deep: true })  // 深度监听

// 4. watchEffect - 自动收集依赖的监听
watchEffect(() => {
  // 自动监听函数内使用的所有响应式数据
  if (count.value > 0) {
    console.log('Count大于0，当前值:', count.value)
  }
  if (message.value) {
    console.log('有新消息:', message.value)
  }
})

// 5. nextTick - 等待DOM更新
function updateAndLog() {
  count.value++
  message.value = `Count现在是${count.value}`

  nextTick(() => {
    console.log('DOM已更新，可以访问更新后的DOM')
    const element = document.querySelector('#count-display')
    console.log('元素内容:', element.textContent)
  })
}
</script>

<template>
  <div>
    <p id="count-display">Count: {{ count }}</p>
    <p>Message: {{ message }}</p>
    <button @click="updateAndLog">更新并记录</button>
  </div>
</template>
```

### 生命周期钩子

管理组件的生命周期：

```vue
<script setup>
import { ref, onMounted, onUnmounted, onUpdated, onBeforeMount, onBeforeUnmount } from 'vue'

const data = ref(null)
const timer = ref(null)
const updateCount = ref(0)

// 1. onBeforeMount - 组件挂载前
onBeforeMount(() => {
  console.log('组件即将挂载到DOM')
})

// 2. onMounted - 组件挂载后
onMounted(() => {
  console.log('组件已挂载到DOM')

  // 发起API请求
  fetchData()

  // 启动定时器
  timer.value = setInterval(() => {
    console.log('定时执行...')
  }, 1000)
})

// 3. onUpdated - 组件更新后
onUpdated(() => {
  console.log('组件已更新')
  updateCount.value++
  console.log('组件更新次数:', updateCount.value)
})

// 4. onBeforeUnmount - 组件卸载前
onBeforeUnmount(() => {
  console.log('组件即将卸载')
})

// 5. onUnmounted - 组件卸载后
onUnmounted(() => {
  console.log('组件已卸载')

  // 清理定时器
  if (timer.value) {
    clearInterval(timer.value)
    timer.value = null
  }
})

// 模拟数据获取
function fetchData() {
  setTimeout(() => {
    data.value = {
      id: 1,
      title: 'Vue 3 学习笔记',
      content: 'Composition API很强大!'
    }
    console.log('数据加载完成:', data.value)
  }, 500)
}

 function triggerUpdate() {
  data.value = {
    ...data.value,
    updateAt: new Date().toLocaleTimeString()
  }
}
</script>

<template>
  <div v-if="data">
    <h2>{{ data.title }}</h2>
    <p>{{ data.content }}</p>
    <p v-if="data.updateAt">更新于: {{ data.updateAt }}</p>
    <button @click="triggerUpdate">触发更新</button>
  </div>
  <div v-else>
    <p>加载中...</p>
  </div>
</template>
```

---

## 🔧 组件通信API

### Props和Emits

父子组件之间的通信：

```vue
<!-- Parent.vue -->
<template>
  <div>
    <h2>父组件</h2>
    <Child
      :message="parentMessage"
      :count="count"
      :user-info="userInfo"
      @child-message="handleChildMessage"
      @increment="handleIncrement"
    />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import Child from './Child.vue'

const parentMessage = ref('来自父组件的消息')
const count = ref(0)
const userInfo = reactive({
  name: '张三',
  age: 25
})

function handleChildMessage(message) {
  console.log('收到子组件消息:', message)
  parentMessage.value = `父组件收到: ${message}`
}

function handleIncrement() {
  count.value++
}
</script>

<!-- Child.vue -->
<template>
  <div class="child">
    <h3>子组件</h3>
    <p>收到消息: {{ message }}</p>
    <p>计数: {{ count }}</p>
    <p>用户信息: {{ userInfo.name }} - {{ userInfo.age }}</p>

    <button @click="sendMessage">向父组件发送消息</button>
    <button @click="emitIncrement">通知父组件增加</button>
  </div>
</template>

<script setup>
// 1. defineProps - 定义接收的属性
const props = defineProps({
  message: String,
  count: Number,
  userInfo: {
    type: Object,
    required: true
  }
})

// 2. defineEmits - 定义可以触发的事件
const emit = defineEmits(['child-message', 'increment'])

function sendMessage() {
  const childMessage = `来自子组件的消息 - 时间: ${new Date().toLocaleTimeString()}`
  emit('child-message', childMessage)
}

function emitIncrement() {
  emit('increment')
}
</script>

<style scoped>
.child {
  border: 1px solid #ccc;
  padding: 15px;
  margin: 10px 0;
  border-radius: 5px;
}
</style>
```

---

## 📋 指令和API使用最佳实践

### ✅ 推荐做法

1. **合理使用v-if和v-show**
```vue
<!-- 频繁切换用v-show -->
<div v-show="isVisible">经常显示隐藏的内容</div>

<!-- 条件很少改变用v-if -->
<heavy-component v-if="showComponent" />
```

2. **正确的key使用**
```vue
<!-- ✅ 使用稳定的唯一值作为key -->
<li v-for="user in users" :key="user.id">

<!-- ❌ 不要使用index作为key（如果列表会变化） -->
<li v-for="(user, index) in users" :key="index">
```

3. **ref和reactive的选择**
```javascript
// ✅ 基本类型用ref
const count = ref(0)
const message = ref('Hello')

// ✅ 对象用reactive
const user = reactive({ name: '张三', age: 25 })

// ✅ 需要替换整个对象时用ref包裹对象
const user = ref({ name: '张三', age: 25 })
user.value = { name: '李四', age: 30 }
```

4. **计算属性的使用**
```javascript
// ✅ 复杂的派生状态用计算属性
const fullName = computed(() => {
  return `${user.firstName} ${user.lastName}`
})

// ❌ 避免在模板中写复杂逻辑
<template>
  <!-- 不推荐 -->
  {{ user.firstName + ' ' + user.lastName }}

  <!-- 推荐 -->
  {{ fullName }}
</template>
```

### ❌ 避免做法

```vue
<!-- ❌ 避免在模板中直接修改数据 -->
<button @click="count++"> <!-- 不推荐 -->
  {{ count }}
</button>

<!-- ✅ 使用方法 -->
<button @click="increment"> <!-- 推荐 -->
  {{ count }}
</button>

<script setup>
function increment() {
  count.value++
}
</script>
```

---

## 🎯 指令和API速查表

| 分类 | 指令/API | 作用 | 使用场景 |
|------|----------|------|----------|
| **数据绑定** | `{{ }}` | 文本插值 | 显示文本内容 |
| | `v-bind`(:) | 属性绑定 | 绑定HTML属性 |
| | `v-model` | 双向绑定 | 表单输入控件 |
| | `v-html` | HTML渲染 | 渲染富文本内容 |
| **条件渲染** | `v-if` | 条件渲染 | 根据条件创建/销毁元素 |
| | `v-show` | 显示隐藏 | 基于CSS显示隐藏 |
| | `v-else-if` | 多条件 | 多条件判断 |
| | `v-else` | 默认条件 | 默认分支 |
| **列表渲染** | `v-for` | 循环渲染 | 数组、对象、数字遍历 |
| **事件处理** | `v-on`(@) | 事件绑定 | 用户交互事件 |
| **响应式API** | `ref` | 引用响应式 | 基本类型、需要替换的对象 |
| | `reactive` | 对象响应式 | 复杂对象状态 |
| | `computed` | 计算属性 | 派生状态计算 |
| **工具API** | `watch` | 监听器 | 监控特定数据变化 |
| | `watchEffect` | 自动监听 | 自动收集依赖的监听 |
| | `nextTick` | DOM更新后 | 等待DOM更新完成 |
| **生命周期** | `onMounted` | 挂载后 | DOM操作、API请求 |
| | `onUpdated` | 更新后 | 响应更新操作 |
| | `onUnmounted` | 卸载后 | 清理工作 |

---

## 🚀 下一步学习

掌握了指令和API体系后，继续深入学习：

- [[02-Vue核心概念/03-Vue快速入门指南.md|Vue快速入门指南]]
- [[02-Vue学习笔记/01-组件系统/01-组件通信.md|组件通信详解]]
- [[02-Vue学习笔记/02-状态管理/01-Pinia状态管理.md|Pinia状态管理]]

---

**记住：指令是Vue模板的语法，Composition API是Vue逻辑的基础，掌握了它们就掌握了Vue的核心能力！** 🎉