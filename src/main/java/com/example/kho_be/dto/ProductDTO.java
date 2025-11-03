package com.example.kho_be.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProductDTO {

    private Integer id;

    @NotBlank(message = "Mã sản phẩm (SKU) không được để trống")
    @Pattern(regexp = "^[A-Z0-9\\-]+$", message = "Mã sản phẩm (SKU) chỉ được chứa chữ in hoa, số và dấu gạch ngang (VD: SP001, LAPTOP-DELL-123)")
    @Size(max = 100, message = "Mã sản phẩm không được vượt quá 100 ký tự")
    private String sku;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255, message = "Tên sản phẩm không được vượt quá 255 ký tự")
    private String name;

    private String description;

    @Min(value = 0, message = "Số lượng tồn kho không thể âm")
    private Integer currentStock;

    @Size(max = 255, message = "URL hình ảnh không được vượt quá 255 ký tự")
    private String imageUrl;

    private Integer defaultSupplierId;

    // Để hiển thị thông tin supplier (không cần validate)
    private String defaultSupplierName;

    // Constructors
    public ProductDTO() {
        this.currentStock = 0;
    }

    public ProductDTO(Integer id, String sku, String name, String description, Integer currentStock,
            String imageUrl, Integer defaultSupplierId, String defaultSupplierName) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.currentStock = currentStock != null ? currentStock : 0;
        this.imageUrl = imageUrl;
        this.defaultSupplierId = defaultSupplierId;
        this.defaultSupplierName = defaultSupplierName;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getDefaultSupplierId() {
        return defaultSupplierId;
    }

    public void setDefaultSupplierId(Integer defaultSupplierId) {
        this.defaultSupplierId = defaultSupplierId;
    }

    public String getDefaultSupplierName() {
        return defaultSupplierName;
    }

    public void setDefaultSupplierName(String defaultSupplierName) {
        this.defaultSupplierName = defaultSupplierName;
    }
}
