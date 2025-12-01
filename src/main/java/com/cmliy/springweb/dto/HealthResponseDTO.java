package com.cmliy.springweb.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * 💊 健康检查响应DTO
 *
 * 用于健康检查接口的响应数据，包含应用状态信息。
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
public class HealthResponseDTO {

    @ToString.Include
    private String status;

    @ToString.Include
    private String timestamp;

    private String application;

    private String version;
}