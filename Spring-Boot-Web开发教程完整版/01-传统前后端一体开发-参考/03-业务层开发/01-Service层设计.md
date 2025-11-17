# Service层设计

## 📋 学习目标

- 理解Service层的作用和设计原则
- 掌握Service接口和实现类的编写方法
- 学会业务逻辑的组织和实现
- 了解Service层的异常处理和日志记录

## 🏗️ Service层基础概念

### 什么是Service层？
Service层是业务逻辑层，负责处理应用程序的业务规则和逻辑。它位于Controller层和Repository层之间，起到承上启下的作用。

### Service层的职责
- **业务逻辑处理**：实现复杂的业务规则
- **事务管理**：确保数据的一致性
- **数据转换**：在DTO和Entity之间转换数据
- **异常处理**：处理业务异常并转换为适当的响应
- **调用协调**：协调多个Repository的调用

## 📝 Service接口设计

### 1. 用户服务接口 (UserService)

```java
package com.cmliy.springweb.service;

import com.cmliy.springweb.dto.UserDTO;
import com.cmliy.springweb.dto.UserCreateDTO;
import com.cmliy.springweb.dto.UserUpdateDTO;
import com.cmliy.springweb.dto.UserSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务接口
 * 定义用户相关的业务操作
 */
public interface UserService {

    // ==================== 基础CRUD操作 ====================

    /**
     * 创建用户
     * @param userCreateDTO 用户创建DTO
     * @return 创建的用户DTO
     */
    UserDTO createUser(UserCreateDTO userCreateDTO);

    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户DTO（可能为空）
     */
    Optional<UserDTO> getUserById(Long id);

    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户DTO（可能为空）
     */
    Optional<UserDTO> getUserByUsername(String username);

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param userUpdateDTO 用户更新DTO
     * @return 更新后的用户DTO
     */
    UserDTO updateUser(Long id, UserUpdateDTO userUpdateDTO);

    /**
     * 删除用户
     * @param id 用户ID
     */
    void deleteUser(Long id);

    // ==================== 查询操作 ====================

    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<UserDTO> getAllUsers();

    /**
     * 分页查询用户
     * @param pageable 分页参数
     * @return 分页用户结果
     */
    Page<UserDTO> getUsers(Pageable pageable);

    /**
     * 根据搜索条件查询用户
     * @param criteria 搜索条件
     * @param pageable 分页参数
     * @return 分页搜索结果
     */
    Page<UserDTO> searchUsers(UserSearchCriteria criteria, Pageable pageable);

    /**
     * 根据角色查询用户
     * @param role 用户角色
     * @return 用户列表
     */
    List<UserDTO> getUsersByRole(String role);

    /**
     * 获取启用的用户
     * @return 启用的用户列表
     */
    List<UserDTO> getEnabledUsers();

    // ==================== 业务操作 ====================

    /**
     * 启用/禁用用户
     * @param id 用户ID
     * @param enabled 是否启用
     */
    void toggleUserStatus(Long id, boolean enabled);

    /**
     * 重置用户密码
     * @param id 用户ID
     * @param newPassword 新密码
     */
    void resetPassword(Long id, String newPassword);

    /**
     * 修改用户密码
     * @param id 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long id, String oldPassword, String newPassword);

    /**
     * 验证用户登录
     * @param username 用户名
     * @param password 密码
     * @return 用户DTO（验证失败返回空）
     */
    Optional<UserDTO> authenticateUser(String username, String password);

    // ==================== 统计操作 ====================

    /**
     * 获取用户总数
     * @return 用户总数
     */
    long getTotalUserCount();

    /**
     * 获取启用用户数
     * @return 启用用户数
     */
    long getEnabledUserCount();

    /**
     * 按角色统计用户数
     * @return 角色用户统计结果
     */
    java.util.Map<String, Long> getUserCountByRole();

    // ==================== 批量操作 ====================

    /**
     * 批量启用/禁用用户
     * @param userIds 用户ID列表
     * @param enabled 是否启用
     * @return 操作影响的用户数
     */
    int batchUpdateUserStatus(List<Long> userIds, boolean enabled);

    /**
     * 批量删除用户
     * @param userIds 用户ID列表
     * @return 删除的用户数
     */
    int batchDeleteUsers(List<Long> userIds);
}
```

