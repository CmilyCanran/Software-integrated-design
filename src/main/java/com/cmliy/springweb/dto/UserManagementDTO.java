package com.cmliy.springweb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 👤 用户管理DTO
 *
 * 用于用户管理界面的数据传输对象，包含用户的基本信息。
 * 这个DTO用于在管理员用户管理界面展示用户数据。
 *
 * Lombok注解优化：
 * - @Data: 自动生成getter、setter、toString、equals、hashCode
 * - @Builder: 支持Builder模式创建对象
 * - @NoArgsConstructor: 无参构造函数
 * - @AllArgsConstructor: 全参构造函数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserManagementDTO {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 用户角色
     */
    private String role;

    /**
     * 启用状态
     */
    private boolean enabled;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}