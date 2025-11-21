// package: Java包声明，用于组织类和避免命名冲突
package com.cmliy.springweb.controller;

// import: 导入其他包中的类，以便在当前类中使用
import com.cmliy.springweb.model.User;                      // 导入用户实体类
import com.cmliy.springweb.repository.UserRepository;       // 导入用户数据访问层接口
import com.cmliy.springweb.util.JwtUtil;                    // 导入JWT工具类
import com.cmliy.springweb.common.ApiResponse;
import com.cmliy.springweb.security.CustomUserDetailsService; // 导入自定义用户详情服务
import org.springframework.beans.factory.annotation.Autowired; // 导入Spring依赖注入注解
import org.springframework.http.ResponseEntity;               // 导入Spring HTTP响应实体类
import org.springframework.security.authentication.AuthenticationManager; // 导入Spring Security认证管理器
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // 导入用户名密码认证令牌
import org.springframework.security.core.Authentication;      // 导入Spring Security认证接口
import org.springframework.security.core.context.SecurityContextHolder; // 导入安全上下文持有者
import org.springframework.security.core.userdetails.UserDetails; // 导入Spring Security用户详情接口
import org.springframework.security.crypto.password.PasswordEncoder; // 导入密码编码器接口
import org.springframework.web.bind.annotation.PostMapping;   // 导入Spring Web POST请求映射注解
import org.springframework.web.bind.annotation.RequestBody;   // 导入Spring Web请求体绑定注解
import org.springframework.web.bind.annotation.RestController; // 导入Spring Web REST控制器注解
import org.springframework.web.bind.annotation.RequestMapping; // 导入Spring Web请求映射注解

import java.time.LocalDateTime;  // 导入Java 8日期时间类，用于获取当前时间
import java.util.HashMap;        // 导入Java Map接口实现，用于存储键值对数据
import java.util.Map;           // 导入Java Map接口，定义键值对集合的规范
import java.util.Optional;      // 导入Java 8 Optional容器类，避免空指针异常

/**
 * 🔐 认证控制器
 *
 * 这个类处理用户认证相关的HTTP请求，包括登录和注册功能。
 * 使用JWT（JSON Web Token）机制实现无状态的用户认证。
 *
 * RESTful API设计原则：
 * - 使用HTTP动词表示操作类型（POST用于创建资源）
 * - 使用名词表示资源（/auth表示认证资源）
 * - 返回JSON格式的响应数据
 * - 使用适当的HTTP状态码
 *
 * @RestController: Spring框架注解，结合了@Controller和@ResponseBody，
 *                  标记这是一个REST API控制器，所有方法都返回JSON数据。
 */
@RestController // @RestController注解：声明这是一个REST控制器类
@RequestMapping("/auth") // @RequestMapping注解：为整个控制器设置基础路径
public class AuthController {  // public class: 定义公共类，其他类可以访问

    // ===== 依赖注入的字段 =====
    // 使用final字段和构造函数注入，这是Spring Boot推荐的最佳实践
    // 优势：1. 保证不可变性 2. 支持单元测试 3. 避免字段注入的潜在问题

    /**
     * 🔑 认证管理器
     *
     * Spring Security的核心组件，负责验证用户凭据（用户名和密码）的有效性。
     * 会调用UserDetailsService加载用户信息并进行密码比对。
     *
     * final关键字：表示这个字段一旦初始化就不能再修改，确保线程安全和不可变性
     */
    private final AuthenticationManager authenticationManager; // authenticationManager: 认证管理器，处理用户认证

    /**
     * 🎫 JWT工具类
     *
     * 负责JWT令牌的生成、解析和验证。
     * JWT是无状态认证的核心，包含用户信息但不需要服务器存储。
     */
    private final JwtUtil jwtUtil; // jwtUtil: JWT工具类实例

    /**
     * 🗄️ 用户数据访问层
     *
     * Spring Data JPA的Repository接口，提供用户数据的CRUD操作。
     * 用于注册时检查用户是否存在，以及保存新用户信息。
     */
    private final UserRepository userRepository; // userRepository: 用户数据访问接口

    /**
     * 🔒 密码编码器
     *
     * 使用BCrypt强哈希算法对密码进行加密。
     * 明文密码永远不会存储在数据库中，只存储加密后的哈希值。
     */
    private final PasswordEncoder passwordEncoder; // passwordEncoder: 密码编码器

