<template>
  <div class="pagination-bar">
    <!-- 分页信息 -->
    <div class="pagination-info">
      <span class="total-info">
        共 {{ total }} 条记录
      </span>
      <span class="page-info">
        第 {{ currentPage }} / {{ totalPages }} 页
      </span>
    </div>

    <!-- 分页控件 -->
    <el-pagination
      :current-page="currentPage"
      :page-size="pageSize"
      :page-sizes="pageSizes"
      :total="total"
      :layout="layout"
      :background="background"
      :small="small"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      @prev-click="handlePrevClick"
      @next-click="handleNextClick"
    />

    <!-- 跳转输入框 -->
    <div
      v-if="showJumper"
      class="pagination-jumper"
    >
      <span class="jumper-text">前往</span>
      <el-input-number
        v-model="jumpPage"
        :min="1"
        :max="totalPages"
        :size="small ? 'small' : 'default'"
        class="jumper-input"
        @change="handleJump"
      />
      <span class="jumper-text">页</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

// 定义props接口
interface Props {
  total: number                      // 总记录数
  currentPage: number               // 当前页码
  pageSize: number                  // 每页条数
  pageSizes?: number[]              // 可选的每页条数
  layout?: string                   // 分页布局
  background?: boolean              // 是否显示背景色
  small?: boolean                   // 是否使用小尺寸
  showJumper?: boolean              // 是否显示跳转输入框
  showTotal?: boolean               // 是否显示总数信息
  showPageInfo?: boolean            // 是否显示页码信息
}

// 定义emits
interface Emits {
  (e: 'update:currentPage', page: number): void
  (e: 'update:pageSize', size: number): void
  (e: 'change', page: number, size: number): void
  (e: 'size-change', size: number): void
  (e: 'current-change', page: number): void
  (e: 'prev-click', page: number): void
  (e: 'next-click', page: number): void
}

// 使用withDefaults设置默认值
const props = withDefaults(defineProps<Props>(), {
  pageSizes: () => [10, 20, 50, 100],
  layout: 'sizes, prev, pager, next, jumper',
  background: true,
  small: false,
  showJumper: true,
  showTotal: true,
  showPageInfo: true
})

// 定义emits
const emit = defineEmits<Emits>()

// 响应式数据
const jumpPage = ref<number>(props.currentPage)

// 计算属性：总页数
const totalPages = computed<number>(() => {
  return Math.ceil(props.total / props.pageSize)
})


// 处理每页条数变化
const handleSizeChange = (size: number): void => {
  emit('update:pageSize', size)
  emit('size-change', size)
  emit('change', props.currentPage, size)
}

// 处理当前页变化
const handleCurrentChange = (page: number): void => {
  emit('update:currentPage', page)
  emit('current-change', page)
  emit('change', page, props.pageSize)
}

// 处理上一页点击
const handlePrevClick = (page: number): void => {
  emit('prev-click', page)
}

// 处理下一页点击
const handleNextClick = (page: number): void => {
  emit('next-click', page)
}

// 处理跳转
const handleJump = (page: number): void => {
  if (page >= 1 && page <= totalPages.value) {
    handleCurrentChange(page)
  }
}

// 监听当前页变化，同步跳转输入框
watch(() => props.currentPage, (newPage) => {
  jumpPage.value = newPage
})

// 监听总页数变化，确保跳转值在有效范围内
watch(totalPages, (newTotal) => {
  if (jumpPage.value > newTotal) {
    jumpPage.value = newTotal
  }
})

// 暴露方法给父组件
defineExpose({
  totalPages,
  jumpToPage: handleJump
})
</script>

<style scoped>
.pagination-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 0;
  flex-wrap: wrap;
  gap: 16px;
}

.pagination-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.total-info {
  font-size: 14px;
  color: #606266;
}

.page-info {
  font-size: 12px;
  color: #909399;
}

.pagination-jumper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.jumper-text {
  font-size: 14px;
  color: #606266;
}

.jumper-input {
  width: 60px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .pagination-bar {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .pagination-info {
    align-items: center;
    text-align: center;
  }

  .pagination-jumper {
    justify-content: center;
  }
}

/* 小尺寸样式 */
.pagination-bar.small {
  padding: 12px 0;
}

.pagination-bar.small .total-info {
  font-size: 12px;
}

.pagination-bar.small .page-info {
  font-size: 11px;
}

.pagination-bar.small .jumper-text {
  font-size: 12px;
}

.pagination-bar.small .jumper-input {
  width: 50px;
}
</style>