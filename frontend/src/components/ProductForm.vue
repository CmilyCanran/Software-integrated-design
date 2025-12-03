<template>
  <div class="product-form">
    <el-form ref="productFormRef" :model="formData" :rules="formRules">
      <!-- 🔧 标签页导航 -->
      <el-tabs v-model="activeTab" type="card">
        <!-- 基本信息标签页 -->
        <el-tab-pane label="基本信息" name="basic">
          <div class="tab-content">
            <el-form-item label="商品名称" prop="productName">
              <el-input v-model="formData.productName" placeholder="请输入商品名称（3-50个字符）" />
            </el-form-item>
            <el-form-item label="商品描述" prop="description">
              <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入商品描述" />
            </el-form-item>
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
            <el-form-item label="是否上架" prop="isAvailable">
              <el-switch
                v-model="formData.isAvailable"
                active-text="上架"
                inactive-text="下架"
              />
              <span class="form-tip">上架后商品将在前台展示</span>
            </el-form-item>
          </div>
        </el-tab-pane>

        
        <!-- 规格管理标签页 -->
        <el-tab-pane label="规格管理" name="specifications">
          <div class="tab-content">
            <!-- 🔧 统一规格系统 - 所有属性都在规格中管理 -->
            <div class="specifications-section">
              <div class="spec-header">
                <h4>商品规格</h4>
                <el-button type="primary" size="small" @click="addSpecification">
                  <el-icon><Plus /></el-icon>
                  添加规格
                </el-button>
              </div>

              <!-- 🔧 常用规格提示 -->
              <div class="spec-tips">
                <p>💡 常用规格建议：类别、品牌、颜色、尺寸、材质、季节、风格、重量、产地等</p>
                <p>🎯 每个商品可以有完全不同的规格，支持完全自定义</p>
              </div>

              <div v-if="specifications.length > 0" class="spec-list">
                <div v-for="(spec, index) in specifications" :key="index" class="spec-item">
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
                        v-for="value in getAllSpecificationValues()"
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
          </div>
        </el-tab-pane>

        <!-- 商品图片标签页 -->
        <el-tab-pane label="商品图片" name="image">
          <div class="tab-content">
            <div class="image-upload-section">
              <!-- 🔧 图片预览和上传 -->
              <div class="image-preview-area">
                <div v-if="formData.mainImageUrl" class="current-image">
                  <img :src="processImageUrl(formData.mainImageUrl)" alt="商品主图" />
                  <div class="image-actions">
                    <el-button type="primary" size="small" @click="triggerImageUpload">
                      <el-icon><Upload /></el-icon>
                      更换图片
                    </el-button>
                    <el-button type="danger" size="small" @click="removeImage">
                      <el-icon><Delete /></el-icon>
                      删除图片
                    </el-button>
                  </div>
                </div>

                <div v-else class="upload-area">
                  <el-upload
                    :show-file-list="false"
                    :http-request="handleManualUpload"
                    :multiple="false"
                    accept="image/*"
                    :disabled="isUploading"
                  >
                    <div class="upload-placeholder">
                      <el-icon size="48"><Plus /></el-icon>
                      <p>点击上传商品主图</p>
                    </div>
                  </el-upload>

                  <!-- 上传进度指示器 -->
                  <div v-if="isUploading" class="upload-progress">
                    <el-progress :percentage="uploadProgress" :show-text="true" />
                    <span>上传中... {{ uploadProgress }}%</span>
                  </div>
                </div>
              </div>
              <div class="upload-tips">
                <p>📸 商品主图：支持 jpg、png 格式，单张图片不超过 5MB</p>
                <p>💡 每个商品只需要一张主图，删除后可重新上传</p>
                <p>🔄 如需更换图片，请先删除当前图片再上传新图片</p>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>

      <!-- 操作按钮 -->
      <div class="form-actions">
        <el-button type="primary" @click="handleSave" :loading="loading">保存</el-button>
        <el-button @click="$emit('cancel')">取消</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Delete, Upload } from '@element-plus/icons-vue'
import type { Product, ProductCreateRequest, ProductUpdateRequest } from '@/types/product'
import { processImageUrl } from '@/utils/imageUtils'
import { productAPI } from '@/api/product'

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

// 上传状态管理
const isUploading = ref(false)
const uploadProgress = ref(0)

// 表单引用
const productFormRef = ref()

