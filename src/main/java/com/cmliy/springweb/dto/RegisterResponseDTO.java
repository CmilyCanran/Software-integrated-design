package com.cmliy.springweb.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * 📝 注册响应DTO
 *
 * 用于注册接口的响应数据，包含时间戳信息。
 *
 * Lombok注解优化：
 * - @Data: 自动生成getter、setter、toString、equals、hashCode
 * - @Builder: 支持Builder模式创建对象
 * - @NoArgsConstructor: 无参构造函数
 * - @AllArgsConstructor: 全参构造函数
 * - @ToString(onlyExplicitlyIncluded = true): 只包含指定字段的toString
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class RegisterResponseDTO {

    @ToString.Include
    private String timestamp;
}