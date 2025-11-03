package com.example.kho_be.controller;

import com.example.kho_be.dto.ProductDTO;
import com.example.kho_be.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Management", description = "API quản lý sản phẩm")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả sản phẩm có phân trang")
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String stockFilter,
            @RequestParam(required = false) Integer threshold,
            @RequestParam(required = false) Integer supplierId) {

        // Tạo Pageable với sorting
        Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductDTO> products;

        // Xử lý các trường hợp filter khác nhau
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Tìm kiếm theo keyword
            products = productService.searchProductsWithPagination(keyword, pageable);
        } else if (supplierId != null) {
            // Lọc theo supplier
            products = productService.getProductsBySupplierWithPagination(supplierId, pageable);
        } else if ("low".equals(stockFilter) || threshold != null) {
            // Lọc theo tồn kho thấp
            int stockThreshold = threshold != null ? threshold : 10;
            products = productService.getLowStockProductsWithPagination(stockThreshold, pageable);
        } else {
            // Lấy tất cả
            products = productService.getAllProductsWithPagination(pageable);
        }

        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin sản phẩm theo ID")
    public ResponseEntity<?> getProductById(@PathVariable Integer id) {
        try {
            ProductDTO product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm sản phẩm theo tên hoặc SKU")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String keyword) {
        List<ProductDTO> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/supplier/{supplierId}")
    @Operation(summary = "Lấy sản phẩm theo nhà cung cấp")
    public ResponseEntity<List<ProductDTO>> getProductsBySupplier(@PathVariable Integer supplierId) {
        List<ProductDTO> products = productService.getProductsBySupplier(supplierId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Lấy sản phẩm có tồn kho thấp")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold) {
        List<ProductDTO> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    @Operation(summary = "Thêm sản phẩm mới")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        try {
            ProductDTO created = productService.createProduct(productDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin sản phẩm")
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductDTO productDTO) {
        try {
            ProductDTO updated = productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa sản phẩm")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(createSuccessResponse("Xóa sản phẩm thành công"));
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Cập nhật tồn kho sản phẩm")
    public ResponseEntity<?> updateStock(
            @PathVariable Integer id,
            @RequestParam Integer stock) {
        try {
            ProductDTO updated = productService.updateStock(id, stock);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    // Helper methods
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        return response;
    }

    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }
}
