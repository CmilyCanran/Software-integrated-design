# Repository开发指南

## 📋 学习目标

- 理解Spring Data JPA Repository的概念
- 掌握Repository接口的使用方法
- 学会自定义查询方法
- 了解分页、排序和条件查询

## 🏗️ Repository基础概念

### 什么是Repository？
Repository是数据访问层的接口，Spring Data JPA会根据接口定义自动生成实现类，提供数据库CRUD操作。

### Repository层次结构

```
Repository (标记接口)
├── CrudRepository (提供CRUD操作)
├── PagingAndSortingRepository (提供分页和排序)
└── JpaRepository (继承以上所有，提供JPA特定功能)
```

## 📝 Repository接口开发

### 1. 用户Repository

```java
package com.cmliy.springweb.repository;

import com.cmliy.springweb.entity.User;
import com.cmliy.springweb.entity.enums.Gender;
import com.cmliy.springweb.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // ==================== 基础查询方法 ====================

    // 根据用户名查找（自动生成）
    Optional<User> findByUsername(String username);

    // 根据邮箱查找
    Optional<User> findByEmail(String email);

    // 根据用户名或邮箱查找
    Optional<User> findByUsernameOrEmail(String username, String email);

    // 根据角色查找用户列表
    List<User> findByRole(Role role);

    // 根据性别查找用户列表
    List<User> findByGender(Gender gender);

    // 查找启用的用户
    List<User> findByEnabledTrue();

    // ==================== 条件查询 ====================

    // 根据用户名模糊查询
    List<User> findByUsernameContainingIgnoreCase(String keyword);

    // 根据邮箱模糊查询
    List<User> findByEmailContainingIgnoreCase(String keyword);

    // 根据全名模糊查询
    List<User> findByFullNameContainingIgnoreCase(String keyword);

    // ==================== 统计查询 ====================

    // 统计指定角色的用户数量
    long countByRole(Role role);

    // 统计启用的用户数量
    long countByEnabledTrue();

    // 检查用户名是否存在
    boolean existsByUsername(String username);

    // 检查邮箱是否存在
    boolean existsByEmail(String email);

    // ==================== 排序查询 ====================

    // 按创建时间降序查找所有用户
    List<User> findAllByOrderByCreatedAtDesc();

    // 按用户名升序查找用户
    List<User> findByRoleOrderByUsernameAsc(Role role);

    // ==================== 分页查询 ====================

    // 分页查询所有用户
    Page<User> findAll(Pageable pageable);

    // 根据角色分页查询
    Page<User> findByRole(Role role, Pageable pageable);

    // 根据启用状态分页查询
    Page<User> findByEnabled(Boolean enabled, Pageable pageable);

    // ==================== 自定义JPQL查询 ====================

    // 使用JPQL查询（推荐）
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.email LIKE %:keyword%")
    List<User> searchUsers(@Param("keyword") String keyword);

    // 查询指定时间范围内的用户
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    List<User> findUsersByDateRange(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    // 统计各角色用户数量
    @Query("SELECT u.role, COUNT(u) FROM User u GROUP BY u.role")
    List<Object[]> countUsersByRole();

    // 查找最近注册的用户
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    List<User> findRecentUsers(Pageable pageable);

    // ==================== 更新操作 ====================

    // 批量更新用户状态
    @Modifying
    @Query("UPDATE User u SET u.enabled = :enabled WHERE u.id IN :userIds")
    int updateUsersStatus(@Param("enabled") Boolean enabled, @Param("userIds") List<Long> userIds);

    // 更新用户最后登录时间
    @Modifying
    @Query("UPDATE User u SET u.updatedAt = :loginTime WHERE u.id = :userId")
    int updateLastLoginTime(@Param("userId") Long userId, @Param("loginTime") LocalDateTime loginTime);

    // ==================== 原生SQL查询 ====================

    // 使用原生SQL（复杂查询时使用）
    @Query(value = "SELECT * FROM users WHERE DATE(created_at) = CURRENT_DATE", nativeQuery = true)
    List<User> findTodayRegisteredUsers();

    // 统计查询（原生SQL）
    @Query(value = "SELECT COUNT(*) FROM users WHERE enabled = true", nativeQuery = true)
    long countActiveUsers();

    // ==================== 复杂查询示例 ====================

    // 查找活跃用户（最近30天有活动）
    @Query("SELECT u FROM User u WHERE u.enabled = true AND u.updatedAt >= :since")
    List<User> findActiveUsers(@Param("since") LocalDateTime since);

    // 查找没有设置头像的用户
    @Query("SELECT u FROM User u WHERE u.avatarUrl IS NULL OR u.avatarUrl = ''")
    List<User> findUsersWithoutAvatar();

    // 查找生日在本月的用户
    @Query("SELECT u FROM User u WHERE MONTH(u.birthDate) = :month")
    List<User> findUsersByBirthMonth(@Param("month") int month);
}
```

