<template>
  <div
    class="search-box"
    ref="searchBoxRef"
    @click="handleBoxClick"
  >
    <!-- 搜索输入框 -->
    <el-input
      ref="searchInputRef"
      v-model="searchQuery"
      :placeholder="placeholder"
      :size="size"
      :clearable="clearable"
      :disabled="disabled"
      class="search-input"
      @input="handleInput"
      @clear="handleClear"
      @keyup.enter="handleSearch"
      @focus="handleInputFocus"
      @blur="handleInputBlur"
    >
      <!-- 前置图标 -->
      <template #prefix>
        <el-icon class="search-icon">
          <Search />
        </el-icon>
      </template>

      <!-- 后置按钮 -->
      <template #append>
        <el-button
          :size="size"
          :loading="loading"
          type="primary"
          @click="handleSearch"
        >
          搜索
        </el-button>
      </template>
    </el-input>

    <!-- 搜索建议（可选） -->
    <div
      v-if="shouldShowSuggestions"
      class="search-suggestions"
    >
      <div
        v-for="(suggestion, index) in filteredSuggestions"
        :key="index"
        class="suggestion-item"
        @click="handleSuggestionClick(suggestion)"
      >
        <el-icon class="suggestion-icon">
          <Search />
        </el-icon>
        <span class="suggestion-text">{{ suggestion }}</span>
      </div>
    </div>

    <!-- 搜索历史（可选） -->
    <div
      v-if="showHistory && searchHistory.length > 0 && !searchQuery"
      class="search-history"
    >
      <div class="history-header">
        <span>搜索历史</span>
        <el-button
          type="text"
          size="small"
          @click="clearHistory"
        >
          清空
        </el-button>
      </div>
      <div class="history-list">
        <el-tag
          v-for="(item, index) in searchHistory"
          :key="index"
          class="history-tag"
          closable
          @close="removeHistoryItem(index)"
          @click="handleHistoryClick(item)"
        >
          {{ item }}
        </el-tag>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed, onMounted, onUnmounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 定义props接口
interface Props {
  placeholder?: string            // 占位符文本
  size?: 'large' | 'default' | 'small'  // 尺寸
  clearable?: boolean            // 是否可清空
  disabled?: boolean             // 是否禁用
  loading?: boolean              // 是否加载中
  debounceTime?: number          // 防抖时间（毫秒）
  showSuggestions?: boolean      // 是否显示搜索建议
  showHistory?: boolean          // 是否显示搜索历史
  maxHistoryItems?: number       // 最大历史记录数量
  suggestions?: string[]         // 搜索建议列表
}

// 定义emits
interface Emits {
  (e: 'search', query: string): void
  (e: 'input', query: string): void
  (e: 'clear'): void
  (e: 'suggestion-select', suggestion: string): void
  (e: 'history-select', item: string): void
}

// 使用withDefaults设置默认值
const props = withDefaults(defineProps<Props>(), {
  placeholder: '请输入搜索关键词',
  size: 'default',
  clearable: true,
  disabled: false,
  loading: false,
  debounceTime: 300,
  showSuggestions: false,
  showHistory: true,
  maxHistoryItems: 10,
  suggestions: () => []
})

// 定义emits
const emit = defineEmits<Emits>()

// 响应式数据
const searchQuery = ref<string>('')
const searchHistory = ref<string[]>([])
const debounceTimer = ref<NodeJS.Timeout | null>(null)
const isInputFocused = ref<boolean>(false)
const searchBoxRef = ref<HTMLElement | null>(null)
const searchInputRef = ref<any>(null)

// 初始化搜索历史
const initHistory = (): void => {
  const saved = localStorage.getItem('search-history')
  if (saved) {
    try {
      searchHistory.value = JSON.parse(saved).slice(0, props.maxHistoryItems)
    } catch (error) {
      console.warn('Failed to parse search history:', error)
      searchHistory.value = []
    }
  }
}

// 保存搜索历史
const saveHistory = (): void => {
  try {
    localStorage.setItem('search-history', JSON.stringify(searchHistory.value))
  } catch (error) {
    console.warn('Failed to save search history:', error)
  }
}

