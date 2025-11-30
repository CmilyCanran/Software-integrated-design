package com.cmliy.springweb.converter;

import com.cmliy.springweb.dto.ProductResponseDTO;
import com.cmliy.springweb.dto.ProductDetailDTO;
import com.cmliy.springweb.dto.ProductSummaryDTO;
import com.cmliy.springweb.dto.ProductListItemDTO;
import com.cmliy.springweb.dto.ProductCreateRequestDTO;
import com.cmliy.springweb.dto.ProductUpdateRequestDTO;
import com.cmliy.springweb.model.Product;
import com.cmliy.springweb.model.User;
import com.cmliy.springweb.service.ProductDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 🔄 商品转换器 - Product Converter
 *
 * 负责Product实体与各种DTO之间的转换
 * 统一管理商品数据的映射逻辑，确保数据一致性
 *
 * @author Claude
 * @since 2025-11-22
 */
@Slf4j
@Component
public class ProductConverter {

    @Autowired
    private ProductDataService productDataService;

    /**
     * 🔄 Product实体转ProductResponseDTO
     *
     * @param product 商品实体
     * @return ProductResponseDTO
     */
    public ProductResponseDTO toResponseDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setProductName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setSalesCount(product.getSalesCount());
        dto.setDiscount(product.getDiscount());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setIsAvailable(product.getIsAvailable());

        // 创建者信息
        if (product.getCreator() != null) {
            dto.setCreatorId(product.getCreator().getId());
            dto.setCreatorUsername(product.getCreator().getUsername());
        }

        // 图片信息
        dto.setMainImageUrl(product.getMainImage());
        dto.setImageUrls(product.getImageUrls());

        // 统一规格信息 - 只返回specifications
        dto.setSpecifications(product.getAllSpecifications());

        // 格式化价格
        dto.setFormattedPrice(product.getFormattedPrice());
        dto.setFormattedDiscountedPrice(product.getFormattedDiscountedPrice());
        dto.setStockStatus(product.getStockStatus());

