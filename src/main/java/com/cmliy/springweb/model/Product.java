// ============================================================================
// 商品实体模型 - Product Entity
// ============================================================================

// package: Java包声明，用于组织类和避免命名冲突
package com.cmliy.springweb.model;

// import: 导入其他包中的类，以便在当前类中使用
import jakarta.persistence.*;                           // 导入JPA（Jakarta Persistence API）所有注解
import org.hibernate.annotations.CreationTimestamp;    // 导入Hibernate创建时间戳注解
import org.hibernate.annotations.UpdateTimestamp;      // 导入Hibernate更新时间戳注解

import java.time.LocalDateTime;                         // 导入Java 8日期时间类，用于处理日期和时间

/**
 * 📦 商品实体模型
 *
 * 这个类使用JPA（Jakarta Persistence API）注解映射到数据库的products表。
 * 实体类是ORM（对象关系映射）的核心，实现了Java对象与数据库记录的双向转换。
 *
 * 商品实体的业务功能：
 * - 商品基本信息管理（名称、价格）
 * - 商品与创建者的关联关系
 * - 商品上架状态控制
 * - 库存数量管理
 * - 创建时间自动记录
 *
 * JPA主要注解：
 * - @Entity: 标记类为JPA实体，告诉JPA这是一个需要管理的实体
 * - @Table: 指定数据库表名、索引等元数据信息
 * - @Id: 标记主键字段，唯一标识数据库记录
 * - @Column: 定义列属性（名称、约束、长度等）
 * - @ManyToOne: 定义多对一关联关系
 * - @JoinColumn: 定义外键列映射
 * - @CreationTimestamp: Hibernate时间戳注解
 */
@Entity // @Entity注解：声明这是一个JPA实体类，Hibernate会自动管理其数据库映射
@Table(name = "products") // @Table注解：定义数据库表的元数据
public class Product { // public class: 定义公共类，其他类可以访问

    /**
     * 🏗️ 默认构造函数
     * JPA规范要求实体类必须有无参构造函数
     */
    public Product() {} // 无参构造函数：JPA反射创建对象时使用

    /**
     * 🔑 商品主键ID
     *
     * 使用数据库自增策略，确保每个商品都有唯一的标识符。
     *
     * @Id: JPA注解，标记这个字段为主键
     * @GeneratedValue: JPA注解，配置主键生成策略
     * GenerationType.IDENTITY: 使用数据库自增功能（MySQL、PostgreSQL等）
     */
    @Id // @Id注解：标记这个字段为主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // @GeneratedValue注解：配置主键自增策略
    private Long id; // id: 商品ID，Long类型，数据库自动递增

    /**
     * 📝 商品名称
     *
     * 商品的显示名称，必须唯一且不能为空。
     * 设置了唯一约束，防止重复商品。
     *
     * @Column: JPA注解，定义数据库列属性
     * name = "product_name": 数据库列名
     * unique = true: 唯一约束，确保商品名不重复
     * nullable = false: 非空约束，必须提供值
     * length = 50: 字段长度限制，最多50个字符
     */
    @Column(name = "product_name", unique = true, nullable = false, length = 50) // @Column注解：定义数据库列属性
    private String productName; // productName: 商品名称，String类型，唯一且不能为空

    /**
     * 💰 商品价格
     *
     * 商品的价格信息，必须为非空值。
     * 使用Double类型支持小数点价格。
     *
     * @Column(name = "price", nullable = false) // @Column注解：定义价格列属性
     */
    @Column(name="price", nullable = false)
    private Double price; // price: 商品价格，Double类型，不能为空

    /**
     * 👤 商品创建者关联
     *
     * 建立与User实体的多对一关系，一个用户可以创建多个商品。
     * 使用LAZY加载策略，提高查询性能。
     *
     * @ManyToOne: JPA注解，定义多对一关系
     * fetch = FetchType.LAZY: 延迟加载，避免不必要的数据查询
     * @JoinColumn: JPA注解，定义外键列映射
     * name = "creator_id": 外键列名，指向users表的id字段
     * nullable = false: 外键不能为空，每个商品必须有创建者
     */
    @ManyToOne(fetch = FetchType.LAZY) // @ManyToOne注解：定义多对一关系
    @JoinColumn(name = "creator_id", nullable = false) // @JoinColumn注解：定义外键列映射
    private User creator; // creator: 创建者用户对象

