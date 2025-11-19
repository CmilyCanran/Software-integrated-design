---
tags:
  - JPA
  - 实体映射
  - Spring Boot
  - 数据库设计
  - 注解
created: 2025-11-19
modified: 2025-11-19
category: 数据库技术
difficulty: intermediate
---

# 02-JPA核心技术

> **学习目标**: 掌握JPA实体映射和关系设计，实现简单的数据库CRUD操作

## 🎯 本章概览

**学习时间**: 60-75分钟 | **难度等级**: ⭐⭐⭐ | **重点程度**: 🔥🔥

JPA（Java Persistence API）是Spring Boot中处理数据库操作的核心技术。本章将教你如何使用JPA注解将Java对象映射到数据库表，为简单CRUD操作奠定基础。

---

## 📋 核心需求

### 🎯 业务目标
- 将Java类映射为数据库表
- 定义字段类型和约束
- 实现实体之间的基本关系
- 为CRUD操作提供数据模型

### 🛠️ 技术需求
- JPA核心注解使用
- 实体类设计原则
- 字段映射配置
- 基础关系映射

---

## 🏗️ JPA核心概念

### 🎯 实体 (Entity) 映射

JPA通过注解将Java类映射到数据库表：

```java
@Entity                          // 🎯 标记为JPA实体
@Table(name = "users")           // 📊 指定数据库表名
public class User {

    @Id                          // 🔑 主键标识
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 🚀 自增策略
    private Long id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;     // 👤 用户名字段

    @Column(name = "email", unique = true, nullable = false)
    private String email;        // 📧 邮箱字段

    @Column(name = "password", nullable = false)
    private String password;     // 🔐 密码字段

    @Column(name = "role", nullable = false)
    private String role = "USER"; // 👥 用户角色（默认值）

    @CreationTimestamp           // 🕐 自动创建时间
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp           // 🕐 自动更新时间
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 构造函数、getter、setter...
}
```

### 📊 常用JPA注解

| 注解 | 用途 | 常用属性 |
|------|------|----------|
| `@Entity` | 标记实体类 | name |
| `@Table` | 指定表信息 | name, schema, indexes |
| `@Id` | 标记主键 | - |
| `@GeneratedValue` | 主键生成策略 | strategy, generator |
| `@Column` | 字段映射 | name, nullable, length, unique |
| `@CreationTimestamp` | 自动创建时间 | - |
| `@UpdateTimestamp` | 自动更新时间 | - |

---

## 💻 实战：用户实体设计

### 1️⃣ 基础用户实体

```java
package com.cmliy.springweb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_username", columnList = "username"),
    @Index(name = "idx_email", columnList = "email")
})
public class User {

    // 🔑 主键字段
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 👤 用户名字段
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    // 📧 邮箱字段
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    // 🔐 密码字段
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6位")
    @Column(name = "password", nullable = false)
    private String password;

    // 👥 用户角色
    @Column(name = "role", nullable = false)
    private String role = "USER";

    // 🕐 创建时间
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    // 🕐 更新时间
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 🏗️ 默认构造函数
    public User() {}

    // 📝 带参构造函数
    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getter和Setter方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // 📝 toString方法（调试用）
    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", role='" + role + '\'' +
               ", createdAt=" + createdAt +
               '}';
    }
}
```

### 2️⃣ 商品实体设计

```java
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_name", columnList = "name"),
    @Index(name = "idx_category", columnList = "category"),
    @Index(name = "idx_price", columnList = "price")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "商品名称不能为空")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @DecimalMin(value = "0.0", message = "价格不能为负数")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Min(value = 0, message = "库存不能为负数")
    @Column(name = "stock", nullable = false)
    private Integer stock = 0;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 构造函数和getter/setter...
}
```

---

## 🔗 基础关系映射

### 1️⃣ 一对多关系

```java
// 👤 用户实体（一方）
@Entity
public class User {
    // ... 其他字段

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    // 便捷方法
    public void addOrder(Order order) {
        orders.add(order);
        order.setUser(this);
    }
}

// 📦 订单实体（多方）
@Entity
public class Order {
    // ... 其他字段

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
```

### 2️⃣ 多对多关系

```java
// 👤 用户实体
@Entity
public class User {
    // ... 其他字段

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}

// 🎭 角色实体
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();
}
```

