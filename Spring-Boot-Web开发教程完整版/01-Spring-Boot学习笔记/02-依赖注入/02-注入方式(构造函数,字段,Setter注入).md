---
tags:
  - 依赖注入
  - 构造函数注入
  - 字段注入
  - Setter注入
  - @Autowired
  - 注入方式
created: 2025-11-16
modified: 2025-11-16
category: 依赖注入
difficulty: intermediate
---

# 注入方式：构造函数、字段、Setter注入

## @Autowired 注解详解

### 基本概念

`@Autowired` 是Spring框架中最重要的注解之一，用于实现**依赖注入（Dependency Injection）**。它告诉Spring容器自动寻找匹配的Bean并注入到标记的字段、构造函数或方法中。

### 使用方式

#### 1. 字段注入（Field Injection）

```java
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public void createUser(User user) {
        userRepository.save(user);
        emailService.sendWelcomeEmail(user.getEmail());
    }
}
```

**特点：**
- ✅ 代码简洁，声明直观
- ❌ 隐藏了类的依赖关系
- ❌ 无法保证不可变性（字段不能是final）
- ❌ 单元测试困难，需要使用Spring测试上下文

#### 2. 构造函数注入（Constructor Injection）- **推荐方式**

> [!TIP] 推荐方式
> 构造函数注入是现代Spring开发的首选方式，它提供了不可变性和明确的依赖关系。

```java
@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    @Autowired  // Spring 4.3+ 版本中，单个构造函数可以省略@Autowired
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
}
```

**特点：**
- ✅ 不可变性（字段可以是final）
- ✅ 明确显示类的依赖关系
- ✅ 强制依赖必须满足，对象创建后即完整
- ✅ 单元测试友好，可以直接调用构造函数
- ✅ 避免循环依赖问题

#### 3. Setter方法注入（Setter Injection）

```java
@Service
public class UserService {

    private UserRepository userRepository;
    private EmailService emailService;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}
```

**特点：**
- ✅ 可选依赖，可以在运行时改变
- ✅ 支持重新注入
- ❌ 依赖可能为null，需要null检查
- ❌ 破坏了不可变性

---

## 注入方式对比与选择

### 详细对比表

| 注入方式 | 优点 | 缺点 | 适用场景 |
|---------|------|------|----------|
| **构造函数注入** | ✅ 不可变性<br>✅ 强制依赖<br>✅ 测试友好<br>✅ 避免循环依赖 | 代码稍长 | **必需依赖**（推荐） |
| **Setter注入** | ✅ 可选依赖<br>✅ 支持重新注入<br>✅ 解决循环依赖 | ❌ 可变性<br>❌ 依赖可能为null | **可选依赖** |
| **字段注入** | ✅ 代码简洁<br>✅ 声明简单 | ❌ 测试困难<br>❌ 隐藏依赖<br>❌ 支持循环依赖 | 仅限简单场景 |

### 最佳实践示例

```java
@Service
@RequiredArgsConstructor  // Lombok自动生成构造函数
public class OrderService {

    // ✅ 必需依赖 - 构造函数注入
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    // ✅ 可选依赖 - Setter注入
    private NotificationService notificationService;

    @Autowired(required = false)
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // ❌ 避免字段注入（除了测试类）
    // @Autowired
    // private EmailService emailService;
}
```

### 为什么说构造函数注入能避免循环依赖？

**A:** 因为Spring在创建Bean时需要先满足构造函数参数，如果存在循环依赖，会在启动时立即失败：

```java
// ❌ 循环依赖 - 启动时失败
@Service
public class ServiceA {
    private final ServiceB serviceB;

    public ServiceA(ServiceB serviceB) {  // 需要ServiceB
        this.serviceB = serviceB;
    }
}

@Service
public class ServiceB {
    private final ServiceA serviceA;

    public ServiceB(ServiceA serviceA) {  // 需要ServiceA
        this.serviceA = serviceA;
    }
}

// ✅ 解决方案1：使用@Lazy延迟初始化
@Service
public class ServiceA {
    private final ServiceB serviceB;

    public ServiceA(@Lazy ServiceB serviceB) {  // 延迟初始化
        this.serviceB = serviceB;
    }
}

// ✅ 解决方案2：重构设计，提取共同依赖
@Service
public class SharedService {
    // 共同逻辑
}

@Service
public class ServiceA {
    private final SharedService sharedService;
    public ServiceA(SharedService sharedService) {
        this.sharedService = sharedService;
    }
}
```

---

## @Autowired 高级用法

### @Autowired 的属性

#### 1. required 属性

```java
@Service
public class UserService {

    @Autowired(required = true)  // 默认值，必需注入
    private UserRepository userRepository;

    @Autowired(required = false)  // 可选注入，找不到Bean也不会报错
    private OptionalService optionalService;
}
```

#### 2. 处理多个实现类的情况

```java
public interface PaymentService {
    void processPayment(double amount);
}

@Service("creditCardService")
public class CreditCardPaymentService implements PaymentService {
    public void processPayment(double amount) { /* 实现 */ }
}

@Service("paypalService")
public class PayPalPaymentService implements PaymentService {
    public void processPayment(double amount) { /* 实现 */ }
}

@Service
public class OrderService {

    @Autowired
    @Qualifier("creditCardService")  // 指定具体的Bean
    private PaymentService paymentService;

    // 或者注入所有实现
    @Autowired
    private List<PaymentService> paymentServices;
}
```

