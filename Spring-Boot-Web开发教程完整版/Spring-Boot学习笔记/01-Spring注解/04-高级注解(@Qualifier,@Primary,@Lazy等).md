---
tags:
  - Spring注解
  - 高级注解
  - 依赖注入
  - 条件注入
  - 高级用法
created: 2025-11-16
modified: 2025-11-16
category: 注解
difficulty: advanced
---

# Spring 高级注解详解

## @Qualifier

### 基本概念

`@Qualifier` 注解用于解决当有多个相同类型的Bean时，明确指定要注入哪一个Bean的问题。它是依赖注入中的"精准定位器"。

### 核心作用

> [!IMPORTANT] 解决歧义
> 当Spring容器中存在多个相同类型的Bean时，`@Qualifier` 帮助你精确选择要注入的目标Bean。

### 基本使用

```java
// 定义多个实现类
@Service("fastPayment")
public class FastPaymentService implements PaymentService {
    public void processPayment(double amount) {
        System.out.println("快速支付：" + amount);
    }
}

@Service("securePayment")
public class SecurePaymentService implements PaymentService {
    public void processPayment(double amount) {
        System.out.println("安全支付：" + amount);
    }
}

// 使用 @Qualifier 指定具体的Bean
@Service
public class OrderService {

    @Autowired
    @Qualifier("fastPayment")  // 明确指定使用 fastPayment
    private PaymentService paymentService;

    public void processOrder(double amount) {
        paymentService.processPayment(amount);
    }
}
```

### @Qualifier 的高级用法

#### 1. 在构造函数中使用

```java
@Service
public class OrderService {

    private final PaymentService paymentService;
    private final NotificationService notificationService;

    public OrderService(
            @Qualifier("fastPayment") PaymentService paymentService,
            @Qualifier("emailNotification") NotificationService notificationService) {
        this.paymentService = paymentService;
        this.notificationService = notificationService;
    }
}
```

#### 2. 在Set方法中使用

```java
@Service
public class OrderService {

    private PaymentService paymentService;

    @Autowired
    public void setPaymentService(@Qualifier("securePayment") PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

#### 3. 结合 @Bean 使用

```java
@Configuration
public class PaymentConfig {

    @Bean
    @Qualifier("fast")
    public PaymentService fastPaymentService() {
        return new FastPaymentService();
    }

    @Bean
    @Qualifier("secure")
    public PaymentService securePaymentService() {
        return new SecurePaymentService();
    }
}
```

#### 4. 自定义 @Qualifier 注解

```java
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier  // 继承 @Qualifier 的功能
public @interface FastPayment {
}

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface SecurePayment {
}

// 使用自定义注解
@Service
public class FastPaymentService implements PaymentService {
    // 实现
}

@Service
public class OrderService {

    @Autowired
    @FastPayment  // 使用自定义的限定符
    private PaymentService paymentService;
}
```

---

## @Primary

### 基本概念

`@Primary` 注解用于标记某个Bean为"主要候选者"，当存在多个相同类型的Bean且没有明确的`@Qualifier`指定时，Spring会优先选择被标记为`@Primary`的Bean。

### 核心作用

> [!IMPORTANT] 默认选择
> `@Primary` 设定了一个默认的Bean，避免在没有明确指定时注入失败。

### 基本使用

```java
@Service
@Primary  // 标记为主要候选者
public class DefaultPaymentService implements PaymentService {
    public void processPayment(double amount) {
        System.out.println("默认支付方式：" + amount);
    }
}

@Service
public class FastPaymentService implements PaymentService {
    public void processPayment(double amount) {
        System.out.println("快速支付：" + amount);
    }
}

// 不需要 @Qualifier，会自动选择 @Primary 标记的Bean
@Service
public class OrderService {

    @Autowired  // 会自动注入 DefaultPaymentService
    private PaymentService paymentService;

    public void processOrder(double amount) {
        paymentService.processPayment(amount);
    }
}
```

### @Primary 与 @Qualifier 的组合

```java
@Service
public class OrderService {

    // 使用 @Qualifier 覆盖 @Primary 的默认选择
    @Autowired
    @Qualifier("fastPayment")
    private PaymentService fastPaymentService;

    // 不使用 @Qualifier，使用 @Primary 的默认选择
    @Autowired
    private PaymentService defaultPaymentService;
}
```

### 在配置类中使用 @Primary

```java
@Configuration
public class PaymentConfig {

    @Bean
    @Primary
    public PaymentService defaultPaymentService() {
        return new DefaultPaymentService();
    }

    @Bean
    public PaymentService fastPaymentService() {
        return new FastPaymentService();
    }

