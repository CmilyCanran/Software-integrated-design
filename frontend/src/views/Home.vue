<template>
  <div class="home-container">
    <Header
      page-title="首页"
      :showCart="isUserRole"
      :cartItemCount="cartItemCount"
    />
    <el-main class="main-content">
      <!-- 轮播图区域 -->
      <div class="banner-section">
        <el-carousel height="500px" :interval="5000" arrow="always">
          <el-carousel-item v-for="item in banners" :key="item.id">
            <div class="banner-item" :style="{ backgroundImage: `url(${item.imageUrl})` }">
              <div class="banner-overlay"></div>
              <div class="banner-content">
                <h2>{{ item.title }}</h2>
                <p>{{ item.subtitle }}</p>
                <el-button type="primary" size="large">{{ item.buttonText }}</el-button>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>

      <!-- 主要内容区域 -->
      <div class="content-section">
        <div class="welcome-section">
          <h2 class="welcome-title">欢迎来到时尚服装商城</h2>
          <p class="welcome-description">
            我们为您提供最新潮的服装选择，高品质的商品和优质的服务。
            无论您是寻找日常穿搭还是特殊场合的服装，我们都能满足您的需求。
          </p>
        </div>

        <div class="features-section">
          <div class="feature-item feature-item-animate" v-for="(feature, index) in features" :key="index">
            <div class="feature-icon">
              <el-icon :size="48" :class="feature.iconClass">
                <component :is="feature.icon" />
              </el-icon>
            </div>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.description }}</p>
          </div>
        </div>

        <div class="cta-section">
          <h3>开始您的购物之旅</h3>
          <p>浏览我们的商品分类，发现最适合您的时尚单品</p>
          <div class="cta-buttons">
            <el-button type="primary" size="large" @click="goToProducts">浏览商品</el-button>
            <el-button type="default" size="large" @click="goToProducts">新品推荐</el-button>
          </div>
        </div>
      </div>

      <!-- 促销横幅 -->
      <div class="promo-section">
        <div class="promo-banner">
          <h3>季节大促</h3>
          <p>全场服装低至5折起，限时优惠！</p>
          <el-button type="danger" size="large">立即购买</el-button>
        </div>
      </div>
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import Header from '@/components/Header.vue'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { Goods, Van, Service } from '@element-plus/icons-vue'

// 状态管理
const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()

// 轮播图数据
const banners = [
  {
    id: 1,
    title: '春夏新品上市',
    subtitle: '时尚潮流，品质保证',
    buttonText: '立即选购',
    imageUrl: 'https://images.unsplash.com/photo-1520006403909-838d6b92c221?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1200&q=80'
  },
  {
    id: 2,
    title: '限时折扣',
    subtitle: '精选商品5折起',
    buttonText: '立即抢购',
    imageUrl: 'https://images.unsplash.com/photo-1539109136881-3be0616acfbe?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1200&q=80'
  },
  {
    id: 3,
    title: '会员专享',
    subtitle: 'VIP会员享受额外优惠',
    buttonText: '成为会员',
    imageUrl: 'https://images.unsplash.com/photo-1593030103066-0093718efeb9?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1200&q=80'
  }
]

// 特性功能数据
const features = [
  {
    title: '品质保证',
    description: '所有商品均经过严格质量检测，确保您购买到的每一件商品都是精品。',
    icon: Goods,
    iconClass: 'quality-icon'
  },
  {
    title: '快速配送',
    description: '下单后快速发货，让您的购物体验更加便捷愉快。',
    icon: Van,
    iconClass: 'delivery-icon'
  },
  {
    title: '优质服务',
    description: '专业的客服团队随时为您解答疑问，提供优质的购物体验。',
    icon: Service,
    iconClass: 'service-icon'
  }
]

// 计算属性
const isUserRole = computed(() => {
  return authStore.userInfo?.role === 'USER'
})

const cartItemCount = computed(() => {
  return cartStore.totalItems
})

// 事件处理函数
const goToProducts = () => {
  router.push('/products')
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.main-content {
  padding: 0;
}

/* 轮播图样式 */
.banner-section {
  margin-bottom: 60px;
  border-radius: 0 0 20px 20px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.banner-item {
  width: 100%;
  height: 500px;
  background-size: cover;
  background-position: center;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.7) 0%, rgba(118, 75, 162, 0.7) 100%);
  z-index: 1;
}

.banner-content {
  text-align: center;
  color: white;
  z-index: 2;
  max-width: 600px;
  padding: 20px;
}

