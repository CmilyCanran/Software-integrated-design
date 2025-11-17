# DTO设计模式详解

## 📋 学习目标

- 理解DTO的概念和作用
- 掌握DTO的设计原则和最佳实践
- 学会Entity和DTO之间的转换
- 了解不同类型DTO的使用场景

## 🏗️ DTO基础概念

### 什么是DTO？
DTO（Data Transfer Object）数据传输对象，用于在不同层之间传输数据的对象。

### DTO的作用
- **数据封装**: 封装需要传输的数据
- **接口隔离**: 隔离内部实体和外部接口
- **数据验证**: 在传输过程中进行数据验证
- **性能优化**: 避免传输不必要的数据

## 📝 DTO类型设计

### 1. 基础DTO类

#### 用户基础DTO (UserDTO)
```java
package com.cmliy.springweb.dto;

import com.cmliy.springweb.entity.enums.Gender;
import com.cmliy.springweb.entity.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户基础DTO - 用于数据展示
 */
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private Gender gender;
    private java.util.Date birthDate;
    private String avatarUrl;
    private Boolean enabled;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联数据
    private List<AddressDTO> addresses;
    private UserProfileDTO profile;
    private Integer addressCount;
}
```

#### 用户创建DTO (UserCreateDTO)
```java
package com.cmliy.springweb.dto;

import com.cmliy.springweb.entity.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

/**
 * 用户创建DTO - 用于创建用户
 */
@Data
public class UserCreateDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20之间")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100")
    private String email;

    @Size(max = 100, message = "全名长度不能超过100")
    private String fullName;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phoneNumber;

    private Gender gender;

    @Past(message = "出生日期必须是过去的日期")
    private Date birthDate;

    @URL(message = "头像URL格式不正确")
    @Size(max = 255, message = "头像URL长度不能超过255")
    private String avatarUrl;

    // 自定义验证
    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }
}
```

#### 用户更新DTO (UserUpdateDTO)
```java
package com.cmliy.springweb.dto;

import com.cmliy.springweb.entity.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

/**
 * 用户更新DTO - 用于更新用户信息
 */
@Data
public class UserUpdateDTO {

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100")
    private String email;

    @Size(max = 100, message = "全名长度不能超过100")
    private String fullName;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phoneNumber;

    private Gender gender;

    @Past(message = "出生日期必须是过去的日期")
    private Date birthDate;

    @URL(message = "头像URL格式不正确")
    @Size(max = 255, message = "头像URL长度不能超过255")
    private String avatarUrl;

    private Boolean enabled;

    // 注意：密码更新通常使用单独的DTO
}
```

#### 密码更新DTO (PasswordUpdateDTO)
```java
package com.cmliy.springweb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 密码更新DTO
 */
@Data
public class PasswordUpdateDTO {

    @NotBlank(message = "当前密码不能为空")
    private String currentPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "新密码长度必须在6-20之间")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]",
            message = "密码必须包含大小写字母、数字和特殊字符")
    private String newPassword;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    public boolean isNewPasswordMatching() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
}
```

### 2. 查询DTO类

#### 用户搜索条件DTO (UserSearchCriteria)
```java
package com.cmliy.springweb.dto;

import com.cmliy.springweb.entity.enums.Gender;
import com.cmliy.springweb.entity.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户搜索条件DTO
 */
@Data
public class UserSearchCriteria {

    private String username;          // 用户名模糊查询
    private String email;             // 邮箱模糊查询
    private String fullName;          // 全名模糊查询
    private Role role;                // 精确匹配角色
    private Gender gender;            // 精确匹配性别
    private Boolean enabled;          // 是否启用
    private String keyword;           // 通用关键词（搜索用户名、邮箱、全名）

    private LocalDateTime startDate;  // 创建时间范围-开始
    private LocalDateTime endDate;    // 创建时间范围-结束
    private String sortBy = "createdAt"; // 排序字段
    private String sortDirection = "DESC"; // 排序方向

    // 分页参数（通常由Controller层处理）
    private Integer page = 0;
    private Integer size = 10;
}
```

#### 分页响应DTO (PageResponse)
```java
package com.cmliy.springweb.dto;

import lombok.Data;

import java.util.List;

/**
 * 分页响应DTO
 */
@Data
public class PageResponse<T> {

    private List<T> content;          // 数据列表
    private int currentPage;          // 当前页码（从0开始）
    private int pageSize;             // 每页大小
    private long totalElements;       // 总记录数
    private int totalPages;           // 总页数
    private boolean first;            // 是否第一页
    private boolean last;             // 是否最后一页
    private boolean hasNext;          // 是否有下一页
    private boolean hasPrevious;      // 是否有上一页

    public static <T> PageResponse<T> of(org.springframework.data.domain.Page<T> page) {
        PageResponse<T> response = new PageResponse<>();
        response.setContent(page.getContent());
        response.setCurrentPage(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        response.setHasNext(page.hasNext());
        response.setHasPrevious(page.hasPrevious());
        return response;
    }

    public static <T> PageResponse<T> empty(int page, int size) {
        PageResponse<T> response = new PageResponse<>();
        response.setContent(List.of());
        response.setCurrentPage(page);
        response.setPageSize(size);
        response.setTotalElements(0);
        response.setTotalPages(0);
        response.setFirst(true);
        response.setLast(true);
        response.setHasNext(false);
        response.setHasPrevious(false);
        return response;
    }
}
```

