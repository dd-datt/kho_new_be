package com.example.kho_be.repository;

import com.example.kho_be.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

        // Tìm sản phẩm theo SKU
        Optional<Product> findBySku(String sku);

        // Kiểm tra SKU đã tồn tại chưa (trừ product hiện tại khi update)
        @Query("SELECT COUNT(p) FROM Product p WHERE p.sku = :sku AND p.id != :id")
        long countBySkuAndIdNot(@Param("sku") String sku, @Param("id") Integer id);

        // Tìm kiếm sản phẩm theo tên hoặc SKU
        @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY p.id DESC")
        List<Product> searchProducts(@Param("keyword") String keyword);

        // Tìm sản phẩm theo supplier
        @Query("SELECT p FROM Product p WHERE p.defaultSupplierId = :supplierId ORDER BY p.id DESC")
        List<Product> findByDefaultSupplierId(@Param("supplierId") Integer supplierId);

        // Tìm sản phẩm có tồn kho thấp hơn ngưỡng
        @Query("SELECT p FROM Product p WHERE p.currentStock < :threshold ORDER BY p.currentStock ASC")
        List<Product> findLowStockProducts(@Param("threshold") Integer threshold);

        // Lấy tất cả sản phẩm sắp xếp theo ID giảm dần
        @Query("SELECT p FROM Product p ORDER BY p.id DESC")
        List<Product> findAllOrderByIdDesc();

        // Lấy tất cả sản phẩm có phân trang
        @Query("SELECT p FROM Product p ORDER BY p.id DESC")
        Page<Product> findAllWithPagination(Pageable pageable);

        // Tìm kiếm sản phẩm có phân trang
        @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY p.id DESC")
        Page<Product> searchProductsWithPagination(@Param("keyword") String keyword, Pageable pageable);

        // Tìm sản phẩm theo supplier có phân trang
        @Query("SELECT p FROM Product p WHERE p.defaultSupplierId = :supplierId ORDER BY p.id DESC")
        Page<Product> findByDefaultSupplierIdWithPagination(@Param("supplierId") Integer supplierId, Pageable pageable);

        // Tìm sản phẩm có tồn kho thấp có phân trang
        @Query("SELECT p FROM Product p WHERE p.currentStock <= :threshold ORDER BY p.currentStock ASC")
        Page<Product> findLowStockProductsWithPagination(@Param("threshold") Integer threshold, Pageable pageable);

        // Tìm sản phẩm với nhiều filter kết hợp (supplier + stock range)
        @Query("SELECT p FROM Product p WHERE " +
                        "(:supplierId IS NULL OR p.defaultSupplierId = :supplierId) AND " +
                        "(:minStock IS NULL OR p.currentStock >= :minStock) AND " +
                        "(:maxStock IS NULL OR p.currentStock <= :maxStock)")
        Page<Product> findProductsWithCombinedFilters(
                        @Param("supplierId") Integer supplierId,
                        @Param("minStock") Integer minStock,
                        @Param("maxStock") Integer maxStock,
                        Pageable pageable);

        // Tìm kiếm sản phẩm với nhiều filter kết hợp (keyword + supplier + stock range)
        @Query("SELECT p FROM Product p WHERE " +
                        "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
                        +
                        "(:supplierId IS NULL OR p.defaultSupplierId = :supplierId) AND " +
                        "(:minStock IS NULL OR p.currentStock >= :minStock) AND " +
                        "(:maxStock IS NULL OR p.currentStock <= :maxStock)")
        Page<Product> findProductsWithAllFilters(
                        @Param("keyword") String keyword,
                        @Param("supplierId") Integer supplierId,
                        @Param("minStock") Integer minStock,
                        @Param("maxStock") Integer maxStock,
                        Pageable pageable);

        // Cập nhật sản phẩm
        @Modifying
        @Query("UPDATE Product p SET p.sku = :sku, p.name = :name, p.description = :description, " +
                        "p.currentStock = :currentStock, p.imageUrl = :imageUrl, p.defaultSupplierId = :defaultSupplierId "
                        +
                        "WHERE p.id = :id")
        int updateProduct(@Param("id") Integer id,
                        @Param("sku") String sku,
                        @Param("name") String name,
                        @Param("description") String description,
                        @Param("currentStock") Integer currentStock,
                        @Param("imageUrl") String imageUrl,
                        @Param("defaultSupplierId") Integer defaultSupplierId);

        // Cập nhật tồn kho
        @Modifying
        @Query("UPDATE Product p SET p.currentStock = :stock WHERE p.id = :id")
        int updateStock(@Param("id") Integer id, @Param("stock") Integer stock);
}
