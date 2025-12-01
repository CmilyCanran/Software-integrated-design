package com.cmliy.springweb.util;

import com.cmliy.springweb.exception.ConversionException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 通用DTO转换工具类
 * 提供Entity与DTO之间的转换功能，支持Builder模式
 */
@Component
@Slf4j
public class DtoConverterUtils {

    private final ModelMapper modelMapper;

    public DtoConverterUtils() {
        this.modelMapper = new ModelMapper();
        // 配置ModelMapper跳过null值
        this.modelMapper.getConfiguration().setSkipNullEnabled(true);
    }

    /**
     * 通用Entity到DTO转换 - 支持Builder模式
     *
     * @param entity 源实体对象
     * @param dtoClass 目标DTO类
     * @param <E> 实体类型
     * @param <D> DTO类型
     * @return 转换后的DTO对象
     */
    public <E, D> D toDTO(E entity, Class<D> dtoClass) {
        if (entity == null) return null;

        try {
            // 优先使用Builder模式
            return convertWithBuilder(entity, dtoClass);
        } catch (Exception e) {
            log.warn("Builder转换失败，使用ModelMapper: {}", e.getMessage());
            // 降级到ModelMapper
            return modelMapper.map(entity, dtoClass);
        }
    }

    /**
     * 通用DTO到Entity转换
     *
     * @param dto 源DTO对象
     * @param entityClass 目标实体类
     * @param <D> DTO类型
     * @param <E> 实体类型
     * @return 转换后的实体对象
     */
    public <D, E> E toEntity(D dto, Class<E> entityClass) {
        if (dto == null) return null;

        try {
            return modelMapper.map(dto, entityClass);
        } catch (Exception e) {
            log.error("DTO转Entity失败: {}", e.getMessage());
            throw new ConversionException("转换失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量Entity到DTO转换
     *
     * @param entities 实体列表
     * @param dtoClass 目标DTO类
     * @param <E> 实体类型
     * @param <D> DTO类型
     * @return DTO列表
     */
    public <E, D> List<D> toDTOList(List<E> entities, Class<D> dtoClass) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        return entities.stream()
                .map(entity -> toDTO(entity, dtoClass))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 批量DTO到Entity转换
     *
     * @param dtos DTO列表
     * @param entityClass 目标实体类
     * @param <D> DTO类型
     * @param <E> 实体类型
     * @return 实体列表
     */
    public <D, E> List<E> toEntityList(List<D> dtos, Class<E> entityClass) {
        if (dtos == null || dtos.isEmpty()) {
            return new ArrayList<>();
        }

        return dtos.stream()
                .map(dto -> toEntity(dto, entityClass))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 智能更新现有Entity（只更新非null字段）
     *
     * @param existingEntity 现有实体对象
     * @param updateDTO 更新DTO对象
     * @param <D> DTO类型
     * @param <E> 实体类型
     * @return 更新后的实体对象
     */
    public <D, E> E updateEntity(E existingEntity, D updateDTO) {
        if (existingEntity == null || updateDTO == null) {
            return existingEntity;
        }

        modelMapper.map(updateDTO, existingEntity);
        return existingEntity;
    }

    /**
     * Builder模式转换实现
     * 优先使用Builder模式进行转换，保持类型安全
     *
     * @param entity 源实体对象
     * @param dtoClass 目标DTO类
     * @param <E> 实体类型
     * @param <D> DTO类型
     * @return 转换后的DTO对象
     */
    @SuppressWarnings("unchecked")
    private <E, D> D convertWithBuilder(E entity, Class<D> dtoClass) {
        try {
            // 获取builder()方法
            Method builderMethod = dtoClass.getMethod("builder");
            Object builder = builderMethod.invoke(null);

            // 获取entity的所有getter方法
            Method[] entityMethods = entity.getClass().getMethods();

            for (Method entityMethod : entityMethods) {
                if (entityMethod.getName().startsWith("get") &&
                    entityMethod.getParameterCount() == 0) {

                    String fieldName = entityMethod.getName().substring(3);
                    fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);

                    Object value = entityMethod.invoke(entity);
                    if (value != null) {
                        try {
                            Method setter = builder.getClass()
                                .getMethod(fieldName, value.getClass());
                            setter.invoke(builder, value);
                        } catch (NoSuchMethodException e) {
                            // 忽略不匹配的字段
                            log.debug("字段{}不存在于Builder中，跳过", fieldName);
                        }
                    }
                }
            }

            // 调用build()方法
            Method buildMethod = builder.getClass().getMethod("build");
            return (D) buildMethod.invoke(builder);

        } catch (Exception e) {
            throw new RuntimeException("Builder转换失败", e);
        }
    }

    /**
     * 安全转换，提供默认值
     *
     * @param entity 源实体对象
     * @param dtoClass 目标DTO类
     * @param defaultValue 默认值
     * @param <E> 实体类型
     * @param <D> DTO类型
     * @return 转换后的DTO对象或默认值
     */
    public <E, D> D safeToDTO(E entity, Class<D> dtoClass, D defaultValue) {
        try {
            D result = toDTO(entity, dtoClass);
            return result != null ? result : defaultValue;
        } catch (Exception e) {
            log.error("安全转换失败，返回默认值: {}", e.getMessage());
            return defaultValue;
        }
    }
}