### 2. 地址服务接口 (AddressService)

```java
package com.cmliy.springweb.service;

import com.cmliy.springweb.dto.AddressDTO;
import com.cmliy.springweb.dto.AddressCreateDTO;
import com.cmliy.springweb.dto.AddressUpdateDTO;
import com.cmliy.springweb.entity.enums.AddressType;

import java.util.List;
import java.util.Optional;

/**
 * 地址服务接口
 */
public interface AddressService {

    /**
     * 创建地址
     * @param addressCreateDTO 地址创建DTO
     * @return 创建的地址DTO
     */
    AddressDTO createAddress(AddressCreateDTO addressCreateDTO);

    /**
     * 根据ID获取地址
     * @param id 地址ID
     * @return 地址DTO（可能为空）
     */
    Optional<AddressDTO> getAddressById(Long id);

    /**
     * 更新地址
     * @param id 地址ID
     * @param addressUpdateDTO 地址更新DTO
     * @return 更新后的地址DTO
     */
    AddressDTO updateAddress(Long id, AddressUpdateDTO addressUpdateDTO);

    /**
     * 删除地址
     * @param id 地址ID
     */
    void deleteAddress(Long id);

    /**
     * 获取用户的所有地址
     * @param userId 用户ID
     * @return 地址列表
     */
    List<AddressDTO> getUserAddresses(Long userId);

    /**
     * 获取用户指定类型的地址
     * @param userId 用户ID
     * @param type 地址类型
     * @return 地址列表
     */
    List<AddressDTO> getUserAddressesByType(Long userId, AddressType type);

    /**
     * 获取用户默认地址
     * @param userId 用户ID
     * @return 默认地址（可能为空）
     */
    Optional<AddressDTO> getUserDefaultAddress(Long userId);

    /**
     * 设置默认地址
     * @param id 地址ID
     * @param userId 用户ID
     */
    void setDefaultAddress(Long id, Long userId);

    /**
     * 清除用户的其他默认地址
     * @param userId 用户ID
     * @param excludeId 排除的地址ID
     */
    void clearOtherDefaultAddresses(Long userId, Long excludeId);
}
```

## 🔧 Service实现类设计

### 1. 用户服务实现类 (UserServiceImpl)

