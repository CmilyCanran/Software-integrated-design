// package: Java包声明，用于组织类和避免命名冲突
package com.cmliy.springweb.security;

// import: 导入其他包中的类，以便在当前类中使用
import com.cmliy.springweb.model.User;              // 导入用户实体类
import com.cmliy.springweb.repository.UserRepository; // 导入用户数据访问接口
import org.springframework.beans.factory.annotation.Autowired; // 导入Spring依赖注入注解
import org.springframework.security.core.authority.SimpleGrantedAuthority; // 导入Spring Security权限类
import org.springframework.security.core.userdetails.UserDetails; // 导入Spring Security用户详情接口
import org.springframework.security.core.userdetails.UserDetailsService; // 导入Spring Security用户详情服务接口
import org.springframework.security.core.userdetails.UsernameNotFoundException; // 导入用户名未找到异常类
import org.springframework.stereotype.Service;         // 导入Spring服务层注解

import java.util.Collections;                         // 导入Java集合工具类

/**
 * 👤 自定义用户详情服务
 *
 * 这个类实现了Spring Security的UserDetailsService接口，
 * 用于从数据库加载用户信息，供Spring Security进行认证和授权。
 *
 * @Service: Spring框架注解，标记这是一个服务层组件，
 *         Spring容器会自动扫描并注册这个类为Bean。
 */
@Service // @Service注解：声明这是一个Spring服务类，Spring会自动管理其生命周期
public class CustomUserDetailsService implements UserDetailsService { // implements: 实现接口，必须提供接口中所有方法的实现

    // ===== 依赖注入的字段 =====
    // 使用final字段和构造函数注入，这是Spring Boot推荐的最佳实践
    // 优势：1. 保证不可变性 2. 支持单元测试 3. 避免字段注入的潜在问题

    /**
     * 🗄️ 用户数据访问层接口
     *
     * Spring Data JPA的Repository接口，提供用户数据的CRUD操作。
     * 用于根据用户名查询用户信息。
     *
     * final关键字：表示这个字段一旦初始化就不能再修改，确保线程安全和不可变性
     */
    private final UserRepository userRepository; // userRepository: 用户数据访问层接口，用于数据库操作

    /**
     * 🏗️ 构造函数注入
     *
     * 使用构造函数进行依赖注入是Spring Boot推荐的最佳实践。
     * Spring容器会自动调用这个构造函数并传入所需的Bean实例。
     *
     * 构造函数注入的优势：
     * 1. 不可变性：依赖对象在构造后无法修改
     * 2. 测试友好：单元测试时可以轻松传入Mock对象
     * 3. 明确依赖：所有依赖关系在构造函数中一目了然
     * 4. 避免空指针：保证依赖对象在对象创建时就已经初始化
     *
     * 在服务层中使用构造函数注入特别重要，因为服务类通常包含业务逻辑
     *
     * @param userRepository Spring Data JPA用户Repository接口
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        // this关键字：引用当前对象的字段，区分同名的参数和字段
        this.userRepository = userRepository; // 将传入的用户Repository赋值给当前对象的字段
    }

    /**
     * 🔍 根据用户名加载用户详情
     *
     * 这是UserDetailsService接口的核心方法，Spring Security在用户认证时会调用此方法。
     * 这个方法是Spring Security认证流程的关键入口点。
     *
     * 认证流程说明：
     * 1. 用户提交用户名和密码
     * 2. Spring Security调用此方法获取用户详情
     * 3. 比对用户输入的密码与数据库中的加密密码
     * 4. 如果密码匹配，创建认证成功的用户对象
     *
     * @Override: 注解表示这个方法重写了父类或接口的方法，确保方法签名正确
     * @param username: String类型，用户名，来自登录表单或JWT令牌解析
     * @return UserDetails: Spring Security的用户详情对象，包含用户信息和权限列表
     * @throws UsernameNotFoundException: 当用户不存在时抛出此异常，Spring Security会处理
     */
    @Override // 重写注解：确保正确实现了接口方法
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { // public方法：公开访问，可能抛出用户名未找到异常

        // 🗄️ 第一步：从数据库查询用户信息
        // userRepository.findByUsername(): Spring Data JPA自动生成的查询方法
        // 根据方法名自动生成SQL：SELECT * FROM users WHERE username = ?
        // Optional<User>: Java 8引入的容器类，优雅地处理可能为null的值
        User user = userRepository.findByUsername(username) // 调用Repository方法查询用户
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
                // .orElseThrow(): Optional的方法，如果值不存在则抛出指定异常
                // Lambda表达式: () -> new UsernameNotFoundException(...) 创建异常实例

        // 🎯 第二步：构建Spring Security用户对象
        // org.springframework.security.core.userdetails.User: Spring Security内置的用户实现类
        // .builder(): 使用建造者模式（Builder Pattern）创建复杂对象
        return org.springframework.security.core.userdetails.User.builder() // 开始构建User对象
                .username(user.getUsername())           // 设置用户名（来自数据库）
                .password(user.getPassword())           // 设置密码（BCrypt加密后的哈希值）
                .authorities(Collections.singletonList(  // 设置用户权限列表
                    new SimpleGrantedAuthority("ROLE_" + user.getRole()) // 创建权限对象
                    // ROLE_前缀: Spring Security的角色约定，所有角色都需要ROLE_前缀
                    // 例如：USER -> ROLE_USER, ADMIN -> ROLE_ADMIN
                    // SimpleGrantedAuthority: Spring Security的权限实现类
                ))
                .accountExpired(false)                  // 设置账户是否过期：false表示账户未过期
                .accountLocked(false)                   // 设置账户是否锁定：false表示账户未锁定
                .credentialsExpired(false)              // 设置凭证是否过期：false表示密码未过期
                .disabled(!user.getEnabled())            // 设置账户是否禁用：取反用户的enabled字段
                // user.getEnabled(): true表示启用，false表示禁用
                // !user.getEnabled(): 取反，因为disabled参数表示是否禁用
                .build(); // .build(): 建造者模式的最后一步，创建并返回UserDetails对象
    }
}