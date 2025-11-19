---
tags:
  - Vue模板语法
  - 数据绑定
  - 插值表达式
  - 属性绑定
  - 双向绑定
  - Vue3
created: 2025-11-18
modified: 2025-11-18
category: Vue核心概念
difficulty: beginner
---

# Vue模板语法与数据绑定

> **学习目标**：掌握Vue的数据绑定机制，理解如何在模板中显示和操作数据

## 🎯 数据绑定的意义

数据绑定是Vue的"魔法"，它让数据的变化自动反映到视图上，视图的修改也能自动更新数据。

**数据绑定的好处：**
- 自动同步数据和视图
- 减少手动DOM操作
- 让代码更简洁易读
- 提升开发效率

---

## 📝 文本插值 - {{ }}

最基础的数据显示方式，将数据直接渲染为文本：

### 基本用法

```vue
<template>
  <div>
    <!-- 1. 基本文本插值 -->
    <h1>{{ message }}</h1>
    <p>{{ description }}</p>

    <!-- 2. 支持表达式计算 -->
    <p>数量: {{ quantity }}</p>
    <p>总价: {{ price * quantity }}</p>
    <p>状态: {{ isLoggedIn ? '已登录' : '未登录' }}</p>

    <!-- 3. 支持方法调用 -->
    <p>格式化日期: {{ formatDate(orderDate) }}</p>
    <p>用户名大写: {{ userName.toUpperCase() }}</p>

    <!-- 4. 支持对象属性访问 -->
    <p>用户姓名: {{ user.name }}</p>
    <p>用户邮箱: {{ user.contact.email }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const message = ref('欢迎来到Vue学习!')
const description = ref('这里是最详细的Vue教程')
const quantity = ref(3)
const price = ref(99.99)
const isLoggedIn = ref(true)
const orderDate = ref(new Date())
const userName = ref('john_doe')

const user = ref({
  name: '张三',
  age: 25,
  contact: {
    email: 'zhangsan@example.com',
    phone: '13800138000'
  }
})

// 格式化日期方法
function formatDate(date) {
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}
</script>
```

### 插值表达式的限制

```vue
<template>
  <!-- ✅ 可以使用的表达式 -->
  <p>{{ number + 1 }}</p>
  <p>{{ message.split('').reverse().join('') }}</p>
  <p>{{ user.name || '匿名用户' }}</p>

  <!-- ❌ 不能使用的语句 -->
  <!-- <p>{{ let a = 1 }}</p> -->           <!-- 不能声明变量 -->
  <!-- <p>{{ if (condition) { ... } }}</p> --> <!-- 不能使用控制流 -->
  <!-- <p>{{ return value }}</p> -->        <!-- 不能使用return -->
</template>
```

---

## 🔗 属性绑定 - v-bind

用于将数据绑定到HTML属性上，简写为冒号 `:`

### 基本属性绑定

```vue
<template>
  <div>
    <!-- 1. 基本属性绑定 -->
    <img :src="avatarUrl" :alt="userName">
    <a :href="productLink" :title="productTitle">查看商品</a>

    <!-- 2. 动态class绑定 -->
    <div :class="{ active: isActive, 'text-danger': hasError }">
      动态class样式
    </div>

    <!-- 3. 动态style绑定 -->
    <div :style="{ color: textColor, fontSize: fontSize + 'px' }">
      动态style样式
    </div>

    <!-- 4. 布尔属性绑定 -->
    <button :disabled="!isAvailable" @click="addToCart">
      {{ isAvailable ? '加入购物车' : '暂时缺货' }}
    </button>

    <!-- 5. 数组语法绑定class -->
    <div :class="[baseClass, { active: isActive }, errorClass]">
      多重class绑定
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const avatarUrl = ref('/images/avatar.jpg')
const userName = ref('用户头像')
const productLink = ref('https://example.com/product/123')
const productTitle = ref('查看商品详情')

const isActive = ref(true)
const hasError = ref(false)
const textColor = ref('blue')
const fontSize = ref(16)
const isAvailable = ref(true)

const baseClass = ref('card')
const errorClass = ref('error-text')

function addToCart() {
  console.log('商品已加入购物车')
}
</script>

<style>
.active {
  border: 2px solid #42b983;
  background-color: #f0f9ff;
}

.text-danger {
  color: #dc3545;
}

.card {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.error-text {
  color: #dc3545;
  font-weight: bold;
}
</style>
```

### 对象语法和数组语法

