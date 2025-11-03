package com.example.kho_be.dto;

import com.example.kho_be.entity.Product;

public class ProductMapper {

    public static ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }
        return new ProductDTO(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getCurrentStock(),
                product.getImageUrl(),
                product.getDefaultSupplierId(),
                null // defaultSupplierName sẽ được set sau nếu cần
        );
    }

    public static Product toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }
        Product product = new Product();
        product.setId(dto.getId());
        product.setSku(dto.getSku());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setCurrentStock(dto.getCurrentStock() != null ? dto.getCurrentStock() : 0);
        product.setImageUrl(dto.getImageUrl());
        product.setDefaultSupplierId(dto.getDefaultSupplierId());
        return product;
    }

    public static void updateEntityFromDTO(ProductDTO dto, Product product) {
        if (dto == null || product == null) {
            return;
        }
        if (dto.getSku() != null) {
            product.setSku(dto.getSku());
        }
        if (dto.getName() != null) {
            product.setName(dto.getName());
        }
        product.setDescription(dto.getDescription());
        if (dto.getCurrentStock() != null) {
            product.setCurrentStock(dto.getCurrentStock());
        }
        product.setImageUrl(dto.getImageUrl());
        product.setDefaultSupplierId(dto.getDefaultSupplierId());
    }
}
