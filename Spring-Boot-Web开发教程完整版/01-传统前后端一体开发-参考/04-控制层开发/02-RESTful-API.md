# RESTful API设计详解

## 📋 学习目标

- 理解RESTful架构和设计原则
- 掌握RESTful API的设计规范
- 学会实现标准的RESTful接口
- 了解API版本控制和错误处理

## 🏗️ RESTful基础概念

### 什么是REST？
REST（Representational State Transfer）是一种软件架构风格，强调资源的表现状态转移。

### RESTful设计原则
- **资源导向**: 以资源为中心设计API
- **统一接口**: 使用统一的HTTP方法
- **无状态**: 每个请求包含完整的处理信息
- **可缓存**: 响应应该明确标识是否可缓存
- **分层系统**: 客户端不需要知道是否直接连接到最终服务器

## 📝 RESTful API设计规范

### 1. URI设计规范

#### 资源命名规则
```java
// ✅ 好的URI设计
GET    /api/v1/users              // 获取用户列表
GET    /api/v1/users/{id}         // 获取指定用户
POST   /api/v1/users              // 创建用户
PUT    /api/v1/users/{id}         // 完整更新用户
PATCH  /api/v1/users/{id}         // 部分更新用户
DELETE /api/v1/users/{id}         // 删除用户

// 资源关系
GET    /api/v1/users/{id}/addresses        // 获取用户地址
POST   /api/v1/users/{id}/addresses        // 为用户添加地址
GET    /api/v1/addresses/{id}               // 获取指定地址
PUT    /api/v1/addresses/{id}               // 更新地址
DELETE /api/v1/addresses/{id}               // 删除地址

// ❌ 避免的URI设计
GET    /api/v1/getAllUsers           // 动词在URI中
GET    /api/v1/users/list/1          // 不必要的路径层级
POST   /api/v1/createUser            // 动词在URI中
GET    /api/v1/users?operation=get  // 操作参数在URI中
```

#### 查询参数规范
```java
// 分页参数
GET /api/v1/users?page=0&size=10&sort=createdAt,desc

// 过滤参数
GET /api/v1/users?role=USER&enabled=true

// 搜索参数
GET /api/v1/users?search=john&fields=username,email

// 日期范围
GET /api/v1/users?startDate=2024-01-01&endDate=2024-12-31

// 字段选择
GET /api/v1/users?fields=id,username,email
```

### 2. HTTP方法使用

#### 标准HTTP方法
```java
@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    // GET - 获取资源
    @GetMapping
    public ResponseEntity<PageResponse<UserDTO>> getUsers() {
        // 获取用户列表
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        // 获取指定用户
    }

    // POST - 创建资源
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO dto) {
        // 创建新用户
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // PUT - 完整更新资源
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO dto) {
        // 完整更新用户信息
    }

    // PATCH - 部分更新资源
    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> patchUser(
            @PathVariable Long id,
            @RequestBody UserUpdateDTO dto) {
        // 部分更新用户信息
    }

    // DELETE - 删除资源
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // 删除用户
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // 其他常用方法
    @Head("/{id}")        // 只获取响应头
    @Options("/{id}")     // 获取支持的HTTP方法
}
```

#### 幂等性说明
| 方法 | 幂等性 | 安全性 | 描述 |
|------|--------|--------|------|
| GET | ✅ | ✅ | 获取资源，不修改服务端状态 |
| HEAD | ✅ | ✅ | 类似GET，只返回头信息 |
| OPTIONS | ✅ | ✅ | 获取支持的方法信息 |
| POST | ❌ | ❌ | 创建资源，每次调用可能创建新资源 |
| PUT | ✅ | ❌ | 完整更新资源，多次调用结果相同 |
| PATCH | ❌ | ❌ | 部分更新，可能产生不同结果 |
| DELETE | ✅ | ❌ | 删除资源，多次删除结果相同 |

## 🔧 完整的RESTful API实现

### 1. 用户管理API

