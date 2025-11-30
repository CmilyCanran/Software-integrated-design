// package: Java包声明，用于组织类和避免命名冲突
package com.cmliy.springweb.controller;

// import: 导入其他包中的类，以便在当前类中使用
import com.cmliy.springweb.model.User;                      // 导入用户实体类
import com.cmliy.springweb.repository.UserRepository;       // 导入用户数据访问层接口
import com.cmliy.springweb.util.JwtUtil;                    // 导入JWT工具类
import com.cmliy.springweb.common.ApiResponse;
import com.cmliy.springweb.security.CustomUserDetailsService; // 导入自定义用户详情服务
import com.cmliy.springweb.dto.LoginResponseDTO;            // 导入登录响应DTO
import com.cmliy.springweb.dto.RegisterResponseDTO;         // 导入注册响应DTO
import com.cmliy.springweb.dto.UserDTO;                     // 导入用户信息DTO
import org.springframework.beans.factory.annotation.Autowired; // 导入Spring依赖注入注解
import org.springframework.http.ResponseEntity;               // 导入Spring HTTP响应实体类
import org.springframework.security.authentication.AuthenticationManager; // 导入Spring Security认证管理器
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // 导入用户名密码认证令牌
import org.springframework.security.core.Authentication;      // 导入Spring Security认证接口
import org.springframework.security.core.context.SecurityContextHolder; // 导入安全上下文持有者
import org.springframework.security.core.userdetails.UserDetails; // 导入Spring Security用户详情接口
import org.springframework.security.crypto.password.PasswordEncoder; // 导入密码编码器接口
import org.springframework.web.bind.annotation.GetMapping;    // 导入Spring Web GET请求映射注解
import org.springframework.web.bind.annotation.PostMapping;   // 导入Spring Web POST请求映射注解
import org.springframework.web.bind.annotation.RequestBody;   // 导入Spring Web请求体绑定注解
import org.springframework.web.bind.annotation.RestController; // 导入Spring Web REST控制器注解
import org.springframework.web.bind.annotation.RequestMapping; // 导入Spring Web请求映射注解
import java.time.LocalDateTime;  // 导入Java 8日期时间类，用于获取当前时间
import java.util.Map;           // 导入Java Map接口，用于处理请求参数
import java.util.Optional;      // 导入Java 8 Optional容器类，避免空指针异常

// 🚀 Lombok注解导入 - 大幅简化样板代码
import lombok.extern.slf4j.Slf4j;        // @Slf4j: 自动生成Logger实例，替代手动创建
import lombok.RequiredArgsConstructor;   // @RequiredArgsConstructor: 自动生成包含所有final字段的构造函数

/**
 * 🔐 认证控制器 (Lombok优化版本)
 *
 * 这个类处理用户认证相关的HTTP请求，包括登录和注册功能。
 * 使用JWT（JSON Web Token）机制实现无状态的用户认证。
 *
 * 🚀 Lombok优化展示：
 * - @Slf4j: 自动生成Logger实例，无需手动创建
 * - @RequiredArgsConstructor: 自动生成包含所有final字段的构造函数
 * - 继承BaseController: 获得统一的API响应方法
 *
 * RESTful API设计原则：
 * - 使用HTTP动词表示操作类型（POST用于创建资源）
 * - 使用名词表示资源（/auth表示认证资源）
 * - 返回JSON格式的响应数据
 * - 使用适当的HTTP状态码
 */
@RestController // @RestController注解：声明这是一个REST控制器类
@RequestMapping("/auth") // @RequestMapping注解：为整个控制器设置基础路径
@Slf4j // 🚀 Lombok: 自动生成 private static final Logger log = LoggerFactory.getLogger(AuthController.class);
public class AuthController extends BaseController {  // 🚀 继承BaseController获得统一响应方法

