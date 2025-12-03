# 🛒 购物车CRUD操作实现计划

## 📋 项目概述

基于Spring Boot服装购物系统，为Cart实体创建完整的CRUD操作逻辑，包括Service层、Controller层、DTO设计、转换器和异常处理。

## 🎯 实现目标

### 功能需求
- ✅ 获取购物车详情（包含商品完整信息）
- ✅ 添加商品到购物车（支持数量累加）
- ✅ 更新购物车商品数量
- ✅ 从购物车删除商品
- ✅ 清空整个购物车
- ✅ 获取购物车统计信息（总价、总数量等）

### 技术要求
- 遵循现有BaseService/BaseController架构模式
- 使用统一ApiResponse响应格式
- 完整的业务验证和异常处理
- 并发安全和数据一致性
- 性能优化（避免N+1查询）

## 🏗️ 架构设计

### 文件结构
```
src/main/java/com/cmliy/springweb/
├── repository/
│   └── CartRepository.java          # 数据访问层
├── service/
│   └── CartService.java             # 业务逻辑层
├── controller/
│   └── CartController.java          # RESTful API
├── dto/
│   ├── CartResponseDTO.java         # 购物车响应
│   ├── CartItemResponseDTO.java     # 购物车商品项
│   ├── CartAddRequestDTO.java       # 添加商品请求
│   ├── CartUpdateRequestDTO.java    # 更新数量请求
│   └── CartStatisticsDTO.java       # 统计信息
├── converter/
│   └── CartConverter.java           # 实体转换器
└── exception/
    ├── CartException.java           # 购物车异常基类
    ├── ProductNotFoundException.java # 商品不存在
    ├── InsufficientStockException.java # 库存不足
    └── CartNotFoundException.java   # 购物车不存在
```

## 📝 详细实现步骤

### 步骤1: 创建Repository接口
**文件**: `src/main/java/com/cmliy/springweb/repository/CartRepository.java`

```java
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // 基础查询
    Optional<Cart> findByUserId(Long userId);
    boolean existsByUserId(Long userId);

    // 带关联查询
    @Query("SELECT c FROM Cart c JOIN FETCH c.user WHERE c.user.id = :userId")
    Optional<Cart> findByUserIdWithUser(@Param("userId") Long userId);
}
```

### 步骤2: 创建DTO类

