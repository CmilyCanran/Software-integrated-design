package com.cmliy.springweb.model;

import com.cmliy.springweb.converter.JsonConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 📦 商品实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_name", columnList = "product_name"),
    @Index(name = "idx_creator_id", columnList = "creator_id"),
    @Index(name = "idx_price", columnList = "price"),
    @Index(name = "idx_sales_count", columnList = "sales_count"),
    @Index(name = "idx_discount", columnList = "discount")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", unique = true, nullable = false, length = 50)
    private String productName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "sales_count", nullable = false)
    private Integer salesCount = 0;

    @Column(name = "discount", precision = 5, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Convert(converter = JsonConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> productData = Map.of();

    // ==================== ⏰ 时间戳字段 ====================

    /**
     * ⏰ 创建时间戳
     *
     * 记录商品首次创建的时间，由数据库自动管理
     * 格式：UTC时间戳
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    /**
     * 🔄 更新时间戳
     *
     * 记录商品最后一次更新的时间，由数据库自动管理
     * 格式：UTC时间戳
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ==================== 💰 业务逻辑方法 ====================

    /**
     * 📈 增加库存
     */
    public void increaseStock(Integer quantity) {
        if (quantity != null && quantity > 0) {
            this.stockQuantity += quantity;
        }
    }

    /**
     * 📉 减少库存并增加销量
     */
    public boolean decreaseStock(Integer quantity) {
        if (quantity != null && quantity > 0 && this.stockQuantity >= quantity) {
            this.stockQuantity -= quantity;
            this.salesCount += quantity;
            return true;
        }
        return false;
    }

    /**
     * 🛒 检查商品是否可购买
     */
    public boolean isPurchasable() {
        return this.isAvailable && this.stockQuantity > 0;
    }

    /**
     * 💰 计算折扣价格
     */
    public BigDecimal getDiscountedPrice() {
        if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
            return price.multiply(BigDecimal.ONE.subtract(discount.divide(BigDecimal.valueOf(100))));
        }
        return price;
    }

    /**
     * 📊 获取折扣金额
     */
    public BigDecimal getDiscountAmount() {
        if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
            return price.multiply(discount.divide(BigDecimal.valueOf(100)));
        }
        return BigDecimal.ZERO;
    }

    // ==================== 🖼️ 图片相关便捷方法 ====================

    /**
     * 🖼️ 设置主图片URL
     */
    public void setMainImage(String mainImageUrl) {
        @SuppressWarnings("unchecked")
        Map<String, Object> imageData = (Map<String, Object>) productData.computeIfAbsent("image_data", k -> new java.util.HashMap<>());
        imageData.put("main_image", mainImageUrl);
    }

    /**
     * 📸 获取主图片URL
     */
    public String getMainImage() {
        @SuppressWarnings("unchecked")
        Map<String, Object> imageData = (Map<String, Object>) productData.getOrDefault("image_data", new java.util.HashMap<>());
        return (String) imageData.get("main_image");
    }

    /**
     * 🖼️ 添加图片URL
     */
    public void addImageUrl(String imageUrl) {
        @SuppressWarnings("unchecked")
        Map<String, Object> imageData = (Map<String, Object>) productData.computeIfAbsent("image_data", k -> new java.util.HashMap<>());
        @SuppressWarnings("unchecked")
        List<String> gallery = (List<String>) imageData.computeIfAbsent("gallery", k -> new java.util.ArrayList<>());
        gallery.add(imageUrl);
    }

    /**
     * 📋 获取所有图片URL
     */
    @SuppressWarnings("unchecked")
    public List<String> getImageUrls() {
        @SuppressWarnings("unchecked")
        Map<String, Object> imageData = (Map<String, Object>) productData.getOrDefault("image_data", new java.util.HashMap<>());
        return (List<String>) imageData.getOrDefault("gallery", new java.util.ArrayList<>());
    }

    /**
     * 📋 设置图片URL列表
     */
    public void setImageUrls(List<String> imageUrls) {
        @SuppressWarnings("unchecked")
        Map<String, Object> imageData = (Map<String, Object>) productData.computeIfAbsent("image_data", k -> new java.util.HashMap<>());
        imageData.put("gallery", imageUrls);
    }

    /**
     * 🖼️ 获取缩略图信息
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getThumbnails() {
        @SuppressWarnings("unchecked")
        Map<String, Object> imageData = (Map<String, Object>) productData.getOrDefault("image_data", new java.util.HashMap<>());
        return (Map<String, String>) imageData.get("thumbnails");
    }

    /**
     * 🖼️ 设置缩略图信息
     */
    public void setThumbnails(Map<String, String> thumbnails) {
        @SuppressWarnings("unchecked")
        Map<String, Object> imageData = (Map<String, Object>) productData.computeIfAbsent("image_data", k -> new java.util.HashMap<>());
        imageData.put("thumbnails", thumbnails);
    }

    /**
     * 📊 获取图片总数
     */
    @SuppressWarnings("unchecked")
    public Integer getTotalImages() {
        @SuppressWarnings("unchecked")
        Map<String, Object> imageData = (Map<String, Object>) productData.getOrDefault("image_data", new java.util.HashMap<>());
        return (Integer) imageData.getOrDefault("total_images", 0);
    }

    /**
     * 📏 更新图片总数统计
     */
    public void updateImageCount() {
        @SuppressWarnings("unchecked")
        Map<String, Object> imageData = (Map<String, Object>) productData.getOrDefault("image_data", new java.util.HashMap<>());
        @SuppressWarnings("unchecked")
        List<String> gallery = (List<String>) imageData.getOrDefault("gallery", new java.util.ArrayList<>());
        imageData.put("total_images", gallery.size());
    }

    // ==================== 📋 规格相关便捷方法 ====================

    /**
     * 🏷️ 添加规格属性
     */
    public void addSpecification(String key, Object value) {
        @SuppressWarnings("unchecked")
        Map<String, Object> specifications = (Map<String, Object>) productData.computeIfAbsent("specifications", k -> new java.util.HashMap<>());
        specifications.put(key, value);
    }

    /**
     * 📋 获取规格属性
     */
    public Object getSpecification(String key) {
        @SuppressWarnings("unchecked")
        Map<String, Object> specifications = (Map<String, Object>) productData.getOrDefault("specifications", new java.util.HashMap<>());
        return specifications.get(key);
    }

    /**
     * 🗑️ 移除规格属性
     */
    public Object removeSpecification(String key) {
        @SuppressWarnings("unchecked")
        Map<String, Object> specifications = (Map<String, Object>) productData.getOrDefault("specifications", new java.util.HashMap<>());
        return specifications.remove(key);
    }

    /**
     * 📋 获取所有规格属性
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAllSpecifications() {
        return Map.copyOf((Map<String, Object>) productData.getOrDefault("specifications", new java.util.HashMap<>()));
    }

    
    // ==================== 🔧 扩展属性便捷方法 ====================

    /**
     * 🔧 添加扩展属性
     */
    public void addExtendedAttribute(String key, Object value) {
        @SuppressWarnings("unchecked")
        Map<String, Object> extendedAttributes = (Map<String, Object>) productData.computeIfAbsent("extended_attributes", k -> new java.util.HashMap<>());
        extendedAttributes.put(key, value);
    }

    /**
     * 📋 获取扩展属性
     */
    public Object getExtendedAttribute(String key) {
        @SuppressWarnings("unchecked")
        Map<String, Object> extendedAttributes = (Map<String, Object>) productData.getOrDefault("extended_attributes", new java.util.HashMap<>());
        return extendedAttributes.get(key);
    }

    /**
     * 🗑️ 移除扩展属性
     */
    public Object removeExtendedAttribute(String key) {
        @SuppressWarnings("unchecked")
        Map<String, Object> extendedAttributes = (Map<String, Object>) productData.getOrDefault("extended_attributes", new java.util.HashMap<>());
        return extendedAttributes.remove(key);
    }

    /**
     * 📋 获取所有扩展属性
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAllExtendedAttributes() {
        return Map.copyOf((Map<String, Object>) productData.getOrDefault("extended_attributes", new java.util.HashMap<>()));
    }

    // ==================== 🎨 变体相关便捷方法 ====================

    /**
     * 🎨 添加商品变体
     */
    public void addVariant(String variant) {
        @SuppressWarnings("unchecked")
        List<String> variants = (List<String>) productData.computeIfAbsent("variants", k -> new java.util.ArrayList<>());
        if (!variants.contains(variant)) {
            variants.add(variant);
        }
    }

    /**
     * 🗑️ 移除商品变体
     */
    public boolean removeVariant(String variant) {
        @SuppressWarnings("unchecked")
        List<String> variants = (List<String>) productData.getOrDefault("variants", new java.util.ArrayList<>());
        return variants.remove(variant);
    }

    /**
     * 📋 检查是否包含变体
     */
    public boolean hasVariant(String variant) {
        @SuppressWarnings("unchecked")
        List<String> variants = (List<String>) productData.getOrDefault("variants", new java.util.ArrayList<>());
        return variants.contains(variant);
    }

    // ==================== 📊 商品状态判断方法 ====================

    /**
     * 🛒 检查是否为上架状态
     */
    public boolean isListed() {
        return Boolean.TRUE.equals(this.isAvailable);
    }

    /**
     * 📦 检查是否有库存
     */
    public boolean hasStock() {
        return this.stockQuantity != null && this.stockQuantity > 0;
    }

    /**
     * 📉 检查是否为低库存状态
     */
    public boolean isLowStock() {
        return this.stockQuantity != null && this.stockQuantity < 10;
    }

    /**
     * 📈 获取库存状态描述
     */
    public String getStockStatus() {
        if (this.stockQuantity == null || this.stockQuantity == 0) {
            return "缺货";
        } else if (this.stockQuantity < 10) {
            return "库存紧张";
        } else if (this.stockQuantity < 50) {
            return "库存充足";
        } else {
            return "库存过多";
        }
    }

    // ==================== 📈 价格相关便捷方法 ====================

    /**
     * 💰 获取格式化的显示价格
     */
    public String getFormattedPrice() {
        return String.format("¥%.2f", price);
    }

    /**
     * 💰 获取格式化的折扣价格
     */
    public String getFormattedDiscountedPrice() {
        return String.format("¥%.2f", getDiscountedPrice());
    }

    /**
     * 📊 获取折扣率显示
     */
    public String getDiscountDisplay() {
        if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
            return String.format("%.1f%%", discount);
        }
        return "";
    }

    // ==================== 🏷️ 分类相关便捷方法 ====================

    /**
     * 📂 获取商品分类
     */
    public String getCategory() {
        @SuppressWarnings("unchecked")
        Map<String, Object> specifications = (Map<String, Object>) productData.getOrDefault("specifications", new java.util.HashMap<>());
        return (String) specifications.get("category");
    }

    /**
     * 🏷️ 获取商品品牌
     */
    public String getBrand() {
        @SuppressWarnings("unchecked")
        Map<String, Object> specifications = (Map<String, Object>) productData.getOrDefault("specifications", new java.util.HashMap<>());
        return (String) specifications.get("brand");
    }

    /**
     * 🎨 获取商品颜色
     */
    public String getColor() {
        @SuppressWarnings("unchecked")
        Map<String, Object> specifications = (Map<String, Object>) productData.getOrDefault("specifications", new java.util.HashMap<>());
        return (String) specifications.get("color");
    }

    /**
     * 📏 获取商品尺寸
     */
    public String getSize() {
        @SuppressWarnings("unchecked")
        Map<String, Object> specifications = (Map<String, Object>) productData.getOrDefault("specifications", new java.util.HashMap<>());
        return (String) specifications.get("size");
    }
}