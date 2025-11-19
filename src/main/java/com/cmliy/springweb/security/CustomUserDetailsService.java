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

    // @Autowired: Spring依赖注入注解，自动装配UserRepository类型的Bean
    @Autowired // 自动注入：Spring容器会自动查找并注入UserRepository实例
    private UserRepository userRepository; // userRepository: 用户数据访问层接口，用于数据库操作

    /**
     * 🔍 根据用户名加载用户详情
     *
     * 这是UserDetailsService接口的核心方法，Spring Security在用户认证时会调用此方法。
     *
     * @Override: 注解表示这个方法重写了父类或接口的方法
     * @param username: 用户名，从登录表单或JWT令牌中获取
     * @return UserDetails: Spring Security的用户详情对象，包含用户信息和权限
     * @throws UsernameNotFoundException: 当用户不存在时抛出此异常
     */
    @Override // 重写注解：确保正确实现了接口方法
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { // 方法签名：返回UserDetails，参数为用户名，可能抛出异常

        // 🗄️ 从数据库查询用户
        // userRepository.findByUsername(): 调用Repository方法按用户名查询用户
        // Optional<User>: Java 8的容器类，可能包含User对象也可能为空
        // .orElseThrow(): 如果Optional为空则抛出指定异常，否则返回包含的值
        User user = userRepository.findByUsername(username) // 查询数据库获取用户信息
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username)); // 如果用户不存在，抛出异常

        // 🎯 转换为Spring Security用户对象
        // org.springframework.security.core.userdetails.User: Spring Security提供的用户实现类
        // .builder(): 使用建造者模式创建User对象
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())           // 设置用户名
                .password(user.getPassword())           // 设置密码（已加密）
                .authorities(Collections.singletonList(  // 设置用户权限列表
                    new SimpleGrantedAuthority("ROLE_" + user.getRole()) // 创建权限对象，ROLE_前缀是Spring Security约定
                ))
                .accountExpired(false)                  // 设置账户是否过期：false表示未过期
                .accountLocked(false)                   // 设置账户是否锁定：false表示未锁定
                .credentialsExpired(false)              // 设置凭证是否过期：false表示未过期
                .disabled(!user.getEnabled())            // 设置账户是否禁用：根据用户的enabled字段决定
                .build(); // .build(): 建造者模式的最后一步，创建User对象
    }
}