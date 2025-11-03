package com.example.kho_be.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "current_stock", nullable = false)
    private Integer currentStock = 0;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "default_supplier_id")
    private Integer defaultSupplierId;

    // Constructors
    public Product() {
    }

    public Product(String sku, String name, String description, Integer currentStock, String imageUrl,
            Integer defaultSupplierId) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.currentStock = currentStock != null ? currentStock : 0;
        this.imageUrl = imageUrl;
        this.defaultSupplierId = defaultSupplierId;
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
}
