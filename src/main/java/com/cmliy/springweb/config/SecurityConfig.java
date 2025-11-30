// package: Java包声明，用于组织类和避免命名冲突
package com.cmliy.springweb.config;

// import: 导入其他包中的类，以便在当前类中使用
import com.cmliy.springweb.security.JwtAuthenticationEntryPoint;  // 导入JWT认证入口点类
import com.cmliy.springweb.security.JwtAuthenticationFilter;    // 导入JWT认证过滤器类
import org.springframework.beans.factory.annotation.Autowired;     // 导入Spring依赖注入注解
import org.springframework.context.annotation.Bean;                // 导入Spring Bean注解
import org.springframework.context.annotation.Configuration;          // 导入Spring配置注解
import org.springframework.security.authentication.AuthenticationManager; // 导入Spring Security认证管理器
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // 导入认证配置类
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // 导入方法级安全注解
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // 导入HTTP安全构建器
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // 导入Web安全启用注解
import org.springframework.security.config.http.SessionCreationPolicy; // 导入会话创建策略
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 导入BCrypt密码编码器
import org.springframework.security.crypto.password.PasswordEncoder; // 导入密码编码器接口
import org.springframework.security.core.userdetails.UserDetailsService; // 导入用户详情服务接口
import org.springframework.security.web.SecurityFilterChain; // 导入安全过滤器链接口
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // 导入用户名密码认证过滤器
import org.springframework.web.cors.CorsConfiguration; // 导入CORS配置类
import org.springframework.web.cors.CorsConfigurationSource; // 导入CORS配置源接口
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // 导入基于URL的CORS配置源

import java.util.Arrays;  // 导入Java数组工具类

/**
 * 🛡️ Spring Security安全配置类
 *
 * 这个类负责配置应用程序的安全策略，包括：
 * - HTTP请求的访问控制规则
 * - JWT认证过滤器的集成
 * - CORS跨域资源共享配置
 * - 密码加密策略设置
 * - 方法级安全控制
 *
 * Spring Security工作原理：
 * 1. 过滤器链：每个HTTP请求都会经过一系列安全过滤器
 * 2. 认证：验证用户身份（用户名密码、JWT令牌等）
 * 3. 授权：检查用户是否有权限访问特定资源
 * 4. 异常处理：处理认证和授权过程中的异常
 *
 * @Configuration: Spring框架注解，标记这是一个配置类，
 *                  Spring容器会扫描并处理其中的@Bean方法。
 */
@Configuration // @Configuration注解：声明这是一个Spring配置类
@EnableWebSecurity // @EnableWebSecurity注解：启用Spring Security Web安全功能
@EnableMethodSecurity(prePostEnabled = true)  // @EnableMethodSecurity注解：启用方法级安全控制
public class SecurityConfig {  // public class: 定义公共类，其他类可以访问

    // ===== 依赖注入的安全组件 =====
    // 使用final字段和构造函数注入，这是Spring Boot推荐的最佳实践
    // 优势：1. 保证不可变性 2. 支持单元测试 3. 避免字段注入的潜在问题

    /**
     * ⚙️ 应用配置类
     *
     * 提供类型安全的配置访问，包括JWT和CORS配置。
     * 从环境变量或配置文件中读取配置，避免硬编码。
     */
    private final AppConfig appConfig; // appConfig: 应用配置类

    /**
     * 🚨 JWT认证入口点
     *
     * 处理未认证用户访问受保护资源时的响应。
     * 当用户没有有效的JWT令牌或令牌过期时，Spring Security会调用此类。
     *
     * final关键字：表示这个字段一旦初始化就不能再修改，确保线程安全和不可变性
     */
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint; // jwtAuthenticationEntryPoint: JWT认证入口点

