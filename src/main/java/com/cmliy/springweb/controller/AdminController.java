// package: Java包声明，用于组织类和避免命名冲突
package com.cmliy.springweb.controller;

// import: 导入其他包中的类，以便在当前类中使用
import com.cmliy.springweb.common.ApiResponse;  // 导入统一API响应包装类
import org.springframework.http.ResponseEntity;  // 导入Spring HTTP响应实体类，用于构建HTTP响应
import org.springframework.security.access.prepost.PreAuthorize; // 导入方法级安全注解
import org.springframework.web.bind.annotation.GetMapping;  // 导入Spring Web GET请求映射注解
import org.springframework.web.bind.annotation.RequestMapping;  // 导入Spring Web请求映射注解
import org.springframework.web.bind.annotation.RestController;  // 导入Spring Web REST控制器注解

import java.time.LocalDateTime;  // 导入Java 8日期时间类，用于获取当前时间

/**
 * 🛡️ 管理员控制器
 *
 * 这个类处理需要管理员权限的API请求。
 * 所有在 /admin 路径下的请求都需要ADMIN角色。
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
@RestController // @RestController注解：声明这是一个REST控制器类
@RequestMapping("/admin")  // @RequestMapping注解：为整个控制器设置基础路径
public class AdminController {  // public class: 定义公共类，其他类可以访问

    /**
     * 🛡️ 管理员仪表板接口
     *
     * 这个接口返回管理员仪表板的基本信息。
     * 只有拥有ADMIN角色的用户才能访问。
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

        // 📤 构建标准响应格式
        ApiResponse<String> response = ApiResponse.success(dashboardData, "管理员访问成功");

        // 📤 返回HTTP响应
        // ResponseEntity.ok(): 静态方法，创建HTTP状态码为200(OK)的响应
        // 200 OK: HTTP状态码，表示请求成功处理
        return ResponseEntity.ok(response);  // 返回包含响应数据的HTTP 200响应
    }

    /**
     * 👥 用户管理接口
     *
     * 这个接口返回用户管理相关的信息。
     * 只有拥有ADMIN角色的用户才能访问。
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

        // 👥 创建用户管理数据
        String managementData = "用户管理界面 - 当前用户总数: " + (int)(Math.random() * 1000);

        // 📤 构建标准响应格式
        ApiResponse<String> response = ApiResponse.success(managementData, "用户管理访问成功");

        // 📤 返回HTTP响应
        return ResponseEntity.ok(response);  // 返回包含响应数据的HTTP 200响应
    }
}