```java
package com.cmliy.springweb.service.impl;

import com.cmliy.springweb.dto.UserDTO;
import com.cmliy.springweb.dto.UserCreateDTO;
import com.cmliy.springweb.dto.UserUpdateDTO;
import com.cmliy.springweb.dto.UserSearchCriteria;
import com.cmliy.springweb.entity.User;
import com.cmliy.springweb.entity.enums.Role;
import com.cmliy.springweb.repository.UserRepository;
import com.cmliy.springweb.service.UserService;
import com.cmliy.springweb.service.mapper.UserMapper;
import com.cmliy.springweb.exception.UserNotFoundException;
import com.cmliy.springweb.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // ==================== 基础CRUD操作 ====================

    @Override
    @Transactional
    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        log.info(" Creating user with username: {}", userCreateDTO.getUsername());

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(userCreateDTO.getUsername())) {
            throw new DuplicateResourceException("用户名已存在: " + userCreateDTO.getUsername());
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
            throw new DuplicateResourceException("邮箱已存在: " + userCreateDTO.getEmail());
        }

        // 转换DTO到Entity
        User user = userMapper.toEntity(userCreateDTO);

        // 加密密码
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));

        // 设置默认值
        user.setEnabled(true);
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 保存用户
        User savedUser = userRepository.save(user);

        log.info("User created successfully with ID: {}", savedUser.getId());

        return userMapper.toDTO(savedUser);
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    public Optional<UserDTO> getUserById(Long id) {
        log.debug("Fetching user by ID: {}", id);

        return userRepository.findById(id)
                .map(userMapper::toDTO);
    }

    @Override
    @Cacheable(value = "users", key = "#username")
    public Optional<UserDTO> getUserByUsername(String username) {
        log.debug("Fetching user by username: {}", username);

        return userRepository.findByUsername(username)
                .map(userMapper::toDTO);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public UserDTO updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        log.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("用户不存在，ID: " + id));

        // 检查邮箱是否被其他用户使用
        if (userUpdateDTO.getEmail() != null &&
            !userUpdateDTO.getEmail().equals(user.getEmail()) &&
            userRepository.existsByEmail(userUpdateDTO.getEmail())) {
            throw new DuplicateResourceException("邮箱已被使用: " + userUpdateDTO.getEmail());
        }

        // 更新字段
        userMapper.updateEntityFromDTO(userUpdateDTO, user);
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);

        log.info("User updated successfully with ID: {}", updatedUser.getId());

        return userMapper.toDTO(updatedUser);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("用户不存在，ID: " + id);
        }

        userRepository.deleteById(id);

        log.info("User deleted successfully with ID: {}", id);
    }

    // ==================== 查询操作 ====================

    @Override
    public List<UserDTO> getAllUsers() {
        log.debug("Fetching all users");

        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserDTO> getUsers(Pageable pageable) {
        log.debug("Fetching users with pagination: {}", pageable);

        return userRepository.findAll(pageable)
                .map(userMapper::toDTO);
    }

    @Override
    public Page<UserDTO> searchUsers(UserSearchCriteria criteria, Pageable pageable) {
        log.debug("Searching users with criteria: {}", criteria);

        // 使用Specification进行动态查询
        // 这里简化处理，实际应该使用UserSpecificationService
        return userRepository.findAll(pageable)
                .map(userMapper::toDTO);
    }

    @Override
    public List<UserDTO> getUsersByRole(String role) {
        log.debug("Fetching users by role: {}", role);

        try {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            return userRepository.findByRole(roleEnum).stream()
                    .map(userMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的角色: " + role);
        }
    }

    @Override
    public List<UserDTO> getEnabledUsers() {
        log.debug("Fetching enabled users");

        return userRepository.findByEnabledTrue().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ==================== 业务操作 ====================

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void toggleUserStatus(Long id, boolean enabled) {
        log.info("Toggling user status for ID: {} to: {}", id, enabled);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("用户不存在，ID: " + id));

        user.setEnabled(enabled);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        log.info("User status updated successfully for ID: {}", id);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void resetPassword(Long id, String newPassword) {
        log.info("Resetting password for user ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("用户不存在，ID: " + id));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        log.info("Password reset successfully for user ID: {}", id);
    }

    @Override
    @Transactional
    public void changePassword(Long id, String oldPassword, String newPassword) {
        log.info("Changing password for user ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("用户不存在，ID: " + id));

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("旧密码不正确");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        log.info("Password changed successfully for user ID: {}", id);
    }

    @Override
    public Optional<UserDTO> authenticateUser(String username, String password) {
        log.debug("Authenticating user: {}", username);

        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .filter(User::getEnabled)
                .map(userMapper::toDTO);
    }

    // ==================== 统计操作 ====================

    @Override
    public long getTotalUserCount() {
        return userRepository.count();
    }

    @Override
    public long getEnabledUserCount() {
        return userRepository.countByEnabledTrue();
    }

    @Override
    public Map<String, Long> getUserCountByRole() {
        List<Object[]> results = userRepository.countUsersByRole();

        return results.stream()
                .collect(Collectors.toMap(
                    result -> ((Role) result[0]).name(),
                    result -> (Long) result[1]
                ));
    }

    // ==================== 批量操作 ====================

    @Override
    @Transactional
    public int batchUpdateUserStatus(List<Long> userIds, boolean enabled) {
        log.info("Batch updating user status for {} users to: {}", userIds.size(), enabled);

        int updatedCount = userRepository.updateUsersStatus(enabled, userIds);

        // 清除缓存
        userIds.forEach(id -> {
            // 这里应该清除相关的缓存
        });

        log.info("Batch update completed. {} users updated.", updatedCount);

        return updatedCount;
    }

    @Override
    @Transactional
    public int batchDeleteUsers(List<Long> userIds) {
        log.info("Batch deleting {} users", userIds.size());

        // 先获取要删除的用户信息用于日志
        List<User> usersToDelete = userRepository.findAllById(userIds);

        // 执行删除
        List<User> deletedUsers = userRepository.findAllById(userIds);
        userRepository.deleteAll(deletedUsers);

        log.info("Batch delete completed. {} users deleted.", deletedUsers.size());

        return deletedUsers.size();
    }
}
```