    /**
     * 🔑 JWT认证过滤器
     *
     * 拦截HTTP请求并验证JWT令牌的有效性。
     * 在用户名密码认证过滤器之前执行，实现无状态认证。
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // jwtAuthenticationFilter: JWT认证过滤器

    /**
     * 👤 用户详情服务
     *
     * 自定义的用户详情服务，从数据库加载用户信息。
     * 在认证过程中提供用户详情给Spring Security。
     */
    private final UserDetailsService userDetailsService; // userDetailsService: 用户详情服务

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
     * 在配置类中，构造函数注入特别重要，因为配置类通常在应用启动时就被初始化
     *
     * @param appConfig 应用配置类，提供JWT和CORS配置
     * @param jwtAuthenticationEntryPoint JWT认证入口点处理器
     * @param jwtAuthenticationFilter JWT认证过滤器
     * @param userDetailsService 用户详情服务
     */
    public SecurityConfig(AppConfig appConfig,
                         JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                         JwtAuthenticationFilter jwtAuthenticationFilter,
                         UserDetailsService userDetailsService) {
        // this关键字：引用当前对象的字段，区分同名的参数和字段
        this.appConfig = appConfig; // 将传入的应用配置赋值给当前对象的字段
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint; // 将传入的JWT认证入口点赋值给当前对象的字段
        this.jwtAuthenticationFilter = jwtAuthenticationFilter; // 将传入的JWT认证过滤器赋值给当前对象的字段
        this.userDetailsService = userDetailsService; // 将传入的用户详情服务赋值给当前对象的字段
    }

    /**
     * 🔐 密码编码器配置
     *
     * 配置密码加密策略，使用BCrypt强哈希算法对用户密码进行加密。
     * 这是Spring Security推荐的密码编码方式。
     *
     * BCrypt算法特点：
     * - 自动加盐：每个密码都有唯一的盐值
     * - 计算慢：防止暴力破解攻击
     * - 可调强度：可以通过参数调整计算复杂度
     * - 单向哈希：无法从哈希值反推原始密码
     *
     * @Bean: Spring框架注解，声明这个方法返回一个Bean对象，
     *       Spring容器会自动管理该对象的生命周期。
     *
     * @return PasswordEncoder: 配置好的密码编码器实例
     */
    @Bean // @Bean注解：将方法返回值注册为Spring容器中的Bean
    public PasswordEncoder passwordEncoder() { // public方法：公开访问，返回PasswordEncoder对象
        // BCryptPasswordEncoder: BCrypt密码编码器的实现类
        // new BCryptPasswordEncoder(): 创建BCrypt编码器实例
        return new BCryptPasswordEncoder(); // 返回BCrypt密码编码器实例
    }

    /**
     * 🔑 认证管理器配置
     *
     * 配置Spring Security的认证管理器，负责处理用户认证逻辑。
     * 认证管理器是Spring Security的核心组件，协调各种认证提供者。
     *
     * 认证流程：
     * 1. 接收认证请求（用户名密码、JWT令牌等）
     * 2. 调用合适的AuthenticationProvider进行认证
     * 3. 返回Authentication对象（包含用户信息和权限）
     *
     * @Bean: Spring框架注解，声明这个方法返回一个Bean对象
     *
     * @param config AuthenticationConfiguration: Spring Security的认证配置对象
     * @return AuthenticationManager: 配置好的认证管理器实例
     * @throws Exception: 配置过程中可能出现的异常
     */
    @Bean // @Bean注解：将方法返回值注册为Spring容器中的Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception { // public方法：公开访问，可能抛出异常

