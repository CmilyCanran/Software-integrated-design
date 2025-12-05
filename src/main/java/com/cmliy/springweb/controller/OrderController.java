package com.cmliy.springweb.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cmliy.springweb.common.ApiResponse;
import com.cmliy.springweb.model.Order;
import com.cmliy.springweb.service.OrderService;
import com.cmliy.springweb.service.OrderSecurityService;
import com.cmliy.springweb.repository.UserRepository;
import com.cmliy.springweb.util.JwtUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 📋 订单控制器
 *
 * 提供订单相关的RESTful API接口
 */
@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController extends BaseController {

    private final OrderService orderService;
    private final OrderSecurityService orderSecurityService;

    public OrderController(UserRepository userRepository, JwtUtil jwtUtil, OrderService orderService, OrderSecurityService orderSecurityService) {
        super(userRepository, jwtUtil);
        this.orderService = orderService;
        this.orderSecurityService = orderSecurityService;
    }

    /**
     * 🛒 创建单个商品订单
     */
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Order>> createOrder(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        try {
            Long userId = getCurrentUserId();
            Order order = orderService.createOrder(userId, productId, quantity);
            return ResponseEntity.ok(ApiResponse.success(order, "订单创建成功"));
        } catch (Exception e) {
            log.error("创建订单失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("订单创建失败: " + e.getMessage(), 400));
        }
    }

    /**
     * 🛒 从购物车创建订单
     */
    @PostMapping("/create-from-cart")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<Order>>> createOrdersFromCart() {

        try {
            Long userId = getCurrentUserId();
            List<Order> orders = orderService.createOrdersFromCart(userId);
            return ResponseEntity.ok(ApiResponse.success(orders, "订单创建成功，共创建 " + orders.size() + " 个订单"));
        } catch (Exception e) {
            log.error("从购物车创建订单失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("订单创建失败: " + e.getMessage(), 400));
        }
    }

    /**
     * 🔍 获取订单详情
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable Long orderId) {

        try {
            Long userId = getCurrentUserId();
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }

            // 使用OrderSecurityService进行权限验证
            if (!orderSecurityService.canViewOrder(userId, orderId)) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("无权查看此订单", 403));
            }

            return ResponseEntity.ok(ApiResponse.success(order, "获取订单详情成功"));
        } catch (Exception e) {
            log.error("获取订单详情失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取订单详情失败: " + e.getMessage(), 400));
        }
    }

    /**
     * 📋 获取用户订单列表
     */
    @GetMapping("/my-orders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {

        try {
            Long userId = getCurrentUserId();
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

            Page<Order> orderPage;
            if (status != null && !status.trim().isEmpty()) {
                orderPage = orderService.getUserOrdersByStatus(userId, status, pageable);
            } else {
                orderPage = orderService.getUserOrders(userId, pageable);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("orders", orderPage.getContent());
            response.put("currentPage", orderPage.getNumber());
            response.put("totalPages", orderPage.getTotalPages());
            response.put("totalElements", orderPage.getTotalElements());
            response.put("hasNext", orderPage.hasNext());
            response.put("hasPrevious", orderPage.hasPrevious());

            return ResponseEntity.ok(ApiResponse.success(response, "获取订单列表成功"));
        } catch (Exception e) {
            log.error("获取用户订单失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取订单列表失败: " + e.getMessage(), 400));
        }
    }

    /**
     * 📋 获取商家订单列表
     */
    @GetMapping("/seller-orders")
    @PreAuthorize("hasRole('SHOPER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSellerOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {

        try {
            Long userId = getCurrentUserId();
            // 验证用户是否为商家
            var user = userRepository.findById(userId).orElse(null);
            if (user == null || (!"SHOPER".equals(user.getRole()) && !"ADMIN".equals(user.getRole()))) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("无权限访问商家订单", 403));
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

            Page<Order> orderPage;
            if (status != null && !status.trim().isEmpty()) {
                orderPage = orderService.getSellerOrdersByStatus(userId, status, pageable);
            } else {
                orderPage = orderService.getSellerOrders(userId, pageable);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("orders", orderPage.getContent());
            response.put("currentPage", orderPage.getNumber());
            response.put("totalPages", orderPage.getTotalPages());
            response.put("totalElements", orderPage.getTotalElements());
            response.put("hasNext", orderPage.hasNext());
            response.put("hasPrevious", orderPage.hasPrevious());

            return ResponseEntity.ok(ApiResponse.success(response, "获取商家订单列表成功"));
        } catch (Exception e) {
            log.error("获取商家订单失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取商家订单列表失败: " + e.getMessage(), 400));
        }
    }

    /**
     * 🔄 更新订单状态
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String newStatus) {

        try {
            Long userId = getCurrentUserId();
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }

            // 使用OrderSecurityService进行权限验证
            if (!orderSecurityService.canUpdateOrderStatus(userId, orderId, newStatus)) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("无权修改此订单状态", 403));
            }

            Order updatedOrder = orderService.updateOrderStatus(orderId, newStatus, userId);
            return ResponseEntity.ok(ApiResponse.success(updatedOrder, "订单状态更新成功"));
        } catch (Exception e) {
            log.error("更新订单状态失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("订单状态更新失败: " + e.getMessage(), 400));
        }
    }

    /**
     * ❌ 取消订单
     */
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(@PathVariable Long orderId) {

        try {
            Long userId = getCurrentUserId();
            // 使用OrderSecurityService进行权限验证
            if (!orderSecurityService.canCancelOrder(userId, orderId)) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("无权取消此订单", 403));
            }

            Order cancelledOrder = orderService.cancelOrder(orderId, userId);
            return ResponseEntity.ok(ApiResponse.success(cancelledOrder, "订单取消成功"));
        } catch (Exception e) {
            log.error("取消订单失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("订单取消失败: " + e.getMessage(), 400));
        }
    }

    /**
     * 📊 获取订单统计信息
     */
    @GetMapping("/statistics")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<OrderService.OrderStatistics>> getOrderStatistics() {

        try {
            Long userId = getCurrentUserId();
            var statistics = orderService.getOrderStatistics(userId);
            return ResponseEntity.ok(ApiResponse.success(statistics, "获取订单统计成功"));
        } catch (Exception e) {
            log.error("获取订单统计失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取订单统计失败: " + e.getMessage(), 400));
        }
    }

    /**
     * 🔍 检查商品购买状态
     */
    @GetMapping("/check-purchase/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkPurchaseStatus(@PathVariable Long productId) {

        try {
            Long userId = getCurrentUserId();
            boolean hasPurchased = orderService.hasUserPurchasedProduct(userId, productId);

            Map<String, Object> result = new HashMap<>();
            result.put("hasPurchased", hasPurchased);
            result.put("productId", productId);

            return ResponseEntity.ok(ApiResponse.success(result, "检查购买状态成功"));
        } catch (Exception e) {
            log.error("检查购买状态失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("检查购买状态失败: " + e.getMessage(), 400));
        }
    }
}