```vue
<template>
  <div>
    <!-- 对象语法：适合复杂的class逻辑 -->
    <h3 :class="classObject">使用对象语法</h3>

    <!-- 数组语法：适合组合多个class -->
    <h3 :class="classList">使用数组语法</h3>

    <!-- 混合使用：对象 + 数组 -->
    <h3 :class="[baseClasses, extraClasses]">混合语法</h3>

    <!-- 样式绑定对象语法 -->
    <div :style="styleObject">样式对象</div>

    <!-- 样式绑定数组语法（多重样式） -->
    <div :style="[baseStyles, overrideStyles]">样式数组</div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const isActive = ref(true)
const hasError = ref(false)
const isWarning = ref(true)

// class对象
const classObject = computed(() => ({
  'text-success': !hasError.value && isActive.value,
  'text-danger': hasError.value,
  'text-warning': isWarning.value
}))

// class数组
const classList = computed(() => [
  'base-class',
  isActive.value ? 'active-class' : 'inactive-class',
  hasError.value ? 'error-class' : ''
])

// 混合语法
const baseClasses = ref(['container', 'layout'])
const extraClasses = computed(() => ({
  'theme-dark': true,
  'has-sidebar': true
}))

// 样式对象
const styleObject = computed(() => ({
  color: isActive.value ? '#42b983' : '#dc3545',
  fontSize: '18px',
  fontWeight: 'bold',
  padding: '10px',
  border: `2px solid ${isActive.value ? '#42b983' : '#dc3545'}`
}))

// 样式数组
const baseStyles = ref({
  backgroundColor: '#f8f9fa',
  padding: '15px'
})

const overrideStyles = ref({
  color: '#333',
  border: '1px solid #dee2e6'
})
</script>

<style>
.text-success { color: #28a745; }
.text-danger { color: #dc3545; }
.text-warning { color: #ffc107; }

.base-class {
  padding: 8px;
  margin: 5px 0;
}

.active-class {
  background-color: #e7f3ff;
}

.inactive-class {
  background-color: #f8f9fa;
}

.error-class {
  border: 1px solid #dc3545;
}

.container {
  max-width: 1200px;
}

.layout {
  display: flex;
}

.theme-dark {
  background-color: #333;
  color: white;
}

.has-sidebar {
  display: grid;
  grid-template-columns: 200px 1fr;
}
</style>
```

---

## 🔄 双向数据绑定 - v-model

实现表单元素和数据的双向绑定，用户输入自动更新数据

### 基本表单绑定

