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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

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

    /**
     * 📋 获取商品列表（分页）
     *
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认10）
     * @param sortBy 排序字段（默认id）
     * @param sortDirection 排序方向（ASC/DESC，默认DESC）
     * @return 分页商品列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductListItemDTO>>> getProductList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        log.info("获取商品列表请求: page={}, size={}, sortBy={}, sortDirection={}",
                page, size, sortBy, sortDirection);

        Page<ProductListItemDTO> productPage = productService.getProductList(page, size, sortBy, sortDirection);

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
            @Validated @RequestBody ProductUpdateRequestDTO requestDTO) {

        log.info("更新商品请求: id={}, updater={}", id, getCurrentUsername());

        try {
            Long currentUserId = getCurrentUserId();
            ProductResponseDTO product = productService.updateProduct(id, requestDTO, currentUserId);

            ApiResponse<ProductResponseDTO> response = ApiResponse.success(product, "商品更新成功");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("更新商品失败: {}", e.getMessage());
            ApiResponse<ProductResponseDTO> response = ApiResponse.error(e.getMessage(), 400);
            return ResponseEntity.badRequest().body(response);
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
     * 📊 获取商品统计信息
     *
     * @return 统计信息
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProductStatistics() {
        log.info("获取商品统计信息请求: operator={}", getCurrentUsername());

        Map<String, Object> statistics = productService.getProductStatistics();

        ApiResponse<Map<String, Object>> response = ApiResponse.success(statistics, "获取统计信息成功");
        return ResponseEntity.ok(response);
    }

    // ==================== 🔧 私有辅助方法 ====================

    /**
     * 👤 获取当前认证用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未认证");
        }

        // 从认证信息中获取用户ID
        // 注意：这里假设在JWT中包含了用户ID，实际实现可能需要调整
        String userIdStr = authentication.getName();
        try {
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new RuntimeException("无法解析用户ID");
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
}