package com.cmliy.springweb.dto;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 📦 商品响应DTO - 通用商品查询响应
 *
 * 用于商品列表、搜索等通用查询场景
 * 仅包含前端需要的基本信息，避免暴露敏感数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
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
     * 📝 商品描述 - 商品详细介绍
     * 数据来源：数据库products表description字段
     */
    private String description;

    /**
     * 💰 价格 - 商品价格
     * 数据来源：数据库products表price字段，BigDecimal格式
     */
    private BigDecimal price;

    /**
     * 📈 销量 - 商品已售数量
     * 数据来源：数据库products表sales_count字段
     */
    private Integer salesCount;

    /**
     * 🎁 折扣 - 商品折扣百分比
     * 数据来源：数据库products表discount字段
     */
    private BigDecimal discount;

    /**
     * 📦 库存 - 商品库存数量
     * 数据来源：数据库products表stock_quantity字段
     */
    private Integer stockQuantity;

    /**
     * ✅ 是否可用 - 商品是否上架
     * 数据来源：数据库products表is_available字段
     */
    private Boolean isAvailable;

    /**
     * 👤 创建人ID - 商品创建者ID
     * 数据来源：数据库products表creator_id字段
     */
    private Long creatorId;

    /**
     * 👤 创建人用户名 - 商品创建者用户名
     * 数据来源：关联查询users表的username字段
     */
    private String creatorUsername;

    /**
     * 🖼️ 主图片URL - 商品主要展示图片
     * 数据来源：Product实体的getMainImage()方法
     */
    private String mainImageUrl;

    
    /**
     * 📋 商品规格 - 商品规格属性（颜色、尺寸等）
     * 数据来源：Product实体的getAllSpecifications()方法
     */
    private Map<String, Object> specifications;

    /**
     * 🏷️ 商品分类 - 商品所属分类
     * 数据来源：Product实体的getCategory()方法
     */
    private String category;

    /**
     * 🏢 商品品牌 - 商品品牌
     * 数据来源：Product实体的getBrand()方法
     */
    private String brand;

    /**
     * 🎨 商品颜色 - 商品颜色
     * 数据来源：Product实体的getColor()方法
     */
    private String color;

    /**
     * 📏 商品尺寸 - 商品尺寸
     * 数据来源：Product实体的getSize()方法
     */
    private String size;

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
     * 📊 库存状态 - 库存状态描述
     * 数据来源：后端根据stockQuantity计算得出
     */
    private String stockStatus;

    /**
     * 📅 创建时间 - 商品创建时间
     * 数据来源：数据库products表created_at字段
     */
    private LocalDateTime createdAt;

    /**
     * 🔄 更新时间 - 商品最后更新时间
     * 数据来源：数据库products表updated_at字段
     */
    private LocalDateTime updatedAt;
}