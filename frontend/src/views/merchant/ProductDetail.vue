<template>
  <div class="merchant-product-detail">
    <Header page-title="商品详情" />

    <div class="detail-container">
      <!-- 左侧：嵌入的用户商品详情组件 -->
      <div class="user-detail-section">
        <ProductDetail
          v-if="product"
          :product="product"
          :hide-shopping-cart="true"
        />
      </div>

      <!-- 右侧：商家专属信息面板 -->
      <div class="merchant-info-panel">
        <el-card class="info-card">
          <template #header>
            <h3>商家数据</h3>
          </template>

          <!-- 销售数据 -->
          <div class="info-section">
            <h4>销售数据</h4>
            <div class="stats-grid">
              <div class="stat-item">
                <span class="label">总销量</span>
                <span class="value">{{ product?.salesCount || 0 }}</span>
              </div>
              <div class="stat-item">
                <span class="label">库存数量</span>
                <span class="value">{{ product?.stockQuantity || 0 }}</span>
              </div>
              <div class="stat-item">
                <span class="label">商品状态</span>
                <el-tag :type="product?.isAvailable ? 'success' : 'danger'">
                  {{ product?.isAvailable ? '在售' : '下架' }}
                </el-tag>
              </div>
            </div>
          </div>

          <!-- 商品信息 -->
          <div class="info-section">
            <h4>商品信息</h4>
            <div class="info-list">
              <div class="info-item">
                <span class="label">商品ID</span>
                <span class="value">{{ product?.id }}</span>
              </div>
              <div class="info-item">
                <span class="label">创建时间</span>
                <span class="value">{{ formatDate(product?.createdAt) }}</span>
              </div>
              <div class="info-item">
                <span class="label">更新时间</span>
                <span class="value">{{ formatDate(product?.updatedAt) }}</span>
              </div>
                          </div>
          </div>

          <!-- 快速操作 -->
          <div class="info-section">
            <h4>快速操作</h4>
            <div class="action-buttons">
              <el-button type="primary" @click="editProduct">
                <el-icon><Edit /></el-icon>
                编辑商品
              </el-button>
              <el-button
                :type="product?.isAvailable ? 'warning' : 'success'"
                @click="toggleProductStatus"
              >
                {{ product?.isAvailable ? '下架商品' : '上架商品' }}
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 商品编辑对话框 -->
    <el-dialog
      v-model="formDialogVisible"
      title="编辑商品"
      width="600px"
    >
      <ProductForm
        :product="editingProduct"
        :is-edit="true"
        @save="handleFormSave"
        @cancel="closeFormDialog"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'
import ProductDetail from '@/views/ProductDetail.vue'
import Header from '@/components/Header.vue'
import ProductForm from '@/components/ProductForm.vue'
import type { Product, ProductUpdateRequest } from '@/types/product'
import { productAPI } from '@/api/product'
import { useProductCrud } from '@/composables/useProductCrud'
import dayjs from 'dayjs'

// 路由相关
const route = useRoute()
const router = useRouter()

// 响应式数据
const product = ref<Product | null>(null)

// 使用组合式函数管理编辑功能
const {
  formDialogVisible,
  isEditing,
  editingProduct,
  openEditDialog,
  handleUpdate,
  closeFormDialog
} = useProductCrud({
  onUpdateSuccess: () => {
    // 编辑成功后重新加载商品数据
    loadProduct()
  }
})

// 日期格式化函数
const formatDate = (date: string | undefined) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

// 加载商品数据
const loadProduct = async () => {
  try {
    const productId = Number(route.params.id)
    if (!productId) {
      ElMessage.error('商品ID无效')
      router.push('/merchant/products')
      return
    }

    product.value = await productAPI.getProduct(productId)
  } catch (error) {
    console.error('Load product error:', error)
    ElMessage.error('加载商品信息失败')
    router.push('/merchant/products')
  }
}

// 编辑商品
const editProduct = async () => {
  if (!product.value) return
  await openEditDialog(product.value)
}

// 处理表单保存
const handleFormSave = async (formData: ProductUpdateRequest) => {
  try {
    if (isEditing.value && editingProduct.value) {
      await handleUpdate(editingProduct.value.id, formData)
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败，请重试')
  }
}

// 切换商品状态
const toggleProductStatus = async () => {
  if (!product.value) return

  try {
    const action = product.value.isAvailable ? '下架' : '上架'
    await ElMessageBox.confirm(
      `确定要${action}商品"${product.value.productName}"吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 调用专门的切换状态API
    await productAPI.toggleProductStatus(product.value.id)

    // 重新获取完整的商品信息，避免数据丢失
    const updatedProduct = await productAPI.getProduct(product.value.id)
    product.value = updatedProduct

    ElMessage.success(`商品${action}成功`)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Toggle product status error:', error)
      ElMessage.error('操作失败，请重试')
    }
  }
}

// 页面挂载时加载商品数据
onMounted(() => {
  loadProduct()
})
</script>

<style scoped>
.merchant-product-detail {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.detail-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 30px;
  align-items: start;
}

.user-detail-section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.merchant-info-panel {
  position: sticky;
  top: 20px;

  .info-card {
    border: none;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    .el-card__header {
      background: #f8f9fa;
      border-bottom: 1px solid #ebeef5;

      h3 {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }
  }

  .info-section {
    margin-bottom: 24px;

    &:last-child {
      margin-bottom: 0;
    }

    h4 {
      margin: 0 0 16px 0;
      font-size: 14px;
      font-weight: 600;
      color: #606266;
    }
  }

  .stats-grid {
    display: grid;
    gap: 12px;

    .stat-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 16px;
      background: #f8f9fa;
      border-radius: 6px;

      .label {
        color: #909399;
        font-size: 14px;
      }

      .value {
        font-weight: 600;
        color: #303133;
        font-size: 16px;
      }
    }
  }

  .info-list {
    .info-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 8px 0;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .label {
        color: #909399;
        font-size: 14px;
        min-width: 80px;
      }

      .value {
        color: #303133;
        font-size: 14px;
        font-weight: 500;
      }
    }
  }

  .action-buttons {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .el-button {
      justify-content: flex-start;

      .el-icon {
        margin-right: 8px;
      }
    }
  }
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .detail-container {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .merchant-info-panel {
    position: static;

    .stats-grid {
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    }

    .action-buttons {
      flex-direction: row;

      .el-button {
        flex: 1;
      }
    }
  }
}

@media (max-width: 768px) {
  .detail-container {
    padding: 12px;
  }

  .stats-grid {
    grid-template-columns: 1fr !important;
  }

  .action-buttons {
    flex-direction: column !important;
  }
}
</style>