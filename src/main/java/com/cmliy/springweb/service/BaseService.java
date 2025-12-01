package com.cmliy.springweb.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 🔧 基础服务类 - Base Service
 *
 * 为所有Service提供通用的业务逻辑工具方法，包括：
 * - 统一的日志记录模式
 * - 通用的数据验证方法
 * - 标准化的CRUD操作模板
 * - 事务管理工具
 *
 * 设计原则：
 * 1. 只包含通用逻辑，不引入业务耦合
 * 2. 保持方法的简洁性和可读性
 * 3. 提供灵活的工具方法，不强制使用
 * 4. 保持异常类型的兼容性

 */
@Slf4j
@RequiredArgsConstructor
public abstract class BaseService {

    /**
     * 📝 记录操作开始日志
     *
     * @param operation 操作名称
     * @param params 操作参数
     */
    protected void logOperationStart(String operation, Object... params) {
        if (params.length > 0) {
            log.info("开始{}: params={}", operation, Arrays.toString(params));
        } else {
            log.info("开始{}", operation);
        }
    }

    /**
     * ✅ 记录操作成功日志
     *
     * @param operation 操作名称
     * @param result 操作结果
     */
    protected void logOperationSuccess(String operation, Object result) {
        log.info("{}成功: result={}", operation, result);
    }

    /**
     * ❌ 记录操作失败日志
     *
     * @param operation 操作名称
     * @param reason 失败原因
     */
    protected void logOperationFailed(String operation, String reason) {
        log.error("{}失败: reason={}", operation, reason);
    }

    /**
     * 🔍 验证实体存在性
     *
     * @param optional 实体Optional
     * @param entityName 实体名称
     * @param id 实体ID
     * @return 实体对象
     * @throws RuntimeException 如果实体不存在
     */
    protected <T> T validateExists(Optional<T> optional, String entityName, Object id) {
        return optional.orElseThrow(() ->
            new RuntimeException(entityName + "不存在: " + id));
    }

    /**
     * 🔍 验证实体存在性（带自定义异常消息）
     *
     * @param optional 实体Optional
     * @param errorMessage 自定义错误消息
     * @return 实体对象
     * @throws RuntimeException 如果实体不存在
     */
    protected <T> T validateExists(Optional<T> optional, String errorMessage) {
        return optional.orElseThrow(() -> new RuntimeException(errorMessage));
    }

    /**
     * ✔️ 验证唯一性（不存在重复）
     *
     * @param exists 是否已存在
     * @param entityName 实体名称
     * @param value 要检查的值
     * @throws RuntimeException 如果已存在重复
     */
    protected void validateUnique(Boolean exists, String entityName, Object value) {
        if (exists) {
            throw new RuntimeException(entityName + "已存在: " + value);
        }
    }

    /**
     * ➕ 验证正数（大于0）
     *
     * @param value 数值
     * @param fieldName 字段名称
     * @throws RuntimeException 如果数值为null或小于等于0
     */
    protected void validatePositive(Number value, String fieldName) {
        if (value == null || value.doubleValue() <= 0) {
            throw new RuntimeException(fieldName + "必须大于0");
        }
    }

    /**
     * ➕ 验证非负数（大于等于0）
     *
     * @param value 数值
     * @param fieldName 字段名称
     * @throws RuntimeException 如果数值为null或小于0
     */
    protected void validateNonNegative(Number value, String fieldName) {
        if (value == null || value.doubleValue() < 0) {
            throw new RuntimeException(fieldName + "不能为负数");
        }
    }

    /**
     * 🎯 执行带日志的操作
     *
     * 提供统一的日志记录模板，包括：
     * - 操作开始日志
     * - 操作成功日志
     * - 操作失败日志和异常处理
     *
     * @param operation 操作名称
     * @param supplier 操作执行器
     * @param params 操作参数
     * @return 操作结果
     */
    protected <T> T executeWithLog(String operation, Supplier<T> supplier, Object... params) {
        logOperationStart(operation, params);
        try {
            T result = supplier.get();
            logOperationSuccess(operation, result);
            return result;
        } catch (Exception e) {
            logOperationFailed(operation, e.getMessage());
            throw e;
        }
    }

    /**
     * 🎯 执行带日志的操作（支持IOException）
     *
     * 专门用于处理可能抛出IOException的操作，如文件操作。
     * 将检查异常转换为运行时异常，便于lambda表达式使用。
     *
     * @param operation 操作名称
     * @param supplier 操作执行器（可能抛出IOException）
     * @param params 操作参数
     * @return 操作结果
     */
    protected <T> T executeWithLogAndIO(String operation, IOSupplier<T> supplier, Object... params) {
        logOperationStart(operation, params);
        try {
            T result = supplier.get();
            logOperationSuccess(operation, result);
            return result;
        } catch (IOException e) {
            logOperationFailed(operation, e.getMessage());
            throw new RuntimeException("IO操作失败: " + e.getMessage(), e);
        } catch (Exception e) {
            logOperationFailed(operation, e.getMessage());
            throw e;
        }
    }

    /**
     * 🎯 执行带日志的操作（支持IOException，无返回值）
     *
     * 专门用于处理可能抛出IOException的操作，如文件操作。
     *
     * @param operation 操作名称
     * @param runnable 操作执行器（可能抛出IOException）
     * @param params 操作参数
     */
    protected void executeWithLogAndIO(String operation, IORunnable runnable, Object... params) {
        logOperationStart(operation, params);
        try {
            runnable.run();
            logOperationSuccess(operation, "完成");
        } catch (IOException e) {
            logOperationFailed(operation, e.getMessage());
            throw new RuntimeException("IO操作失败: " + e.getMessage(), e);
        } catch (Exception e) {
            logOperationFailed(operation, e.getMessage());
            throw e;
        }
    }

    /**
     * 🎯 执行带日志的操作（无返回值）
     *
     * @param operation 操作名称
     * @param runnable 操作执行器
     * @param params 操作参数
     */
    protected void executeWithLog(String operation, Runnable runnable, Object... params) {
        logOperationStart(operation, params);
        try {
            runnable.run();
            logOperationSuccess(operation, "完成");
        } catch (Exception e) {
            logOperationFailed(operation, e.getMessage());
            throw e;
        }
    }

    /**
     * 🔄 在事务中执行操作（带日志）
     *
     * 注意：此方法本身不管理事务，需要配合@Transactional注解使用
     * 主要用于统一事务操作的日志格式
     *
     * @param operation 操作名称
     * @param supplier 操作执行器
     * @return 操作结果
     */
    protected <T> T executeInTransaction(String operation, Supplier<T> supplier) {
        return executeWithLog(operation, supplier);
    }

    /**
     * 🔄 在事务中执行操作（带日志，无返回值）
     *
     * @param operation 操作名称
     * @param runnable 操作执行器
     */
    protected void executeInTransaction(String operation, Runnable runnable) {
        executeWithLog(operation, runnable);
    }

    /**
     * 📊 构建分页请求
     *
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDirection 排序方向（ASC/DESC）
     * @return Pageable对象
     */
    protected Pageable buildPageable(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) ?
            Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }

    // ==================== 📋 函数式接口 ====================

    /**
     * 🔄 支持IOException的Supplier接口
     *
     * @param <T> 返回值类型
     */
    @FunctionalInterface
    public interface IOSupplier<T> {
        T get() throws IOException;
    }

    /**
     * 🔄 支持IOException的Runnable接口
     */
    @FunctionalInterface
    public interface IORunnable {
        void run() throws IOException;
    }
}