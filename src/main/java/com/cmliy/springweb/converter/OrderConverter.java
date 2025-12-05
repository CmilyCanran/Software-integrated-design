package com.cmliy.springweb.converter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cmliy.springweb.dto.order.OrderDTO;
import com.cmliy.springweb.model.Order;

/**
 * 🔄 订单转换器
 *
 * 负责Order实体与OrderDTO之间的转换
 */
@Component
public class OrderConverter {

    /**
     * 🔄 将Order实体转换为OrderDTO
     */
    public OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .username(order.getUser() != null ? order.getUser().getUsername() : null)
                .productId(order.getProduct() != null ? order.getProduct().getId() : null)
                .productName(order.getProduct() != null ? order.getProduct().getProductName() : null)
                .productDescription(order.getProduct() != null ? order.getProduct().getDescription() : null)
                .productImage(order.getProduct() != null ? order.getProduct().getMainImage() : null)
                .sellerId(order.getSeller() != null ? order.getSeller().getId() : null)
                .sellerName(order.getSeller() != null ? order.getSeller().getUsername() : null)
                .quantity(order.getQuantity())
                .unitPrice(order.getUnitPrice())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .statusDescription(order.getStatusDescription())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    /**
     * 🔄 将OrderDTO转换为Order实体
     */
    public Order toEntity(OrderDTO dto) {
        if (dto == null) {
            return null;
        }

        // 注意：这里只转换基本字段，关联对象需要在Service层处理
        Order order = new Order();
        order.setId(dto.getId());
        order.setQuantity(dto.getQuantity());
        order.setUnitPrice(dto.getUnitPrice());
        order.setTotalAmount(dto.getTotalAmount());
        order.setStatus(dto.getStatus());
        order.setCreatedAt(dto.getCreatedAt());
        order.setUpdatedAt(dto.getUpdatedAt());

        return order;
    }

    /**
     * 🔄 将Order实体列表转换为OrderDTO列表
     */
    public List<OrderDTO> toDTOList(List<Order> orders) {
        if (orders == null) {
            return null;
        }

        return orders.stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * 🔄 将OrderDTO列表转换为Order实体列表
     */
    public List<Order> toEntityList(List<OrderDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toEntity)
                .toList();
    }

    /**
     * 🔄 更新Order实体（不包含关联对象）
     */
    public void updateEntity(Order existingOrder, OrderDTO dto) {
        if (existingOrder == null || dto == null) {
            return;
        }

        // 只更新非关联字段
        existingOrder.setQuantity(dto.getQuantity());
        existingOrder.setUnitPrice(dto.getUnitPrice());
        existingOrder.setTotalAmount(dto.getTotalAmount());
        if (dto.getStatus() != null) {
            existingOrder.updateStatus(dto.getStatus());
        }
    }
}