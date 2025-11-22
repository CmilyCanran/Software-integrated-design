package com.cmliy.springweb.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 📦 商品查询请求DTO - 商品查询请求参数
 *
 * 用于接收前端商品查询的请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductQueryRequestDTO {
    /**
     * 📄 页码 - 当前页码（从1开始）
     * 前端数据来源：分页组件的当前页码
     */
    private Integer page;

    /**
     * 📏 每页数量 - 每页显示的商品数量
     * 前端数据来源：分页组件的每页数量设置
     */
    private Integer pageSize;

    /**
     * 📊 排序字段 - 排序依据的字段
     * 前端数据来源：排序选择器的值
     */
    private String sort;

    /**
     * 🔽 排序顺序 - 排序顺序（asc/desc）
     * 前端数据来源：排序选择器的顺序设置
     */
    private String order;

    /**
     * 💰 最低价格 - 最低价格筛选
     * 前端数据来源：价格筛选器的最低值
     */
    private BigDecimal minPrice;

    /**
     * 💰 最高价格 - 最高价格筛选
     * 前端数据来源：价格筛选器的最高值
     */
    private BigDecimal maxPrice;

    /**
     * 📦 最低库存 - 最低库存筛选
     * 前端数据来源：库存筛选器的最低值
     */
    private Integer minStock;

    /**
     * 📦 最高库存 - 最高库存筛选
     * 前端数据来源：库存筛选器的最高值
     */
    private Integer maxStock;

    /**
     * ✅ 是否上架 - 是否上架筛选
     * 前端数据来源：上架状态筛选器
     */
    private Boolean isAvailable;

    /**
     * 📦 是否有库存 - 是否有库存筛选
     * 前端数据来源：库存状态筛选器
     */
    private Boolean hasStock;

    /**
     * 👤 创建人ID - 创建人筛选
     * 前端数据来源：创建人筛选器
     */
    private Long creatorId;

    /**
     * 🔍 搜索关键词 - 搜索关键词
     * 前端数据来源：搜索框的输入内容
     */
    private String keyword;

    /**
     * 🏷️ 商品类别 - 商品类别筛选
     * 前端数据来源：规格筛选器中的类别选择
     */
    private String category;

    /**
     * 🏷️ 商品品牌 - 商品品牌筛选
     * 前端数据来源：规格筛选器中的品牌选择
     */
    private String brand;

    /**
     * 🎨 商品颜色 - 商品颜色筛选
     * 前端数据来源：规格筛选器中的颜色选择
     */
    private String color;

    /**
     * 📏 商品尺寸 - 商品尺寸筛选
     * 前端数据来源：规格筛选器中的尺寸选择
     */
    private String size;
}