package com.cmliy.springweb.exception;

/**
 * 🖼️ 图片上传异常类 - Image Upload Exception
 *
 * 这个异常类用于处理图片上传过程中的各种错误情况。
 * 继承自RuntimeException，是一个非受检异常。
 *

 */
public class ImageUploadException extends RuntimeException {

    /**
     * 🔧 构造函数 - 带详细消息
     *
     * @param message 异常详细消息
     */
    public ImageUploadException(String message) {
        super(message);
    }

    /**
     * 🔧 构造函数 - 带详细消息和原因
     *
     * @param message 异常详细消息
     * @param cause 异常原因
     */
    public ImageUploadException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 🔧 构造函数 - 带原因
     *
     * @param cause 异常原因
     */
    public ImageUploadException(Throwable cause) {
        super(cause);
    }
}