package com.cmliy.springweb.converter;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * 🔄 基础转换器类
 *
 * 提供所有转换器的通用功能，包括：
 * - 空值安全检查
 * - 批量转换操作
 * - 通用的转换模板方法
 *
 * 使用继承此基类来消除转换器间的代码重复

 */
@Slf4j
public abstract class BaseConverter {

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
}