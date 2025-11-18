---
tags:
  - Spring注解
  - @Component
  - @Service
  - @Autowired
  - @Configuration
  - 注解体系
created: 2025-11-18
modified: 2025-11-18
category: Spring核心概念
difficulty: beginner
---

# Spring注解体系概览

> **学习目标**：掌握Spring注解的分类和使用，理解每个注解的作用和适用场景

## 🎯 注解的重要性

Spring注解是Spring框架的"语言"，通过注解告诉Spring：
- 哪些类需要被管理
- 如何管理这些类
- 类之间的关系是什么

**注解的好处：**
- 简化配置，减少XML文件
- 提高代码可读性
- IDE智能提示支持
- 编译时检查

---

## 🏷️ Bean定义注解

### 核心注解：@Component系列

这些注解告诉Spring："请把我变成一个Bean！"

```java
// 1. @Component - 通用组件注解
@Component  // 最基础的Bean定义
public class UtilityService {
    public String doSomething() {
        return "工具类在工作";
    }
}

// 2. @Service - 服务层组件
@Service  // 业务逻辑层使用
public class UserService {
    public User findById(Long id) {
        // 业务逻辑
        return new User();
    }
}

// 3. @Repository - 数据访问层组件
@Repository  // 数据访问层使用
public class UserRepository {
    public User save(User user) {
        // 数据库操作
        return user;
    }
}

// 4. @Controller - Web层组件
@Controller  // Web控制器使用（传统MVC）
public class UserController {
    @RequestMapping("/users")
    public String listUsers() {
        return "user-list";  // 返回视图名称
    }
}

// 5. @RestController - REST API控制器
@RestController  // REST API使用
public class UserApiController {
    @GetMapping("/api/users")
    public List<User> getUsers() {
        return Arrays.asList(new User(), new User());
    }
}
```

### 注解选择指南

| 注解 | 适用场景 | 示例 |
|------|----------|------|
| `@Component` | 通用组件、工具类 | `DateUtils`、`StringUtils` |
| `@Service` | 业务逻辑层 | `UserService`、`OrderService` |
| `@Repository` | 数据访问层 | `UserRepository`、`ProductDao` |
| `@Controller` | Web MVC控制器 | `WebController` |
| `@RestController` | REST API控制器 | `ApiController` |

> **💡 实际上，这些注解功能完全相同！**<br>
> 只是为了代码可读性而区分使用场景。

---

## 💉 依赖注入注解

### @Autowired - 自动装配

Spring最常用的注入注解：

```java
@Service
public class OrderService {

    @Autowired  // 自动注入UserService
    private UserService userService;

    @Autowired  // 自动注入ProductService
    private ProductService productService;

    @Autowired  // 也可以注入List，会包含所有匹配的Bean
    private List<NotificationService> notificationServices;

    public void createOrder(Order order) {
        userService.updateUserPoints(order.getUserId());
        productService.decreaseStock(order.getProductId());

        // 发送所有通知
        notificationServices.forEach(service ->
            service.notify("订单创建成功"));
    }
}
```

### @Autowired的三种注入方式

#### 1. 字段注入（最常用，但不推荐）
```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;  // 直接在字段上注入
}
```

#### 2. 构造函数注入（推荐！）
```java
@Service
public class UserService {
    private final UserRepository userRepository;

    // 构造函数注入 - 推荐方式
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

#### 3. Setter注入
```java
@Service
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

### @Qualifier - 指定Bean名称

当有多个相同类型的Bean时使用：

```java
// 定义多个邮件服务
@Service("emailServiceV1")
public class EmailServiceV1 implements EmailService {
    public void send(String to, String msg) {
        System.out.println("V1发送：" + msg);
    }
}

@Service("emailServiceV2")
public class EmailServiceV2 implements EmailService {
    public void send(String to, String msg) {
        System.out.println("V2发送：" + msg);
    }
}

// 使用@Qualifier指定
@Service
public class NotificationService {
    @Autowired
    @Qualifier("emailServiceV2")  // 明确指定使用V2
    private EmailService emailService;
}
```

### @Value - 注入配置值

用于注入配置文件中的值：

```java
@Service
public class AppConfigService {

    @Value("${app.name}")  // 注入配置文件中的值
    private String appName;

    @Value("${server.port}")
    private int serverPort;

    @Value("${app.enabled:true}")  // 默认值
    private boolean enabled;

    @Value("Hello World")  // 直接注入字面值
    private String message;
}
```

---

## ⚙️ 配置相关注解

### @Configuration - 配置类

定义Spring配置类：

```java
@Configuration  // 告诉Spring这是一个配置类
public class AppConfig {

    @Bean  // 定义一个Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/mydb");
        dataSource.setUsername("root");
        dataSource.setPassword("password");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        // 可以依赖其他Bean
        return new JdbcTemplate(dataSource);
    }
}
```

### @Bean - 定义Bean

在配置类中手动定义Bean：

