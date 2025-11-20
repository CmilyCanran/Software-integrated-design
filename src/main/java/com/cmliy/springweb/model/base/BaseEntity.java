package com.cmliy.springweb.model.base;

import com.cmliy.springweb.converter.JsonConverter;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 🏗️ 基础实体类
 *
 * 提供所有实体类的通用字段和方法，包括：
 * - 元数据存储（JSONB）
 * - 扩展属性存储（JSONB）
 * - 创建和更新时间戳
 *
 * 继承此类的实体将自动获得JSONB存储能力，
 * 无需在子类中重复定义通用的元数据和属性字段
 *
 * @author Claude
 * @since 2025-01-20
 */
@Data
@MappedSuperclass
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseEntity {

    /**
     * 📄 实体元数据（JSONB）
     *
     * 存储实体的扩展元数据信息，如：
     * - 创建版本、审核状态等业务元数据
     * - 系统生成的统计信息
     * - 临时状态标记等
     *
     * 使用PostgreSQL的JSONB类型存储，支持高效查询
     */
    @Convert(converter = JsonConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata = new HashMap<>();

    /**
     * 🔧 实体扩展属性（JSONB）
     *
     * 存储实体的自定义扩展属性，如：
     * - 业务相关的自定义属性
     * - 第三方集成数据
     * - 配置参数等
     *
     * 使用PostgreSQL的JSONB类型存储，支持动态扩展
     */
    @Convert(converter = JsonConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> attributes = new HashMap<>();

    /**
     * ⏰ 创建时间戳
     *
     * 记录实体首次创建的时间，由数据库自动管理
     * 格式：UTC时间戳
     */
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    /**
     * 🔄 更新时间戳
     *
     * 记录实体最后一次更新的时间，由数据库自动管理
     * 格式：UTC时间戳
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ==================== 📄 元数据操作方法 ====================

    /**
     * ➕ 添加元数据
     *
     * @param key 元数据键名
     * @param value 元数据值
     */
    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }

    /**
     * 📖 获取元数据
     *
     * @param key 元数据键名
     * @return 对应的元数据值，不存在时返回null
     */
    public Object getMetadata(String key) {
        return this.metadata.get(key);
    }

    /**
     * 🗑️ 移除元数据
     *
     * @param key 要移除的元数据键名
     * @return 被移除的值，不存在时返回null
     */
    public Object removeMetadata(String key) {
        return this.metadata.remove(key);
    }

    /**
     * 🧹 清空所有元数据
     */
    public void clearMetadata() {
        this.metadata.clear();
    }

    // ==================== 🔧 扩展属性操作方法 ====================

    /**
     * ➕ 添加扩展属性
     *
     * @param key 属性键名
     * @param value 属性值
     */
    public void addAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }

    /**
     * 📖 获取扩展属性
     *
     * @param key 属性键名
     * @return 对应的属性值，不存在时返回null
     */
    public Object getAttribute(String key) {
        return this.attributes.get(key);
    }

    /**
     * 🗑️ 移除扩展属性
     *
     * @param key 要移除的属性键名
     * @return 被移除的值，不存在时返回null
     */
    public Object removeAttribute(String key) {
        return this.attributes.remove(key);
    }

    /**
     * 🧹 清空所有扩展属性
     */
    public void clearAttributes() {
        this.attributes.clear();
    }

    // ==================== 📊 数据统计方法 ====================

    /**
     * 📈 获取元数据数量
     *
     * @return 元数据项的数量
     */
    public int getMetadataCount() {
        return this.metadata.size();
    }

    /**
     * 📈 获取扩展属性数量
     *
     * @return 扩展属性项的数量
     */
    public int getAttributesCount() {
        return this.attributes.size();
    }

    /**
     * 📋 检查是否为空实体
     *
     * @return 如果元数据和扩展属性都为空则返回true
     */
    public boolean isEmpty() {
        return this.metadata.isEmpty() && this.attributes.isEmpty();
    }

    /**
     * 📝 获取实体摘要信息
     *
     * @return 包含统计信息的字符串
     */
    public String getSummary() {
        return String.format(
            "Entity[metadata=%d, attributes=%d, created=%s, updated=%s]",
            getMetadataCount(),
            getAttributesCount(),
            createdAt,
            updatedAt
        );
    }
}