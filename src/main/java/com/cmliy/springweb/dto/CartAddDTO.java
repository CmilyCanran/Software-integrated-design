package com.cmliy.springweb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//用于用户在商品详情等可以添加购物车的界面添加单个商品入购物车

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartAddDTO {

    private Long userId;
    private Long productId;
    private Integer productQuantity;

    public CartAddDTO(Long uID, Long pID) {
        userId = uID;
        productId = pID;
        productQuantity = 1;
    }
}