    // ===== 新增属性 =====

    /**
     * 🕒 商品创建时间
     *
     * 使用Hibernate自动时间戳注解，创建时自动设置当前时间。
     * 与User实体的时间戳设计保持一致。
     *
     * @CreationTimestamp: Hibernate注解，实体创建时自动设置当前时间
     * @Column: JPA注解，定义创建时间列属性
     * name = "created_at": 数据库列名，与User实体保持一致
     * updatable = false: 创建后不可修改，保持时间戳的准确性
     * nullable = false: 必须有值，Hibernate会自动设置
     */
    @CreationTimestamp // 创建时间戳注解：实体创建时自动设置当前时间
    @Column(name = "created_at", updatable = false, nullable = false) // 定义创建时间列属性
    private LocalDateTime createdAt; // createdAt: 商品创建时间，LocalDateTime类型

    /**
     * 📦 商品上架状态
     *
     * 控制商品是否在前台显示和可购买：
     * - true: 上架可购买
     * - false: 下架不可购买
     *
     * @Column: JPA注解，定义上架状态列属性
     * name = "is_available": 数据库列名，语义清晰
     * nullable = false: 必须明确状态
     */
    @Column(name = "is_available", nullable = false) // 定义上架状态列属性
    private Boolean isAvailable = false; // isAvailable: 上架状态，Boolean类型，默认为false

    /**
     * 📊 商品库存数量
     *
     * 管理商品的库存数量，必须为非负数。
     * 使用Integer类型，足够大的整数范围支持库存管理。
     *
     * @Column: JPA注解，定义库存数量列属性
     * name = "stock_quantity": 数据库列名，清晰表达库存数量含义
     * nullable = false: 必须有明确的库存值
     */
    @Column(name = "stock_quantity", nullable = false) // 定义库存数量列属性
    private Integer stockQuantity = 0; // stockQuantity: 库存数量，Integer类型，默认为0

    // ===== Getter和Setter方法 =====
    // Getter方法：用于获取私有字段的值，遵循JavaBean规范

    /**
     * 🔑 获取商品ID
     * @return 商品ID
     */
    public Long getId() { return id; } // 获取商品ID

    /**
     * 📝 获取商品名称
     * @return 商品名称
     */
    public String getProductName() { return productName; } // 获取商品名称

    /**
     * 💰 获取商品价格
     * @return 商品价格
     */
    public Double getPrice() { return price; } // 获取价格

    /**
     * 👤 获取创建者对象
     * @return 创建者用户对象
     */
    public User getCreator() { return creator; } // 获取创建者对象

    /**
     * 🎯 便捷方法：获取创建者ID
     * 从关联的User对象中获取ID，避免空指针异常
     * @return 创建者用户ID，如果creator为null则返回null
     */
    public Long getCreatorId() {
        return creator != null ? creator.getId() : null;
    }

    /**
     * 🕒 获取商品创建时间
     * @return 商品创建时间
     */
    public LocalDateTime getCreatedAt() {
        return createdAt; // 返回创建时间
    }

    /**
     * 📦 获取商品上架状态
     * @return 上架状态，true表示已上架
     */
    public Boolean getIsAvailable() {
        return isAvailable; // 返回上架状态
    }

    /**
     * 📊 获取商品库存数量
     * @return 库存数量
     */
    public Integer getStockQuantity() {
        return stockQuantity; // 返回库存数量
    }

    // ===== Setter方法 =====
    // Setter方法：用于设置私有字段的值，遵循JavaBean规范

    /**
     * 🔑 设置商品ID
     * @param id 商品ID
     */
    public void setId(Long id) { this.id = id; } // 设置商品ID