// 添加到搜索历史
const addToHistory = (query: string): void => {
  if (!query.trim()) return

  // 移除重复项
  const index = searchHistory.value.indexOf(query)
  if (index > -1) {
    searchHistory.value.splice(index, 1)
  }

  // 添加到开头
  searchHistory.value.unshift(query)

  // 限制数量
  if (searchHistory.value.length > props.maxHistoryItems) {
    searchHistory.value = searchHistory.value.slice(0, props.maxHistoryItems)
  }

  saveHistory()
}

// 处理输入事件（带防抖）
const handleInput = (value: string): void => {
  emit('input', value)

  // 清除之前的定时器
  if (debounceTimer.value) {
    clearTimeout(debounceTimer.value)
  }

  // 设置新的防抖定时器
  if (props.debounceTime > 0) {
    debounceTimer.value = setTimeout(() => {
      emit('search', value)
    }, props.debounceTime)
  }
}

// 处理搜索
const handleSearch = (): void => {
  const query = searchQuery.value.trim()

  if (query) {
    addToHistory(query)
    emit('search', query)
  } else {
    ElMessage.warning('请输入搜索关键词')
  }
}

// 处理清空
const handleClear = (): void => {
  searchQuery.value = ''
  emit('clear')
  emit('search', '')
}

// 处理建议点击
const handleSuggestionClick = (suggestion: string): void => {
  searchQuery.value = suggestion
  addToHistory(suggestion)
  emit('suggestion-select', suggestion)
  emit('search', suggestion)
}

// 处理历史点击
const handleHistoryClick = (item: string): void => {
  searchQuery.value = item
  emit('history-select', item)
  emit('search', item)
}

// 清空历史
const clearHistory = (): void => {
  searchHistory.value = []
  saveHistory()
  ElMessage.success('搜索历史已清空')
}

// 移除历史项
const removeHistoryItem = (index: number): void => {
  searchHistory.value.splice(index, 1)
  saveHistory()
}

// 计算属性：是否显示建议
const shouldShowSuggestions = computed<boolean>(() => {
  return props.showSuggestions &&
         isInputFocused.value &&
         props.suggestions.length > 0
})

// 处理输入框聚焦
const handleInputFocus = (): void => {
  isInputFocused.value = true
}

// 处理输入框失焦
const handleInputBlur = (): void => {
  // 延迟失焦，以便点击建议项
  setTimeout(() => {
    isInputFocused.value = false
  }, 200)
}

// 处理搜索框点击
const handleBoxClick = (): void => {
  if (searchInputRef.value) {
    searchInputRef.value.focus()
  }
}

// 点击外部关闭建议
const handleClickOutside = (event: MouseEvent): void => {
  if (searchBoxRef.value && !searchBoxRef.value.contains(event.target as Node)) {
    isInputFocused.value = false
  }
}

// 初始化
initHistory()

// 监听suggestions变化，过滤匹配项
const filteredSuggestions = computed<string[]>(() => {
  // 如果没有输入内容，显示所有建议
  if (!searchQuery.value) return props.suggestions

  // 如果有输入内容，过滤匹配的建议
  return props.suggestions.filter(suggestion =>
    suggestion.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

// 添加全局点击事件监听
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

// 移除全局点击事件监听
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.search-box {
  position: relative;
  width: 100%;
  max-width: 600px;
}

.search-input {
  width: 100%;
}

.search-icon {
  color: #909399;
}

.search-suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #dcdfe6;
  border-top: none;
  border-radius: 0 0 4px 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 1000;
  max-height: 200px;
  overflow-y: auto;
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.suggestion-item:hover {
  background-color: #f5f7fa;
}

.suggestion-icon {
  color: #909399;
  font-size: 14px;
}

.suggestion-text {
  color: #333;
  font-size: 14px;
}

.search-history {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #dcdfe6;
  border-top: none;
  border-radius: 0 0 4px 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 999;
  padding: 12px;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 12px;
  color: #666;
}

.history-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.history-tag {
  cursor: pointer;
  font-size: 12px;
}

.history-tag:hover {
  background-color: #ecf5ff;
}
</style>