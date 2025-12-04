package com.cmliy.springweb.converter;

import com.cmliy.springweb.dto.UserDTO;
import com.cmliy.springweb.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 🔄 用户转换器 - User Converter
 *
 * 负责User实体与DTO之间的转换
 * 集成DtoConverterUtils通用转换工具，支持Builder模式
 * 统一管理用户数据的映射逻辑，确保数据一致性

 */
@Component
@Slf4j
public class UserConverter extends BaseConverter<User, UserDTO> {

    /**
     * 🔄 User实体转UserDTO
     * 使用Builder模式创建DTO，保持类型安全和代码简洁
     *
     * @param user 用户实体
     * @return UserDTO
     */
    public UserDTO toDTO(User user) {
        if (user == null) return null;

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    /**
     * 🔄 UserDTO转User实体
     * 使用智能字段映射，只设置非null字段
     *
     * @param userDTO 用户DTO
     * @return User实体
     */
    public User toEntity(UserDTO userDTO) {
        if (userDTO == null) return null;

        return User.builder()
                .id(userDTO.getId())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .role(userDTO.getRole())
                .build();
    }

    /**
     * 🔄 批量转换User实体列表为UserDTO列表
     * 使用Stream API进行高效批量转换
     *
     * @param users 用户实体列表
     * @return UserDTO列表
     */
    @Override
    public List<UserDTO> toDTOList(List<User> users) {
        if (users == null || users.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 🔧 获取DTO类型（用于通用转换）
     *
     * @return UserDTO类
     */
    @Override
    protected Class<UserDTO> getDTOClass() {
        return UserDTO.class;
    }

    /**
     * 🔧 获取实体类型（用于通用转换）
     *
     * @return User类
     */
    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    /**
     * 🔄 安全转换User实体（处理null值）
     *
     * @param user 用户实体（可能为null）
     * @return UserDTO或null
     */
    @Override
    public UserDTO safeToDTO(User user) {
        return toDTO(user);
    }

    /**
     * 🔄 安全转换UserDTO（处理null值）
     *
     * @param userDTO 用户DTO（可能为null）
     * @return User实体或null
     */
    @Override
    public User safeToEntity(UserDTO userDTO) {
        return toEntity(userDTO);
    }
}