// 🔧 完全对齐DTO的表单数据结构
const formData = reactive<ProductCreateRequest>({
  productName: '',
  description: '',
  price: 0,
  discount: 0,
  stockQuantity: 0,
  isAvailable: false,
  productData: {},           // 新增：扩展数据
  mainImageUrl: '',          // 商品主图URL
  specifications: {},        // 统一规格数据
    })

// 🔧 新增：当前活动标签页
const activeTab = ref('basic')

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

// 修复循环依赖的核心方案 - 从当前规格数据中收集所有值
const getAllSpecificationValues = () => {
  const allValues = new Set<string>()

  // 🔧 关键修复：从当前规格数据中收集所有值
  specifications.value.forEach(spec => {
    if (spec.values && Array.isArray(spec.values)) {
      spec.values.forEach(value => {
        if (value && typeof value === 'string' && value.trim()) {
          allValues.add(value.trim())
        }
      })
    }
  })

  // 🔧 关键修复：如果没有值，返回空数组而不是undefined
  return Array.from(allValues).sort()
}

// 添加规格值到指定规格
const addSpecificationValue = (specIndex: number, value: string) => {
  if (!value || value.trim() === '') return

  const spec = specifications.value[specIndex]
  if (!spec) return

  const trimmedValue = value.trim()
  if (!spec.values.includes(trimmedValue)) {
    spec.values.push(trimmedValue)
  }
}

// 从指定规格中删除规格值
const removeSpecificationValue = (specIndex: number, valueIndex: number) => {
  const spec = specifications.value[specIndex]
  if (!spec || valueIndex < 0 || valueIndex >= spec.values.length) return

  spec.values.splice(valueIndex, 1)
}

// 验证规格数据完整性
const validateSpecificationsData = () => {
  const validSpecs = specifications.value.filter(spec =>
    spec.name.trim() !== '' || spec.values.length > 0
  )

  // 如果有规格数据，进行详细验证
  if (validSpecs.length > 0) {
    for (let i = 0; i < specifications.value.length; i++) {
      const spec = specifications.value[i]

      // 检查规格名称
      if (!spec.name.trim()) {
        ElMessage.error(`第 ${i + 1} 个规格的名称不能为空`)
        return false
      }

      // 检查规格值
      if (spec.values.length === 0) {
        ElMessage.error(`规格 "${spec.name}" 至少需要一个值`)
        return false
      }

      // 检查规格值是否为空
      const hasEmptyValue = spec.values.some(value => !value.trim())
      if (hasEmptyValue) {
        ElMessage.error(`规格 "${spec.name}" 包含空的值`)
        return false
      }

      // 检查重复的规格值
      const uniqueValues = new Set(spec.values.map(v => v.trim()))
      if (uniqueValues.size !== spec.values.length) {
        ElMessage.error(`规格 "${spec.name}" 包含重复的值`)
        return false
      }
    }

    // 检查重复的规格名称
    const specNames = specifications.value
      .filter(spec => spec.name.trim())
      .map(spec => spec.name.trim())

    const uniqueNames = new Set(specNames)
    if (uniqueNames.size !== specNames.length) {
      ElMessage.error('存在重复的规格名称')
      return false
    }
  }

  return true
}

