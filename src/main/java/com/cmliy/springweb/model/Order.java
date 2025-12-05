package com.cmliy.springweb.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📋 订单实体
 *
 * 采用"一个商品一个订单"的设计模式，每个订单只包含一个商品
 * 支持完整的订单生命周期管理
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_product_id", columnList = "product_id"),
    @Index(name = "idx_seller_id", columnList = "seller_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
public class Order {

    /**
     * 🆔 订单唯一标识符
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 👤 买家用户
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 📦 订单商品
     */
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * 🏪 商家用户（商品创建者）
     */
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    /**
     * 🔢 购买数量
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * 💰 下单时的商品单价（价格快照）
     * 避免商品价格变动影响历史订单
     */
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    /**
     * 💵 订单总金额（数量 × 单价）
     */
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * 📊 订单状态
     */
    @Column(nullable = false, length = 20)
    private String status = OrderStatus.PENDING;

    /**
     * ⏰ 创建时间戳
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    /**
     * 🔄 更新时间戳
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ==================== 💰 业务逻辑方法 ====================

    /**
     * 📊 计算订单总金额
     */
    public void calculateTotalAmount() {
        if (quantity != null && unitPrice != null) {
            this.totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    /**
     * 🔄 更新订单状态
     */
    public void updateStatus(String newStatus) {
        if (OrderStatus.isValidStatus(newStatus)) {
            this.status = newStatus;
        } else {
            throw new IllegalArgumentException("无效的订单状态: " + newStatus);
        }
    }

    /**
     * ✅ 检查订单是否可以取消
     */
    public boolean canCancel() {
        return OrderStatus.PENDING.equals(this.status);
    }

    /**
     * ✅ 检查订单是否已完成
     */
    public boolean isCompleted() {
        return OrderStatus.COMPLETED.equals(this.status);
    }

    /**
     * 📊 获取状态描述
     */
    public String getStatusDescription() {
        return OrderStatus.getDescription(this.status);
    }

    /**
     * 🔍 检查订单是否属于指定用户
     */
    public boolean belongsToUser(Long userId) {
        return this.user != null && this.user.getId().equals(userId);
    }

    /**
     * 🔍 检查订单是否属于指定商家
     */
    public boolean belongsToSeller(Long sellerId) {
        return this.seller != null && this.seller.getId().equals(sellerId);
    }

    // ==================== 📋 订单状态常量 ====================

    /**
     * 📋 订单状态常量定义
     */
    public static class OrderStatus {
        public static final String PENDING = "PENDING";      // 待处理
        public static final String PAID = "PAID";            // 已支付
        public static final String SHIPPED = "SHIPPED";      // 已发货
        public static final String COMPLETED = "COMPLETED";  // 已完成
        public static final String CANCELLED = "CANCELLED";  // 已取消

        /**
         * ✅ 验证订单状态是否有效
         */
        public static boolean isValidStatus(String status) {
            return PENDING.equals(status) ||
                   PAID.equals(status) ||
                   SHIPPED.equals(status) ||
                   COMPLETED.equals(status) ||
                   CANCELLED.equals(status);
        }

        /**
         * 📊 获取状态描述
         */
        public static String getDescription(String status) {
            switch (status) {
                case PENDING:
                    return "待处理";
                case PAID:
                    return "已支付";
                case SHIPPED:
                    return "已发货";
                case COMPLETED:
                    return "已完成";
                case CANCELLED:
                    return "已取消";
                default:
                    return "未知状态";
            }
        }
    }
}