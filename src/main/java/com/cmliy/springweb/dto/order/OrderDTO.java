package com.cmliy.springweb.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📋 订单数据传输对象
 *
 * 用于前后端数据传输的订单对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    /**
     * 🆔 订单ID
     */
    private Long id;

    /**
     * 👤 用户ID
     */
    private Long userId;

    /**
     * 👤 用户名
     */
    private String username;

    /**
     * 📦 商品ID
     */
    private Long productId;

    /**
     * 📦 商品名称
     */
    private String productName;

    /**
     * 📦 商品描述
     */
    private String productDescription;

    /**
     * 📦 商品图片
     */
    private String productImage;

    /**
     * 🏪 商家ID
     */
    private Long sellerId;

    /**
     * 🏪 商家名称
     */
    private String sellerName;

    /**
     * 🔢 购买数量
     */
    private Integer quantity;

    /**
     * 💰 下单时单价
     */
    private BigDecimal unitPrice;

    /**
     * 💵 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 📊 订单状态
     */
    private String status;

    /**
     * 📊 订单状态描述
     */
    private String statusDescription;

    /**
     * ⏰ 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 🔄 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 📄 订单备注
     */
    private String remarks;

    /**
     * 🚚 物流信息
     */
    private String shippingInfo;

    /**
     * 💳 支付信息
     */
    private String paymentInfo;

    /**
     * 📍 收货地址
     */
    private String shippingAddress;

    /**
     * 📞 联系电话
     */
    private String contactPhone;
}