// 统一规格数据加载逻辑 - 支持完全灵活的规格结构
const loadSpecifications = (product: any) => {
  let specs = null

  // 优先级1：直接规格字段（基于后端分析的主要来源）
  if (product.specifications && typeof product.specifications === 'object') {
    specs = product.specifications
  }
  // 优先级2：嵌套的 productData.specifications（备选）
  else if (product.productData?.specifications && typeof product.productData.specifications === 'object') {
    specs = product.productData.specifications
  }

  // 处理规格数据
  if (specs && typeof specs === 'object') {
    const processedSpecs = Object.entries(specs)
      .map(([name, values]) => ({
        name: String(name || '').trim(),
        values: Array.isArray(values)
          ? values.map(v => String(v)).filter(v => v.trim())
          : [String(values)].filter(v => v.trim())
      }))
      .filter(spec => spec.name && spec.values.length > 0)

    if (processedSpecs.length > 0) {
      // 通过创建新数组确保Vue 3响应式
      specifications.value = [...processedSpecs]
    } else {
      specifications.value = [{ name: '', values: [] }]
    }
  } else {
    specifications.value = [{ name: '', values: [] }]
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

// 构建完整的productData对象（不包含specifications，避免重复提交）
const buildProductData = () => {
  const productData: Record<string, any> = {}

  // 添加其他可能需要的产品数据字段
  if (formData.mainImageUrl) {
    productData.image_data = {
      main_image: formData.mainImageUrl
    }
  }

  return productData
}
// uploadAction 已移除 - 现在使用认证的 productAPI.uploadProductImage() 方法

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

// 手动上传处理器（带认证）
const handleManualUpload = async (options: any) => {
  const file = options.file
  console.log('🔍 [DEBUG] handleManualUpload 开始', { file: file.name, size: file.size })

  if (!file) {
    console.log('❌ [DEBUG] 文件为空')
    options.onError(new Error('文件为空'))
    return
  }

  // 验证商品是否存在（编辑模式）
  console.log('🔍 [DEBUG] 检查商品信息', { isEdit: props.isEdit, product: props.product })
  if (props.isEdit && !props.product?.id) {
    console.log('❌ [DEBUG] 商品ID不存在')
    ElMessage.error('请先保存商品信息')
    options.onError(new Error('请先保存商品信息'))
    return
  }

  // 文件验证（使用现有逻辑）
  console.log('🔍 [DEBUG] 开始文件验证')
  if (!beforeImageUpload(file)) {
    console.log('❌ [DEBUG] 文件验证失败')
    options.onError(new Error('文件验证失败'))
    return
  }

  console.log('✅ [DEBUG] 文件验证通过，开始上传')
  isUploading.value = true
  uploadProgress.value = 0

  try {
    // 使用认证的 API 方法
    console.log('🔍 [DEBUG] 调用API上传', { productId: props.product!.id })
    const response = await productAPI.uploadProductImage(props.product!.id, file)
    console.log('✅ [DEBUG] API调用成功', response)

    // 更新表单数据中的图片 URL
    formData.mainImageUrl = response.imageUrl
    ElMessage.success('图片上传成功')
    options.onSuccess(response)

  } catch (error: any) {
    console.error('❌ [DEBUG] 图片上传失败:', error)

    // 处理特定的认证错误
    if (error.response?.status === 401) {
      ElMessage.error('认证失败，请重新登录')
    } else {
      ElMessage.error(error.response?.data?.message || '图片上传失败')
    }
    options.onError(error)
  } finally {
    isUploading.value = false
    uploadProgress.value = 0
  }
}

// 图片上传成功处理（已简化 - 成功处理在 handleManualUpload 中）
const handleImageSuccess = (response: any) => {
  console.log('图片上传成功:', response)
  // 成功处理现在在 handleManualUpload 中完成
}

// 图片上传失败处理（已简化 - 错误处理在 handleManualUpload 中）
const handleImageError = (error: any) => {
  console.error('图片上传失败:', error)
  // 错误处理现在在 handleManualUpload 中完成
}

// 删除图片 - 立即执行软删除
const removeImage = async () => {
  if (!props.product?.id) {
    ElMessage.error('商品信息不存在')
    return
  }

  try {
    isUploading.value = true
    await productAPI.deleteProductImage(props.product.id)
    formData.mainImageUrl = ''
    ElMessage.success('图片已删除')
  } catch (error: any) {
    console.error('删除图片失败:', error)
    ElMessage.error(error.response?.data?.message || '图片删除失败')
  } finally {
    isUploading.value = false
  }
}

// 触发图片更换（点击"更换图片"按钮时调用）
const triggerImageUpload = () => {
  if (isUploading.value) {
    ElMessage.warning('正在操作中，请稍候')
    return
  }

  if (!props.product?.id) {
    ElMessage.error('请先保存商品信息')
    return
  }

  const fileInput = document.createElement('input')
  fileInput.type = 'file'
  fileInput.accept = 'image/*'
  fileInput.style.display = 'none'

  fileInput.onchange = async (event: any) => {
    const file = event.target.files[0]
    if (file && beforeImageUpload(file)) {
      isUploading.value = true

      try {
        // 1. 如果有现有图片，先删除
        if (formData.mainImageUrl) {
          await productAPI.deleteProductImage(props.product!.id)
          console.log('旧图片删除成功')
        }

        // 2. 上传新图片
        const response = await productAPI.uploadProductImage(props.product!.id, file)
        formData.mainImageUrl = response.imageUrl
        ElMessage.success('图片更新成功')

      } catch (error: any) {
        console.error('图片更新失败:', error)
        ElMessage.error(error.response?.data?.message || '图片更新失败')
      } finally {
        isUploading.value = false
      }
    }

    document.body.removeChild(fileInput)
  }

  document.body.appendChild(fileInput)
  fileInput.click()
}

// 🔧 后端验证错误的智能处理
const handleBackendValidationError = (error: any) => {
  const response = error.response?.data

  if (response?.code === 400 && response?.message) {
    const errorMessage = response.message

    // 根据错误类型显示不同的提示 - 映射后端验证注解
    if (errorMessage.includes('商品名称') || errorMessage.includes('productName')) {
      ElMessage.error(`商品名称验证失败: ${errorMessage}`)
    } else if (errorMessage.includes('价格') || errorMessage.includes('price')) {
      ElMessage.error(`价格验证失败: ${errorMessage}`)
    } else if (errorMessage.includes('库存') || errorMessage.includes('stockQuantity')) {
      ElMessage.error(`库存验证失败: ${errorMessage}`)
    } else if (errorMessage.includes('折扣') || errorMessage.includes('discount')) {
      ElMessage.error(`折扣验证失败: ${errorMessage}`)
    } else if (errorMessage.includes('规格') || errorMessage.includes('specifications')) {
      ElMessage.error(`规格数据验证失败: ${errorMessage}`)
    } else {
      ElMessage.error(`数据验证失败: ${errorMessage}`)
    }
  } else {
    ElMessage.error('保存失败，请检查网络连接或稍后重试')
  }
}

// 🔧 增强的保存方法
const handleSave = async () => {
  if (!productFormRef.value) return

  try {
    await productFormRef.value.validate()
    loading.value = true

    // 构建完全对齐DTO的数据
    const specificationsData = buildSpecificationsData()
    const submitData = {
      ...formData,
      specifications: specificationsData
    } as ProductCreateRequest | ProductUpdateRequest

    emit('save', submitData)
  } catch (error) {
    handleBackendValidationError(error)
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
    discount: 0,
    stockQuantity: 0,
    isAvailable: false,
    productData: {},           // 重置扩展数据
    mainImageUrl: '',          // 重置商品主图URL
    specifications: {},        // 重置规格数据
          })
  specifications.value = [{ name: '', values: [] }]
  fileList.value = []
  activeTab.value = 'basic'  // 重置标签页
  if (productFormRef.value) {
    productFormRef.value.clearValidate()
  }
}

// 监听属性变化，填充表单数据
watch(() => props.product, (newProduct) => {
  console.log('🔍 [DEBUG] 产品监视器触发，isEdit:', props.isEdit)
  console.log('🔍 [DEBUG] 新产品数据:', newProduct)

  if (props.isEdit && newProduct) {
    // 填充商品数据
    Object.assign(formData, {
      productName: newProduct.productName,
      description: newProduct.description || '',
      price: newProduct.price,
      stockQuantity: newProduct.stockQuantity,
      discount: newProduct.discount,
      isAvailable: newProduct.isAvailable,
      productData: newProduct.productData || {},  // 填充扩展数据
      mainImageUrl: newProduct.mainImageUrl || '', // 填充商品主图URL
      specifications: newProduct.specifications || {}, // 填充规格数据
                })

    // 关键：加载规格
    loadSpecifications(newProduct)

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

    // 强制UI更新
    nextTick(() => {
      console.log('🔄 [DEBUG] Next tick 完成，UI应该已更新')
    })
  } else {
    console.log('🔍 [DEBUG] 重置表单（非编辑模式或无产品）')
    resetForm()
  }
}, { immediate: true, deep: true })

// 添加响应式监听确保UI及时更新
watch(() => specifications.value, (newSpecs) => {
  console.log('规格数据更新:', newSpecs)
  // 强制触发el-select选项更新
  nextTick(() => {
    // Vue会在下一个tick更新DOM
  })
}, { deep: true })
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

/* 图片预览区域样式 */
.image-preview-area {
  margin-bottom: 20px;
}

.current-image {
  position: relative;
  display: inline-block;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 8px;
  background-color: #fafafa;
}

.current-image img {
  width: 200px;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
  display: block;
}

.image-actions {
  margin-top: 10px;
  text-align: center;
}

.upload-area {
  border: 2px dashed #d9d9d9;
  border-radius: 6px;
  padding: 40px;
  text-align: center;
  background-color: #fafafa;
  transition: border-color 0.3s ease;
}

.upload-area:hover {
  border-color: #409eff;
}

.upload-placeholder {
  color: #8c939d;
  cursor: pointer;
}

.upload-placeholder p {
  margin: 16px 0 0 0;
  font-size: 14px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
}

/* 上传进度样式 */
.upload-progress {
  margin-top: 10px;
  text-align: center;
}

.upload-progress span {
  display: block;
  margin-top: 8px;
  font-size: 14px;
  color: #409eff;
}

@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 0;
  }
}
</style>