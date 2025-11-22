package com.cmliy.springweb.repository;

import com.cmliy.springweb.model.Product;
import com.cmliy.springweb.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 📦 商品数据访问接口 - Product Repository
 *
 * 这个接口定义了商品数据访问层的所有操作方法。
 * 继承JpaRepository，自动获得基本的CRUD操作能力。
 * 通过自定义查询方法，支持复杂的JSONB字段查询。
 *
 * @author Claude
 * @since 2025-01-20
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    /**
     * 📋 根据商品名称查找商品
     *
     * 使用商品名称进行精确匹配查询。
     * 由于productName字段有唯一约束，这个方法最多返回一个结果。
     *
     * @param productName 商品名称
     * @return 匹配的商品，不存在时返回Optional.empty()
     */
    Optional<Product> findByProductName(String productName);

    /**
     * 📋 根据创建者ID查找商品列表
     *
     * 查询指定用户创建的所有商品，支持分页。
     * 使用LAZY加载避免N+1查询问题。
     *
     * @param creatorId 创建者ID
     * @param pageable 分页对象
     * @return 商品分页结果
     */
    Page<Product> findByCreatorId(Long creatorId, Pageable pageable);

    /**
     * 📋 根据价格范围查找商品列表
     *
     * 查询价格在指定范围内的所有商品。
     * 支持分页和排序功能。
     *
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param pageable 分页对象
     * @return 商品分页结果
     */
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * 📋 查找可购买的商品列表
     *
     * 查询上架且有库存的商品，即isAvailable=true且stockQuantity>0。
     * 这是前台展示的核心查询方法。
     *
     * @param pageable 分页对象
     * @return 可购买的商品分页结果
     */
    Page<Product> findByIsAvailableTrueAndStockQuantityGreaterThan(Integer stockQuantity, Pageable pageable);

    /**
     * 📋 查找缺货的商品列表
     *
     * 查询库存为0的商品，用于库存管理。
     *
     * @param stockQuantity 库存数量
     * @param pageable 分页对象
     * @return 缺货的商品分页结果
     */
    Page<Product> findByStockQuantity(Integer stockQuantity, Pageable pageable);

    /**
     * 📋 查找低库存的商品列表
     *
     * 查询库存小于10的商品，用于库存预警。
     *
     * @param threshold 库存阈值
     * @param pageable 分页对象
     * @return 低库存的商品分页结果
     */
    Page<Product> findByStockQuantityLessThan(Integer threshold, Pageable pageable);

    /**
     * 📋 查找有折扣的商品列表
     *
     * 查询折扣率大于0的商品，用于促销活动。
     *
     * @param minDiscount 最小折扣率
     * @param pageable 分页对象
     * @return 有折扣的商品分页结果
     */
    Page<Product> findByDiscountGreaterThan(BigDecimal minDiscount, Pageable pageable);

    /**
     * 📋 查找指定折扣范围的商品列表
     *
     * 查询折扣率在指定范围内的商品。
     * 支持分页和排序功能。
     *
     * @param minDiscount 最低折扣率
     * @param maxDiscount 最高折扣率
     * @param pageable 分页对象
     * @return 指定折扣范围的商品分页结果
     */
    Page<Product> findByDiscountBetween(BigDecimal minDiscount, BigDecimal maxDiscount, Pageable pageable);

    /**
     * 📋 查找热销商品列表
     *
     * 查询销量大于指定数量的商品，按销量降序排列。
     * 用于推荐系统和热销榜单。
     *
     * @param minSalesCount 最小销量
     * @param pageable 分页对象
     * @return 热销商品分页结果
     */
    @Query("SELECT p FROM Product p WHERE p.salesCount > :minSalesCount ORDER BY p.salesCount DESC")
    Page<Product> findTopSellingProducts(@Param("minSalesCount") Integer minSalesCount, Pageable pageable);

    /**
     * 📋 查找新品商品列表
     *
     * 查询最近创建的商品，按创建时间降序排列。
     * 用于新品展示和推荐。
     *
     * @param pageable 分页对象
     * @return 新品商品分页结果
     */
    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    Page<Product> findNewestProducts(Pageable pageable);

    /**
     * 📋 根据商品名称模糊查询
     *
     * 使用LIKE操作符进行商品名称的模糊查询。
     * 支持分页和排序功能。
     *
     * @param keyword 搜索关键词
     * @param pageable 分页对象
     * @return 匹配的商品分页结果
     */
    @Query("SELECT p FROM Product p WHERE p.productName LIKE %:keyword%")
    Page<Product> findByProductNameContaining(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 📋 根据商品描述模糊查询
     *
     * 使用LIKE操作符进行商品描述的模糊查询。
     * 支持分页和排序功能。
     *
     * @param keyword 搜索关键词
     * @param pageable 分页对象
     * @return 匹配的商品分页结果
     */
    @Query("SELECT p FROM Product p WHERE p.description LIKE %:keyword%")
    Page<Product> findByDescriptionContaining(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 📋 复合搜索商品列表
     *
     * 支持商品名称、描述、类别、价格范围、上架状态等多条件组合查询。
     * 这是前台搜索的核心方法。
     *
     * @param keyword 搜索关键词（可为null）
     * @param category 商品类别（可为null）
     * @param minPrice 最低价格（可为null）
     * @param maxPrice 最高价格（可为null）
     * @param isAvailable 上架状态（可为null）
     * @param pageable 分页对象
     * @return 匹配的商品分页结果
     */
    @Query(value = "SELECT p.* FROM Product p WHERE " +
           "(:keyword IS NULL OR p.product_name LIKE %:keyword% OR p.description LIKE %:keyword%) AND " +
           "(:category IS NULL OR p.productData->'specifications'->>'category' = :category) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:isAvailable IS NULL OR p.is_available = :isAvailable)", nativeQuery = true)
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("isAvailable") Boolean isAvailable,
            Pageable pageable
    );

    /**
     * 📋 查找有主图片的商品列表
     *
     * 使用JSONB路径查询，查询设置了主图片的商品。
     * 用于图片展示和列表渲染。
     *
     * @param pageable 分页对象
     * @return 有主图片的商品分页结果
     */
    @Query(value = "SELECT p.* FROM Product p WHERE p.productData->'image_data'->>'main_image' IS NOT NULL", nativeQuery = true)
    Page<Product> findByHasMainImage(Pageable pageable);

    /**
     * 📋 查找有图片的商品列表
     *
     * 使用JSONB包含查询，查询有图片的商品。
     * 用于图片展示和列表渲染。
     *
     * @param pageable 分页对象
     * @return 有图片的商品分页结果
     */
    @Query(value = "SELECT p.* FROM Product p WHERE jsonb_array_length(p.productData->'image_data'->'gallery') > 0", nativeQuery = true)
    Page<Product> findByHasImages(Pageable pageable);

    /**
     * 📋 统计商品总数
     *
     * 统计数据库中的商品总数。
     *
     * @return 商品总数
     */
    @Query("SELECT COUNT(p) FROM Product p")
    Long countAll();

    /**
     * 📋 统计可购买的商品数量
     *
     * 统计上架且有库存的商品数量。
     *
     * @return 可购买的商品数量
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.isAvailable = true AND p.stockQuantity > 0")
    Long countByIsAvailableTrueAndStockQuantityGreaterThan();

    /**
     * 📋 统计缺货的商品数量
     *
     * 统计库存为0的商品数量。
     *
     * @return 缺货的商品数量
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity = 0")
    Long countByStockQuantityZero();

    /**
     * 📋 统计低库存的商品数量
     *
     * 统计库存小于10的商品数量。
     *
     * @return 低库存的商品数量
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity < 10")
    Long countByLowStock();

    /**
     * 📋 统计有折扣的商品数量
     *
     * 统计折扣率大于0的商品数量。
     *
     * @return 有折扣的商品数量
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.discount > 0")
    Long countByDiscountGreaterThan();

    /**
     * 📋 按类别统计商品数量
     *
     * 使用JSONB路径查询，统计每个类别的商品数量。
     * 用于分类管理和统计报表。
     *
     * @return 类别统计结果
     */
    @Query(value = "SELECT p.productData->'specifications'->>'category' as category, COUNT(p) as count " +
           "FROM Product p GROUP BY p.productData->'specifications'->>'category'", nativeQuery = true)
    List<Object[]> countByCategory();

    /**
     * 📋 计算指定类别的平均价格
     *
     * 使用JSONB路径查询，计算指定类别的平均价格。
     * 用于价格分析和市场调研。
     *
     * @param category 商品类别
     * @return 平均价格
     */
    @Query(value = "SELECT AVG(p.price) FROM Product p WHERE p.productData->'specifications'->>'category' = :category", nativeQuery = true)
    BigDecimal getAveragePriceByCategory(@Param("category") String category);

    /**
     * 📋 查找指定用户的商品统计信息
     *
     * 统计指定用户的商品总数、可购买数量、缺货数量等。
     * 用于用户管理和统计报表。
     *
     * @param creatorId 创建者ID
     * @return 统计信息对象数组
     */
    @Query("SELECT " +
           "COUNT(p) as totalProducts, " +
           "SUM(CASE WHEN p.isAvailable = true AND p.stockQuantity > 0 THEN 1 ELSE 0 END) as purchasableProducts, " +
           "SUM(CASE WHEN p.stockQuantity = 0 THEN 1 ELSE 0 END) as outOfStockProducts, " +
           "SUM(p.salesCount) as totalSales, " +
           "AVG(p.price) as averagePrice " +
           "FROM Product p WHERE p.creator.id = :creatorId")
    Object[] getProductStatsByCreator(@Param("creatorId") Long creatorId);

    /**
     * 📋 查找指定创建者的商品列表
     *
     * 查询指定用户创建的商品，支持分页和排序。
     * 用于用户商品管理和展示。
     *
     * @param creator 创建者用户
     * @param pageable 分页对象
     * @return 指定创建者的商品分页结果
     */
    Page<Product> findByCreator(User creator, Pageable pageable);

    /**
     * 📋 查找指定创建者的商品数量
     *
     * 统计指定用户创建的商品数量。
     * 用于用户统计和权限管理。
     *
     * @param creator 创建者用户
     * @return 指定创建者的商品数量
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.creator = :creator")
    Long countByCreator(@Param("creator") User creator);

    /**
     * 📋 查找有图片的商品数量
     *
     * 使用JSONB路径查询，统计设置了主图片的商品数量。
     * 用于图片管理和统计报表。
     *
     * @return 有主图片的商品数量
     */
    @Query(value = "SELECT COUNT(p) FROM Product p WHERE p.productData->'image_data'->>'main_image' IS NOT NULL", nativeQuery = true)
    Long countByHasMainImage();

    /**
     * 📋 查找有图片的商品数量
     *
     * 使用JSONB包含查询，统计有图片的商品数量。
     * 用于图片管理和统计报表。
     *
     * @return 有图片的商品数量
     */
    @Query(value = "SELECT COUNT(p) FROM Product p WHERE jsonb_array_length(p.productData->'image_data'->'gallery') > 0", nativeQuery = true)
    Long countByHasImages();

    /**
     * 📋 查找有规格的商品数量
     *
     * 使用JSONB路径查询，统计有规格属性的商品数量。
     * 用于规格管理和统计报表。
     *
     * @return 有规格的商品数量
     */
    @Query(value = "SELECT COUNT(p) FROM Product p WHERE jsonb_object_keys(p.productData->'specifications') IS NOT NULL", nativeQuery = true)
    Long countByHasSpecifications();

    /**
     * 📋 查找有扩展属性的商品数量
     *
     * 使用JSONB路径查询，统计有扩展属性的商品数量。
     * 用于扩展属性管理和统计报表。
     *
     * @return 有扩展属性的商品数量
     */
    @Query(value = "SELECT COUNT(p) FROM Product p WHERE jsonb_object_keys(p.productData->'extended_attributes') IS NOT NULL", nativeQuery = true)
    Long countByHasExtendedAttributes();

    /**
     * 📋 查找有变体的商品数量
     *
     * 使用JSONB包含查询，统计有变体的商品数量。
     * 用于变体管理和统计报表。
     *
     * @return 有变体的商品数量
     */
    @Query(value = "SELECT COUNT(p) FROM Product p WHERE jsonb_array_length(p.productData->'variants') > 0", nativeQuery = true)
    Long countByHasVariants();

    // ==================== 🔄 动态属性系统支持 ====================

    /**
     * 🔄 根据动态属性键值对查询商品
     *
     * 完全灵活的动态属性查询，支持任意属性名称（如"颜色"、"尺寸"、"材质"、"款式"等）
     * 零假设：不对属性名称和值做任何预设，完全由数据驱动
     * 优雅降级：没有动态属性的商品自动隐藏属性选择区域
     *
     * 使用PostgreSQL的jsonb_each_text函数实现扁平化查询
     *
     * @param attrName 属性名称（如："颜色"、"尺寸"、"材质"等）
     * @param attrValue 属性值
     * @param pageable 分页对象
     * @return 商品分页结果
     */
    @Query(value = "SELECT p.* FROM Product p, " +
           "jsonb_each_text(p.productData->'specifications') as spec " +
           "WHERE spec.key = :attrName AND spec.value = :attrValue", nativeQuery = true)
    Page<Product> findByAttributeName(@Param("attrName") String attrName,
                                    @Param("attrValue") String attrValue,
                                    Pageable pageable);

    /**
     * 🔄 多动态属性组合查询
     *
     * 支持多个任意属性名称的组合查询
     * 使用PostgreSQL的jsonb_path_exists函数实现复杂查询
     *
     * @param attributes 属性名值对
     * @param pageable 分页对象
     * @return 商品分页结果
     */
    @Query(value = "SELECT p.* FROM Product p WHERE " +
           "EXISTS(SELECT 1 FROM jsonb_each_text(p.productData->'specifications') as spec " +
           "WHERE (spec.key = :attrName1 AND spec.value = :attrValue1) " +
           "AND (spec.key = :attrName2 AND spec.value = :attrValue2))", nativeQuery = true)
    Page<Product> findByDynamicAttributes(@Param("attrName1") String attrName1,
                                        @Param("attrValue1") String attrValue1,
                                        @Param("attrName2") String attrName2,
                                        @Param("attrValue2") String attrValue2,
                                        Pageable pageable);

    /**
     * 🔄 获取所有存在的属性名称
     *
     * 查询所有商品中使用过的属性名称，用于前端构建筛选器
     * 优雅降级：没有动态属性的商品自动隐藏属性选择区域
     *
     * @return 属性名称列表
     */
    @Query(value = "SELECT DISTINCT jsonb_object_keys(p.productData->'specifications') as attrName " +
           "FROM Product p WHERE p.productData->'specifications' IS NOT NULL", nativeQuery = true)
    List<String> findAllSpecificationAttributeNames();

    /**
     * 📊 按属性名称统计值分布
     *
     * 统计指定属性名称的所有值及其出现次数
     * 用于前端动态构建筛选器
     *
     * @param attrName 属性名称
     * @return 值及其计数的列表
     */
    @Query(value = "SELECT p.productData->'specifications'->>:attrName as attrValue, COUNT(p) as count " +
           "FROM Product p WHERE p.productData->'specifications'->>:attrName IS NOT NULL " +
           "GROUP BY p.productData->'specifications'->>:attrName ORDER BY count DESC", nativeQuery = true)
    List<Object[]> countByAttributeValue(@Param("attrName") String attrName);

    /**
     * 🔄 根据属性名称和值范围查询（用于数值型属性）
     *
     * 支持价格、重量等数值型属性的范围查询
     *
     * @param attrName 属性名称
     * @param minValue 最小值
     * @param maxValue 最大值
     * @param pageable 分页对象
     * @return 商品分页结果
     */
    @Query(value = "SELECT p.* FROM Product p WHERE " +
           "(p.productData->'specifications'->>:attrName)::numeric BETWEEN :minValue AND :maxValue", nativeQuery = true)
    Page<Product> findBySpecificationRange(@Param("attrName") String attrName,
                                          @Param("minValue") BigDecimal minValue,
                                          @Param("maxValue") BigDecimal maxValue,
                                          Pageable pageable);

    /**
     * 🔄 根据属性名进行模糊匹配查询
     *
     * 支持文本型属性的模糊查询
     *
     * @param attrName 属性名称
     * @param attrValue 模糊匹配值
     * @param pageable 分页对象
     * @return 商品分页结果
     */
    @Query(value = "SELECT p.* FROM Product p WHERE " +
           "p.productData->'specifications'->>:attrName ILIKE %:attrValue%", nativeQuery = true)
    Page<Product> findBySpecificationLike(@Param("attrName") String attrName,
                                         @Param("attrValue") String attrValue,
                                         Pageable pageable);

    // ==================== 📊 ProductService专用方法 ====================

    /**
     * 📋 检查商品名称是否存在
     *
     * @param productName 商品名称
     * @return 是否存在
     */
    boolean existsByProductName(String productName);

    /**
     * 📋 统计上架商品数量
     *
     * @param isAvailable 上架状态
     * @return 商品数量
     */
    long countByIsAvailable(Boolean isAvailable);

    /**
     * 📋 统计指定库存的商品数量
     *
     * @param stockQuantity 库存数量
     * @return 商品数量
     */
    long countByStockQuantity(Integer stockQuantity);

    /**
     * 📋 获取热销商品列表
     *
     * @param pageable 分页对象
     * @return 热销商品列表
     */
    @Query("SELECT p FROM Product p ORDER BY p.salesCount DESC")
    List<Product> findTopProductsBySalesCount(Pageable pageable);
}