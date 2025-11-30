package com.cmliy.springweb.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 🔄 JSON数据转换器
 *
 * 负责将Map<String, Object>与数据库JSONB字段之间的转换
 * 支持PostgreSQL的JSONB类型存储和检索

 */
@Component
@Converter(autoApply = true)
public class JsonConverter implements AttributeConverter<Map<String, Object>, String> {

    /**
     * 📄 JSON对象映射器
     * 配置Jackson进行JSON序列化和反序列化
     */
    private final ObjectMapper objectMapper;

    /**
     * 🏗️ 构造函数
     * 初始化ObjectMapper并注册模块
     */
    public JsonConverter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules(); // 支持Java 8时间等新特性
    }

    /**
     * 📤 将Map转换为JSON字符串（存入数据库）
     *
     * @param attribute 需要转换的Map对象
     * @return JSON字符串，空Map返回"{}"
     * @throws RuntimeException 当JSON序列化失败时
     */
    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "{}"; // 空对象存储为空JSON对象
        }

        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON转换失败", e);
        }
    }

    /**
     * 📥 将JSON字符串转换为Map（从数据库读取）
     *
     * @param dbData 数据库中的JSON字符串
     * @return Map对象，空字符串返回空Map
     * @throws RuntimeException 当JSON反序列化失败时
     */
    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return new HashMap<>(); // 空字符串返回空Map
        }

        try {
            return objectMapper.readValue(dbData, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new RuntimeException("JSON解析失败", e);
        }
    }
}