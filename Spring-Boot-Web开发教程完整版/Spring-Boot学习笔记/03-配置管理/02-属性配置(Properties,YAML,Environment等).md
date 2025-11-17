---
tags:
  - 属性配置
  - Properties
  - YAML
  - Environment
  - 配置绑定
  - "@ConfigurationProperties"
  - Spring Boot
created: 2025-11-16
modified: 2025-11-16
category: 配置管理
difficulty: intermediate
---

# Spring Boot 属性配置详解

## 目录
- [配置文件格式](#配置文件格式)
- [Properties 配置](#properties-配置)
- [YAML 配置](#yaml-配置)
- [Environment 对象](#environment-对象)
- [配置属性绑定](#配置属性绑定)
- [多环境配置](#多环境配置)
- [配置最佳实践](#配置最佳实践)

---

## 配置文件格式

Spring Boot 支持多种配置文件格式，主要包括：

- **`application.properties`** - 传统的键值对格式
- **`application.yml`** - YAML 格式，层级更清晰
- **`application.yaml`** - YAML 格式的另一种扩展名

### 配置文件加载优先级

Spring Boot 按以下优先级加载配置文件：

1. 命令行参数
2. Java 系统属性
3. 操作系统环境变量
4. 外部配置文件
   - `./config/application.properties`
   - `./application.properties`
   - `classpath:/config/application.properties`
   - `classpath:/application.properties`
5. `@PropertySource` 注解指定的配置文件
6. 默认属性

---

## Properties 配置

### 基本语法

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=secret
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA配置
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# 服务器配置
server.port=8080
server.servlet.context-path=/api

# 自定义配置
app.name=我的Spring Boot应用
app.version=1.0.0
app.cache.enabled=true
app.cache.timeout=300
```

### 复杂对象配置

```properties
# 多级属性配置
app.payment.providers.alipay.enabled=true
app.payment.providers.alipay.app-id=12345
app.payment.providers.alipay.private-key=your-private-key

app.payment.providers.wechat.enabled=true
app.payment.providers.wechat.app-id=67890
app.payment.providers.wechat.mch-id=your-mch-id

# 数组配置
app.servers[0].name=server1
app.servers[0].host=192.168.1.10
app.servers[0].port=8080

app.servers[1].name=server2
app.servers[1].host=192.168.1.11
app.servers[1].port=8081
```

---

## YAML 配置

### 基本语法

```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: secret
    driver-class-name: com.mysql.cj.jdbc.Driver

# JPA配置
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect

# 服务器配置
server:
  port: 8080
  servlet:
    context-path: /api

# 自定义配置
app:
  name: 我的Spring Boot应用
  version: 1.0.0
  cache:
    enabled: true
    timeout: 300
```

### YAML 的高级特性

#### 1. 多文档配置

```yaml
# 开发环境配置
---
spring:
  profiles: dev
  datasource:
    url: jdbc:h2:mem:devdb
    username: sa
    password:
server:
  port: 8080

# 测试环境配置
---
spring:
  profiles: test
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password:
server:
  port: 8081

# 生产环境配置
---
spring:
  profiles: prod
  datasource:
    url: jdbc:mysql://prod-server:3306/proddb
    username: prod_user
    password: ${DB_PASSWORD}
server:
  port: 80
```

#### 2. 复杂对象和数组

```yaml
app:
  payment:
    providers:
      alipay:
        enabled: true
        app-id: "12345"
        private-key: "your-private-key"
        timeout: 30000
      wechat:
        enabled: true
        app-id: "67890"
        mch-id: "your-mch-id"
        timeout: 15000
    default-provider: alipay

  servers:
    - name: server1
      host: 192.168.1.10
      port: 8080
      weight: 1
    - name: server2
      host: 192.168.1.11
      port: 8080
      weight: 2
    - name: server3
      host: 192.168.1.12
      port: 8080
      weight: 1
```

#### 3. 环境变量替换

```yaml
app:
  database:
    url: ${DATABASE_URL:jdbc:mysql://localhost:3306/defaultdb}
    username: ${DB_USER:root}
    password: ${DB_PASSWORD:secret}

  security:
    jwt-secret: ${JWT_SECRET:my-secret-key}
    jwt-expiration: ${JWT_EXPIRATION:86400}

  external-api:
    key: ${API_KEY:default-api-key}
    url: ${API_URL:https://api.example.com}
```

---

## Environment 对象

### 访问配置属性

```java
@Component
public class ConfigurationReader {

    @Autowired
    private Environment environment;

    public void printConfigurations() {
        // 读取简单属性
        String serverPort = environment.getProperty("server.port");
        String appName = environment.getProperty("app.name");

        // 带默认值的读取
        String timeout = environment.getProperty("app.cache.timeout", "5000");

        // 读取特定类型的属性
        Boolean cacheEnabled = environment.getProperty("app.cache.enabled", Boolean.class, false);
        Integer maxConnections = environment.getProperty("db.max-connections", Integer.class, 10);

        // 检查属性是否存在
        boolean hasApiKey = environment.containsProperty("app.api.key");

        // 获取当前激活的profile
        String[] activeProfiles = environment.getActiveProfiles();

        System.out.println("Server Port: " + serverPort);
        System.out.println("App Name: " + appName);
        System.out.println("Cache Timeout: " + timeout);
        System.out.println("Cache Enabled: " + cacheEnabled);
        System.out.println("Active Profiles: " + Arrays.toString(activeProfiles));
    }

    public Map<String, Object> getAllAppProperties() {
        Map<String, Object> appProps = new HashMap<>();

        // 获取所有以app.开头的属性
        for (String propertyName : environment.getPropertyNames()) {
            if (propertyName.startsWith("app.")) {
                appProps.put(propertyName, environment.getProperty(propertyName));
            }
        }

        return appProps;
    }
}
```

### 动态配置读取

```java
@Component
public class DynamicConfigService {

    @Autowired
    private Environment environment;

    // 根据条件动态读取配置
    public String getDatabaseUrl() {
        String profile = Arrays.toString(environment.getActiveProfiles());

        if (profile.contains("prod")) {
            return environment.getProperty("spring.datasource.prod.url");
        } else if (profile.contains("test")) {
            return environment.getProperty("spring.datasource.test.url");
        } else {
            return environment.getProperty("spring.datasource.url",
                "jdbc:h2:mem:defaultdb");
        }
    }

    // 读取配置数组
    public List<String> getAllowedOrigins() {
        String origins = environment.getProperty("app.cors.allowed-origins",
            "http://localhost:3000");
        return Arrays.asList(origins.split(","));
    }
}
```

---

## 配置属性绑定

### @ConfigurationProperties 基础用法

```java
@Component
@ConfigurationProperties(prefix = "app.payment")
@Data // Lombok
public class PaymentProperties {

    private String defaultProvider;
    private int timeout = 30000;
    private int retryCount = 3;
    private Map<String, String> providers = new HashMap<>();
    private List<PaymentMethod> methods = new ArrayList<>();

    @Data
    public static class PaymentMethod {
        private String name;
        private boolean enabled;
        private String config;
    }
}
```

### 对应的配置文件

```yaml
app:
  payment:
    default-provider: alipay
    timeout: 30000
    retry-count: 3
    providers:
      alipay: "Alipay Payment Service"
      wechat: "WeChat Payment Service"
    methods:
      - name: credit-card
        enabled: true
        config: "credit-card-config"
      - name: debit-card
        enabled: true
        config: "debit-card-config"
      - name: paypal
        enabled: false
        config: "paypal-config"
```

### 启用配置属性

```java
@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
public class PaymentConfig {

    private final PaymentProperties paymentProperties;

    public PaymentConfig(PaymentProperties paymentProperties) {
        this.paymentProperties = paymentProperties;
    }

    @Bean
    public PaymentService paymentService() {
        PaymentService service = new PaymentService();
        service.setDefaultProvider(paymentProperties.getDefaultProvider());
        service.setTimeout(paymentProperties.getTimeout());
        service.setRetryCount(paymentProperties.getRetryCount());
        return service;
    }

    @PostConstruct
    public void printConfiguration() {
        System.out.println("Payment Configuration:");
        System.out.println("Default Provider: " + paymentProperties.getDefaultProvider());
        System.out.println("Timeout: " + paymentProperties.getTimeout());
        System.out.println("Providers: " + paymentProperties.getProviders());
        System.out.println("Methods: " + paymentProperties.getMethods());
    }
}
```

### 高级配置属性功能

#### 1. 数据转换和验证

```java
@Component
@ConfigurationProperties(prefix = "app.database")
@Data
@Validated
public class DatabaseProperties {

    @NotNull
    private String url;

    @NotNull
    private String username;

    private String password;

    @Min(1)
    @Max(100)
    private int maxConnections = 10;

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration connectionTimeout = Duration.ofSeconds(30);

    @DataSizeUnit(DataUnit.MEGABYTES)
    private DataSize maxPoolSize = DataSize.ofMegabytes(100);

    // 自定义转换器
    @ConvertWith(DatabaseTypeConverter.class)
    private DatabaseType type;
}

public enum DatabaseType {
    MYSQL, POSTGRESQL, ORACLE, H2
}

@Component
public static class DatabaseTypeConverter implements Converter<String, DatabaseType> {
    @Override
    public DatabaseType convert(String source) {
        return DatabaseType.valueOf(source.toUpperCase());
    }
}
```

#### 2. 嵌套配置属性

```java
@Component
@ConfigurationProperties(prefix = "app")
@Data
public class ApplicationProperties {

    private String name;
    private String version;

    private final Cache cache = new Cache();
    private final Security security = new Security();
    private final ExternalServices externalServices = new ExternalServices();

    @Data
    public static class Cache {
        private boolean enabled = true;
        private Duration ttl = Duration.ofMinutes(30);
        private int maxSize = 1000;
        private CacheType type = CacheType.MEMORY;
    }

    @Data
    public static class Security {
        private boolean enabled = true;
        private String jwtSecret;
        private Duration jwtExpiration = Duration.ofHours(24);
        private List<String> allowedOrigins = new ArrayList<>();
    }

    @Data
    public static class ExternalServices {
        private final Payment payment = new Payment();
        private final Email email = new Email();
        private final Sms sms = new Sms();

        @Data
        public static class Payment {
            private String url;
            private String apiKey;
            private Duration timeout = Duration.ofSeconds(30);
        }

        @Data
        public static class Email {
            private String provider;
            private String username;
            private String password;
            private String host;
            private int port = 587;
        }

        @Data
        public static class Sms {
            private String provider;
            private String apiKey;
            private String secret;
            private String fromNumber;
        }
    }
}
```

---

## 多环境配置

### Profile-specific 配置文件

```
src/main/resources/
├── application.yml              # 默认配置
├── application-dev.yml          # 开发环境配置
├── application-test.yml         # 测试环境配置
├── application-prod.yml         # 生产环境配置
└── application-local.yml        # 本地开发配置
```

### 激活 Profile 的方式

#### 1. 配置文件中指定

```yaml
# application.yml
spring:
  profiles:
    active: dev
```

#### 2. 命令行参数

```bash
java -jar myapp.jar --spring.profiles.active=prod
```

#### 3. 环境变量

```bash
export SPRING_PROFILES_ACTIVE=prod
java -jar myapp.jar
```

#### 4. JVM 系统属性

```bash
java -Dspring.profiles.active=prod -jar myapp.jar
```

### Profile 配置示例

```yaml
# application-dev.yml
spring:
  config:
    activate:
      on-profile: dev

  datasource:
    url: jdbc:h2:mem:devdb
    username: sa
    password:
    driver-class-name: org.h2.Driver

  jpa:
    show-sql: true
    hibernate:
      ddl-auto: create-drop

  h2:
    console:
      enabled: true
      path: /h2-console

logging:
  level:
    root: DEBUG
    com.example.myapp: DEBUG

app:
  name: "MyApp - Development"
  debug: true
  cache:
    enabled: false
```

```yaml
# application-prod.yml
spring:
  config:
    activate:
      on-profile: prod

  datasource:
    url: ${DATABASE_URL}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000

  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect

logging:
  level:
    root: WARN
    com.example.myapp: INFO
  file:
    name: /var/log/myapp/application.log

app:
  name: "MyApp - Production"
  debug: false
  cache:
    enabled: true
    ttl: 3600
```

### 条件化配置

```java
@Configuration
@Profile("dev")
public class DevConfiguration {

    @Bean
    public DataSource devDataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .setName("devdb")
            .build();
    }

    @Bean
    public CommandLineRunner devDataLoader() {
        return args -> {
            System.out.println("Loading development data...");
            // 加载开发环境数据
        };
    }
}

@Configuration
@Profile("prod")
public class ProdConfiguration {

    @Bean
    @Primary
    public DataSource prodDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("${DATABASE_URL}");
        config.setUsername("${DB_USER}");
        config.setPassword("${DB_PASSWORD}");
        return new HikariDataSource(config);
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
}

@Configuration
@Profile({"dev", "test"})
public class DevTestConfiguration {

    @Bean
    public MockEmailService mockEmailService() {
        return new MockEmailService();
    }
}
```

---

## 配置最佳实践

### 1. 配置文件组织

```yaml
# application.yml - 通用配置
spring:
  application:
    name: myapp
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}

server:
  port: ${SERVER_PORT:8080}

app:
  name: MyApplication
  version: @project.version@
```

### 2. 敏感信息管理

```yaml
# 不要在配置文件中硬编码敏感信息
app:
  security:
    jwt-secret: ${JWT_SECRET:default-secret-for-dev-only}
    db-password: ${DB_PASSWORD}
    api-keys:
      payment-service: ${PAYMENT_API_KEY}
      email-service: ${EMAIL_API_KEY}
```

### 3. 配置验证

```java
@Component
@ConfigurationProperties(prefix = "app")
@Data
@Validated
public class AppProperties {

    @NotBlank
    private String name;

    @Pattern(regexp = "^\\d+\\.\\d+\\.\\d+$")
    private String version;

    @Valid
    private final Database database = new Database();

    @Data
    public static class Database {
        @NotBlank
        private String url;

        @Min(1)
        @Max(100)
        private int maxConnections = 10;

        @NotNull
        private Duration timeout = Duration.ofSeconds(30);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void validateConfiguration() {
        // 自定义验证逻辑
        if (database.getMaxConnections() > 50 && "prod".equals(getActiveProfile())) {
            throw new IllegalStateException("生产环境数据库连接数不能超过50");
        }
    }
}
```

### 4. 配置文档化

```yaml
# application.yml
app:
  # 应用名称，用于日志和监控标识
  name: MyApplication

  # 应用版本，与构建版本保持一致
  version: 1.0.0

  # 缓存配置
  cache:
    # 是否启用缓存
    enabled: true
    # 缓存过期时间（秒）
    ttl: 300
    # 最大缓存条目数
    max-size: 1000

  # 外部服务配置
  external-services:
    # 支付服务配置
    payment:
      # 支付服务API地址
      url: https://api.payment.com
      # API密钥，从环境变量读取
      api-key: ${PAYMENT_API_KEY}
      # 请求超时时间（毫秒）
      timeout: 30000
```

---

## 总结

### 配置方式对比

| 配置方式 | 优点 | 缺点 | 适用场景 |
|----------|------|------|----------|
| **Properties** | 简单直观，兼容性好 | 层级不清晰，复杂配置难读 | 简单配置，传统项目 |
| **YAML** | 层级清晰，支持复杂结构 | 语法严格，缩进敏感 | 复杂配置，现代项目 |
| **Environment** | 动态读取，类型安全 | 代码侵入性强 | 运行时动态配置 |
| **@ConfigurationProperties** | 类型安全，支持验证 | 需要额外配置类 | 复杂对象绑定 |

### 最佳实践总结

1. **优先使用 YAML 格式**，特别是复杂配置
2. **合理使用 Profile**，分离不同环境配置
3. **敏感信息使用环境变量**，不要硬编码
4. **使用 @ConfigurationProperties** 进行类型安全的配置绑定
5. **添加配置验证**，确保配置的正确性
6. **保持配置文档化**，便于团队维护

---

## 相关学习笔记

- [[01-基础配置(@Configuration,@Bean等)]] - 基础配置注解详解
- [[03-自动配置(@EnableAutoConfiguration,@Conditional等)]] - Spring Boot自动配置机制
- [[04-外部化配置(配置文件,环境变量等)]] - 外部化配置源管理

## 相关技术文档

- Spring Boot配置属性绑定 - @ConfigurationProperties完整指南
- 多环境配置管理 - Profile配置最佳实践
- 配置元数据 - 自定义配置提示和文档生成