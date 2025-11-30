package com.cmliy.springweb.service;

import com.cmliy.springweb.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 🛠️ 商品数据服务 - Product Data Service
 *
 * 专门处理Product实体的动态数据操作，分离业务逻辑与实体模型
 * 避免Hibernate脏检查机制的复杂性问题，提供安全的数据更新方式
 *
 * @author Claude
 * @since 2025-11-30
 */
@Slf4j
@Service
public class ProductDataService {

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
        log.info("🔧 [ProductDataService] 开始更新商品图片数据: productId={}, mainImageUrl={}",
                product.getId(), mainImageUrl);

        // 获取当前的productData，如果为null则初始化
        Map<String, Object> currentData = product.getProductData();
        if (currentData == null) {
            log.info("🔧 [ProductDataService] productData为null，初始化新的Map");
            currentData = new HashMap<>();
            product.setProductData(currentData);
        }

        // 安全地更新image_data结构
        @SuppressWarnings("unchecked")
        Map<String, Object> imageData = (Map<String, Object>)
                currentData.computeIfAbsent("image_data", k -> {
                    log.info("🔧 [ProductDataService] 创建新的image_data结构");
                    return new HashMap<>();
                });

        String oldMainImage = (String) imageData.get("main_image");
        imageData.put("main_image", mainImageUrl);

