// ============================================================================
// 类型转换工具函数 - 统一处理string/number类型转换
// ============================================================================

/**
 * 将字符串转换为数字
 * @param value - 要转换的值
 * @returns 转换后的数字，转换失败时返回0
 */
export const toNumber = (value: string | number): number => {
  if (typeof value === 'number') return value
  const num = Number(value)
  return isNaN(num) ? 0 : num
}

/**
 * 将数字转换为字符串
 * @param value - 要转换的值
 * @returns 转换后的字符串
 */
export const toString = (value: string | number): string => {
  return String(value)
}

/**
 * 将对象的所有键从字符串转换为数字
 * @param obj - 包含字符串键的对象
 * @returns 键为数字的新对象
 */
export const convertObjectKeysToNumbers = <T>(obj: Record<string, T>): Record<number, T> => {
  const result: Record<number, T> = {}
  for (const [key, value] of Object.entries(obj)) {
    const numKey = toNumber(key)
    result[numKey] = value
  }
  return result
}

/**
 * 将对象的所有键从数字转换为字符串
 * @param obj - 包含数字键的对象
 * @returns 键为字符串的新对象
 */
export const convertObjectKeysToStrings = <T>(obj: Record<number, T>): Record<string, T> => {
  const result: Record<string, T> = {}
  for (const [key, value] of Object.entries(obj)) {
    result[key] = value
  }
  return result
}