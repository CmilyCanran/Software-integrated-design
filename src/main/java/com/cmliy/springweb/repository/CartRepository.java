package com.cmliy.springweb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // 导入Spring Data JPA基础Repository接口

import com.cmliy.springweb.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>{
    Optional<Cart> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
