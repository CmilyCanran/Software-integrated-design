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
    public boolean canUpdateOrderStatus(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return false;
        }
        // 只有订单的卖家可以更新状态
        return order.belongsToSeller(userId);
    }
}