// package: Java包声明，用于组织类和避免命名冲突
package com.cmliy.springweb.common;

import java.time.LocalDateTime;

/**
 * 📦 统一API响应包装类
 *
 * 为所有API端点提供标准化的响应格式，确保前后端数据交互的一致性。
 * 支持成功和错误响应，包含业务状态码、消息和数据。
 *
 * 响应格式：
 * {
 *   "success": true/false,     // 业务操作是否成功
 *   "data": {...},            // 实际响应数据（成功时）
 *   "message": "描述信息",      // 操作结果描述
 *   "code": 200              // 业务状态码（向后兼容）
 *   "timestamp": "2025-11-21T13:20:00"  // 响应时间戳
 * }
 *
 * @param <T> 响应数据的类型
 */
public class ApiResponse<T> {

    // ===== 响应字段 =====

    /**
     * ✅ 业务操作成功标识
     * true表示操作成功，false表示操作失败
     */
    private boolean success;

    /**
     * 📊 实际响应数据
     * 包含API操作的具体结果数据
     */
    private T data;

    /**
     * 📝 操作结果描述信息
     * 提供人类可读的操作结果说明
     */
    private String message;

    /**
     * 🔢 业务状态码（向后兼容字段）
     * 与前端现有代码保持兼容性
     */
    private int code;

    /**
     * ⏰ 响应时间戳
     * 记录响应生成的时间
     */
    private LocalDateTime timestamp;

    // ===== 构造函数 =====

    /**
     * 🏗️ 默认构造函数
     * 创建空的API响应对象
     */
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 🏗️ 完整构造函数
     *
     * @param success 业务操作是否成功
     * @param data 实际响应数据
     * @param message 操作结果描述
     * @param code 业务状态码
     */
    public ApiResponse(boolean success, T data, String message, int code) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.code = code;
        this.timestamp = LocalDateTime.now();
    }

    // ===== 便捷工厂方法 =====

    /**
     * ✅ 创建成功响应
     *
     * @param data 实际响应数据
     * @param message 成功消息
     * @param <T> 数据类型
     * @return 成功的API响应对象
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, 200);
    }

    /**
     * ✅ 创建成功响应（无数据）
     *
     * @param message 成功消息
     * @param <T> 数据类型
     * @return 成功的API响应对象
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, null, message, 200);
    }

    /**
     * ❌ 创建错误响应
     *
     * @param message 错误消息
     * @param code 业务状态码
     * @param <T> 数据类型
     * @return 错误的API响应对象
     */
    public static <T> ApiResponse<T> error(String message, int code) {
        return new ApiResponse<>(false, null, message, code);
    }

    /**
     * ❌ 创建错误响应（带详细数据）
     *
     * @param message 错误消息
     * @param code 业务状态码
     * @param errorDetails 错误详情数据
     * @param <T> 数据类型
     * @return 错误的API响应对象
     */
    public static <T> ApiResponse<T> errorWithData(String message, int code, T errorDetails) {
        ApiResponse<T> response = new ApiResponse<>(false, errorDetails, message, code);
        return response;
    }

    // ===== Getter和Setter方法 =====

    /**
     * 获取业务操作成功标识
     * @return true表示成功，false表示失败
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 设置业务操作成功标识
     * @param success true表示成功，false表示失败
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * 获取实际响应数据
     * @return 实际响应数据
     */
    public T getData() {
        return data;
    }

    /**
     * 设置实际响应数据
     * @param data 实际响应数据
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * 获取操作结果描述信息
     * @return 操作结果描述
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置操作结果描述信息
     * @param message 操作结果描述
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取业务状态码
     * @return 业务状态码
     */
    public int getCode() {
        return code;
    }

    /**
     * 设置业务状态码
     * @param code 业务状态码
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 获取响应时间戳
     * @return 响应时间戳
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * 设置响应时间戳
     * @param timestamp 响应时间戳
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // ===== 对象表示方法 =====

    /**
     * 📝 获取对象的字符串表示
     * @return 对象的字符串表示
     */
    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", code=" + code +
                ", timestamp=" + timestamp +
                '}';
    }
}