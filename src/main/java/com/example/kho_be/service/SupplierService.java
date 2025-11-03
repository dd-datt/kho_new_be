package com.example.kho_be.service;

import com.example.kho_be.dto.SupplierDTO;
import com.example.kho_be.dto.SupplierMapper;
import com.example.kho_be.entity.Supplier;
import com.example.kho_be.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierMapper supplierMapper;

    // Lấy tất cả nhà cung cấp
    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(supplierMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Lấy nhà cung cấp theo ID
    public SupplierDTO getSupplierById(Integer id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp với ID: " + id));
        return supplierMapper.toDTO(supplier);
    }

    // Tạo mới nhà cung cấp
    @Transactional
    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        // Kiểm tra mã nhà cung cấp đã tồn tại
        if (supplierRepository.existsByCode(supplierDTO.getCode())) {
            throw new RuntimeException("Mã nhà cung cấp đã tồn tại: " + supplierDTO.getCode());
        }

        // Kiểm tra email đã tồn tại (nếu có)
        if (supplierDTO.getEmail() != null && !supplierDTO.getEmail().isEmpty()) {
            if (supplierRepository.existsByEmail(supplierDTO.getEmail())) {
                throw new RuntimeException("Email đã được sử dụng: " + supplierDTO.getEmail());
            }
        }

        Supplier supplier = supplierMapper.toEntity(supplierDTO);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toDTO(savedSupplier);
    }

    // Cập nhật nhà cung cấp
    @Transactional
    public SupplierDTO updateSupplier(Integer id, SupplierDTO supplierDTO) {
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp với ID: " + id));

        // Kiểm tra mã nhà cung cấp nếu thay đổi
        if (!existingSupplier.getCode().equals(supplierDTO.getCode())) {
            if (supplierRepository.existsByCode(supplierDTO.getCode())) {
                throw new RuntimeException("Mã nhà cung cấp đã tồn tại: " + supplierDTO.getCode());
            }
        }

        // Kiểm tra email nếu thay đổi
        if (supplierDTO.getEmail() != null && !supplierDTO.getEmail().isEmpty()) {
            if (!supplierDTO.getEmail().equals(existingSupplier.getEmail())) {
                if (supplierRepository.existsByEmail(supplierDTO.getEmail())) {
                    throw new RuntimeException("Email đã được sử dụng: " + supplierDTO.getEmail());
                }
            }
        }

        supplierMapper.updateEntityFromDTO(supplierDTO, existingSupplier);
        Supplier updatedSupplier = supplierRepository.save(existingSupplier);
        return supplierMapper.toDTO(updatedSupplier);
    }

    // Xóa nhà cung cấp
    @Transactional
    public void deleteSupplier(Integer id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy nhà cung cấp với ID: " + id);
        }
        supplierRepository.deleteById(id);
    }

    // Tìm kiếm nhà cung cấp
    public List<SupplierDTO> searchSuppliers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllSuppliers();
        }
        return supplierRepository.searchSuppliers(keyword.trim()).stream()
                .map(supplierMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Kiểm tra mã nhà cung cấp đã tồn tại
    public boolean existsByCode(String code) {
        return supplierRepository.existsByCode(code);
    }

    // Kiểm tra email đã tồn tại
    public boolean existsByEmail(String email) {
        return supplierRepository.existsByEmail(email);
    }
}