    /**
     * 🏗️ 构造函数（手动创建以支持继承）
     *
     * 🎓 Lombok教学要点：
     * - @RequiredArgsConstructor在继承时无法处理父类字段
     * - 这是Lombok的一个已知限制，适合教学展示
     * 手动编写构造函数能更好地理解继承和依赖注入
     *
     * 🚀 Lombok高级方案对比（@SuperBuilder）：
     * 如果使用@SuperBuilder，代码会是这样：
     *
     * ```java
     * @SuperBuilder
     * @Slf4j
     * public class AuthController extends BaseController {
     *     private final AuthenticationManager authenticationManager;
     *     private final PasswordEncoder passwordEncoder;
     *     private final CustomUserDetailsService userDetailsService;
     *
     *     // @SuperBuilder会自动生成包含父类字段的构造函数
     * }
     *
     * // 使用时：
     * AuthController controller = AuthController.builder()
     *     .userRepository(userRepository)
     *     .jwtUtil(jwtUtil)
     *     .authenticationManager(authenticationManager)
     *     .passwordEncoder(passwordEncoder)
     *     .userDetailsService(userDetailsService)
     *     .build();
     * ```
     *
     * @SuperBuilder的优势：
     * ✅ 自动处理继承关系
     * ✅ 支持复杂的构造逻辑
     * ✅ 代码更简洁
     *
     * @SuperBuilder的缺点：
     * ❌ 语法复杂，初学者难理解
     * ❌ 调试时不够直观
     * ❌ 需要额外的Lombok依赖
     *
     * @param userRepository 用户数据访问层（传递给基类）
     * @param jwtUtil JWT工具类（传递给基类）
     * @param authenticationManager 认证管理器（当前类需要）
     * @param passwordEncoder 密码编码器（当前类需要）
     * @param userDetailsService 用户详情服务（当前类需要）
     */
    public AuthController(UserRepository userRepository,
                         JwtUtil jwtUtil,
                         AuthenticationManager authenticationManager,
                         PasswordEncoder passwordEncoder,
                         CustomUserDetailsService userDetailsService) {
        // 🚀 调用父类构造函数，传递基类需要的字段
        super(userRepository, jwtUtil);

        // 🚀 赋值当前类的字段
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    // ===== 依赖注入的字段 (Lombok简化版) =====
    // 🚀 Lombok的@RequiredArgsConstructor会自动生成包含所有final字段的构造函数
    // 注意：由于继承BaseController，UserRepository和JwtUtil已经由基类管理

    /**
     * 🔑 认证管理器
     *
     * Spring Security的核心组件，负责验证用户凭据（用户名和密码）的有效性。
     * 会调用UserDetailsService加载用户信息并进行密码比对。
     *
     * final关键字：表示这个字段一旦初始化就不能再修改，确保线程安全和不可变性
     */
    private final AuthenticationManager authenticationManager; // 🚀 Lombok会自动生成构造函数注入

    /**
     * 🔒 密码编码器
     *
     * 使用BCrypt强哈希算法对密码进行加密。
     * 明文密码永远不会存储在数据库中，只存储加密后的哈希值。
     */
    private final PasswordEncoder passwordEncoder; // 🚀 Lombok会自动生成构造函数注入

    /**
     * 👤 自定义用户详情服务
     *
     * 实现Spring Security的UserDetailsService接口，
     * 在认证过程中从数据库加载用户信息。
     */
    private final CustomUserDetailsService userDetailsService; // 🚀 Lombok会自动生成构造函数注入

    // 🚀 Lombok生成的构造函数等效代码（包含基类字段）：
    // public AuthController(
    //     // 基类需要的字段
    //     UserRepository userRepository,
    //     JwtUtil jwtUtil,
    //     // 当前类需要的字段
    //     AuthenticationManager authenticationManager,
    //     PasswordEncoder passwordEncoder,
    //     CustomUserDetailsService userDetailsService
    // ) {
    //     // 基类字段赋值（由Lombok自动完成）
    //     this.userRepository = userRepository;
    //     this.jwtUtil = jwtUtil;
    //
    //     // 当前类字段赋值（由Lombok自动完成）
    //     this.authenticationManager = authenticationManager;
    //     this.passwordEncoder = passwordEncoder;
    //     this.userDetailsService = userDetailsService;
    // }

    /**
     * 🔐 用户登录接口 (Lombok + BaseController优化版本)
     *
     * 处理用户登录请求，验证用户凭据并生成JWT令牌。
     *
     * 🚀 优化亮点：
     * - 使用BaseController的success()和error()方法简化响应构建
     * - 使用@Slf4j的log替代手动logger
     * - 保持完整功能的同时大幅简化代码
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
     * @return ResponseEntity<ApiResponse<LoginResponseDTO>> 包含JWT令牌和用户信息的HTTP响应
     */
    @PostMapping("/login") // @PostMapping注解：声明这是一个处理POST请求的方法
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody Map<String, String> loginRequest) {
        try {
            // 📥 第一步：解析请求参数
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");

            // 🔐 第二步：执行用户认证
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            // 🛡️ 第三步：设置安全上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 👤 第四步：获取完整用户信息
            Optional<User> userOpt = userRepository.findByUsername(username);
            User user = userOpt.orElse(null);

            // 👤 第五步：获取用户详情信息
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 🎫 第六步：生成JWT访问令牌
            String token = null;
            if (user != null) {
                token = jwtUtil.generateTokenWithUserId(userDetails, user.getId());
            } else {
                // 备用方案：如果用户信息获取失败，使用原来的方法
                token = jwtUtil.generateToken(userDetails);
            }

            // 👤 第七步：创建UserDTO对象
            UserDTO userDTO = null;
            if (user != null) {
                userDTO = new UserDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole()
                );
            }

            // 📊 第八步：创建LoginResponseDTO对象
            LoginResponseDTO loginResponseDTO = new LoginResponseDTO(
                token,
                "Bearer",
                jwtUtil.getExpiration(),
                userDTO,
                LocalDateTime.now().toString()
            );

            // 🚀 第九步：使用BaseController的success()方法 - 大幅简化！
            return success(loginResponseDTO, "登录成功");

        } catch (Exception e) {
            // 🚨 异常处理：使用BaseController的error()方法 - 大幅简化！
            log.warn("登录失败: {}", e.getMessage());
            return error(401, "用户名或密码错误");
        }
    }

    /**
     * 📝 用户注册接口 (Lombok + BaseController优化版本)
     *
     * 处理新用户注册请求，验证用户信息并创建新账户。
     *
     * 🚀 优化亮点：
     * - 使用BaseController的success()和error()方法，代码从17行缩减到2行
     * - 使用@Slf4j的log进行简洁的日志记录
     * - 保持完整业务逻辑的同时大幅简化响应处理
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
     * @return ResponseEntity<ApiResponse<RegisterResponseDTO>> 注册结果响应
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponseDTO>> register(@RequestBody Map<String, String> registerRequest) {
        try {
            // 📥 第一步：解析注册信息
            String username = registerRequest.get("username");
            String email = registerRequest.get("email");
            String password = registerRequest.get("password");

            // 🔍 第二步：检查用户名是否已存在
            if (userRepository.existsByUsername(username)) {
                // 🚨 使用BaseController的error()方法 - 一行搞定！
                return error("用户名已存在");
            }

            // 🔍 第三步：检查邮箱是否已存在
            if (userRepository.existsByEmail(email)) {
                // 🚨 使用BaseController的error()方法 - 一行搞定！
                return error("邮箱已存在");
            }

            // 👤 第四步：创建新用户实体
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);

            // 🔒 第五步：加密用户密码
            user.setPassword(passwordEncoder.encode(password));

            // 👑 第六步：设置用户角色和状态
            user.setRole("USER");
            user.setEnabled(true);

            // 💾 第七步：保存用户到数据库
            userRepository.save(user);

            log.info("新用户注册成功: {}", username);

            // 📊 第八步：创建RegisterResponseDTO对象
            RegisterResponseDTO registerResponseDTO = new RegisterResponseDTO(
                LocalDateTime.now().toString()
            );

            // 🚀 第九步：使用BaseController的success()方法 - 一行搞定！
            return success(201, registerResponseDTO, "注册成功");

        } catch (Exception e) {
            // 🚨 异常处理：使用BaseController的error()方法 - 一行搞定！
            log.error("注册失败: {}", e.getMessage(), e);
            return error(500, "注册失败: " + e.getMessage());
        }
    }

    /**
     * 👤 获取当前用户信息接口 (Lombok + BaseController优化版本)
     *
     * 处理获取当前认证用户信息的请求。
     * 这个接口需要用户已经通过JWT认证，会返回当前用户的完整信息。
     *
     * 🚀 优化亮点：
     * - 使用BaseController的success()和error()方法，代码从13行缩减到3行
     * - 简化异常处理逻辑
     * - 保持完整业务逻辑
     *
     * 用户信息获取流程：
     * 1. 从Spring Security上下文中获取当前认证的用户名
     * 2. 从数据库查询完整的用户信息
     * 3. 转换为UserDTO并返回
     *
     * @GetMapping: Spring Web注解，将HTTP GET请求映射到这个方法
     *              "/userinfo": 这个方法处理 /auth/userinfo 路径的请求
     *
     * @return ResponseEntity<ApiResponse<UserDTO>> 包含用户信息的HTTP响应
     */
    @GetMapping("/userinfo")
    public ResponseEntity<ApiResponse<UserDTO>> getUserInfo() {
        try {
            // 🔍 第一步：从安全上下文中获取当前认证信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                // 🚨 使用BaseController的error()方法 - 一行搞定！
                return error(401, "未认证");
            }

            // 👤 第二步：获取当前用户名
            String username = authentication.getName();

            // 🗄️ 第三步：从数据库查询用户信息
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (!userOpt.isPresent()) {
                // 🚨 使用BaseController的error()方法 - 一行搞定！
                return error(404, "用户不存在");
            }

            User user = userOpt.get();

            // 👤 第四步：创建UserDTO对象
            UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
            );

            // 🚀 第五步：使用BaseController的success()方法 - 一行搞定！
            return success(userDTO, "获取用户信息成功");

        } catch (Exception e) {
            // 🚨 异常处理：使用BaseController的error()方法 - 一行搞定！
            log.error("获取用户信息失败: {}", e.getMessage(), e);
            return error(500, "获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 🚪 用户登出接口 (Lombok + BaseController优化版本)
     *
     * 处理用户登出请求。
     * 由于JWT是无状态的，后端无法真正"注销"令牌。
     * 这个接口主要用于通知前端清除本地存储的认证信息。
     *
     * 🚀 优化亮点：
     * - 使用BaseController的success()和error()方法，代码从8行缩减到3行
     * - 使用@Slf4j的log替代手动logger
     * - 保持完整功能的同时简化代码
     *
     * 登出流程：
     * 1. 验证用户是否已认证（可选，因为登出时可能已经过期）
     * 2. 返回成功响应，前端收到后清除localStorage中的token和用户信息
     *
     * @PostMapping: Spring Web注解，将HTTP POST请求映射到这个方法
     *              "/logout": 这个方法处理 /auth/logout 路径的请求
     *
     * @return ResponseEntity<ApiResponse<Void>> 登出操作结果响应
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        try {
            // 🔍 可选：验证当前用户是否已认证
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                // 👤 获取当前用户名（用于日志记录，可选）
                String username = authentication.getName();
                log.info("用户登出: {}", username);
            }

            // 🚀 使用BaseController的success()方法 - 一行搞定！
            return success(null, "登出成功");

        } catch (Exception e) {
            // 🚨 异常处理：使用BaseController的error()方法 - 一行搞定！
            log.error("登出失败: {}", e.getMessage(), e);
            return error(500, "登出失败: " + e.getMessage());
        }
    }
}