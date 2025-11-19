---
tags:
  - 快速入门
  - Vue入门
  - 实践指南
  - 第一个Vue应用
  - Vite
created: 2025-11-18
modified: 2025-11-18
category: Vue核心概念
difficulty: beginner
---

# Vue快速入门指南

> **学习目标**：20分钟内创建你的第一个Vue应用，体验响应式和组件化的魔力！

## 🎯 学习目标

通过这个快速入门，你将：
- ✅ 理解Vue响应式系统和组件化思想
- ✅ 掌握最常用的Vue指令和API
- ✅ 创建一个完整的Vue应用
- ✅ 体验现代前端开发的便利

---

## 🚀 开始第一步：创建Vue项目

### 使用Vite创建项目（推荐）

1. 打开终端，运行命令：
```bash
npm create vue@latest my-vue-app
```

2. 按提示选择配置：
```
✔ Project name: … my-vue-app
✔ Add TypeScript? … No
✔ Add JSX Support? … No
✔ Add Vue Router for Single Page Application development? … No
✔ Add Pinia for state management? … No
✔ Add Vitest for Unit Testing? … No
✔ Add an End-to-End Testing Solution? › No
✔ Add ESLint for code quality? … Yes
✔ Add Prettier for code formatting? … Yes
```

3. 进入项目并安装依赖：
```bash
cd my-vue-app
npm install
```

### 项目结构说明

```
my-vue-app/
├── index.html                 # HTML入口文件
├── package.json              # 项目配置
├── vite.config.js            # Vite配置
├── src/
│   ├── main.js               # 应用入口
│   ├── App.vue               # 根组件
│   ├── assets/               # 静态资源
│   └── components/           # 组件目录
└── public/                   # 公共资源
```

---

## 🏗️ 第二步：创建你的第一个组件

### 1. 创建计数器组件

```vue
<!-- src/components/Counter.vue -->
<template>
  <div class="counter">
    <h2>计数器：{{ count }}</h2>
    <div class="buttons">
      <button @click="decrement" :disabled="count <= 0">-</button>
      <button @click="increment">+</button>
      <button @click="reset">重置</button>
    </div>
    <p>点击次数：{{ clickCount }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 响应式数据
const count = ref(0)
const clickCount = ref(0)

// 方法
function increment() {
  count.value++
  clickCount.value++
}

function decrement() {
  if (count.value > 0) {
    count.value--
    clickCount.value++
  }
}

function reset() {
  count.value = 0
  clickCount.value = 0
}
</script>

<style scoped>
.counter {
  border: 2px solid #42b883;
  border-radius: 8px;
  padding: 20px;
  margin: 20px 0;
  text-align: center;
  background-color: #f9f9f9;
}

.counter h2 {
  color: #42b883;
  margin-bottom: 15px;
}

.buttons {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin: 15px 0;
}

.buttons button {
  padding: 8px 16px;
  font-size: 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.buttons button:not(:disabled) {
  background-color: #42b883;
  color: white;
}

.buttons button:not(:disabled):hover {
  background-color: #369870;
}

.buttons button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

p {
  color: #666;
  font-style: italic;
}
</style>
```

### 2. 创建用户卡片组件

```vue
<!-- src/components/UserCard.vue -->
<template>
  <div class="user-card">
    <div class="avatar">
      <img :src="user.avatar" :alt="user.name">
    </div>
    <div class="user-info">
      <h3>{{ user.name }}</h3>
      <p>年龄：{{ user.age }}</p>
      <p>城市：{{ user.city }}</p>
      <p>状态：<span :class="['status', user.status]">{{ statusText }}</span></p>
    </div>
    <div class="actions">
      <button @click="toggleStatus">切换状态</button>
      <button @click="celebrateBirthday" :disabled="user.age >= 100">
        过生日
      </button>
    </div>
  </div>
</template>

<script setup>
import { reactive, computed } from 'vue'

// 定义组件属性
const props = defineProps({
  initialUser: {
    type: Object,
    required: true
  }
})

// 响应式数据
const user = reactive({ ...props.initialUser })

// 计算属性
const statusText = computed(() => {
  const statusMap = {
    active: '在线',
    busy: '忙碌',
    offline: '离线'
  }
  return statusMap[user.status] || '未知'
})

// 方法
function toggleStatus() {
  const statuses = ['active', 'busy', 'offline']
  const currentIndex = statuses.indexOf(user.status)
  user.status = statuses[(currentIndex + 1) % statuses.length]
}

function celebrateBirthday() {
  if (user.age < 100) {
    user.age++
    alert(`🎉 ${user.name} 过生日了！现在 ${user.age} 岁！`)
  }
}
</script>

<style scoped>
.user-card {
  border: 1px solid #ddd;
  border-radius: 12px;
  padding: 20px;
  margin: 15px 0;
  display: flex;
  align-items: center;
  gap: 20px;
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: transform 0.2s;
}

.user-card:hover {
  transform: translateY(-2px);
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-info {
  flex-grow: 1;
}

.user-info h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.user-info p {
  margin: 5px 0;
  color: #666;
}

.status {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
}

.status.active {
  background-color: #d4edda;
  color: #155724;
}

.status.busy {
  background-color: #fff3cd;
  color: #856404;
}

.status.offline {
  background-color: #f8d7da;
  color: #721c24;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.actions button {
  padding: 6px 12px;
  border: 1px solid #42b883;
  background-color: white;
  color: #42b883;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.actions button:hover:not(:disabled) {
  background-color: #42b883;
  color: white;
}

.actions button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
```

