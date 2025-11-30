package com.cmliy.springweb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

/**
 * 应用配置类
 * 用于读取自定义配置，提供类型安全的配置访问
 *
 * @author SpringWeb Team
 * @since 2025-11-30
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app-config")
public class AppConfig {

    /**
     * JWT相关配置
     */
    @Data
    public static class JwtConfig {
        /**
         * JWT密钥
         */
        private String secret;

        /**
         * JWT过期时间（毫秒）
         */
        private Long expiration;
    }

    /**
     * CORS跨域配置
     */
    @Data
    public static class CorsConfig {
        /**
         * 允许的源地址
         */
        private String[] allowedOrigins;

        /**
         * 允许的HTTP方法
         */
        private String[] allowedMethods;

        /**
         * 允许的请求头
         */
        private String[] allowedHeaders;

        /**
         * 是否允许携带凭证
         */
        private Boolean allowCredentials;
    }

    /**
     * JWT配置实例
     */
    private JwtConfig jwt = new JwtConfig();

    /**
     * CORS配置实例
     */
    private CorsConfig cors = new CorsConfig();
}