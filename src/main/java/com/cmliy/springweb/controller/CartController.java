package com.cmliy.springweb.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;

import com.cmliy.springweb.service.CartService;
import com.cmliy.springweb.util.JwtUtil;
import com.cmliy.springweb.common.ApiResponse;
import com.cmliy.springweb.dto.*;
import com.cmliy.springweb.exception.BusinessException;
import com.cmliy.springweb.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/cart")
public class CartController extends BaseController {
    private final CartService cartService;

    public CartController(UserRepository userRepository,
                    JwtUtil jwtUtil,
                    CartService cartService) {
        super(userRepository, jwtUtil);  
        this.cartService = cartService;
    }

    @GetMapping("/get")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartResponseDTO>> getCart() {
        try {
            Long userId = getCurrentUserId();  // 从JWT获取用户ID
            CartResponseDTO cart = cartService.getCartByUserId(userId);
            return success(cart, "获取购物车成功");
        } catch (BusinessException e) {
            log.warn("获取购物车失败: {}", e.getMessage());
            return error(400, e.getMessage());
        } catch (Exception e) {
            log.error("获取购物车失败: {}", e.getMessage(), e);
            return error(500, "获取购物车失败");
        }
    }

    /**
     * ➕ 添加商品到购物车
     * POST /api/cart/items
     */
    @PostMapping("/items")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartResponseDTO>> addToCart(
            @RequestBody CartAddDTO request) {
        try {
            Long userId = getCurrentUserId();
            CartResponseDTO cart = cartService.addToCart(userId, request);
            return success(cart, "商品添加成功");
        } catch (BusinessException e) {
            log.warn("添加商品失败: {}", e.getMessage());
            return error(400, e.getMessage());
        } catch (Exception e) {
            log.error("添加商品失败: {}", e.getMessage(), e);
            return error(500, "添加商品失败");
        }
    }

    /**
     * 🔄 更新购物车商品数量
     * PUT /api/cart/items
     */
    @PutMapping("/items")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartResponseDTO>> updateCartItem(
            @RequestBody CartUpdateDTO request) {
        try {
            Long userId = getCurrentUserId();
            CartResponseDTO cart = cartService.updateCartItem(userId, request);
            return success(cart, "商品数量更新成功");
        } catch (BusinessException e) {
            log.warn("更新商品数量失败: {}", e.getMessage());
            return error(400, e.getMessage());
        } catch (Exception e) {
            log.error("更新商品数量失败: {}", e.getMessage(), e);
            return error(500, "更新商品数量失败");
        }
    }

    /**
     * ❌ 从购物车删除商品
     * DELETE /api/cart/items/{productId}
     */
    @DeleteMapping("/items/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartResponseDTO>> removeFromCart(
            @PathVariable Long productId) {
        try {
            Long userId = getCurrentUserId();
            CartResponseDTO cart = cartService.removeFromCart(userId, productId);
            return success(cart, "商品删除成功");
        } catch (BusinessException e) {
            log.warn("删除商品失败: {}", e.getMessage());
            return error(400, e.getMessage());
        } catch (Exception e) {
            log.error("删除商品失败: {}", e.getMessage(), e);
            return error(500, "删除商品失败");
        }
    }

    /**
     * 🗑️ 清空购物车
     * DELETE /api/cart
     */
    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartResponseDTO>> clearCart() {
        try {
            Long userId = getCurrentUserId();
            CartResponseDTO cart = cartService.clearCart(userId);
            return success(cart, "购物车已清空");
        } catch (BusinessException e) {
            log.warn("清空购物车失败: {}", e.getMessage());
            return error(400, e.getMessage());
        } catch (Exception e) {
            log.error("清空购物车失败: {}", e.getMessage(), e);
            return error(500, "清空购物车失败");
        }
    }

}

