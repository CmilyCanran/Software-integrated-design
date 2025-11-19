// package: Java包声明，用于组织类和避免命名冲突
package com.cmliy.springweb.security;

// import: 导入其他包中的类，以便在当前类中使用
import com.cmliy.springweb.util.JwtUtil;                 // 导入JWT工具类
import jakarta.servlet.FilterChain;                      // 导入Servlet过滤器链接口
import jakarta.servlet.ServletException;               // 导入Servlet异常类
import jakarta.servlet.http.HttpServletRequest;          // 导入HTTP请求接口
import jakarta.servlet.http.HttpServletResponse;         // 导入HTTP响应接口
import org.springframework.beans.factory.annotation.Autowired; // 导入Spring依赖注入注解
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // 导入Spring Security认证令牌类
import org.springframework.security.core.context.SecurityContextHolder; // 导入Spring Security安全上下文持有者
import org.springframework.security.core.userdetails.UserDetails; // 导入Spring Security用户详情接口
import org.springframework.security.core.userdetails.UserDetailsService; // 导入Spring Security用户详情服务接口
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; // 导入Web认证详情源
import org.springframework.stereotype.Component;          // 导入Spring组件注解
import org.springframework.web.filter.OncePerRequestFilter; // 导入Spring Web过滤器基类

import java.io.IOException;                             // 导入Java IO异常类

/**
 * 🔑 JWT认证过滤器
 *
 * 这个类继承自Spring的OncePerRequestFilter，用于拦截每个HTTP请求，
 * 验证请求中的JWT令牌，并在验证成功后设置Spring Security的认证上下文。
 *
 * 过滤器的工作流程：
 * 1. 从HTTP请求头中提取JWT令牌
 * 2. 验证令牌的有效性
 * 3. 加载用户详情信息
 * 4. 设置Spring Security认证上下文
 *
 * @Component: Spring框架注解，标记这是一个组件类，
 *             Spring容器会自动扫描并注册这个类为Bean。
 */
@Component // @Component注解：声明这是一个Spring组件，Spring会自动管理其生命周期
public class JwtAuthenticationFilter extends OncePerRequestFilter { // extends: 继承父类，获得父类的功能

    // @Autowired: Spring依赖注入注解，自动装配JwtUtil类型的Bean
    @Autowired // 自动注入：Spring容器会自动查找并注入JwtUtil实例
    private JwtUtil jwtUtil; // jwtUtil: JWT工具类，用于令牌的解析和验证

    // @Autowired: Spring依赖注入注解，自动装配UserDetailsService类型的Bean
    @Autowired // 自动注入：Spring容器会自动查找并注入UserDetailsService实例
    private UserDetailsService userDetailsService; // userDetailsService: 用户详情服务，用于加载用户信息

    /**
     * 🔍 过滤器核心逻辑
     *
     * 这是过滤器的主要方法，每个HTTP请求都会经过此方法。
     * 方法负责从请求中提取JWT令牌，验证令牌，并设置认证信息。
     *
     * @Override: 注解表示这个方法重写了父类的方法
     * @param request: HTTP请求对象，包含请求信息和JWT令牌
     * @param response: HTTP响应对象，用于设置响应信息
     * @param filterChain: 过滤器链，用于继续传递请求到下一个过滤器
     * @throws ServletException: 当处理请求时可能发生Servlet异常
     * @throws IOException: 当处理请求时可能发生IO异常
     */
    @Override // 重写注解：确保正确重写了父类方法
    protected void doFilterInternal(HttpServletRequest request,        // HTTP请求：客户端发送的请求信息
                                  HttpServletResponse response,       // HTTP响应：服务器返回的响应信息
                                  FilterChain filterChain)          // 过滤器链：用于继续传递请求
            throws ServletException, IOException { // 可能抛出的异常类型

        // 🔍 从请求头中获取Authorization
        // request.getHeader(): 从HTTP请求头中获取指定名称的值
        final String authorizationHeader = request.getHeader("Authorization"); // 获取Authorization请求头

        String username = null; // username: 从JWT中提取的用户名，初始化为null
        String jwt = null;      // jwt: 提取的JWT令牌字符串，初始化为null

        // 🎯 检查Authorization头格式
        // if条件：检查Authorization头是否存在且以"Bearer "开头
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) { // Bearer是JWT的标准前缀
            jwt = authorizationHeader.substring(7); // 移除"Bearer "前缀（7个字符），获取纯JWT令牌

            try { // try-catch: 捕获JWT解析过程中可能出现的异常
                // 📤 从JWT中提取用户名
                // jwtUtil.extractUsername(): 调用JWT工具类方法从令牌中提取用户名
                username = jwtUtil.extractUsername(jwt); // 从JWT令牌中解析出用户名
            } catch (Exception e) { // 捕获所有可能的异常（令牌格式错误、过期等）
                // System.err.println(): 向标准错误输出打印错误信息
                System.err.println("无法从JWT令牌中提取用户名: " + e.getMessage()); // 打印错误日志
            }
        }

