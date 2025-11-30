package com.cmliy.springweb.controller;

import com.cmliy.springweb.common.ApiResponse;
import com.cmliy.springweb.dto.ProductResponseDTO;
import com.cmliy.springweb.dto.ProductDetailDTO;
import com.cmliy.springweb.dto.ProductSummaryDTO;
import com.cmliy.springweb.dto.ProductListItemDTO;
import com.cmliy.springweb.dto.ProductCreateRequestDTO;
import com.cmliy.springweb.dto.ProductUpdateRequestDTO;
import com.cmliy.springweb.dto.ProductQueryRequestDTO;
import com.cmliy.springweb.service.ProductService;
import com.cmliy.springweb.service.ImageService;
import com.cmliy.springweb.service.ProductDataService;
import com.cmliy.springweb.repository.UserRepository;
import com.cmliy.springweb.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

/**
 * 📦 商品控制器 - Product Controller
 *
 * 提供商品相关的REST API接口，包括商品的CRUD操作、查询、统计等
 * 集成DTO转换，确保API响应的一致性和安全性
 *
 * @author Claude
 * @since 2025-11-22
 */
@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final ImageService imageService;
    private final ProductDataService productDataService;

    /**
     * 📋 获取商品列表（分页）
     *
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认10）
     * @param sortBy 排序字段（默认id）
     * @param sortDirection 排序方向（ASC/DESC，默认DESC）
     * @param isAvailable 是否上架（可选，true=只显示上架商品，false=只显示下架商品，null=显示所有商品）
     * @return 分页商品列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductListItemDTO>>> getProductList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false) Boolean isAvailable) {

        log.info("获取商品列表请求: page={}, size={}, sortBy={}, sortDirection={}, isAvailable={}",
                page, size, sortBy, sortDirection, isAvailable);

        Page<ProductListItemDTO> productPage = productService.getProductList(page, size, sortBy, sortDirection, isAvailable);

        ApiResponse<Page<ProductListItemDTO>> response = ApiResponse.success(productPage, "获取商品列表成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 🔍 根据ID获取商品详情
     *
     * @param id 商品ID
     * @return 商品详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDetailDTO>> getProductById(@PathVariable Long id) {
        log.info("获取商品详情请求: id={}", id);

        return productService.getProductById(id)
                .map(product -> {
                    ApiResponse<ProductDetailDTO> response = ApiResponse.success(product, "获取商品详情成功");
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    ApiResponse<ProductDetailDTO> response = ApiResponse.error("商品不存在", 404);
                    return ResponseEntity.status(404).body(response);
                });
    }

    /**
     * 📝 创建新商品
     *
     * @param requestDTO 创建请求DTO
     * @return 创建的商品信息
     */
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('SHOPER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(
            @Validated @RequestBody ProductCreateRequestDTO requestDTO) {

        log.info("创建商品请求: name={}, creator={}", requestDTO.getProductName(), getCurrentUsername());

        try {
            Long currentUserId = getCurrentUserId();
            ProductResponseDTO product = productService.createProduct(requestDTO, currentUserId);

            ApiResponse<ProductResponseDTO> response = ApiResponse.success(product, "商品创建成功");
            return ResponseEntity.status(201).body(response);

        } catch (RuntimeException e) {
            log.error("创建商品失败: {}", e.getMessage());
            ApiResponse<ProductResponseDTO> response = ApiResponse.error(e.getMessage(), 400);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * ✏️ 更新商品信息
     *
     * @param id 商品ID
     * @param requestDTO 更新请求DTO
     * @return 更新后的商品信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('SHOPER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequestDTO requestDTO) {

        log.info("🔍 [DEBUG] 更新商品请求开始");
        log.info("🔍 [DEBUG] 商品ID: {}", id);
        log.info("🔍 [DEBUG] 操作用户: {}", getCurrentUsername());
        log.info("🔍 [DEBUG] 请求DTO原始内容: {}", requestDTO);

        // 详细记录每个关键字段
        log.info("🔍 [DEBUG] 商品名称: '{}'", requestDTO.getProductName());
        log.info("🔍 [DEBUG] 商品价格: {} (类型: {})", requestDTO.getPrice(),
                requestDTO.getPrice() != null ? requestDTO.getPrice().getClass().getSimpleName() : "null");
        log.info("🔍 [DEBUG] 库存数量: {} (类型: {})", requestDTO.getStockQuantity(),
                requestDTO.getStockQuantity() != null ? requestDTO.getStockQuantity().getClass().getSimpleName() : "null");
        log.info("🔍 [DEBUG] 折扣率: {} (类型: {})", requestDTO.getDiscount(),
                requestDTO.getDiscount() != null ? requestDTO.getDiscount().getClass().getSimpleName() : "null");
        log.info("🔍 [DEBUG] 是否上架: {}", requestDTO.getIsAvailable());
        log.info("🔍 [DEBUG] 商品规格: {} (类型: {})", requestDTO.getSpecifications(),
                requestDTO.getSpecifications() != null ? requestDTO.getSpecifications().getClass().getSimpleName() : "null");
        log.info("🔍 [DEBUG] productData: {} (类型: {})", requestDTO.getProductData(),
                requestDTO.getProductData() != null ? requestDTO.getProductData().getClass().getSimpleName() : "null");
        log.info("🔍 [DEBUG] 主图片URL: '{}'", requestDTO.getMainImageUrl());
        log.info("🔍 [DEBUG] 商品描述: '{}'", requestDTO.getDescription());

        try {
            log.info("🔍 [DEBUG] 开始获取当前用户ID");
            Long currentUserId = getCurrentUserId();
            log.info("🔍 [DEBUG] 当前用户ID: {}", currentUserId);

            log.info("🔍 [DEBUG] 开始调用productService.updateProduct");
            ProductResponseDTO product = productService.updateProduct(id, requestDTO, currentUserId);
            log.info("🔍 [DEBUG] productService.updateProduct调用成功");
            log.info("🔍 [DEBUG] 返回的商品信息: {}", product);

            ApiResponse<ProductResponseDTO> response = ApiResponse.success(product, "商品更新成功");
            log.info("🔍 [DEBUG] 构建成功响应: {}", response);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ [DEBUG] 更新商品失败: {}", e.getMessage());
            log.error("❌ [DEBUG] 异常类型: {}", e.getClass().getSimpleName());
            log.error("❌ [DEBUG] 异常堆栈: ", e);

            // 记录失败时的请求状态
            log.error("❌ [DEBUG] 失败时的请求DTO状态: {}", requestDTO);

            ApiResponse<ProductResponseDTO> response = ApiResponse.error(e.getMessage(), 400);
            log.info("🔍 [DEBUG] 构建错误响应: {}", response);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("❌ [DEBUG] 未预期的异常: {}", e.getMessage(), e);
            log.error("❌ [DEBUG] 异常类型: {}", e.getClass().getSimpleName());

            ApiResponse<ProductResponseDTO> response = ApiResponse.error("系统内部错误: " + e.getMessage(), 500);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 🗑️ 删除商品
     *
     * @param id 商品ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SHOPER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        log.info("删除商品请求: id={}, deleter={}", id, getCurrentUsername());

        try {
            Long currentUserId = getCurrentUserId();
            productService.deleteProduct(id, currentUserId);

            ApiResponse<Void> response = ApiResponse.success(null, "商品删除成功");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("删除商品失败: {}", e.getMessage());
            ApiResponse<Void> response = ApiResponse.error(e.getMessage(), 400);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 🔍 搜索商品
     *
     * @param queryRequest 查询请求DTO
     * @return 搜索结果
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductListItemDTO>>> searchProducts(
            @RequestBody ProductQueryRequestDTO queryRequest) {

        log.info("搜索商品请求: keyword={}, category={}",
                queryRequest.getKeyword(), queryRequest.getCategory());

        List<ProductListItemDTO> products = productService.searchProducts(queryRequest);

        ApiResponse<List<ProductListItemDTO>> response = ApiResponse.success(products, "搜索商品成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 📊 获取商品摘要列表
     *
     * @param limit 限制数量（默认10）
     * @return 商品摘要列表
     */
    @GetMapping("/summaries")
    public ResponseEntity<ApiResponse<List<ProductSummaryDTO>>> getProductSummaries(
            @RequestParam(defaultValue = "10") int limit) {

        log.info("获取商品摘要列表请求: limit={}", limit);

        List<ProductSummaryDTO> summaries = productService.getProductSummaries(limit);

        ApiResponse<List<ProductSummaryDTO>> response = ApiResponse.success(summaries, "获取商品摘要成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 📈 增加商品库存
     *
     * @param id 商品ID
     * @param request 包含增加数量的请求体
     * @return 操作结果
     */
    @PostMapping("/{id}/stock/increase")
    @PreAuthorize("hasRole('SHOPER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> increaseStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {

        Integer quantity = request.get("quantity");
        if (quantity == null || quantity <= 0) {
            ApiResponse<Void> response = ApiResponse.error("增加数量必须大于0", 400);
            return ResponseEntity.badRequest().body(response);
        }

        log.info("增加商品库存请求: id={}, quantity={}, operator={}", id, quantity, getCurrentUsername());

        try {
            productService.increaseStock(id, quantity);
            ApiResponse<Void> response = ApiResponse.success(null, "库存增加成功");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("增加库存失败: {}", e.getMessage());
            ApiResponse<Void> response = ApiResponse.error(e.getMessage(), 400);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 📉 减少商品库存
     *
     * @param id 商品ID
     * @param request 包含减少数量的请求体
     * @return 操作结果
     */
    @PostMapping("/{id}/stock/decrease")
    @PreAuthorize("hasRole('SHOPER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> decreaseStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {

        Integer quantity = request.get("quantity");
        if (quantity == null || quantity <= 0) {
            ApiResponse<Map<String, Object>> response = ApiResponse.error("减少数量必须大于0", 400);
            return ResponseEntity.badRequest().body(response);
        }

        log.info("减少商品库存请求: id={}, quantity={}, operator={}", id, quantity, getCurrentUsername());

        try {
            boolean success = productService.decreaseStock(id, quantity);
            Map<String, Object> result = Map.of("success", success);

            if (success) {
                ApiResponse<Map<String, Object>> response = ApiResponse.success(result, "库存减少成功");
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Map<String, Object>> response = ApiResponse.error("库存不足，无法减少", 400);
                response.setData(result);
                return ResponseEntity.badRequest().body(response);
            }

        } catch (RuntimeException e) {
            log.error("减少库存失败: {}", e.getMessage());
            ApiResponse<Map<String, Object>> response = ApiResponse.error(e.getMessage(), 400);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 🔄 切换商品上架状态
     *
     * @param id 商品ID
     * @return 操作结果
     */
    @PostMapping("/{id}/toggle-availability")
    @PreAuthorize("hasRole('SHOPER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> toggleProductAvailability(@PathVariable Long id) {
        log.info("切换商品上架状态请求: id={}, operator={}", id, getCurrentUsername());

        try {
            Long currentUserId = getCurrentUserId();
            productService.toggleProductAvailability(id, currentUserId);

            ApiResponse<Void> response = ApiResponse.success(null, "商品状态切换成功");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("切换商品状态失败: {}", e.getMessage());
            ApiResponse<Void> response = ApiResponse.error(e.getMessage(), 400);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 🏪 获取当前商家的商品列表
     *
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认12）
     * @param keyword 搜索关键词（可选）
     * @param category 商品分类（可选）
     * @param isAvailable 是否上架（可选）
     * @param sortBy 排序字段（默认createdAt）
     * @param sortDirection 排序方向（默认desc）
     * @return 商家商品分页列表
     */
    @GetMapping("/merchant")
    @PreAuthorize("hasRole('SHOPER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ProductListItemDTO>>> getMerchantProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        log.info("获取商家商品列表请求: page={}, size={}, keyword={}, category={}, isAvailable={}, sortBy={}, sortDirection={}, operator={}",
                page, size, keyword, category, isAvailable, sortBy, sortDirection, getCurrentUsername());

        try {
            Long currentUserId = getCurrentUserId();
            Page<ProductListItemDTO> productPage = productService.getMerchantProducts(
                    currentUserId, page, size, keyword, category, isAvailable, sortBy, sortDirection);

            ApiResponse<Page<ProductListItemDTO>> response = ApiResponse.success(productPage, "获取商家商品列表成功");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("获取商家商品列表失败: {}", e.getMessage());
            ApiResponse<Page<ProductListItemDTO>> response = ApiResponse.error(e.getMessage(), 400);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 📊 获取商品统计信息
     *
     * @return 统计信息
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('SHOPER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProductStatistics() {
        log.info("获取商品统计信息请求: operator={}", getCurrentUsername());

        try {
            Long currentUserId = getCurrentUserId();
            Map<String, Object> statistics = productService.getProductStatistics(currentUserId);

            ApiResponse<Map<String, Object>> response = ApiResponse.success(statistics, "获取统计信息成功");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("获取商品统计信息失败: {}", e.getMessage());
            ApiResponse<Map<String, Object>> response = ApiResponse.error(e.getMessage(), 400);
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== 🔧 私有辅助方法 ====================

    /**
     * 👤 获取当前认证用户ID
     * 优先从JWT claims中获取用户ID，如果失败则从用户名查询
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未认证");
        }

        // 方案1：尝试从JWT claims中获取用户ID（推荐的优化方案）
        try {
            // 从认证信息中获取JWT token
            if (authentication.getCredentials() instanceof String) {
                String token = (String) authentication.getCredentials();
                // 确保token包含Bearer前缀时去除
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }

                // 使用JwtUtil提取用户ID
                Long userId = jwtUtil.extractUserId(token);
                if (userId != null) {
                    return userId;
                }
            }
        } catch (Exception e) {
            // JWT claims提取失败，使用备用方案
            log.debug("从JWT claims获取用户ID失败，使用备用方案: {}", e.getMessage());
        }

        // 方案2：备用方案 - 从用户名查询用户ID
        String username = authentication.getName();
        try {
            return userRepository.findByUsername(username)
                    .map(user -> user.getId())
                    .orElseThrow(() -> new RuntimeException("用户不存在: " + username));
        } catch (Exception e) {
            throw new RuntimeException("无法获取用户ID: " + e.getMessage());
        }
    }

    /**
     * 👤 获取当前认证用户名
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "anonymous";
        }
        return authentication.getName();
    }

    // ==================== 🖼️ 图片管理端点 ====================

    /**
     * 📤 上传商品图片
     *
     * @param id 商品ID
     * @param file 上传的图片文件
     * @return 上传结果，包含图片URL
     */
    @PostMapping("/{id}/image")
    @PreAuthorize("hasRole('SHOPER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadProductImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        log.info("上传商品图片请求: id={}, filename={}", id, file.getOriginalFilename());

        try {
            Long currentUserId = getCurrentUserId();

            // 验证商品所有权
            com.cmliy.springweb.model.Product product = productService.getProductByIdForUpdate(id, currentUserId)
                    .orElseThrow(() -> new RuntimeException("商品不存在或无权限访问"));

            // 上传图片（使用商品ID+image命名规则）
            com.cmliy.springweb.service.ImageService.ImageUploadResult uploadResult = imageService.uploadProductImage(file, id);

            // 更新商品图片数据
            productDataService.updateProductImageData(product, uploadResult.getImageUrl());

            // 保存商品
            productService.saveProduct(product);

            // 返回前端期望的格式
            Map<String, String> responseData = Map.of("imageUrl", uploadResult.getImageUrl());
            ApiResponse<Map<String, String>> response = ApiResponse.success(responseData, "图片上传成功");

            return ResponseEntity.ok(response);

        } catch (com.cmliy.springweb.exception.ImageUploadException e) {
            log.error("图片上传失败: {}", e.getMessage());
            ApiResponse<Map<String, String>> response = ApiResponse.error(e.getMessage(), 400);
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            log.error("处理图片上传失败: {}", e.getMessage());
            ApiResponse<Map<String, String>> response = ApiResponse.error(e.getMessage(), 400);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 🗑️ 删除商品图片
     *
     * @param id 商品ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}/image")
    @PreAuthorize("hasRole('SHOPER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProductImage(@PathVariable Long id) {

        log.info("删除商品图片请求: id={}", id);

        try {
            Long currentUserId = getCurrentUserId();

            // 验证商品所有权
            com.cmliy.springweb.model.Product product = productService.getProductByIdForUpdate(id, currentUserId)
                    .orElseThrow(() -> new RuntimeException("商品不存在或无权限访问"));

            // 清除商品图片数据
            productDataService.updateProductImageData(product, null);

            // 保存商品
            productService.saveProduct(product);

            ApiResponse<Void> response = ApiResponse.success(null, "图片删除成功");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("删除图片失败: {}", e.getMessage());
            ApiResponse<Void> response = ApiResponse.error(e.getMessage(), 400);
            return ResponseEntity.badRequest().body(response);
        }
    }
}