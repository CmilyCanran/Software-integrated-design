<template>
  <el-form
    ref="productFormRef"
    :model="formData"
    :rules="formRules"
    label-width="100px"
  >
    <!-- 商品基本信息 -->
    <el-form-item label="商品名称" prop="productName">
      <el-input
        v-model="formData.productName"
        placeholder="请输入商品名称（3-50个字符）"
      />
    </el-form-item>

    <el-form-item label="商品描述" prop="description">
      <el-input
        v-model="formData.description"
        type="textarea"
        :rows="3"
        placeholder="请输入商品描述"
      />
    </el-form-item>

    <!-- 商品价格和库存 -->
    <div class="form-row">
      <el-form-item label="商品价格" prop="price">
        <el-input-number
          v-model="formData.price"
          :min="0.01"
          :precision="2"
          :step="1"
          controls-position="right"
          style="width: 200px"
          placeholder="请输入商品价格"
        />
      </el-form-item>

      <el-form-item label="商品库存" prop="stockQuantity">
        <el-input-number
          v-model="formData.stockQuantity"
          :min="0"
          :step="1"
          controls-position="right"
          style="width: 150px"
          placeholder="请输入库存数量"
        />
      </el-form-item>
    </div>

    <!-- 折扣信息 -->
    <el-form-item label="折扣率" prop="discount">
      <el-input-number
        v-model="formData.discount"
        :min="0"
        :max="100"
        :step="5"
        controls-position="right"
        style="width: 150px"
        placeholder="请输入折扣率（0-100%）"
      />
      <span class="form-tip">折扣率 0-100，如：10 表示打9折</span>
    </el-form-item>

    <!-- 商品分类和品牌 -->
    <div class="form-row">
      <el-form-item label="商品分类" prop="category">
        <el-select
          v-model="formData.category"
          placeholder="请选择商品分类"
          clearable
          style="width: 200px"
        >
          <el-option label="服装" value="服装" />
          <el-option label="鞋类" value="鞋类" />
          <el-option label="配饰" value="配饰" />
          <el-option label="箱包" value="箱包" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>

      <el-form-item label="商品品牌" prop="brand">
        <el-input
          v-model="formData.brand"
          placeholder="请输入商品品牌"
          style="width: 200px"
        />
      </el-form-item>
    </div>

    <!-- 商品状态 -->
    <el-form-item label="是否上架" prop="isAvailable">
      <el-switch
        v-model="formData.isAvailable"
        active-text="上架"
        inactive-text="下架"
      />
      <span class="form-tip">上架后商品将在前台展示</span>
    </el-form-item>

    <!-- 商品标签 -->
    <el-form-item label="商品标签" prop="tags">
      <el-select
        v-model="formData.tags"
        multiple
        filterable
        allow-create
        default-first-option
        placeholder="请输入或选择商品标签"
        style="width: 100%"
      >
        <el-option
          v-for="item in tagOptions"
          :key="item"
          :label="item"
          :value="item"
        />
      </el-select>
      <span class="form-tip">按回车键或点击添加标签</span>
    </el-form-item>

    <!-- 图片上传区域 -->
    <el-form-item label="商品图片">
      <div class="image-upload-section">
        <div class="upload-area">
          <el-upload
            :action="uploadAction"
            list-type="picture-card"
            :on-success="handleImageSuccess"
            :on-error="handleImageError"
            :before-upload="beforeImageUpload"
            :file-list="fileList"
            :multiple="true"
            accept="image/*"
            :limit="5"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </div>
        <div class="upload-tips">
          <p>支持 jpg、png 格式，单张图片不超过 5MB，最多上传 5 张</p>
        </div>
      </div>
    </el-form-item>

    <!-- 表单操作按钮 -->
    <el-form-item>
      <div class="form-actions">
        <el-button type="primary" @click="handleSave" :loading="loading">
          保存
        </el-button>
        <el-button @click="$emit('cancel')">
          取消
        </el-button>
      </div>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { Product, ProductCreateRequest, ProductUpdateRequest } from '@/types/product'