        log.info("🔧 [ProductDataService] 图片数据更新完成: productId={}, oldMainImage={}, newMainImage={}",
                product.getId(), oldMainImage, mainImageUrl);
    }

    /**
     * 📋 安全更新商品规格数据
     *
     * 以可控的方式更新规格信息，避免直接操作嵌套结构
     *
     * @param product 商品实体
     * @param specifications 规格数据Map
     */
    public void updateSpecifications(Product product, Map<String, Object> specifications) {
        log.info("🔧 [ProductDataService] 开始更新商品规格数据: productId={}, specifications={}",
                product.getId(), specifications);

        if (specifications == null || specifications.isEmpty()) {
            log.info("🔧 [ProductDataService] 规格数据为空，跳过更新");
            return;
        }

        // 获取当前的productData，如果为null则初始化
        Map<String, Object> currentData = product.getProductData();
        if (currentData == null) {
            log.info("🔧 [ProductDataService] productData为null，初始化新的Map");
            currentData = new HashMap<>();
            product.setProductData(currentData);
        }

        // 备份旧的规格数据用于日志
        @SuppressWarnings("unchecked")
        Map<String, Object> oldSpecifications = (Map<String, Object>) currentData.get("specifications");

        // 更新规格数据
        currentData.put("specifications", new HashMap<>(specifications));

        log.info("🔧 [ProductDataService] 规格数据更新完成: productId={}, oldSpecifications={}, newSpecifications={}",
                product.getId(), oldSpecifications, specifications);
    }

    /**
     * 🖼️ 设置商品图片URL列表
     *
     * 批量设置商品的图片URL，用于商品相册功能
     *
     * @param product 商品实体
     * @param imageUrls 图片URL列表
     */
    public void updateImageUrls(Product product, List<String> imageUrls) {
        log.info("🔧 [ProductDataService] 开始更新商品图片URL列表: productId={}, imageUrlsCount={}",
                product.getId(), imageUrls != null ? imageUrls.size() : 0);

        if (imageUrls == null || imageUrls.isEmpty()) {
            log.info("🔧 [ProductDataService] 图片URL列表为空，跳过更新");
            return;
        }

        // 获取当前的productData，如果为null则初始化
        Map<String, Object> currentData = product.getProductData();
        if (currentData == null) {
            log.info("🔧 [ProductDataService] productData为null，初始化新的Map");
            currentData = new HashMap<>();
            product.setProductData(currentData);
        }

        // 安全地更新image_data结构
        @SuppressWarnings("unchecked")
        Map<String, Object> imageData = (Map<String, Object>)
                currentData.computeIfAbsent("image_data", k -> {
                    log.info("🔧 [ProductDataService] 创建新的image_data结构");
                    return new HashMap<>();
                });

        List<String> oldImageUrls = (List<String>) imageData.get("gallery");
        imageData.put("gallery", imageUrls);
        imageData.put("total_images", imageUrls.size());

        log.info("🔧 [ProductDataService] 图片URL列表更新完成: productId={}, oldCount={}, newCount={}",
                product.getId(), oldImageUrls != null ? oldImageUrls.size() : 0, imageUrls.size());
    }

    /**
     * 🏷️ 更新商品分类信息
     *
     * @param product 商品实体
     * @param category 商品分类
     */
    public void updateCategory(Product product, String category) {
        log.info("🔧 [ProductDataService] 开始更新商品分类: productId={}, category={}",
                product.getId(), category);

        if (category == null || category.trim().isEmpty()) {
            log.info("🔧 [ProductDataService] 分类为空，跳过更新");
            return;
        }

        // 获取当前的productData，如果为null则初始化
        Map<String, Object> currentData = product.getProductData();
        if (currentData == null) {
            log.info("🔧 [ProductDataService] productData为null，初始化新的Map");
            currentData = new HashMap<>();
            product.setProductData(currentData);
        }

        // 更新分类信息到规格中
        @SuppressWarnings("unchecked")
        Map<String, Object> specifications = (Map<String, Object>)
                currentData.computeIfAbsent("specifications", k -> {
                    log.info("🔧 [ProductDataService] 创建新的specifications结构");
                    return new HashMap<>();
                });

        String oldCategory = (String) specifications.get("category");
        specifications.put("category", category.trim());

        log.info("🔧 [ProductDataService] 分类更新完成: productId={}, oldCategory={}, newCategory={}",
                product.getId(), oldCategory, category.trim());
    }

    /**
     * 🏷️ 更新商品品牌信息
     *
     * @param product 商品实体
     * @param brand 商品品牌
     */
    public void updateBrand(Product product, String brand) {
        log.info("🔧 [ProductDataService] 开始更新商品品牌: productId={}, brand={}",
                product.getId(), brand);

        if (brand == null || brand.trim().isEmpty()) {
            log.info("🔧 [ProductDataService] 品牌为空，跳过更新");
            return;
        }

        // 获取当前的productData，如果为null则初始化
        Map<String, Object> currentData = product.getProductData();
        if (currentData == null) {
            log.info("🔧 [ProductDataService] productData为null，初始化新的Map");
            currentData = new HashMap<>();
            product.setProductData(currentData);
        }

        // 更新品牌信息到规格中
        @SuppressWarnings("unchecked")
        Map<String, Object> specifications = (Map<String, Object>)
                currentData.computeIfAbsent("specifications", k -> {
                    log.info("🔧 [ProductDataService] 创建新的specifications结构");
                    return new HashMap<>();
                });

        String oldBrand = (String) specifications.get("brand");
        specifications.put("brand", brand.trim());

        log.info("🔧 [ProductDataService] 品牌更新完成: productId={}, oldBrand={}, newBrand={}",
                product.getId(), oldBrand, brand.trim());
    }

    /**
     * 🎨 更新商品颜色信息
     *
     * @param product 商品实体
     * @param color 商品颜色
     */
    public void updateColor(Product product, String color) {
        log.info("🔧 [ProductDataService] 开始更新商品颜色: productId={}, color={}",
                product.getId(), color);

        if (color == null || color.trim().isEmpty()) {
            log.info("🔧 [ProductDataService] 颜色为空，跳过更新");
            return;
        }

        // 获取当前的productData，如果为null则初始化
        Map<String, Object> currentData = product.getProductData();
        if (currentData == null) {
            log.info("🔧 [ProductDataService] productData为null，初始化新的Map");
            currentData = new HashMap<>();
            product.setProductData(currentData);
        }

        // 更新颜色信息到规格中
        @SuppressWarnings("unchecked")
        Map<String, Object> specifications = (Map<String, Object>)
                currentData.computeIfAbsent("specifications", k -> {
                    log.info("🔧 [ProductDataService] 创建新的specifications结构");
                    return new HashMap<>();
                });

        String oldColor = (String) specifications.get("color");
        specifications.put("color", color.trim());

        log.info("🔧 [ProductDataService] 颜色更新完成: productId={}, oldColor={}, newColor={}",
                product.getId(), oldColor, color.trim());
    }

    /**
     * 📏 更新商品尺寸信息
     *
     * @param product 商品实体
     * @param size 商品尺寸
     */
    public void updateSize(Product product, String size) {
        log.info("🔧 [ProductDataService] 开始更新商品尺寸: productId={}, size={}",
                product.getId(), size);

        if (size == null || size.trim().isEmpty()) {
            log.info("🔧 [ProductDataService] 尺寸为空，跳过更新");
            return;
        }

        // 获取当前的productData，如果为null则初始化
        Map<String, Object> currentData = product.getProductData();
        if (currentData == null) {
            log.info("🔧 [ProductDataService] productData为null，初始化新的Map");
            currentData = new HashMap<>();
            product.setProductData(currentData);
        }

        // 更新尺寸信息到规格中
        @SuppressWarnings("unchecked")
        Map<String, Object> specifications = (Map<String, Object>)
                currentData.computeIfAbsent("specifications", k -> {
                    log.info("🔧 [ProductDataService] 创建新的specifications结构");
                    return new HashMap<>();
                });

        String oldSize = (String) specifications.get("size");
        specifications.put("size", size.trim());

        log.info("🔧 [ProductDataService] 尺寸更新完成: productId={}, oldSize={}, newSize={}",
                product.getId(), oldSize, size.trim());
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