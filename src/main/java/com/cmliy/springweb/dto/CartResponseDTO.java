package com.cmliy.springweb.dto;

import java.util.Map;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//用于向前端购物车打开的时候传递初始的购物车内容
//以及购物车界面关闭时向后端更新购物车信息
//只传递商品数量map和用户id 其余的计算由前端完成
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {
    private Long userId;
    private Map<Long, Integer> productQuantities;
    private ArrayList<ProductResponseDTO> products;
}
