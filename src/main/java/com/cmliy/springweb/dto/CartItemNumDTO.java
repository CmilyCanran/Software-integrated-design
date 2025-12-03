package com.cmliy.springweb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//用于前端Header组件的购物车按钮角标的购物车商品种类数量展示
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemNumDTO {
    private Long userId;
    private int num;
}