```vue
<template>
  <div class="form-container">
    <h3>用户信息表单</h3>

    <!-- 1. 文本输入框 -->
    <div class="form-group">
      <label>用户名:</label>
      <input v-model="userInfo.username" placeholder="请输入用户名">
      <span>当前值: {{ userInfo.username }}</span>
    </div>

    <!-- 2. 文本域 -->
    <div class="form-group">
      <label>个人简介:</label>
      <textarea v-model="userInfo.bio" placeholder="介绍一下自己"></textarea>
      <span>字符数: {{ userInfo.bio.length }}</span>
    </div>

    <!-- 3. 单选按钮 -->
    <div class="form-group">
      <label>性别:</label>
      <label>
        <input type="radio" value="male" v-model="userInfo.gender"> 男
      </label>
      <label>
        <input type="radio" value="female" v-model="userInfo.gender"> 女
      </label>
      <span>选择: {{ userInfo.gender }}</span>
    </div>

    <!-- 4. 复选框 - 单个值 -->
    <div class="form-group">
      <label>
        <input type="checkbox" v-model="userInfo.agreeTerms">
        我同意用户协议
      </label>
      <span>同意状态: {{ userInfo.agreeTerms }}</span>
    </div>

    <!-- 5. 复选框 - 多个值 -->
    <div class="form-group">
      <label>兴趣爱好:</label>
      <label>
        <input type="checkbox" value="reading" v-model="userInfo.hobbies"> 阅读
      </label>
      <label>
        <input type="checkbox" value="sports" v-model="userInfo.hobbies"> 运动
      </label>
      <label>
        <input type="checkbox" value="music" v-model="userInfo.hobbies"> 音乐
      </label>
      <label>
        <input type="checkbox" value="coding" v-model="userInfo.hobbies"> 编程
      </label>
      <span>已选择: {{ userInfo.hobbies.join(', ') }}</span>
    </div>

    <!-- 6. 下拉选择框 - 单选 -->
    <div class="form-group">
      <label>城市:</label>
      <select v-model="userInfo.city">
        <option value="">请选择城市</option>
        <option value="beijing">北京</option>
        <option value="shanghai">上海</option>
        <option value="guangzhou">广州</option>
        <option value="shenzhen">深圳</option>
      </select>
      <span>选择的城市: {{ userInfo.city }}</span>
    </div>

    <!-- 7. 下拉选择框 - 多选 -->
    <div class="form-group">
      <label>技能 (按住Ctrl多选):</label>
      <select v-model="userInfo.skills" multiple>
        <option value="javascript">JavaScript</option>
        <option value="vue">Vue.js</option>
        <option value="react">React</option>
        <option value="node">Node.js</option>
        <option value="python">Python</option>
      </select>
      <span>已选技能: {{ userInfo.skills.join(', ') }}</span>
    </div>

    <!-- 8. 修饰符示例 -->
    <div class="form-group">
      <label>年龄 (只能输入数字):</label>
      <input v-model.number="userInfo.age" type="number">
      <span>年龄: {{ userInfo.age }} (类型: {{ typeof userInfo.age }})</span>
    </div>

    <div class="form-group">
      <label>搜索内容 (去除首尾空格):</label>
      <input v-model.trim="userInfo.searchText" placeholder="输入搜索内容">
      <span>"{{ userInfo.searchText}}" (长度: {{ userInfo.searchText.length }})</span>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'

const userInfo = reactive({
  username: '',
  bio: '',
  gender: 'male',
  agreeTerms: false,
  hobbies: [],
  city: '',
  skills: [],
  age: null,
  searchText: ''
})
</script>

<style scoped>
.form-container {
  max-width: 600px;
  margin: 20px auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.form-group {
  margin-bottom: 15px;
  padding: 10px;
  border-bottom: 1px solid #eee;
}

.form-group:last-child {
  border-bottom: none;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
}

.form-group textarea {
  height: 80px;
  resize: vertical;
}

.form-group span {
  display: block;
  margin-top: 5px;
  color: #666;
  font-size: 12px;
}

input[type="radio"],
input[type="checkbox"] {
  width: auto;
  margin-right: 5px;
}
</style>
```

### 自定义组件的v-model

```vue
<template>
  <div>
    <h3>自定义输入组件</h3>

    <!-- 使用自定义组件的v-model -->
    <CustomInput
      v-model="searchText"
      placeholder="请输入搜索内容"
      label="搜索:"
    />

    <p>搜索内容: "{{ searchText }}"</p>

    <!-- 自定义开关组件 -->
    <CustomSwitch v-model="isDarkMode" label="深色模式:" />
    <p>深色模式: {{ isDarkMode ? '开启' : '关闭' }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import CustomInput from './CustomInput.vue'
import CustomSwitch from './CustomSwitch.vue'

const searchText = ref('')
const isDarkMode = ref(false)
</script>

<!-- CustomInput.vue -->
<template>
  <div class="custom-input">
    <label>{{ label }}</label>
    <input
      :value="modelValue"
      @input="handleInput"
      :placeholder="placeholder"
      class="input-field"
    />
  </div>
</template>

<script setup>
const props = defineProps({
  modelValue: String,
  placeholder: String,
  label: String
})

const emit = defineEmits(['update:modelValue'])

function handleInput(event) {
  emit('update:modelValue', event.target.value)
}
</script>

<!-- CustomSwitch.vue -->
<template>
  <div class="custom-switch">
    <label>{{ label }}</label>
    <button
      @click="toggleSwitch"
      :class="{ active: modelValue }"
      class="switch-button"
    >
      {{ modelValue ? 'ON' : 'OFF' }}
    </button>
  </div>
</template>

<script setup>
const props = defineProps({
  modelValue: Boolean,
  label: String
})

const emit = defineEmits(['update:modelValue'])

function toggleSwitch() {
  emit('update:modelValue', !props.modelValue)
}
</script>

<style scoped>
.custom-input,
.custom-switch {
  margin: 10px 0;
}

.input-field {
  padding: 8px 12px;
  border: 2px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  margin-left: 10px;
}

.switch-button {
  padding: 8px 16px;
  border: 2px solid #ddd;
  background-color: #f8f9fa;
  cursor: pointer;
  margin-left: 10px;
}

.switch-button.active {
  background-color: #42b983;
  color: white;
  border-color: #42b983;
}
</style>
```

---