### 3. 地址相关DTO

#### 地址DTO (AddressDTO)
```java
package com.cmliy.springweb.dto;

import com.cmliy.springweb.entity.enums.AddressType;
import lombok.Data;

/**
 * 地址DTO
 */
@Data
public class AddressDTO {
    private Long id;
    private Long userId;
    private String street;
    private String city;
    private String province;
    private String postalCode;
    private String country;
    private AddressType type;
    private Boolean isDefault;
    private String fullAddress; // 完整地址（计算属性）
}
```

#### 地址创建DTO (AddressCreateDTO)
```java
package com.cmliy.springweb.dto;

import com.cmliy.springweb.entity.enums.AddressType;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 地址创建DTO
 */
@Data
public class AddressCreateDTO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "街道地址不能为空")
    @Size(max = 200, message = "街道地址长度不能超过200")
    private String street;

    @NotBlank(message = "城市不能为空")
    @Size(max = 100, message = "城市名称长度不能超过100")
    private String city;

    @NotBlank(message = "省份不能为空")
    @Size(max = 100, message = "省份名称长度不能超过100")
    private String province;

    @Pattern(regexp = "^\\d{6}$", message = "邮政编码格式不正确")
    private String postalCode;

    @Size(max = 100, message = "国家名称长度不能超过100")
    private String country = "中国";

    @NotNull(message = "地址类型不能为空")
    private AddressType type;

    private Boolean isDefault = false;
}
```

### 4. 响应DTO类

#### 统一响应DTO (ApiResponse)
```java
package com.cmliy.springweb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 统一API响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;          // 是否成功
    private String message;           // 响应消息
    private T data;                   // 响应数据
    private String errorCode;         // 错误代码
    private LocalDateTime timestamp;  // 响应时间戳

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

    public static <T> ApiResponse<T> error(String message, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
```

#### 批量操作结果DTO (BatchOperationResult)
```java
package com.cmliy.springweb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量操作结果DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchOperationResult {

    private int totalCount;          // 总数量
    private int successCount;        // 成功数量
    private int failureCount;        // 失败数量
    private List<String> failures;   // 失败详情
    private String message;          // 操作消息

    public boolean isAllSuccess() {
        return failureCount == 0;
    }

    public boolean hasFailures() {
        return failureCount > 0;
    }

    public double getSuccessRate() {
        return totalCount == 0 ? 0.0 : (double) successCount / totalCount;
    }
}
```

## 🔄 DTO转换器设计

### 1. 基础转换器接口

```java
package com.cmliy.springweb.service.mapper;

import java.util.List;

/**
 * DTO转换器基础接口
 */
public interface BaseMapper<E, D> {

    /**
     * Entity转DTO
     */
    D toDTO(E entity);

    /**
     * DTO转Entity
     */
    E toEntity(D dto);

    /**
     * Entity列表转DTO列表
     */
    List<D> toDTOList(List<E> entities);

    /**
     * DTO列表转Entity列表
     */
    List<E> toEntityList(List<D> dtos);

    /**
     * 更新Entity从DTO
     */
    void updateEntityFromDTO(D dto, E entity);
}
```

### 2. 用户转换器实现

```java
package com.cmliy.springweb.service.mapper;

import com.cmliy.springweb.dto.UserDTO;
import com.cmliy.springweb.dto.UserCreateDTO;
import com.cmliy.springweb.dto.UserUpdateDTO;
import com.cmliy.springweb.entity.User;
import com.cmliy.springweb.entity.enums.Gender;
import com.cmliy.springweb.entity.enums.Role;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户实体和DTO转换器
 */
@Component
public class UserMapper implements BaseMapper<User, UserDTO> {

    @Override
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setGender(user.getGender());
        dto.setBirthDate(user.getBirthDate());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setEnabled(user.getEnabled());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        // 计算关联数据
        if (user.getAddresses() != null) {
            dto.setAddressCount(user.getAddresses().size());
            dto.setAddresses(user.getAddresses().stream()
                    .map(address -> addressMapper.toDTO(address))
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    @Override
    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setGender(dto.getGender());
        user.setBirthDate(dto.getBirthDate());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setEnabled(dto.getEnabled());
        user.setRole(dto.getRole());
        user.setCreatedAt(dto.getCreatedAt());
        user.setUpdatedAt(dto.getUpdatedAt());

        return user;
    }

    /**
     * 创建DTO转Entity（不包含ID和创建时间）
     */
    public User toEntity(UserCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setGender(dto.getGender());
        user.setBirthDate(dto.getBirthDate());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setEnabled(true); // 默认启用
        user.setRole(Role.USER); // 默认角色

        return user;
    }

    @Override
    public void updateEntityFromDTO(UserUpdateDTO dto, User user) {
        if (dto == null || user == null) {
            return;
        }

        // 只更新非空字段
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }
        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        if (dto.getBirthDate() != null) {
            user.setBirthDate(dto.getBirthDate());
        }
        if (dto.getAvatarUrl() != null) {
            user.setAvatarUrl(dto.getAvatarUrl());
        }
        if (dto.getEnabled() != null) {
            user.setEnabled(dto.getEnabled());
        }
    }

    @Override
    public List<UserDTO> toDTOList(List<User> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<User> toEntityList(List<UserDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return List.of();
        }
        return dtos.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
```

