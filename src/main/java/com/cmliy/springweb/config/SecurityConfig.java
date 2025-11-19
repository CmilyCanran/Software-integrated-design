package com.cmliy.springweb.config;

import com.cmliy.springweb.security.JwtAuthenticationEntryPoint;
import com.cmliy.springweb.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)  // 🎯 启用方法级安全
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 🔐 密码编码器配置
     * 使用BCrypt强哈希算法加密密码
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 🔑 认证管理器配置
     * 用于处理用户认证逻辑
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 🌐 CORS跨域配置
     * 允许前端应用跨域访问
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 🎯 允许的源
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // 🎯 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 🎯 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // 🎯 允许凭证
        configuration.setAllowCredentials(true);

        // 🎯 预检请求缓存时间
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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