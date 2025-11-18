---
tags:
  - 基础配置
  - @Configuration
  - @Bean
  - Bean管理
  - 配置类
  - Spring Boot
created: 2025-11-16
modified: 2025-11-16
category: 配置管理
difficulty: intermediate
---

# Spring Boot 基础配置详解

## 目录
- [@Configuration 注解](#configuration-注解)
- [@Bean 注解](#bean-注解)
- [Spring Bean 的概念](#spring-bean-的概念)
- [Bean 和 @Autowired 的关系](#bean-和-autowired-的关系)
- [组件扫描机制](#组件扫描机制)
- [基础配置最佳实践](#基础配置最佳实践)

---

## @Configuration 注解

### 基本概念

`@Configuration` 是一个"工厂类"，它里面包含了制造特殊 Bean 的"生产线"。这个"生产线"就是 `@Bean` 注解。

### 核心作用

> [!IMPORTANT] 核心作用
> 告诉Spring这是一个配置类，类中的 `@Bean` 方法会被Spring容器处理，用于创建和配置Bean对象。

### 基本用法

```java
@Configuration  // ← 告诉Spring：这是一个"工厂"类
public class MyFactory {

    @Bean        // ← 告诉Spring：这是工厂里的一条"生产线"
    public RestTemplate restTemplate() { // 生产线的方法名，就是Bean的名字
        // 在这里写下"如何制造"这个Bean的详细步骤
        return new RestTemplate(); // 造出一个 RestTemplate 对象
    }
}
```

### 完整示例

```java
// AppConfiguration.java - 这是你的工厂配置文件
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration // ← 我是工厂！
public class AppConfiguration {

    @Bean // ← 我是生产线！我要生产一个叫 "restTemplate" 的Bean
    public RestTemplate restTemplate() {
        // 制造过程：直接 new 一个 RestTemplate 对象
        return new RestTemplate();
    }

    @Bean("fastPayment") // 给Bean起自定义名字
    public PaymentService fastPaymentService() {
        return new FastPaymentService();
    }

    @Bean("slowPayment") // 再起个名字 "slowPayment"
    public PaymentService slowPaymentService() {
        return new SlowPaymentService();
    }
}
```

### 使用配置的Bean

```java
@Service
public class WeatherService {

    @Autowired // ← "喂！Spring！我需要一个网络请求工具！"
    private RestTemplate restTemplate; // Spring会自动把工厂里造好的那个给你

    public String getWeather(String city) {
        String url = "http://api.weather.com/" + city;
        // 直接用！你都不用管它是怎么来的
        return restTemplate.getForObject(url, String.class);
    }
}
```

---

## @Bean 注解

### 基本概念

`@Bean` 注解用于在 `@Configuration` 类中声明Bean，它告诉Spring如何创建和配置特定的Bean实例。

### Bean命名规则

```java
@Configuration
public class AppConfig {

    // 默认命名：方法名就是Bean名
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // 自定义命名
    @Bean("userRestTemplate")
    public RestTemplate createUserRestTemplate() {
        return new RestTemplate();
    }

    // 多个别名
    @Bean(name = {"paymentTemplate", "orderTemplate", "billingTemplate"})
    public RestTemplate paymentRestTemplate() {
        return new RestTemplate();
    }
}
```

### Bean初始化和销毁

```java
@Configuration
public class LifecycleConfig {

    @Bean(initMethod = "init", destroyMethod = "cleanup")
    public DatabaseService databaseService() {
        return new DatabaseService();
    }
}

// 对应的服务类
public class DatabaseService {

    public void init() {
        System.out.println("数据库服务初始化...");
        // 连接数据库等初始化操作
    }

    public void cleanup() {
        System.out.println("数据库服务清理...");
        // 关闭连接等清理操作
    }
}
```

---

## Spring Bean 的概念

> [!NOTE] 核心概念
> **一句话解释：**
>
> > Spring Bean 就是一个 **被 Spring 框架管理的 Java 对象**。

> [!TIP] 管理含义
> "被管理"意味着：
>
> 1. **Spring 负责创建它** (你不用写 `new MyService()`)
> 2. **Spring 负责储存它** (放在一个叫 "Spring容器" 的特殊仓库里)
> 3. **Spring 负物把它交给需要它的地方** (通过 `@Autowired` 自动注入)

### 如何制作 Spring Bean

最常用的标签有四个，但新手阶段只用记住 `@Component` 和 `@Service`：

```java
// 1. 写一个普通的Java类
public class EmailService {
    public void sendEmail(String to) {
        System.out.println("正在给 " + to + " 发送邮件...");
    }
}

// 2. 给它加一个 "我是豆子" 的标签
import org.springframework.stereotype.Service;

@Service  // ← 就加这一行！告诉Spring：请把我变成一个Bean！
public class EmailService {
    public void sendEmail(String to) {
        System.out.println("正在给 " + to + " 发送邮件...");
    }
}
```

### Bean作用域

```java
@Component
@Scope("prototype") // 每次注入都创建新实例
public class PrototypeBean {
    // ...
}

@Component
@Scope("singleton") // 默认作用域，整个应用只有一个实例
public class SingletonBean {
    // ...
}

@Configuration
public class ScopeConfig {

    @Bean
    @Scope("request") // Web应用中，每个请求一个实例
    public RequestScopedBean requestBean() {
        return new RequestScopedBean();
    }

    @Bean
    @Scope("session") // Web应用中，每个会话一个实例
    public SessionScopedBean sessionBean() {
        return new SessionScopedBean();
    }
}
```

---

## Bean 和 @Autowired 的关系

- `@Service` **/** `@Component`: **定义Bean**。就像在仓库注册："嘿Spring，我这里有货，请管好它！"
- `@Autowired`: **使用Bean**。就像下单："嘿Spring，我需要一个这玩意儿，给我发货！"

### 完整流程示例

```java
// 步骤1：定义一个Bean (注册商品)
@Service // "我是一个邮件服务Bean！"
public class EmailService {
    public void send(String message) {
        System.out.println("邮件已发送：" + message);
    }
}

// 步骤2：在别的地方使用它 (下单购买)
@RestController
public class MessageController {

    @Autowired // "Spring！我需要一个EmailService，请给我一个！"
    private EmailService emailService; // Spring会自动把仓库里的EmailService实例放这里

    @GetMapping("/send")
    public String sendMsg() {
        emailService.send("你好，世界！"); // 直接用！
        return "发送成功！";
    }
}
```

---

## 组件扫描机制

### @ComponentScan 注解

**核心职责**

如果说 `@Service` 是给产品贴上标签，那么 `@ComponentScan` 就是那个**寻找所有带标签产品，并把它们搬进仓库的"自动扫描机器人"**。

> 它明确地告诉Spring："请从这个'基础包'开始，连同它下面所有的子包，去扫描一遍。凡是看到带有 `@Component`, `@Service`, `@Controller`, `@Repository` 这些标签的类，统统给我创建实例，放进容器里！"

### 默认行为

你可能会有疑问："我好像从来没在代码里写过 `@ComponentScan`，为什么我的 `@Service` 也生效了？"

**因为** `@SpringBootApplication` **这个大礼包里，已经自带了一个！**

```java
@SpringBootApplication
// ↓ 这是一个复合注解，它包含了...
public @interface SpringBootApplication {
	// ...其他注解
	@EnableAutoConfiguration
	@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
			@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
	// ...其他注解
}
```

**默认扫描规则：**

> **它会扫描你的"主启动类"（比如** `XxxApplication`**）所在的包，以及其所有子孙包。**

**项目结构示例：**

```text
com
└── example
    └── myproject
        ├── MyProjectApplication.java  🗺️ **主启动类在这里！**
        ├── controller
        │   └── UserController.java
        ├── service
        │   └── UserService.java
        ├── repository
        │   └── UserRepository.java
        ├── config
       │   └── AppConfig.java
        └── model
            └── User.java
```

- `MyProjectApplication` 在 `com.example.myproject` 包下。
- `@ComponentScan` 会自动扫描 `com.example.myproject` 包。
- `controller`, `service`, `repository`, `config`, `model` 这些都是它的子包。
- 所以，所有这些包下的 `@Service`, `@Controller` 等都会被Spring找到并管理！

### 手动配置扫描路径

当你把Bean放在了默认扫描路径之外时，需要手动配置：

```java
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.myproject",  // 自己的项目包
    "com.company.common.utils" // 第三方工具类包
})
public class MyProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyProjectApplication.class, args);
    }
}
```

### 排除特定组件

```java
@ComponentScan(
    basePackages = "com.example.myproject",
    excludeFilters = {
        @Filter(type = FilterType.ANNOTATION, classes = {Controller.class}),
        @Filter(type = FilterType.REGEX, pattern = "com\\.example\\.myproject\\.test\\..*")
    }
)
public class MyProjectApplication {
    // ...
}
```

---

## 基础配置最佳实践

### 1. Bean命名规范

```java
@Configuration
public class AppConfig {

    // 推荐：使用有意义的名字
    @Bean("userRestTemplate")
    public RestTemplate userRestTemplate() {
        return new RestTemplate();
    }

    @Bean("paymentRestTemplate")
    public RestTemplate paymentRestTemplate() {
        RestTemplate template = new RestTemplate();
        // 特殊配置
        return template;
    }
}
```

### 2. 配置类组织原则

```java
// 按功能模块组织配置类
@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        return new HikariDataSource();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}

@Configuration
public class WebConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
```

### 3. 条件化Bean创建

```java
@Configuration
public class ConditionalConfig {

    @Bean
    @ConditionalOnProperty(name = "app.cache.enabled", havingValue = "true")
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("users", "products");
    }

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource defaultDataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
    }
}
```

### 4. Bean依赖关系管理

```java
@Configuration
public class DependencyConfig {

    // 先创建依赖的Bean
    @Bean
    public DataSource dataSource() {
        return new HikariDataSource();
    }

    // 然后创建依赖上述Bean的Bean
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    // 方法参数自动注入，Spring会找到对应的Bean
    @Bean
    public UserService userService(JdbcTemplate jdbcTemplate, EmailService emailService) {
        return new UserService(jdbcTemplate, emailService);
    }
}
```

---

## 总结

### 核心概念对比

| 概念 | 比喻 | 代码示例 | 作用 |
|------|------|----------|------|
| **Spring Bean** | 乐高工厂生产的积木 | `EmailService` 的实例 | Spring帮你创建和管理好的对象 |
| **制作Bean的注解** | 给积木贴的"生产"标签 | `@Service`, `@Component` | 告诉Spring："请把我的类变成Bean！" |
| **使用Bean的注解** | "我需要这个积木"的订单 | `@Autowired` | 告诉Spring："请在我需要的地方自动给我Bean！" |
| **配置类** | 私人定制工厂 | `@Configuration` + `@Bean` | 管理复杂Bean的创建逻辑 |
| **组件扫描** | 自动收集机器人 | `@ComponentScan` | 自动发现并注册所有带注解的类 |

### 最佳实践总结

1. **合理使用 `@Configuration` + `@Bean`** 管理复杂对象的创建
2. **遵循组件扫描规则**，合理组织项目结构
3. **使用有意义的Bean命名**，提高代码可读性
4. **按功能模块组织配置类**，保持配置的清晰性
5. **善用条件注解**，实现灵活的配置管理
6. **保持配置的简洁性**，避免过度复杂的配置逻辑

---

## 相关学习笔记

- [[02-属性配置(Properties,YAML,Environment等)]] - 属性和环境配置详解
- [[03-自动配置(@EnableAutoConfiguration,@Conditional等)]] - Spring Boot自动配置机制
- [[04-外部化配置(配置文件,环境变量等)]] - 外部化配置源管理

## 相关技术文档

- Bean生命周期管理 - Bean创建和销毁的完整流程
- 依赖注入详解 - 深入理解DI机制
- 组件扫描原理 - @ComponentScan工作机制详解