.banner-content h2 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  animation: slideInDown 0.8s ease-out;
}

.banner-content p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
  animation: slideInUp 0.8s ease-out 0.2s both;
}

.banner-content .el-button {
  animation: fadeIn 0.8s ease-out 0.4s both;
}

/* 主要内容区域样式 */
.content-section {
  padding: 0 20px 60px;
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-section {
  text-align: center;
  margin-bottom: 60px;
  animation: fadeInUp 1s ease-out;
}

.welcome-title {
  font-size: 2.5rem;
  color: #2c3e50;
  margin-bottom: 20px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-fill-color: transparent;
}

.welcome-description {
  font-size: 1.2rem;
  color: #7f8c8d;
  line-height: 1.8;
  max-width: 800px;
  margin: 0 auto;
  font-weight: 300;
}

.features-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 40px;
  margin-bottom: 60px;
}

.feature-item {
  text-align: center;
  padding: 40px 30px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.feature-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.feature-item:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.feature-item:hover::before {
  transform: scaleX(1);
}

.feature-icon {
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea20 0%, #764ba220 100%);
}

.quality-icon {
  color: #667eea;
}

.delivery-icon {
  color: #f093fb;
}

.service-icon {
  color: #4facfe;
}

.feature-item h3 {
  font-size: 1.5rem;
  color: #2c3e50;
  margin-bottom: 16px;
  font-weight: 600;
}

.feature-item p {
  font-size: 1rem;
  color: #7f8c8d;
  line-height: 1.6;
  margin: 0;
}

.feature-item-animate {
  opacity: 0;
  transform: translateY(30px);
  animation: slideInUp 0.6s ease-out forwards;
}

.feature-item-animate:nth-child(1) {
  animation-delay: 0.1s;
}

.feature-item-animate:nth-child(2) {
  animation-delay: 0.2s;
}

.feature-item-animate:nth-child(3) {
  animation-delay: 0.3s;
}

.cta-section {
  text-align: center;
  padding: 50px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  margin-bottom: 60px;
  animation: fadeInUp 1s ease-out 0.5s both;
}

.cta-section h3 {
  font-size: 1.8rem;
  color: #2c3e50;
  margin-bottom: 16px;
  font-weight: 600;
}

.cta-section p {
  font-size: 1.2rem;
  color: #7f8c8d;
  margin-bottom: 30px;
  font-weight: 300;
}

.cta-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
}

.cta-buttons .el-button {
  min-width: 160px;
  font-size: 16px;
}

/* 促销横幅样式 */
.promo-section {
  padding: 0 20px 60px;
  max-width: 1200px;
  margin: 0 auto;
}

.promo-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-align: center;
  padding: 50px;
  border-radius: 16px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  animation: fadeInUp 1s ease-out 0.7s both;
}

.promo-banner h3 {
  font-size: 2.2rem;
  margin-bottom: 15px;
  font-weight: 600;
}

.promo-banner p {
  font-size: 1.3rem;
  margin-bottom: 25px;
  opacity: 0.9;
}

.promo-banner .el-button {
  min-width: 180px;
  font-size: 18px;
  padding: 16px 32px;
}

/* 动画效果 */
@keyframes slideInDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .banner-content h2 {
    font-size: 2rem;
  }

  .banner-content p {
    font-size: 1rem;
  }

  .banner-item {
    height: 300px;
  }

  .welcome-title {
    font-size: 2rem;
  }

  .welcome-description {
    font-size: 1rem;
  }

  .features-section {
    grid-template-columns: 1fr;
  }

  .feature-item {
    padding: 30px 20px;
  }

  .cta-section {
    padding: 30px;
  }

  .cta-buttons {
    flex-direction: column;
    align-items: center;
  }

  .cta-buttons .el-button {
    width: 100%;
    max-width: 300px;
  }

  .promo-banner {
    padding: 30px 20px;
  }

  .promo-banner h3 {
    font-size: 1.8rem;
  }

  .promo-banner p {
    font-size: 1.1rem;
  }
}

@media (max-width: 480px) {
  .banner-content h2 {
    font-size: 1.5rem;
  }

  .welcome-title {
    font-size: 1.8rem;
  }

  .feature-icon {
    width: 60px;
    height: 60px;
  }

  .feature-item h3 {
    font-size: 1.3rem;
  }

  .feature-item p {
    font-size: 0.9rem;
  }

  .promo-banner h3 {
    font-size: 1.5rem;
  }

  .promo-banner p {
    font-size: 1rem;
  }
}
</style>