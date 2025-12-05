package com.cmliy.springweb.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import com.cmliy.springweb.enums.OrderStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * 🛒 订单实体
 *
 * 订单系统核心实体，管理用户的购买订单信息
 * 主键设计：用户ID + "-" + 时间戳 (如：1234567890-1701234567890)
 */
@Data                                      // @Data注解：Lombok自动生成getter、setter、toString、equals、hashCode
@Builder                                   // @Builder注解：Lombok支持Builder模式创建对象
@NoArgsConstructor                         // @NoArgsConstructor注解：Lombok生成无参构造函数
@AllArgsConstructor                        // @AllArgsConstructor注解：Lombok生成全参构造函数
@Entity                                    // @Entity注解：声明这是一个JPA实体类，Hibernate会自动管理其数据库映射
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_user_id", columnList = "user_id"),
    @Index(name = "idx_order_number", columnList = "order_number"),
    @Index(name = "idx_order_status", columnList = "status"),
    @Index(name = "idx_order_created_at", columnList = "created_at"),
    @Index(name = "idx_order_user_created", columnList = "user_id, created_at")
})
public class Order {

    /**
     * 🔑 订单主键ID
     * 格式：用户ID + "-" + 时间戳 (如：1234567890-1701234567890)
     */
    @Id
    @Column(name = "order_number", length = 50)
    private String orderNumber;

    /**
     * 👤 订单所属用户
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 📊 订单状态
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /**
     * 💰 订单总价
     */
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * 📦 商品列表 - JSONB格式
     * 存储格式：Map<Long, Integer> (商品ID → 数量)
     */
    @Column(name = "order_items")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<Long, Integer> orderItems;

    /**
     * 📝 订单备注
     */
    @Column(name = "remarks", length = 500)
    private String remarks;

    /**
     * ⏰ 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    /**
     * 🔄 更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ==================== 💼 业务逻辑方法 ====================

    /**
     * 🛒 获取商品列表
     * 确保orderItems不为null
     */
    public Map<Long, Integer> getOrderItems() {
        if (orderItems == null) {
            orderItems = new HashMap<>();
        }
        return orderItems;
    }

    /**
     * 📊 获取订单商品总数
     * @return 订单中所有商品的数量总和
     */
    public int getTotalItemCount() {
        return getOrderItems().values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * 🔍 检查订单状态
     * @param status 要检查的状态
     * @return true如果订单状态匹配
     */
    public boolean isStatus(OrderStatus status) {
        return this.status.equals(status.name());
    }

    /**
     * 📈 更新订单状态
     * @param newStatus 新的订单状态
     */
    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus.name();
    }

    /**
     * 🔍 检查是否可以取消
     * @return true如果订单可以取消
     */
    public boolean canCancel() {
        OrderStatus currentStatus = OrderStatus.valueOf(this.status);
        return currentStatus.canCancel();
    }

    /**
     * 🔍 检查是否已完成
     * @return true如果订单已完成（包括完成、取消、退款等终态）
     */
    public boolean isCompleted() {
        OrderStatus currentStatus = OrderStatus.valueOf(this.status);
        return currentStatus.isFinalStatus();
    }

    /**
     * 🔍 检查是否可以支付
     * @return true如果订单可以支付
     */
    public boolean canPay() {
        return OrderStatus.PENDING.name().equals(this.status);
    }

    /**
     * 🔍 检查是否可以发货
     * @return true如果订单可以发货
     */
    public boolean canShip() {
        OrderStatus currentStatus = OrderStatus.valueOf(this.status);
        return currentStatus.canShip();
    }

    /**
     * 🔍 检查是否可以完成
     * @return true如果订单可以完成
     */
    public boolean canComplete() {
        OrderStatus currentStatus = OrderStatus.valueOf(this.status);
        return currentStatus.canComplete();
    }

    /**
     * 🔍 检查是否可以退款
     * @return true如果订单可以退款
     */
    public boolean canRefund() {
        OrderStatus currentStatus = OrderStatus.valueOf(this.status);
        return currentStatus.canRefund();
    }
}