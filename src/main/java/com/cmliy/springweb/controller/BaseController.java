package com.cmliy.springweb.controller;

import com.cmliy.springweb.repository.UserRepository;
import com.cmliy.springweb.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 🏗️ 基础控制器类
 *
 * 提供所有控制器的通用功能，包括：
 * - 用户认证信息获取
 * - 通用工具方法
 * - 统一的日志记录
 *
 * 使用继承此基类来消除控制器间的代码重复
 *
 * @author Claude
 * @since 2025-11-30
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
}