        // 🔐 如果用户名不为空且当前没有认证
        // SecurityContextHolder.getContext(): 获取当前线程的安全上下文
        // .getAuthentication(): 获取当前认证信息，如果未认证则返回null
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { // 条件：有用户名且未认证
            // 👤 加载用户详情
            // this.userDetailsService.loadUserByUsername(): 调用用户详情服务加载用户信息
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username); // 从数据库加载用户详细信息

            // ✅ 验证JWT令牌
            // jwtUtil.validateToken(): 验证JWT令牌的有效性（签名、过期时间等）
            if (jwtUtil.validateToken(jwt, userDetails)) { // 如果JWT令牌有效
                // 🔑 创建认证令牌
                // UsernamePasswordAuthenticationToken: Spring Security的认证令牌类
                UsernamePasswordAuthenticationToken authToken = // 创建Spring Security认证对象
                    new UsernamePasswordAuthenticationToken(
                        userDetails,           // 第一个参数：用户详情对象
                        null,                  // 第二个参数：凭证（密码），JWT认证时为null
                        userDetails.getAuthorities() // 第三个参数：用户权限列表
                    );

                // 🎯 设置认证详情
                // WebAuthenticationDetailsSource: 创建Web认证详情的工厂类
                // .buildDetails(request): 根据HTTP请求创建认证详情对象
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // 设置请求详情（如IP地址、会话ID等）

                // 🛡️ 设置安全上下文
                // SecurityContextHolder.getContext(): 获取安全上下文
                // .setAuthentication(): 设置认证信息到上下文中
                SecurityContextHolder.getContext().setAuthentication(authToken); // 将认证信息存储到安全上下文
            }
        }

        // 🔄 继续过滤器链
        // filterChain.doFilter(): 将请求传递给下一个过滤器或目标处理器
        filterChain.doFilter(request, response); // 继续处理请求，传递给下一个过滤器
    }

    /**
     * 🚫 是否跳过此过滤器
     *
     * 重写父类方法，用于判断哪些请求不需要JWT验证。
     * 公开的API端点（如登录、注册）应该跳过JWT验证。
     *
     * @Override: 注解表示这个方法重写了父类的方法
     * @param request: HTTP请求对象
     * @return boolean: true表示跳过此过滤器，false表示执行此过滤器
     * @throws ServletException: 当处理请求时可能发生Servlet异常
     */
    @Override // 重写注解：确保正确重写了父类方法
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException { // 方法返回布尔值
        String path = request.getRequestURI(); // 获取请求的URI路径

        // 🎯 公开端点列表
        // return: 直接返回布尔值结果，多个条件用||（或）连接
        return path.startsWith("/api/auth/") ||      // 认证相关API（登录、注册等）
               path.startsWith("/api/public/") ||    // 公开API
               path.equals("/actuator/health") ||    // 健康检查端点
               path.startsWith("/swagger-ui/") ||    // Swagger UI文档
               path.startsWith("/v3/api-docs/");      // API文档
    }
}