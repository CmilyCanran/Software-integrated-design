package com.cmliy.springweb.service;

import com.cmliy.springweb.model.Order;
import com.cmliy.springweb.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 🛡️ 订单安全服务
 *
 * 专门处理订单相关的权限验证逻辑
 */
@Service
@RequiredArgsConstructor
public class OrderSecurityService {

    private final OrderRepository orderRepository;

    /**
     * 🔍 检查用户是否有权限查看订单
     */
    public boolean canViewOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return false;
        }
        // 用户是订单的买家或卖家
        return order.belongsToUser(userId) || order.belongsToSeller(userId);
    }

    /**
     * 🔍 检查用户是否有权限取消订单
     */
    public boolean canCancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return false;
        }
        // 只有订单的买家可以取消订单
        return order.belongsToUser(userId);
    }

    /**
     * 🔍 检查用户是否有权限更新订单状态
     */
    public boolean canUpdateOrderStatus(Long userId, Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return false;
        }

        // 根据订单状态转换判断权限
        String currentStatus = order.getStatus();

        // PENDING → PAID: 允许买家支付
        if ("PENDING".equals(currentStatus) && "PAID".equals(newStatus)) {
            return order.belongsToUser(userId);
        }
        // PAID → SHIPPED: 允许卖家发货
        else if ("PAID".equals(currentStatus) && "SHIPPED".equals(newStatus)) {
            return order.belongsToSeller(userId);
        }
        // SHIPPED → COMPLETED: 允许买家确认收货
        else if ("SHIPPED".equals(currentStatus) && "COMPLETED".equals(newStatus)) {
            return order.belongsToUser(userId);
        }
        // PENDING → CANCELLED: 允许买家取消订单
        else if ("PENDING".equals(currentStatus) && "CANCELLED".equals(newStatus)) {
            return order.belongsToUser(userId);
        }
        // PAID → CANCELLED: 允许买家取消订单（在发货前）
        else if ("PAID".equals(currentStatus) && "CANCELLED".equals(newStatus)) {
            return order.belongsToUser(userId);
        }
        // 其他情况（如管理员操作）可以保留原有逻辑
        else if ("COMPLETED".equals(currentStatus) || "CANCELLED".equals(currentStatus)) {
            // 已完成或已取消的订单不允许再修改状态
            return false;
        }
        // 默认情况下仍然只允许卖家操作
        else {
            return order.belongsToSeller(userId);
        }
    }
}