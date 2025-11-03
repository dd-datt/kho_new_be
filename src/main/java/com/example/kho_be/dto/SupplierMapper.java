package com.example.kho_be.dto;

import com.example.kho_be.entity.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    public SupplierDTO toDTO(Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        return new SupplierDTO(
                supplier.getId(),
                supplier.getCode(),
                supplier.getName(),
                supplier.getEmail(),
                supplier.getPhoneNumber(),
                supplier.getAddress());
    }

    public Supplier toEntity(SupplierDTO dto) {
        if (dto == null) {
            return null;
        }
        Supplier supplier = new Supplier();
        supplier.setId(dto.getId());
        supplier.setCode(dto.getCode());
        supplier.setName(dto.getName());
        supplier.setEmail(dto.getEmail());
        supplier.setPhoneNumber(dto.getPhoneNumber());
        supplier.setAddress(dto.getAddress());
        return supplier;
    }

    public void updateEntityFromDTO(SupplierDTO dto, Supplier supplier) {
        if (dto == null || supplier == null) {
            return;
        }
        supplier.setCode(dto.getCode());
        supplier.setName(dto.getName());
        supplier.setEmail(dto.getEmail());
        supplier.setPhoneNumber(dto.getPhoneNumber());
        supplier.setAddress(dto.getAddress());
    }
}
