<template>
  <router-view />
</template>

<script setup>
// 这里不需要额外逻辑
</script>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f7fa;
  line-height: 1.6;
  color: #333;
}

#app {
  min-height: 100vh;
}

/* 链接样式 */
a {
  text-decoration: none;
  color: inherit;
}

/* 卡片通用样式 */
.el-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border: none;
}

/* 页面容器 */
.page-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 页面标题 */
.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #303133;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-container {
    padding: 10px;
  }
}
</style>
