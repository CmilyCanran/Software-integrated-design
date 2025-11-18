---
tags:
  - 自动配置
  - 11@Conditional
  - Spring Boot
  - 自动配置原理
  - 条件注解
created: 2025-11-16
modified: 2025-11-16
category: 配置管理
difficulty: intermediate
---

# Spring Boot 自动配置详解

## 目录
- [@EnableAutoConfiguration 注解](#enableautoconfiguration-注解)
- [自动配置原理](#自动配置原理)
- [条件注解详解](#条件注解详解)
- [自定义自动配置](#自定义自动配置)
- [自动配置控制](#自动配置控制)
- [调试自动配置](#调试自动配置)
- [自动配置最佳实践](#自动配置最佳实践)

---

## @EnableAutoConfiguration 注解

### 核心思想

> [!TIP] 核心思想
> `@EnableAutoConfiguration` **的作用是告诉 Spring Boot："请根据我项目里的依赖（JAR包），自动帮我猜测和配置我可能需要的东西。"**

这背后的"约定优于配置"理念是：

> [!IMPORTANT] 约定优于配置
> **"只要你做了常规的选择（比如引入了** `spring-boot-starter-data-jpa`**），我（Spring Boot）就默认你知道自己想要什么样的配置，并为你准备好。除非你明确告诉我你想要别的。"**

### @SpringBootApplication 复合注解

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration // @Configuration的变体，标记这是一个配置类
@EnableAutoConfiguration // 👈 这就是它！
@ComponentScan(excludeFilters = { ... }) // 自动扫描@Component, @Service等
public @interface SpringBootApplication {
    // ...
}
```

### 基本用法

```java
// 通常不需要单独使用，包含在 @SpringBootApplication 中
@SpringBootApplication
// 等价于：
// @SpringBootConfiguration
// @EnableAutoConfiguration
// @ComponentScan
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}

// 如果需要单独使用
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class MyApplication {
    // ...
}
```

---

## 自动配置原理

### 工作原理

`@EnableAutoConfiguration` 的实现主要依赖于以下三大支柱：

#### 1. 类路径扫描

Spring Boot 启动时，会检查你的 `pom.xml`（或 `build.gradle`）中的所有依赖。

- 它发现 `spring-boot-starter-web`，就知道："嗯，这个应用是个Web应用，需要一个Web服务器（如Tomcat）。"
- 它发现 `h2database` 的JAR包，就知道："哦，你很可能想用H2数据库。"
- 它发现 `spring-boot-starter-data-jpa`，就知道："好，你需要JPA（Hibernate）来做数据持久化。"

#### 2. 条件注解

常见的条件注解有：

- `@ConditionalOnClass`：当类路径上存在指定的类时，才生效。
- `@ConditionalOnMissingBean`：当容器中**没有**你自定义的某个 Bean 时，才生效。
- `@ConditionalOnProperty`：当配置文件中存在某个属性，且值符合预期时，才生效。
- `@ConditionalOnWebApplication`：当前应用是一个Web应用时，才生效。

#### 3. Spring Factories 机制

Spring Boot 会读取所有依赖 JAR 包下的 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 文件。

### 自动配置加载流程

```java
// 简化的自动配置加载流程
public class AutoConfigurationImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        // 1. 获取所有自动配置类
        List<String> configurations = getCandidateConfigurations();

        // 2. 根据条件注解过滤
        configurations = filter(configurations, autoConfigurationMetadata);

        // 3. 触发自动配置事件
        fireAutoConfigurationImportEvents(configurations, exclusions);

        return configurations.toArray(new String[0]);
    }

    protected List<String> getCandidateConfigurations() {
        // 从 spring.factories 文件中加载所有自动配置类
        return SpringFactoriesLoader.loadFactoryNames(
            EnableAutoConfiguration.class, this.beanClassLoader);
    }
}
```

### 自动配置示例

#### DataSource 自动配置

```java
// 简化的 DataSource 自动配置类
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
@ConditionalOnMissingBean(type = "javax.sql.DataSource")
@EnableConfigurationProperties(DataSourceProperties.class)
@Import({ DataSourcePoolMetadataProvidersConfiguration.class,
          DataSourceInitializationConfiguration.class })
public class DataSourceAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @Conditional(EmbeddedDatabaseCondition.class)
    @ConditionalOnMissingBean({ DataSource.class, XADataSource.class })
    @Import(EmbeddedDataSourceConfiguration.class)
    protected static class EmbeddedDatabaseConfiguration {
    }

    @Configuration(proxyBeanMethods = false)
    @Conditional(PooledDataSourceCondition.class)
    @ConditionalOnMissingBean({ DataSource.class, XADataSource.class })
    @Import({ DataSourceConfiguration.Hikari.class,
              DataSourceConfiguration.Tomcat.class,
              DataSourceConfiguration.Dbcp2.class,
              DataSourceConfiguration.OracleUcp.class,
              DataSourceConfiguration.Generic.class })
    protected static class PooledDataSourceConfiguration {
    }
}
```

---

## 条件注解详解

### 核心条件注解

#### 1. @ConditionalOnClass

```java
@Configuration
@ConditionalOnClass(name = "com.mysql.cj.jdbc.Driver")
public class MySqlAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DataSource mysqlDataSource() {
        return DataSourceBuilder.create()
            .driverClassName("com.mysql.cj.jdbc.Driver")
            .build();
    }
}
```

#### 2. @ConditionalOnMissingBean

```java
@Configuration
public class DefaultServiceConfiguration {

    @Bean
    @ConditionalOnMissingBean(EmailService.class) // 容器中没有EmailService时才创建
    public EmailService defaultEmailService() {
        return new SimpleEmailService();
    }

    @Bean
    @ConditionalOnMissingBean(name = "paymentService")
    public PaymentService defaultPaymentService() {
        return new DefaultPaymentService();
    }
}
```

#### 3. @ConditionalOnProperty

```java
@Configuration
public class CacheAutoConfiguration {

    @Bean
    @ConditionalOnProperty(
        name = "app.cache.enabled", // 属性名
        havingValue = "true",       // 期望值
        matchIfMissing = false      // 属性不存在时是否匹配
    )
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }

    @Bean
    @ConditionalOnProperty("redis.enabled") // 只要属性存在即可
    public RedisTemplate<String, Object> redisTemplate() {
        return new RedisTemplate<>();
    }
}
```

#### 4. @ConditionalOnWebApplication

```java
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebMvcAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public InternalResourceViewResolver defaultViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }
}

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class WebFluxAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WebFluxConfigurer webFluxConfigurer() {
        return new WebFluxConfigurer() {
            // 配置 WebFlux
        };
    }
}
```

### 高级条件注解

#### 1. @ConditionalOnBean

```java
@Configuration
public class JpaAutoConfiguration {

