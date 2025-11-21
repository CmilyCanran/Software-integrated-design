// package: Java包声明，用于组织类和避免命名冲突
package com.cmliy.springweb.controller;

// import: 导入其他包中的类，以便在当前类中使用
import com.cmliy.springweb.common.ApiResponse;  // 导入统一API响应包装类
import com.cmliy.springweb.dto.HealthResponseDTO; // 导入健康检查响应DTO
import org.springframework.http.ResponseEntity;  // 导入Spring HTTP响应实体类，用于构建HTTP响应
import org.springframework.web.bind.annotation.GetMapping;  // 导入Spring Web GET请求映射注解
import org.springframework.web.bind.annotation.RequestMapping;  // 导入Spring Web请求映射注解
import org.springframework.web.bind.annotation.RestController;  // 导入Spring Web REST控制器注解

import java.time.LocalDateTime;  // 导入Java 8日期时间类，用于获取当前时间

/**
 * 🌐 公开访问控制器
 *
 * 这个类处理不需要认证的公开API请求。
 * 所有在 /public 路径下的请求都可以被任何人访问，
 * 不需要登录或提供JWT令牌。
 *
 * @RestController: Spring框架注解，标记这是一个REST API控制器，
 *                  Spring会自动扫描并注册这个类为Bean，
 *                  同时表明这个类中的所有方法都返回JSON数据。
 */
@RestController // @RestController注解：声明这是一个REST控制器类
@RequestMapping("/public")  // @RequestMapping注解：为整个控制器设置基础路径
public class PublicController {  // public class: 定义公共类，其他类可以访问

    /**
     * 💊 应用健康检查接口
     *
     * 这个接口用于检查应用是否正常运行，
     * 通常用于负载均衡器健康检查或监控系统。
     *
     * @GetMapping: Spring Web注解，将HTTP GET请求映射到这个方法
     *              "/health": 这个方法处理 /public/health 路径的请求
     *
     * @param: 无参数，因为这是一个简单的GET请求
     * @return: ResponseEntity<ApiResponse<HealthResponseDTO>> - 包含状态信息的HTTP响应
     */
    @GetMapping("/health")  // @GetMapping注解：声明这是一个处理GET请求的方法
    public ResponseEntity<ApiResponse<HealthResponseDTO>> health() {  // public方法：公开访问，返回HTTP响应实体

        // 📊 创建HealthResponseDTO对象
        HealthResponseDTO healthResponseDTO = new HealthResponseDTO(
            "UP",
            LocalDateTime.now().toString(),
            "SpringWeb",
            "1.0.0"
        );

        // 📤 构建标准响应格式
        ApiResponse<HealthResponseDTO> response = ApiResponse.success(healthResponseDTO, "应用正常运行");

        // 📤 返回HTTP响应
        // ResponseEntity.ok(): 静态方法，创建HTTP状态码为200(OK)的响应
        // 200 OK: HTTP状态码，表示请求成功处理
        return ResponseEntity.ok(response);  // 返回包含响应数据的HTTP 200响应
    }
}