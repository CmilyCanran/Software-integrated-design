// package: Java包声明，用于组织类和避免命名冲突
package com.cmliy.springweb.model;

// import: 导入其他包中的类，以便在当前类中使用
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;                           // 导入JPA（Jakarta Persistence API）所有注解
import org.hibernate.annotations.CreationTimestamp;    // 导入Hibernate创建时间戳注解
import org.hibernate.annotations.UpdateTimestamp;      // 导入Hibernate更新时间戳注解
import lombok.Data;                                     // 导入Lombok @Data注解
import lombok.Builder;                                  // 导入Lombok @Builder注解
import lombok.NoArgsConstructor;                       // 导入Lombok @NoArgsConstructor注解
import lombok.AllArgsConstructor;                      // 导入Lombok @AllArgsConstructor注解

import java.time.LocalDateTime;                         // 导入Java 8日期时间类，用于处理日期和时间

/**
 * 👤 用户实体模型
 *
 * 这个类使用JPA（Jakarta Persistence API）注解映射到数据库的users表。
 * 实体类是ORM（对象关系映射）的核心，实现了Java对象与数据库记录的双向转换。
 *
 * ORM工作原理：
 * 1. JPA根据注解自动生成SQL语句
 * 2. Hibernate执行数据库操作
 * 3. Java对象与数据库记录自动转换
 * 4. 开发者只需操作Java对象，无需编写SQL
 *
 * 数据库设计说明：
 * - 表名：users（复数形式，符合命名约定）
 * - 主键：id（Long类型，自增）
 * - 索引：username和email（提高查询性能）
 * - 时间戳：自动管理创建和更新时间
 *
 * JPA主要注解：
 * - @Entity: 标记类为JPA实体，告诉JPA这是一个需要管理的实体
 * - @Table: 指定数据库表名、索引等元数据信息
 * - @Id: 标记主键字段，唯一标识数据库记录
 * - @Column: 定义列属性（名称、约束、长度等）
 * - @GeneratedValue: 配置主键生成策略
 * - @CreationTimestamp/@UpdateTimestamp: Hibernate时间戳注解
 */
@Data                                      // @Data注解：Lombok自动生成getter、setter、toString、equals、hashCode
@Builder                                   // @Builder注解：Lombok支持Builder模式创建对象
@NoArgsConstructor                         // @NoArgsConstructor注解：Lombok生成无参构造函数
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@AllArgsConstructor                        // @AllArgsConstructor注解：Lombok生成全参构造函数
@Entity // @Entity注解：声明这是一个JPA实体类，Hibernate会自动管理其数据库映射
@Table(name = "users", indexes = { // @Table注解：定义数据库表的元数据
    @Index(name = "idx_username", columnList = "username"), // 创建用户名索引：提高按用户名查询的性能
    @Index(name = "idx_email", columnList = "email")        // 创建邮箱索引：提高按邮箱查询的性能
    // 索引作用：数据库索引就像书籍的目录，大幅提高查询速度
})
public class User { // public class: 定义公共类，其他类可以访问

    /**
     * 🔑 用户主键ID
     *
     * 使用数据库自增策略，确保每个用户都有唯一的标识符。
     * 主键是数据库表中最重要的字段，用于唯一标识每条记录。
     *
     * @Id: JPA注解，标记这个字段为主键
     * @GeneratedValue: JPA注解，配置主键生成策略
     * GenerationType.IDENTITY: 使用数据库自增功能（MySQL、PostgreSQL等）
     */
    @Id // @Id注解：标记这个字段为主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // @GeneratedValue注解：配置主键自增策略
    private Long id; // id: 用户ID，Long类型，数据库自动递增

    /**
     * 👤 用户名
     *
     * 用户的登录名称，必须唯一且不能为空。
     * 设置了唯一约束，防止重复注册。
     *
     * @Column: JPA注解，定义数据库列属性
     * name = "username": 数据库列名
     * unique = true: 唯一约束，确保用户名不重复
     * nullable = false: 非空约束，必须提供值
     * length = 50: 字段长度限制，最多50个字符
     */
    @Column(name = "username", unique = true, nullable = false, length = 50) // @Column注解：定义数据库列属性
    private String username; // username: 用户名，String类型，唯一且不能为空

    /**
     * 📧 用户邮箱地址
     *
     * 用于用户找回密码、接收通知等功能。
     * 同样设置了唯一约束，防止一个邮箱注册多个账户。
     *
     * @Column: JPA注解，定义数据库列属性
     * name = "email": 数据库列名
     * unique = true: 唯一约束，确保邮箱不重复
     * nullable = false: 非空约束，必须提供值
     * length = 100: 字段长度限制，最多100个字符
     */
    @Column(name = "email", unique = true, nullable = false, length = 100) // @Column注解：定义数据库列属性
    private String email; // email: 邮箱地址，String类型，唯一且不能为空

    /**
     * 🔒 用户密码
     *
     * 存储经过BCrypt加密后的密码哈希值。
     * 重要：明文密码永远不会存储在数据库中！
     *
     * @Column: JPA注解，定义数据库列属性
     * name = "password": 数据库列名
     * nullable = false: 非空约束，必须提供值
     */
    @Column(name = "password", nullable = false) // @Column注解：定义数据库列属性
    private String password; // password: 用户密码，String类型，存储加密后的哈希值

    /**
     * 👑 用户角色
     *
     * 定义用户的权限级别：
     * - "USER": 普通用户，可以访问基本功能
     * - "ADMIN": 管理员用户，可以访问管理功能
     * - “SHOPER” 商家用户，可以访问商家界面
     * 默认值为"USER"，新注册用户都是普通用户。
     *
     * @Column: JPA注解，定义数据库列属性
     * name = "role": 数据库列名
     * nullable = false: 非空约束，必须提供值
     * length = 20: 字段长度限制，最多20个字符
     */
    @Column(name = "role", nullable = false, length = 20) // @Column注解：定义数据库列属性
    private String role = "USER"; // role: 用户角色，String类型，默认为"USER"

    /**
     * ✅ 账户启用状态
     *
     * 控制用户账户是否可以登录：
     * - true: 账户正常，可以登录和使用系统
     * - false: 账户被禁用，无法登录（软删除）
     * 默认值为true，新注册账户自动启用。
     *
     * @Column: JPA注解，定义数据库列属性
     * name = "enabled": 数据库列名
     * nullable = false: 非空约束，必须提供值
     */
    @Column(name = "enabled", nullable = false) // @Column注解：定义数据库列属性
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

}