    @Bean
    public PaymentService securePaymentService() {
        return new SecurePaymentService();
    }
}
```

---

## @Lazy

### 基本概念

`@Lazy` 注解用于延迟Bean的初始化，即只有在第一次使用时才创建Bean实例，而不是在Spring容器启动时就创建。

### 核心作用

> [!IMPORTANT] 延迟加载
> `@Lazy` 可以优化应用启动时间，解决循环依赖问题，节省内存资源。

### 基本使用

```java
@Service
@Lazy  // 延迟初始化
public class HeavyService {

    @PostConstruct
    public void init() {
        System.out.println("HeavyService 被初始化了！");
        // 耗时的初始化操作
        try {
            Thread.sleep(3000);  // 模拟耗时操作
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void doWork() {
        System.out.println("执行工作...");
    }
}

// 使用延迟加载的Service
@Service
public class LightService {

    @Autowired
    @Lazy  // 也可以在注入点指定延迟加载
    private HeavyService heavyService;

    public void useHeavyService() {
        // 只有在这里调用时，HeavyService 才会被初始化
        heavyService.doWork();
    }
}
```

### 解决循环依赖

```java
@Service
public class ServiceA {

    @Autowired
    @Lazy  // 延迟加载 ServiceB，解决循环依赖
    private ServiceB serviceB;

    public void methodA() {
        System.out.println("ServiceA.methodA");
        serviceB.methodB();
    }
}

@Service
public class ServiceB {

    @Autowired
    @Lazy  // 延迟加载 ServiceA，解决循环依赖
    private ServiceA serviceA;

    public void methodB() {
        System.out.println("ServiceB.methodB");
    }
}
```

### 在配置类中使用 @Lazy

```java
@Configuration
public class LazyConfig {

    @Bean
    @Lazy
    public ExpensiveBean expensiveBean() {
        System.out.println("创建昂贵的Bean...");
        return new ExpensiveBean();
    }

    @Bean
    public SimpleBean simpleBean() {
        return new SimpleBean();
    }
}
```

---

## @Scope

### 基本概念

`@Scope` 注解用于定义Bean的作用域，控制Bean的生命周期和实例化方式。

### Spring Bean 作用域类型

| 作用域 | 描述 | 使用场景 |
|--------|------|----------|
| **singleton** | 单例模式，整个容器只存在一个实例 | 默认作用域，无状态的服务 |
| **prototype** | 原型模式，每次请求都创建新实例 | 有状态的对象 |
| **request** | Web应用中，每个HTTP请求一个实例 | Web层的数据对象 |
| **session** | Web应用中，每个HTTP会话一个实例 | 用户会话数据 |
| **application** | Web应用中，ServletContext生命周期内一个实例 | 应用级别的共享数据 |

### 基本使用

```java
// 单例模式（默认）
@Service
@Scope("singleton")
public class SingletonService {
    private int counter = 0;

    public void increment() {
        counter++;
        System.out.println("Counter: " + counter);
    }
}

// 原型模式
@Component
@Scope("prototype")
public class PrototypeBean {
    private final String id = UUID.randomUUID().toString();

    public String getId() {
        return id;
    }
}

// Web应用中的作用域
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestScopedBean {
    // 每个HTTP请求一个实例
}

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionScopedBean {
    // 每个HTTP会话一个实例
}
```

### 代理模式

```java
@Service
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PrototypeService {

    public void doSomething() {
        System.out.println("PrototypeService 实例: " + this.hashCode());
    }
}

@Service
public class SingletonService {

    @Autowired
    private PrototypeService prototypeService;  // 注入代理对象

    public void usePrototypeService() {
        // 每次调用都会创建新的 PrototypeService 实例
        prototypeService.doSomething();
    }
}
```

---

## @Profile

### 基本概念

`@Profile` 注解用于根据不同的环境（开发、测试、生产等）来注册不同的Bean，实现环境特定的配置。

### 基本使用

```java
@Configuration
public class DataSourceConfig {

    @Bean
    @Profile("dev")  // 开发环境
    public DataSource devDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }

    @Bean
    @Profile("test")  // 测试环境
    public DataSource testDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .build();
    }

    @Bean
    @Profile("prod")  // 生产环境
    public DataSource prodDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://prod-db:3306/myapp");
        config.setUsername("prod_user");
        config.setPassword("prod_password");
        return new HikariDataSource(config);
    }
}
```

### 激活Profile

```properties
# application.properties
spring.profiles.active=dev
```

或者通过命令行：
```bash
java -jar myapp.jar --spring.profiles.active=prod
```

### 多Profile组合

```java
@Service
@Profile({"dev", "test"})  // 同时匹配dev和test环境
public class DevTestService {
    // 开发和测试环境共用的服务
}