### 集合注入模式

```java
@Service
public class PluginManager {

    // 注入所有实现，按@Order排序
    @Autowired
    private List<Plugin> plugins;  // 自动按@Order排序

    // 注入所有实现，按名称映射
    @Autowired
    private Map<String, Plugin> pluginMap;  // {beanName=instance}

    // 注入特定类型的实现
    @Autowired
    private List<DataProcessor> dataProcessors;

    // 结合泛型注入
    @Autowired
    private List<Handler<Message>> messageHandlers;

    public void executePlugins() {
        plugins.forEach(Plugin::execute);
    }
}
```

### 条件化注入

```java
@Service
public class ConditionalService {

    // 只在特定条件下注入
    @Autowired(required = false)
    @Qualifier("specialService")
    private SpecialService specialService;

    // 使用Optional避免NPE
    @Autowired
    private Optional<CacheService> cacheService;

    // 使用JSR-330 @Inject替代@Autowired
    @Inject
    private LegacyService legacyService;

    // 结合条件注解
    @Autowired
    @ConditionalOnProperty(name = "feature.enabled", havingValue = "true")
    private FeatureService featureService;
}
```

### 动态注入和解析

```java
@Service
public class DynamicService {

    @Autowired
    private ApplicationContext applicationContext;

    // 动态获取Bean
    public <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    // 按名称获取Bean
    public Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    // 条件性获取Bean
    public <T> Optional<T> getOptionalBean(Class<T> clazz) {
        try {
            return Optional.of(applicationContext.getBean(clazz));
        } catch (NoSuchBeanDefinitionException e) {
            return Optional.empty();
        }
    }

    // 获取所有实现
    public <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz);
    }
}
```

---

## 结合Lombok简化代码

### 使用@RequiredArgsConstructor

```java
@Service
@RequiredArgsConstructor  // 自动生成构造函数
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    // 构造函数和其他方法自动生成
}
```

### 使用@AllArgsConstructor

```java
@Service
@AllArgsConstructor  // 为所有字段生成构造函数
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private String serviceName;  // 非final字段也会包含在构造函数中
}
```

### 使用@Data（谨慎使用）

```java
@Service
@Data  // 包含构造函数、setter、getter等
public class UserService {

    private UserRepository userRepository;  // 非final，字段注入
    private final EmailService emailService;
}
```

> [!WARNING] 注意
> `@Data` 会生成setter方法，破坏不可变性，推荐使用 `@RequiredArgsConstructor` + `@Getter`。

---

## 常见误区和最佳实践

### 误区1：过度依赖字段注入

```java
// ❌ 避免这样做
@Service
public class BadService {
    @Autowired
    private UserService userService;      // 隐藏依赖，测试困难

    @Autowired
    private EmailService emailService;    // 无法保证不可变性

    @Autowired
    private ConfigService configService;  // 循环依赖风险
}

// ✅ 推荐做法
@Service
@RequiredArgsConstructor
public class GoodService {
    private final UserService userService;      // 明确依赖
    private final EmailService emailService;    // 不可变性
    private final ConfigService configService;  // 强制初始化
}
```

### 误区2：循环依赖设计问题

```java
// ❌ 设计有问题的循环依赖
@Service
public class OrderService {
    @Autowired
    private UserService userService;  // 订单服务依赖用户服务

    public void createOrder() {
        userService.updateUser();     // 在订单服务中调用用户服务
    }
}

@Service
public class UserService {
    @Autowired
    private OrderService orderService;  // 用户服务又依赖订单服务

    public void updateUser() {
        orderService.createOrder();     // 在用户服务中调用订单服务
    }
}

// ✅ 重构设计，提取共同逻辑
@Service
public class OrderService {
    @Autowired
    private BusinessService businessService;  // 依赖共同的业务服务

    public void createOrder() {
        businessService.processOrder();
    }
}

@Service
public class UserService {
    @Autowired
    private BusinessService businessService;  // 依赖共同的业务服务

    public void updateUser() {
        businessService.processUser();
    }
}

@Service
public class BusinessService {
    // 统一处理业务逻辑，避免循环依赖
    public void processOrder() { }
    public void processUser() { }
}
```

---

## 📚 相关概念交叉引用

- [[01-核心概念(IoC容器,Bean生命周期等)]] - 了解IoC容器和Bean生命周期
- [[03-高级特性(循环依赖,作用域,条件注入)]] - 深入学习循环依赖处理
- [[04-问题排查(NoSuchBeanDefinitionException等)]] - 常见注入问题解决

## 🎯 核心要点总结

1. **注入方式选择**：构造函数注入 > Setter注入 > 字段注入
2. **构造函数注入**：推荐用于必需依赖，提供不可变性和明确依赖
3. **Setter注入**：适用于可选依赖，支持运行时重新注入
4. **字段注入**：仅限简单场景，避免在生产代码中使用
5. **Lombok集成**：使用`@RequiredArgsConstructor`简化构造函数注入
6. **循环依赖**：构造函数注入能及早发现循环依赖问题

选择合适的注入方式是构建可维护、可测试Spring应用的关键。推荐优先使用构造函数注入，配合Lombok简化代码编写。