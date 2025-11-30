package com.cmliy.springweb.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * 📦 商品实体
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@DynamicUpdate  // 🔧 关键修复：只更新实际修改的字段
@Table(name = "products", indexes = {
    @Index(name = "idx_product_name", columnList = "product_name"),
    @Index(name = "idx_creator_id", columnList = "creator_id"),
    @Index(name = "idx_price", columnList = "price"),
    @Index(name = "idx_sales_count", columnList = "sales_count"),
    @Index(name = "idx_discount", columnList = "discount")
})
public class Product {

    /**
     * 🆔 商品唯一标识符
     *
     * 主键，自动递增的唯一标识
     * 用于数据库索引和关联查询
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 📝 商品名称
     *
     * 商品的显示名称，必须唯一且不为空
     * 最大长度50字符，用于用户界面显示
     */
    @Column(name = "product_name", unique = true, nullable = false, length = 50)
    private String productName;

    /**
     * 📄 商品详细描述
     *
     * 商品的详细说明信息，支持长文本
     * 使用TEXT类型，可存储大段描述内容
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 💰 商品价格
     *
     * 商品的销售价格，必须不为空
     * 使用BigDecimal保证精度，最大10位数，其中2位小数
     */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 📊 销售数量
     *
     * 商品的总销售数量，默认值为0
     * 用于统计商品销量和热门程度分析
     */
    @Column(name = "sales_count", nullable = false)
    private Integer salesCount = 0;

    /**
     * 🏷️ 折扣百分比
     *
     * 商品的折扣百分比，默认为0（无折扣）
     * 精度为5位数，其中2位小数，例如：15.50表示15.5%折扣
     */
    @Column(name = "discount", precision = 5, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    /**
     * 📦 库存数量
     *
     * 商品的库存数量，默认为0
     * 用于库存管理和购买可用性检查
     */
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    /**
     * ✅ 商品上架状态
     *
     * 商品是否可购买的状态标志，默认为false（下架）
     * true表示上架可购买，false表示下架不可购买
     */
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = false;

    /**
     * 👤 商品创建者
     *
     * 创建该商品的用户，必须指定
     * 使用LAZY加载避免不必要的关联查询
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    /**
     * 🗄️ 商品扩展数据 - JSONB字段
     * 存储商品的规格、图片等扩展信息
     * 使用Hibernate原生JSONB支持，确保类型安全
     */
    @JdbcTypeCode(SqlTypes.JSON)  // 🔧 关键修复：指定JSON类型处理
    private Map<String, Object> productData = new HashMap<>();

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

    // ==================== 🖼️ 图片相关只读方法 ====================

    /**
     * 📸 获取主图片URL
     */
    public String getMainImage() {
        @SuppressWarnings("unchecked")
        Map<String, Object> imageData = (Map<String, Object>) productData.getOrDefault("image_data", new java.util.HashMap<>());
        return (String) imageData.get("main_image");
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
     * 🖼️ 获取缩略图信息
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getThumbnails() {
        @SuppressWarnings("unchecked")
        Map<String, Object> imageData = (Map<String, Object>) productData.getOrDefault("image_data", new java.util.HashMap<>());
        return (Map<String, String>) imageData.get("thumbnails");
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

    // ==================== 📋 规格相关只读方法 ====================

    /**
     * 📋 获取规格属性
     */
    public Object getSpecification(String key) {
        @SuppressWarnings("unchecked")
        Map<String, Object> specifications = (Map<String, Object>) productData.getOrDefault("specifications", new java.util.HashMap<>());
        return specifications.get(key);
    }

    /**
     * 📋 获取所有规格属性
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAllSpecifications() {
        return Map.copyOf((Map<String, Object>) productData.getOrDefault("specifications", new java.util.HashMap<>()));
    }

    /**
     * 🔍 检查是否存在指定规格
     */
    public boolean hasSpecification(String key) {
        @SuppressWarnings("unchecked")
        Map<String, Object> specifications = (Map<String, Object>) productData.getOrDefault("specifications", new java.util.HashMap<>());
        return specifications.containsKey(key);
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

    // ==================== 🔧 JSONB数据处理方法 ====================

    /**
     * 🔧 新增方法：安全的JSONB数据设置
     * 确保数据可以被正确序列化为JSON
     */
    public void setProductData(Map<String, Object> productData) {
        // 确保数据可以被正确序列化为JSON
        if (productData != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(productData);
                // 验证JSON格式正确性
                mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
                this.productData = productData;
            } catch (Exception e) {
                log.error("🔧 JSONB数据格式验证失败: {}", productData, e);
                throw new IllegalArgumentException("商品数据格式不正确", e);
            }
        } else {
            this.productData = new HashMap<>();
        }
    }

    /**
     * 🔧 新增方法：安全的JSONB数据获取
     */
    public Map<String, Object> getProductData() {
        if (this.productData == null) {
            return new HashMap<>();
        }
        return this.productData;
    }

    /**
     * 🔧 新增方法：安全地更新productData中的特定字段
     */
    public void updateProductDataField(String key, Object value) {
        if (this.productData == null) {
            this.productData = new HashMap<>();
        }
        this.productData.put(key, value);
    }

    /**
     * 🔧 新增方法：从productData中安全地获取特定字段
     */
    @SuppressWarnings("unchecked")
    public <T> T getProductDataField(String key, Class<T> type, T defaultValue) {
        if (this.productData == null) {
            return defaultValue;
        }
        Object value = this.productData.get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return type.cast(value);
        } catch (ClassCastException e) {
            log.warn("🔧 productData字段类型转换失败: key={}, expectedType={}, actualType={}, value={}",
                     key, type.getSimpleName(), value.getClass().getSimpleName(), value);
            return defaultValue;
        }
    }
}