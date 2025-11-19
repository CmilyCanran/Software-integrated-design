---
tags:
  - Vue事件处理
  - 表单绑定
  - v-on
  - 事件修饰符
  - 表单验证
  - Vue3
created: 2025-11-18
modified: 2025-11-18
category: Vue核心概念
difficulty: beginner
---

# Vue事件处理与表单绑定

> **学习目标**：掌握Vue的事件处理机制和表单数据绑定，构建交互式用户界面

## 🎯 事件处理与表单绑定的意义

事件处理和表单绑定是构建交互式应用的核心：

**事件处理**：
- 响应用户的各种操作（点击、输入、滚动等）
- 实现页面交互和动态效果
- 连接用户行为和应用逻辑

**表单绑定**：
- 自动同步表单数据和应用状态
- 简化表单验证和数据处理
- 提升用户体验和开发效率

---

## 🖱️ 事件处理基础

### v-on 指令和 @ 简写

Vue使用 `v-on` 指令监听DOM事件，通常使用简写 `@`：

```vue
<template>
  <div class="event-basics">
    <h3>事件处理基础</h3>

    <!-- 基本事件绑定 -->
    <div class="basic-events">
      <h4>基本事件</h4>
      <button @click="handleClick">点击我</button>
      <button @mouseover="handleMouseOver">鼠标悬停</button>
      <button @mouseout="handleMouseOut">鼠标离开</button>
      <button @dblclick="handleDoubleClick">双击</button>
    </div>

    <!-- 事件传参 -->
    <div class="event-params">
      <h4>事件传参</h4>
      <button @click="sayHello('Vue')">向Vue问好</button>
      <button @click="greetUser(userName)">问候用户</button>
      <button @click="increment(5)">增加5</button>
      <button @click="showMessage('成功', 'success')">显示成功消息</button>
    </div>

    <!-- 事件对象 -->
    <div class="event-object">
      <h4>事件对象</h4>
      <button @click="handleEvent">获取事件信息</button>
      <div @click="handleDivClick" class="clickable-div">
        <button @click.stop="handleButtonClick">点击按钮（阻止冒泡）</button>
      </div>
      <input @keyup="handleKeyUp" placeholder="按任意键">
      <div class="mouse-position" @mousemove="handleMouseMove">
        鼠标位置: {{ mousePosition.x }}, {{ mousePosition.y }}
      </div>
    </div>

    <!-- 同时传递参数和事件对象 -->
    <div class="event-with-params">
      <h4>参数 + 事件对象</h4>
      <button @click="handleClickWithParams('按钮1', $event)">按钮1</button>
      <button @click="handleClickWithParams('按钮2', $event)">按钮2</button>
      <p>最后点击: {{ lastClickedButton }} at {{ clickPosition }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const userName = ref('张三')
const mousePosition = ref({ x: 0, y: 0 })
const lastClickedButton = ref('')
const clickPosition = ref('')

// 基本事件处理
function handleClick() {
  console.log('按钮被点击了!')
  alert('你点击了按钮!')
}

function handleMouseOver() {
  console.log('鼠标悬停在按钮上')
}

function handleMouseOut() {
  console.log('鼠标离开按钮')
}

function handleDoubleClick() {
  console.log('双击事件触发')
  alert('双击成功!')
}

// 事件传参
function sayHello(name) {
  console.log(`Hello, ${name}!`)
  alert(`你好, ${name}!`)
}

function greetUser(name) {
  console.log(`问候用户: ${name}`)
  alert(`欢迎, ${name}!`)
}

function increment(amount) {
  console.log(`增加 ${amount}`)
  // 这里可以更新计数器
}

function showMessage(text, type) {
  console.log(`显示${type}消息: ${text}`)
  alert(`[${type.toUpperCase()}] ${text}`)
}

// 事件对象处理
function handleEvent(event) {
  console.log('事件对象:', event)
  console.log('事件类型:', event.type)
  console.log('目标元素:', event.target)
  console.log('点击位置:', event.clientX, event.clientY)

  alert(`事件类型: ${event.type}, 点击位置: (${event.clientX}, ${event.clientY})`)
}

function handleDivClick(event) {
  console.log('div被点击')
  alert('div区域被点击')
}

function handleButtonClick(event) {
  console.log('按钮被点击')
  console.log('事件冒泡被阻止:', event.cancelBubble)
  alert('按钮被点击，但事件不会冒泡到div')
}

function handleKeyUp(event) {
  console.log('按键释放:', event.key)
  console.log('按键码:', event.keyCode)
}

function handleMouseMove(event) {
  mousePosition.value = {
    x: event.clientX,
    y: event.clientY
  }
}

// 参数 + 事件对象
function handleClickWithParams(buttonName, event) {
  lastClickedButton.value = buttonName
  clickPosition.value = `(${event.clientX}, ${event.clientY})`
  console.log(`${buttonName} 被点击`, event)
}
</script>

<style scoped>
.event-basics {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}

.basic-events, .event-params, .event-object, .event-with-params {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

button {
  margin: 5px;
  padding: 8px 16px;
  border: 1px solid #007bff;
  background-color: #007bff;
  color: white;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}

.clickable-div {
  margin: 10px 0;
  padding: 20px;
  background-color: #f8f9fa;
  border: 2px dashed #dee2e6;
  border-radius: 4px;
  cursor: pointer;
}

.mouse-position {
  margin: 10px 0;
  padding: 15px;
  background-color: #e9ecef;
  border-radius: 4px;
  font-family: monospace;
}

input {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  margin: 5px 0;
}
</style>
```