@Configuration
@Profile("!prod")  // 除了生产环境之外的所有环境
public class DevOnlyConfig {
    // 非生产环境的配置
}
```

---

## @Conditional

### 基本概念

`@Conditional` 注解提供了更强大的条件化Bean注册机制，可以根据任意条件来决定是否创建Bean。

### 基本使用

```java
@Configuration
public class ConditionalConfig {

    @Bean
    @Conditional(WindowsCondition.class)  // 自定义条件
    public WindowsService windowsService() {
        return new WindowsService();
    }

    @Bean
    @Conditional(LinuxCondition.class)
    public LinuxService linuxService() {
        return new LinuxService();
    }
}

// 自定义条件实现
public class WindowsCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().getProperty("os.name").contains("Windows");
    }
}
```

### Spring Boot 提供的条件注解

```java
@Configuration
public class BootConditionalConfig {

    @Bean
    @ConditionalOnProperty(name = "cache.enabled", havingValue = "true")
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }

    @Bean
    @ConditionalOnClass(name = "redis.clients.jedis.Jedis")
    public RedisTemplate<String, Object> redisTemplate() {
        return new RedisTemplate<>();
    }

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource defaultDataSource() {
        return new HikariDataSource();
    }

    @Bean
    @ConditionalOnBean(DataSource.class)
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            // Web配置
        };
    }

    @Bean
    @ConditionalOnExpression("${app.feature.enabled:false}")
    public FeatureService featureService() {
        return new FeatureService();
    }
}
```

---

## @DependsOn

### 基本概念

`@DependsOn` 注解用于显式指定Bean的依赖关系，确保某些Bean在其他Bean之前初始化。

### 基本使用

```java
@Configuration
public class DependencyConfig {

    @Bean
    @DependsOn("databaseInitializer")  // 依赖 databaseInitializer
    public UserService userService() {
        return new UserService();
    }

    @Bean
    public DatabaseInitializer databaseInitializer() {
        return new DatabaseInitializer();
    }
}
```

### 复杂依赖关系

```java
@Service
@DependsOn({"cacheManager", "connectionPool"})  // 依赖多个Bean
public class ApplicationService {
    // 确保缓存管理器和连接池先初始化
}

@Component
@DependsOn("applicationService")
public class StartupRunner {
    // 在 applicationService 之后初始化
}
```

---

## 高级注解组合使用

### 实际项目示例

```java
@Configuration
public class AdvancedConfig {

    @Bean
    @Primary
    @Lazy
    @Scope("prototype")
    @ConditionalOnProperty(name = "payment.provider", havingValue = "default")
    @DependsOn("paymentGateway")
    public PaymentService defaultPaymentService(PaymentGateway gateway) {
        return new DefaultPaymentService(gateway);
    }

    @Bean
    @Qualifier("fast")
    @Profile({"dev", "test"})
    @ConditionalOnClass(FastPaymentProvider.class)
    public PaymentService fastPaymentService() {
        return new FastPaymentService();
    }
}
```

### 复杂的依赖注入场景

```java
@Service
public class ComplexService {

    private final PaymentService primaryPaymentService;
    private final List<PaymentService> allPaymentServices;
    private final Map<String, PaymentService> paymentServiceMap;

    public ComplexService(
            @Autowired PaymentService primaryPaymentService,  // 使用 @Primary 的Bean
            @Autowired List<PaymentService> allPaymentServices,  // 注入所有实现
            @Autowired Map<String, PaymentService> paymentServiceMap) {  // 注入所有Bean的映射
        this.primaryPaymentService = primaryPaymentService;
        this.allPaymentServices = allPaymentServices;
        this.paymentServiceMap = paymentServiceMap;
    }

    public void processPayment(double amount, String provider) {
        if (provider != null && paymentServiceMap.containsKey(provider)) {
            paymentServiceMap.get(provider).processPayment(amount);
        } else {
            primaryPaymentService.processPayment(amount);
        }
    }

    public void processWithAllProviders(double amount) {
        allPaymentServices.forEach(service -> service.processPayment(amount));
    }
}
```

---

## 常见问题解答

### Q1: @Primary 和 @Qualifier 有什么区别？什么时候用哪个？

**A:** 两者解决不同的问题：

| 特性 | @Primary | @Qualifier |
|------|----------|------------|
| **作用范围** | 全局默认选择 | 局部精确选择 |
| **使用场景** | 设定默认实现 | 选择特定实现 |
| **灵活性** | 较低 | 较高 |
| **代码量** | 较少 | 较多 |

**使用建议：**

```java
// 1. 当有一个明确的"默认"选择时，用 @Primary
@Service
@Primary
public class DefaultEmailService implements EmailService { }

@Service
public class PriorityEmailService implements EmailService { }

// 大多数情况下使用默认
@Autowired
private EmailService emailService;  // 自动选择 DefaultEmailService

