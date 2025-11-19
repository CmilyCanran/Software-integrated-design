// ============================================================================
// 商品数据访问层 - Product Repository 接口
// ============================================================================

// package: Java包声明，用于组织类和避免命名冲突
package com.cmliy.springweb.repository;

// import: 导入其他包中的类，以便在当前类中使用
import com.cmliy.springweb.model.Product;                    // 导入Product实体类
import com.cmliy.springweb.model.User;                      // 导入User实体类
import org.springframework.data.jpa.repository.JpaRepository; // 导入Spring Data JPA基础Repository接口
import org.springframework.data.jpa.repository.Query;          // 导入JPA查询注解
import org.springframework.data.repository.query.Param;        // 导入查询参数注解
import org.springframework.stereotype.Repository;             // 导入Spring Repository注解

import java.util.List;                                      // 导入Java List接口，用于处理列表数据
import java.util.Optional;                                   // 导入Java Optional容器类，避免空指针异常
import java.time.LocalDateTime;                              // 导入Java 8日期时间类，用于处理日期和时间

/**
 * 📦 商品数据访问层接口
 *
 * 这个接口继承自Spring Data JPA的JpaRepository，提供商品数据的CRUD操作。
 * Spring Data JPA会自动实现这个接口，无需编写具体的SQL语句。
 *
 * JpaRepository继承关系：
 * JpaRepository -> PagingAndSortingRepository -> CrudRepository -> Repository
 * 提供了完整的分页、排序、增删改查功能。
 *
 * Spring Data JPA工作原理：
 * 1. 根据方法名自动生成SQL查询
 * 2. 支持分页和排序
 * 3. 提供事务管理
 * 4. 支持自定义查询注解
 *
 * @Repository: Spring框架注解，标记这是一个数据访问层组件，
 *              Spring容器会自动扫描并注册这个类为Bean。
 */
@Repository // @Repository注解：声明这是一个Repository接口
public interface ProductRepository extends JpaRepository<Product, Long> { // extends: 继承JpaRepository，获得完整的CRUD功能

    // ============================================================================
    // 🔥 自定义查询方法：Spring Data JPA根据方法名自动生成SQL
    // ============================================================================

    /**
     * 🔍 根据创建者查询商品列表
     *
     * 使用关联对象查询，Spring Data JPA会自动生成：
     * SELECT * FROM products WHERE creator_id = ?1
     *
     * @param creator 创建者用户对象
     * @return List<Product>: 该用户创建的所有商品列表
     */
    List<Product> findByCreator(User creator); // 根据创建者对象查询商品

    /**
     * 🔍 根据商品名称查询商品
     *
     * 自动生成SQL：
     * SELECT * FROM products WHERE product_name = ?1
     *
     * @param productName 商品名称
     * @return Optional<Product>: 可能为空的商品对象，避免空指针异常
     */
    Optional<Product> findByProductName(String productName); // 根据商品名称查询

    /**
     * 📊 检查商品名称是否已存在
     *
     * 自动生成SQL：
     * SELECT COUNT(*) > 0 FROM products WHERE product_name = ?1
     *
     * @param productName 要检查的商品名称
     * @return boolean: true表示存在，false表示不存在
     */
    boolean existsByProductName(String productName); // 检查商品名是否存在

    /**
     * 📈 统计用户创建的商品数量
     *
     * 使用关联对象查询，自动生成SQL：
     * SELECT COUNT(*) FROM products WHERE creator_id = ?1
     *
     * @param creator 创建者用户对象
     * @return long: 该用户创建的商品总数
     */
    long countByCreator(User creator); // 统计用户创建的商品数量

    /**
     * 🔍 根据创建者ID查询商品列表（便捷方法）
     *
     * 使用@Query注解自定义查询，通过ID查询关联对象
     *
     * @param creatorId 创建者用户ID
     * @return List<Product>: 该用户创建的所有商品列表
     */
    @Query("SELECT p FROM Product p WHERE p.creator.id = :creatorId")
    List<Product> findByCreatorId(@Param("creatorId") Long creatorId);