### 事件修饰符

Vue提供了丰富的修饰符来简化常见的事件处理操作：

```vue
<template>
  <div class="event-modifiers">
    <h3>事件修饰符</h3>

    <!-- .prevent 阻止默认行为 -->
    <div class="modifier-section">
      <h4>.prevent - 阻止默认行为</h4>
      <a href="https://www.example.com" @click.prevent="handleLinkClick">
        点击链接不会跳转
      </a>

      <form @submit.prevent="handleSubmit">
        <input v-model="message" placeholder="输入消息">
        <button type="submit">提交（不会刷新页面）</button>
      </form>
    </div>

    <!-- .stop 阻止事件冒泡 -->
    <div class="modifier-section">
      <h4>.stop - 阻止事件冒泡</h4>
      <div @click="outerClick" class="outer-div">
        外层div
        <div @click="middleClick" class="middle-div">
          中层div
          <button @click.stop="innerClick">内层按钮（阻止冒泡）</button>
        </div>
      </div>
      <p>点击日志: {{ clickLog.join(' → ') }}</p>
    </div>

    <!-- .capture 使用捕获模式 -->
    <div class="modifier-section">
      <h4>.capture - 捕获模式</h4>
      <div @click.capture="outerCapture" class="capture-div">
        外层（捕获）
        <div @click="innerCapture" class="capture-inner">
          内层
        </div>
      </div>
      <p>捕获日志: {{ captureLog.join(' → ') }}</p>
    </div>

    <!-- .self 只在自身触发 -->
    <div class="modifier-section">
      <h4>.self - 只在自身触发</h4>
      <div @click.self="selfClick" class="self-div">
        <p>点击这个区域的外部会触发</p>
        <div class="self-inner">
          点击这个内部区域不会触发外部事件
        </div>
      </div>
    </div>

    <!-- .once 只触发一次 -->
    <div class="modifier-section">
      <h4>.once - 只触发一次</h4>
      <button @click.once="onceClick">只能点击一次</button>
      <button @click.once="onceClick2">另一个只能点击一次</button>
      <p>点击次数统计: {{ onceCount }}</p>
    </div>

    <!-- .passive 被动模式 -->
    <div class="modifier-section">
      <h4>.passive - 被动模式</h4>
      <div @wheel.passive="handleWheel" class="wheel-div">
        在这个区域滚动鼠标滚轮（性能优化）
        <p>滚动次数: {{ wheelCount }}</p>
      </div>
    </div>

    <!-- 组合修饰符 -->
    <div class="modifier-section">
      <h4>组合修饰符</h4>
      <form @submit.prevent.stop="handleSubmitWithStop">
        <input v-model="formMessage" placeholder="表单消息">
        <button type="submit">提交（阻止默认+阻止冒泡）</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const message = ref('')
const formMessage = ref('')
const clickLog = ref([])
const captureLog = ref([])
const onceCount = ref(0)
const wheelCount = ref(0)

// .prevent 示例
function handleLinkClick() {
  console.log('链接被点击，但默认跳转被阻止')
  alert('链接点击事件已处理，不会跳转')
}

function handleSubmit() {
  console.log('表单提交:', message.value)
  alert(`表单提交成功: ${message.value}`)
  message.value = ''
}

// .stop 示例
function outerClick() {
  clickLog.value.push('外层')
  console.log('外层div点击')
}

function middleClick() {
  clickLog.value.push('中层')
  console.log('中层div点击')
}

function innerClick() {
  clickLog.value.push('内层')
  console.log('内层按钮点击')
}

// .capture 示例
function outerCapture() {
  captureLog.value.push('外层(捕获)')
  console.log('外层捕获事件')
}

function innerCapture() {
  captureLog.value.push('内层')
  console.log('内层事件')
}

// .self 示例
function selfClick() {
  console.log('self点击事件触发')
  alert('只在外层div区域（非子元素）点击时触发')
}

// .once 示例
function onceClick() {
  onceCount.value++
  console.log('once按钮被点击')
  alert('这个按钮只能点击一次!')
}

function onceClick2() {
  onceCount.value++
  console.log('once按钮2被点击')
  alert('另一个只能点击一次的按钮!')
}

// .passive 示例
function handleWheel(event) {
  wheelCount.value++
  console.log('滚轮滚动:', event.deltaY)
}

// 组合修饰符示例
function handleSubmitWithStop() {
  console.log('表单提交（阻止默认+阻止冒泡）:', formMessage.value)
  alert(`组合修饰符提交: ${formMessage.value}`)
  formMessage.value = ''
}

// 重置日志
function resetLogs() {
  clickLog.value = []
  captureLog.value = []
}
</script>

<style scoped>
.event-modifiers {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}

.modifier-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.outer-div {
  padding: 20px;
  background-color: #f8d7da;
  border: 2px solid #dc3545;
  border-radius: 4px;
  margin: 10px 0;
}

.middle-div {
  padding: 15px;
  background-color: #fff3cd;
  border: 2px solid #ffc107;
  border-radius: 4px;
  margin: 10px 0;
}

.capture-div {
  padding: 20px;
  background-color: #d1ecf1;
  border: 2px solid #17a2b8;
  border-radius: 4px;
  margin: 10px 0;
}

.capture-inner {
  padding: 15px;
  background-color: #d4edda;
  border: 2px solid #28a745;
  border-radius: 4px;
  margin: 10px 0;
}

.self-div {
  padding: 20px;
  background-color: #e2e3e5;
  border: 2px solid #6c757d;
  border-radius: 4px;
  margin: 10px 0;
  cursor: pointer;
}

.self-inner {
  padding: 15px;
  background-color: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 4px;
  margin: 10px 0;
}

.wheel-div {
  height: 150px;
  padding: 20px;
  background-color: #fff3cd;
  border: 2px solid #ffc107;
  border-radius: 4px;
  overflow: auto;
}

input, button {
  margin: 5px;
  padding: 8px 16px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  background-color: #007bff;
  color: white;
  border-color: #007bff;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}
</style>
```