---

## 🧪 JPA配置与测试

### 1️⃣ JPA配置优化

```yaml
# 🎯 application.yml - JPA配置
spring:
  jpa:
    hibernate:
      ddl-auto: update           # 开发环境使用update，生产使用validate
    show-sql: true               # 显示SQL语句
    properties:
      hibernate:
        format_sql: true         # 格式化SQL
        dialect: org.hibernate.dialect.MySQL8Dialect
        use_sql_comments: true   # 添加SQL注释
        jdbc:
          batch_size: 20         # 批处理大小
        order_inserts: true      # 优化插入顺序
        order_updates: true      # 优化更新顺序
```

### 2️⃣ 实体创建测试

```java
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class EntityMappingTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    @Order(1)
    public void testCreateUser() {
        // 🎯 创建用户实体
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedpassword");
        user.setRole("USER");

        // 💾 保存到数据库
        entityManager.persist(user);
        entityManager.flush();

        // ✅ 验证保存结果
        assertNotNull(user.getId());
        System.out.println("✅ 用户创建成功: " + user);
    }

    @Test
    @Order(2)
    public void testCreateProduct() {
        // 🎯 创建商品实体
        Product product = new Product();
        product.setName("测试商品");
        product.setDescription("这是一个测试商品");
        product.setPrice(new BigDecimal("99.99"));
        product.setStock(100);
        product.setCategory("服装");

        // 💾 保存到数据库
        entityManager.persist(product);
        entityManager.flush();

        // ✅ 验证保存结果
        assertNotNull(product.getId());
        System.out.println("✅ 商品创建成功: " + product);
    }

    @Test
    @Order(3)
    public void testQueryUser() {
        // 🔍 查询用户
        User user = entityManager.find(User.class, 1L);
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());

        System.out.println("✅ 用户查询成功: " + user);
    }
}
```

---

## 🚀 常见问题与解决方案

### ❓ 问题1: 表名和字段名映射错误

**错误**: `Table 'test.user' doesn't exist`

**解决方案**:
```java
@Entity
@Table(name = "users")  // 明确指定表名
public class User {
    @Column(name = "username")  // 明确指定字段名
    private String username;
}
```

### ❓ 问题2: 字段类型不匹配

**错误**: `Data type mismatch`

**解决方案**:
```java
@Column(name = "price", precision = 10, scale = 2)
private BigDecimal price;  // 使用BigDecimal处理金额

@Column(name = "stock")
private Integer stock;     // 使用Integer处理数量
```

### ❓ 问题3: 实体序列化问题

**错误**: 循环引用导致无限递归

**解决方案**:
```java
@Entity
public class User {
    @OneToMany(mappedBy = "user")
    @JsonIgnore  // 忽略序列化
    private List<Order> orders;
}
```

---

## 📊 实体设计最佳实践

### ✅ 推荐做法

1. **📝 使用明确的表名和字段名**
2. **🔐 敏感字段不要序列化**
3. **🕐 使用时间戳审计字段**
4. **📏 设置合理的字段长度**
5. **🔍 添加必要的索引**
6. **✅ 使用Bean Validation注解**

### ❌ 避免做法

1. **🚫 使用默认构造函数进行业务初始化**
2. **🚫 在实体中包含业务逻辑**
3. **🚫 过度使用EAGER加载**
4. **🚫 忽略equals和hashCode方法**

---

## 📝 本章小结

### ✅ 已掌握技能

- [ ] **理解** JPA实体映射原理
- [ ] **能够** 设计基础实体类
- [ ] **掌握** 常用JPA注解
- [ ] **了解** 基础关系映射
- [ ] **能够** 测试实体映射

### 🎯 关键要点

1. **实体映射** - 使用注解将Java类映射到数据库表
2. **字段配置** - 合理设置字段类型和约束
3. **关系映射** - 理解一对一、一对多、多对多关系
4. **最佳实践** - 遵循实体设计规范

### 🚀 下一步学习

现在你已经掌握了JPA实体映射，接下来可以学习：
- → **03-数据访问层Repository** - 学习如何操作这些实体
- → **04-高级查询技术** - 掌握复杂查询方法
- → **实战项目** - 开始实现具体的CRUD功能

---

**记住：好的实体设计是数据操作的基础！** 🎉

---