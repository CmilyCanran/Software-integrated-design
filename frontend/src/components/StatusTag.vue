<template>
  <el-tag :type="tagType" :effect="effect">
    {{ statusText }}
  </el-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue'

// 定义props类型
interface Props {
  status: 'available' | 'unavailable' | 'out_of_stock'
  effect?: 'dark' | 'light' | 'plain'
}

// 使用withDefaults设置默认值
const props = withDefaults(defineProps<Props>(), {
  effect: 'light'
})

// 计算属性
const tagType = computed(() => {
  switch (props.status) {
    case 'available':
      return 'success'
    case 'unavailable':
      return 'warning'
    case 'out_of_stock':
      return 'danger'
    default:
      return 'info'
  }
})

const statusText = computed(() => {
  switch (props.status) {
    case 'available':
      return '在售'
    case 'unavailable':
      return '下架'
    case 'out_of_stock':
      return '缺货'
    default:
      return '未知'
  }
})
</script>

<style scoped>
.el-tag {
  font-weight: 500;
}
</style>