<template>
  <div class="order-status-filter">
    <el-radio-group
      :model-value="modelValue"
      @change="handleChange"
      size="default"
    >
      <el-radio-button
        v-for="option in filterOptions"
        :key="option.value"
        :label="option.value"
        :class="getButtonClass(option.value)"
      >
        {{ option.label }}
        <span v-if="option.count !== undefined" class="option-count">
          ({{ option.count }})
        </span>
      </el-radio-button>
    </el-radio-group>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { OrderStatus, OrderFilterOption } from '@/types/order'
import {
  ORDER_STATUS_DESCRIPTIONS,
  ORDER_STATUS_COLORS
} from '@/types/order'

// Props定义
interface Props {
  modelValue: string
  statuses?: OrderStatus[]
  counts?: Record<string, number>
}

// Props默认值
const props = withDefaults(defineProps<Props>(), {
  statuses: () => [
    'PENDING',
    'PAID',
    'SHIPPED',
    'COMPLETED',
    'CANCELLED'
  ] as OrderStatus[]
})

// Emits定义
const emit = defineEmits<{
  'update:modelValue': [value: string]
  change: [status: string]
}>()

// 计算属性
const filterOptions = computed<OrderFilterOption[]>(() => {
  const options: OrderFilterOption[] = [
    {
      value: '',
      label: '全部'
    }
  ]

  // 添加指定的状态选项
  props.statuses.forEach(status => {
    options.push({
      value: status,
      label: ORDER_STATUS_DESCRIPTIONS[status]
    })
  })

  // 如果有计数数据，添加到选项中
  if (props.counts) {
    options.forEach(option => {
      if (option.value in props.counts!) {
        option.count = props.counts![option.value]
      }
    })
  }

  return options
})

// 方法
const handleChange = (value: string) => {
  emit('update:modelValue', value)
  emit('change', value)
}

const getButtonClass = (status: string) => {
  if (!status) return 'status-all'
  return `status-${status.toLowerCase()}`
}
</script>

<style scoped>
.order-status-filter {
  padding: 12px 0;
}

/* 状态颜色变体 */
.status-all :deep(.el-radio-button__inner) {
  border-color: #dcdfe6 !important;
  background-color: #f5f7fa !important;
  color: #606266 !important;
}

.status-all :deep(.el-radio-button__inner):hover {
  color: #409EFF !important;
}

.status-all.is-active :deep(.el-radio-button__inner) {
  background-color: #409EFF !important;
  border-color: #409EFF !important;
  color: #fff !important;
}

.status-pending :deep(.el-radio-button__inner) {
  border-color: #faecd8 !important;
  background-color: #fef9ec !important;
  color: #e6a23c !important;
}

.status-pending :deep(.el-radio-button__inner):hover {
  color: #e6a23c !important;
}

.status-pending.is-active :deep(.el-radio-button__inner) {
  background-color: #e6a23c !important;
  border-color: #e6a23c !important;
  color: #fff !important;
}

.status-paid :deep(.el-radio-button__inner) {
  border-color: #d9ecff !important;
  background-color: #ecf5ff !important;
  color: #409eff !important;
}

.status-paid :deep(.el-radio-button__inner):hover {
  color: #409eff !important;
}

.status-paid.is-active :deep(.el-radio-button__inner) {
  background-color: #409eff !important;
  border-color: #409eff !important;
  color: #fff !important;
}

.status-shipped :deep(.el-radio-button__inner) {
  border-color: #e1f3d8 !important;
  background-color: #f0f9ff !important;
  color: #67c23a !important;
}

.status-shipped :deep(.el-radio-button__inner):hover {
  color: #67c23a !important;
}

.status-shipped.is-active :deep(.el-radio-button__inner) {
  background-color: #67c23a !important;
  border-color: #67c23a !important;
  color: #fff !important;
}

.status-completed :deep(.el-radio-button__inner) {
  border-color: #e1f3d8 !important;
  background-color: #f0f9ff !important;
  color: #67c23a !important;
}

.status-completed :deep(.el-radio-button__inner):hover {
  color: #67c23a !important;
}

.status-completed.is-active :deep(.el-radio-button__inner) {
  background-color: #67c23a !important;
  border-color: #67c23a !important;
  color: #fff !important;
}

.status-cancelled :deep(.el-radio-button__inner) {
  border-color: #fde2e2 !important;
  background-color: #fef0f0 !important;
  color: #f56c6c !important;
}

.status-cancelled :deep(.el-radio-button__inner):hover {
  color: #f56c6c !important;
}

.status-cancelled.is-active :deep(.el-radio-button__inner) {
  background-color: #f56c6c !important;
  border-color: #f56c6c !important;
  color: #fff !important;
}

.option-count {
  margin-left: 4px;
  font-size: 12px;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .order-status-filter :deep(.el-radio-group) {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  .order-status-filter :deep(.el-radio-button) {
    flex: 1;
    min-width: 80px;
  }

  .order-status-filter :deep(.el-radio-button__inner) {
    width: 100%;
    text-align: center;
    padding: 8px 12px;
    font-size: 13px;
  }
}
</style>