---

## 🎉 第三步：在App.vue中使用组件

### 更新App.vue

```vue
<!-- src/App.vue -->
<template>
  <div id="app">
    <header class="header">
      <h1>🚀 我的Vue应用</h1>
      <p>体验响应式和组件化的威力！</p>
    </header>

    <main class="main">
      <!-- 计数器组件 -->
      <section class="section">
        <h2>📊 计数器组件</h2>
        <Counter />
      </section>

      <!-- 用户卡片组件 -->
      <section class="section">
        <h2>👥 用户卡片组件</h2>
        <div class="users-grid">
          <UserCard
            v-for="user in users"
            :key="user.id"
            :initial-user="user"
          />
        </div>
      </section>

      <!-- 交互统计 -->
      <section class="section">
        <h2>📈 应用统计</h2>
        <div class="stats">
          <div class="stat-item">
            <span class="stat-label">用户数量：</span>
            <span class="stat-value">{{ users.length }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">平均年龄：</span>
            <span class="stat-value">{{ averageAge }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">在线用户：</span>
            <span class="stat-value">{{ onlineUsers }}</span>
          </div>
        </div>
      </section>
    </main>

    <footer class="footer">
      <p>使用Vue 3 + Vite构建 | 快速入门演示</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import Counter from './components/Counter.vue'
import UserCard from './components/UserCard.vue'

// 响应式数据
const users = ref([
  {
    id: 1,
    name: '张三',
    age: 25,
    city: '北京',
    status: 'active',
    avatar: 'https://picsum.photos/seed/user1/150/150.jpg'
  },
  {
    id: 2,
    name: '李四',
    age: 30,
    city: '上海',
    status: 'busy',
    avatar: 'https://picsum.photos/seed/user2/150/150.jpg'
  },
  {
    id: 3,
    name: '王五',
    age: 28,
    city: '深圳',
    status: 'offline',
    avatar: 'https://picsum.photos/seed/user3/150/150.jpg'
  }
])

// 计算属性
const averageAge = computed(() => {
  if (users.value.length === 0) return 0
  const total = users.value.reduce((sum, user) => sum + user.age, 0)
  return (total / users.value.length).toFixed(1)
})

const onlineUsers = computed(() => {
  return users.value.filter(user => user.status === 'active').length
})
</script>

<style>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  line-height: 1.6;
  color: #333;
  background-color: #f5f5f5;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 2rem 1rem;
  text-align: center;
}

.header h1 {
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
}

.header p {
  font-size: 1.2rem;
  opacity: 0.9;
}

.main {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
  width: 100%;
}

.section {
  margin-bottom: 3rem;
}

.section h2 {
  color: #333;
  margin-bottom: 1.5rem;
  font-size: 1.8rem;
  border-bottom: 2px solid #42b883;
  padding-bottom: 0.5rem;
}

.users-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
}

.stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.stat-item {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  text-align: center;
}

.stat-label {
  display: block;
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.stat-value {
  display: block;
  color: #42b883;
  font-size: 2rem;
  font-weight: bold;
}

.footer {
  background-color: #333;
  color: white;
  text-align: center;
  padding: 1rem;
  margin-top: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header h1 {
    font-size: 2rem;
  }

  .header p {
    font-size: 1rem;
  }

  .users-grid {
    grid-template-columns: 1fr;
  }

  .stats {
    grid-template-columns: 1fr;
  }
}
</style>
```

---

## 🚀 第四步：运行并测试

### 启动开发服务器

```bash
npm run dev
```

