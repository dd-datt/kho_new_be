package com.example.kho_be.controller;

import com.example.kho_be.dto.SupplierDTO;
import com.example.kho_be.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/suppliers")
@Tag(name = "Supplier Management", description = "APIs quản lý nhà cung cấp")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả nhà cung cấp")
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers() {
        List<SupplierDTO> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin nhà cung cấp theo ID")
    public ResponseEntity<?> getSupplierById(@PathVariable Integer id) {
        try {
            SupplierDTO supplier = supplierService.getSupplierById(id);
            return ResponseEntity.ok(supplier);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Tạo mới nhà cung cấp")
    public ResponseEntity<?> createSupplier(@Valid @RequestBody SupplierDTO supplierDTO) {
        try {
            SupplierDTO createdSupplier = supplierService.createSupplier(supplierDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSupplier);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin nhà cung cấp")
    public ResponseEntity<?> updateSupplier(
            @PathVariable Integer id,
            @Valid @RequestBody SupplierDTO supplierDTO) {
        try {
            SupplierDTO updatedSupplier = supplierService.updateSupplier(id, supplierDTO);
            return ResponseEntity.ok(updatedSupplier);
        } catch (RuntimeException e) {
            HttpStatus status = e.getMessage().contains("Không tìm thấy")
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa nhà cung cấp")
    public ResponseEntity<?> deleteSupplier(@PathVariable Integer id) {
        try {
            supplierService.deleteSupplier(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Xóa nhà cung cấp thành công");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm nhà cung cấp", description = "Tìm kiếm theo tên, mã, email hoặc số điện thoại")
    public ResponseEntity<List<SupplierDTO>> searchSuppliers(
            @RequestParam(required = false) String keyword) {
        List<SupplierDTO> suppliers = supplierService.searchSuppliers(keyword);
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/check-code")
    @Operation(summary = "Kiểm tra mã nhà cung cấp đã tồn tại")
    public ResponseEntity<Map<String, Boolean>> checkCodeExists(@RequestParam String code) {
        boolean exists = supplierService.existsByCode(code);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-email")
    @Operation(summary = "Kiểm tra email đã tồn tại")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam String email) {
        boolean exists = supplierService.existsByEmail(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
