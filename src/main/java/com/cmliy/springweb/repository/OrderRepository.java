package com.cmliy.springweb.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cmliy.springweb.model.Order;

/**
 * 📋 订单数据访问层
 *
 * 提供订单相关的数据库操作方法
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 🔍 根据用户ID查找订单
     */
    List<Order> findByUserId(Long userId);

    /**
     * 🔍 根据用户ID分页查找订单
     */
    Page<Order> findByUserId(Long userId, Pageable pageable);

    /**
     * 🔍 根据商家ID查找订单
     */
    List<Order> findBySellerId(Long sellerId);

    /**
     * 🔍 根据商家ID分页查找订单
     */
    Page<Order> findBySellerId(Long sellerId, Pageable pageable);

    /**
     * 🔍 根据订单状态查找订单
     */
    List<Order> findByStatus(String status);

    /**
     * 🔍 根据用户ID和订单状态查找订单
     */
    List<Order> findByUserIdAndStatus(Long userId, String status);

    /**
     * 🔍 根据用户ID和订单状态分页查找订单
     */
    Page<Order> findByUserIdAndStatus(Long userId, String status, Pageable pageable);

    /**
     * 🔍 根据商家ID和订单状态查找订单
     */
    List<Order> findBySellerIdAndStatus(Long sellerId, String status);

    /**
     * 🔍 根据商家ID和订单状态分页查找订单
     */
    Page<Order> findBySellerIdAndStatus(Long sellerId, String status, Pageable pageable);

    /**
     * 🔍 根据商品ID查找订单
     */
    List<Order> findByProductId(Long productId);

    /**
     * 🔍 根据用户ID和商品ID查找订单（检查是否已购买）
     */
    Optional<Order> findByUserIdAndProductId(Long userId, Long productId);

    /**
     * 📊 统计用户订单总数
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    /**
     * 📊 统计商家订单总数
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.seller.id = :sellerId")
    long countBySellerId(@Param("sellerId") Long sellerId);

    /**
     * 📊 统计用户指定状态的订单数量
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId AND o.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 📊 统计商家指定状态的订单数量
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.seller.id = :sellerId AND o.status = :status")
    long countBySellerIdAndStatus(@Param("sellerId") Long sellerId, @Param("status") String status);

    /**
     * 💰 计算用户订单总金额
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.user.id = :userId")
    BigDecimal sumTotalAmountByUserId(@Param("userId") Long userId);

    /**
     * 💰 计算商家订单总金额
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.seller.id = :sellerId")
    BigDecimal sumTotalAmountBySellerId(@Param("sellerId") Long sellerId);

    /**
     * 🔍 查找用户最近的订单
     */
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 🔍 查找用户最近的订单（分页）
     */
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 🔍 查找商家最近的订单
     */
    List<Order> findBySellerIdOrderByCreatedAtDesc(Long sellerId);

    /**
     * 🔍 查找商家最近的订单（分页）
     */
    Page<Order> findBySellerIdOrderByCreatedAtDesc(Long sellerId, Pageable pageable);

    /**
     * 🔍 查找指定时间范围内的订单
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt >= :startDate AND o.createdAt <= :endDate")
    List<Order> findByDateRange(@Param("startDate") java.time.LocalDateTime startDate,
                               @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * 🔍 查找用户指定时间范围内的订单
     */
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.createdAt >= :startDate AND o.createdAt <= :endDate")
    List<Order> findByUserIdAndDateRange(@Param("userId") Long userId,
                                        @Param("startDate") java.time.LocalDateTime startDate,
                                        @Param("endDate") java.time.LocalDateTime endDate);
}