```java
@Configuration
public class ServiceConfig {

    @Bean  // 定义一个Bean，Spring会自动管理
    public EmailService emailService() {
        return new EmailServiceImpl();  // 可以自定义创建逻辑
    }

    @Bean("customEmailService")  // 指定Bean名称
    @Scope("prototype")  // 指定作用域
    public EmailService customEmailService() {
        EmailServiceImpl service = new EmailServiceImpl();
        service.setDebug(true);  // 自定义初始化
        return service;
    }

    @Bean
    @Lazy  // 延迟初始化
    public ExpensiveService expensiveService() {
        return new ExpensiveServiceImpl();
    }
}
```

### @ComponentScan - 组件扫描

告诉Spring去哪里找Bean：

```java
@Configuration
@ComponentScan(basePackages = "com.example")  // 扫描指定包
public class AppConfig {
    // 配置内容
}

// 或者更精确的扫描
@Configuration
@ComponentScan(
    basePackages = {"com.example.service", "com.example.repository"},
    excludeFilters = @ComponentScan.Filter(Controller.class)  // 排除控制器
)
public class AppConfig {
}
```

---

## 🔧 高级注解

### 作用域注解

```java
// 单例（默认）
@Service
@Scope("singleton")
public class SingletonService {
}

// 原型（每次请求创建新实例）
@Component
@Scope("prototype")
public class PrototypeService {
}

// Web作用域
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST,
       proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestScopedBean {
}

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION,
       proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionScopedBean {
}
```

### 生命周期注解

```java
@Service
public class LifecycleService {

    public LifecycleService() {
        System.out.println("1. 构造函数执行");
    }

    @Autowired  // 依赖注入
    private DependencyService dependencyService;

    @PostConstruct  // 初始化后执行
    public void init() {
        System.out.println("2. @PostConstruct执行");
    }

    @PreDestroy  // 销毁前执行
    public void cleanup() {
        System.out.println("3. @PreDestroy执行");
    }
}
```

### 条件注解

```java
@Configuration
public class ConditionalConfig {

    @Bean
    @ConditionalOnClass(name = "com.mysql.cj.jdbc.Driver")  // 当MySQL驱动存在时
    public DataSource mysqlDataSource() {
        return new HikariDataSource();
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "cache.enabled", havingValue = "true")
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }

    @Bean
    @ConditionalOnMissingBean(DataSource.class)  // 当不存在DataSource Bean时
    public DataSource defaultDataSource() {
        return new EmbeddedDatabaseBuilder().build();
    }
}
```

---

## 📋 注解使用最佳实践

### ✅ 推荐做法

1. **分层使用注解**
```java
@Service     // 业务层
@Repository  // 数据层
@RestController  // API层
@Component   // 工具类
```

2. **构造函数注入优先**
```java
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

3. **合理使用@Value**
```java
@Service
public class EmailService {
    @Value("${email.from}")
    private String fromEmail;

    @Value("${email.enabled:false}")
    private boolean enabled;
}
```

### ❌ 避免做法

1. **过度使用字段注入**
```java
@Service
public class BadService {
    @Autowired
    private UserRepository userRepository;  // 不推荐
}
```

2. **注解使用不当**
```java
@Controller  // 应该用@Service
public class UserService {
}

@Repository  // 应该用@Service
public class EmailService {
}
```

---

## 🎯 注解速查表

| 分类 | 注解 | 作用 | 使用场景 |
|------|------|------|----------|
| **Bean定义** | `@Component` | 定义通用Bean | 工具类、通用组件 |
| | `@Service` | 定义服务Bean | 业务逻辑层 |
| | `@Repository` | 定义仓储Bean | 数据访问层 |
| | `@Controller` | 定义控制器Bean | Web MVC |
| | `@RestController` | 定义REST控制器 | REST API |
| **依赖注入** | `@Autowired` | 自动装配Bean | 注入依赖 |
| | `@Qualifier` | 指定Bean名称 | 多个同类型Bean |
| | `@Value` | 注入配置值 | 配置属性 |
| **配置相关** | `@Configuration` | 定义配置类 | Java配置 |
| | `@Bean` | 定义Bean方法 | 手动创建Bean |
| | `@ComponentScan` | 组件扫描 | 指定扫描包 |
| **作用域** | `@Scope` | 指定Bean作用域 | singleton、prototype等 |
| **生命周期** | `@PostConstruct` | 初始化后执行 | 初始化逻辑 |
| | `@PreDestroy` | 销毁前执行 | 清理逻辑 |
| **条件** | `@ConditionalOnClass` | 类存在时生效 | 条件化配置 |

---

## 🚀 下一步学习

掌握了注解体系后，继续深入学习：

- [[01-Spring注解/01-核心注解(@Required,@Autowired,@Component等).md|核心注解详解]]
- [[01-Spring注解/02-配置注解(@Configuration,@Bean,@Value等).md|配置注解详解]]
- [[02-依赖注入/02-注入方式(构造函数,字段,Setter注入).md|依赖注入方式详解]]

---

**记住：注解是Spring的语言，掌握了注解就掌握了与Spring对话的能力！** 🎉