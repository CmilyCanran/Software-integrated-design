package com.cmliy.springweb.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmliy.springweb.converter.UserConverter;
import com.cmliy.springweb.dto.UserDTO;
import com.cmliy.springweb.dto.UserManagementDTO;
import com.cmliy.springweb.dto.UserQueryRequestDTO;
import com.cmliy.springweb.dto.UserStatisticsDTO;
import com.cmliy.springweb.exception.BusinessException;
import com.cmliy.springweb.model.User;
import com.cmliy.springweb.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 👤 用户服务 - User Service
 *
 * 处理所有与用户相关的业务逻辑，包括：
 * - 用户注册
 * - 用户信息管理
 * - 用户认证相关操作
 * - 用户状态管理
 *
 * 🚀 设计特点：
 * - 继承BaseService获得统一的基础功能
 * - 使用事务管理确保数据一致性
 * - 完整的错误处理和日志记录
 * - 支持Builder模式创建DTO
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService extends BaseService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;

    /**
     * 📝 用户注册
     *
     * 处理新用户注册，包括：
     * 1. 验证用户名和邮箱的唯一性
     * 2. 创建用户实体并加密密码
     * 3. 设置默认角色和状态
     * 4. 保存用户到数据库
     *
     * @param username 用户名
     * @param email 邮箱地址
     * @param password 明文密码（将被加密存储）
     * @return 创建成功的用户实体
     * @throws BusinessException 当用户名或邮箱已存在时抛出
     */
    public User registerUser(String username, String email, String password) {
        return executeWithLog("用户注册", () -> {
            log.info("开始用户注册流程: username={}", username);

            // 1. 验证用户名唯一性
            validateUnique(userRepository.existsByUsername(username),
                          "用户名", username);

            // 2. 验证邮箱唯一性
            validateUnique(userRepository.existsByEmail(email),
                          "邮箱", email);

            // 3. 创建用户实体
            User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role("USER")      // 默认角色
                .enabled(true)     // 默认启用
                .build();

            // 4. 保存用户
            User savedUser = userRepository.save(user);

            log.info("用户注册成功: username={}, userId={}", username, savedUser.getId());
            return savedUser;

        }, username, email);
    }

    /**
     * 👤 获取当前用户信息
     *
     * 从Spring Security上下文中获取当前认证的用户信息
     *
     * @return 当前用户的DTO对象
     * @throws IllegalArgumentException 当用户未认证或用户不存在时抛出
     */
    @Transactional(readOnly = true)
    public UserDTO getCurrentUserInfo() {
        return executeWithLog("获取当前用户信息", () -> {
            // 1. 从安全上下文中获取认证信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                throw new BusinessException("未认证");
            }

            // 2. 获取用户名
            String username = authentication.getName();

            // 3. 查询用户信息
            User user = validateExists(userRepository.findByUsername(username), "用户", username);

            // 4. 转换为DTO并返回
            return userConverter.toDTO(user);

        });
    }

    /**
     * 🔍 根据ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户DTO对象
     * @throws BusinessException 当用户不存在时抛出
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long userId) {
        return executeWithLog("根据ID获取用户信息", () -> {
            User user = validateExists(userRepository.findById(userId), "用户", userId);
            return userConverter.toDTO(user);
        }, userId);
    }

    /**
     * 🔍 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户DTO对象
     * @throws BusinessException 当用户不存在时抛出
     */
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        return executeWithLog("根据用户名获取用户信息", () -> {
            User user = validateExists(userRepository.findByUsername(username), "用户", username);
            return userConverter.toDTO(user);
        }, username);
    }

    /**
     * 👤 用户登出处理
     *
     * 处理用户登出请求，记录登出日志
     * 由于JWT是无状态的，主要作用是通知前端清除本地存储
     *
     * @param username 登出用户的用户名（可选，用于日志记录）
     */
    public void logoutUser(String username) {
        if (username != null && !username.isEmpty()) {
            log.info("用户登出: {}", username);
        }
        // JWT是无状态的，后端不需要特殊处理
        // 前端负责清除localStorage中的token
    }

    /**
     * 🔒 更新用户密码
     *
     * @param userId 用户ID
     * @param oldPassword 旧密码（用于验证）
     * @param newPassword 新密码
     * @throws BusinessException 当旧密码错误或用户不存在时抛出
     */
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        executeWithLog("更新用户密码", () -> {
            User user = validateExists(userRepository.findById(userId), "用户", userId);

            // 验证旧密码
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new BusinessException("旧密码错误");
            }

            // 更新密码
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            log.info("用户密码更新成功: userId={}", userId);
            return null;
        }, userId);
    }

    /**
     * 📧 更新用户邮箱
     *
     * @param userId 用户ID
     * @param newEmail 新邮箱地址
     * @throws BusinessException 当邮箱已存在或用户不存在时抛出
     */
    public void updateEmail(Long userId, String newEmail) {
        executeWithLog("更新用户邮箱", () -> {
            User user = validateExists(userRepository.findById(userId), "用户", userId);

            // 验证新邮箱的唯一性
            validateUnique(userRepository.existsByEmail(newEmail), "邮箱", newEmail);

            // 更新邮箱
            user.setEmail(newEmail);
            userRepository.save(user);

            log.info("用户邮箱更新成功: userId={}, newEmail={}", userId, newEmail);
            return null;
        }, userId, newEmail);
    }

    /**
     * 🔒 启用/禁用用户账户
     *
     * @param userId 用户ID
     * @param enabled 启用状态（true=启用，false=禁用）
     * @throws BusinessException 当用户不存在时抛出
     */
    public void setUserEnabled(Long userId, boolean enabled) {
        executeWithLog("设置用户启用状态", () -> {
            User user = validateExists(userRepository.findById(userId), "用户", userId);
            user.setEnabled(enabled);
            userRepository.save(user);

            log.info("用户启用状态更新成功: userId={}, enabled={}", userId, enabled);
            return null;
        }, userId, enabled);
    }

    /**
     * 👑 更新用户角色
     *
     * @param userId 用户ID
     * @param role 新角色（USER, SHOPER, ADMIN等）
     * @throws BusinessException 当用户不存在或角色无效时抛出
     */
    public void updateUserRole(Long userId, String role) {
        executeWithLog("更新用户角色", () -> {
            User user = validateExists(userRepository.findById(userId), "用户", userId);

            // 验证角色有效性
            if (!isValidRole(role)) {
                throw new BusinessException("无效的角色: " + role);
            }

            user.setRole(role);
            userRepository.save(user);

            log.info("用户角色更新成功: userId={}, newRole={}", userId, role);
            return null;
        }, userId, role);
    }

    /**
     * ✅ 验证角色有效性
     *
     * @param role 角色字符串
     * @return 是否有效
     */
    private boolean isValidRole(String role) {
        return "USER".equals(role) || "SHOPER".equals(role) || "ADMIN".equals(role);
    }

    /**
     * 🔍 检查用户是否存在
     *
     * @param userId 用户ID
     * @return 用户是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    /**
     * 🔍 检查用户名是否存在
     *
     * @param username 用户名
     * @return 用户名是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 🔍 检查邮箱是否存在
     *
     * @param email 邮箱地址
     * @return 邮箱是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 📄 分页查询用户列表
     *
     * 根据查询条件分页获取用户列表，支持用户名模糊搜索、角色筛选、状态筛选等。
     *
     * @param query 查询条件DTO
     * @return 分页的用户管理DTO
     */
    @Transactional(readOnly = true)
    public Page<UserManagementDTO> getUsers(UserQueryRequestDTO query) {
        return executeWithLog("分页查询用户列表", () -> {
            // 构建排序参数
            Sort sort = Sort.by(query.getSortDirection().equalsIgnoreCase("desc") ?
                          Sort.Direction.DESC : Sort.Direction.ASC,
                          query.getSortBy());

            // 构建分页参数
            Pageable pageable = PageRequest.of(query.getPage(), query.getSize(), sort);

            // 执行分页查询
            Page<User> userPage;

            if (query.getUsername() != null && query.getRole() != null && query.getEnabled() != null) {
                // 三条件组合查询
                userPage = userRepository.findByUsernameContainingIgnoreCaseAndRoleAndEnabled(
                    query.getUsername(), query.getRole(), query.getEnabled(), pageable);
            } else if (query.getUsername() != null && query.getRole() != null) {
                // 用户名 + 角色
                userPage = userRepository.findByUsernameContainingIgnoreCaseAndRole(
                    query.getUsername(), query.getRole(), pageable);
            } else if (query.getUsername() != null && query.getEnabled() != null) {
                // 用户名 + 状态
                userPage = userRepository.findByUsernameContainingIgnoreCaseAndEnabled(
                    query.getUsername(), query.getEnabled(), pageable);
            } else if (query.getRole() != null && query.getEnabled() != null) {
                // 角色 + 状态
                userPage = userRepository.findByRoleAndEnabled(
                    query.getRole(), query.getEnabled(), pageable);
            } else if (query.getUsername() != null) {
                // 仅用户名
                userPage = userRepository.findByUsernameContainingIgnoreCase(
                    query.getUsername(), pageable);
            } else if (query.getRole() != null) {
                // 仅角色
                userPage = userRepository.findByRole(query.getRole(), pageable);
            } else if (query.getEnabled() != null) {
                // 仅状态
                userPage = userRepository.findByEnabled(query.getEnabled(), pageable);
            } else {
                // 无条件查询所有
                userPage = userRepository.findAll(pageable);
            }

            // 转换为UserManagementDTO
            return userPage.map(user -> UserManagementDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build());
        }, query);
    }

    /**
     * 📊 获取用户统计信息
     *
     * 统计所有用户的数量信息，包括总用户数、启用/禁用用户数、各角色用户数。
     *
     * @return 用户统计DTO
     */
    @Transactional(readOnly = true)
    public UserStatisticsDTO getUserStatistics() {
        return executeWithLog("获取用户统计信息", () -> {
            // 获取所有统计信息
            long totalUsers = userRepository.count();
            long enabledUsers = userRepository.countByEnabledTrue();
            long disabledUsers = userRepository.countByEnabledFalse();
            long userCount = userRepository.countByRole("USER");
            long shoperCount = userRepository.countByRole("SHOPER");
            long adminCount = userRepository.countByRole("ADMIN");

            // 构建统计DTO
            return UserStatisticsDTO.builder()
                .totalUsers(totalUsers)
                .enabledUsers(enabledUsers)
                .disabledUsers(disabledUsers)
                .userCount(userCount)
                .shoperCount(shoperCount)
                .adminCount(adminCount)
                .build();
        });
    }
}