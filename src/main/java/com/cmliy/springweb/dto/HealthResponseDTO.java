package com.cmliy.springweb.dto;

/**
 * 💊 健康检查响应DTO
 *
 * 用于健康检查接口的响应数据，包含应用状态信息。
 */
public class HealthResponseDTO {
    private String status;
    private String timestamp;
    private String application;
    private String version;

    // 无参构造函数
    public HealthResponseDTO() {}

    // 全参构造函数
    public HealthResponseDTO(String status, String timestamp, String application, String version) {
        this.status = status;
        this.timestamp = timestamp;
        this.application = application;
        this.version = version;
    }

    // Getter和Setter方法
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "HealthResponseDTO{" +
                "status='" + status + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", application='" + application + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}