// package: Java包声明，用于组织类和避免命名冲突
package com.cmliy.springweb.model;

// import: 导入其他包中的类，以便在当前类中使用
import jakarta.persistence.*;                           // 导入JPA所有注解
import org.hibernate.annotations.CreationTimestamp;    // 导入Hibernate创建时间戳注解
import org.hibernate.annotations.UpdateTimestamp;      // 导入Hibernate更新时间戳注解

import java.time.LocalDateTime;                         // 导入Java 8日期时间类

/**
 * 👤 用户实体模型
 *
 * 这个类使用JPA（Java Persistence API）注解映射到数据库的users表。
 * 实体类是ORM（对象关系映射）的核心，将Java对象与数据库表进行映射。
 *
 * JPA注解说明：
 * - @Entity: 标记类为JPA实体
 * - @Table: 指定数据库表信息
 * - @Id: 标记主键字段
 * - @Column: 定义列属性
 * - @Index: 定义数据库索引
 */
@Entity // @Entity注解：声明这是一个JPA实体类，映射到数据库表
@Table(name = "users", indexes = { // @Table注解：定义表名和索引
    @Index(name = "idx_username", columnList = "username"), // 创建用户名索引，提高查询效率
    @Index(name = "idx_email", columnList = "email")        // 创建邮箱索引，提高查询效率
})
public class User { // public class: 定义公共类，其他类可以访问

    // @Id: JPA注解，标记这个字段为主键
    @Id // 主键注解：数据库表的主键字段
    // @GeneratedValue: 主键生成策略注解
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDENTITY: 使用数据库自增主键策略
    private Long id; // id: 用户ID，Long类型，自动递增

    // @Column: JPA注解，定义列属性
    @Column(name = "username", unique = true, nullable = false, length = 50) // 定义用户名列
    private String username; // username: 用户名，String类型，唯一且不能为空

    // @Column: 定义列属性
    @Column(name = "email", unique = true, nullable = false, length = 100) // 定义邮箱列
    private String email; // email: 邮箱地址，String类型，唯一且不能为空

    // @Column: 定义列属性
    @Column(name = "password", nullable = false) // 定义密码列
    private String password; // password: 用户密码，String类型，不能为空

    // @Column: 定义列属性
    @Column(name = "role", nullable = false, length = 20) // 定义角色列
    private String role = "USER"; // role: 用户角色，默认为"USER"

    // @Column: 定义列属性
    @Column(name = "enabled", nullable = false) // 定义启用状态列
    private Boolean enabled = true; // enabled: 账户启用状态，Boolean类型，默认为true

    // @CreationTimestamp: Hibernate注解，自动设置创建时间
    @CreationTimestamp // 创建时间戳注解：实体创建时自动设置当前时间
    // @Column: 定义列属性
    @Column(name = "created_at", updatable = false, nullable = false) // 定义创建时间列
    private LocalDateTime createdAt; // createdAt: 创建时间，LocalDateTime类型

    // @UpdateTimestamp: Hibernate注解，自动设置更新时间
    @UpdateTimestamp // 更新时间戳注解：实体更新时自动设置当前时间
    // @Column: 定义列属性
    @Column(name = "updated_at", nullable = false) // 定义更新时间列
    private LocalDateTime updatedAt; // updatedAt: 更新时间，LocalDateTime类型

    // 🏗️ 默认构造函数
    // JPA规范要求实体类必须有无参构造函数
    public User() { // 无参构造函数：JPA反射创建对象时使用
    }

    // 📝 带参构造函数
    // 便捷构造函数，用于快速创建用户对象
    public User(String username, String email, String password, String role) { // 带参构造函数
        this.username = username; // this.username: 引用当前对象的username字段
        this.email = email;       // this.email: 引用当前对象的email字段
        this.password = password; // this.password: 引用当前对象的password字段
        this.role = role;         // this.role: 引用当前对象的role字段
    }

    // ===== Getter方法 =====
    // Getter方法：用于获取私有字段的值，遵循JavaBean规范

    public Long getId() { return id; } // 获取用户ID
    public String getUsername() { return username; } // 获取用户名
    public String getEmail() { return email; } // 获取邮箱
    public String getPassword() { return password; } // 获取密码
    public String getRole() { return role; } // 获取角色
    public Boolean getEnabled() { return enabled; } // 获取启用状态
    public LocalDateTime getCreatedAt() { return createdAt; } // 获取创建时间
    public LocalDateTime getUpdatedAt() { return updatedAt; } // 获取更新时间

    // ===== Setter方法 =====
    // Setter方法：用于设置私有字段的值，遵循JavaBean规范

    public void setId(Long id) { this.id = id; } // 设置用户ID
    public void setUsername(String username) { this.username = username; } // 设置用户名
    public void setEmail(String email) { this.email = email; } // 设置邮箱
    public void setPassword(String password) { this.password = password; } // 设置密码
    public void setRole(String role) { this.role = role; } // 设置角色
    public void setEnabled(Boolean enabled) { this.enabled = enabled; } // 设置启用状态
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; } // 设置创建时间
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; } // 设置更新时间
}