// 属性定义
const props = defineProps<{
  product?: Product | null
  isEdit: boolean
}>()

// 事件定义
const emit = defineEmits<{
  save: [data: ProductCreateRequest | ProductUpdateRequest]
  cancel: []
}>()

// 加载状态
const loading = ref(false)

// 表单引用
const productFormRef = ref()

// 表单数据
const formData = reactive<Partial<ProductCreateRequest | ProductUpdateRequest>>({
  productName: '',
  description: '',
  price: 0,
  stockQuantity: 0,
  discount: 0,
  category: '',
  brand: '',
  isAvailable: false,
  tags: [] as string[]
})

// 表单验证规则
const formRules = reactive({
  productName: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { min: 3, max: 50, message: '商品名称长度在 3 到 50 个字符', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '商品价格必须大于0', trigger: 'blur' }
  ],
  stockQuantity: [
    { required: true, message: '请输入库存数量', trigger: 'blur' },
    { type: 'number', min: 0, message: '库存数量不能为负数', trigger: 'blur' }
  ],
  discount: [
    { type: 'number', min: 0, max: 100, message: '折扣率范围在 0 到 100', trigger: 'blur' }
  ]
})

// 标签选项
const tagOptions = [
  '新品',
  '热销',
  '推荐',
  '限时特价',
  '品牌正品',
  '包邮',
  '七日退换'
]

// 图片上传状态
const fileList = ref<any[]>([])

// 图片上传URL
const uploadAction = computed(() => {
  return props.isEdit && props.product
    ? `/api/products/${props.product.id}/image`
    : '/api/products/upload'
})

// 图片上传前验证
const beforeImageUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }

  return true
}

// 图片上传成功处理
const handleImageSuccess = (response: any, file: any, fileList: any[]) => {
  console.log('图片上传成功:', response, file)
  ElMessage.success('图片上传成功')
}

// 图片上传失败处理
const handleImageError = (error: any, file: any, fileList: any[]) => {
  console.error('图片上传失败:', error)
  ElMessage.error('图片上传失败，请重试')
}

// 处理表单保存
const handleSave = async () => {
  if (!productFormRef.value) return

  try {
    await productFormRef.value.validate()
    loading.value = true

    // 确保价格和库存是数字类型
    const submitData = {
      ...formData,
      price: Number(formData.price),
      stockQuantity: Number(formData.stockQuantity),
      discount: Number(formData.discount || 0)
    } as ProductCreateRequest | ProductUpdateRequest

    emit('save', submitData)
  } catch (error) {
    console.error('表单验证失败:', error)
    ElMessage.error('请检查表单填写是否正确')
  } finally {
    loading.value = false
  }
}

// 重置表单数据
const resetForm = () => {
  Object.assign(formData, {
    productName: '',
    description: '',
    price: 0,
    stockQuantity: 0,
    discount: 0,
    category: '',
    brand: '',
    isAvailable: false,
    tags: [] as string[]
  })
  fileList.value = []
  if (productFormRef.value) {
    productFormRef.value.clearValidate()
  }
}

// 监听属性变化，填充表单数据
watch(() => props.product, (newProduct) => {
  if (props.isEdit && newProduct) {
    // 填充商品数据
    Object.assign(formData, {
      productName: newProduct.productName,
      description: newProduct.description || '',
      price: newProduct.price,
      stockQuantity: newProduct.stockQuantity,
      discount: newProduct.discount,
      category: newProduct.category || '',
      brand: newProduct.brand || '',
      isAvailable: newProduct.isAvailable,
      tags: newProduct.tags || []
    })
  } else {
    // 重置表单
    resetForm()
  }
}, { immediate: true })
</script>

<style scoped>
.form-row {
  display: flex;
  gap: 20px;
}

.form-tip {
  font-size: 12px;
  color: #999;
  margin-left: 8px;
}

.image-upload-section {
  width: 100%;
}

.upload-area {
  margin-bottom: 10px;
}

.upload-tips {
  color: #999;
  font-size: 12px;
}

.upload-tips p {
  margin: 0;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 0;
  }
}
</style>