// 2. 当需要明确指定特定实现时，用 @Qualifier
@Autowired
@Qualifier("priorityEmailService")
private EmailService priorityEmailService;
```

---

### Q2: @Lazy 会影响性能吗？什么时候应该使用？

**A:** `@Lazy` 的影响是双面的：

#### 优点：
- **启动时间优化**：延迟加载非关键组件
- **内存节省**：不用的Bean不会被创建
- **解决循环依赖**：避免启动时的依赖循环

#### 缺点：
- **首次使用延迟**：第一次调用时需要初始化时间
- **调试复杂性**：问题可能只在运行时暴露
- **配置复杂性**：需要考虑初始化顺序

**推荐使用场景：**

```java
// ✅ 适合使用 @Lazy 的场景
@Service
@Lazy
public class ReportGenerator {
    // 耗时的初始化操作，且不是每次启动都需要
    @PostConstruct
    public void init() {
        // 加载大量模板、配置等
    }
}

@Service
@Lazy
public class AdminPanelService {
    // 管理功能，普通用户不会使用
}

// ❌ 不适合使用 @Lazy 的场景
@Service  // 不要用 @Lazy
public class AuthenticationService {
    // 核心安全功能，需要立即可用
}
```

---

### Q3: 如何在运行时动态选择Bean实现？

**A:** 有几种方式可以实现动态选择：

#### 方式一：使用 ApplicationContext

```java
@Service
public class DynamicServiceSelector {

    @Autowired
    private ApplicationContext applicationContext;

    public PaymentService getPaymentService(String provider) {
        return (PaymentService) applicationContext.getBean(provider + "PaymentService");
    }
}
```

#### 方式二：使用 Map 注入

```java
@Service
public class PaymentServiceRouter {

    @Autowired
    private Map<String, PaymentService> paymentServices;

    public PaymentService getPaymentService(String provider) {
        return paymentServices.get(provider + "PaymentService");
    }
}
```

#### 方式三：使用策略模式

```java
@Service
public class PaymentStrategyService {

    private final Map<String, PaymentService> strategies;

    public PaymentStrategyService(List<PaymentService> services) {
        this.strategies = services.stream()
                .collect(Collectors.toMap(
                        service -> service.getClass().getAnnotation(Service.class).value(),
                        Function.identity()
                ));
    }

    public PaymentService getStrategy(String provider) {
        PaymentService service = strategies.get(provider);
        if (service == null) {
            throw new IllegalArgumentException("Unknown payment provider: " + provider);
        }
        return service;
    }
}
```

---

### Q4: @Scope 的 prototype 模式有什么陷阱？

**A:** prototype 作用域有几个重要陷阱需要注意：

#### 陷阱一：依赖注入时的单例问题

```java
@Service  // 单例
public class SingletonService {

    @Autowired  // 这里注入的是代理对象
    private PrototypeBean prototypeBean;

    public void doSomething() {
        // 每次调用都会创建新实例
        prototypeBean.doWork();
    }
}
```

#### 陷阱二：生命周期管理

```java
@Component
@Scope("prototype")
public class PrototypeBean implements DisposableBean {

    @PostConstruct
    public void init() {
        System.out.println("PrototypeBean 初始化");
    }

    @PreDestroy
    public void cleanup() {
        // ❌ 这个方法不会被自动调用！
        System.out.println("PrototypeBean 销毁");
    }

    @Override
    public void destroy() throws Exception {
        // ❌ 这个方法也不会被自动调用！
        System.out.println("DisposableBean.destroy()");
    }
}
```

#### 解决方案：

```java
@Service
public class PrototypeBeanManager {

    @Autowired
    private ApplicationContext applicationContext;

    private final Set<PrototypeBean> activeBeans = ConcurrentHashMap.newKeySet();

    public PrototypeBean createPrototypeBean() {
        PrototypeBean bean = applicationContext.getBean(PrototypeBean.class);
        activeBeans.add(bean);
        return bean;
    }

    public void destroyPrototypeBean(PrototypeBean bean) {
        // 手动清理
        if (bean instanceof DisposableBean) {
            try {
                ((DisposableBean) bean).destroy();
            } catch (Exception e) {
                // 处理异常
            }
        }
        activeBeans.remove(bean);
    }
}
```

---

## 相关文档链接

- [[01-核心注解(@Required,@Autowired,@Component等)]] - 核心注解详解
- [[02-配置注解(@Configuration,@Bean,@Value等)]] - 配置相关注解详解
- [[03-分层注解(@Service,@Repository,@Controller等)]] - 分层架构注解详解
- [[Spring注解详解]] - Spring注解总览

## 相关学习笔记

- [[Spring条件化配置]] - 条件化配置详解
- [[Bean生命周期详解]] - Bean创建和销毁过程
- [[依赖注入高级用法]] - 高级依赖注入技巧