## 🌐 HTML内容渲染 - v-html

用于渲染包含HTML标签的内容

### 基本用法

```vue
<template>
  <div class="html-rendering">
    <h3>HTML内容渲染</h3>

    <!-- 1. 基本HTML渲染 -->
    <div class="content-box">
      <h4>普通文本插值 vs HTML渲染</h4>
      <p><strong>文本插值:</strong> {{ richContent }}</p>
      <p><strong>HTML渲染:</strong> <span v-html="richContent"></span></p>
    </div>

    <!-- 2. 动态内容渲染 -->
    <div class="content-box">
      <h4>动态内容示例</h4>
      <div v-html="formattedContent"></div>
    </div>

    <!-- 3. 用户输入的HTML渲染（危险！） -->
    <div class="content-box">
      <h4>用户输入渲染示例</h4>
      <textarea
        v-model="userInput"
        placeholder="输入HTML内容（注意安全性）"
      ></textarea>
      <div class="render-preview">
        <strong>渲染结果:</strong>
        <div v-html="sanitizedContent"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const richContent = ref(`
  <p style="color: blue; font-weight: bold;">
    这是一个<strong>加粗</strong>的<em>斜体</em>文本，
    包含<code>代码</code>和<a href="#">链接</a>。
  </p>
`)

const articleContent = ref(`
  <h2>Vue 3 新特性</h2>
  <ul>
    <li><strong>Composition API</strong>: 更好的逻辑复用</li>
    <li><strong>更好的TypeScript支持</strong>: 完整的类型推导</li>
    <li><strong>性能提升</strong>: 更小的包大小，更快的渲染</li>
  </ul>
  <blockquote>
    "Vue 3 是现代前端开发的理想选择"
  </blockquote>
`)

const userInput = ref('<span style="color: red;">用户输入的内容</span>')

// 格式化内容示例
const formattedContent = computed(() => {
  return `
    <div class="article">
      ${articleContent.value}
      <div style="margin-top: 20px; padding: 15px; background-color: #f8f9fa; border-left: 4px solid #42b983;">
        <strong>提示:</strong> 这是一个动态生成的内容块
      </div>
    </div>
  `
})

// 简单的内容净化（仅演示，实际应使用专门的净化库）
const sanitizedContent = computed(() => {
  // 这里仅作简单演示，实际项目应使用DOMPurify等专门库
  return userInput.value
    .replace(/<script[^>]*>.*?<\/script>/gi, '') // 移除script标签
    .replace(/javascript:/gi, '') // 移除javascript协议
    .replace(/on\w+\s*=/gi, '') // 移除事件处理器
})
</script>

<style scoped>
.html-rendering {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}

.content-box {
  margin: 20px 0;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #fafafa;
}

.content-box h4 {
  margin-top: 0;
  color: #333;
}

.render-preview {
  margin-top: 10px;
  padding: 10px;
  background-color: #fff;
  border: 1px solid #ccc;
  border-radius: 4px;
}

textarea {
  width: 100%;
  height: 80px;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-family: monospace;
}

:deep(.article) {
  line-height: 1.6;
}

:deep(.article h2) {
  color: #42b983;
  border-bottom: 2px solid #42b983;
  padding-bottom: 5px;
}

:deep(.article ul) {
  margin: 15px 0;
}

:deep(.article li) {
  margin: 8px 0;
}

:deep(.article blockquote) {
  font-style: italic;
  color: #666;
  border-left: 4px solid #ddd;
  padding-left: 15px;
  margin: 15px 0;
}
</style>
```

### 安全注意事项