### 2. 地址服务实现类 (AddressServiceImpl)

```java
package com.cmliy.springweb.service.impl;

import com.cmliy.springweb.dto.AddressDTO;
import com.cmliy.springweb.dto.AddressCreateDTO;
import com.cmliy.springweb.dto.AddressUpdateDTO;
import com.cmliy.springweb.entity.Address;
import com.cmliy.springweb.entity.enums.AddressType;
import com.cmliy.springweb.repository.AddressRepository;
import com.cmliy.springweb.repository.UserRepository;
import com.cmliy.springweb.service.AddressService;
import com.cmliy.springweb.service.mapper.AddressMapper;
import com.cmliy.springweb.exception.AddressNotFoundException;
import com.cmliy.springweb.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 地址服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public AddressDTO createAddress(AddressCreateDTO addressCreateDTO) {
        log.info("Creating address for user ID: {}", addressCreateDTO.getUserId());

        // 验证用户是否存在
        if (!userRepository.existsById(addressCreateDTO.getUserId())) {
            throw new UserNotFoundException("用户不存在，ID: " + addressCreateDTO.getUserId());
        }

        // 如果设置为默认地址，先清除其他默认地址
        if (addressCreateDTO.getIsDefault()) {
            clearOtherDefaultAddresses(addressCreateDTO.getUserId(), null);
        }

        Address address = addressMapper.toEntity(addressCreateDTO);
        Address savedAddress = addressRepository.save(address);

        log.info("Address created successfully with ID: {}", savedAddress.getId());
        return addressMapper.toDTO(savedAddress);
    }

    @Override
    public Optional<AddressDTO> getAddressById(Long id) {
        log.debug("Fetching address by ID: {}", id);

        return addressRepository.findById(id)
                .map(addressMapper::toDTO);
    }

    @Override
    @Transactional
    public AddressDTO updateAddress(Long id, AddressUpdateDTO addressUpdateDTO) {
        log.info("Updating address with ID: {}", id);

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException("地址不存在，ID: " + id));

        // 如果设置为默认地址，先清除其他默认地址
        if (addressUpdateDTO.getIsDefault() != null && addressUpdateDTO.getIsDefault()) {
            clearOtherDefaultAddresses(address.getUser().getId(), id);
        }

        addressMapper.updateEntityFromDTO(addressUpdateDTO, address);
        Address updatedAddress = addressRepository.save(address);

        log.info("Address updated successfully with ID: {}", updatedAddress.getId());
        return addressMapper.toDTO(updatedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        log.info("Deleting address with ID: {}", id);

        if (!addressRepository.existsById(id)) {
            throw new AddressNotFoundException("地址不存在，ID: " + id);
        }

        addressRepository.deleteById(id);

        log.info("Address deleted successfully with ID: {}", id);
    }

    @Override
    public List<AddressDTO> getUserAddresses(Long userId) {
        log.debug("Fetching addresses for user ID: {}", userId);

        return addressRepository.findByUserId(userId).stream()
                .map(addressMapper::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<AddressDTO> getUserAddressesByType(Long userId, AddressType type) {
        log.debug("Fetching addresses for user ID: {} and type: {}", userId, type);

        return addressRepository.findByUserIdAndType(userId, type).stream()
                .map(addressMapper::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Optional<AddressDTO> getUserDefaultAddress(Long userId) {
        log.debug("Fetching default address for user ID: {}", userId);

        return addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .map(addressMapper::toDTO);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long id, Long userId) {
        log.info("Setting address ID: {} as default for user ID: {}", id, userId);

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException("地址不存在，ID: " + id));

        // 验证地址属于指定用户
        if (!address.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("地址不属于指定用户");
        }

        // 清除其他默认地址
        clearOtherDefaultAddresses(userId, id);

        // 设置当前地址为默认
        address.setIsDefault(true);
        addressRepository.save(address);

        log.info("Default address set successfully for user ID: {}", userId);
    }

    @Override
    @Transactional
    public void clearOtherDefaultAddresses(Long userId, Long excludeId) {
        log.debug("Clearing other default addresses for user ID: {}, excluding: {}", userId, excludeId);

        if (excludeId != null) {
            addressRepository.clearOtherDefaultAddresses(userId, excludeId);
        } else {
            // 清除所有默认地址
            List<Address> userAddresses = addressRepository.findByUserId(userId);
            userAddresses.stream()
                    .filter(Address::getIsDefault)
                    .forEach(address -> {
                        address.setIsDefault(false);
                        addressRepository.save(address);
                    });
        }
    }
}
```