看到以下输出说明启动成功：
```
  VITE v5.x.x  ready in xxx ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
  ➜  press h to show help
```

### 测试功能

打开浏览器访问：`http://localhost:5173`

**你会看到：**

1. **计数器组件**
   - 点击"+"按钮增加计数
   - 点击"-"按钮减少计数（到0时禁用）
   - 点击"重置"按钮清零
   - 显示点击次数统计

2. **用户卡片组件**
   - 显示3个用户的信息卡片
   - 点击"切换状态"改变用户在线状态
   - 点击"过生日"增加年龄
   - 状态用不同颜色显示

3. **应用统计**
   - 实时显示用户数量
   - 自动计算平均年龄
   - 统计在线用户数量

---

## 🔍 第五步：理解发生了什么？

### 神奇之处分析

让我们分析刚才的代码：

```vue
<!-- 我们从未写过这样的代码： -->
<!-- document.querySelector('.count').textContent = count; -->
<!-- document.querySelector('.status').className = 'status active'; -->

<!-- 而是直接使用： -->
<template>
  <h2>计数器：{{ count }}</h2>  <!-- 数据变化自动更新 -->
  <span :class="['status', user.status]">{{ statusText }}</span>  <!-- 动态样式 -->
</template>
```

**传统方式 vs Vue方式：**

```javascript
// ❌ 传统方式（如果我们不用Vue）
function updateCount() {
  count++;
  // 必须手动更新DOM
  document.querySelector('.count-display').textContent = count;
  document.querySelector('.click-count').textContent = clickCount;
}

// ✅ Vue方式
function updateCount() {
  count.value++;  // Vue自动处理所有DOM更新
}
```

### 响应式系统的工作流程

```mermaid
sequenceDiagram
    participant User as 用户
    participant Vue as Vue响应式系统
    component Data as 响应式数据
    component View as 视图模板

    User->>Data: 修改数据(count++)
    Data->>Vue: 通知数据变化
    Vue->>View: 计算需要更新的DOM
    Vue->>View: 智能更新DOM节点
    Note over User,View: 用户看到最新界面
```

### 组件化的威力

```vue
<!-- 我们写了一个UserCard组件 -->
<UserCard :initial-user="user1" />  <!-- 复用1次 -->
<UserCard :initial-user="user2" />  <!-- 复用2次 -->
<UserCard :initial-user="user3" />  <!-- 复用3次 -->

<!-- 每个组件都有独立的状态和数据 -->
```

---

## 🛠️ 第六步：添加更多功能

### 添加搜索功能

```vue
<!-- 在App.vue中添加搜索区域 -->
<template>
  <div id="app">
    <!-- 搜索区域 -->
    <section class="search-section">
      <div class="search-box">
        <input
          v-model="searchText"
          placeholder="搜索用户姓名..."
          @input="filterUsers"
        >
        <button @click="clearSearch">清除</button>
      </div>
      <p v-if="searchText">找到 {{ filteredUsers.length }} 个用户</p>
    </section>

    <!-- 用户卡片区域 - 使用过滤后的用户 -->
    <section class="section">
      <h2>👥 用户卡片组件</h2>
      <div class="users-grid">
        <UserCard
          v-for="user in filteredUsers"
          :key="user.id"
          :initial-user="user"
        />
      </div>
      <p v-if="filteredUsers.length === 0" class="no-results">
        没有找到匹配的用户
      </p>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
// ... 其他导入保持不变

// 搜索相关数据
const searchText = ref('')
const filteredUsers = ref([...users.value])

// 监听搜索文本变化
watch(searchText, (newText) => {
  filterUsers()
})

// 搜索方法
function filterUsers() {
  if (!searchText.value.trim()) {
    filteredUsers.value = [...users.value]
  } else {
    const searchLower = searchText.value.toLowerCase()
    filteredUsers.value = users.value.filter(user =>
      user.name.toLowerCase().includes(searchLower)
    )
  }
}

function clearSearch() {
  searchText.value = ''
  filteredUsers.value = [...users.value]
}

// 监听原始用户数据变化，更新过滤结果
watch(users, () => {
  filterUsers()
}, { deep: true })
</script>

<style scoped>
/* 添加搜索样式 */
.search-section {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  margin-bottom: 2rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.search-box {
  display: flex;
  gap: 10px;
  margin-bottom: 1rem;
}

.search-box input {
  flex: 1;
  padding: 10px 15px;
  border: 2px solid #ddd;
  border-radius: 25px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.search-box input:focus {
  outline: none;
  border-color: #42b883;
}

.search-box button {
  padding: 10px 20px;
  background-color: #f8f9fa;
  border: 2px solid #ddd;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s;
}

.search-box button:hover {
  background-color: #e9ecef;
}

.no-results {
  text-align: center;
  color: #666;
  font-style: italic;
  padding: 2rem;
}
</style>
```