```vue
<template>
  <div class="security-demo">
    <h3>⚠️ 安全注意事项</h3>

    <div class="warning-box">
      <h4>🚨 永远不要对不受信任的内容使用v-html！</h4>
      <p>以下示例展示了XSS攻击的风险：</p>
    </div>

    <!-- 危险示例 - 不要在生产环境中这样做 -->
    <div class="danger-zone">
      <h4>危险的XSS攻击示例</h4>
      <textarea
        v-model="maliciousInput"
        placeholder="尝试输入恶意脚本，如: <script>alert('XSS攻击')</script>"
      ></textarea>
      <button @click="showDangerousContent">渲染危险内容（仅演示）</button>
      <div v-if="showDangerous" v-html="maliciousInput" class="danger-result"></div>
    </div>

    <!-- 安全做法 -->
    <div class="safe-zone">
      <h4>✅ 安全的做法</h4>
      <textarea
        v-model="safeInput"
        placeholder="输入HTML内容"
      ></textarea>
      <button @click="showSafeContent">显示净化后的内容</button>
      <div v-html="sanitizedSafeContent" class="safe-result"></div>
    </div>

    <!-- 推荐的安全做法：使用文本插值 -->
    <div class="recommended-zone">
      <h4>💡 推荐做法：使用文本插值</h4>
      <p>如果不需要HTML格式，直接使用文本插值最安全：</p>
      <div class="text-display">
        {{ userInputSafe }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const maliciousInput = ref('<img src="x" onerror="alert(\'XSS攻击示例！\')">')
const showDangerous = ref(false)
const safeInput = ref('<strong>安全的内容</strong> <em>支持基本格式</em>')
const userInputSafe = ref('<script>alert("这不会执行")</script>这只是普通文本')

const sanitizedSafeContent = computed(() => {
  // 基本的HTML净化（仅演示）
  return safeInput.value
    .replace(/<script[^>]*>.*?<\/script>/gi, '')
    .replace(/on\w+\s*=/gi, '')
    .replace(/javascript:/gi, '')
})

function showDangerousContent() {
  showDangerous.value = true
  setTimeout(() => {
    showDangerous.value = false
  }, 3000)
}

function showSafeContent() {
  // 安全的内容已经通过computed处理
  console.log('显示安全内容')
}
</script>

<style scoped>
.security-demo {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}

.warning-box {
  background-color: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
}

.danger-zone, .safe-zone, .recommended-zone {
  margin: 20px 0;
  padding: 15px;
  border-radius: 8px;
}

.danger-zone {
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
}

.safe-zone {
  background-color: #d4edda;
  border: 1px solid #c3e6cb;
}

.recommended-zone {
  background-color: #d1ecf1;
  border: 1px solid #bee5eb;
}

textarea {
  width: 100%;
  height: 60px;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  margin: 10px 0;
}

button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin: 5px 0;
}

.danger-zone button {
  background-color: #dc3545;
  color: white;
}

.safe-zone button {
  background-color: #28a745;
  color: white;
}

.danger-result, .safe-result {
  margin-top: 10px;
  padding: 10px;
  border-radius: 4px;
  background-color: white;
  border: 1px solid #ddd;
}

.text-display {
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 4px;
  font-family: monospace;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
```

---

## 🎯 实际应用：商品展示组件

