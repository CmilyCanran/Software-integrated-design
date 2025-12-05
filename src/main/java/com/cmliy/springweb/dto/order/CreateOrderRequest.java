package com.cmliy.springweb.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📋 创建订单请求DTO
 *
 * 用于创建订单的请求参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    /**
     * 📦 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * 🔢 购买数量
     */
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量不能小于1")
    private Integer quantity;

    /**
     * 📄 订单备注
     */
    private String remarks;

    /**
     * 📍 收货地址
     */
    private String shippingAddress;

    /**
     * 📞 联系电话
     */
    private String contactPhone;
}