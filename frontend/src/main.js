import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'  // 引入样式
import * as ElementPlusIconsVue from '@element-plus/icons-vue'  // 引入图标

import App from './App.vue'
import router from './router'

const app = createApp(App)

// 🔥 关键配置：注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 🔥 关键配置：使用Element Plus
app.use(createPinia())
app.use(router)
app.use(ElementPlus)  // 这行让整个项目都能用Element Plus组件

app.mount('#app')
