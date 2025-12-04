package com.cmliy.springweb.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmliy.springweb.dto.CartResponseDTO;
import com.cmliy.springweb.dto.CartUpdateDTO;
import com.cmliy.springweb.dto.CartAddDTO;

import com.cmliy.springweb.model.Cart;
import com.cmliy.springweb.model.User;

import com.cmliy.springweb.repository.CartRepository;
import com.cmliy.springweb.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CartService extends BaseService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

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
     * 构建CartResponseDTO
     */
    private CartResponseDTO buildCartResponseDTO(Cart cart){
        if(cart == null){
            return null;
        }
        // 构建响应
        return CartResponseDTO.builder()
            .userId(cart.getUser().getId())
            .productQuantities(new HashMap<>(cart.getCartData()))
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
        Map<Long, Integer> map = cart.getCartData();
        Long key =dto.getProductId();
        if(map.containsKey(key)){
            map.put( key , ( map.get(key) + dto.getProductQuantity() ) );
        }
        map.put(key,dto.getProductQuantity());
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
    public boolean addToCart(CartAddDTO cartDto){
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
            addDtoToCart(cart,cartDto);
            return true;
        });
    }
    
}
