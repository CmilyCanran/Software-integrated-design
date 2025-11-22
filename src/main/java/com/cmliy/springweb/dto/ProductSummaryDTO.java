package com.cmliy.springweb.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 📦 商品摘要DTO - 列表显示的摘要信息
 *
 * 用于商品列表页面展示，包含精简的商品信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryDTO {
    /**
     * 🔢 商品ID - 数据库主键
     * 数据来源：数据库自增主键
     */
    private Long id;

    /**
     * 🏷️ 商品名称 - 商品的显示名称
     * 数据来源：数据库products表product_name字段
     */
    private String productName;

    /**
     * 🖼️ 主图片URL - 商品的主图片
     * 数据来源：productData中的image_data.main_image字段
     */
    private String mainImage;

    /**
     * 🖼️ 主图片URL - 商品的主图片（别名）
     * 数据来源：productData中的image_data.main_image字段
     */
    private String mainImageUrl;

    /**
     * 📊 销售数量 - 商品销售数量
     * 数据来源：数据库products表sales_count字段
     */
    private Integer salesCount;

    /**
     * 🏷️ 商品类别 - 商品类别
     * 数据来源：productData中的basic_info.category字段
     */
    private String category;

    /**
     * 🏷️ 商品品牌 - 商品品牌
     * 数据来源：productData中的basic_info.brand字段
     */
    private String brand;

    /**
     * 💰 价格 - 商品价格
     * 数据来源：数据库products表price字段，BigDecimal格式
     */
    private BigDecimal price;

    /**
     * 🎁 折扣 - 商品折扣百分比
     * 数据来源：数据库products表discount字段
     */
    private BigDecimal discount;

    /**
     * ✅ 是否可用 - 商品是否上架
     * 数据来源：数据库products表is_available字段
     */
    private Boolean isAvailable;

    /**
     * 💰 格式化价格 - 带货币符号的显示价格
     * 数据来源：后端格式化处理，基于price字段
     */
    private String formattedPrice;

    /**
     * 💰 格式化折扣价 - 带货币符号的折扣后价格
     * 数据来源：后端计算，基于price和discount字段
     */
    private String formattedDiscountedPrice;

    /**
     * ✅ 是否可购买 - 商品是否可购买（上架且有库存）
     * 数据来源：后端根据isAvailable和stockQuantity计算得出
     */
    private Boolean isPurchasable;

    /**
     * 📊 库存状态 - 库存状态描述
     * 数据来源：后端根据stockQuantity计算得出
     */
    private String stockStatus;

    /**
     * 📅 创建时间 - 商品创建时间
     * 数据来源：数据库products表created_at字段
     */
    private LocalDateTime createdAt;
}