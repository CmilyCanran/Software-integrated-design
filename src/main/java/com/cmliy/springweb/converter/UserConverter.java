package com.cmliy.springweb.converter;

import com.cmliy.springweb.dto.UserDTO;
import com.cmliy.springweb.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 🔄 用户转换器 - User Converter
 *
 * 负责User实体与DTO之间的转换
 * 统一管理用户数据的映射逻辑，确保数据一致性
 *
 * @author Claude
 * @since 2025-11-22
 */
@Component
public class UserConverter {

    /**
     * 🔄 User实体转UserDTO
     *
     * @param user 用户实体
     * @return UserDTO
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        return new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole()
        );
    }

    /**
     * 🔄 批量转换User实体列表为UserDTO列表
     *
     * @param users 用户实体列表
     * @return UserDTO列表
     */
    public List<UserDTO> toDTOList(List<User> users) {
        if (users == null || users.isEmpty()) {
            return List.of();
        }
        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 🔄 UserDTO转User实体（部分字段）
     * 注意：此方法仅用于基本信息转换，不包含密码等敏感信息
     *
     * @param userDTO 用户DTO
     * @return User实体
     */
    public User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User user = new User();
        user.setId(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());

        // 注意：密码和启用状态需要单独设置
        return user;
    }
}