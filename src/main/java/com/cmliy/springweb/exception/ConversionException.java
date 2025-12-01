package com.cmliy.springweb.exception;

/**
 * DTO转换异常类
 * 用于处理Entity与DTO转换过程中出现的异常

 */
public class ConversionException extends RuntimeException {

    /**
     * 构造转换异常
     *
     * @param message 异常消息
     */
    public ConversionException(String message) {
        super(message);
    }

    /**
     * 构造转换异常
     *
     * @param message 异常消息
     * @param cause 原因异常
     */
    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造转换异常
     *
     * @param cause 原因异常
     */
    public ConversionException(Throwable cause) {
        super(cause);
    }
}