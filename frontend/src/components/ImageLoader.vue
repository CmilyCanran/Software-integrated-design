<template>
  <div class="image-loader-container" :class="containerClass">
    <!-- 图片显示 -->
    <img
      v-if="!isLoading && !hasError"
      :src="currentSrc"
      :alt="alt"
      :class="imageClass"
      :style="imageStyle"
      :loading="loading"
      @load="handleLoad"
      @error="handleError"
      @click="$emit('click', $event)"
      @mouseenter="$emit('mouseenter', $event)"
      @mouseleave="$emit('mouseleave', $event)"
    />

    <!-- 加载状态 -->
    <div v-else-if="isLoading" class="image-loading">
      <div class="loading-spinner">
        <el-icon class="is-loading" spin>
          <Loading />
        </el-icon>
      </div>
      <img
        :src="placeholder"
        :alt="alt"
        class="placeholder-image"
        :style="{ opacity: 0.3 }"
      />
    </div>

    <!-- 错误状态 -->
    <div v-else-if="hasError" class="image-error">
      <img
        :src="fallback"
        :alt="alt || '图片加载失败'"
        class="error-image"
      />
      <div class="error-overlay">
        <el-icon><WarningFilled /></el-icon>
        <span>图片加载失败</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { Loading, WarningFilled } from '@element-plus/icons-vue'

// 定义组件属性接口
interface ImageLoaderProps {
  // 主图片URL
  src: string
  // 替代文本
  alt?: string
  // 加载时显示的占位图
  placeholder?: string
  // 加载失败时显示的图片
  fallback?: string
  // 是否懒加载
  lazy?: boolean
  // 图片容器类名
  class?: string
  // 图片类名
  imageClass?: string
  // 图片样式
  imageStyle?: string | Record<string, any>
  // 图片加载模式
  loading?: 'lazy' | 'eager'
  // 是否显示加载状态
  showLoading?: boolean
}

// 定义组件事件
interface ImageLoaderEmits {
  (e: 'load', event: Event): void
  (e: 'error', event: Event | string): void
  (e: 'click', event: MouseEvent): void
  (e: 'mouseenter', event: MouseEvent): void
  (e: 'mouseleave', event: MouseEvent): void
}

// 定义props和默认值
const props = withDefaults(defineProps<ImageLoaderProps>(), {
  placeholder: '/images/placeholder-product.png',
  fallback: '/images/noDisplay.png',
  lazy: false,
  loading: 'eager',
  showLoading: false
})

const emit = defineEmits<ImageLoaderEmits>()

// 状态管理
const isLoading = ref(false)
const hasError = ref(false)
const currentSrc = ref(props.src)

// 计算属性
const containerClass = computed(() => [
  'image-loader',
  props.class,
  {
    'image-loading': isLoading.value,
    'image-error': hasError.value
  }
])

const imageClass = computed(() => [
  'image-loader-image',
  props.imageClass,
  {
    'image-error': hasError.value
  }
])

// 监听src变化，重置状态
watch(() => props.src, (newSrc) => {
  if (newSrc !== currentSrc.value) {
    resetState()
    currentSrc.value = newSrc
    loadImage()
  }
})

// 重置状态
const resetState = (): void => {
  isLoading.value = props.showLoading
  hasError.value = false
}

// 加载图片
const loadImage = (): void => {
  console.log('ImageLoader: 开始加载图片', { src: props.src })

  if (!props.src) {
    console.log('ImageLoader: 图片URL为空，显示错误状态')
    hasError.value = true
    isLoading.value = false
    return
  }

  isLoading.value = true
  hasError.value = false

  const img = new Image()

  img.onload = (event) => {
    console.log('ImageLoader: 图片加载成功', { src: props.src })
    isLoading.value = false
    hasError.value = false
    emit('load', event)
  }

  img.onerror = (event: any) => {
    console.log('ImageLoader: 图片加载失败', { src: props.src, error: event })
    isLoading.value = false
    hasError.value = true
    emit('error', event)
  }

  img.src = props.src
}

// 处理图片加载
const handleLoad = (event: Event): void => {
  isLoading.value = false
  hasError.value = false
  emit('load', event)
}

// 处理图片错误
const handleError = (event: Event): void => {
  isLoading.value = false
  hasError.value = true
  emit('error', event)
}

// 组件挂载时开始加载图片
console.log('ImageLoader: 组件挂载', { src: props.src, currentSrc: currentSrc.value })
if (props.src) {
  loadImage()
}
</script>

<style scoped>
.image-loader-container {
  position: relative;
  display: inline-block;
  overflow: hidden;
  background-color: #f5f7fa;
}

.image-loader-image {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: opacity 0.3s ease;
}

.image-loading {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100px;
}

.loading-spinner {
  position: absolute;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #409eff;
  font-size: 24px;
}

.placeholder-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: opacity 0.3s ease;
}

.image-error {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.error-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0.7;
}

.error-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  font-size: 14px;
  z-index: 2;
}

.error-overlay .el-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .loading-spinner {
    font-size: 20px;
  }

  .error-overlay {
    font-size: 12px;
  }

  .error-overlay .el-icon {
    font-size: 24px;
    margin-bottom: 4px;
  }
}

/* 动画效果 */
.image-loader-image {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* 懒加载样式 */
.image-lazy {
  opacity: 0;
  transition: opacity 0.3s ease;
}

.image-lazy.loaded {
  opacity: 1;
}

/* 不同尺寸的变体 */
.image-loader-container.small {
  min-height: 60px;
}

.image-loader-container.medium {
  min-height: 120px;
}

.image-loader-container.large {
  min-height: 200px;
}

.image-loader-container.square {
  aspect-ratio: 1;
}

.image-loader-container.rectangle {
  aspect-ratio: 16/9;
}
</style>