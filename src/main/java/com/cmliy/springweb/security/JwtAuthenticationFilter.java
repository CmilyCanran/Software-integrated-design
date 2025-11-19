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

    // ===== 依赖注入的字段 =====
    // 使用final字段和构造函数注入，这是Spring Boot推荐的最佳实践
    // 优势：1. 保证不可变性 2. 支持单元测试 3. 避免字段注入的潜在问题

    /**
     * 🎫 JWT工具类
     *
     * 负责JWT令牌的生成、解析和验证。
     * 在过滤器中用于解析用户请求中的JWT令牌。
     *
     * final关键字：表示这个字段一旦初始化就不能再修改，确保线程安全和不可变性
     */
    private final JwtUtil jwtUtil; // jwtUtil: JWT工具类，用于令牌的解析和验证

    /**
     * 👤 用户详情服务
     *
     * 自定义的用户详情服务，从数据库加载用户信息。
     * 在JWT验证成功后，用于加载完整的用户详情信息。
     */
    private final UserDetailsService userDetailsService; // userDetailsService: 用户详情服务，用于加载用户信息

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
     * 在过滤器中使用构造函数注入特别重要，因为过滤器是单例的，在多线程环境下运行
     *
     * @param jwtUtil JWT工具类实例，用于令牌的解析和验证
     * @param userDetailsService 用户详情服务，用于加载用户信息
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        // this关键字：引用当前对象的字段，区分同名的参数和字段
        this.jwtUtil = jwtUtil; // 将传入的JWT工具类赋值给当前对象的字段
        this.userDetailsService = userDetailsService; // 将传入的用户详情服务赋值给当前对象的字段
    }

    /**
     * 🔍 过滤器核心处理逻辑
     *
     * 这是过滤器的主要方法，每个HTTP请求都会经过此方法（除了shouldNotFilter返回true的请求）。
     * 方法负责从请求中提取JWT令牌，验证令牌，并设置Spring Security认证上下文。
     *
     * Servlet过滤器机制说明：
     * 1. 过滤器链：请求依次经过多个过滤器
     * 2. 每个过滤器都可以修改请求/响应
     * 3. 最后一个过滤器调用目标控制器
     * 4. 响应按相反顺序经过过滤器链返回
     *
     * JWT认证流程：
     * 1. 提取Authorization请求头中的JWT令牌
     * 2. 验证令牌的格式和签名
     * 3. 从令牌中提取用户信息
     * 4. 加载用户详情并验证权限
     * 5. 设置Spring Security认证上下文
     *
     * @Override: 注解表示这个方法重写了父类的方法，确保方法签名正确
     * @param request: HttpServletRequest对象，包含HTTP请求的所有信息
     * @param response: HttpServletResponse对象，用于构建HTTP响应
     * @param filterChain: FilterChain对象，用于将请求传递给下一个过滤器
     * @throws ServletException: 当处理请求时可能发生的Servlet相关异常
     * @throws IOException: 当处理请求时可能发生的IO相关异常
     */
    @Override // 重写注解：确保正确重写了父类方法
    protected void doFilterInternal(HttpServletRequest request,        // HTTP请求：客户端发送的请求信息
                                  HttpServletResponse response,       // HTTP响应：服务器返回的响应信息
                                  FilterChain filterChain)          // 过滤器链：用于继续传递请求
            throws ServletException, IOException { // 可能抛出的异常类型

        // 🔍 第一步：从请求头获取Authorization头
        // request.getHeader(): HTTPServletRequest的方法，获取指定名称的请求头
        // Authorization: HTTP标准请求头，用于传递认证信息
        final String authorizationHeader = request.getHeader("Authorization"); // 获取Authorization请求头

        String username = null; // username变量：存储从JWT中提取的用户名，初始化为null
        String jwt = null;      // jwt变量：存储提取的JWT令牌字符串，初始化为null

        // 🎯 第二步：检查并提取JWT令牌
        // JWT标准格式：Authorization: Bearer <jwt_token>
        // Bearer前缀：OAuth 2.0和JWT的标准约定，共7个字符（包含空格）
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) { // 检查请求头是否存在且以Bearer开头
            jwt = authorizationHeader.substring(7); // 移除"Bearer "前缀，获取纯JWT令牌字符串
            // .substring(7): 从索引7开始截取字符串，跳过"Bearer "

            try { // try-catch: 捕获JWT解析过程中可能出现的各种异常
                // 📤 第三步：从JWT中提取用户名
                // jwtUtil.extractUsername(): JWT工具类的方法，解析JWT载荷中的subject声明
                username = jwtUtil.extractUsername(jwt); // 从JWT令牌中解析出用户名
            } catch (Exception e) { // 捕获所有可能的JWT解析异常
                // 常见异常：令牌格式错误、签名无效、令牌过期等
                // System.err.println(): Java标准错误输出流，用于打印错误信息
                System.err.println("无法从JWT令牌中提取用户名: " + e.getMessage()); // 打印详细错误信息
                // 在生产环境中，应该使用日志框架（如SLF4J）而不是System.err
            }
        }

        // 🔐 第四步：验证用户名和认证状态
        // SecurityContextHolder.getContext(): 获取当前线程的Spring Security安全上下文
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