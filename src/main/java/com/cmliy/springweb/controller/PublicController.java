// package: Java包声明，用于组织类和避免命名冲突
package com.cmliy.springweb.controller;

// import: 导入其他包中的类，以便在当前类中使用
import com.cmliy.springweb.common.ApiResponse;  // 导入统一API响应包装类
import com.cmliy.springweb.dto.HealthResponseDTO; // 导入健康检查响应DTO
import com.cmliy.springweb.repository.UserRepository;  // 导入用户数据访问层
import com.cmliy.springweb.util.JwtUtil;  // 导入JWT工具类
import org.springframework.http.ResponseEntity;  // 导入Spring HTTP响应实体类，用于构建HTTP响应
import org.springframework.web.bind.annotation.GetMapping;  // 导入Spring Web GET请求映射注解
import org.springframework.web.bind.annotation.RequestMapping;  // 导入Spring Web请求映射注解
import org.springframework.web.bind.annotation.RestController;  // 导入Spring Web REST控制器注解

import java.time.LocalDateTime;  // 导入Java 8日期时间类，用于获取当前时间

// 🚀 Lombok注解导入 - 大幅简化样板代码
import lombok.extern.slf4j.Slf4j;        // @Slf4j: 自动生成Logger实例
// 注意：@RequiredArgsConstructor在继承时有限制，需要手动构造函数

/**
 * 🌐 公开访问控制器 (Lombok + BaseController优化版本)
 *
 * 这个类处理不需要认证的公开API请求。
 * 所有在 /public 路径下的请求都可以被任何人访问，
 * 不需要登录或提供JWT令牌。
 *
 * 🚀 Lombok优化展示：
 * - @Slf4j: 自动生成Logger实例，无需手动创建
 * - 继承BaseController: 获得统一的API响应方法
 *
 * 🚀 BaseController集成优势：
 * - success(): 统一成功响应方法
 * - error(): 统一错误响应方法
 * - logOperation(): 统一操作日志记录
 * - getCurrentUsername(): 获取当前用户名（对于公共接口返回"anonymous"）
 *
 * @RestController: Spring框架注解，标记这是一个REST API控制器，
 *                  Spring会自动扫描并注册这个类为Bean，
 *                  同时表明这个类中的所有方法都返回JSON数据。
 */
@Slf4j  // 🚀 Lombok: 自动生成 private static final Logger log = LoggerFactory.getLogger(PublicController.class);
@RestController // @RestController注解：声明这是一个REST控制器类
@RequestMapping("/public")  // @RequestMapping注解：为整个控制器设置基础路径
public class PublicController extends BaseController {  // 🚀 继承BaseController获得统一响应方法

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
    public PublicController(UserRepository userRepository, JwtUtil jwtUtil) {
        // 🚀 调用父类构造函数，传递基类需要的字段
        super(userRepository, jwtUtil);
    }

    /**
     * 💊 应用健康检查接口 (BaseController优化版本)
     *
     * 这个接口用于检查应用是否正常运行，
     * 通常用于负载均衡器健康检查或监控系统。
     *
     * 🚀 优化亮点：
     * - 使用BaseController的success()方法简化响应构建
     * - 使用@Slf4j的log替代手动logger
     * - 添加操作日志记录，便于监控和调试
     * - 展示getCurrentUsername()的用法（公共接口返回"anonymous"）
     *
     * @GetMapping: Spring Web注解，将HTTP GET请求映射到这个方法
     *              "/health": 这个方法处理 /public/health 路径的请求
     *
     * @param: 无参数，因为这是一个简单的GET请求
     * @return: ResponseEntity<ApiResponse<HealthResponseDTO>> - 包含状态信息的HTTP响应
     */
    @GetMapping("/health")  // @GetMapping注解：声明这是一个处理GET请求的方法
    public ResponseEntity<ApiResponse<HealthResponseDTO>> health() {  // public方法：公开访问，返回HTTP响应实体

        try {
            // 📊 创建HealthResponseDTO对象
            HealthResponseDTO healthResponseDTO = new HealthResponseDTO(
                "UP",
                LocalDateTime.now().toString(),
                "SpringWeb",
                "1.0.0"
            );

            // 🚀 使用BaseController的getCurrentUsername()方法获取当前用户名
            String currentUser = getCurrentUsername(); // 对于公共接口会返回"anonymous"

            // 🚀 使用BaseController的logOperation()方法记录操作
            logOperation("健康检查", "用户: " + currentUser + " 执行了健康检查");

            // 🚀 使用BaseController的success()方法 - 一行搞定！
            return success(healthResponseDTO, "应用正常运行");

        } catch (Exception e) {
            // 🚨 使用BaseController的error()方法处理异常
            log.error("健康检查接口访问失败: {}", e.getMessage(), e);
            return error(500, "健康检查失败");
        }
    }
}