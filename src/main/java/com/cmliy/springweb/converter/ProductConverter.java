package com.cmliy.springweb.converter;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.cmliy.springweb.dto.ProductCreateRequestDTO;
import com.cmliy.springweb.dto.ProductDetailDTO;
import com.cmliy.springweb.dto.ProductListItemDTO;
import com.cmliy.springweb.dto.ProductResponseDTO;
import com.cmliy.springweb.dto.ProductSummaryDTO;
import com.cmliy.springweb.dto.ProductUpdateRequestDTO;
import com.cmliy.springweb.model.Product;          // @Slf4j: 自动生成Logger实例
import com.cmliy.springweb.model.User;   // @RequiredArgsConstructor: 自动生成构造函数
import com.cmliy.springweb.service.ProductDataService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🔄 商品转换器 - Product Converter (Lombok + BaseConverter优化版本)
 *
 * 负责Product实体与各种DTO之间的转换
 * 统一管理商品数据的映射逻辑，确保数据一致性
 *
 * 🚀 Lombok优化展示：
 * - @Slf4j: 自动生成Logger实例，无需手动创建
 * - @RequiredArgsConstructor: 自动生成包含所有final字段的构造函数
 * - 继承BaseConverter: 获得统一的转换工具方法
 *
 * 🚀 BaseConverter集成优势：
 * - safeConvert(): 单个对象转换的空值安全和异常处理
 * - safeConvertList(): 批量转换的统一逻辑
 * - 减少重复的null检查和异常处理代码
 *

 */
@Slf4j
@RequiredArgsConstructor  // 🚀 Lombok: 自动生成包含所有final字段的构造函数
@Component
public class ProductConverter extends BaseConverter<Product, ProductResponseDTO> {  // 🚀 继承BaseConverter获得统一转换方法

    /**
     * 🏗️ 商品数据服务
     *
     * 🚀 Lombok的@RequiredArgsConstructor会自动生成构造函数注入
     * final关键字：表示这个字段一旦初始化就不能再修改，确保线程安全
     */
    private final ProductDataService productDataService;

    // 🚀 Lombok生成的构造函数等效代码：
    // public ProductConverter(ProductDataService productDataService) {
    //     this.productDataService = productDataService;
    // }

    /**
     * 🔄 标准Entity到DTO转换
     * 委派给专门的toResponseDTO方法处理
     *
     * @param product 实体对象
     * @return ProductResponseDTO对象
     */
    @Override
    public ProductResponseDTO toDTO(Product product) {
        return toResponseDTO(product);
    }

    /**
     * 🔄 标准DTO到Entity转换
     * 基础转换，实际业务中应使用专门的toEntity方法
     *
     * @param dto ProductResponseDTO对象
     * @return Product实体
     */
    @Override
    public Product toEntity(ProductResponseDTO dto) {
        if (dto == null) return null;

        // 使用通用转换工具进行基础转换
        return dtoConverter.toEntity(dto, Product.class);
    }

    /**
     * 🔧 获取DTO类型（用于通用转换）
     *
     * @return ProductResponseDTO类
     */
    @Override
    protected Class<ProductResponseDTO> getDTOClass() {
        return ProductResponseDTO.class;
    }

    /**
     * 🔧 获取实体类型（用于通用转换）
     *
     * @return Product类
     */
    @Override
    protected Class<Product> getEntityClass() {
        return Product.class;
    }