### 3. 高级转换器（MapStruct示例）

```java
package com.cmliy.springweb.service.mapper;

import com.cmliy.springweb.dto.UserDTO;
import com.cmliy.springweb.dto.UserCreateDTO;
import com.cmliy.springweb.dto.UserUpdateDTO;
import com.cmliy.springweb.entity.User;
import org.mapstruct.*;

import java.util.List;

/**
 * 使用MapStruct的用户映射器
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserStructMapper {

    UserStructMapper INSTANCE = Mappers.getMapper(UserStructMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "enabled", constant = "true")
    User toEntity(UserCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDTO(UserUpdateDTO dto, @MappingTarget User entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(UserDTO dto, @MappingTarget User entity);

    @Named("calculateAddressCount")
    default Integer calculateAddressCount(User user) {
        return user.getAddresses() != null ? user.getAddresses().size() : 0;
    }

    @Mapping(target = "addressCount", source = "user", qualifiedByName = "calculateAddressCount")
    UserDTO toDTO(User user);

    List<UserDTO> toDTOList(List<User> users);

    List<User> toEntityList(List<UserDTO> userDTOs);
}
```

## 🎯 DTO设计最佳实践

### 1. 命名规范
```java
// ✅ 好的命名
UserDTO, UserCreateDTO, UserUpdateDTO, UserSearchDTO

// ❌ 避免的命名
UserData, UserInformation, UserVO, UserForm
```

### 2. 字段选择原则
```java
// ✅ 只包含必要字段
@Data
public class UserListDTO {
    private Long id;
    private String username;
    private String email;
    private Boolean enabled;
    private String avatarUrl;
}

// ❌ 包含不必要字段
@Data
public class UserListDTO {
    private Long id;
    private String username;
    private String email;
    private String password; // 列表中不需要密码
    private String phoneNumber;
    private String fullName;
    private Gender gender;
    private Date birthDate;
    private String avatarUrl;
    private Boolean enabled;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // ... 更多字段
}
```

### 3. 验证注解使用
```java
@Data
public class ValidatedDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
    private String username;

    @Email(message = "邮箱格式不正确")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phoneNumber;

    @Min(value = 0, message = "年龄不能小于0")
    @Max(value = 150, message = "年龄不能大于150")
    private Integer age;
}
```

### 4. 构造器模式使用
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryDTO {
    private String username;
    private String email;
    private Role role;
    private Boolean enabled;
    private Integer page;
    private Integer size;
}

// 使用示例
UserQueryDTO query = UserQueryDTO.builder()
    .username("john")
    .role(Role.USER)
    .enabled(true)
    .page(0)
    .size(10)
    .build();
```

## 🔧 DTO高级特性

### 1. 条件序列化
```java
@Data
public class ConditionalSerializationDTO {

    private Long id;
    private String username;

    @JsonIgnore
    private String password; // 永不序列化

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String email; // 只读，不反序列化

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String secretKey; // 只写，不序列化

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String optionalField; // null值不序列化

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
```

### 2. 继承设计
```java
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseUserDTO {
    private Long id;
    private String username;
    private String email;
    private Boolean enabled;
}

@Data
@EqualsAndHashCode(callSuper = true)
public class UserDetailDTO extends BaseUserDTO {
    private String fullName;
    private String phoneNumber;
    private String avatarUrl;
    private List<AddressDTO> addresses;
    private UserProfileDTO profile;
}

@Data
@EqualsAndHashCode(callSuper = true)
public class UserSummaryDTO extends BaseUserDTO {
    private Integer addressCount;
    private LocalDateTime lastLoginAt;
}
```

### 3. 组合DTO
```java
@Data
public class UserWithStatsDTO {
    private UserDTO user;
    private UserStatsDTO stats;
}

@Data
public class UserStatsDTO {
    private int addressCount;
    private int orderCount;
    private LocalDateTime lastLoginAt;
    private String membershipLevel;
}
```

## ✅ 检查点

完成本节学习后，您应该能够：

- [ ] 理解DTO的概念和作用
- [ ] 设计合理的DTO类结构
- [ ] 实现Entity和DTO之间的转换
- [ ] 使用验证注解进行数据验证
- [ ] 掌握DTO设计的最佳实践

## 🚀 下一步

DTO设计完成后，接下来我们将进入：
[控制层开发 - 创建Controller](../04-控制层开发/01-控制器开发.md)

---

**提示**: DTO是数据传输的重要工具，合理使用DTO可以提高系统的安全性和性能，同时简化前后端数据交互。