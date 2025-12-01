package com.cmliy.springweb.converter;

import com.cmliy.springweb.util.DtoConverterUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 🔄 基础转换器类（泛型版本）
 *
 * 提供所有转换器的通用功能，包括：
 * - 空值安全检查
 * - 批量转换操作
 * - 通用转换模板方法
 * - 统一DTO转换工具集成
 *
 * 使用继承此基类来消除转换器间的代码重复
 *
 * @param <E> 实体类型
 * @param <D> DTO类型
 * @author Claude
 */
@Slf4j
public abstract class BaseConverter<E, D> {

    @Autowired
    protected DtoConverterUtils dtoConverter;

    /**
     * 🔒 安全的空值检查
     *
     * 统一的空值检查逻辑，避免NullPointerException
     *
     * @param object 需要检查的对象
     * @param objectName 对象名称（用于日志）
     * @return true如果对象为null，false否则
     */
    protected boolean isNull(Object object, String objectName) {
        if (object == null) {
            log.debug("{} is null, returning null", objectName);
            return true;
        }
        return false;
    }

    /**
     * 🔒 安全的空值检查（简化版本）
     *
     * @param object 需要检查的对象
     * @return true如果对象为null，false否则
     */
    protected boolean isNull(Object object) {
        return object == null;
    }