    /**
     * 🔄 Product实体转ProductResponseDTO (BaseController优化版本)
     *
     * 🚀 优化亮点：
     * - 使用BaseConverter的safeConvert()方法确保空值安全和异常处理
     * - 保持完整业务逻辑的同时增加安全性
     *
     * @param product 商品实体
     * @return ProductResponseDTO
     */
    public ProductResponseDTO toResponseDTO(Product product) {
        return safeConvert(product, p -> {
            ProductResponseDTO dto = new ProductResponseDTO();
            dto.setId(p.getId());
            dto.setProductName(p.getProductName());
            dto.setDescription(p.getDescription());
            dto.setPrice(p.getPrice());
            dto.setSalesCount(p.getSalesCount());
            dto.setDiscount(p.getDiscount());
            dto.setStockQuantity(p.getStockQuantity());
            dto.setIsAvailable(p.getIsAvailable());

            // 创建者信息
            if (p.getCreator() != null) {
                dto.setCreatorId(p.getCreator().getId());
                dto.setCreatorUsername(p.getCreator().getUsername());
            }

            // 图片信息
            dto.setMainImageUrl(p.getMainImage());

            // 统一规格信息 - 只返回specifications
            dto.setSpecifications(p.getAllSpecifications());

            // 格式化价格
            dto.setFormattedPrice(p.getFormattedPrice());
            dto.setFormattedDiscountedPrice(p.getFormattedDiscountedPrice());
            dto.setStockStatus(p.getStockStatus());

            // 时间戳
            dto.setCreatedAt(p.getCreatedAt());
            dto.setUpdatedAt(p.getUpdatedAt());

            return dto;
        });
    }

    /**
     * 🔄 Product实体转ProductDetailDTO (BaseConverter优化版本)
     *
     * 🚀 优化亮点：
     * - 使用BaseConverter的safeConvert()方法确保空值安全和异常处理
     *
     * @param product 商品实体
     * @return ProductDetailDTO
     */
    public ProductDetailDTO toDetailDTO(Product product) {
        return safeConvert(product, p -> {
            ProductDetailDTO dto = new ProductDetailDTO();
            dto.setId(p.getId());
            dto.setProductName(p.getProductName());
            dto.setDescription(p.getDescription());
            dto.setPrice(p.getPrice());
            dto.setSalesCount(p.getSalesCount());
            dto.setDiscount(p.getDiscount());
            dto.setStockQuantity(p.getStockQuantity());
            dto.setIsAvailable(p.getIsAvailable());

            // 创建者信息
            if (p.getCreator() != null) {
                dto.setCreatorId(p.getCreator().getId());
                dto.setCreatorUsername(p.getCreator().getUsername());
            }

            // 详细图片信息
            dto.setMainImageUrl(p.getMainImage());
            dto.setThumbnails(p.getThumbnails());
            dto.setTotalImages(p.getTotalImages());

            // 统一规格信息 - 只返回specifications
            dto.setSpecifications(p.getAllSpecifications());

            // 价格信息
            dto.setFormattedPrice(p.getFormattedPrice());
            dto.setFormattedDiscountedPrice(p.getFormattedDiscountedPrice());
            dto.setDiscountDisplay(p.getDiscountDisplay());
            dto.setDiscountAmount(p.getDiscountAmount());
            dto.setStockStatus(p.getStockStatus());

            // 时间戳
            dto.setCreatedAt(p.getCreatedAt());
            dto.setUpdatedAt(p.getUpdatedAt());

            return dto;
        });
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

        // 更新规格信息 - 使用ProductDataService安全处理
        if (requestDTO.getSpecifications() != null && !requestDTO.getSpecifications().isEmpty()) {
            productDataService.updateSpecifications(product, requestDTO.getSpecifications());
        }

        
        return product;
    }

    /**
     * 🔄 批量转换Product实体列表为ProductResponseDTO列表 (BaseConverter优化版本)
     *
     * 🚀 优化亮点：
     * - 使用BaseConverter的safeConvertList()方法确保空值安全和异常处理
     * - 统一批量转换逻辑，减少重复代码
     *
     * @param products 商品实体列表
     * @return ProductResponseDTO列表
     */
    public List<ProductResponseDTO> toResponseDTOList(List<Product> products) {
        return safeConvertList(products, this::toResponseDTO, "ProductResponseDTO列表");
    }

    /**
     * 🔄 批量转换Product实体列表为ProductListItemDTO列表 (BaseConverter优化版本)
     *
     * 🚀 优化亮点：
     * - 使用BaseConverter的safeConvertList()方法确保空值安全和异常处理
     * - 统一批量转换逻辑，减少重复代码
     *
     * @param products 商品实体列表
     * @return ProductListItemDTO列表
     */
    public List<ProductListItemDTO> toListItemDTOList(List<Product> products) {
        return safeConvertList(products, this::toListItemDTO, "ProductListItemDTO列表");
    }

    // ==================== 🔧 私有辅助方法 ====================

    
    
    
}