## 🗺️ Mapper类设计

### 用户映射器 (UserMapper)

```java
package com.cmliy.springweb.service.mapper;

import com.cmliy.springweb.dto.UserDTO;
import com.cmliy.springweb.dto.UserCreateDTO;
import com.cmliy.springweb.dto.UserUpdateDTO;
import com.cmliy.springweb.entity.User;
import org.springframework.stereotype.Component;

/**
 * 用户实体和DTO之间的映射器
 */
@Component
public class UserMapper {

    /**
     * 实体转DTO
     */
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

        return dto;
    }

    /**
     * 创建DTO转实体
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

        return user;
    }

    /**
     * 使用更新DTO更新实体
     */
    public void updateEntityFromDTO(UserUpdateDTO dto, User user) {
        if (dto == null || user == null) {
            return;
        }

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
    }
}
```

## 🎯 Service层设计原则

### 1. 接口隔离原则
- 每个Service接口专注于特定的业务领域
- 避免过于庞大的接口
- 客户端不应该依赖它不需要的方法

### 2. 单一职责原则
- 每个Service类只负责一种业务类型
- 避免将不相关的业务逻辑混在一起
- 保持方法的内聚性

### 3. 依赖倒置原则
- Service层依赖于抽象接口而不是具体实现
- 使用构造器注入来管理依赖关系
- 便于单元测试和Mock操作

### 4. 开闭原则
- 对扩展开放，对修改关闭
- 通过接口和抽象类支持功能扩展
- 避免修改现有的稳定代码

## ✅ 检查点

完成本节学习后，您应该能够：

- [ ] 理解Service层的作用和设计原则
- [ ] 设计合理的Service接口
- [ ] 实现Service接口的具体业务逻辑
- [ ] 掌握DTO和Entity之间的转换
- [ ] 了解Service层的异常处理方式

## 🚀 下一步

Service层设计完成后，接下来我们将学习：
[事务管理](02-事务管理.md)

---

**提示**: Service层是业务逻辑的核心，应该保持简洁和可测试性。避免在Service层中直接处理HTTP请求或数据库操作细节。