        // AuthenticationConfiguration: Spring Security提供的认证配置类
        // .getAuthenticationManager(): 从配置中获取认证管理器实例
        return config.getAuthenticationManager(); // 返回认证管理器实例
    }

    /**
     * 🌐 CORS跨域资源配置
     *
     * 配置跨域资源共享（Cross-Origin Resource Sharing）策略，
     * 允许前端应用（如Vue.js、React等）跨域访问后端API。
     *
     * 现在使用AppConfig从配置文件读取CORS设置，支持环境差异化配置：
     * - 开发环境：允许本地开发服务器
     * - 生产环境：仅允许指定域名
     *
     * CORS工作原理：
     * 1. 浏览器发送OPTIONS预检请求
     * 2. 服务器返回允许的跨域策略
     * 3. 浏览器根据策略决定是否发送实际请求
     *
     * 安全改进：
     * - 不再使用通配符"*"，提高安全性
     * - 配置集中管理，支持环境变量
     * - 类型安全的配置访问
     *
     * @Bean: Spring框架注解，声明这个方法返回一个Bean对象
     *
     * @return CorsConfigurationSource: 配置好的CORS策略源
     */
    @Bean // @Bean注解：将方法返回值注册为Spring容器中的Bean
    public CorsConfigurationSource corsConfigurationSource() { // public方法：公开访问，返回CORS配置源

        // 📋 创建CORS配置对象
        // CorsConfiguration: Spring提供的CORS配置类
        CorsConfiguration configuration = new CorsConfiguration(); // 创建CORS配置实例

        // 🌐 配置允许的源（Origin）- 从AppConfig读取
        // .setAllowedOrigins(): 设置允许的具体源域名（不使用通配符提高安全性）
        // appConfig.getCors().getAllowedOrigins(): 从配置文件读取允许的域名列表
        configuration.setAllowedOrigins(Arrays.asList(appConfig.getCors().getAllowedOrigins()));

        // 🔄 配置允许的HTTP方法 - 从AppConfig读取
        // .setAllowedMethods(): 设置允许的HTTP动词
        // appConfig.getCors().getAllowedMethods(): 从配置文件读取允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList(appConfig.getCors().getAllowedMethods()));

        // 📤 配置允许的请求头 - 从AppConfig读取
        // .setAllowedHeaders(): 设置允许的HTTP请求头
        // appConfig.getCors().getAllowedHeaders(): 从配置文件读取允许的请求头
        configuration.setAllowedHeaders(Arrays.asList(appConfig.getCors().getAllowedHeaders()));

        // 🔐 配置是否允许凭证 - 从AppConfig读取
        // .setAllowCredentials(): 是否允许发送Cookie和认证信息
        // appConfig.getCors().getAllowCredentials(): 从配置文件读取凭证设置
        configuration.setAllowCredentials(appConfig.getCors().getAllowCredentials());

        // ⏰ 配置预检请求缓存时间
        // .setMaxAge(): 设置浏览器缓存CORS预检结果的时间（秒）
        // 3600L: 缓存1小时，减少预检请求频率
        configuration.setMaxAge(3600L); // 设置预检请求缓存时间为1小时

        // 🌍 创建基于URL的CORS配置源
        // UrlBasedCorsConfigurationSource: 根据URL路径配置CORS策略
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // 创建URL基础的CORS配置源

        // 📝 注册CORS配置到所有路径
        // .registerCorsConfiguration(): 为指定路径注册CORS配置
        // "/**": 匹配所有URL路径
        source.registerCorsConfiguration("/**", configuration); // 将配置应用到所有路径

        // 📤 返回CORS配置源
        return source; // 返回配置好的CORS源
    }

    /**
     * 🛡️ 安全过滤器链配置
     * 核心安全配置，定义访问规则和认证流程
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 🚫 禁用CSRF（JWT不需要CSRF保护）
                .csrf(csrf -> csrf.disable())

                // 🌐 配置CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 🎯 配置会话管理（无状态）
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 🔐 配置异常处理
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))

                // 🎯 配置请求授权规则
                .authorizeHttpRequests(auth -> auth
                        // 📄 公开访问的端点
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/uploads/images/**").permitAll()

                        // 🔧 管理员端点
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 📊 Swagger文档（开发环境）
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 🔍 健康检查端点
                        .requestMatchers("/actuator/health").permitAll()

                        // 🛡️ 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )

                // 🔑 添加用户详情服务
                .userDetailsService(userDetailsService);

        // 🎯 添加JWT认证过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}