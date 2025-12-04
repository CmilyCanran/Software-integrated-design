package com.cmliy.springweb.exception;

public class CartNotFoundException extends CartException{
    public CartNotFoundException(Long userId) {
        super("购物车不存在，用户ID: " + userId);
    }
        public CartNotFoundException(Long userId,Throwable cause) {
        super("购物车不存在，用户ID: " + userId,cause);
    }
}
