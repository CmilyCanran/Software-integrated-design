package com.cmliy.springweb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.cmliy.springweb.common.ApiResponse;
import com.cmliy.springweb.repository.UserRepository;
import com.cmliy.springweb.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🏗️ 基础控制器类
 *
 * 提供所有控制器的通用功能，包括：
 * - 用户认证信息获取
 * - 通用工具方法
 * - 统一的日志记录
 *
 * 使用继承此基类来消除控制器间的代码重复

 */
@Slf4j
@RequiredArgsConstructor
public abstract class BaseController {

    protected final UserRepository userRepository;
    protected final JwtUtil jwtUtil;

    /**
     * 👤 获取当前认证用户ID
     *
     * 从Spring Security上下文中获取当前用户的ID。
     * 优先从JWT claims中获取，失败时从数据库查询。
     *
     * @return 当前用户ID
     * @throws RuntimeException 当用户未认证或无法获取用户ID时
     */
    protected Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未认证");
        }

        // 方案1：优先从JWT claims中获取用户ID（性能更好）
        try {
            // 从认证信息中获取JWT token
            if (authentication.getCredentials() instanceof String) {
                String token = (String) authentication.getCredentials();
                // 确保token包含Bearer前缀时去除
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }

                // 使用JwtUtil提取用户ID
                Long userId = jwtUtil.extractUserId(token);
                if (userId != null) {
                    return userId;
                }
            }
        } catch (Exception e) {
            // JWT claims提取失败，使用备用方案
            log.debug("从JWT claims获取用户ID失败，使用备用方案: {}", e.getMessage());
        }

        // 方案2：备用方案 - 从用户名查询用户ID
        String username = authentication.getName();
        try {
            return userRepository.findByUsername(username)
                    .map(user -> user.getId())
                    .orElseThrow(() -> new RuntimeException("用户不存在: " + username));
        } catch (Exception e) {
            throw new RuntimeException("无法获取用户ID: " + e.getMessage());
        }
    }

    /**
     * 👤 获取当前认证用户名
     *
     * 从Spring Security上下文中获取当前用户的用户名。
     * 如果用户未认证，返回"anonymous"。
     *
     * @return 当前用户名，未认证时返回"anonymous"
     */
    protected String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "anonymous";
        }
        return authentication.getName();
    }

    /**
     * 🔍 检查当前用户是否已认证
     *
     * @return true如果用户已认证，false否则
     */
    protected boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    /**
     * 📝 记录操作日志
     *
     * 记录用户操作的统一日志格式，便于审计和调试。
     *
     * @param operation 操作名称
     * @param details 操作详情
     */
    protected void logOperation(String operation, String details) {
        log.info("操作: {} | 用户: {} | 详情: {}", operation, getCurrentUsername(), details);
    }

    /**
     * 📝 记录操作日志（带目标ID）
     *
     * 记录涉及特定资源ID的操作日志。
     *
     * @param operation 操作名称
     * @param targetId 目标资源ID
     * @param details 操作详情
     */
    protected void logOperation(String operation, Long targetId, String details) {
        log.info("操作: {} | 用户: {} | 目标: {} | 详情: {}",
                operation, getCurrentUsername(), targetId, details);
    }

    // ===== 🚀 统一API响应方法 =====
    // 这些方法大大简化了控制器的响应处理代码

    /**
     * ✅ 成功响应（默认状态码200）
     *
     * @param data 响应数据
     * @param message 成功消息
     * @param <T> 数据类型
     * @return 统一成功响应
     */
    protected <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(ApiResponse.success(data, message));
    }

    /**
     * ✅ 成功响应（带自定义状态码）
     *
     * @param status HTTP状态码
     * @param data 响应数据
     * @param message 成功消息
     * @param <T> 数据类型
     * @return 统一成功响应
     */
    protected <T> ResponseEntity<ApiResponse<T>> success(int status, T data, String message) {
        return ResponseEntity.status(status).body(ApiResponse.success(data, message));
    }

    /**
     * ✅ 成功响应（无数据）
     *
     * @param message 成功消息
     * @return 统一成功响应
     */
    protected ResponseEntity<ApiResponse<Void>> success(String message) {
        return ResponseEntity.ok(ApiResponse.success(null, message));
    }

    /**
     * ❌ 错误响应（默认状态码400）
     *
     * @param message 错误消息
     * @return 统一错误响应
     */
    protected <T> ResponseEntity<ApiResponse<T>> error(String message) {
        return ResponseEntity.badRequest().body(ApiResponse.<T>error(message, 400));
    }

    /**
     * ❌ 错误响应（带自定义状态码）
     *
     * @param status HTTP状态码
     * @param message 错误消息
     * @return 统一错误响应
     */
    protected <T> ResponseEntity<ApiResponse<T>> error(int status, String message) {
        return ResponseEntity.status(status).body(ApiResponse.<T>error(message, status));
    }

    /**
     * ❌ 错误响应（指定HTTP状态）
     *
     * @param status HTTP状态枚举
     * @param message 错误消息
     * @return 统一错误响应
     */
    protected <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ApiResponse.<T>error(message, status.value()));
    }

    /**
     * 🔄 执行带日志的操作（无参数）
     *
     * 执行操作并记录日志，如果发生异常则记录错误日志。
     *
     * @param operation 操作名称
     * @param supplier 操作执行函数
     * @param <T> 返回类型
     * @return 操作结果
     */
    protected <T> T executeWithLog(String operation, java.util.function.Supplier<T> supplier) {
        try {
            logOperation(operation, "开始执行");
            T result = supplier.get();
            logOperation(operation, "执行成功");
            return result;
        } catch (Exception e) {
            log.error("操作执行失败: {} | 错误: {}", operation, e.getMessage(), e);
            throw new RuntimeException("操作失败: " + e.getMessage(), e);
        }
    }

    /**
     * 🔄 执行带日志的操作（带参数）
     *
     * 执行操作并记录日志，如果发生异常则记录错误日志。
     *
     * @param operation 操作名称
     * @param supplier 操作执行函数
     * @param params 操作参数（用于日志记录）
     * @param <T> 返回类型
     * @return 操作结果
     */
    protected <T> T executeWithLog(String operation, java.util.function.Supplier<T> supplier, Object... params) {
        try {
            logOperation(operation, "开始执行 | 参数: " + java.util.Arrays.toString(params));
            T result = supplier.get();
            logOperation(operation, "执行成功");
            return result;
        } catch (Exception e) {
            log.error("操作执行失败: {} | 参数: {} | 错误: {}", operation, java.util.Arrays.toString(params), e.getMessage(), e);
            throw new RuntimeException("操作失败: " + e.getMessage(), e);
        }
    }
}