### 2. 地址Repository

```java
package com.cmliy.springweb.repository;

import com.cmliy.springweb.entity.Address;
import com.cmliy.springweb.entity.enums.AddressType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    // 根据用户查找地址
    List<Address> findByUserId(Long userId);

    // 根据用户和地址类型查找
    List<Address> findByUserIdAndType(Long userId, AddressType type);

    // 根据用户查找默认地址
    Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);

    // 根据城市查找地址
    List<Address> findByCityIgnoreCase(String city);

    // 根据省份查找地址
    List<Address> findByProvinceIgnoreCase(String province);

    // 统计用户的地址数量
    long countByUserId(Long userId);

    // 查找用户的默认地址数量
    long countByUserIdAndIsDefaultTrue(Long userId);

    // 更新默认地址（清除其他默认地址）
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId AND a.id != :excludeId")
    int clearOtherDefaultAddresses(@Param("userId") Long userId, @Param("excludeId") Long excludeId);
}
```

## 🔍 自定义查询方法命名规则

### 1. 基础查询

| 方法名 | 说明 | 示例 |
|--------|------|------|
| findByXxx | 根据字段查找 | `findByUsername(String username)` |
| readByXxx | 根据字段读取 | `readByEmail(String email)` |
| getByXxx | 根据字段获取 | `getByRole(Role role)` |
| queryByXxx | 根据字段查询 | `queryByEnabled(Boolean enabled)` |

### 2. 条件组合

| 关键词 | 说明 | 示例 |
|--------|------|------|
| And | 并且 | `findByUsernameAndEmail(String username, String email)` |
| Or | 或者 | `findByUsernameOrEmail(String username, String email)` |
| Between | 在...之间 | `findByCreatedAtBetween(LocalDateTime start, LocalDateTime end)` |
| LessThan | 小于 | `findByAgeLessThan(int age)` |
| GreaterThan | 大于 | `findByAgeGreaterThan(int age)` |
| Like | 模糊查询 | `findByUsernameLike(String pattern)` |
| In | 在集合中 | `findByRoleIn(List<Role> roles)` |
| NotIn | 不在集合中 | `findByRoleNotIn(List<Role> roles)` |

### 3. 字符串操作

| 关键词 | 说明 | 示例 |
|--------|------|------|
| StartingWith | 以...开头 | `findByUsernameStartingWith(String prefix)` |
| EndingWith | 以...结尾 | `findByUsernameEndingWith(String suffix)` |
| Containing | 包含 | `findByUsernameContaining(String keyword)` |
| IgnoreCase | 忽略大小写 | `findByUsernameIgnoreCase(String username)` |

### 4. 排序和分页

| 关键词 | 说明 | 示例 |
|--------|------|------|
| OrderBy | 排序 | `findByRoleOrderByUsernameAsc(Role role)` |
| Asc | 升序 | `findAllByOrderByIdAsc()` |
| Desc | 降序 | `findAllByOrderByCreatedAtDesc()` |
| Top/First | 限制数量 | `findTop10ByRoleOrderByCreatedAtDesc(Role role)` |