    /**
     * 📝 设置商品名称
     * @param productName 商品名称
     */
    public void setProductName(String productName) { this.productName = productName; } // 设置商品名称

    /**
     * 💰 设置商品价格
     * @param price 商品价格
     */
    public void setPrice(Double price) { this.price = price; } // 设置价格

    /**
     * 👤 设置创建者
     * 直接设置User对象，JPA会自动处理外键关系
     * @param creator 创建者用户对象
     */
    public void setCreator(User creator) {
        this.creator = creator; // 设置创建者对象
    }

    /**
     * 🕒 设置商品创建时间
     * @param createdAt 商品创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt; // 设置创建时间
    }

    /**
     * 📦 设置商品上架状态
     * @param isAvailable 上架状态，true表示上架
     */
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable; // 设置上架状态
    }

    /**
     * 📊 设置商品库存数量
     * 包含业务逻辑验证：不允许设置负数库存
     * @param stockQuantity 库存数量，必须为非负数
     */
    public void setStockQuantity(Integer stockQuantity) {
        // 业务逻辑验证：库存不能为负数
        if (stockQuantity != null && stockQuantity < 0) { // 检查库存是否为负数
            throw new IllegalArgumentException("库存数量不能为负数"); // 抛出业务异常
        }
        this.stockQuantity = stockQuantity != null ? stockQuantity : 0; // 设置库存，null时默认为0
    }

    // ===== 业务逻辑方法 =====

    /**
     * 🎯 增加库存数量
     *
     * 安全地增加库存，包含参数验证。
     * 适用于商品补货、入库等业务场景。
     *
     * @param quantity 要增加的数量，必须为正数
     * @throws IllegalArgumentException 当数量为null或非正数时
     */
    public void increaseStock(Integer quantity) {
        if (quantity == null || quantity <= 0) { // 验证参数有效性
            throw new IllegalArgumentException("增加数量必须为正数"); // 抛出参数异常
        }
        this.stockQuantity += quantity; // 增加库存数量
    }

    /**
     * 🎯 减少库存数量
     *
     * 安全地减少库存，包含库存充足性检查。
     * 适用于商品销售、出库等业务场景。
     *
     * @param quantity 要减少的数量，必须为正数且不超过当前库存
     * @throws IllegalArgumentException 当数量为null、非正数或库存不足时
     */
    public void decreaseStock(Integer quantity) {
        if (quantity == null || quantity <= 0) { // 验证参数有效性
            throw new IllegalArgumentException("减少数量必须为正数"); // 抛出参数异常
        }
        if (this.stockQuantity < quantity) { // 检查库存是否充足
            throw new IllegalArgumentException("库存不足，当前库存：" + this.stockQuantity); // 抛出库存不足异常
        }
        this.stockQuantity -= quantity; // 减少库存数量
    }

    /**
     * 🎯 检查是否有库存
     *
     * 快速判断商品是否有库存可供销售。
     *
     * @return true表示有库存，false表示无库存
     */
    public boolean hasStock() {
        return this.stockQuantity > 0; // 检查库存是否大于0
    }

    /**
     * 🎯 上架商品
     *
     * 将商品设置为可购买状态。
     * 适用于新品上架、重新上架等业务场景。
     */
    public void listProduct() {
        this.isAvailable = true; // 设置为上架状态
    }

    /**
     * 🎯 下架商品
     *
     * 将商品设置为不可购买状态。
     * 适用于商品下架、暂时缺货等业务场景。
     */
    public void unlistProduct() {
        this.isAvailable = false; // 设置为下架状态
    }

    /**
     * 🎯 检查商品是否可购买
     *
     * 商品必须同时满足两个条件：
     * 1. 已上架（isAvailable = true）
     * 2. 有库存（stockQuantity > 0）
     *
     * @return true表示可购买，false表示不可购买
     */
    public boolean isPurchasable() {
        return this.isAvailable && this.hasStock(); // 检查上架状态和库存状态
    }
}