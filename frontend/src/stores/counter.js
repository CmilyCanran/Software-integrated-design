// ============================================================================
// 计数器状态管理 Store - Pinia状态管理示例，演示基础用法
// ============================================================================

// 🔥 依赖导入：Vue响应式API和Pinia状态管理
import { ref, computed } from 'vue'                        // 导入Vue响应式API：ref和computed
import { defineStore } from 'pinia'                        // 导入Pinia状态定义函数

// ============================================================================
// 🔥 计数器Store定义：使用Composition API模式
// ============================================================================
export const useCounterStore = defineStore('counter', () => {
  // ┌─ 状态定义：响应式计数器数据
  const count = ref(0)                                    // 🔢 计数器状态，初始值为0，响应式数据

  // ┌─ 计算属性：基于计数器的派生数据
  const doubleCount = computed(() => count.value * 2)     // ➕ 双倍计数器，自动计算count的2倍值

  // ┌─ 方法定义：操作计数器的函数
  function increment() {                                 // 📈 增加计数器的方法
    count.value++                                        // 计数器值加1
  }

  // ┌─ Store导出：暴露状态、计算属性和方法
  return {
    count,                                              // 🔢 计数器状态
    doubleCount,                                        // ➕ 双倍计数器计算属性
    increment                                           // 📈 增加计数器方法
  }
})
