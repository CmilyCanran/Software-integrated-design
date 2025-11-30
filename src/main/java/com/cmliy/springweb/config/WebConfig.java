package com.cmliy.springweb.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 🌐 Web配置类 - Web Configuration
 *
 * 配置静态资源访问，特别是图片文件的HTTP访问
 * 支持长期缓存以提高性能
 *
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 商品图片HTTP访问映射 - 365天缓存
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:D:\\Code\\Learn\\Java\\Spring\\image\\uploads\\images\\")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));
    }
}