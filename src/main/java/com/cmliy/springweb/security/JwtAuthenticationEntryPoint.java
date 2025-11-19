// package: Java包声明，用于组织类和避免命名冲突
package com.cmliy.springweb.security;

// import: 导入其他包中的类，以便在当前类中使用
import com.fasterxml.jackson.databind.ObjectMapper;         // 导入Jackson JSON处理库
import org.springframework.beans.factory.annotation.Autowired;     // 导入Spring依赖注入注解
import jakarta.servlet.ServletException;               // 导入Servlet异常类
import jakarta.servlet.http.HttpServletRequest;          // 导入HTTP请求接口
import jakarta.servlet.http.HttpServletResponse;         // 导入HTTP响应接口
import org.springframework.http.MediaType;             // 导入Spring HTTP媒体类型类
import org.springframework.security.core.AuthenticationException; // 导入Spring Security认证异常类
import org.springframework.security.web.AuthenticationEntryPoint; // 导入Spring Security认证入口点接口
import org.springframework.stereotype.Component;          // 导入Spring组件注解

import java.io.IOException;                             // 导入Java IO异常类
import java.time.LocalDateTime;                         // 导入Java 8日期时间类
import java.util.HashMap;                              // 导入Java Map接口实现
import java.util.Map;                                  // 导入Java Map接口

/**
 * 🚨 JWT认证入口点
 *
 * 这个类实现了Spring Security的AuthenticationEntryPoint接口，
 * 用于处理未认证用户访问受保护资源时的响应。
 * 当用户没有有效的JWT令牌或令牌过期时，Spring Security会调用此类。
 *
 * @Component: Spring框架注解，标记这是一个组件类，
 *             Spring容器会自动扫描并注册这个类为Bean。
 */
@Component // @Component注解：声明这是一个Spring组件，Spring会自动管理其生命周期
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint { // implements: 实现接口，必须提供接口中所有方法的实现

    // @Autowired: Spring依赖注入注解，自动装配ObjectMapper类型的Bean
    // ObjectMapper: Jackson库的核心类，用于JSON序列化和反序列化
    // @Autowired: Spring依赖注入，如果注入失败可能需要检查Jackson依赖
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 🚨 处理认证失败
     *
     * 当用户未认证访问受保护资源时，Spring Security会调用此方法。
     * 方法负责生成统一的JSON错误响应给前端。
     *
     * @Override: 注解表示这个方法重写了父类或接口的方法
     * @param request: HTTP请求对象，包含请求信息如URL、头信息等
     * @param response: HTTP响应对象，用于设置响应状态、头信息和内容
     * @param authException: 认证异常对象，包含认证失败的详细信息
     * @throws IOException: 当写入响应时可能发生IO异常
     * @throws ServletException: 当处理请求时可能发生Servlet异常
     */
    @Override // 重写注解：确保正确实现了接口方法
    public void commence(HttpServletRequest request,                    // HTTP请求：客户端发送的请求信息
                        HttpServletResponse response,                   // HTTP响应：服务器返回的响应信息
                        AuthenticationException authException)      // 认证异常：Spring Security抛出的认证异常
            throws IOException, ServletException { // 可能抛出的异常类型

        // 🎯 设置响应状态和内容类型
        // HttpServletResponse.SC_UNAUTHORIZED: HTTP 401状态码，表示未授权
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置HTTP状态码为401
        // MediaType.APPLICATION_JSON_VALUE: "application/json"，表示响应内容为JSON格式
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // 设置响应内容类型为JSON
        response.setCharacterEncoding("UTF-8"); // 设置响应字符编码为UTF-8，支持中文

        // 📝 构建错误响应
        // HashMap<String, Object>: 创建一个Map来存储响应数据，键为String类型，值为Object类型
        Map<String, Object> body = new HashMap<>(); // 创建响应数据容器
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);  // 设置状态码字段
        body.put("error", "Unauthorized");                           // 设置错误类型字段
        body.put("message", "认证失败，请先登录");                   // 设置错误消息字段
        body.put("path", request.getServletPath());                 // 设置请求路径字段，方便前端定位
        body.put("timestamp", LocalDateTime.now().toString());       // 设置时间戳字段，记录错误发生时间

        // 📤 写入响应
        try {
            // objectMapper.writeValue(): 将Java对象转换为JSON字符串并写入输出流
            // response.getOutputStream(): 获取HTTP响应的输出流，用于向客户端发送数据
            String json = objectMapper.writeValueAsString(body); // 先转换为JSON字符串
            response.getWriter().write(json); // 使用Writer而不是OutputStream
        } catch (Exception e) {
            // 🚨 如果JSON序列化失败，提供备用响应
            response.getWriter().write("{\"error\":\"Serialization failed\",\"message\":\"" +
                authException.getMessage() + "\"}");
        }
    }
}