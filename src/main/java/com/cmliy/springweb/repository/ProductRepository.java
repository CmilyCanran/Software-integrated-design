package com.cmliy.springweb.repository;

import com.cmliy.springweb.model.Product;
import com.cmliy.springweb.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
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
public interface ProductRepository extends JpaRepository<Product, Long> {

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
     * 📋 根据商品类别查找商品列表
     *
     * 使用JSONB路径查询，查询指定类别的所有商品。
     * 使用PostgreSQL的->>操作符进行JSONB字段路径访问。
     *
     * @param category 商品类别
     * @param pageable 分页对象
     * @return 商品分页结果
     */
    @Query(value = "SELECT p.* FROM Product p WHERE p.productData->'specifications'->>'category' = :category", nativeQuery = true)
    Page<Product> findByCategory(@Param("category") String category, Pageable pageable);

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
     * 📋 根据商品规格属性查找商品列表
     *
     * 使用JSONB路径查询，查询具有指定规格属性的商品。
     * 支持多种规格属性的组合查询。
     *
     * @param color 颜色属性值
     * @param size 尺寸属性值
     * @param brand 品牌属性值
     * @param pageable 分页对象
     * @return 商品分页结果
     */
    @Query(value = "SELECT p.* FROM Product p WHERE " +
           "(:color IS NULL OR p.productData->'specifications'->>'color' = :color) AND " +
           "(:size IS NULL OR p.productData->'specifications'->>'size' = :size) AND " +
           "(:brand IS NULL OR p.productData->'specifications'->>'brand' = :brand)", nativeQuery = true)
    Page<Product> findBySpecifications(
            @Param("color") String color,
            @Param("size") String size,
            @Param("brand") String brand,
            Pageable pageable
    );

    /**
     * 📋 根据商品颜色查找商品列表
     *
     * 使用JSONB路径查询，查询指定颜色的所有商品。
     * 这是一个便捷方法，内部调用findBySpecifications。
     *
     * @param color 颜色
     * @param pageable 分页对象
     * @return 商品分页结果
     */
    default Page<Product> findByColor(String color, Pageable pageable) {
        return findBySpecifications(color, null, null, pageable);
    }

    /**
     * 📋 根据商品尺寸查找商品列表
     *
     * 使用JSONB路径查询，查询指定尺寸的所有商品。
     * 这是一个便捷方法，内部调用findBySpecifications。
     *
     * @param size 尺寸
     * @param pageable 分页对象
     * @return 商品分页结果
     */
    default Page<Product> findBySize(String size, Pageable pageable) {
        return findBySpecifications(null, size, null, pageable);
    }

    /**
     * 📋 根据商品品牌查找商品列表
     *
     * 使用JSONB路径查询，查询指定品牌的所有商品。
     * 这是一个便捷方法，内部调用findBySpecifications。
     *
     * @param brand 品牌
     * @param pageable 分页对象
     * @return 商品分页结果
     */
    default Page<Product> findByBrand(String brand, Pageable pageable) {
        return findBySpecifications(null, null, brand, pageable);
    }

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
     * 📋 查找指定颜色的商品数量
     *
     * 使用JSONB路径查询，统计指定颜色的商品数量。
     * 用于颜色管理和统计报表。
     *
     * @param color 商品颜色
     * @return 指定颜色的商品数量
     */
    @Query(value = "SELECT COUNT(p) FROM Product p WHERE p.productData->'specifications'->>'color' = :color", nativeQuery = true)
    Long countByColor(@Param("color") String color);

    /**
     * 📋 查找所有颜色及其商品数量
     *
     * 使用JSONB路径查询，统计所有颜色及其商品数量。
     * 用于颜色筛选和统计报表。
     *
     * @return 颜色统计结果
     */
    @Query(value = "SELECT p.productData->'specifications'->>'color' as color, COUNT(p) as count " +
           "FROM Product p WHERE p.productData->'specifications'->>'color' IS NOT NULL " +
           "GROUP BY p.productData->'specifications'->>'color' ORDER BY count DESC", nativeQuery = true)
    List<Object[]> countAllColors();

    /**
     * 📋 查找指定品牌的商品数量
     *
     * 使用JSONB路径查询，统计指定品牌的商品数量。
     * 用于品牌管理和统计报表。
     *
     * @param brand 商品品牌
     * @return 指定品牌的商品数量
     */
    @Query(value = "SELECT COUNT(p) FROM Product p WHERE p.productData->'specifications'->>'brand' = :brand", nativeQuery = true)
    Long countByBrand(@Param("brand") String brand);

    /**
     * 📋 查找所有品牌及其商品数量
     *
     * 使用JSONB路径查询，统计所有品牌及其商品数量。
     * 用于品牌筛选和统计报表。
     *
     * @return 品牌统计结果
     */
    @Query(value = "SELECT p.productData->'specifications'->>'brand' as brand, COUNT(p) as count " +
           "FROM Product p WHERE p.productData->'specifications'->>'brand' IS NOT NULL " +
           "GROUP BY p.productData->'specifications'->>'brand' ORDER BY count DESC", nativeQuery = true)
    List<Object[]> countAllBrands();

    /**
     * 📋 查找指定尺寸的商品数量
     *
     * 使用JSONB路径查询，统计指定尺寸的商品数量。
     * 用于尺寸管理和统计报表。
     *
     * @param size 商品尺寸
     * @return 指定尺寸的商品数量
     */
    @Query(value = "SELECT COUNT(p) FROM Product p WHERE p.productData->'specifications'->>'size' = :size", nativeQuery = true)
    Long countBySize(@Param("size") String size);

    /**
     * 📋 查找所有尺寸及其商品数量
     *
     * 使用JSONB路径查询，统计所有尺寸及其商品数量。
     * 用于尺寸筛选和统计报表。
     *
     * @return 尺寸统计结果
     */
    @Query(value = "SELECT p.productData->'specifications'->>'size' as size, COUNT(p) as count " +
           "FROM Product p WHERE p.productData->'specifications'->>'size' IS NOT NULL " +
           "GROUP BY p.productData->'specifications'->>'size' ORDER BY count DESC", nativeQuery = true)
    List<Object[]> countAllSizes();

    /**
     * 📋 查找指定价格范围的商品数量
     *
     * 统计价格在指定范围内的商品数量。
     * 用于价格区间分析和市场调研。
     *
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 指定价格范围的商品数量
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Long countByPriceBetween(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    /**
     * 📋 查找指定折扣范围的商品数量
     *
     * 统计折扣率在指定范围内的商品数量。
     * 用于促销活动分析和效果评估。
     *
     * @param minDiscount 最低折扣率
     * @param maxDiscount 最高折扣率
     * @return 指定折扣范围的商品数量
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.discount BETWEEN :minDiscount AND :maxDiscount")
    Long countByDiscountBetween(@Param("minDiscount") BigDecimal minDiscount, @Param("maxDiscount") BigDecimal maxDiscount);

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
}