```java
package com.cmliy.springweb.controller.api;

import com.cmliy.springweb.dto.*;
import com.cmliy.springweb.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * 用户管理RESTful API
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    // ==================== 基础CRUD操作 ====================

    /**
     * 获取用户列表
     * 支持分页、排序、过滤
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserDTO>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) String search) {

        log.debug("获取用户列表: page={}, size={}, search={}", page, size, search);

        try {
            // 构建搜索条件
            UserSearchCriteria criteria = new UserSearchCriteria();
            criteria.setKeyword(search);
            criteria.setRole(role != null ? Role.valueOf(role) : null);
            criteria.setEnabled(enabled);

            // 构建分页参数
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<UserDTO> users = userService.searchUsers(criteria, pageable);
            PageResponse<UserDTO> response = PageResponse.of(users);

            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(300)) // 5分钟缓存
                    .body(ApiResponse.success(response));

        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取用户列表失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable Long id) {
        log.debug("获取用户详情: {}", id);

        Optional<UserDTO> user = userService.getUserById(id);

        if (user.isPresent()) {
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(600)) // 10分钟缓存
                    .body(ApiResponse.success(user.get()));
        } else {
            return ResponseEntity.notFound()
                    .body(ApiResponse.error("用户不存在", "USER_NOT_FOUND"));
        }
    }

    /**
     * 创建用户
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createUser(
            @Valid @RequestBody UserCreateDTO userCreateDTO,
            @RequestHeader(value = "X-Request-ID", required = false) String requestId) {

        log.info("创建用户: username={}, requestId={}", userCreateDTO.getUsername(), requestId);

        // 验证密码匹配
        if (!userCreateDTO.isPasswordMatching()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("确认密码不匹配", "PASSWORD_MISMATCH"));
        }

        try {
            UserDTO createdUser = userService.createUser(userCreateDTO);

            // 构建资源URI
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdUser.getId())
                    .toUri();

            return ResponseEntity.created(location)
                    .body(ApiResponse.success("用户创建成功", createdUser));

        } catch (Exception e) {
            log.error("创建用户失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("创建用户失败: " + e.getMessage()));
        }
    }

    /**
     * 完整更新用户
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO,
            @RequestHeader(value = "If-Match", required = false) String ifMatch) {

        log.info("完整更新用户: id={}", id);

        // 可以在这里实现乐观锁检查
        if (ifMatch != null) {
            // 检查ETag或版本号
        }

        try {
            UserDTO updatedUser = userService.updateUser(id, userUpdateDTO);

            return ResponseEntity.ok()
                    .body(ApiResponse.success("用户更新成功", updatedUser));

        } catch (Exception e) {
            log.error("更新用户失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("更新用户失败: " + e.getMessage()));
        }
    }

    /**
     * 部分更新用户
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> patchUser(
            @PathVariable Long id,
            @RequestBody UserUpdateDTO userUpdateDTO) {

        log.info("部分更新用户: id={}", id);

        try {
            // 获取现有用户
            UserDTO existingUser = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            // 只更新非空字段
            UserUpdateDTO patchDTO = new UserUpdateDTO();
            if (userUpdateDTO.getEmail() != null) patchDTO.setEmail(userUpdateDTO.getEmail());
            if (userUpdateDTO.getFullName() != null) patchDTO.setFullName(userUpdateDTO.getFullName());
            if (userUpdateDTO.getPhoneNumber() != null) patchDTO.setPhoneNumber(userUpdateDTO.getPhoneNumber());
            if (userUpdateDTO.getAvatarUrl() != null) patchDTO.setAvatarUrl(userUpdateDTO.getAvatarUrl());

            UserDTO updatedUser = userService.updateUser(id, patchDTO);

            return ResponseEntity.ok()
                    .body(ApiResponse.success("用户部分更新成功", updatedUser));

        } catch (Exception e) {
            log.error("部分更新用户失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("更新用户失败: " + e.getMessage()));
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        log.info("删除用户: id={}", id);

        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build(); // 204 No Content

        } catch (Exception e) {
            log.error("删除用户失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("删除用户失败: " + e.getMessage()));
        }
    }

    // ==================== 批量操作 ====================

    /**
     * 批量获取用户
     */
    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<List<UserDTO>>> batchGetUsers(
            @RequestBody List<Long> userIds) {

        log.info("批量获取用户: count={}", userIds.size());

        try {
            List<UserDTO> users = userIds.stream()
                    .map(userService::getUserById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(java.util.stream.Collectors.toList());

            return ResponseEntity.ok()
                    .body(ApiResponse.success(users));

        } catch (Exception e) {
            log.error("批量获取用户失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("批量获取用户失败: " + e.getMessage()));
        }
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<BatchOperationResult>> batchDeleteUsers(
            @RequestBody BatchDeleteRequest request) {

        log.info("批量删除用户: count={}", request.getUserIds().size());

        try {
            BatchOperationResult result = userService.batchDeleteUsers(request.getUserIds());

            return ResponseEntity.ok()
                    .body(ApiResponse.success("批量删除完成", result));

        } catch (Exception e) {
            log.error("批量删除用户失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("批量删除失败: " + e.getMessage()));
        }
    }

    // ==================== 特殊操作 ====================

    /**
     * 重置密码
     */
    @PostMapping("/{id}/password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable Long id,
            @RequestBody PasswordResetRequest request) {

        log.info("重置用户密码: id={}", id);

        try {
            userService.resetPassword(id, request.getNewPassword());
            return ResponseEntity.ok()
                    .body(ApiResponse.success("密码重置成功", null));

        } catch (Exception e) {
            log.error("重置密码失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("重置密码失败: " + e.getMessage()));
        }
    }

    /**
     * 切换用户状态
     */
    @PostMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> toggleUserStatus(
            @PathVariable Long id,
            @RequestBody UserStatusRequest request) {

        log.info("切换用户状态: id={}, enabled={}", id, request.getEnabled());

        try {
            userService.toggleUserStatus(id, request.getEnabled());
            String status = request.getEnabled() ? "启用" : "禁用";
            return ResponseEntity.ok()
                    .body(ApiResponse.success("用户" + status + "成功", null));

        } catch (Exception e) {
            log.error("切换用户状态失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("操作失败: " + e.getMessage()));
        }
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<UserStatisticsDTO>> getUserStatistics() {
        log.debug("获取用户统计信息");

        try {
            UserStatisticsDTO statistics = UserStatisticsDTO.builder()
                    .totalCount(userService.getTotalUserCount())
                    .enabledCount(userService.getEnabledUserCount())
                    .disabledCount(userService.getTotalUserCount() - userService.getEnabledUserCount())
                    .roleDistribution(userService.getUserCountByRole())
                    .build();

            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(1800)) // 30分钟缓存
                    .body(ApiResponse.success(statistics));

        } catch (Exception e) {
            log.error("获取用户统计失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取统计信息失败: " + e.getMessage()));
        }
    }

    // ==================== HTTP方法支持 ====================

    /**
     * OPTIONS - 获取支持的HTTP方法
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> getUserOptions(@PathVariable Long id) {
        log.debug("获取用户资源支持的HTTP方法: {}", id);

        // 检查用户是否存在
        if (userService.getUserById(id).isPresent()) {
            return ResponseEntity.ok()
                    .allow(HttpMethod.GET, HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE)
                    .build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * HEAD - 获取用户资源头信息
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> getUserHead(@PathVariable Long id) {
        log.debug("获取用户资源头信息: {}", id);

        Optional<UserDTO> user = userService.getUserById(id);

        if (user.isPresent()) {
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(600))
                    .eTag(String.valueOf(user.get().getUpdatedAt().hashCode()))
                    .build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
```

