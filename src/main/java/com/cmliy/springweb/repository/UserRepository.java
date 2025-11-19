// package: Java包声明，用于组织类和避免命名冲突
package com.cmliy.springweb.repository;

// import: 导入其他包中的类，以便在当前类中使用
import com.cmliy.springweb.model.User;                    // 导入用户实体类
import org.springframework.data.jpa.repository.JpaRepository; // 导入Spring Data JPA基础Repository接口
import org.springframework.stereotype.Repository;             // 导入Spring Repository注解

import java.util.Optional;                                  // 导入Java 8 Optional容器类

/**
 * 👤 用户数据访问层
 *
 * 这个接口继承自Spring Data JPA的JpaRepository，提供用户数据的CRUD操作。
 * Spring Data JPA会根据方法名自动生成SQL查询，无需手写SQL语句。
 *
 * Repository模式的优势：
 * - 封装数据访问逻辑
 * - 提供类型安全的操作
 * - 支持方法名查询
 * - 可集成分页和排序
 *
 * @Repository: Spring框架注解，标记这是一个数据访问层组件。
 */
@Repository // @Repository注解：声明这是一个Repository类，Spring会自动管理其生命周期
public interface UserRepository extends JpaRepository<User, Long> { // extends: 继承父接口，获得父接口的所有方法

    // ===== 查询方法 =====
    // Spring Data JPA会根据方法名自动生成查询语句

    /**
     * 🔍 根据用户名查找用户
     *
     * 方法名解析：findBy + Username = 根据用户名查找
     * Spring Data JPA会自动生成：SELECT * FROM users WHERE username = ?
     *
     * @param username: 用户名，查询条件
     * @return Optional<User>: 可能包含User对象的容器，如果找不到则为空
     */
    Optional<User> findByUsername(String username); // 根据用户名查询用户

    /**
     * 🔍 根据邮箱查找用户
     *
     * 方法名解析：findBy + Email = 根据邮箱查找
     * Spring Data JPA会自动生成：SELECT * FROM users WHERE email = ?
     *
     * @param email: 邮箱地址，查询条件
     * @return Optional<User>: 可能包含User对象的容器，如果找不到则为空
     */
    Optional<User> findByEmail(String email); // 根据邮箱查询用户

    /**
     * 🔍 根据用户名或邮箱查找用户
     *
     * 方法名解析：findBy + UsernameOrEmail = 根据用户名或邮箱查找
     * Spring Data JPA会自动生成：SELECT * FROM users WHERE username = ? OR email = ?
     *
     * @param username: 用户名，查询条件之一
     * @param email: 邮箱地址，查询条件之二
     * @return Optional<User>: 可能包含User对象的容器，如果找不到则为空
     */
    Optional<User> findByUsernameOrEmail(String username, String email); // 根据用户名或邮箱查询用户

    // ===== 存在性检查方法 =====
    // 这些方法返回boolean值，用于检查记录是否存在

    /**
     * 🔍 检查用户名是否存在
     *
     * 方法名解析：exists + By + Username = 根据用户名检查是否存在
     * Spring Data JPA会自动生成：SELECT COUNT(*) > 0 FROM users WHERE username = ?
     *
     * @param username: 用户名，检查条件
     * @return boolean: true表示存在，false表示不存在
     */
    boolean existsByUsername(String username); // 检查用户名是否存在

    /**
     * 🔍 检查邮箱是否存在
     *
     * 方法名解析：exists + By + Email = 根据邮箱检查是否存在
     * Spring Data JPA会自动生成：SELECT COUNT(*) > 0 FROM users WHERE email = ?
     *
     * @param email: 邮箱地址，检查条件
     * @return boolean: true表示存在，false表示不存在
     */
    boolean existsByEmail(String email); // 检查邮箱是否存在

    // ===== 统计方法 =====
    // 这些方法返回long值，用于统计记录数量

    /**
     * 📊 统计指定角色的用户数量
     *
     * 方法名解析：count + By + Role = 根据角色统计数量
     * Spring Data JPA会自动生成：SELECT COUNT(*) FROM users WHERE role = ?
     *
     * @param role: 用户角色，统计条件
     * @return long: 符合条件的用户数量
     */
    long countByRole(String role); // 统计指定角色的用户数量

    /**
     * 📊 统计启用的用户数量
     *
     * 方法名解析：count + By + Enabled + True = 根据启用状态统计数量
     * Spring Data JPA会自动生成：SELECT COUNT(*) FROM users WHERE enabled = true
     *
     * @return long: 启用的用户数量
     */
    long countByEnabledTrue(); // 统计启用的用户数量

    // ===== 继承的方法 =====
    // 从JpaRepository<User, Long>继承的方法包括：
    // - save(User): 保存或更新用户
    // - findById(Long): 根据ID查找用户
    // - findAll(): 查找所有用户
    // - deleteById(Long): 根据ID删除用户
    // - count(): 统计所有用户数量
    // - existsById(Long): 检查ID是否存在
}