    @Bean
    @ConditionalOnBean(DataSource.class) // 容器中有DataSource时才创建
    public EntityManagerFactory entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("com.example.myapp.entity");
        return factory.getObject();
    }
}
```

#### 2. @ConditionalOnResource

```java
@Configuration
public class CustomConfiguration {

    @Bean
    @ConditionalOnResource(resources = "classpath:custom-config.xml")
    public CustomService customService() {
        return new CustomService();
    }

    @Bean
    @ConditionalOnResource(resources = "file:/opt/app/config.properties")
    public FileBasedConfig fileBasedConfig() {
        return new FileBasedConfig();
    }
}
```

#### 3. @ConditionalOnExpression

```java
@Configuration
public class ConditionalExpressionConfiguration {

    @Bean
    @ConditionalOnExpression("${app.feature.enabled:false} and ${app.environment} != 'test'")
    public FeatureService featureService() {
        return new FeatureService();
    }

    @Bean
    @ConditionalOnExpression("'${app.profile}'.matches('dev|test')")
    public DevelopmentService developmentService() {
        return new DevelopmentService();
    }
}
```

#### 4. 自定义条件注解

```java
// 自定义条件
public class OnSystemPropertyCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalOnSystemProperty.class.getName());
        String propertyName = (String) attributes.get("name");
        String expectedValue = (String) attributes.get("value");

        String actualValue = System.getProperty(propertyName);
        return expectedValue.equals(actualValue);
    }
}

// 自定义注解
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(OnSystemPropertyCondition.class)
public @interface ConditionalOnSystemProperty {
    String name();
    String value();
}

// 使用自定义注解
@Configuration
public class CustomConditionConfiguration {

    @Bean
    @ConditionalOnSystemProperty(name = "os.name", value = "Windows 10")
    public WindowsService windowsService() {
        return new WindowsService();
    }
}
```

---

## 自定义自动配置

### 创建自动配置类

```java
@Configuration
@ConditionalOnClass(GreetingService.class)
@EnableConfigurationProperties(GreetingProperties.class)
public class GreetingServiceAutoConfiguration {

    private final GreetingProperties properties;

    public GreetingServiceAutoConfiguration(GreetingProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public GreetingService greetingService() {
        return new DefaultGreetingService(properties);
    }

    @Bean
    @ConditionalOnProperty(name = "greeting.advanced.enabled", havingValue = "true")
    public AdvancedGreetingService advancedGreetingService() {
        return new AdvancedGreetingService(properties);
    }
}
```

### 配置属性类

```java
@ConfigurationProperties(prefix = "greeting")
@Data
public class GreetingProperties {

    private String message = "Hello, World!";
    private String language = "en";
    private boolean enabled = true;
    private final Advanced advanced = new Advanced();

