// ============================================================================
// JWT工具函数 - 统一处理JWT token的验证和解析
// ============================================================================

/**
 * 解析JWT token的payload部分
 * @param token - JWT token字符串
 * @returns 解析后的payload对象，解析失败时返回null
 */
export const parseJwtPayload = (token: string): any | null => {
  try {
    // JWT token分为三部分：header.payload.signature，用点号分隔
    const parts = token.split('.')

    // 验证JWT格式是否正确
    if (parts.length !== 3) {
      console.warn('Invalid JWT format: token must have exactly 3 parts separated by dots')
      return null
    }

    // 解码payload部分（第二部分）
    // JWT使用Base64Url编码，需要先替换特殊字符再进行Base64解码
    const base64 = parts[1]
      .replace(/-/g, '+')
      .replace(/_/g, '/')

    // 添加必要的填充字符
    let paddedBase64 = base64
    const pad = paddedBase64.length % 4
    if (pad) {
      if (pad === 2) paddedBase64 += '=='
      else if (pad === 3) paddedBase64 += '='
    }

    // 解码并解析JSON
    const jsonPayload = decodeURIComponent(
      atob(paddedBase64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    )

    return JSON.parse(jsonPayload)
  } catch (error) {
    console.error('Failed to parse JWT token:', error)
    return null
  }
}

/**
 * 验证JWT token是否有效（未过期）
 * @param token - JWT token字符串
 * @returns token是否有效
 */
export const isJwtTokenValid = (token: string): boolean => {
  if (!token) {
    return false
  }

  try {
    const payload = parseJwtPayload(token)
    if (!payload) {
      return false
    }

    // 检查exp字段是否存在且未过期
    const currentTime = Math.floor(Date.now() / 1000) // 当前时间戳（秒）
    if (payload.exp && payload.exp < currentTime) {
      console.warn('JWT token has expired')
      return false
    }

    return true
  } catch (error) {
    console.error('Error validating JWT token:', error)
    return false
  }
}

/**
 * 检查JWT token是否即将过期
 * @param token - JWT token字符串
 * @param thresholdMinutes - 提前多少分钟认为即将过期（默认5分钟）
 * @returns token是否即将过期
 */
export const isJwtTokenExpiringSoon = (token: string, thresholdMinutes: number = 5): boolean => {
  if (!token) {
    return true // 没有token认为即将过期
  }

  try {
    const payload = parseJwtPayload(token)
    if (!payload || !payload.exp) {
      return true // 无法解析或没有过期时间认为即将过期
    }

    const currentTime = Math.floor(Date.now() / 1000) // 当前时间戳（秒）
    const expTime = payload.exp
    const thresholdSeconds = thresholdMinutes * 60

    return expTime - currentTime < thresholdSeconds
  } catch (error) {
    console.error('Error checking JWT expiration:', error)
    return true // 出错时认为即将过期
  }
}

/**
 * 获取JWT token的过期时间
 * @param token - JWT token字符串
 * @returns 过期时间的Date对象，获取失败时返回null
 */
export const getJwtExpirationDate = (token: string): Date | null => {
  if (!token) {
    return null
  }

  try {
    const payload = parseJwtPayload(token)
    if (!payload || !payload.exp) {
      return null
    }

    // 将秒时间戳转换为毫秒
    return new Date(payload.exp * 1000)
  } catch (error) {
    console.error('Error getting JWT expiration date:', error)
    return null
  }
}