        // 时间戳
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        return dto;
    }

    /**
     * 🔄 Product实体转ProductDetailDTO
     *
     * @param product 商品实体
     * @return ProductDetailDTO
     */
    public ProductDetailDTO toDetailDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setId(product.getId());
        dto.setProductName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setSalesCount(product.getSalesCount());
        dto.setDiscount(product.getDiscount());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setIsAvailable(product.getIsAvailable());

        // 创建者信息
        if (product.getCreator() != null) {
            dto.setCreatorId(product.getCreator().getId());
            dto.setCreatorUsername(product.getCreator().getUsername());
        }

        // 详细图片信息
        dto.setMainImageUrl(product.getMainImage());
        dto.setImageUrls(product.getImageUrls());
        dto.setThumbnails(product.getThumbnails());
        dto.setTotalImages(product.getTotalImages());

        // 统一规格信息 - 只返回specifications
        dto.setSpecifications(product.getAllSpecifications());

        // 价格信息
        dto.setFormattedPrice(product.getFormattedPrice());
        dto.setFormattedDiscountedPrice(product.getFormattedDiscountedPrice());
        dto.setDiscountDisplay(product.getDiscountDisplay());
        dto.setDiscountAmount(product.getDiscountAmount());
        dto.setStockStatus(product.getStockStatus());

        // 变体信息
        dto.setVariants(getVariantsFromProductData(product));

        // 时间戳
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        return dto;
    }

    /**
     * 🔄 Product实体转ProductSummaryDTO
     *
     * @param product 商品实体
     * @return ProductSummaryDTO
     */
    public ProductSummaryDTO toSummaryDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductSummaryDTO dto = new ProductSummaryDTO();
        dto.setId(product.getId());
        dto.setProductName(product.getProductName());
        dto.setPrice(product.getPrice());
        dto.setSalesCount(product.getSalesCount());
        dto.setDiscount(product.getDiscount());
        dto.setMainImageUrl(product.getMainImage());
        // 从specifications中动态获取分类和品牌信息（如果存在）
        Object category = product.getSpecification("分类");
        Object brand = product.getSpecification("品牌");
        if (category instanceof String) {
            dto.setCategory((String) category);
        }
        if (brand instanceof String) {
            dto.setBrand((String) brand);
        }
        dto.setFormattedPrice(product.getFormattedPrice());
        dto.setFormattedDiscountedPrice(product.getFormattedDiscountedPrice());
        dto.setStockStatus(product.getStockStatus());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setIsAvailable(product.getIsAvailable());

        return dto;
    }

    /**
     * 🔄 Product实体转ProductListItemDTO
     *
     * @param product 商品实体
     * @return ProductListItemDTO
     */
    public ProductListItemDTO toListItemDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductListItemDTO dto = new ProductListItemDTO();
        dto.setId(product.getId());
        dto.setProductName(product.getProductName());
        dto.setPrice(product.getPrice());
        dto.setDiscount(product.getDiscount());
        dto.setSalesCount(product.getSalesCount());
        dto.setMainImageUrl(product.getMainImage());
        dto.setCategory(product.getCategory());
        dto.setFormattedPrice(product.getFormattedPrice());
        dto.setFormattedDiscountedPrice(product.getFormattedDiscountedPrice());
        dto.setStockStatus(product.getStockStatus());
        dto.setIsAvailable(product.getIsAvailable());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setDescription(product.getDescription());

        return dto;
    }

    /**
     * 🔄 ProductCreateRequestDTO转Product实体
     *
     * @param requestDTO 创建请求DTO
     * @param creator 创建者用户
     * @return Product实体
     */
    public Product toEntity(ProductCreateRequestDTO requestDTO, User creator) {
        if (requestDTO == null) {
            return null;
        }

        Product product = new Product();
        product.setProductName(requestDTO.getProductName());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setStockQuantity(requestDTO.getStockQuantity());
        product.setIsAvailable(requestDTO.getIsAvailable() != null ? requestDTO.getIsAvailable() : false);
        product.setDiscount(requestDTO.getDiscount() != null ? requestDTO.getDiscount() : java.math.BigDecimal.ZERO);
        product.setCreator(creator);

        // 设置主图片
        if (requestDTO.getMainImageUrl() != null && !requestDTO.getMainImageUrl().trim().isEmpty()) {
            productDataService.updateProductImageData(product, requestDTO.getMainImageUrl());
        }

        // 设置图片列表
        if (requestDTO.getImageUrls() != null && !requestDTO.getImageUrls().isEmpty()) {
            productDataService.updateImageUrls(product, requestDTO.getImageUrls());
        }

        // 统一设置规格属性 - 所有属性都通过specifications处理
        if (requestDTO.getSpecifications() != null && !requestDTO.getSpecifications().isEmpty()) {
            productDataService.updateSpecifications(product, requestDTO.getSpecifications());
        }

        return product;
    }

    /**
     * 🔄 更新Product实体
     *
     * @param product 现有商品实体
     * @param requestDTO 更新请求DTO
     * @return 更新后的Product实体
     */
    public Product updateEntity(Product product, ProductUpdateRequestDTO requestDTO) {
        if (product == null || requestDTO == null) {
            return product;
        }

        // 更新基本信息
        if (requestDTO.getProductName() != null) {
            product.setProductName(requestDTO.getProductName());
        }
        if (requestDTO.getDescription() != null) {
            product.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getPrice() != null) {
            product.setPrice(requestDTO.getPrice());
        }
        if (requestDTO.getStockQuantity() != null) {
            product.setStockQuantity(requestDTO.getStockQuantity());
        }
        if (requestDTO.getIsAvailable() != null) {
            product.setIsAvailable(requestDTO.getIsAvailable());
        }
        if (requestDTO.getDiscount() != null) {
            product.setDiscount(requestDTO.getDiscount());
        }

        // 更新主图片
        if (requestDTO.getMainImageUrl() != null) {
            productDataService.updateProductImageData(product, requestDTO.getMainImageUrl());
        }

        // 更新图片列表
        if (requestDTO.getImageUrls() != null) {
            productDataService.updateImageUrls(product, requestDTO.getImageUrls());
        }

        // 更新规格信息 - 使用ProductDataService安全处理
        if (requestDTO.getSpecifications() != null && !requestDTO.getSpecifications().isEmpty()) {
            productDataService.updateSpecifications(product, requestDTO.getSpecifications());
        }

        // 🔧 新架构：使用ProductDataService处理分类品牌等数据
        // 这样可以确保所有productData的更新都是安全和可控的
        if (requestDTO.getCategory() != null) {
            productDataService.updateCategory(product, requestDTO.getCategory());
        }
        if (requestDTO.getBrand() != null) {
            productDataService.updateBrand(product, requestDTO.getBrand());
        }
        if (requestDTO.getColor() != null) {
            productDataService.updateColor(product, requestDTO.getColor());
        }
        if (requestDTO.getSize() != null) {
            productDataService.updateSize(product, requestDTO.getSize());
        }

        return product;
    }

    /**
     * 🔄 批量转换Product实体列表为ProductResponseDTO列表
     *
     * @param products 商品实体列表
     * @return ProductResponseDTO列表
     */
    public List<ProductResponseDTO> toResponseDTOList(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return List.of();
        }
        return products.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 🔄 批量转换Product实体列表为ProductListItemDTO列表
     *
     * @param products 商品实体列表
     * @return ProductListItemDTO列表
     */
    public List<ProductListItemDTO> toListItemDTOList(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return List.of();
        }
        return products.stream()
                .map(this::toListItemDTO)
                .collect(Collectors.toList());
    }

    // ==================== 🔧 私有辅助方法 ====================

    /**
     * 📋 从Product数据中获取变体列表
     */
    @SuppressWarnings("unchecked")
    private List<String> getVariantsFromProductData(Product product) {
        try {
            Map<String, Object> productData = product.getProductData();
            if (productData != null) {
                Object variantsObj = productData.get("variants");
                if (variantsObj instanceof List) {
                    return (List<String>) variantsObj;
                }
            }
        } catch (Exception e) {
            // 忽略异常，返回空列表
        }
        return List.of();
    }

    
    
}