## 📄 分页和排序

### 1. 分页查询

```java
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Page<User> getUsersWithPagination(int page, int size, String sortBy) {
        // 创建分页请求
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        // 执行分页查询
        return userRepository.findAll(pageable);
    }

    public Page<User> searchUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByUsernameContainingIgnoreCase(keyword, pageable);
    }
}
```

### 2. 排序查询

```java
public List<User> getSortedUsers(String sortBy, String direction) {
    // 创建排序对象
    Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);

    // 执行排序查询
    return userRepository.findAll(sort);
}

// 多字段排序
public List<User> getUsersWithMultiSort() {
    Sort sort = Sort.by("role").ascending()
                   .and(Sort.by("username").ascending());
    return userRepository.findAll(sort);
}
```

### 3. 分页响应对象

```java
public class PageResponse<T> {
    private List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;

    // 构造器和getter/setter方法
    public static <T> PageResponse<T> of(Page<T> page) {
        PageResponse<T> response = new PageResponse<>();
        response.setContent(page.getContent());
        response.setCurrentPage(page.getNumber());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        response.setHasNext(page.hasNext());
        response.setHasPrevious(page.hasPrevious());
        return response;
    }
}
```

## 🔧 高级查询功能

### 1. Specification动态查询

```java
@Service
public class UserSpecificationService {

    public Specification<User> hasUsername(String username) {
        return (root, query, criteriaBuilder) ->
            username == null ? null :
            criteriaBuilder.equal(root.get("username"), username);
    }

    public Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) ->
            email == null ? null :
            criteriaBuilder.equal(root.get("email"), email);
    }

    public Specification<User> hasRole(Role role) {
        return (root, query, criteriaBuilder) ->
            role == null ? null :
            criteriaBuilder.equal(root.get("role"), role);
    }

    public Specification<User> keywordInUsernameOrEmail(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return null;
            }
            String pattern = "%" + keyword.toLowerCase() + "%";
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), pattern)
            );
        };
    }

    // 组合查询示例
    public Page<User> searchUsers(UserSearchCriteria criteria, Pageable pageable) {
        Specification<User> spec = Specification.where(hasUsername(criteria.getUsername()))
                                              .and(hasEmail(criteria.getEmail()))
                                              .and(hasRole(criteria.getRole()))
                                              .and(keywordInUsernameOrEmail(criteria.getKeyword()));

        return userRepository.findAll(spec, pageable);
    }
}
```

### 2. 查询条件对象

```java
@Data
public class UserSearchCriteria {
    private String username;
    private String email;
    private Role role;
    private Gender gender;
    private Boolean enabled;
    private String keyword;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
```

## 🎯 最佳实践

### 1. Repository设计原则
- 保持接口简洁，只定义必要的查询方法
- 使用有意义的命名，遵循Spring Data命名规范
- 复杂查询使用@Query注解，提高可读性
- 批量操作使用@Modifying注解

### 2. 性能优化
- 使用分页查询避免大量数据加载
- 合理使用LAZY加载避免N+1问题
- 复杂查询考虑使用原生SQL
- 添加适当的数据库索引

### 3. 异常处理
```java
@Service
public class UserService {

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("用户不存在，ID: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("用户不存在，用户名: " + username));
    }
}
```

## ✅ 检查点

完成本节学习后，您应该能够：

- [ ] 理解Repository接口的层次结构
- [ ] 掌握查询方法命名规则
- [ ] 实现分页和排序查询
- [ ] 使用@Query注解进行自定义查询
- [ ] 使用Specification进行动态查询

## 🚀 下一步

Repository开发完成后，接下来我们将学习：
[数据库表结构设计](03-数据库设计.md)

---

**提示**: Repository接口应该保持简洁，复杂业务逻辑应该在Service层实现。