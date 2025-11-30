// package: Java包声明，用于组织类和避免命名冲突
package com.cmliy.springweb.exception;

import com.cmliy.springweb.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 🌐 全局异常处理器
 *
 * 统一处理应用程序中抛出的异常，确保所有API端点返回一致的错误响应格式。
 * 通过@ControllerAdvice注解，该类会拦截所有控制器中未处理的异常。
 *
 * 异常处理原则：
 * 1. 业务异常返回4xx状态码
 * 2. 系统异常返回5xx状态码
 * 3. 所有异常都返回标准化的ApiResponse格式
 * 4. 提供清晰的错误信息便于前端处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 📝 日志记录器
     * 使用SLF4J进行统一的日志记录，便于调试和监控
     */
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 🔐 处理认证相关异常
     *
     * 拦截Spring Security认证异常，返回标准的认证失败响应。
     * 包括用户名密码错误、令牌无效等情况。
     *
     * @param e 认证异常
     * @return 认证失败的标准化响应
     */
    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class, AuthenticationServiceException.class})
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleAuthException(Exception e) {
        // 📝 记录认证失败日志
        logger.warn("认证失败: {}", e.getMessage());

        // 🛠️ 构建认证失败的响应数据
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("error", "AUTHENTICATION_FAILED");
        errorData.put("timestamp", java.time.LocalDateTime.now().toString());

        // 📤 返回401未授权状态的标准化响应
        ApiResponse<Map<String, Object>> response = ApiResponse.error("认证失败，请先登录", 401);
        response.setData(errorData);

        return ResponseEntity.status(401).body(response);
    }

    /**
     * 📦 处理参数验证异常
     *
     * 拦截请求参数验证失败的异常，返回标准的参数错误响应。
     * 包括@Valid注解验证失败等情况。
     *
     * @param e 参数验证异常
     * @return 参数错误的标准化响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(IllegalArgumentException e) {
        // 📝 记录参数验证失败日志
        logger.warn("参数验证失败: {}", e.getMessage());

        // 📤 返回400错误请求状态的标准化响应
        return ResponseEntity.status(400).body(
            ApiResponse.error("参数验证失败: " + e.getMessage(), 400)
        );
    }

    /**
     * 🎯 处理通用异常
     *
     * 拦截所有未被特定处理器处理的异常，返回标准的服务器错误响应。
     * 这是异常处理链的最后一环。
     *
     * @param e 通用异常
     * @return 服务器错误的标准化响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleGenericException(Exception e) {
        // 📝 记录服务器错误日志 - 使用ERROR级别记录完整异常信息
        logger.error("服务器内部错误: {}", e.getMessage(), e);

        // 🛠️ 构建错误详情数据
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("exceptionType", e.getClass().getSimpleName());
        errorDetails.put("timestamp", java.time.LocalDateTime.now().toString());

        // 📤 返回500服务器内部错误状态的标准化响应
        ApiResponse<Map<String, Object>> response = ApiResponse.error("服务器内部错误，请稍后重试", 500);
        response.setData(errorDetails);

        return ResponseEntity.status(500).body(response);
    }
}