---

## ⌨️ 键盘和鼠标事件修饰符

### 键盘修饰符

```vue
<template>
  <div class="keyboard-events">
    <h3>键盘事件修饰符</h3>

    <!-- 基本键盘事件 -->
    <div class="keyboard-section">
      <h4>基本键盘事件</h4>
      <input
        @keyup="handleKeyUp"
        @keydown="handleKeyDown"
        @keypress="handleKeyPress"
        placeholder="输入任意键，查看键盘事件"
      >
      <div class="event-info">
        <p>最后按键: {{ lastKey }}</p>
        <p>按键码: {{ keyCode }}</p>
        <p>事件类型: {{ eventType }}</p>
      </div>
    </div>

    <!-- 特定按键修饰符 -->
    <div class="key-modifiers">
      <h4>特定按键修饰符</h4>
      <div class="key-inputs">
        <input
          @keyup.enter="onEnter"
          placeholder="按回车键触发"
        >
        <span class="key-hint">Enter</span>

        <input
          @keyup.tab="onTab"
          placeholder="按Tab键触发"
        >
        <span class="key-hint">Tab</span>

        <input
          @keyup.delete="onDelete"
          placeholder="按Delete键触发"
        >
        <span class="key-hint">Delete</span>

        <input
          @keyup.esc="onEsc"
          placeholder="按Esc键触发"
        >
        <span class="key-hint">Esc</span>

        <input
          @keyup.space="onSpace"
          placeholder="按空格键触发"
        >
        <span class="key-hint">Space</span>
      </div>
    </div>

    <!-- 系统修饰键 -->
    <div class="system-modifiers">
      <h4>系统修饰键</h4>
      <div class="modifier-demo">
        <input
          @keyup.ctrl.enter="onCtrlEnter"
          placeholder="Ctrl + Enter"
        >
        <span class="key-hint">Ctrl+Enter</span>

        <input
          @keyup.alt.s="onAltS"
          placeholder="Alt + S"
        >
        <span class="key-hint">Alt+S</span>

        <input
          @keyup.shift.a="onShiftA"
          placeholder="Shift + A"
        >
        <span class="key-hint">Shift+A</span>

        <input
          @keyup.meta.f="onMetaF"
          placeholder="Meta/F + F"
        >
        <span class="key-hint">Meta+F</span>
      </div>
    </div>

    <!-- 实际应用：快捷键系统 -->
    <div class="shortcut-system">
      <h4>快捷键系统</h4>
      <div class="editor-demo">
        <textarea
          v-model="editorContent"
          @keydown.ctrl.s.prevent="saveFile"
          @keydown.ctrl.z.prevent="undo"
          @keydown.ctrl.y.prevent="redo"
          @keydown.ctrl.f.prevent="openSearch"
          placeholder="编辑器（支持快捷键）"
          rows="8"
        ></textarea>

        <div class="shortcut-help">
          <h5>支持的快捷键：</h5>
          <ul>
            <li>Ctrl+S - 保存文件</li>
            <li>Ctrl+Z - 撤销</li>
            <li>Ctrl+Y - 重做</li>
            <li>Ctrl+F - 搜索</li>
          </ul>
        </div>
      </div>
    </div>

    <!-- 操作日志 -->
    <div class="action-log">
      <h4>操作日志</h4>
      <div class="log-content">
        <div v-for="(log, index) in actionLogs" :key="index" class="log-item">
          <span class="log-time">{{ log.time }}</span>
          <span class="log-action">{{ log.action }}</span>
        </div>
      </div>
      <button @click="clearLogs">清空日志</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const lastKey = ref('')
const keyCode = ref('')
const eventType = ref('')
const editorContent = ref('')
const actionLogs = ref([])

// 基本键盘事件
function handleKeyUp(event) {
  lastKey.value = event.key
  keyCode.value = event.keyCode
  eventType.value = 'keyup'
  console.log('键盘释放:', event.key)
}

function handleKeyDown(event) {
  lastKey.value = event.key
  keyCode.value = event.keyCode
  eventType.value = 'keydown'
  console.log('键盘按下:', event.key)
}

function handleKeyPress(event) {
  lastKey.value = event.key
  keyCode.value = event.keyCode
  eventType.value = 'keypress'
  console.log('按键字符:', event.key)
}

// 特定按键事件
function onEnter() {
  addLog('按下了回车键')
  console.log('回车键被按下')
}

function onTab() {
  addLog('按下了Tab键')
  console.log('Tab键被按下')
}

function onDelete() {
  addLog('按下了删除键')
  console.log('删除键被按下')
}

function onEsc() {
  addLog('按下了Esc键')
  console.log('Esc键被按下')
}

function onSpace() {
  addLog('按下了空格键')
  console.log('空格键被按下')
}

// 系统修饰键组合
function onCtrlEnter() {
  addLog('Ctrl+Enter 组合键')
  console.log('Ctrl+Enter 被按下')
}

function onAltS() {
  addLog('Alt+S 组合键')
  console.log('Alt+S 被按下')
}

function onShiftA() {
  addLog('Shift+A 组合键')
  console.log('Shift+A 被按下')
}

function onMetaF() {
  addLog('Meta+F 组合键')
  console.log('Meta+F 被按下')
}

// 编辑器快捷键
function saveFile() {
  addLog('保存文件 (Ctrl+S)')
  console.log('文件已保存:', editorContent.value)
}

function undo() {
  addLog('撤销操作 (Ctrl+Z)')
  console.log('执行撤销操作')
}

function redo() {
  addLog('重做操作 (Ctrl+Y)')
  console.log('执行重做操作')
}

function openSearch() {
  addLog('打开搜索 (Ctrl+F)')
  console.log('打开搜索功能')
}

// 日志管理
function addLog(action) {
  actionLogs.value.unshift({
    time: new Date().toLocaleTimeString(),
    action: action
  })

  // 保持日志数量
  if (actionLogs.value.length > 10) {
    actionLogs.value = actionLogs.value.slice(0, 10)
  }
}

function clearLogs() {
  actionLogs.value = []
}
</script>

<style scoped>
.keyboard-events {
  max-width: 900px;
  margin: 20px auto;
  padding: 20px;
}

.keyboard-section, .key-modifiers, .system-modifiers, .shortcut-system, .action-log {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.event-info {
  margin-top: 10px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
  font-family: monospace;
}

.key-inputs, .modifier-demo {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-top: 15px;
}

.key-inputs input, .modifier-demo input {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.key-hint {
  display: inline-block;
  padding: 4px 8px;
  background-color: #007bff;
  color: white;
  border-radius: 4px;
  font-size: 12px;
  margin-left: 5px;
}

.editor-demo {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-top: 15px;
}

textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-family: monospace;
  resize: vertical;
}

.shortcut-help {
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 4px;
}

.shortcut-help h5 {
  margin-top: 0;
}

.shortcut-help ul {
  margin-bottom: 0;
}

.log-content {
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid #dee2e6;
  border-radius: 4px;
  padding: 10px;
  background-color: #f8f9fa;
}

.log-item {
  display: flex;
  justify-content: space-between;
  padding: 5px 0;
  border-bottom: 1px solid #dee2e6;
}

.log-item:last-child {
  border-bottom: none;
}

.log-time {
  color: #6c757d;
  font-family: monospace;
  font-size: 12px;
}

.log-action {
  color: #333;
}

button {
  margin-top: 10px;
  padding: 8px 16px;
  border: 1px solid #dc3545;
  background-color: #dc3545;
  color: white;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #c82333;
}
</style>
```

