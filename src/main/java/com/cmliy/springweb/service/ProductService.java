package com.cmliy.springweb.service;

import com.cmliy.springweb.converter.ProductConverter;
import com.cmliy.springweb.dto.ProductResponseDTO;
import com.cmliy.springweb.dto.ProductDetailDTO;
import com.cmliy.springweb.dto.ProductSummaryDTO;
import com.cmliy.springweb.dto.ProductListItemDTO;
import com.cmliy.springweb.dto.ProductCreateRequestDTO;
import com.cmliy.springweb.dto.ProductUpdateRequestDTO;
import com.cmliy.springweb.dto.ProductQueryRequestDTO;
import com.cmliy.springweb.model.Product;
import com.cmliy.springweb.model.User;
import com.cmliy.springweb.repository.ProductRepository;
import com.cmliy.springweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.criteria.Predicate;

/**
 * 📦 商品服务 - Product Service
 *
 * 提供商品的完整业务逻辑处理，包括CRUD操作、查询、统计等
 * 集成DTO转换，确保数据传输的一致性和安全性
 *
 * @author Claude
 * @since 2025-11-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

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
        log.info("创建商品: name={}, creatorId={}", requestDTO.getProductName(), creatorId);

        // 验证创建者存在
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("创建者不存在: " + creatorId));

        // 检查商品名称是否已存在
        if (productRepository.existsByProductName(requestDTO.getProductName())) {
            throw new RuntimeException("商品名称已存在: " + requestDTO.getProductName());
        }

        // 转换DTO为实体
        Product product = productConverter.toEntity(requestDTO, creator);

        // 保存商品
        Product savedProduct = productRepository.save(product);

        log.info("商品创建成功: id={}, name={}", savedProduct.getId(), savedProduct.getProductName());

        return productConverter.toResponseDTO(savedProduct);
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
        log.info("更新商品: id={}, updaterId={}", id, updaterId);

        // 获取现有商品
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在: " + id));

        // 检查商品名称是否与其他商品冲突（如果更改了名称）
        if (requestDTO.getProductName() != null &&
            !requestDTO.getProductName().equals(product.getProductName()) &&
            productRepository.existsByProductName(requestDTO.getProductName())) {
            throw new RuntimeException("商品名称已存在: " + requestDTO.getProductName());
        }

        // 更新商品信息
        Product updatedProduct = productConverter.updateEntity(product, requestDTO);
        Product savedProduct = productRepository.save(updatedProduct);

        log.info("商品更新成功: id={}, name={}", savedProduct.getId(), savedProduct.getProductName());

        return productConverter.toResponseDTO(savedProduct);
    }

    /**
     * 🗑️ 删除商品
     *
     * @param id 商品ID
     * @param deleterId 删除者ID
     */
    @Transactional
    public void deleteProduct(Long id, Long deleterId) {
        log.info("删除商品: id={}, deleterId={}", id, deleterId);

        if (!productRepository.existsById(id)) {
            throw new RuntimeException("商品不存在: " + id);
        }

        productRepository.deleteById(id);
        log.info("商品删除成功: id={}", id);
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
        log.info("增加商品库存: id={}, quantity={}", id, quantity);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在: " + id));

        product.increaseStock(quantity);
        productRepository.save(product);

        log.info("商品库存增加成功: id={}, newStock={}", id, product.getStockQuantity());
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
        log.info("减少商品库存: id={}, quantity={}", id, quantity);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在: " + id));

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
    }

    /**
     * 🔄 切换商品上架状态
     *
     * @param id 商品ID
     * @param operatorId 操作者ID
     */
    @Transactional
    public void toggleProductAvailability(Long id, Long operatorId) {
        log.info("切换商品上架状态: id={}, operatorId={}", id, operatorId);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在: " + id));

        product.setIsAvailable(!product.getIsAvailable());
        productRepository.save(product);

        log.info("商品状态切换成功: id={}, newStatus={}", id, product.getIsAvailable());
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
}