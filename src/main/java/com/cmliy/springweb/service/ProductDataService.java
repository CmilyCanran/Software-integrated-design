package com.cmliy.springweb.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.cmliy.springweb.model.Product;

import lombok.extern.slf4j.Slf4j;

/**
 * 🛠️ 商品数据服务 - Product Data Service
 *
 * 专门处理Product实体的动态数据操作，分离业务逻辑与实体模型
 * 避免Hibernate脏检查机制的复杂性问题，提供安全的数据更新方式
 *

 */
@Slf4j
@Service
public class ProductDataService extends BaseService {

    /**
     * 🖼️ 安全更新商品图片数据
     *
     * 以不触发Hibernate脏检查的方式更新图片相关数据
     * 确保productData字段的修改是可控和可预测的
     *
     * @param product 商品实体
     * @param mainImageUrl 主图片URL
     */
    public void updateProductImageData(Product product, String mainImageUrl) {
        executeWithLog("更新商品图片数据", () -> {
            // 获取当前的productData，如果为null则初始化
            Map<String, Object> currentData = product.getProductData();
            if (currentData == null) {
                currentData = new HashMap<>();
                product.setProductData(currentData);
            }

            // 安全地更新image_data结构
            @SuppressWarnings("unchecked")
            Map<String, Object> imageData = (Map<String, Object>)
                    currentData.computeIfAbsent("image_data", k -> new HashMap<>());

            String oldMainImage = (String) imageData.get("main_image");
            imageData.put("main_image", mainImageUrl);

            return null; // void方法返回null
        }, product.getId(), mainImageUrl);
    }

    /**
     * 🔧 修复方法：安全的商品规格数据更新
     */
    public void updateSpecifications(Product product, Map<String, Object> specifications) {
        executeWithLog("更新商品规格数据", () -> {
            if (specifications == null || specifications.isEmpty()) {
                log.info("规格数据为空，跳过更新");
                return null;
            }

            // 🔧 关键修复：验证和转换规格数据
            Map<String, List<String>> validatedSpecifications = new HashMap<>();

            for (Map.Entry<String, Object> entry : specifications.entrySet()) {
                String specName = entry.getKey();
                Object specValues = entry.getValue();

                // 验证规格名称
                if (specName == null || specName.trim().isEmpty()) {
                    log.warn("跳过空的规格名称");
                    continue;
                }

                // 转换规格值为字符串列表
                List<String> stringValues = convertToStringList(specValues);
                if (stringValues == null || stringValues.isEmpty()) {
                    log.warn("跳过空的规格值: specName={}", specName);
                    continue;
                }

                validatedSpecifications.put(specName.trim(), stringValues);
            }

            if (validatedSpecifications.isEmpty()) {
                log.warn("没有有效的规格数据，跳过更新");
                return null;
            }

            // 🔧 关键修复：安全地更新JSONB数据
            Map<String, Object> currentData = product.getProductData();
            if (currentData == null) {
                currentData = new HashMap<>();
            }

            // 备份旧数据
            @SuppressWarnings("unchecked")
            Map<String, Object> oldSpecifications = (Map<String, Object>) currentData.get("specifications");

            // 更新规格数据
            currentData.put("specifications", new HashMap<>(validatedSpecifications));

            // 🔧 关键修复：使用setter方法确保JSONB正确处理
            product.setProductData(currentData);

            return null; // void方法返回null
        }, product.getId(), specifications);
    }

    /**
     * 🔧 新增方法：将任意类型转换为字符串列表
     */
    private List<String> convertToStringList(Object values) {
        if (values == null) {
            return null;
        }

        // 处理List类型
        if (values instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> objectList = (List<Object>) values;
            List<String> stringList = new ArrayList<>();

            for (Object item : objectList) {
                if (item != null) {
                    String strValue = item.toString().trim();
                    if (!strValue.isEmpty()) {
                        stringList.add(strValue);
                    }
                }
            }

            return stringList.isEmpty() ? null : stringList;
        }

        // 处理数组类型
        if (values.getClass().isArray()) {
            Object[] array = (Object[]) values;
            List<String> stringList = new ArrayList<>();

            for (Object item : array) {
                if (item != null) {
                    String strValue = item.toString().trim();
                    if (!strValue.isEmpty()) {
                        stringList.add(strValue);
                    }
                }
            }

            return stringList.isEmpty() ? null : stringList;
        }

        // 处理单个值（数字、字符串等）
        String strValue = values.toString().trim();
        return strValue.isEmpty() ? null : List.of(strValue);
    }

    
    
    
    /**
     * 🗑️ 清除商品规格数据
     *
     * @param product 商品实体
     */
    public void clearSpecifications(Product product) {
        log.info("🔧 [ProductDataService] 开始清除商品规格数据: productId={}", product.getId());

        // 获取当前的productData
        Map<String, Object> currentData = product.getProductData();
        if (currentData != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> oldSpecifications = (Map<String, Object>) currentData.get("specifications");
            currentData.remove("specifications");
            log.info("🔧 [ProductDataService] 规格数据已清除: productId={}, oldSpecifications={}",
                    product.getId(), oldSpecifications);
        } else {
            log.info("🔧 [ProductDataService] productData为null，无需清除规格");
        }
    }

    /**
     * 🔍 获取商品规格数据的安全副本
     *
     * @param product 商品实体
     * @return 规格数据的不可变副本
     */
    public Map<String, Object> getSpecificationsCopy(Product product) {
        Map<String, Object> currentData = product.getProductData();
        if (currentData == null) {
            return Map.of();
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> specifications = (Map<String, Object>) currentData.get("specifications");
        return specifications != null ? Map.copyOf(specifications) : Map.of();
    }

    /**
     * 🔍 获取商品图片数据的安全副本
     *
     * @param product 商品实体
     * @return 图片数据的不可变副本
     */
    public Map<String, Object> getImageDataCopy(Product product) {
        Map<String, Object> currentData = product.getProductData();
        if (currentData == null) {
            return Map.of();
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> imageData = (Map<String, Object>) currentData.get("image_data");
        return imageData != null ? Map.copyOf(imageData) : Map.of();
    }
}