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


    <!-- 商品状态 -->
    <el-form-item label="是否上架" prop="isAvailable">
      <el-switch
        v-model="formData.isAvailable"
        active-text="上架"
        inactive-text="下架"
      />
      <span class="form-tip">上架后商品将在前台展示</span>
    </el-form-item>


    <!-- 商品规格 -->
    <el-form-item label="商品规格">
      <div class="specifications-section">
        <div class="spec-header">
          <h4>规格属性</h4>
          <el-button type="primary" size="small" @click="addSpecification">
            <el-icon><Plus /></el-icon>
            添加规格
          </el-button>
        </div>

        <div v-if="specifications.length > 0" class="spec-list">
          <div
            v-for="(spec, index) in specifications"
            :key="index"
            class="spec-item"
          >
            <div class="spec-row">
              <el-input
                v-model="spec.name"
                placeholder="规格名称（如：颜色、尺寸）"
                style="width: 150px"
                @input="validateSpecification(index)"
              />
              <el-select
                v-model="spec.values"
                multiple
                filterable
                allow-create
                default-first-option
                placeholder="输入规格值"
                style="flex: 1; margin: 0 10px"
              >
                <el-option
                  v-for="value in spec.values"
                  :key="value"
                  :label="value"
                  :value="value"
                />
              </el-select>
              <el-button
                type="danger"
                size="small"
                @click="removeSpecification(index)"
                :disabled="specifications.length <= 1"
              >
                删除
              </el-button>
            </div>
          </div>
        </div>

        <div v-else class="empty-specs">
          <p>暂无规格，点击"添加规格"开始添加商品规格属性</p>
        </div>

        <div class="spec-tips">
          <p>💡 提示：规格用于定义商品的不同属性，如颜色、尺寸、材质等</p>
          <p>💡 每个规格可以包含多个值，如颜色：红色、蓝色、黑色</p>
        </div>
      </div>
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
            :multiple="false"
            accept="image/*"
            :limit="1"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </div>
        <div class="upload-tips">
          <p>支持 jpg、png 格式，单张图片不超过 5MB</p>
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
  isAvailable: false,
})

// 规格管理数据
const specifications = ref<Array<{ name: string; values: string[] }>>([
  { name: '', values: [] }
])

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


// 图片上传状态
const fileList = ref<any[]>([])

// 图片上传URL

// 规格管理方法
const addSpecification = () => {
  specifications.value.push({ name: '', values: [] })
}

const removeSpecification = (index: number) => {
  if (specifications.value.length > 1) {
    specifications.value.splice(index, 1)
  }
}

const validateSpecification = (index: number) => {
  const spec = specifications.value[index]
  if (!spec) return // 添加空值检查

  // 确保规格名称不为空，且不重复
  if (spec.name.trim() === '') {
    return
  }

  // 检查是否有重复的规格名称
  const duplicateIndex = specifications.value.findIndex((s, i) =>
    i !== index && s.name.trim() === spec.name.trim()
  )

  if (duplicateIndex !== -1) {
    ElMessage.warning('规格名称不能重复')
    spec.name = ''
  }
}

// 构建规格数据用于提交
const buildSpecificationsData = () => {
  const specs: Record<string, string[]> = {}

  specifications.value.forEach(spec => {
    if (spec.name.trim() && spec.values.length > 0) {
      specs[spec.name.trim()] = spec.values.filter(v => v.trim() !== '')
    }
  })

  return specs
}
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
// 注意：uploadFile 和 uploadFileList 参数是 Element Plus Upload 组件回调函数的标准参数
// 虽然当前实现中未使用这些参数，但需要保留以符合组件API规范
const handleImageSuccess = (response: any, uploadFile: any, uploadFileList: any[]) => {
  // 只使用response参数记录上传成功的响应
  // uploadFile: 当前上传的文件对象
  // uploadFileList: 当前的文件列表
  console.log('图片上传成功:', response, uploadFile)
  ElMessage.success('图片上传成功')
  // 更新主图片URL
  if (response && response.imageUrl) {
    formData.mainImageUrl = response.imageUrl
  }
}

// 图片上传失败处理
// 注意：uploadFile 和 uploadFileList 参数是 Element Plus Upload 组件回调函数的标准参数
// 虽然当前实现中未使用这些参数，但需要保留以符合组件API规范
const handleImageError = (error: any, uploadFile: any, uploadFileList: any[]) => {
  // 只使用error参数记录错误信息
  // uploadFile: 上传失败的文件对象
  // uploadFileList: 当前的文件列表
  console.error('图片上传失败:', error)
  ElMessage.error('图片上传失败，请重试')
}

// 处理表单保存
const handleSave = async () => {
  if (!productFormRef.value) return

  try {
    await productFormRef.value.validate()
    loading.value = true

    // 构建规格数据
    const specificationsData = buildSpecificationsData()

    // 确保价格和库存是数字类型
    const submitData = {
      ...formData,
      price: Number(formData.price),
      stockQuantity: Number(formData.stockQuantity),
      discount: Number(formData.discount || 0),
      productData: {
        specifications: specificationsData
      }
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
    isAvailable: false,
    mainImageUrl: '',
  })
  specifications.value = [{ name: '', values: [] }]
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
      isAvailable: newProduct.isAvailable,
      mainImageUrl: newProduct.mainImageUrl || '',
    })

    // 填充规格数据
    const specs = newProduct.productData?.specifications
    if (specs && typeof specs === 'object') {
      specifications.value = Object.entries(specs).map(([name, values]) => ({
        name,
        values: Array.isArray(values) ? values : []
      }))
    } else {
      specifications.value = [{ name: '', values: [] }]
    }

    // 填充图片数据
    if (newProduct.mainImageUrl) {
      fileList.value = [
        {
          name: '主图',
          url: newProduct.mainImageUrl
        }
      ]
    } else {
      fileList.value = []
    }
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

/* 规格管理样式 */
.specifications-section {
  width: 100%;
}

.spec-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.spec-header h4 {
  margin: 0;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}

.spec-list {
  margin-bottom: 16px;
}

.spec-item {
  margin-bottom: 12px;
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background-color: #fafafa;
}

.spec-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.empty-specs {
  text-align: center;
  padding: 20px;
  color: #909399;
  background-color: #fafafa;
  border-radius: 4px;
  margin-bottom: 16px;
}

.spec-tips {
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}

.spec-tips p {
  margin: 4px 0;
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