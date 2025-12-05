package com.cmliy.springweb.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmliy.springweb.model.Cart;
import com.cmliy.springweb.model.Order;
import com.cmliy.springweb.model.Product;
import com.cmliy.springweb.model.User;
import com.cmliy.springweb.repository.OrderRepository;
import com.cmliy.springweb.repository.ProductRepository;
import com.cmliy.springweb.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 📋 订单业务逻辑层
 *
 * 处理订单相关的业务逻辑，包括创建、查询、状态更新等
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final CartService cartService;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * 🛒 创建单个商品订单
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 购买数量
     * @return 创建的订单
     */
    @Transactional
    public Order createOrder(Long userId, Long productId, Integer quantity) {
        log.info("🛒 创建订单: userId={}, productId={}, quantity={}", userId, productId, quantity);

        // 1. 验证用户存在
        var userDTO = userService.getUserById(userId);
        if (userDTO == null) {
            throw new IllegalArgumentException("用户不存在: " + userId);
        }

        // 2. 获取商品信息
        var productDTO = productService.getProductById(productId).orElse(null);
        if (productDTO == null) {
            throw new IllegalArgumentException("商品不存在: " + productId);
        }

        // 3. 验证商品可购买性
        if (!productDTO.getIsAvailable() || productDTO.getStockQuantity() <= 0) {
            throw new IllegalArgumentException("商品不可购买: " + productDTO.getProductName());
        }

        // 4. 验证库存充足
        if (productDTO.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("库存不足: 需要 " + quantity + "，库存 " + productDTO.getStockQuantity());
        }

        // 5. 获取商品实体（用于库存扣减）
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new IllegalArgumentException("商品信息不存在");
        }

        // 6. 获取商家信息
        User seller = product.getCreator();
        if (seller == null) {
            throw new IllegalArgumentException("商品商家信息不存在");
        }

        // 7. 获取完整用户实体 (修复数据一致性问题 - 不再使用手动构建的User对象)
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("用户信息不存在");
        }

        // 8. 计算价格（使用商品当前价格作为快照）
        BigDecimal unitPrice = calculateDiscountedPrice(productDTO.getPrice(), productDTO.getDiscount());
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));

        // 9. 创建订单
        Order order = Order.builder()
                .user(user)
                .product(product)
                .seller(seller)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .totalAmount(totalAmount)
                .status(Order.OrderStatus.PENDING)
                .build();

        // 10. 保存订单
        order = orderRepository.save(order);

        // 11. 扣减库存
        boolean stockDecreased = product.decreaseStock(quantity);
        if (!stockDecreased) {
            throw new RuntimeException("库存扣减失败，请重试");
        }

        log.info("✅ 订单创建成功: orderId={}, totalAmount={}", order.getId(), order.getTotalAmount());
        return order;
    }

    /**
     * 🛒 从购物车创建订单（为每个商品创建独立订单）
     *
     * @param userId 用户ID
     * @return 创建的订单列表
     */
    @Transactional
    public List<Order> createOrdersFromCart(Long userId) {
        log.info("🛒 从购物车创建订单: userId={}", userId);

        // 1. 获取用户购物车
        var cartResponse = cartService.getCartByUserId(userId);
        if (cartResponse.getProductQuantities() == null || cartResponse.getProductQuantities().isEmpty()) {
            throw new IllegalArgumentException("购物车为空");
        }

        // 2. 遍历购物车商品，为每个商品创建订单
        List<Order> orders = cartResponse.getProductQuantities().entrySet().stream()
                .map(entry -> createOrder(userId, Long.valueOf(entry.getKey()), entry.getValue()))
                .toList();

        // 3. 清空购物车
        cartService.clearCart(userId);

        log.info("✅ 从购物车创建订单成功: userId={}, orderCount={}", userId, orders.size());
        return orders;
    }

    /**
     * 🔍 根据ID获取订单
     */
    public Order getOrderById(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        return order.orElse(null);
    }

    /**
     * 🔍 根据ID获取订单（如果不存在则抛出异常）
     */
    public Order getRequiredOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在: " + orderId));
    }

    /**
     * 📋 获取用户的所有订单
     */
    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 📋 分页获取用户订单
     */
    public Page<Order> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * 📋 获取商家的所有订单
     */
    public List<Order> getSellerOrders(Long sellerId) {
        return orderRepository.findBySellerIdOrderByCreatedAtDesc(sellerId);
    }

    /**
     * 📋 分页获取商家订单
     */
    public Page<Order> getSellerOrders(Long sellerId, Pageable pageable) {
        return orderRepository.findBySellerIdOrderByCreatedAtDesc(sellerId, pageable);
    }

    /**
     * 📊 根据状态获取用户订单
     */
    public List<Order> getUserOrdersByStatus(Long userId, String status) {
        return orderRepository.findByUserIdAndStatus(userId, status);
    }

    /**
     * 📊 分页获取用户订单按状态
     */
    public Page<Order> getUserOrdersByStatus(Long userId, String status, Pageable pageable) {
        return orderRepository.findByUserIdAndStatus(userId, status, pageable);
    }

    /**
     * 📊 根据状态获取商家订单
     */
    public List<Order> getSellerOrdersByStatus(Long sellerId, String status) {
        return orderRepository.findBySellerIdAndStatus(sellerId, status);
    }

    /**
     * 📊 分页获取商家订单按状态
     */
    public Page<Order> getSellerOrdersByStatus(Long sellerId, String status, Pageable pageable) {
        return orderRepository.findBySellerIdAndStatus(sellerId, status, pageable);
    }

    /**
     * 🔄 更新订单状态
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, String newStatus, Long operatorId) {
        Order order = getRequiredOrderById(orderId);

        // 验证状态转换是否合法
        validateStatusTransition(order.getStatus(), newStatus);

        // 更新状态
        order.updateStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        log.info("🔄 订单状态更新: orderId={}, oldStatus={}, newStatus={}, operatorId={}",
                orderId, order.getStatus(), newStatus, operatorId);

        return updatedOrder;
    }

    /**
     * ❌ 取消订单
     */
    @Transactional
    public Order cancelOrder(Long orderId, Long userId) {
        Order order = getRequiredOrderById(orderId);

        // 验证订单归属
        if (!order.belongsToUser(userId)) {
            throw new IllegalArgumentException("无权操作此订单");
        }

        // 验证订单是否可以取消
        if (!order.canCancel()) {
            throw new IllegalArgumentException("订单无法取消，当前状态: " + order.getStatusDescription());
        }

        // 更新订单状态
        order.updateStatus(Order.OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);

        // 恢复库存
        Product product = order.getProduct();
        product.increaseStock(order.getQuantity());

        log.info("❌ 订单取消成功: orderId={}, userId={}, restoredQuantity={}",
                orderId, userId, order.getQuantity());

        return updatedOrder;
    }

    /**
     * 📊 获取订单统计信息
     */
    public OrderStatistics getOrderStatistics(Long userId) {
        long totalOrders = orderRepository.countByUserId(userId);
        long pendingOrders = orderRepository.countByUserIdAndStatus(userId, Order.OrderStatus.PENDING);
        long completedOrders = orderRepository.countByUserIdAndStatus(userId, Order.OrderStatus.COMPLETED);
        BigDecimal totalAmount = orderRepository.sumTotalAmountByUserId(userId);

        return new OrderStatistics(totalOrders, pendingOrders, completedOrders, totalAmount);
    }

    /**
     * 🔍 检查用户是否购买过指定商品
     */
    public boolean hasUserPurchasedProduct(Long userId, Long productId) {
        return orderRepository.findByUserIdAndProductId(userId, productId).isPresent();
    }

    /**
     * ✅ 验证订单状态转换是否合法
     */
    private void validateStatusTransition(String currentStatus, String newStatus) {
        // 这里可以添加更复杂的状态转换规则
        if (!Order.OrderStatus.isValidStatus(newStatus)) {
            throw new IllegalArgumentException("无效的订单状态: " + newStatus);
        }

        // 例如：已完成的订单不能取消
        if (Order.OrderStatus.COMPLETED.equals(currentStatus) &&
            Order.OrderStatus.CANCELLED.equals(newStatus)) {
            throw new IllegalArgumentException("已完成的订单不能取消");
        }
    }

    /**
     * 💰 计算折扣价格
     */
    private BigDecimal calculateDiscountedPrice(BigDecimal price, BigDecimal discount) {
        if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
            return price.multiply(BigDecimal.ONE.subtract(discount.divide(BigDecimal.valueOf(100))));
        }
        return price;
    }

    /**
     * 📊 订单统计信息DTO
     */
    public record OrderStatistics(
            long totalOrders,      // 总订单数
            long pendingOrders,    // 待处理订单数
            long completedOrders,  // 已完成订单数
            BigDecimal totalAmount // 总金额
    ) {}
}