    @Data
    public static class Advanced {
        private boolean enabled = false;
        private String format = "formal";
        private List<String> recipients = new ArrayList<>();
    }
}
```

### 服务实现类

```java
public interface GreetingService {
    String greet(String name);
}

public class DefaultGreetingService implements GreetingService {

    private final GreetingProperties properties;

    public DefaultGreetingService(GreetingProperties properties) {
        this.properties = properties;
    }

    @Override
    public String greet(String name) {
        if (!properties.isEnabled()) {
            return "Greeting service is disabled";
        }

        return String.format("%s, %s!", properties.getMessage(), name);
    }
}

public class AdvancedGreetingService implements GreetingService {

    private final GreetingProperties properties;

    public AdvancedGreetingService(GreetingProperties properties) {
        this.properties = properties;
    }

    @Override
    public String greet(String name) {
        String baseMessage = properties.getMessage();
        String format = properties.getAdvanced().getFormat();

        switch (format.toLowerCase()) {
            case "formal":
                return String.format("Dear %s, %s", name, baseMessage);
            case "casual":
                return String.format("Hey %s! %s", name, baseMessage);
            default:
                return String.format("%s, %s!", baseMessage, name);
        }
    }
}
```

### 注册自动配置

在 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 文件中注册：

```
com.example.myapp.autoconfigure.GreetingServiceAutoConfiguration
```

### 创建 Starter

```xml
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-configuration-processor</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

---

## 自动配置控制

### 排除特定自动配置

#### 1. 使用注解排除

```java
@SpringBootApplication(exclude = {
    RedisAutoConfiguration.class,
    SecurityAutoConfiguration.class,
    DataSourceAutoConfiguration.class
})
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

#### 2. 使用配置文件排除

```yaml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
      - org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

#### 3. 条件化排除

```java
@Configuration
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "false", matchIfMissing = true)
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class NoSecurityConfiguration {
    // 在安全禁用时排除安全自动配置
}
```

### 控制自动配置顺序

```java
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class HighPriorityAutoConfiguration {
    // 高优先级自动配置
}

@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@Configuration
public class BeforeDataSourceAutoConfiguration {
    // 在 DataSource 自动配置之前执行
}

@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@Configuration
public class AfterDataSourceAutoConfiguration {
    // 在 DataSource 自动配置之后执行
}
```

---

## 调试自动配置

### 启用调试模式

在 `application.properties` 中开启调试模式：

```properties
debug=true
```

### 查看自动配置报告

启动应用时，你会看到一份非常详细的报告，分为 **Positive matches**（已生效的自动配置）和 **Negative matches**（未生效的自动配置）。

```bash
============================
CONDITIONS EVALUATION REPORT
============================


Positive matches:
-----------------
   AopAutoConfiguration matched:
      - @ConditionalOnProperty (spring.aop.auto=true) matched (OnPropertyCondition)

   AopAutoConfiguration.ClassProxyingConfiguration matched:
      - @ConditionalOnMissingClass did not find unwanted class 'org.aspectj.weaver.Advice' (OnClassCondition)
      - @ConditionalOnProperty (spring.aop.proxy-target-class=true) matched (OnPropertyCondition)

   DataSourceAutoConfiguration matched:
      - @ConditionalOnClass found required classes 'javax.sql.DataSource', 'org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType' (OnClassCondition)

Negative matches:
-----------------
   ActiveMQAutoConfiguration:
      Did not match:
         - @ConditionalOnClass did not find required class 'javax.jms.ConnectionFactory' (OnClassCondition)

   AopAutoConfiguration.AspectJAutoProxyingConfiguration:
      Did not match:
         - @ConditionalOnClass did not find required class 'org.aspectj.weaver.Advice' (OnClassCondition)

   CassandraAutoConfiguration:
      Did not match:
         - @ConditionalOnClass did not find required class 'com.datastax.oss.driver.api.core.CqlSession' (OnClassCondition)
```

### 编程方式查看自动配置

```java
@Component
public class AutoConfigurationDebugger {

    @Autowired
    private ApplicationContext applicationContext;

    @EventListener(ApplicationReadyEvent.class)
    public void debugAutoConfigurations() {
        // 获取所有自动配置类
        String[] autoConfigBeans = applicationContext.getBeanNamesForType(AutoConfiguration.class);

        System.out.println("=== Auto Configuration Beans ===");
        for (String beanName : autoConfigBeans) {
            Object bean = applicationContext.getBean(beanName);
            System.out.println(beanName + " -> " + bean.getClass().getName());
        }

        // 检查特定配置是否存在
        boolean hasDataSource = applicationContext.containsBean("dataSource");
        boolean hasRedisTemplate = applicationContext.containsBean("redisTemplate");

        System.out.println("DataSource exists: " + hasDataSource);
        System.out.println("RedisTemplate exists: " + hasRedisTemplate);
    }
}
```

### 条件评估报告

```java
@Component
public class ConditionEvaluationReporter {

    @Autowired
    private ConditionEvaluationReport conditionEvaluationReport;

    @EventListener(ApplicationReadyEvent.class)
    public void printConditionEvaluationReport() {
        Map<String, ConditionAndOutcomes> noMatches = conditionEvaluationReport.getConditionAndOutcomesBySource();

        System.out.println("=== Unmatched Conditions ===");
        noMatches.forEach((source, outcomes) -> {
            if (outcomes.isFullMatch()) {
                return; // 只看未匹配的
            }

            System.out.println("\n" + source + ":");
            outcomes.forEach(outcome -> {
                if (!outcome.isMatch()) {
                    System.out.println("  - " + outcome.getMessage());
                }
            });
        });
    }
}
```

---

## 自动配置最佳实践

### 1. 合理使用条件注解

```java
@Configuration
@ConditionalOnClass(MyService.class)
@ConditionalOnProperty(name = "my.service.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(MyServiceProperties.class)
public class MyServiceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MyService myService(MyServiceProperties properties) {
        return new DefaultMyService(properties);
    }

    @Bean
    @ConditionalOnBean(MyService.class)
    @ConditionalOnProperty(name = "my.service.advanced.enabled")
    public MyServiceAdvisor myServiceAdvisor(MyService myService) {
        return new MyServiceAdvisor(myService);
    }
}
```

### 2. 提供合理的默认值

```java
@ConfigurationProperties(prefix = "my.service")
@Data
public class MyServiceProperties {

    /**
     * 是否启用 MyService
     */
    private boolean enabled = true;

    /**
     * 服务名称
     */
    private String name = "DefaultMyService";

    /**
     * 连接超时时间（毫秒）
     */
    private Duration timeout = Duration.ofSeconds(30);

    /**
     * 最大连接数
     */
    private int maxConnections = 10;

    /**
     * 重试次数
     */
    private int retryCount = 3;
}
```

### 3. 支持多种配置方式

```java
@Configuration
public class FlexibleConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "my.service.type", havingValue = "simple")
    public MyService simpleMyService() {
        return new SimpleMyService();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "my.service.type", havingValue = "advanced")
    public MyService advancedMyService() {
        return new AdvancedMyService();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "my.service.type", matchIfMissing = true)
    public MyService defaultMyService() {
        return new DefaultMyService();
    }
}
```

### 4. 提供配置元数据

在 `META-INF/spring-configuration-metadata.json` 中提供配置提示：

```json
{
  "properties": [
    {
      "name": "my.service.enabled",
      "type": "java.lang.Boolean",
      "description": "是否启用 MyService。",
      "defaultValue": true
    },
    {
      "name": "my.service.timeout",
      "type": "java.time.Duration",
      "description": "服务超时时间。",
      "defaultValue": "30s"
    },
    {
      "name": "my.service.type",
      "type": "java.lang.String",
      "description": "服务类型。",
      "defaultValue": "default",
      "allowedValues": ["simple", "advanced", "default"]
    }
  ]
}
```

---

## 总结

### 自动配置核心概念

| 概念 | 作用 | 示例 |
|------|------|------|
| **@EnableAutoConfiguration** | 启用自动配置机制 | `@SpringBootApplication` |
| **条件注解** | 控制自动配置的生效条件 | `@ConditionalOnClass` |
| **Spring Factories** | 自动配置类发现机制 | `META-INF/spring.factories` |
| **配置属性绑定** | 外部化配置支持 | `@ConfigurationProperties` |
| **@ConditionalOnMissingBean** | 允许用户覆盖默认配置 | 自定义 Bean 替换默认实现 |

### 最佳实践总结

1. **理解自动配置原理**，能够有效控制和调试配置问题
2. **合理使用条件注解**，实现灵活的配置管理
3. **提供合理的默认值**，降低使用门槛
4. **支持用户自定义**，通过 `@ConditionalOnMissingBean` 允许覆盖
5. **提供配置文档**，通过元数据文件支持 IDE 提示
6. **保持向后兼容**，谨慎修改现有自动配置

记住：**自动配置的目标是让开发者"零配置"就能开始工作，同时在需要时提供足够的灵活性来定制行为。**

---

## 相关学习笔记

- [[01-基础配置(@Configuration,@Bean等)]] - 基础配置注解详解
- [[02-属性配置(Properties,YAML,Environment等)]] - 属性和环境配置详解
- [[04-外部化配置(配置文件,环境变量等)]] - 外部化配置源管理

## 相关技术文档

- Spring Boot自动配置原理 - 深入理解自动配置机制
- 条件注解详解 - @Conditional系列注解完整指南
- 自定义Starter开发 - 创建可重用的自动配置模块