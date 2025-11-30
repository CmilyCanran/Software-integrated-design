// package: Java包声明，用于组织类和避免命名冲突
package com.cmliy.springweb.controller;

// import: 导入其他包中的类，以便在当前类中使用
import com.cmliy.springweb.common.ApiResponse;  // 导入统一API响应包装类
import com.cmliy.springweb.repository.UserRepository;  // 导入用户数据访问层
import com.cmliy.springweb.util.JwtUtil;  // 导入JWT工具类
import org.springframework.http.ResponseEntity;  // 导入Spring HTTP响应实体类，用于构建HTTP响应
import org.springframework.security.access.prepost.PreAuthorize; // 导入方法级安全注解
import org.springframework.web.bind.annotation.GetMapping;  // 导入Spring Web GET请求映射注解
import org.springframework.web.bind.annotation.RequestMapping;  // 导入Spring Web请求映射注解
import org.springframework.web.bind.annotation.RestController;  // 导入Spring Web REST控制器注解

import java.time.LocalDateTime;  // 导入Java 8日期时间类，用于获取当前时间

// 🚀 Lombok注解导入 - 大幅简化样板代码
import lombok.extern.slf4j.Slf4j;        // @Slf4j: 自动生成Logger实例
// 注意：@RequiredArgsConstructor在继承时有限制，需要手动构造函数

/**
 * 🛡️ 管理员控制器 (Lombok + BaseController优化版本)
 *
 * 这个类处理需要管理员权限的API请求。
 * 所有在 /admin 路径下的请求都需要ADMIN角色。
 *
 * 🚀 Lombok优化展示：
 * - @Slf4j: 自动生成Logger实例，无需手动创建
 * - @RequiredArgsConstructor: 自动生成包含所有final字段的构造函数
 * - 继承BaseController: 获得统一的API响应方法
 *
 * 🚀 BaseController集成优势：
 * - success(): 统一成功响应方法
 * - error(): 统一错误响应方法
 * - getCurrentUserId(): 获取当前用户ID
 * - logOperation(): 统一操作日志记录
 *
 * 安全控制说明：
 * - 路径级安全：/admin/** 需要ADMIN角色（在SecurityConfig中配置）
 * - 方法级安全：使用@PreAuthorize注解进行细粒度控制
 * - 数据安全：敏感操作需要特定权限
 *
 * @RestController: Spring框架注解，标记这是一个REST API控制器，
 *                  Spring会自动扫描并注册这个类为Bean，
 *                  同时表明这个类中的所有方法都返回JSON数据。
 */
@Slf4j  // 🚀 Lombok: 自动生成 private static final Logger log = LoggerFactory.getLogger(AdminController.class);
@RestController // @RestController注解：声明这是一个REST控制器类
@RequestMapping("/admin")  // @RequestMapping注解：为整个控制器设置基础路径
public class AdminController extends BaseController {  // 🚀 继承BaseController获得统一响应方法

    /**
     * 🏗️ 构造函数（手动创建 - Lombok继承限制）
     *
     * 🎓 Lombok教学要点：
     * - @RequiredArgsConstructor在继承时无法处理父类字段
     * - 这是Lombok的一个已知限制，适合教学展示
     * 手动编写构造函数能更好地理解继承和依赖注入
     *
     * @param userRepository 用户数据访问层（传递给基类）
     * @param jwtUtil JWT工具类（传递给基类）
     */
    public AdminController(UserRepository userRepository, JwtUtil jwtUtil) {
        // 🚀 调用父类构造函数，传递基类需要的字段
        super(userRepository, jwtUtil);
    }

    /**
     * 🛡️ 管理员仪表板接口 (BaseController优化版本)
     *
     * 这个接口返回管理员仪表板的基本信息。
     * 只有拥有ADMIN角色的用户才能访问。
     *
     * 🚀 优化亮点：
     * - 使用BaseController的success()方法简化响应构建
     * - 使用@Slf4j的log替代手动logger
     * - 添加操作日志记录，便于审计
     *
     * 安全控制：
     * - 路径级：/admin/dashboard 需要ADMIN角色
     * - 方法级：@PreAuthorize注解确保调用者有ADMIN角色
     *
     * @GetMapping: Spring Web注解，将HTTP GET请求映射到这个方法
     *              "/dashboard": 这个方法处理 /admin/dashboard 路径的请求
     *
     * @PreAuthorize: Spring Security注解，在方法执行前检查权限
     *                "hasRole('ADMIN')": 要求用户拥有ADMIN角色
     *                注意：Spring Security会自动添加ROLE_前缀
     *
     * @return ResponseEntity<ApiResponse<String>> 包含管理员信息的HTTP响应
     */
    @GetMapping("/dashboard")  // @GetMapping注解：声明这是一个处理GET请求的方法
    @PreAuthorize("hasRole('ADMIN')")  // @PreAuthorize注解：方法级安全控制
    public ResponseEntity<ApiResponse<String>> adminDashboard() {  // public方法：公开访问，返回HTTP响应实体

        // 📊 创建管理员仪表板数据
        String dashboardData = "管理员仪表板 - 访问时间: " + LocalDateTime.now();

        // 🚀 使用BaseController的logOperation()方法记录操作
        logOperation("管理员仪表板访问", "管理员访问了仪表板");

        // 🚀 使用BaseController的success()方法 - 一行搞定！
        return success(dashboardData, "管理员访问成功");
    }

    /**
     * 👥 用户管理接口 (BaseController优化版本)
     *
     * 这个接口返回用户管理相关的信息。
     * 只有拥有ADMIN角色的用户才能访问。
     *
     * 🚀 优化亮点：
     * - 使用BaseController的success()方法简化响应构建
     * - 使用@Slf4j的log替代手动logger
     * - 添加操作日志记录，便于审计
     * - 展示getCurrentUserId()的用法
     *
     * @GetMapping: Spring Web注解，将HTTP GET请求映射到这个方法
     *              "/users": 这个方法处理 /admin/users 路径的请求
     *
     * @PreAuthorize: Spring Security注解，在方法执行前检查权限
     *                "hasRole('ADMIN')": 要求用户拥有ADMIN角色
     *
     * @return ResponseEntity<ApiResponse<String>> 包含用户管理信息的HTTP响应
     */
    @GetMapping("/users")  // @GetMapping注解：声明这是一个处理GET请求的方法
    @PreAuthorize("hasRole('ADMIN')")  // @PreAuthorize注解：方法级安全控制
    public ResponseEntity<ApiResponse<String>> userManagement() {  // public方法：公开访问，返回HTTP响应实体

        try {
            // 👥 创建用户管理数据
            int userCount = (int)(Math.random() * 1000);
            String managementData = "用户管理界面 - 当前用户总数: " + userCount;

            // 🚀 使用BaseController的getCurrentUserId()方法获取当前管理员ID
            Long adminId = getCurrentUserId();

            // 🚀 使用BaseController的logOperation()方法记录操作（带目标ID）
            logOperation("用户管理访问", adminId, "管理员ID: " + adminId + " 查看了用户管理，用户总数: " + userCount);

            // 🚀 使用BaseController的success()方法 - 一行搞定！
            return success(managementData, "用户管理访问成功");

        } catch (Exception e) {
            // 🚨 使用BaseController的error()方法处理异常
            log.error("用户管理接口访问失败: {}", e.getMessage(), e);
            return error(500, "用户管理数据获取失败");
        }
    }
}