### 2. API版本控制

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserV1Controller {
    // V1版本API实现
}

@RestController
@RequestMapping("/api/v2/users")
public class UserV2Controller {
    // V2版本API实现，可能有不同的字段或行为
}

// 使用请求头版本控制
@RestController
@RequestMapping(value = "/api/users", headers = "X-API-Version=1")
public class UserHeaderV1Controller {
    // V1版本实现
}

@RestController
@RequestMapping(value = "/api/users", headers = "X-API-Version=2")
public class UserHeaderV2Controller {
    // V2版本实现
}
```

### 3. HATEOAS支持

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserHateoasController {

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserDTO>> getUserWithLinks(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("用户不存在"));

        // 构建HATEOAS链接
        EntityModel<UserDTO> userModel = EntityModel.of(user,
                linkTo(methodOn(UserHateoasController.class).getUser(id)).withSelfRel(),
                linkTo(methodOn(UserHateoasController.class).getUserAddresses(id)).withRel("addresses"),
                linkTo(methodOn(UserHateoasController.class).updateUser(id, null)).withRel("update"),
                linkTo(methodOn(UserHateoasController.class).deleteUser(id)).withRel("delete")
        );

        return ResponseEntity.ok(userModel);
    }

    @GetMapping("/{id}/addresses")
    public ResponseEntity<CollectionModel<AddressDTO>> getUserAddresses(@PathVariable Long id) {
        List<AddressDTO> addresses = addressService.getUserAddresses(id);

        CollectionModel<AddressDTO> addressModel = CollectionModel.of(addresses,
                linkTo(methodOn(UserHateoasController.class).getUserAddresses(id)).withSelfRel(),
                linkTo(methodOn(UserHateoasController.class).getUser(id)).withRel("user")
        );

        return ResponseEntity.ok(addressModel);
    }
}
```

