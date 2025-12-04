package com.cmliy.springweb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📊 用户统计DTO
 *
 * 用于展示用户管理界面的统计信息。
 * 这个DTO包含了各种用户数量的统计结果。
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
public class UserStatisticsDTO {
    /**
     * 总用户数
     */
    private long totalUsers;

    /**
     * 启用的用户数
     */
    private long enabledUsers;

    /**
     * 禁用的用户数
     */
    private long disabledUsers;

    /**
     * 普通用户数量
     */
    private long userCount;

    /**
     * 商家用户数量
     */
    private long shoperCount;

    /**
     * 管理员数量
     */
    private long adminCount;
}