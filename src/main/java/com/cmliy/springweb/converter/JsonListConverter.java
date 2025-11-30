package com.cmliy.springweb.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * 📋 JSON列表转换器
 *
 * 负责将List<String>与数据库JSONB字段之间的转换
 * 主要用于存储商品标签、图片URL列表等数组数据

 */
@Component
@Converter
public class JsonListConverter implements AttributeConverter<List<String>, String> {

    /**
     * 📄 JSON列表映射器
     * 配置Jackson进行JSON列表的序列化和反序列化
     */
    private final ObjectMapper objectMapper;

    /**
     * 🏗️ 构造函数
     * 初始化ObjectMapper用于JSON处理
     */
    public JsonListConverter() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 📤 将List转换为JSON字符串（存入数据库）
     *
     * @param attribute 需要转换的String列表
     * @return JSON字符串，空List返回"[]"
     * @throws RuntimeException 当JSON序列化失败时
     */
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "[]"; // 空列表存储为空JSON数组
        }

        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("List JSON转换失败", e);
        }
    }

    /**
     * 📥 将JSON字符串转换为List（从数据库读取）
     *
     * @param dbData 数据库中的JSON字符串
     * @return String列表，空字符串返回空List
     * @throws RuntimeException 当JSON反序列化失败时
     */
    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return List.of(); // 空字符串返回空List
        }

        try {
            return objectMapper.readValue(dbData, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            throw new RuntimeException("List JSON解析失败", e);
        }
    }
}