#### CartResponseDTO - 购物车响应
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {
    private Long userId;
    private String username;
    private List<CartItemResponseDTO> items;
    private Integer totalItems;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### CartItemResponseDTO - 购物车商品项
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDTO {
    private Long productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private String mainImage;
    private boolean available;
    private String stockStatus;
}
```

#### CartAddRequestDTO - 添加商品请求
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartAddRequestDTO {
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    @Max(value = 999, message = "数量不能超过999")
    private Integer quantity;
}
```

#### CartUpdateRequestDTO - 更新数量请求
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartUpdateRequestDTO {
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    @Max(value = 999, message = "数量不能超过999")
    private Integer quantity;
}
```

#### CartStatisticsDTO - 统计信息
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartStatisticsDTO {
    private Long userId;
    private Integer totalItems;
    private Integer totalProducts;
    private BigDecimal totalPrice;
    private BigDecimal totalSavings;
    private LocalDateTime lastUpdated;
}
```

### 步骤3: 创建异常类

#### CartException.java - 购物车异常基类
```java
public class CartException extends RuntimeException {
    public CartException(String message) {
        super(message);
    }

    public CartException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

#### 具体异常类
```java
public class ProductNotFoundException extends CartException {
    public ProductNotFoundException(Long productId) {
        super("商品不存在: " + productId);
    }
}

public class InsufficientStockException extends CartException {
    public InsufficientStockException(Long productId, Integer requested, Integer available) {
        super(String.format("商品库存不足，商品ID: %d，需要: %d，可用: %d", productId, requested, available));
    }
}

public class CartNotFoundException extends CartException {
    public CartNotFoundException(Long userId) {
        super("购物车不存在，用户ID: " + userId);
    }
}
```

### 步骤4: 实现CartConverter转换器

**文件**: `src/main/java/com/cmliy/springweb/converter/CartConverter.java`

```java
@Component
@Slf4j
@RequiredArgsConstructor
public class CartConverter extends BaseConverter<Cart, CartResponseDTO> {

    private final ProductRepository productRepository;
    private final ProductConverter productConverter;

    @Override
    public CartResponseDTO toDTO(Cart cart) {
        return safeConvert(cart, c -> {
            List<CartItemResponseDTO> items = convertCartItems(c.getCartData());

            return CartResponseDTO.builder()
                .userId(c.getUser().getId())
                .username(c.getUser().getUsername())
                .items(items)
                .totalItems(c.getTotalItems())
                .totalPrice(calculateTotalPrice(items))
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
        });
    }

    private List<CartItemResponseDTO> convertCartItems(Map<Long, Integer> cartData) {
        if (cartData == null || cartData.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量获取商品信息，避免N+1查询
        List<Long> productIds = new ArrayList<>(cartData.keySet());
        List<Product> products = productRepository.findAllById(productIds);

        return products.stream()
            .map(product -> {
                Integer quantity = cartData.get(product.getId());
                return CartItemResponseDTO.builder()
                    .productId(product.getId())
                    .productName(product.getProductName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .discountedPrice(product.getDiscountedPrice())
                    .quantity(quantity)
                    .subtotal(product.getDiscountedPrice().multiply(BigDecimal.valueOf(quantity)))
                    .mainImage(product.getMainImage())
                    .available(product.isPurchasable())
                    .stockStatus(product.getStockStatus())
                    .build();
            })
            .collect(Collectors.toList());
    }

    private BigDecimal calculateTotalPrice(List<CartItemResponseDTO> items) {
        return items.stream()
            .map(CartItemResponseDTO::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

### 步骤5: 实现CartService业务逻辑

**文件**: `src/main/java/com/cmliy/springweb/service/CartService.java`

```java
@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class CartService extends BaseService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartConverter cartConverter;

    // 用户级锁，确保并发安全
    private final Map<Long, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    /**
     * 获取用户购物车
     */
    public CartResponseDTO getCartByUserId(Long userId) {
        return executeWithLog("获取购物车", () -> {
            User user = validateExists(userRepository.findById(userId), "用户", userId);
            Cart cart = getOrCreateCart(user);
            return cartConverter.toDTO(cart);
        }, userId);
    }

    /**
     * 添加商品到购物车
     */
    @Transactional
    public CartResponseDTO addToCart(Long userId, CartAddRequestDTO request) {
        return executeWithLog("添加商品到购物车", () -> {
            validateAddRequest(request);

            Product product = validateExists(productRepository.findById(request.getProductId()),
                                          "商品", request.getProductId());
            validateProductAvailable(product);
            validateStockSufficient(product, request.getQuantity());

            ReentrantLock userLock = getUserLock(userId);
            userLock.lock();
            try {
                User user = validateExists(userRepository.findById(userId), "用户", userId);
                Cart cart = getOrCreateCart(user);

                cart.addItem(request.getProductId(), request.getQuantity());
                cart = cartRepository.save(cart);

                return cartConverter.toDTO(cart);
            } finally {
                userLock.unlock();
            }
        }, userId, request.getProductId(), request.getQuantity());
    }

    /**
     * 更新购物车商品数量
     */
    @Transactional
    public CartResponseDTO updateCartItem(Long userId, CartUpdateRequestDTO request) {
        return executeWithLog("更新购物车商品数量", () -> {
            validateUpdateRequest(request);

            Product product = validateExists(productRepository.findById(request.getProductId()),
                                          "商品", request.getProductId());
            validateProductAvailable(product);
            validateStockSufficient(product, request.getQuantity());

            ReentrantLock userLock = getUserLock(userId);
            userLock.lock();
            try {
                User user = validateExists(userRepository.findById(userId), "用户", userId);
                Cart cart = getOrCreateCart(user);

                cart.updateItem(request.getProductId(), request.getQuantity());
                cart = cartRepository.save(cart);

                return cartConverter.toDTO(cart);
            } finally {
                userLock.unlock();
            }
        }, userId, request.getProductId(), request.getQuantity());
    }

    /**
     * 从购物车删除商品
     */
    @Transactional
    public CartResponseDTO removeFromCart(Long userId, Long productId) {
        return executeWithLog("从购物车删除商品", () -> {
            ReentrantLock userLock = getUserLock(userId);
            userLock.lock();
            try {
                User user = validateExists(userRepository.findById(userId), "用户", userId);
                Cart cart = getOrCreateCart(user);

                cart.removeItem(productId);
                cart = cartRepository.save(cart);

                return cartConverter.toDTO(cart);
            } finally {
                userLock.unlock();
            }
        }, userId, productId);
    }

    /**
     * 清空购物车
     */
    @Transactional
    public CartResponseDTO clearCart(Long userId) {
        return executeWithLog("清空购物车", () -> {
            ReentrantLock userLock = getUserLock(userId);
            userLock.lock();
            try {
                User user = validateExists(userRepository.findById(userId), "用户", userId);
                Cart cart = getOrCreateCart(user);

                cart.clearCart();
                cart = cartRepository.save(cart);

                return cartConverter.toDTO(cart);
            } finally {
                userLock.unlock();
            }
        }, userId);
    }

    /**
     * 获取购物车统计信息
     */
    public CartStatisticsDTO getCartStatistics(Long userId) {
        return executeWithLog("获取购物车统计", () -> {
            User user = validateExists(userRepository.findById(userId), "用户", userId);
            Cart cart = getOrCreateCart(user);
            return cartConverter.toStatisticsDTO(cart);
        }, userId);
    }

    // 私有辅助方法
    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
            .orElseGet(() -> createNewCart(user));
    }

    private Cart createNewCart(User user) {
        Cart cart = Cart.builder()
            .user(user)
            .build();
        return cartRepository.save(cart);
    }

    private void validateAddRequest(CartAddRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
    }

    private void validateUpdateRequest(CartUpdateRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
    }

    private void validateProductAvailable(Product product) {
        if (!product.isPurchasable()) {
            throw new CartException("商品不可购买：" + product.getStockStatus());
        }
    }

    private void validateStockSufficient(Product product, Integer requestedQuantity) {
        if (product.getStockQuantity() < requestedQuantity) {
            throw new InsufficientStockException(product.getId(), requestedQuantity, product.getStockQuantity());
        }
    }

    private ReentrantLock getUserLock(Long userId) {
        return userLocks.computeIfAbsent(userId, k -> new ReentrantLock());
    }
}
```

### 步骤6: 实现CartController控制器

**文件**: `src/main/java/com/cmliy/springweb/controller/CartController.java`

```java
@RestController
@RequestMapping("/api/cart")
@Validated
@Slf4j
@RequiredArgsConstructor
public class CartController extends BaseController {

    private final CartService cartService;

    /**
     * 获取当前用户购物车
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartResponseDTO>> getCart() {
        try {
            Long userId = getCurrentUserId();
            CartResponseDTO cart = cartService.getCartByUserId(userId);
            return success(cart, "获取购物车成功");
        } catch (Exception e) {
            log.error("获取购物车失败: {}", e.getMessage(), e);
            return error(400, "获取购物车失败: " + e.getMessage());
        }
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping("/items")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartResponseDTO>> addToCart(
            @Valid @RequestBody CartAddRequestDTO request) {

        log.info("添加商品到购物车: userId={}, productId={}, quantity={}",
                getCurrentUserId(), request.getProductId(), request.getQuantity());

        try {
            Long userId = getCurrentUserId();
            CartResponseDTO cart = cartService.addToCart(userId, request);
            return success(cart, "商品添加成功");
        } catch (CartException e) {
            log.warn("添加商品失败: {}", e.getMessage());
            return error(400, e.getMessage());
        } catch (Exception e) {
            log.error("添加商品到购物车失败: {}", e.getMessage(), e);
            return error(500, "系统错误，请稍后重试");
        }
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/items")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartResponseDTO>> updateCartItem(
            @Valid @RequestBody CartUpdateRequestDTO request) {

        log.info("更新购物车商品数量: userId={}, productId={}, quantity={}",
                getCurrentUserId(), request.getProductId(), request.getQuantity());

        try {
            Long userId = getCurrentUserId();
            CartResponseDTO cart = cartService.updateCartItem(userId, request);
            return success(cart, "商品数量更新成功");
        } catch (CartException e) {
            log.warn("更新商品数量失败: {}", e.getMessage());
            return error(400, e.getMessage());
        } catch (Exception e) {
            log.error("更新购物车商品数量失败: {}", e.getMessage(), e);
            return error(500, "系统错误，请稍后重试");
        }
    }

    /**
     * 从购物车删除商品
     */
    @DeleteMapping("/items/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartResponseDTO>> removeFromCart(
            @PathVariable Long productId) {

        log.info("从购物车删除商品: userId={}, productId={}", getCurrentUserId(), productId);

        try {
            Long userId = getCurrentUserId();
            CartResponseDTO cart = cartService.removeFromCart(userId, productId);
            return success(cart, "商品删除成功");
        } catch (CartException e) {
            log.warn("删除商品失败: {}", e.getMessage());
            return error(400, e.getMessage());
        } catch (Exception e) {
            log.error("从购物车删除商品失败: {}", e.getMessage(), e);
            return error(500, "系统错误，请稍后重试");
        }
    }

    /**
     * 清空购物车
     */
    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartResponseDTO>> clearCart() {

        log.info("清空购物车: userId={}", getCurrentUserId());

        try {
            Long userId = getCurrentUserId();
            CartResponseDTO cart = cartService.clearCart(userId);
            return success(cart, "购物车清空成功");
        } catch (CartException e) {
            log.warn("清空购物车失败: {}", e.getMessage());
            return error(400, e.getMessage());
        } catch (Exception e) {
            log.error("清空购物车失败: {}", e.getMessage(), e);
            return error(500, "系统错误，请稍后重试");
        }
    }

    /**
     * 获取购物车统计信息
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartStatisticsDTO>> getCartStatistics() {

        try {
            Long userId = getCurrentUserId();
            CartStatisticsDTO statistics = cartService.getCartStatistics(userId);
            return success(statistics, "获取购物车统计成功");
        } catch (CartException e) {
            log.warn("获取购物车统计失败: {}", e.getMessage());
            return error(400, e.getMessage());
        } catch (Exception e) {
            log.error("获取购物车统计失败: {}", e.getMessage(), e);
            return error(500, "系统错误，请稍后重试");
        }
    }
}
```

### 步骤7: 添加数据库初始化脚本

**文件**: `src/main/resources/data.sql` (添加到现有文件末尾)

```sql
-- 为测试用户创建空购物车
INSERT INTO carts (user_id, cart_data, created_at, updated_at) VALUES
(1, '{}', NOW(), NOW()),
(2, '{}', NOW(), NOW()),
(3, '{}', NOW(), NOW());
```

### 步骤8: 测试API端点

#### 测试数据准备
```bash
# 1. 登录获取token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password"}'

# 2. 获取购物车
curl -X GET http://localhost:8080/api/cart \
  -H "Authorization: Bearer YOUR_TOKEN"

# 3. 添加商品到购物车
curl -X POST http://localhost:8080/api/cart/items \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2}'

# 4. 更新商品数量
curl -X PUT http://localhost:8080/api/cart/items \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":5}'

# 5. 删除商品
curl -X DELETE http://localhost:8080/api/cart/items/1 \
  -H "Authorization: Bearer YOUR_TOKEN"

# 6. 清空购物车
curl -X DELETE http://localhost:8080/api/cart \
  -H "Authorization: Bearer YOUR_TOKEN"

# 7. 获取统计信息
curl -X GET http://localhost:8080/api/cart/statistics \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 🔍 关键特性说明

### 并发安全
- 使用`ReentrantLock`实现用户级锁，确保同一用户的购物车操作原子性
- 避免并发修改导致的数据不一致问题

### 业务验证
1. **商品存在性验证** - 检查商品是否存在于数据库
2. **商品可购买性验证** - 检查商品是否上架且有库存
3. **库存充足性验证** - 确保添加的数量不超过可用库存
4. **参数有效性验证** - 使用Bean Validation注解进行参数校验

### 性能优化
- 批量查询商品信息，避免N+1查询问题
- 使用`findAllById`一次性获取所有相关商品
- 合理的数据库索引设计

### 架构一致性
- 继承BaseService和BaseController，使用统一的操作日志模板
- 遵循现有的异常处理模式
- 使用统一的ApiResponse响应格式
- 集成Spring Security权限控制

## 📊 API端点一览

| HTTP方法 | 路径 | 描述 | 请求体 | 响应 |
|----------|------|------|--------|------|
| GET | `/api/cart` | 获取购物车 | 无 | CartResponseDTO |
| POST | `/api/cart/items` | 添加商品 | CartAddRequestDTO | CartResponseDTO |
| PUT | `/api/cart/items` | 更新数量 | CartUpdateRequestDTO | CartResponseDTO |
| DELETE | `/api/cart/items/{productId}` | 删除商品 | 无 | CartResponseDTO |
| DELETE | `/api/cart` | 清空购物车 | 无 | CartResponseDTO |
| GET | `/api/cart/statistics` | 获取统计 | 无 | CartStatisticsDTO |

## ⚠️ 实现注意事项

### 依赖注入
确保在CartService中正确注入所需的Repository：
```java
private final CartRepository cartRepository;
private final UserRepository userRepository;
private final ProductRepository productRepository;
private final CartConverter cartConverter;
```

### 时间戳字段
需要在Cart实体中添加时间戳字段：
```java
@CreationTimestamp
@Column(name = "created_at", updatable = false, nullable = false)
private LocalDateTime createdAt;

@UpdateTimestamp
@Column(name = "updated_at", nullable = false)
private LocalDateTime updatedAt;
```

### 线程安全
建议使用ConcurrentHashMap：
```java
private Map<Long, Integer> cartData = new ConcurrentHashMap<>();
```

### JSONB字段约束
添加字段约束：
```java
@Column(name = "cart_data", columnDefinition = "jsonb", nullable = false)
@JdbcTypeCode(SqlTypes.JSON)
private Map<Long, Integer> cartData = new ConcurrentHashMap<>();
```

## 🎯 下一步实施

按照以下顺序实现：

1. ✅ **创建Repository接口** - 定义数据访问方法
2. ✅ **创建DTO类** - 定义请求和响应数据结构
3. ✅ **创建异常类** - 定义业务异常类型
4. ✅ **实现转换器** - 实体与DTO转换逻辑
5. ✅ **实现Service层** - 核心业务逻辑
6. ✅ **实现Controller层** - RESTful API端点
7. ✅ **添加数据库脚本** - 测试数据
8. ✅ **测试验证** - 使用curl测试所有API

每个步骤完成后，可以运行测试确保功能正常。遇到问题时随时找我指导！