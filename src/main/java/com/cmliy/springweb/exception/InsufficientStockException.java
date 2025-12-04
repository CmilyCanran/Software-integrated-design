package com.cmliy.springweb.exception;

public class InsufficientStockException extends RuntimeException {

    /**
     * 构造方法
     *
     * @param productId 商品ID
     * @param requested 用户请求的数量
     * @param available 实际可用库存
     */
    public InsufficientStockException(Long id, Throwable cause){
        super("商品库存不足,请求用户："+id,cause);
    }
    public InsufficientStockException(Throwable cause){
        super("商品库存不足",cause);
    }
}

