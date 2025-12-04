package com.cmliy.springweb.model;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;                              
import jakarta.persistence.JoinColumn;                     
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                                      // @Data注解：Lombok自动生成getter、setter、toString、equals、hashCode
@Builder                                   // @Builder注解：Lombok支持Builder模式创建对象
@NoArgsConstructor                         // @NoArgsConstructor注解：Lombok生成无参构造函数
@AllArgsConstructor                        // @AllArgsConstructor注解：Lombok生成全参构造函数
@Entity // @Entity注解：声明这是一个JPA实体类，Hibernate会自动管理其数据库映射
@Table(name = "carts",
        indexes = {
            @Index(name = "idx_userid", columnList = "user_id")
        })
public class Cart {

    @Id
    private Long id;  // 与User的主键相同

    @MapsId  // 表示这个主键同时作为外键关联到User
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "cart_data")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<Long, Integer> cartData = new HashMap<>();

    public void removeItem(Long productId) {
        cartData.remove(productId);
    }

    public boolean updateItem(Long productId, Integer quantity) {
        if (cartData.containsKey(productId)) {
            // 商品已存在：数量累加 为负报错 为0删除
            Integer theQuantity = cartData.get(productId) + quantity;
            if (theQuantity > 0) {
                cartData.put(productId, theQuantity);
            } else if (theQuantity < 0) {
                return false;
            } else {
                removeItem(productId);
            }
        } else {
            if (quantity > 0) { // 商品不存在：直接放入
                cartData.put(productId, quantity);
            }
            return false;
        }
        return true;
    }

    public int getCartSize() {
        return cartData.size();
    }

    public void clearCart() {
        cartData.clear();
    }

}
