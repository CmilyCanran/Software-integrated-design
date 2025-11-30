// ============================================================================
// 图片URL处理工具
// ============================================================================

/**
 * 处理图片URL，确保包含正确的API前缀
 * @param imageUrl 原始图片URL
 * @returns 处理后的图片URL
 */
export const processImageUrl = (imageUrl: string | null | undefined): string => {
  if (!imageUrl) {
    return '/images/placeholder-product.png'
  }

  // 如果URL已经是完整的HTTP URL，直接返回
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    return imageUrl
  }

  // 如果URL已经包含/api前缀，直接返回
  if (imageUrl.startsWith('/api/')) {
    return imageUrl
  }

  // 如果是图片上传路径，添加/api前缀
  if (imageUrl.startsWith('/uploads/images/')) {
    return `/api${imageUrl}`
  }

  // 其他情况，直接返回原URL
  return imageUrl
}

/**
 * 批量处理商品数据中的图片URL
 * @param products 商品列表
 * @returns 处理后的商品列表
 */
export const processProductImages = <T extends Record<string, any>>(products: T[]): T[] => {
  return products.map(product => {
    const processedProduct = { ...product }

    // 处理主图片URL
    if (processedProduct.product_data?.image_data?.main_image) {
      processedProduct.product_data.image_data.main_image = processImageUrl(
        processedProduct.product_data.image_data.main_image
      )
    }

    // 处理其他图片URL（如果有的话）
    if (processedProduct.product_data?.image_data?.additional_images) {
      processedProduct.product_data.image_data.additional_images =
        processedProduct.product_data.image_data.additional_images.map(processImageUrl)
    }

    return processedProduct
  })
}

/**
 * 处理单个商品数据中的图片URL
 * @param product 单个商品数据
 * @returns 处理后的商品数据
 */
export const processSingleProductImage = <T extends Record<string, any>>(product: T): T => {
  const processedProduct = { ...product }

  // 处理主图片URL
  if (processedProduct.product_data?.image_data?.main_image) {
    processedProduct.product_data.image_data.main_image = processImageUrl(
      processedProduct.product_data.image_data.main_image
    )
  }

  // 处理其他图片URL（如果有的话）
  if (processedProduct.product_data?.image_data?.additional_images) {
    processedProduct.product_data.image_data.additional_images =
      processedProduct.product_data.image_data.additional_images.map(processImageUrl)
    }

  return processedProduct
}