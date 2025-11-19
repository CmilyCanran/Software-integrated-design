// package: Java包声明，用于组织类和避免命名冲突
package com.cmliy.springweb.config;

// import: 导入其他包中的类，以便在当前类中使用
import com.fasterxml.jackson.databind.ObjectMapper;           // 导入Jackson JSON处理库
import com.fasterxml.jackson.databind.SerializationFeature;   // 导入Jackson序列化特性
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;  // 导入Java 8时间模块
import org.springframework.context.annotation.Bean;            // 导入Spring Bean注解
import org.springframework.context.annotation.Configuration;      // 导入Spring配置注解

/**
 * 📄 JSON配置类
 *
 * 这个配置类用于自定义Jackson ObjectMapper的行为。
 * ObjectMapper是Jackson库的核心类，负责Java对象与JSON之间的转换。
 *
 * 在Spring Boot中，通常会自动配置ObjectMapper，但显式配置可以更好地控制行为。
 *
 * @Configuration: Spring框架注解，标记这是一个配置类，
 *                  Spring容器会扫描并处理其中的@Bean方法。
 */
@Configuration // @Configuration注解：声明这是一个Spring配置类
public class JsonConfig { // public class: 定义公共类，其他类可以访问

    /**
     * 📄 创建ObjectMapper Bean
     *
     * 自定义ObjectMapper的配置，包括时间格式处理和序列化特性。
     * 这个Bean会被Spring容器管理，并注入到需要的地方。
     *
     * @Bean: Spring框架注解，声明这个方法返回一个Bean对象
     * @return ObjectMapper: 配置好的ObjectMapper实例
     */
    @Bean // @Bean注解：将方法返回值注册为Spring容器中的Bean
    public ObjectMapper objectMapper() { // public方法：返回ObjectMapper对象
        // ObjectMapper: Jackson的核心类，提供JSON序列化和反序列化功能
        ObjectMapper mapper = new ObjectMapper(); // 创建ObjectMapper实例

        // 🕐 注册Java 8时间模块
        // JavaTimeModule: 支持Java 8日期时间类型的序列化
        mapper.registerModule(new JavaTimeModule()); // 注册时间模块，支持LocalDateTime等类型

        // ⚙️ 配置序列化特性
        // SerializationFeature.WRITE_DATES_AS_TIMESTAMPS: 禁用将日期写为时间戳
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 使用ISO-8601格式而不是时间戳

        // ✅ 配置其他有用的特性
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 忽略未知属性

        // 📤 返回配置好的ObjectMapper
        return mapper; // 返回配置完成的ObjectMapper实例
    }
}