package com.cmliy.springweb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 🔍 用户查询请求DTO
 *
 * 用于用户管理界面的查询请求参数，支持多种筛选条件。
 * 这个DTO封装了前端传递的查询参数，包括分页信息。
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
public class UserQueryRequestDTO {
    /**
     * 用户名关键词（模糊搜索）
     */
    private String username;

    /**
     * 邮箱关键词（模糊搜索）
     */
    private String email;

    /**
     * 用户角色（精确匹配）
     */
    private String role;

    /**
     * 启用状态（精确匹配）
     */
    private Boolean enabled;

    /**
     * 页码（从0开始）
     */
    private int page = 0;

    /**
     * 每页大小
     */
    private int size = 10;

    /**
     * 排序字段
     */
    private String sortBy = "createdAt";

    /**
     * 排序方向（asc/desc）
     */
    private String sortDirection = "desc";
}