```vue
<template>
  <div class="product-card">
    <div class="product-image">
      <img :src="product.imageUrl" :alt="product.name">
      <span v-if="product.isNew" class="new-badge">新品</span>
    </div>

    <div class="product-info">
      <h3 :title="product.description">{{ product.name }}</h3>
      <p class="description" v-html="product.shortDescription"></p>

      <div class="price-section">
        <span class="price">¥{{ product.price }}</span>
        <span v-if="product.originalPrice > product.price" class="original-price">
          ¥{{ product.originalPrice }}
        </span>
        <span class="discount" v-if="discountPercent > 0">
          {{ discountPercent }}% OFF
        </span>
      </div>

      <div class="product-tags">
        <span
          v-for="tag in product.tags"
          :key="tag"
          :class="getTagClass(tag)"
        >
          {{ tag }}
        </span>
      </div>

      <div class="purchase-section">
        <div class="quantity-control">
          <button @click="decreaseQuantity" :disabled="quantity <= 1">-</button>
          <input
            type="number"
            v-model.number="quantity"
            min="1"
            :max="product.stock"
          >
          <button @click="increaseQuantity" :disabled="quantity >= product.stock">+</button>
        </div>

        <button
          @click="addToCart"
          :disabled="product.stock === 0"
          :class="{ 'out-of-stock': product.stock === 0 }"
        >
          {{ product.stock === 0 ? '暂时缺货' : '加入购物车' }}
        </button>
      </div>

      <div class="stock-info">
        <span :style="{ color: stockColor }">
          库存: {{ product.stock }} 件
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  product: {
    type: Object,
    required: true,
    default: () => ({
      id: '',
      name: '',
      description: '',
      shortDescription: '',
      price: 0,
      originalPrice: 0,
      imageUrl: '',
      stock: 0,
      isNew: false,
      tags: []
    })
  }
})

const emit = defineEmits(['add-to-cart'])

const quantity = ref(1)

// 计算属性
const discountPercent = computed(() => {
  if (props.product.originalPrice > props.product.price) {
    return Math.round((1 - props.product.price / props.product.originalPrice) * 100)
  }
  return 0
})

const stockColor = computed(() => {
  if (props.product.stock === 0) return '#dc3545'
  if (props.product.stock < 10) return '#ffc107'
  return '#28a745'
})

// 方法
function getTagClass(tag) {
  const tagClasses = {
    '热销': 'tag-hot',
    '新品': 'tag-new',
    '限量': 'tag-limited',
    '特价': 'tag-sale'
  }
  return tagClasses[tag] || 'tag-default'
}

function increaseQuantity() {
  if (quantity.value < props.product.stock) {
    quantity.value++
  }
}

function decreaseQuantity() {
  if (quantity.value > 1) {
    quantity.value--
  }
}

function addToCart() {
  emit('add-to-cart', {
    product: props.product,
    quantity: quantity.value
  })
  quantity.value = 1 // 重置数量
}
</script>

<style scoped>
.product-card {
  max-width: 300px;
  border: 1px solid #e1e8ed;
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
}

.product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.product-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.new-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: #ff4757;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.product-info {
  padding: 15px;
}

.product-info h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #333;
}

.description {
  color: #666;
  font-size: 14px;
  margin: 0 0 15px 0;
  line-height: 1.4;
}

.price-section {
  margin-bottom: 15px;
}

.price {
  font-size: 20px;
  font-weight: bold;
  color: #e74c3c;
}

.original-price {
  text-decoration: line-through;
  color: #999;
  margin-left: 8px;
  font-size: 14px;
}

.discount {
  background-color: #e74c3c;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  margin-left: 8px;
}

.product-tags {
  margin-bottom: 15px;
}

.product-tags span {
  display: inline-block;
  padding: 4px 8px;
  margin: 2px 4px 2px 0;
  border-radius: 4px;
  font-size: 12px;
}

.tag-hot { background-color: #ff4757; color: white; }
.tag-new { background-color: #5f27cd; color: white; }
.tag-limited { background-color: #00d2d3; color: white; }
.tag-sale { background-color: #feca57; color: #333; }
.tag-default { background-color: #f1f2f6; color: #333; }

.purchase-section {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.quantity-control {
  display: flex;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.quantity-control button {
  border: none;
  background-color: #f8f9fa;
  padding: 8px 12px;
  cursor: pointer;
}

.quantity-control button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-control input {
  border: none;
  width: 50px;
  text-align: center;
  padding: 8px 4px;
}

.purchase-section button:not(.quantity-control button) {
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 4px;
  background-color: #42b983;
  color: white;
  cursor: pointer;
  font-weight: bold;
}

.purchase-section button.out-of-stock {
  background-color: #6c757d;
  cursor: not-allowed;
}

.stock-info {
  font-size: 14px;
  font-weight: bold;
}
</style>
```

---

## 📋 数据绑定速查表

| 绑定类型 | 语法 | 用途 | 示例 |
|----------|------|------|------|
| **文本插值** | `{{ }}` | 显示文本内容 | `<h1>{{ title }}</h1>` |
| **属性绑定** | `v-bind:` 或 `:` | 绑定HTML属性 | `<img :src="url">` |
| **双向绑定** | `v-model` | 表单双向数据绑定 | `<input v-model="name">` |
| **HTML渲染** | `v-html` | 渲染HTML内容 | `<div v-html="content"></div>` |
| **类绑定** | `:class` | 动态CSS类 | `<div :class="{ active: isActive }">` |
| **样式绑定** | `:style` | 动态内联样式 | `<div :style="{ color: textColor }">` |

### 修饰符速查

| 修饰符 | 语法 | 作用 | 示例 |
|--------|------|------|------|
| **v-model修饰符** | `.number` | 转换为数字 | `<input v-model.number="age">` |
| | `.trim` | 去除首尾空格 | `<input v-model.trim="text">` |
| | `.lazy` | 在change事件时更新 | `<input v-model.lazy="search">` |

---

## 🚀 下一步学习

掌握基础数据绑定后，继续学习：

- [[02-Vue核心概念/06-Vue条件渲染与列表渲染.md|条件渲染与列表渲染]]
- [[02-Vue核心概念/07-Vue事件处理与表单绑定.md|事件处理与表单绑定]]
- [[02-Vue核心概念/04-Vue计算属性与侦听器.md|计算属性与侦听器]]

---

**记住：数据绑定是Vue的核心能力，掌握了它们就掌握了Vue响应式编程的基础！** 🎉