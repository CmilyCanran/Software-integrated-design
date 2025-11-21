package com.cmliy.springweb.dto;

/**
 * 📝 注册响应DTO
 *
 * 用于注册接口的响应数据，包含时间戳信息。
 */
public class RegisterResponseDTO {
    private String timestamp;

    // 无参构造函数
    public RegisterResponseDTO() {}

    // 全参构造函数
    public RegisterResponseDTO(String timestamp) {
        this.timestamp = timestamp;
    }

    // Getter和Setter方法
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "RegisterResponseDTO{" +
                "timestamp='" + timestamp + '\'' +
                '}';
    }
}