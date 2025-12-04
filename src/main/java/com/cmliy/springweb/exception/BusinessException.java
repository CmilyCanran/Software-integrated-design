package com.cmliy.springweb.exception;

/**
 * 💼 业务异常类
 *
 * 用于处理业务逻辑中的异常情况，如用户输入错误、数据验证失败等。
 * 继承自RuntimeException，可以被Spring的事务管理机制正确处理。
 */
public class BusinessException extends RuntimeException {

    /**
     * 构造业务异常
     * @param message 异常消息
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * 构造业务异常
     * @param message 异常消息
     * @param cause 异常原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造业务异常
     * @param cause 异常原因
     */
    public BusinessException(Throwable cause) {
        super(cause);
    }
}