## 📊 API响应格式设计

### 1. 统一响应格式

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    private LocalDateTime timestamp;
    private String requestId;
    private Map<String, Object> metadata;

    // 成功响应
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message("操作成功")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 错误响应
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
```

### 2. 分页响应格式

```java
@Data
@Builder
public class PageResponse<T> {
    private List<T> content;
    private PageInfo page;
    private List<Link> links;

    @Data
    @Builder
    public static class PageInfo {
        private int number;          // 当前页码
        private int size;            // 每页大小
        private long totalElements;  // 总记录数
        private int totalPages;      // 总页数
        private boolean first;       // 是否第一页
        private boolean last;        // 是否最后一页
        private boolean hasNext;     // 是否有下一页
        private boolean hasPrevious; // 是否有上一页
    }

    @Data
    @Builder
    public static class Link {
        private String rel;
        private String href;
    }

    public static <T> PageResponse<T> of(Page<T> page) {
        // 构建分页链接
        List<Link> links = buildLinks(page);

        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(PageInfo.builder()
                        .number(page.getNumber())
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .first(page.isFirst())
                        .last(page.isLast())
                        .hasNext(page.hasNext())
                        .hasPrevious(page.hasPrevious())
                        .build())
                .links(links)
                .build();
    }

    private static <T> List<Link> buildLinks(Page<T> page) {
        List<Link> links = new ArrayList<>();

        // self链接
        links.add(Link.builder()
                .rel("self")
                .href(buildPageUrl(page.getNumber(), page.getSize()))
                .build());

        // first链接
        if (!page.isFirst()) {
            links.add(Link.builder()
                    .rel("first")
                    .href(buildPageUrl(0, page.getSize()))
                    .build());
        }

        // prev链接
        if (page.hasPrevious()) {
            links.add(Link.builder()
                    .rel("prev")
                    .href(buildPageUrl(page.getNumber() - 1, page.getSize()))
                    .build());
        }

        // next链接
        if (page.hasNext()) {
            links.add(Link.builder()
                    .rel("next")
                    .href(buildPageUrl(page.getNumber() + 1, page.getSize()))
                    .build());
        }

        // last链接
        if (!page.isLast()) {
            links.add(Link.builder()
                    .rel("last")
                    .href(buildPageUrl(page.getTotalPages() - 1, page.getSize()))
                    .build());
        }

        return links;
    }

    private static String buildPageUrl(int page, int size) {
        return String.format("/api/v1/users?page=%d&size=%d", page, size);
    }
}
```

## 🔍 API文档和测试

### 1. OpenAPI (Swagger) 配置

```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("用户管理系统 API")
                        .version("1.0.0")
                        .description("Spring Boot用户管理系统的RESTful API文档")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@example.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("开发环境"),
                        new Server().url("https://api.example.com").description("生产环境")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
```

### 2. API文档注解

```java
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "用户管理", description = "用户相关的API接口")
public class DocumentedUserController {

    @Operation(summary = "获取用户列表", description = "分页获取用户列表，支持搜索和过滤")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功获取用户列表",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserDTO>>> getUsers(
            @Parameter(description = "页码，从0开始", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "排序字段", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,

            @Parameter(description = "排序方向", schema = @Schema(allowableValues = {"asc", "desc"}))
            @RequestParam(defaultValue = "desc") String sortDir) {

        // 实现逻辑
    }

    @Operation(summary = "创建用户", description = "创建新用户账号")
    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createUser(
            @Parameter(description = "用户创建信息", required = true)
            @Valid @RequestBody UserCreateDTO userCreateDTO) {

        // 实现逻辑
    }
}
```

## ✅ 检查点

完成本节学习后，您应该能够：

- [ ] 理解RESTful架构和设计原则
- [ ] 设计符合规范的RESTful API
- [ ] 实现标准的HTTP方法操作
- [ ] 处理API版本控制和错误响应
- [ ] 生成API文档

## 🚀 下一步

RESTful API设计完成后，接下来我们将学习：
[表单处理](03-表单处理.md)

---

**提示**: 良好的RESTful API设计应该遵循统一的规范，提供清晰的文档，确保易用性和可维护性。