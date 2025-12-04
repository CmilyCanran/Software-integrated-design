package com.cmliy.springweb.exception;

public class ProductNotFoundException extends RuntimeException{

    /**
     * 构造方法 - 根据商品ID
     *
     * @param productId 不存在的商品ID
     */
    public ProductNotFoundException(Long productId) {
        super("商品不存在: " + productId);
    }

    /**
     * 构造方法 - 带商品名称
     *
     * @param productId 不存在的商品ID
     * @param productName 尝试查找的商品名称
     */
    public ProductNotFoundException(Long productId, String productName) {
        super("商品不存在: ID=" + productId + ", 名称=" + productName);
    }
}