### 鼠标事件修饰符

```vue
<template>
  <div class="mouse-events">
    <h3>鼠标事件修饰符</h3>

    <!-- 基本鼠标事件 -->
    <div class="basic-mouse">
      <h4>基本鼠标事件</h4>
      <div
        @click="handleClick"
        @dblclick="handleDoubleClick"
        @mousedown="handleMouseDown"
        @mouseup="handleMouseUp"
        @mouseenter="handleMouseEnter"
        @mouseleave="handleMouseLeave"
        @mousemove="handleMouseMove"
        class="mouse-area"
      >
        <p>在这个区域测试各种鼠标事件</p>
        <p>鼠标位置: {{ mousePosition }}</p>
        <p>事件计数: 点击 {{ clickCount }} | 双击 {{ dblClickCount }}</p>
      </div>
    </div>

    <!-- 鼠标按钮修饰符 -->
    <div class="mouse-buttons">
      <h4>鼠标按钮修饰符</h4>
      <div class="button-area">
        <div
          @click.left="leftClick"
          @click.middle="middleClick"
          @click.right.prevent="rightClick"
          class="click-zone"
        >
          <p>左键：普通操作</p>
          <p>中键：特殊操作</p>
          <p>右键：上下文菜单</p>
        </div>
        <div class="button-log">
          <p>最后操作: {{ lastButtonAction }}</p>
        </div>
      </div>
    </div>

    <!-- 拖拽功能 -->
    <div class="drag-drop">
      <h4>拖拽功能</h4>
      <div class="drag-container">
        <div
          @mousedown="startDrag"
          @mousemove="dragging"
          @mouseup="endDrag"
          @mouseleave="endDrag"
          :style="{
            left: dragPosition.x + 'px',
            top: dragPosition.y + 'px',
            cursor: isDragging ? 'grabbing' : 'grab'
          }"
          class="draggable-item"
        >
          拖拽我
        </div>
      </div>
      <p>拖拽状态: {{ isDragging ? '正在拖拽' : '未拖拽' }}</p>
    </div>

    <!-- 画板功能 -->
    <div class="drawing-board">
      <h4>画板功能</h4>
      <div class="board-controls">
        <button @click="clearBoard">清空画板</button>
        <label>颜色:</label>
        <input type="color" v-model="brushColor">
        <label>粗细:</label>
        <input type="range" v-model.number="brushSize" min="1" max="20">
        <span>{{ brushSize }}px</span>
      </div>

      <svg
        @mousedown="startDrawing"
        @mousemove="draw"
        @mouseup="stopDrawing"
        @mouseleave="stopDrawing"
        class="canvas"
        width="600"
        height="300"
      >
        <path
          v-for="(path, index) in paths"
          :key="index"
          :d="path.d"
          :stroke="path.color"
          :stroke-width="path.size"
          fill="none"
          stroke-linecap="round"
          stroke-linejoin="round"
        />
      </svg>
    </div>

    <!-- 鼠标悬停效果 -->
    <div class="hover-effects">
      <h4>鼠标悬停效果</h4>
      <div class="hover-grid">
        <div
          v-for="(item, index) in hoverItems"
          :key="index"
          @mouseenter="handleHover(index, true)"
          @mouseleave="handleHover(index, false)"
          :class="{ 'hovered': item.isHovered }"
          class="hover-item"
        >
          {{ item.text }}
          <div v-if="item.isHovered" class="tooltip">
            {{ item.tooltip }}
          </div>
        </div>
      </div>
    </div>

    <!-- 滚轮事件 -->
    <div class="wheel-events">
      <h4>滚轮事件</h4>
      <div
        @wheel="handleWheel"
        class="wheel-area"
      >
        <p>在这个区域滚动鼠标滚轮</p>
        <p>滚动方向: {{ wheelDirection }}</p>
        <p>滚动强度: {{ wheelDelta }}</p>
        <p>缩放比例: {{ scale.toFixed(2) }}x</p>
        <div
          :style="{ transform: `scale(${scale})` }"
          class="scalable-content"
        >
          可缩放内容
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const mousePosition = ref({ x: 0, y: 0 })
const clickCount = ref(0)
const dblClickCount = ref(0)
const lastButtonAction = ref('')

// 拖拽相关
const isDragging = ref(false)
const dragPosition = reactive({ x: 50, y: 50 })
const dragStart = reactive({ x: 0, y: 0 })

// 画板相关
const isDrawing = ref(false)
const currentPath = ref('')
const brushColor = ref('#000000')
const brushSize = ref(3)
const paths = ref([])

// 悬停效果
const hoverItems = ref([
  { text: '项目1', tooltip: '这是第一个项目的提示', isHovered: false },
  { text: '项目2', tooltip: '这是第二个项目的提示', isHovered: false },
  { text: '项目3', tooltip: '这是第三个项目的提示', isHovered: false },
  { text: '项目4', tooltip: '这是第四个项目的提示', isHovered: false }
])

// 滚轮相关
const wheelDirection = ref('')
const wheelDelta = ref(0)
const scale = ref(1)

// 基本鼠标事件
function handleClick(event) {
  clickCount.value++
  console.log('点击事件:', event)
}

function handleDoubleClick(event) {
  dblClickCount.value++
  console.log('双击事件:', event)
}

function handleMouseDown(event) {
  console.log('鼠标按下:', event.button)
}

function handleMouseUp(event) {
  console.log('鼠标释放:', event.button)
}

function handleMouseEnter() {
  console.log('鼠标进入')
}

function handleMouseLeave() {
  console.log('鼠标离开')
}

function handleMouseMove(event) {
  mousePosition.value = { x: event.offsetX, y: event.offsetY }
}

// 鼠标按钮事件
function leftClick() {
  lastButtonAction.value = '左键点击'
  console.log('左键点击')
}

function middleClick() {
  lastButtonAction.value = '中键点击'
  console.log('中键点击')
}

function rightClick() {
  lastButtonAction.value = '右键点击'
  console.log('右键点击')
}

// 拖拽功能
function startDrag(event) {
  isDragging.value = true
  dragStart.x = event.clientX - dragPosition.x
  dragStart.y = event.clientY - dragPosition.y
}

function dragging(event) {
  if (!isDragging.value) return

  dragPosition.x = event.clientX - dragStart.x
  dragPosition.y = event.clientY - dragStart.y
}

function endDrag() {
  isDragging.value = false
}

// 画板功能
function startDrawing(event) {
  isDrawing.value = true
  const rect = event.currentTarget.getBoundingClientRect()
  const x = event.clientX - rect.left
  const y = event.clientY - rect.top
  currentPath.value = `M ${x} ${y}`
}

function draw(event) {
  if (!isDrawing.value) return

  const rect = event.currentTarget.getBoundingClientRect()
  const x = event.clientX - rect.left
  const y = event.clientY - rect.top
  currentPath.value += ` L ${x} ${y}`
}

function stopDrawing() {
  if (isDrawing.value && currentPath.value) {
    paths.value.push({
      d: currentPath.value,
      color: brushColor.value,
      size: brushSize.value
    })
  }
  isDrawing.value = false
  currentPath.value = ''
}

function clearBoard() {
  paths.value = []
}

// 悬停效果
function handleHover(index, isEntering) {
  hoverItems.value[index].isHovered = isEntering
}

// 滚轮事件
function handleWheel(event) {
  event.preventDefault()

  if (event.deltaY > 0) {
    wheelDirection.value = '向下'
    scale.value = Math.max(0.5, scale.value - 0.1)
  } else {
    wheelDirection.value = '向上'
    scale.value = Math.min(3, scale.value + 0.1)
  }

  wheelDelta.value = Math.abs(event.deltaY)
}
</script>

<style scoped>
.mouse-events {
  max-width: 1000px;
  margin: 20px auto;
  padding: 20px;
}

.basic-mouse, .mouse-buttons, .drag-drop, .drawing-board, .hover-effects, .wheel-events {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.mouse-area {
  height: 150px;
  background-color: #f8f9fa;
  border: 2px dashed #dee2e6;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  user-select: none;
}

.button-area {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.click-zone {
  padding: 30px;
  background-color: #e9ecef;
  border: 2px solid #adb5bd;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  user-select: none;
}

.button-log {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 4px;
  font-weight: bold;
}

.drag-container {
  height: 200px;
  background-color: #f8f9fa;
  border: 2px dashed #dee2e6;
  border-radius: 8px;
  position: relative;
  overflow: hidden;
}

.draggable-item {
  position: absolute;
  width: 80px;
  height: 40px;
  background-color: #007bff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  user-select: none;
}

.board-controls {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.canvas {
  border: 2px solid #dee2e6;
  border-radius: 4px;
  background-color: white;
  cursor: crosshair;
}

.hover-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
}

.hover-item {
  position: relative;
  padding: 20px;
  background-color: #f8f9fa;
  border: 2px solid #dee2e6;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.hover-item.hovered {
  background-color: #007bff;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.tooltip {
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  background-color: #333;
  color: white;
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
  margin-bottom: 5px;
}

.tooltip::after {
  content: '';
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  border: 5px solid transparent;
  border-top-color: #333;
}

.wheel-area {
  height: 200px;
  background-color: #f8f9fa;
  border: 2px dashed #dee2e6;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.scalable-content {
  margin-top: 20px;
  padding: 20px;
  background-color: #007bff;
  color: white;
  border-radius: 8px;
  transition: transform 0.1s ease;
}

button {
  padding: 8px 16px;
  border: 1px solid #007bff;
  background-color: #007bff;
  color: white;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}

input[type="color"],
input[type="range"] {
  vertical-align: middle;
}
</style>
```

