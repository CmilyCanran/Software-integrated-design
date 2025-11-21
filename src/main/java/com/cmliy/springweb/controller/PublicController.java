// package: Java包声明，用于组织类和避免命名冲突
package com.cmliy.springweb.controller;

// import: 导入其他包中的类，以便在当前类中使用
import com.cmliy.springweb.common.ApiResponse;  // 导入统一API响应包装类
import org.springframework.http.ResponseEntity;  // 导入Spring HTTP响应实体类，用于构建HTTP响应
import org.springframework.web.bind.annotation.GetMapping;  // 导入Spring Web GET请求映射注解
import org.springframework.web.bind.annotation.RequestMapping;  // 导入Spring Web请求映射注解
import org.springframework.web.bind.annotation.RestController;  // 导入Spring Web REST控制器注解

import java.time.LocalDateTime;  // 导入Java 8日期时间类，用于获取当前时间
import java.util.HashMap;        // 导入Java Map接口实现，用于存储键值对数据
import java.util.Map;           // 导入Java Map接口，定义键值对集合的规范

/**
 * 🌐 公开访问控制器
 *
 * 这个类处理不需要认证的公开API请求。
 * 所有在 /api/public 路径下的请求都可以被任何人访问，
 * 不需要登录或提供JWT令牌。
 *
 * @RestController: Spring框架注解，标记这是一个REST API控制器，
 *                  Spring会自动扫描并注册这个类为Bean，
 *                  同时表明这个类中的所有方法都返回JSON数据。
 */
@RestController // @RestController注解：声明这是一个REST控制器类
@RequestMapping("/api/public")  // @RequestMapping注解：为整个控制器设置基础路径
public class PublicController {  // public class: 定义公共类，其他类可以访问

    /**
     * 💊 应用健康检查接口
     *
     * 这个接口用于检查应用是否正常运行，
     * 通常用于负载均衡器健康检查或监控系统。
     *
     * @GetMapping: Spring Web注解，将HTTP GET请求映射到这个方法
     *              "/health": 这个方法处理 /api/public/health 路径的请求
     *
     * @param: 无参数，因为这是一个简单的GET请求
     * @return: ResponseEntity<ApiResponse<Map<String, Object>>> - 包含状态信息的HTTP响应
     */
    @GetMapping("/health")  // @GetMapping注解：声明这是一个处理GET请求的方法
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {  // public方法：公开访问，返回HTTP响应实体

        // 🗂️ 创建响应数据容器
        // HashMap<String, Object>: 创建一个Map来存储响应数据，键为String类型，值为Object类型
        // Map: Java集合框架中的接口，用于存储键值对（key-value pairs）
        Map<String, Object> healthData = new HashMap<>();  // 创建HashMap实例，用于存储响应数据

        // 📊 填充响应数据
        // .put(key, value): Map接口的方法，向Map中添加键值对
        healthData.put("status", "UP");                    // 设置应用状态：UP表示正常运行
        healthData.put("timestamp", LocalDateTime.now().toString());  // 设置当前时间戳
        healthData.put("application", "SpringWeb");         // 设置应用名称
        healthData.put("version", "1.0.0");                // 设置应用版本号

        // 📤 构建标准响应格式
        ApiResponse<Map<String, Object>> response = ApiResponse.success(healthData, "应用正常运行");

        // 📤 返回HTTP响应
        // ResponseEntity.ok(): 静态方法，创建HTTP状态码为200(OK)的响应
        // 200 OK: HTTP状态码，表示请求成功处理
        return ResponseEntity.ok(response);  // 返回包含响应数据的HTTP 200响应
    }
}