// ============================================================================
// 商品规格工具函数
// ============================================================================

import type { Product, ProductSpecifications } from '@/types/product'

/**
 * 获取商品规格数据
 * @param product 商品对象
 * @returns 规格数据对象
 */
export const getProductSpecifications = (product: Product): ProductSpecifications => {
  return product.productData?.specifications || {}
}

/**
 * 检查商品是否有规格数据
 * @param product 商品对象
 * @returns 是否有规格数据
 */
export const hasSpecifications = (product: Product): boolean => {
  const specs = getProductSpecifications(product)
  return Object.keys(specs).length > 0
}

/**
 * 格式化规格数据显示
 * @param specs 规格数据对象
 * @returns 格式化后的字符串
 */
export const formatSpecifications = (specs: ProductSpecifications): string => {
  return Object.entries(specs)
    .map(([key, values]) => `${key}: ${Array.isArray(values) ? values.join(', ') : values}`)
    .join(' | ')
}

/**
 * 获取所有可用的规格名称
 * @param products 商品数组
 * @returns 规格名称数组
 */
export const getAllSpecificationNames = (products: Product[]): string[] => {
  const names = new Set<string>()

  products.forEach(product => {
    const specs = getProductSpecifications(product)
    Object.keys(specs).forEach(name => names.add(name))
  })

  return Array.from(names)
}

/**
 * 获取指定规格名称的所有可用值
 * @param products 商品数组
 * @param specName 规格名称
 * @returns 规格值数组
 */
export const getSpecificationValues = (products: Product[], specName: string): string[] => {
  const values = new Set<string>()

  products.forEach(product => {
    const specs = getProductSpecifications(product)
    const specValues = specs[specName]

    if (Array.isArray(specValues)) {
      specValues.forEach(value => values.add(value))
    }
  })

  return Array.from(values)
}

/**
 * 根据规格筛选商品
 * @param products 商品数组
 * @param selectedSpecs 选中的规格筛选条件
 * @returns 筛选后的商品数组
 */
export const filterProductsBySpecifications = (
  products: Product[],
  selectedSpecs: Record<string, string[]>
): Product[] => {
  return products.filter(product => {
    // 如果没有选中的筛选条件，返回所有商品
    if (Object.keys(selectedSpecs).length === 0) {
      return true
    }

    const productSpecs = getProductSpecifications(product)

    // 检查每个选中的规格是否匹配
    return Object.entries(selectedSpecs).every(([specName, selectedValues]) => {
      // 如果没有选中该规格的值，跳过检查
      if (selectedValues.length === 0) {
        return true
      }

      const productValues = productSpecs[specName]

      // 如果商品没有该规格，不匹配
      if (!Array.isArray(productValues)) {
        return false
      }

      // 检查是否有匹配的值
      return selectedValues.some(selectedValue =>
        productValues.includes(selectedValue)
      )
    })
  })
}

/**
 * 合并多个商品的规格数据
 * @param products 商品数组
 * @returns 合并后的规格数据
 */
export const mergeSpecifications = (products: Product[]): ProductSpecifications => {
  const mergedSpecs: ProductSpecifications = {}

  products.forEach(product => {
    const specs = getProductSpecifications(product)
    Object.entries(specs).forEach(([name, values]) => {
      if (Array.isArray(values)) {
        if (!mergedSpecs[name]) {
          mergedSpecs[name] = []
        }
        values.forEach(value => {
          const specArray = mergedSpecs[name] || []
          if (!specArray.includes(value)) {
            specArray.push(value)
            mergedSpecs[name] = specArray
          }
        })
      }
    })
  })

  return mergedSpecs
}

export default {
  getProductSpecifications,
  hasSpecifications,
  formatSpecifications,
  getAllSpecificationNames,
  getSpecificationValues,
  filterProductsBySpecifications,
  mergeSpecifications
}