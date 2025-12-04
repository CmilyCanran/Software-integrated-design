package com.cmliy.springweb.service;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmliy.springweb.dto.CartResponseDTO;
import com.cmliy.springweb.dto.CartUpdateDTO;
import com.cmliy.springweb.dto.CartAddDTO;

import com.cmliy.springweb.model.Cart;
import com.cmliy.springweb.model.User;
import com.cmliy.springweb.model.Product;

import com.cmliy.springweb.repository.CartRepository;
import com.cmliy.springweb.repository.UserRepository;
import com.cmliy.springweb.repository.ProductRepository;

import com.cmliy.springweb.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CartService extends BaseService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /**
     *  创建新购物车
     */
    private Cart createNewCart(User user) {
        Cart cart = Cart.builder()
            .user(user)
            .build();
        return cartRepository.save(cart);
    }
        /**
       * 获取或创建用户购物车
       */
    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser_Id(user.getId())
            .orElseGet(() -> createNewCart(user));
    }


    /**
     * 构建CartResponseDTO - 极简设计，前端负责所有计算
     *
     * 根据购物车数据构建响应DTO，仅包含：
     * - 用户ID（用于识别购物车归属）
     * - 商品数量映射（供前端计算）
     * 前端通过ProductController单独获取商品详细信息
     */
    private CartResponseDTO buildCartResponseDTO(Cart cart){
        if(cart == null){
            return null;
        }

        Map<Long, Integer> cartData = cart.getCartData();
        if (cartData == null) {
            cartData = new HashMap<>();
        }

        
        return CartResponseDTO.builder()
            .userId(cart.getUser().getId())
            .productQuantities(new HashMap<>(cartData))
            .build();
    }
    private Cart updateEntity(Cart cart,CartUpdateDTO dto){
        if(dto == null || dto.getProductQuantities().equals(cart.getCartData())){
            return cart;
        }
        cart.setCartData(dto.getProductQuantities());
        cartRepository.save(cart);
        return cart;
    }
    private Cart addDtoToCart(Cart cart ,CartAddDTO dto){
        if(dto == null){
            return cart;
        }
        // 使用Cart实体的辅助方法来更新商品数量
        // 直接调用updateItem方法，传入productId和quantity
        cart.updateItem(dto.getProductId(), dto.getProductQuantity());
        cartRepository.save(cart);
        return cart;
    }

    /**
     *  获取用户购物车
     */

    public CartResponseDTO getCartByUserId(Long userId) {
        return executeWithLog("获取购物车", () -> {
            User user = validateExists(userRepository.findById(userId),"用户", userId);
            Cart cart = getOrCreateCart(user);
            return buildCartResponseDTO(cart);
        },userId);
    }

    public boolean updateCart(CartUpdateDTO cartDto){
        if(cartDto == null){
            return false;
        }

        return executeWithLog("更新购物车",()->{
            Cart cart = getOrCreateCart(userRepository
                                .findById(
                                    cartDto
                                    .getUserId()
                                ).orElseGet(null)
                            );
            if(cart == null){
                return false;
            }
            updateEntity(cart,cartDto);
            return true;
        });
    }
    /**
     * ➕ 添加商品到购物车
     *
     * 极简设计，仅处理商品添加逻辑，前端负责价格和数量计算
     *
     * @param userId 用户ID
     * @param request 添加商品请求（仅包含商品ID和数量）
     * @return 更新后的购物车
     */
    public CartResponseDTO addToCart(Long userId, CartAddDTO request) {
        return executeWithLog("添加商品到购物车", () -> {
            // 验证请求参数
            if (request == null || request.getProductId() == null || request.getProductQuantity() == null) {
                throw new BusinessException("请求参数不完整");
            }
            if (request.getProductQuantity() <= 0 || request.getProductQuantity() > 999) {
                throw new BusinessException("商品数量必须在1-999之间");
            }

            // 获取用户购物车
            User user = validateExists(userRepository.findById(userId), "用户", userId);
            Cart cart = getOrCreateCart(user);

            // 添加商品到购物车
            cart = addDtoToCart(cart, request);
            cart = cartRepository.save(cart);

            // 返回更新后的购物车
            return buildCartResponseDTO(cart);
        });
    }

    /**
     * 🔄 更新购物车商品数量
     *
     * @param userId 用户ID
     * @param request 更新请求（包含商品ID和数量的映射）
     * @return 更新后的购物车
     */
    public CartResponseDTO updateCartItem(Long userId, CartUpdateDTO request) {
        return executeWithLog("更新购物车商品数量", () -> {
            // 验证请求参数
            if (request == null || request.getProductQuantities() == null) {
                throw new BusinessException("请求参数不完整");
            }
            if (!request.getUserId().equals(userId)) {
                throw new BusinessException("用户ID不匹配");
            }

            // 获取用户购物车
            User user = validateExists(userRepository.findById(userId), "用户", userId);
            Cart cart = getOrCreateCart(user);

            // 更新商品数量
            for (Map.Entry<Long, Integer> entry : request.getProductQuantities().entrySet()) {
                Long productId = entry.getKey();
                Integer quantity = entry.getValue();

                if (quantity == null || quantity <= 0 || quantity > 999) {
                    throw new BusinessException("商品数量必须在1-999之间");
                }

                // 直接设置商品数量（覆盖原有数量）
                cart.getCartData().put(productId, quantity);
            }

            // 保存购物车
            cart = cartRepository.save(cart);

            // 返回更新后的购物车
            return buildCartResponseDTO(cart);
        });
    }

    /**
     * ❌ 从购物车删除商品
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 更新后的购物车
     */
    public CartResponseDTO removeFromCart(Long userId, Long productId) {
        return executeWithLog("从购物车删除商品", () -> {
            // 验证参数
            if (productId == null) {
                throw new BusinessException("商品ID不能为空");
            }

            // 获取用户购物车
            User user = validateExists(userRepository.findById(userId), "用户", userId);
            Cart cart = getOrCreateCart(user);

            // 删除商品
            cart.removeItem(productId);

            // 保存购物车
            cart = cartRepository.save(cart);

            // 返回更新后的购物车
            return buildCartResponseDTO(cart);
        });
    }

    /**
     * 🗑️ 清空购物车
     *
     * @param userId 用户ID
     * @return 清空后的购物车
     */
    public CartResponseDTO clearCart(Long userId) {
        return executeWithLog("清空购物车", () -> {
            // 获取用户购物车
            User user = validateExists(userRepository.findById(userId), "用户", userId);
            Cart cart = getOrCreateCart(user);

            // 清空购物车
            cart.clearCart();

            // 保存购物车
            cart = cartRepository.save(cart);

            // 返回清空后的购物车
            return buildCartResponseDTO(cart);
        });
    }

    /**
     * 📊 获取购物车统计信息
     *
     * @param userId 用户ID
     * @return 购物车统计信息（暂时返回与获取购物车相同的信息）
     */
    public CartResponseDTO getCartStatistics(Long userId) {
        return executeWithLog("获取购物车统计", () -> {
            // 获取用户购物车
            User user = validateExists(userRepository.findById(userId), "用户", userId);
            Cart cart = getOrCreateCart(user);

            // 暂时返回与获取购物车相同的信息
            // 后续可根据需要添加统计功能，如总商品数、总价值等
            return buildCartResponseDTO(cart);
        });
    }

}