    /**
     * 👤 自定义用户详情服务
     *
     * 实现Spring Security的UserDetailsService接口，
     * 在认证过程中从数据库加载用户信息。
     */
    private final CustomUserDetailsService userDetailsService; // userDetailsService: 用户详情服务

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
     * @param authenticationManager Spring Security认证管理器
     * @param jwtUtil JWT工具类实例
     * @param userRepository 用户数据访问层接口
     * @param passwordEncoder 密码编码器
     * @param userDetailsService 用户详情服务
     */
    public AuthController(AuthenticationManager authenticationManager,
                         JwtUtil jwtUtil,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         CustomUserDetailsService userDetailsService) {
        // this关键字：引用当前对象的字段，区分同名的参数和字段
        this.authenticationManager = authenticationManager; // 将传入的认证管理器赋值给当前对象的字段
        this.jwtUtil = jwtUtil; // 将传入的JWT工具类赋值给当前对象的字段
        this.userRepository = userRepository; // 将传入的用户Repository赋值给当前对象的字段
        this.passwordEncoder = passwordEncoder; // 将传入的密码编码器赋值给当前对象的字段
        this.userDetailsService = userDetailsService; // 将传入的用户详情服务赋值给当前对象的字段
    }

    /**
     * 🔐 用户登录接口
     *
     * 处理用户登录请求，验证用户凭据并生成JWT令牌。
     *
     * 登录流程详解：
     * 1. 接收前端发送的用户名和密码
     * 2. 使用AuthenticationManager验证凭据
     * 3. 设置Spring Security安全上下文
     * 4. 生成JWT访问令牌
     * 5. 返回用户信息和令牌
     *
     * @PostMapping: Spring Web注解，将HTTP POST请求映射到这个方法
     *              "/login": 这个方法处理 /auth/login 路径的请求
     *
     * @param loginRequest Map<String, String> 包含用户名和密码的请求体
     * @return ResponseEntity<ApiResponse<Map<String, Object>>> 包含JWT令牌和用户信息的HTTP响应
     */
    @PostMapping("/login") // @PostMapping注解：声明这是一个处理POST请求的方法
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody Map<String, String> loginRequest) { // public方法：公开访问，返回HTTP响应实体
        try { // try-catch: 捕获认证过程中可能出现的异常

            // 📥 第一步：解析请求参数
            // @RequestBody: Spring自动将JSON请求体转换为Map对象
            // .get(key): Map接口方法，根据键获取值
            String username = loginRequest.get("username");  // 从请求中获取用户名
            String password = loginRequest.get("password");  // 从请求中获取密码

            // 🔐 第二步：执行用户认证
            // AuthenticationManager: Spring Security认证管理器
            // authenticate(): 验证用户凭据的方法
            // UsernamePasswordAuthenticationToken: 封装用户名和密码的认证令牌
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
                // 创建认证令牌，包含用户名和密码
            );

            // 🛡️ 第三步：设置安全上下文
            // SecurityContextHolder: Spring Security安全上下文持有者
            // getContext(): 获取当前线程的安全上下文
            // setAuthentication(): 设置认证信息到上下文中
            // 这样在后续的请求中可以获取当前用户信息
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 👤 第四步：获取用户详情信息
            // userDetailsService: 自定义用户详情服务
            // loadUserByUsername(): 根据用户名加载用户详情
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 🎫 第五步：生成JWT访问令牌
            // jwtUtil: JWT工具类
            // generateToken(): 根据用户详情生成JWT令牌
            String token = jwtUtil.generateToken(userDetails);

            // 🗄️ 第六步：获取完整用户信息
            // userRepository: 用户数据访问层
            // findByUsername(): 根据用户名查询用户
            // Optional<User>: Java 8容器类，避免空指针异常
            Optional<User> userOpt = userRepository.findByUsername(username);
            User user = userOpt.orElse(null); // 如果用户存在则获取，否则为null

            // 📊 第七步：构建符合前端期望的响应数据
            // 前端request.js期望格式: {code: 200, data: {...}, message: "成功消息"}
            Map<String, Object> responseData = new HashMap<>(); // 创建实际数据容器
            responseData.put("token", token);                    // 设置JWT令牌
            responseData.put("tokenType", "Bearer");              // 设置令牌类型（JWT标准）
            responseData.put("expiresIn", jwtUtil.getExpiration()); // 设置令牌过期时间（毫秒）

            // 👤 第八步：添加用户基本信息到data中
            if (user != null) { // 检查用户对象是否存在
                responseData.put("user", Map.of(  // 创建用户信息Map
                    "id", user.getId(),           // 用户ID
                    "username", user.getUsername(), // 用户名
                    "email", user.getEmail(),       // 邮箱
                    "role", user.getRole()         // 用户角色
                ));
            }

            // ⏰ 第九步：添加时间戳到data中
            responseData.put("timestamp", LocalDateTime.now().toString()); // 设置当前时间

            // 📤 第十步：构建标准响应格式
            ApiResponse<Map<String, Object>> response = ApiResponse.success(responseData, "登录成功");

            // 📤 第十一步：返回成功响应
            // ResponseEntity.ok(): 创建HTTP状态码为200(OK)的响应
            return ResponseEntity.ok(response); // 返回包含令牌和用户信息的成功响应

        } catch (Exception e) { // 捕获认证异常
            // 🚨 异常处理：构建符合前端期望的错误响应
            ApiResponse<Map<String, Object>> errorResponse = ApiResponse.error("用户名或密码错误", 401);

            // 📤 返回错误响应
            // ResponseEntity.status(): 创建指定状态码的响应
            return ResponseEntity.status(401).body(errorResponse); // 返回401未授权错误
        }
    }

    /**
     * 📝 用户注册接口
     *
     * 处理新用户注册请求，验证用户信息并创建新账户。
     *
     * 注册流程详解：
     * 1. 接收用户注册信息（用户名、邮箱、密码）
     * 2. 检查用户名和邮箱是否已存在
     * 3. 对密码进行加密处理
     * 4. 创建用户实体并保存到数据库
     * 5. 返回注册成功消息
     *
     * 安全注意事项：
     * - 密码使用BCrypt加密存储
     * - 用户名和邮箱必须唯一
     * - 新用户默认角色为USER
     * - 新账户默认启用状态
     *
     * @param registerRequest Map<String, String> 包含注册信息的请求体
     * @return ResponseEntity<ApiResponse<Map<String, Object>>> 注册结果响应
     */
    @PostMapping("/register") // @PostMapping注解：声明这是一个处理POST请求的方法
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@RequestBody Map<String, String> registerRequest) { // public方法：公开访问，返回HTTP响应实体
        try { // try-catch: 捕获注册过程中可能出现的异常

            // 📥 第一步：解析注册信息
            String username = registerRequest.get("username");  // 从请求中获取用户名
            String email = registerRequest.get("email");      // 从请求中获取邮箱
            String password = registerRequest.get("password");  // 从请求中获取密码

            // 🔍 第二步：检查用户名是否已存在
            // userRepository.existsByUsername(): 检查用户名是否存在的自定义方法
            if (userRepository.existsByUsername(username)) { // 如果用户名已存在
                // 🚨 构建用户名重复错误响应
                ApiResponse<Map<String, Object>> errorResponse = ApiResponse.error("用户名已存在", 400);

                // 📤 返回客户端错误响应
                // ResponseEntity.badRequest(): 创建HTTP状态码为400的响应
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // 🔍 第三步：检查邮箱是否已存在
            // userRepository.existsByEmail(): 检查邮箱是否存在的自定义方法
            if (userRepository.existsByEmail(email)) { // 如果邮箱已存在
                // 🚨 构建邮箱重复错误响应
                ApiResponse<Map<String, Object>> errorResponse = ApiResponse.error("邮箱已存在", 400);

                // 📤 返回客户端错误响应
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // 👤 第四步：创建新用户实体
            // User(): 用户实体类的构造函数
            User user = new User(); // 创建新的User对象实例
            user.setUsername(username);     // 设置用户名
            user.setEmail(email);           // 设置邮箱

            // 🔒 第五步：加密用户密码
            // passwordEncoder: BCrypt密码编码器
            // encode(): 对明文密码进行哈希加密
            // 重要：明文密码永远不会存储在数据库中！
            user.setPassword(passwordEncoder.encode(password));

            // 👑 第六步：设置用户角色和状态
            user.setRole("USER");          // 设置默认角色为普通用户
            user.setEnabled(true);         // 设置账户为启用状态

            // 💾 第七步：保存用户到数据库
            // userRepository.save(): Spring Data JPA提供的保存方法
            // 会自动生成INSERT SQL语句并执行
            userRepository.save(user);

            // 📊 第八步：构建符合前端期望的成功响应
            Map<String, Object> responseData = new HashMap<>(); // 创建实际数据容器
            responseData.put("timestamp", LocalDateTime.now().toString()); // 设置时间戳

            ApiResponse<Map<String, Object>> response = ApiResponse.success(responseData, "注册成功");

            // 📤 第九步：返回创建成功响应
            // ResponseEntity.status(): 创建指定状态码的响应
            // 201 Created: HTTP状态码，表示资源成功创建
            return ResponseEntity.status(201).body(response);

        } catch (Exception e) { // 捕捉注册过程中的异常
            // 🚨 异常处理：构建符合前端期望的服务器错误响应
            ApiResponse<Map<String, Object>> errorResponse = ApiResponse.error("注册失败: " + e.getMessage(), 500);

            // 📤 返回服务器错误响应
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}