---

## 📝 高级表单处理

### 复杂表单验证

```vue
<template>
  <div class="advanced-forms">
    <h3>高级表单处理</h3>

    <!-- 用户注册表单 -->
    <div class="registration-form">
      <h4>用户注册表单</h4>
      <form @submit.prevent="submitRegistration">
        <!-- 用户名 -->
        <div class="form-group">
          <label for="username">用户名 *</label>
          <input
            id="username"
            v-model="form.username"
            @blur="validateUsername"
            @input="clearUsernameError"
            :class="{ 'error': errors.username }"
            placeholder="3-20个字符，字母数字下划线"
          >
          <div v-if="errors.username" class="error-message">
            {{ errors.username }}
          </div>
        </div>

        <!-- 邮箱 -->
        <div class="form-group">
          <label for="email">邮箱 *</label>
          <input
            id="email"
            type="email"
            v-model="form.email"
            @blur="validateEmail"
            @input="clearEmailError"
            :class="{ 'error': errors.email }"
            placeholder="example@domain.com"
          >
          <div v-if="errors.email" class="error-message">
            {{ errors.email }}
          </div>
        </div>

        <!-- 密码 -->
        <div class="form-group">
          <label for="password">密码 *</label>
          <div class="password-input">
            <input
              id="password"
              :type="showPassword ? 'text' : 'password'"
              v-model="form.password"
              @blur="validatePassword"
              @input="validatePasswordStrength"
              :class="{ 'error': errors.password }"
              placeholder="至少8个字符"
            >
            <button
              type="button"
              @click="showPassword = !showPassword"
              class="toggle-password"
            >
              {{ showPassword ? '隐藏' : '显示' }}
            </button>
          </div>
          <div v-if="errors.password" class="error-message">
            {{ errors.password }}
          </div>
          <div v-if="passwordStrength.text" class="password-strength">
            <span :class="passwordStrength.class">
              密码强度: {{ passwordStrength.text }}
            </span>
          </div>
        </div>

        <!-- 确认密码 -->
        <div class="form-group">
          <label for="confirmPassword">确认密码 *</label>
          <input
            id="confirmPassword"
            type="password"
            v-model="form.confirmPassword"
            @blur="validateConfirmPassword"
            @input="clearConfirmPasswordError"
            :class="{ 'error': errors.confirmPassword }"
            placeholder="再次输入密码"
          >
          <div v-if="errors.confirmPassword" class="error-message">
            {{ errors.confirmPassword }}
          </div>
        </div>

        <!-- 手机号码 -->
        <div class="form-group">
          <label for="phone">手机号码</label>
          <input
            id="phone"
            v-model="form.phone"
            @blur="validatePhone"
            @input="clearPhoneError"
            :class="{ 'error': errors.phone }"
            placeholder="11位手机号码"
          >
          <div v-if="errors.phone" class="error-message">
            {{ errors.phone }}
          </div>
        </div>

        <!-- 生日 -->
        <div class="form-group">
          <label for="birthday">生日</label>
          <input
            id="birthday"
            type="date"
            v-model="form.birthday"
            @blur="validateBirthday"
            :class="{ 'error': errors.birthday }"
            :max="maxDate"
          >
          <div v-if="errors.birthday" class="error-message">
            {{ errors.birthday }}
          </div>
        </div>

        <!-- 性别 -->
        <div class="form-group">
          <label>性别</label>
          <div class="radio-group">
            <label>
              <input
                type="radio"
                v-model="form.gender"
                value="male"
              >
              男
            </label>
            <label>
              <input
                type="radio"
                v-model="form.gender"
                value="female"
              >
              女
            </label>
            <label>
              <input
                type="radio"
                v-model="form.gender"
                value="other"
              >
              其他
            </label>
          </div>
        </div>

        <!-- 兴趣爱好 -->
        <div class="form-group">
          <label>兴趣爱好</label>
          <div class="checkbox-group">
            <label v-for="hobby in hobbyOptions" :key="hobby.value">
              <input
                type="checkbox"
                v-model="form.hobbies"
                :value="hobby.value"
              >
              {{ hobby.label }}
            </label>
          </div>
        </div>

        <!-- 城市 -->
        <div class="form-group">
          <label for="city">城市 *</label>
          <select
            id="city"
            v-model="form.city"
            @blur="validateCity"
            @change="clearCityError"
            :class="{ 'error': errors.city }"
          >
            <option value="">请选择城市</option>
            <option v-for="city in cityOptions" :key="city.value" :value="city.value">
              {{ city.label }}
            </option>
          </select>
          <div v-if="errors.city" class="error-message">
            {{ errors.city }}
          </div>
        </div>

        <!-- 个人简介 -->
        <div class="form-group">
          <label for="bio">个人简介</label>
          <textarea
            id="bio"
            v-model="form.bio"
            @input="updateBioCount"
            placeholder="介绍一下自己..."
            rows="4"
            maxlength="200"
          ></textarea>
          <div class="char-count">
            {{ form.bio.length }}/200
          </div>
        </div>

        <!-- 服务条款 -->
        <div class="form-group">
          <label class="checkbox-label">
            <input
              type="checkbox"
              v-model="form.agreeTerms"
            >
            我已阅读并同意<a href="#" @click.prevent>服务条款</a>和<a href="#" @click.prevent>隐私政策</a>
          </label>
        </div>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <button
            type="submit"
            :disabled="!isFormValid || isSubmitting"
            class="submit-btn"
          >
            {{ isSubmitting ? '提交中...' : '注册' }}
          </button>
          <button
            type="button"
            @click="resetForm"
            class="reset-btn"
          >
            重置
          </button>
        </div>
      </form>

      <!-- 表单数据显示 -->
      <div v-if="submittedData" class="submitted-data">
        <h4>提交的数据:</h4>
        <pre>{{ JSON.stringify(submittedData, null, 2) }}</pre>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// 表单数据
const form = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  phone: '',
  birthday: '',
  gender: '',
  hobbies: [],
  city: '',
  bio: '',
  agreeTerms: false
})

// 错误信息
const errors = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  phone: '',
  birthday: '',
  city: ''
})

// 状态
const showPassword = ref(false)
const isSubmitting = ref(false)
const submittedData = ref(null)

// 密码强度
const passwordStrength = ref({
  text: '',
  class: ''
})

// 选项数据
const hobbyOptions = [
  { value: 'reading', label: '阅读' },
  { value: 'sports', label: '运动' },
  { value: 'music', label: '音乐' },
  { value: 'travel', label: '旅行' },
  { value: 'coding', label: '编程' },
  { value: 'photography', label: '摄影' }
]

const cityOptions = [
  { value: 'beijing', label: '北京' },
  { value: 'shanghai', label: '上海' },
  { value: 'guangzhou', label: '广州' },
  { value: 'shenzhen', label: '深圳' },
  { value: 'hangzhou', label: '杭州' },
  { value: 'chengdu', label: '成都' }
]

// 计算属性
const maxDate = computed(() => {
  const today = new Date()
  return today.toISOString().split('T')[0]
})

const isFormValid = computed(() => {
  return form.value.username &&
         form.value.email &&
         form.value.password &&
         form.value.confirmPassword &&
         form.value.city &&
         form.value.agreeTerms &&
         !Object.values(errors.value).some(error => error)
})

// 验证方法
function validateUsername() {
  if (!form.value.username) {
    errors.value.username = '用户名不能为空'
  } else if (form.value.username.length < 3 || form.value.username.length > 20) {
    errors.value.username = '用户名长度应为3-20个字符'
  } else if (!/^[a-zA-Z0-9_]+$/.test(form.value.username)) {
    errors.value.username = '用户名只能包含字母、数字和下划线'
  } else {
    errors.value.username = ''
  }
}

function validateEmail() {
  if (!form.value.email) {
    errors.value.email = '邮箱不能为空'
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.value.email)) {
    errors.value.email = '请输入有效的邮箱地址'
  } else {
    errors.value.email = ''
  }
}

function validatePassword() {
  if (!form.value.password) {
    errors.value.password = '密码不能为空'
  } else if (form.value.password.length < 8) {
    errors.value.password = '密码长度至少8个字符'
  } else {
    errors.value.password = ''
  }
}

function validatePasswordStrength() {
  const password = form.value.password
  if (!password) {
    passwordStrength.value = { text: '', class: '' }
    return
  }

  let strength = 0
  if (password.length >= 8) strength++
  if (/[a-z]/.test(password)) strength++
  if (/[A-Z]/.test(password)) strength++
  if (/[0-9]/.test(password)) strength++
  if (/[^a-zA-Z0-9]/.test(password)) strength++

  const strengthMap = {
    1: { text: '弱', class: 'weak' },
    2: { text: '一般', class: 'fair' },
    3: { text: '中等', class: 'medium' },
    4: { text: '强', class: 'strong' },
    5: { text: '非常强', class: 'very-strong' }
  }

  passwordStrength.value = strengthMap[strength] || { text: '', class: '' }
}

function validateConfirmPassword() {
  if (!form.value.confirmPassword) {
    errors.value.confirmPassword = '请确认密码'
  } else if (form.value.password !== form.value.confirmPassword) {
    errors.value.confirmPassword = '两次输入的密码不一致'
  } else {
    errors.value.confirmPassword = ''
  }
}

function validatePhone() {
  if (form.value.phone && !/^1[3-9]\d{9}$/.test(form.value.phone)) {
    errors.value.phone = '请输入有效的11位手机号码'
  } else {
    errors.value.phone = ''
  }
}

function validateBirthday() {
  if (form.value.birthday) {
    const birthday = new Date(form.value.birthday)
    const today = new Date()
    const age = today.getFullYear() - birthday.getFullYear()

    if (age < 18 || age > 100) {
      errors.value.birthday = '年龄应在18-100岁之间'
    } else {
      errors.value.birthday = ''
    }
  }
}

function validateCity() {
  if (!form.value.city) {
    errors.value.city = '请选择城市'
  } else {
    errors.value.city = ''
  }
}

// 清除错误方法
function clearUsernameError() {
  if (errors.value.username) {
    validateUsername()
  }
}

function clearEmailError() {
  if (errors.value.email) {
    validateEmail()
  }
}

function clearConfirmPasswordError() {
  if (errors.value.confirmPassword) {
    validateConfirmPassword()
  }
}

function clearPhoneError() {
  errors.value.phone = ''
}

function clearCityError() {
  errors.value.city = ''
}

// 其他方法
function updateBioCount() {
  // 字符计数自动更新
}

function submitRegistration() {
  // 验证所有字段
  validateUsername()
  validateEmail()
  validatePassword()
  validateConfirmPassword()
  validatePhone()
  validateBirthday()
  validateCity()

  if (!isFormValid.value) {
    alert('请检查表单错误')
    return
  }

  isSubmitting.value = true

  // 模拟API请求
  setTimeout(() => {
    submittedData.value = { ...form.value }
    isSubmitting.value = false
    alert('注册成功!')
  }, 2000)
}

function resetForm() {
  form.value = {
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    phone: '',
    birthday: '',
    gender: '',
    hobbies: [],
    city: '',
    bio: '',
    agreeTerms: false
  }
  errors.value = {
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    phone: '',
    birthday: '',
    city: ''
  }
  passwordStrength.value = { text: '', class: '' }
  submittedData.value = null
}
</script>

<style scoped>
.advanced-forms {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}

.registration-form {
  padding: 30px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #fafafa;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #333;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.form-group input.error,
.form-group select.error {
  border-color: #dc3545;
}

.password-input {
  position: relative;
}

.toggle-password {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #007bff;
  cursor: pointer;
  font-size: 12px;
}

.error-message {
  color: #dc3545;
  font-size: 12px;
  margin-top: 5px;
}

.password-strength {
  margin-top: 5px;
  font-size: 12px;
}

.password-strength .weak { color: #dc3545; }
.password-strength .fair { color: #ffc107; }
.password-strength .medium { color: #17a2b8; }
.password-strength .strong { color: #28a745; }
.password-strength .very-strong { color: #007bff; }

.radio-group,
.checkbox-group {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.radio-group label,
.checkbox-group label,
.checkbox-label {
  display: flex;
  align-items: center;
  gap: 5px;
  font-weight: normal;
}

.char-count {
  text-align: right;
  font-size: 12px;
  color: #6c757d;
  margin-top: 5px;
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-top: 30px;
}

.submit-btn,
.reset-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}

.submit-btn {
  background-color: #007bff;
  color: white;
}

.submit-btn:hover:not(:disabled) {
  background-color: #0056b3;
}

.submit-btn:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
}

.reset-btn {
  background-color: #6c757d;
  color: white;
}

.reset-btn:hover {
  background-color: #545b62;
}

.submitted-data {
  margin-top: 30px;
  padding: 20px;
  background-color: #d4edda;
  border: 1px solid #c3e6cb;
  border-radius: 4px;
}

.submitted-data pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}

a {
  color: #007bff;
  text-decoration: none;
}

a:hover {
  text-decoration: underline;
}
</style>
```

