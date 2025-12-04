package com.cmliy.springweb.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

import com.cmliy.springweb.dto.CartResponseDTO;
import com.cmliy.springweb.dto.ProductResponseDTO;
import com.cmliy.springweb.model.Cart;
import com.cmliy.springweb.model.Product;
import com.cmliy.springweb.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartConverter extends BaseConverter<Cart,CartResponseDTO> {
    private final ProductRepository productRepository;
    private final ProductConverter productConverter;
    @Override
    public CartResponseDTO toDTO(Cart cart) {
        return safeConvert(cart, c -> {
            if(isNull(c)) {
                return null;
            }
            //keySet()获取所有键，用于获取所有productID
            List<Long> productIds = new ArrayList<>(c.getCartData().keySet());
            //批量查询
            List<Product> products = productRepository.findAllById(productIds);

            /**
             * 1. products.stream()
             *
             * products 是一个集合，是 List<Product> 类型。
             * .stream() 方法将这个集合转换为一个流（Stream）。
             * 流是 Java 8 引入的核心概念，它允许你以声明性方式处理数据集合
             * （类似于数据库的 SQL 查询），可以执行非常高效的聚合操作、批量处理
             * 或并行处理。
             * 2. .map(productConverter::toDTO)
             *
             * 这是整个流程的转换核心。
             * .map() 是流的一个中间操作。它的作用是：
             * 将流中的每一个元素，通过给定的函数，映射（转换）为另一个元素。
             * productConverter::toDTO 是一个方法引用，
             * 它等价于 Lambda 表达式 product -> productConverter.toDTO(product)。
             * 所以，这一步的含义是：
             * 将流中的每一个 Product 对象，传入 productConverter.toDTO() 方法，
             * 并将其返回的 ProductResponseDTO 对象放入新的流中。
             * 经过 .map() 操作后，
             * 流中的元素类型已经从 Product 变成了 ProductResponseDTO。
             * 3. .collect(Collectors.toList())
             *
             * 这是流的终止操作。它意味着处理结束，我们需要一个最终的结果。
             * .collect() 是一个收集器，负责将流中的元素汇总成一个结果。
             * Collectors.toList() 是一个静态工厂方法，
             * 它告诉收集器：“请把流中的所有元素收集到一个新的 List 集合里”。
             * 因为上一步的流已经是 ProductResponseDTO 类型，
             * 所以这里收集到的就是一个 List<ProductResponseDTO>。
             * 4. List<ProductResponseDTO> productDTOs = ...
             *
             * 最后，将这个新生成的 List<ProductResponseDTO>
             * 赋值给变量 productDTOs。
             */
            List<ProductResponseDTO> productDTOs = products.stream()
                    .map(productConverter::toDTO)
                    .collect(Collectors.toList());
            return CartResponseDTO.builder()
                    .userId(c.getUser().getId())
                    .productQuantities(new HashMap<>(c.getCartData()))  // 数量映射
                    .products(new ArrayList<>(productDTOs))            // 完整商品信息
                    .build();

        }, "购物车");
    }
    /**
     *  创建空的购物车响应
     *
     * 用于创建购物车时的响应
     *
     * @param userId 用户ID
     * @return 空的购物车DTO
     */
    public CartResponseDTO createEmptyCartResponse(Long userId) {
        return CartResponseDTO.builder()
                .userId(userId)
                .productQuantities(new HashMap<>())
                .products(new ArrayList<>())
                .build();
    }
    /**
     * 🔄 批量转换购物车列表
     *
     * @param carts 购物车实体列表
     * @return 购物车DTO列表
     */
    public List<CartResponseDTO> toDTOList(List<Cart> carts) {
        return safeConvertList(carts, this::toDTO, "购物车列表");
    }

    @Override
    protected Class<CartResponseDTO> getDTOClass() {
        return CartResponseDTO.class;
    }

    @Override
    protected Class<Cart> getEntityClass() {
        return Cart.class;
    }
    public Cart toEntity(CartResponseDTO dto) {
        // 前端不通过DTO创建Cart实体,注册时自动创建
        // 这里可以抛出异常或返回null
        throw new UnsupportedOperationException("购物车不支持通过DTO创建实体");
    }
}