---

## 🎯 第六步：理解核心概念

### 你已经掌握的核心概念

1. **响应式数据** - `ref`和`reactive`的使用
2. **模板指令** - `{{ }}`、`v-for`、`v-bind`、`v-on`
3. **组件化** - 组件的定义和复用
4. **Props传递** - 父子组件通信
5. **计算属性** - `computed`的使用
6. **事件处理** - `@click`等事件绑定
7. **生命周期** - `watch`监听数据变化

### 响应式和组件化的实际体现

```vue
<!-- 我们从未写过这样的代码： -->
<!-- 手动更新DOM、手动绑定事件、手动管理状态 -->

<!-- 而是直接使用： -->
<template>
  <!-- 声明式模板 -->
  <UserCard :initial-user="user" @status-change="handleStatusChange" />
</template>

<script setup>
// 响应式数据管理
const user = reactive({ name: '张三', age: 25 })
</script>
```

这就是Vue响应式 + 组件化的魔力！

---

## 🚀 进阶练习

### 练习1：添加用户功能

```vue
<!-- 在App.vue中添加添加用户表单 -->
<template>
  <section class="section">
    <h2>➕ 添加新用户</h2>
    <form @submit.prevent="addUser" class="add-user-form">
      <div class="form-group">
        <label>姓名：</label>
        <input v-model="newUser.name" required>
      </div>
      <div class="form-group">
        <label>年龄：</label>
        <input type="number" v-model.number="newUser.age" min="1" max="100" required>
      </div>
      <div class="form-group">
        <label>城市：</label>
        <input v-model="newUser.city" required>
      </div>
      <button type="submit">添加用户</button>
    </form>
  </section>
</template>

<script setup>
// 添加用户相关数据
const newUser = reactive({
  name: '',
  age: 25,
  city: '',
  status: 'active',
  avatar: 'https://picsum.photos/seed/newuser/150/150.jpg'
})

// 添加用户方法
function addUser() {
  if (newUser.name.trim()) {
    const id = Math.max(...users.value.map(u => u.id)) + 1
    users.value.push({
      id,
      ...newUser,
      avatar: `https://picsum.photos/seed/user${id}/150/150.jpg`
    })

    // 重置表单
    Object.assign(newUser, {
      name: '',
      age: 25,
      city: '',
      status: 'active'
    })

    alert(`用户 ${newUser.name} 添加成功！`)
  }
}
</script>

<style scoped>
.add-user-form {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
}

.form-group input {
  width: 100%;
  padding: 8px 12px;
  border: 2px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.form-group input:focus {
  outline: none;
  border-color: #42b883;
}

.add-user-form button {
  background-color: #42b883;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.add-user-form button:hover {
  background-color: #369870;
}
</style>
```

---

## 📋 学习检查清单

### ✅ 基础概念掌握
- [ ] 理解什么是响应式系统
- [ ] 理解什么是组件化开发
- [ ] 知道ref和reactive的区别
- [ ] 掌握组件的基本使用

### ✅ 实践能力
- [ ] 能够创建Vue项目
- [ ] 能够编写Vue组件
- [ ] 能够使用Props传递数据
- [ ] 能够处理用户交互事件

### ✅ Vue特性掌握
- [ ] 熟练使用常用指令
- [ ] 掌握Composition API
- [ ] 理解计算属性的作用
- [ ] 能够监听数据变化

---

## 🎯 下一步学习

恭喜你完成了快速入门！接下来建议按顺序学习：

1. **深入Vue核心**
   - [[01-Vue响应式原理与组件化思想.md|Vue响应式原理与组件化思想]]
   - [[02-Vue指令与CompositionAPI体系概览.md|Vue指令与CompositionAPI体系概览]]

2. **学习组件进阶**
   - 组件通信方式详解
   - 插槽(Slots)使用
   - 动态组件和异步组件

3. **掌握状态管理**
   - Pinia状态管理
   - 跨组件状态共享

---

## 🎉 总结

通过这个快速入门，你已经：
- 创建了第一个Vue应用
- 体验了响应式系统的便利
- 掌握了组件化开发的优势
- 理解了Vue的核心价值

**记住：Vue的核心就是"数据驱动视图，组件化构建"！** 🚀

继续加油，Vue的世界很精彩！