---

## 📋 事件处理与表单绑定速查表

### 事件修饰符

| 修饰符 | 作用 | 使用场景 |
|--------|------|----------|
| **.stop** | 阻止事件冒泡 | 避免父元素事件触发 |
| **.prevent** | 阻止默认行为 | 阻止链接跳转、表单提交等 |
| **.capture** | 使用捕获模式 | 在事件捕获阶段处理 |
| **.self** | 只在自身触发 | 忽略子元素事件 |
| **.once** | 只触发一次 | 一次性事件处理 |
| **.passive** | 被动模式 | 提升滚动性能 |

### 按键修饰符

| 修饰符 | 按键 | 示例 |
|--------|------|------|
| **.enter** | 回车键 | `@keyup.enter` |
| **.tab** | Tab键 | `@keyup.tab` |
| **.delete** | 删除键 | `@keyup.delete` |
| **.esc** | Esc键 | `@keyup.esc` |
| **.space** | 空格键 | `@keyup.space` |
| **.ctrl** | Ctrl键 | `@keyup.ctrl.enter` |
| **.alt** | Alt键 | `@keyup.alt.s` |
| **.shift** | Shift键 | `@keyup.shift.a` |
| **.meta** | Meta键 | `@keyup.meta.f` |

### 鼠标修饰符

| 修饰符 | 鼠标按钮 | 示例 |
|--------|----------|------|
| **.left** | 左键 | `@click.left` |
| **.right** | 右键 | `@click.right` |
| **.middle** | 中键 | `@click.middle` |

---

## 🚀 下一步学习

掌握事件处理和表单绑定后，继续学习：

- [[02-Vue核心概念/08-Vue指令与CompositionAPI体系概览.md|指令与CompositionAPI体系]]
- [[02-Vue核心概念/05-Vue模板语法与数据绑定.md|模板语法与数据绑定]]
- [[01-组件系统/01-组件基础概念详解.md|组件系统深入]]

---

**记住：事件处理和表单绑定是构建交互式应用的关键，掌握它们就能创建出用户体验优秀的Web应用！** 🎉