    /**
     * 🔄 通用的单个对象转换方法
     *
     * 提供模板方法，确保空值安全和异常处理
     *
     * @param <S> 源类型
     * @param <T> 目标类型
     * @param source 源对象
     * @param converter 转换函数
     * @param entityName 实体名称（用于日志）
     * @return 转换后的目标对象，如果源对象为null则返回null
     */
    protected <S, T> T safeConvert(S source, Function<S, T> converter, String entityName) {
        if (isNull(source, entityName)) {
            return null;
        }

        try {
            return converter.apply(source);
        } catch (Exception e) {
            log.error("转换{}时发生错误: {}", entityName, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 🔄 通用的单个对象转换方法（简化版本）
     *
     * @param <S> 源类型
     * @param <T> 目标类型
     * @param source 源对象
     * @param converter 转换函数
     * @return 转换后的目标对象，如果源对象为null则返回null
     */
    protected <S, T> T safeConvert(S source, Function<S, T> converter) {
        if (isNull(source)) {
            return null;
        }

        try {
            return converter.apply(source);
        } catch (Exception e) {
            log.error("转换对象时发生错误: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 📋 安全的批量转换方法
     *
     * 提供统一的列表转换逻辑，包含空值检查和异常处理
     *
     * @param <S> 源类型
     * @param <T> 目标类型
     * @param sourceList 源对象列表
     * @param converter 单个对象转换函数
     * @param listName 列表名称（用于日志）
     * @return 转换后的目标对象列表，如果源列表为null或空则返回空列表
     */
    protected <S, T> List<T> safeConvertList(List<S> sourceList, Function<S, T> converter, String listName) {
        if (sourceList == null || sourceList.isEmpty()) {
            log.debug("{} is null or empty, returning empty list", listName);
            return Collections.emptyList();
        }

        try {
            return sourceList.stream()
                    .map(converter)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("批量转换{}时发生错误: {}", listName, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * 📋 安全的批量转换方法（简化版本）
     *
     * @param <S> 源类型
     * @param <T> 目标类型
     * @param sourceList 源对象列表
     * @param converter 单个对象转换函数
     * @return 转换后的目标对象列表，如果源列表为null或空则返回空列表
     */
    protected <S, T> List<T> safeConvertList(List<S> sourceList, Function<S, T> converter) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            return sourceList.stream()
                    .map(converter)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("批量转换列表时发生错误: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * 🔧 安全的字段设置方法
     *
     * 用于安全地设置目标对象的字段，包含空值检查
     *
     * @param <T> 目标对象类型
     * @param target 目标对象
     * @param setter 设置函数
     * @param value 要设置的值
     * @param fieldName 字段名称（用于日志）
     */
    protected <T> void safeSetField(T target, java.util.function.BiConsumer<T, Object> setter, Object value, String fieldName) {
        if (target == null) {
            log.debug("Target object is null, cannot set field: {}", fieldName);
            return;
        }

        if (value != null) {
            try {
                setter.accept(target, value);
            } catch (Exception e) {
                log.error("设置字段{}时发生错误: {}", fieldName, e.getMessage(), e);
            }
        }
    }

    /**
     * 🔧 安全的字段设置方法（简化版本）
     *
     * @param <T> 目标对象类型
     * @param <V> 值类型
     * @param target 目标对象
     * @param setter 设置函数
     * @param value 要设置的值
     */
    protected <T, V> void safeSetField(T target, java.util.function.BiConsumer<T, V> setter, V value) {
        if (target != null && value != null) {
            try {
                setter.accept(target, value);
            } catch (Exception e) {
                log.error("设置字段时发生错误: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 📊 转换统计信息
     *
     * 用于记录转换操作的统计信息
     *
     * @param operation 操作名称
     * @param sourceCount 源对象数量
     * @param successCount 成功转换数量
     * @param targetName 目标对象名称
     */
    protected void logConversionStats(String operation, int sourceCount, int successCount, String targetName) {
        if (sourceCount != successCount) {
            log.warn("{}: {}/{} {}成功转换", operation, successCount, sourceCount, targetName);
        } else {
            log.debug("{}: {} {}成功转换", operation, successCount, targetName);
        }
    }

    /**
     * 🔄 标准Entity到DTO转换
     * 使用DtoConverterUtils进行通用转换
     *
     * @param entity 实体对象
     * @return DTO对象
     */
    public abstract D toDTO(E entity);

    /**
     * 🔄 标准DTO到Entity转换
     * 使用DtoConverterUtils进行通用转换
     *
     * @param dto DTO对象
     * @return 实体对象
     */
    public abstract E toEntity(D dto);

    /**
     * 📋 批量Entity到DTO转换
     * 使用DtoConverterUtils进行批量转换
     *
     * @param entities 实体列表
     * @return DTO列表
     */
    public List<D> toDTOList(List<E> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return dtoConverter.toDTOList(entities, getDTOClass());
    }

    /**
     * 📋 批量DTO到Entity转换
     * 使用DtoConverterUtils进行批量转换
     *
     * @param dtos DTO列表
     * @return 实体列表
     */
    public List<E> toEntityList(List<D> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }
        return dtoConverter.toEntityList(dtos, getEntityClass());
    }

    /**
     * 🔄 智能更新现有实体
     * 只更新非null字段
     *
     * @param existingEntity 现有实体
     * @param updateDTO 更新DTO
     * @return 更新后的实体
     */
    public E updateEntity(E existingEntity, D updateDTO) {
        return dtoConverter.updateEntity(existingEntity, updateDTO);
    }

    /**
     * 🔧 获取DTO类型（用于通用转换）
     *
     * @return DTO类
     */
    protected abstract Class<D> getDTOClass();

    /**
     * 🔧 获取实体类型（用于通用转换）
     *
     * @return 实体类
     */
    protected abstract Class<E> getEntityClass();

    /**
     * 🔄 安全转换包装器
     * 提供异常处理和默认值
     *
     * @param converter 转换函数
     * @param defaultValue 默认值
     * @param <T> 返回类型
     * @return 转换结果或默认值
     */
    protected <T> T safeConvert(Supplier<T> converter, T defaultValue) {
        try {
            return converter.get();
        } catch (Exception e) {
            log.error("转换失败，返回默认值: {}", e.getMessage(), e);
            return defaultValue;
        }
    }

    /**
     * 🔄 安全Entity到DTO转换
     *
     * @param entity 实体对象
     * @return DTO对象或null
     */
    public D safeToDTO(E entity) {
        return safeConvert(() -> toDTO(entity), (D) null);
    }

    /**
     * 🔄 安全DTO到Entity转换
     *
     * @param dto DTO对象
     * @return 实体对象或null
     */
    public E safeToEntity(D dto) {
        return safeConvert(() -> toEntity(dto), (E) null);
    }
}