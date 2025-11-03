package com.example.kho_be.service;

import com.example.kho_be.dto.ProductDTO;
import com.example.kho_be.dto.ProductMapper;
import com.example.kho_be.entity.Product;
import com.example.kho_be.entity.Supplier;
import com.example.kho_be.repository.ProductRepository;
import com.example.kho_be.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    // Lấy tất cả sản phẩm
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAllOrderByIdDesc();
        return convertToProductDTOList(products);
    }

    // Lấy sản phẩm theo ID
    public ProductDTO getProductById(Integer id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            return convertToProductDTO(product.get());
        }
        throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + id);
    }

    // Tìm kiếm sản phẩm
    public List<ProductDTO> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        List<Product> products = productRepository.searchProducts(keyword.trim());
        return convertToProductDTOList(products);
    }

    // Tìm sản phẩm theo supplier
    public List<ProductDTO> getProductsBySupplier(Integer supplierId) {
        List<Product> products = productRepository.findByDefaultSupplierId(supplierId);
        return convertToProductDTOList(products);
    }

    // Tìm sản phẩm có tồn kho thấp
    public List<ProductDTO> getLowStockProducts(Integer threshold) {
        List<Product> products = productRepository.findLowStockProducts(threshold);
        return convertToProductDTOList(products);
    }

    // Thêm sản phẩm mới
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        // Kiểm tra SKU đã tồn tại
        Optional<Product> existingSku = productRepository.findBySku(productDTO.getSku());
        if (existingSku.isPresent()) {
            throw new RuntimeException("Mã sản phẩm (SKU) đã tồn tại: " + productDTO.getSku());
        }

        // Kiểm tra supplier nếu có
        if (productDTO.getDefaultSupplierId() != null) {
            if (!supplierRepository.existsById(productDTO.getDefaultSupplierId())) {
                throw new RuntimeException("Không tìm thấy nhà cung cấp với ID: " + productDTO.getDefaultSupplierId());
            }
        }

        // Thêm sản phẩm mới bằng JPA save
        Product product = ProductMapper.toEntity(productDTO);
        Product saved = productRepository.save(product);

        return convertToProductDTO(saved);
    }

    // Cập nhật sản phẩm
    @Transactional
    public ProductDTO updateProduct(Integer id, ProductDTO productDTO) {
        // Kiểm tra sản phẩm tồn tại
        Optional<Product> existing = productRepository.findById(id);
        if (!existing.isPresent()) {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + id);
        }

        // Kiểm tra SKU trùng (trừ chính nó)
        long duplicateCount = productRepository.countBySkuAndIdNot(productDTO.getSku(), id);
        if (duplicateCount > 0) {
            throw new RuntimeException("Mã sản phẩm (SKU) đã tồn tại: " + productDTO.getSku());
        }

        // Kiểm tra supplier nếu có
        if (productDTO.getDefaultSupplierId() != null) {
            if (!supplierRepository.existsById(productDTO.getDefaultSupplierId())) {
                throw new RuntimeException("Không tìm thấy nhà cung cấp với ID: " + productDTO.getDefaultSupplierId());
            }
        }

        // Cập nhật sản phẩm
        int updated = productRepository.updateProduct(
                id,
                productDTO.getSku(),
                productDTO.getName(),
                productDTO.getDescription(),
                productDTO.getCurrentStock() != null ? productDTO.getCurrentStock() : 0,
                productDTO.getImageUrl(),
                productDTO.getDefaultSupplierId());

        if (updated > 0) {
            return getProductById(id);
        }
        throw new RuntimeException("Không thể cập nhật sản phẩm");
    }

    // Xóa sản phẩm
    @Transactional
    public void deleteProduct(Integer id) {
        // Kiểm tra sản phẩm tồn tại
        Optional<Product> existing = productRepository.findById(id);
        if (!existing.isPresent()) {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + id);
        }

        // Xóa sản phẩm - Nếu có ràng buộc khóa ngoại (phiếu nhập/xuất) sẽ được xử lý
        // bởi GlobalExceptionHandler
        productRepository.deleteById(id);
    }

    // Cập nhật tồn kho
    @Transactional
    public ProductDTO updateStock(Integer id, Integer stock) {
        if (stock < 0) {
            throw new RuntimeException("Số lượng tồn kho không thể âm");
        }

        int updated = productRepository.updateStock(id, stock);
        if (updated > 0) {
            return getProductById(id);
        }
        throw new RuntimeException("Không thể cập nhật tồn kho");
    }

    // Helper methods để convert Product entity sang ProductDTO
    private ProductDTO convertToProductDTO(Product product) {
        ProductDTO dto = ProductMapper.toDTO(product);

        // Lấy tên supplier nếu có
        if (product.getDefaultSupplierId() != null) {
            Optional<Supplier> supplier = supplierRepository.findById(product.getDefaultSupplierId());
            if (supplier.isPresent()) {
                dto.setDefaultSupplierName(supplier.get().getName());
            }
        }

        return dto;
    }

    private List<ProductDTO> convertToProductDTOList(List<Product> products) {
        return products.stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());
    }
}
