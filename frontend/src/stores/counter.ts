// ============================================================================
// 计数器状态管理 Store (TypeScript版本) - Pinia状态管理示例，演示基础用法
// ============================================================================

import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

// ============================================================================
// 计数器Store定义：使用Composition API模式 + TypeScript
// ============================================================================
export const useCounterStore = defineStore('counter', () => {
  // 状态定义：强类型响应式计数器数据
  const count = ref<number>(0)

  // 计算属性：基于计数器的派生数据
  const doubleCount = computed<number>(() => count.value * 2)

  // 方法定义：操作计数器的函数
  function increment(): void {
    count.value++
  }

  // 可选：添加减法功能
  function decrement(): void {
    count.value--
  }

  // 可选：重置功能
  function reset(): void {
    count.value = 0
  }

  // Store导出：暴露状态、计算属性和方法
  return {
    count,
    doubleCount,
    increment,
    decrement,
    reset
  }
})