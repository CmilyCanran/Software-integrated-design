package com.cmliy.springweb.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 👤 用户信息DTO
 *
 * 用于在API响应中传输用户基本信息，确保前后端字段一致性。
 *
 * Lombok注解优化：
 * - @Data: 自动生成getter、setter、toString、equals、hashCode
 * - @Builder: 支持Builder模式创建对象
 * - @NoArgsConstructor: 无参构造函数
 * - @AllArgsConstructor: 全参构造函数
 * - @EqualsAndHashCode(onlyExplicitlyIncluded = true): 只包含指定字段的equals和hashCode
 * - @ToString(onlyExplicitlyIncluded = true): 只包含指定字段的toString
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class UserDTO {

    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @ToString.Include
    private String username;

    private String email;

    private String role;
}