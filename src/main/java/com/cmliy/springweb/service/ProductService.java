package com.cmliy.springweb.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmliy.springweb.converter.ProductConverter;
import com.cmliy.springweb.dto.ProductCreateRequestDTO;
import com.cmliy.springweb.dto.ProductDetailDTO;
import com.cmliy.springweb.dto.ProductListItemDTO;
import com.cmliy.springweb.dto.ProductQueryRequestDTO;
import com.cmliy.springweb.dto.ProductResponseDTO;
import com.cmliy.springweb.dto.ProductSummaryDTO;
import com.cmliy.springweb.dto.ProductUpdateRequestDTO;
import com.cmliy.springweb.model.Product;
import com.cmliy.springweb.model.User;
import com.cmliy.springweb.repository.ProductRepository;
import com.cmliy.springweb.repository.UserRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 📦 商品服务 - Product Service
 *
 * 提供商品的完整业务逻辑处理，包括CRUD操作、查询、统计等
 * 集成DTO转换，确保数据传输的一致性和安全性

 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService extends BaseService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductConverter productConverter;

    /**
     * 📋 获取商品列表（分页）
     *
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDirection 排序方向（ASC/DESC）
     * @param isAvailable 是否上架（可选，true=只显示上架商品，false=只显示下架商品，null=显示所有商品）
     * @return 分页商品列表
     */
    public Page<ProductListItemDTO> getProductList(int page, int size, String sortBy, String sortDirection, Boolean isAvailable) {
        log.info("获取商品列表: page={}, size={}, sortBy={}, sortDirection={}, isAvailable={}",
                page, size, sortBy, sortDirection, isAvailable);

        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // 如果需要按上架状态过滤，使用Specification查询
        if (isAvailable != null) {
            Specification<Product> spec = (root, query, criteriaBuilder) -> {
                return criteriaBuilder.equal(root.get("isAvailable"), isAvailable);
            };
            Page<Product> productPage = productRepository.findAll(spec, pageable);
            return productPage.map(productConverter::toListItemDTO);
        } else {
            // 如果没有过滤条件，使用原来的查询方式
            Page<Product> productPage = productRepository.findAll(pageable);
            return productPage.map(productConverter::toListItemDTO);
        }
    }

    /**
     * 🔍 根据ID获取商品详情
     *
     * @param id 商品ID
     * @return 商品详情DTO
     */
    public Optional<ProductDetailDTO> getProductById(Long id) {
        log.info("获取商品详情: id={}", id);

        Optional<Product> productOpt = productRepository.findById(id);
        return productOpt.map(productConverter::toDetailDTO);
    }

    /**
     * 🔍 根据ID获取商品基本信息
     *
     * @param id 商品ID
     * @return 商品响应DTO
     */
    public Optional<ProductResponseDTO> getProductResponseById(Long id) {
        log.info("获取商品基本信息: id={}", id);

        Optional<Product> productOpt = productRepository.findById(id);
        return productOpt.map(productConverter::toResponseDTO);
    }

    /**
     * 📝 创建新商品
     *
     * @param requestDTO 创建请求DTO
     * @param creatorId 创建者ID
     * @return 创建的商品响应DTO
     */
    @Transactional
    public ProductResponseDTO createProduct(ProductCreateRequestDTO requestDTO, Long creatorId) {
        return executeWithLog("创建商品", () -> {
            // 验证创建者存在
            User creator = validateExists(userRepository.findById(creatorId), "创建者", creatorId);

            // 检查商品名称是否已存在
            validateUnique(productRepository.existsByProductName(requestDTO.getProductName()),
                          "商品名称", requestDTO.getProductName());

            // 转换DTO为实体
            Product product = productConverter.toEntity(requestDTO, creator);

            // 保存商品
            Product savedProduct = productRepository.save(product);

            return productConverter.toResponseDTO(savedProduct);
        }, requestDTO.getProductName(), creatorId);
    }

    /**
     * ✏️ 更新商品信息
     *
     * @param id 商品ID
     * @param requestDTO 更新请求DTO
     * @param updaterId 更新者ID
     * @return 更新后的商品响应DTO
     */
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductUpdateRequestDTO requestDTO, Long updaterId) {
        return executeWithLog("更新商品", () -> {
            // 获取现有商品
            Product product = validateExists(productRepository.findById(id), "商品", id);

            // 检查商品名称是否与其他商品冲突（如果更改了名称）
            if (requestDTO.getProductName() != null &&
                !requestDTO.getProductName().equals(product.getProductName()) &&
                productRepository.existsByProductName(requestDTO.getProductName())) {
                validateUnique(true, "商品名称", requestDTO.getProductName());
            }

            // 更新商品信息
            Product updatedProduct = productConverter.updateEntity(product, requestDTO);
            Product savedProduct = productRepository.save(updatedProduct);

            return productConverter.toResponseDTO(savedProduct);
        }, id, updaterId);
    }

    /**
     * 🗑️ 删除商品
     *
     * @param id 商品ID
     * @param deleterId 删除者ID
     */
    @Transactional
    public void deleteProduct(Long id, Long deleterId) {
        executeWithLog("删除商品", () -> {
            // 验证商品存在
            validateExists(productRepository.findById(id), "商品", id);

            // 删除商品
            productRepository.deleteById(id);
        }, id, deleterId);
    }

    /**
     * 🔍 搜索商品
     *
     * @param queryRequest 查询请求DTO
     * @return 搜索结果列表
     */
    public List<ProductListItemDTO> searchProducts(ProductQueryRequestDTO queryRequest) {
        log.info("搜索商品: keyword={}, category={}",
                queryRequest.getKeyword(), queryRequest.getCategory());

        Specification<Product> spec = buildSearchSpecification(queryRequest);
        List<Product> products = productRepository.findAll(spec);

        return productConverter.toListItemDTOList(products);
    }

    /**
     * 📊 获取商品摘要列表
     *
     * @param limit 限制数量
     * @return 商品摘要列表
     */
    public List<ProductSummaryDTO> getProductSummaries(int limit) {
        log.info("获取商品摘要列表: limit={}", limit);

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "salesCount"));
        List<Product> products = productRepository.findTopProductsBySalesCount(pageable);

        return products.stream()
                .map(productConverter::toSummaryDTO)
                .toList();
    }

    /**
     * 📈 增加商品库存
     *
     * @param id 商品ID
     * @param quantity 增加数量
     */
    @Transactional
    public void increaseStock(Long id, Integer quantity) {
        executeWithLog("增加商品库存", () -> {
            // 验证数量为正数
            validatePositive(quantity, "增加数量");

            // 验证商品存在
            Product product = validateExists(productRepository.findById(id), "商品", id);

            // 增加库存
            product.increaseStock(quantity);
            productRepository.save(product);
        }, id, quantity);
    }

    /**
     * 📉 减少商品库存（用于购买）
     *
     * @param id 商品ID
     * @param quantity 减少数量
     * @return 是否成功
     */
    @Transactional
    public boolean decreaseStock(Long id, Integer quantity) {
        return executeWithLog("减少商品库存", () -> {
            // 验证数量为正数
            validatePositive(quantity, "减少数量");

            // 验证商品存在
            Product product = validateExists(productRepository.findById(id), "商品", id);

            // 减少库存
            boolean success = product.decreaseStock(quantity);
            if (success) {
                productRepository.save(product);
                log.info("商品库存减少成功: id={}, newStock={}, newSales={}",
                        id, product.getStockQuantity(), product.getSalesCount());
            } else {
                log.warn("商品库存不足，无法减少: id={}, requested={}, current={}",
                        id, quantity, product.getStockQuantity());
            }

            return success;
        }, id, quantity);
    }

    /**
     * 🔄 切换商品上架状态
     *
     * @param id 商品ID
     * @param operatorId 操作者ID
     */
    @Transactional
    public void toggleProductAvailability(Long id, Long operatorId) {
        executeWithLog("切换商品上架状态", () -> {
            // 验证商品存在
            Product product = validateExists(productRepository.findById(id), "商品", id);

            // 切换状态
            product.setIsAvailable(!product.getIsAvailable());
            productRepository.save(product);
        }, id, operatorId);
    }

    /**
     * 🏪 获取指定商家的商品列表
     *
     * @param merchantId 商家ID
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param keyword 搜索关键词（可选）
     * @param category 商品分类（可选）
     * @param isAvailable 是否上架（可选）
     * @param sortBy 排序字段
     * @param sortDirection 排序方向
     * @return 商家商品分页列表
     */
    public Page<ProductListItemDTO> getMerchantProducts(
            Long merchantId, int page, int size, String keyword,
            String category, Boolean isAvailable, String sortBy, String sortDirection) {
        log.info("获取商家商品列表: merchantId={}, page={}, size={}, keyword={}, category={}, isAvailable={}, sortBy={}, sortDirection={}",
                merchantId, page, size, keyword, category, isAvailable, sortBy, sortDirection);

        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // 构建查询条件
        Specification<Product> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 按商家过滤
            predicates.add(criteriaBuilder.equal(root.get("creator").get("id"), merchantId));

            // 按上架状态过滤
            if (isAvailable != null) {
                predicates.add(criteriaBuilder.equal(root.get("isAvailable"), isAvailable));
            }

            // 关键词搜索
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchKeyword = "%" + keyword.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("productName")), searchKeyword);
                Predicate descPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")), searchKeyword);
                predicates.add(criteriaBuilder.or(namePredicate, descPredicate));
            }

            // 按分类过滤
            if (category != null && !category.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Product> productPage = productRepository.findAll(spec, pageable);
        return productPage.map(productConverter::toListItemDTO);
    }

    /**
     * 📦 根据ID列表获取商品列表
     *
     * @param productIds 商品ID列表
     * @return 商品列表
     */
    public List<ProductListItemDTO> getProductsByIds(List<Long> productIds) {
        log.info("根据ID列表获取商品: ids={}", productIds);

        if (productIds == null || productIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Product> products = productRepository.findByIdIn(productIds);
        return productConverter.toListItemDTOList(products);
    }

    /**
     * 📊 获取指定商家的商品统计信息
     *
     * @param merchantId 商家ID（可选，如果为null则返回全局统计）
     * @return 统计信息Map
     */
    public java.util.Map<String, Object> getProductStatistics(Long merchantId) {
        log.info("获取商品统计信息: merchantId={}", merchantId);

        long totalProducts;
        long availableProducts;
        long unavailableProducts;
        BigDecimal totalRevenue = BigDecimal.ZERO;

        if (merchantId != null) {
            // 获取指定商家的统计
            totalProducts = productRepository.countByCreatorId(merchantId);
            availableProducts = productRepository.countByCreatorIdAndIsAvailable(merchantId, true);
            unavailableProducts = totalProducts - availableProducts;

            // 计算该商家的总销售额
            List<Product> merchantProducts = productRepository.findByCreatorId(merchantId);
            totalRevenue = merchantProducts.stream()
                .map(product -> product.getPrice().multiply(BigDecimal.valueOf(product.getSalesCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            // 获取全局统计（保持向后兼容）
            totalProducts = productRepository.count();
            availableProducts = productRepository.countByIsAvailable(true);
            unavailableProducts = totalProducts - availableProducts;

            List<Product> allProducts = productRepository.findAll();
            totalRevenue = allProducts.stream()
                .map(product -> product.getPrice().multiply(BigDecimal.valueOf(product.getSalesCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalProducts", totalProducts);
        stats.put("availableProducts", availableProducts);
        stats.put("unavailableProducts", unavailableProducts);
        stats.put("totalRevenue", totalRevenue);
        stats.put("availableRate", totalProducts > 0 ? (double) availableProducts / totalProducts * 100 : 0);

        return stats;
    }

    /**
     * 📊 获取商品统计信息（向后兼容方法）
     *
     * @return 统计信息Map
     */
    public java.util.Map<String, Object> getProductStatistics() {
        return getProductStatistics(null);
    }

    // ==================== 🔧 私有辅助方法 ====================

    /**
     * 🔍 构建搜索规格
     */
    private Specification<Product> buildSearchSpecification(ProductQueryRequestDTO queryRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 关键词搜索
            if (queryRequest.getKeyword() != null && !queryRequest.getKeyword().trim().isEmpty()) {
                String keyword = "%" + queryRequest.getKeyword().trim() + "%";
                Predicate namePredicate = criteriaBuilder.like(root.get("productName"), keyword);
                Predicate descPredicate = criteriaBuilder.like(root.get("description"), keyword);
                predicates.add(criteriaBuilder.or(namePredicate, descPredicate));
            }

            // 价格范围
            if (queryRequest.getMinPrice() != null && queryRequest.getMinPrice().compareTo(BigDecimal.ZERO) > 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), queryRequest.getMinPrice()));
            }
            if (queryRequest.getMaxPrice() != null && queryRequest.getMaxPrice().compareTo(BigDecimal.ZERO) > 0) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), queryRequest.getMaxPrice()));
            }

            // 是否上架
            if (queryRequest.getIsAvailable() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isAvailable"), queryRequest.getIsAvailable()));
            }

            // 库存筛选
            if (queryRequest.getHasStock() != null && queryRequest.getHasStock()) {
                predicates.add(criteriaBuilder.greaterThan(root.get("stockQuantity"), 0));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ==================== 🔧 图片上传相关方法 ====================

    /**
     * 🔍 根据ID和用户ID获取商品（用于更新操作）
     *
     * @param id 商品ID
     * @param userId 用户ID
     * @return 商品信息（如果存在且属于该用户）
     */
    @Transactional(readOnly = true)
    public Optional<Product> getProductByIdForUpdate(Long id, Long userId) {
        return productRepository.findById(id)
                .filter(product -> product.getCreator().getId().equals(userId));
    }

    /**
     * 💾 保存商品
     *
     * @param product 商品实体
     * @return 保存后的商品
     */
    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
}