    /**
     * 📈 根据创建者ID统计商品数量（便捷方法）
     *
     * 使用@Query注解自定义统计查询
     *
     * @param creatorId 创建者用户ID
     * @return long: 该用户创建的商品总数
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.creator.id = :creatorId")
    long countByCreatorId(@Param("creatorId") Long creatorId);

    /**
     * 🔍 根据价格区间查询商品
     *
     * 自动生成SQL：
     * SELECT * FROM products WHERE price BETWEEN ?1 AND ?2
     *
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return List<Product>: 价格区间内的商品列表
     */
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice); // 根据价格区间查询

    /**
     * 🔍 查询指定价格以上的商品
     *
     * 自动生成SQL：
     * SELECT * FROM products WHERE price >= ?1
     *
     * @param price 最低价格
     * @return List<Product]: 价格以上的商品列表
     */
    List<Product> findByPriceGreaterThanEqual(Double price); // 查询价格以上的商品

    // ============================================================================
    // 🆕 新增属性查询方法：支持商品状态和库存管理
    // ============================================================================

    /**
     * 🔍 查询已上架商品
     *
     * 自动生成SQL：
     * SELECT * FROM products WHERE is_available = true
     *
     * @return List<Product>: 所有已上架的商品列表
     */
    List<Product> findByIsAvailableTrue(); // 查询已上架商品

    /**
     * 🔍 查询已下架商品
     *
     * 自动生成SQL：
     * SELECT * FROM products WHERE is_available = false
     *
     * @return List<Product>: 所有已下架的商品列表
     */
    List<Product> findByIsAvailableFalse(); // 查询已下架商品

    /**
     * 🔍 查询有库存的商品
     *
     * 自动生成SQL：
     * SELECT * FROM products WHERE stock_quantity > 0
     *
     * @return List<Product>: 所有有库存的商品列表
     */
    List<Product> findByStockQuantityGreaterThan(Integer quantity); // 查询库存大于指定数量的商品

    /**
     * 🔍 查询缺货商品
     *
     * 自动生成SQL：
     * SELECT * FROM products WHERE stock_quantity = 0
     *
     * @return List<Product]: 所有缺货的商品列表
     */
    List<Product> findByStockQuantityEquals(Integer quantity); // 查询库存等于指定数量的商品

    /**
     * 🔍 查询可购买商品（已上架且有库存）
     *
     * 使用@Query注解自定义复杂查询，同时满足上架和库存条件
     *
     * @return List<Product]: 所有可购买的商品列表
     */
    @Query("SELECT p FROM Product p WHERE p.isAvailable = true AND p.stockQuantity > 0")
    List<Product> findPurchasableProducts(); // 查询可购买商品

    /**
     * 🔍 查询指定创建时间之后的商品
     *
     * 自动生成SQL：
     * SELECT * FROM products WHERE created_at > ?1
     *
     * @param createdAt 创建时间阈值
     * @return List<Product]: 指定时间之后创建的商品列表
     */
    List<Product> findByCreatedAtAfter(LocalDateTime createdAt); // 查询指定时间之后创建的商品

    /**
     * 🔍 查询指定时间范围内创建的商品
     *
     * 自动生成SQL：
     * SELECT * FROM products WHERE created_at BETWEEN ?1 AND ?2
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return List<Product]: 指定时间范围内创建的商品列表
     */
    List<Product> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime); // 查询指定时间范围内创建的商品

    /**
     * 📊 统计已上架商品数量
     *
     * 自动生成SQL：
     * SELECT COUNT(*) FROM products WHERE is_available = true
     *
     * @return long: 已上架商品总数
     */
    long countByIsAvailableTrue(); // 统计已上架商品数量

    /**
     * 📊 统计可购买商品数量（已上架且有库存）
     *
     * 使用@Query注解自定义统计查询
     *
     * @return long: 可购买商品总数
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.isAvailable = true AND p.stockQuantity > 0")
    long countPurchasableProducts(); // 统计可购买商品数量

    /**
     * 📊 统计缺货商品数量
     *
     * 自动生成SQL：
     * SELECT COUNT(*) FROM products WHERE stock_quantity = 0
     *
     * @return long: 缺货商品总数
     */
    long countByStockQuantityEquals(Integer quantity); // 统计库存等于指定数量的商品数量

    // ============================================================================
    // 🎯 继承自JpaRepository的方法（无需定义，直接可用）：
    // ============================================================================

    // 基础CRUD操作：
    // Product save(Product entity)                    - 保存或更新商品
    // Optional<Product> findById(Long id)            - 根据ID查找商品
    // List<Product> findAll()                        - 查找所有商品
    // void deleteById(Long id)                        - 根据ID删除商品
    // void delete(Product entity)                     - 删除商品实体
    // boolean existsById(Long id)                     - 检查ID是否存在
    // long count()                                    - 统计商品总数

    // 分页和排序操作：
    // Page<Product> findAll(Pageable pageable)        - 分页查询
    // List<Product> findAll(Sort sort)                - 排序查询
    // List<Product> findAllById(Iterable<Long> ids)  - 根据ID列表查询
}