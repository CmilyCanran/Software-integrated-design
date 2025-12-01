package com.cmliy.springweb.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

/**
 * 📦 商品更新请求DTO - 商品更新请求参数
 *
 * 用于接收前端更新商品的请求数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequestDTO {
    /**
     * 🏷️ 商品名称 - 商品的显示名称
     * 前端数据来源：商品表单中的名称输入框
     */
    @Size(max = 50, message = "商品名称长度不能超过50个字符")
    private String productName;

    /**
     * 📝 商品描述 - 商品详细介绍
     * 前端数据来源：商品表单中的描述文本域
     */
    private String description;

    /**
     * 💰 价格 - 商品价格
     * 前端数据来源：商品表单中的价格输入框
     */
    @DecimalMin(value = "0.0", message = "价格不能小于0")
    private BigDecimal price;

    /**
     * 🎁 折扣 - 商品折扣百分比
     * 前端数据来源：商品表单中的折扣输入框
     */
    @DecimalMin(value = "0.0", message = "折扣不能小于0")
    @DecimalMax(value = "100.0", message = "折扣不能大于100")
    private BigDecimal discount;

    /**
     * 📦 库存 - 商品库存数量
     * 前端数据来源：商品表单中的库存输入框
     */
    @Min(value = 0, message = "库存不能小于0")
    private Integer stockQuantity;

    /**
     * ✅ 是否可用 - 商品是否上架
     * 前端数据来源：商品表单中的上架状态开关
     */
    private Boolean isAvailable;

    /**
     * 🗂️ 商品数据 - 扩展属性存储
     * 前端数据来源：商品表单中的规格、图片等扩展数据
     */
    private Map<String, Object> productData;

    /**
     * 🖼️ 主图片URL - 商品的主图片
     * 前端数据来源：图片上传组件
     */
    private String mainImageUrl;

    
    /**
     * 📋 商品规格 - 商品规格信息
     * 前端数据来源：规格表单
     */
    private Map<String, Object> specifications;

    /**
     * 🏷️ 商品类别 - 商品类别
     * 前端数据来源：类别选择器
     */
    private String category;

    /**
     * 🏷️ 商品品牌 - 商品品牌
     * 前端数据来源：品牌输入框
     */
    private String brand;

    /**
     * 🎨 商品颜色 - 商品颜色
     * 前端数据来源：颜色选择器
     */
    private String color;

    /**
     * 📏 商品尺寸 - 商品尺寸
     * 前端数据来源：尺寸输入框
     */
    private String size;

    /**
     * 📊 扩展属性 - 其他扩展属性
     * 前端数据来源：扩展属性表单
     */
    private Map<String, Object> extendedAttributes;
}