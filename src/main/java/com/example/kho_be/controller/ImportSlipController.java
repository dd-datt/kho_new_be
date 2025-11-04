package com.example.kho_be.controller;

import com.example.kho_be.dto.ImportSlipRequestDTO;
import com.example.kho_be.dto.ImportSlipResponseDTO;
import com.example.kho_be.security.CustomUserDetails;
import com.example.kho_be.service.ImportSlipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/import-slips")
@Tag(name = "Quản lý Phiếu nhập", description = "APIs quản lý phiếu nhập")
@SecurityRequirement(name = "bearerAuth")
public class ImportSlipController {

    @Autowired
    private ImportSlipService importSlipService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
    @Operation(summary = "Lấy tất cả phiếu nhập", description = "Truy xuất danh sách tất cả phiếu nhập")
    public ResponseEntity<List<ImportSlipResponseDTO>> getAllImportSlips() {
        List<ImportSlipResponseDTO> importSlips = importSlipService.getAllImportSlips();
        return ResponseEntity.ok(importSlips);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
    @Operation(summary = "Lấy phiếu nhập theo ID", description = "Truy xuất một phiếu nhập cụ thể bằng ID")
    public ResponseEntity<ImportSlipResponseDTO> getImportSlipById(@PathVariable Integer id) {
        ImportSlipResponseDTO importSlip = importSlipService.getImportSlipById(id);
        return ResponseEntity.ok(importSlip);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF')")
    @Operation(summary = "Tạo phiếu nhập mới", description = "Tạo một phiếu nhập mới")
    public ResponseEntity<ImportSlipResponseDTO> createImportSlip(
            @Valid @RequestBody ImportSlipRequestDTO requestDTO,
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer userId = userDetails.getUserId();

        ImportSlipResponseDTO createdImportSlip = importSlipService.createImportSlip(requestDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdImportSlip);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF')")
    @Operation(summary = "Cập nhật phiếu nhập", description = "Cập nhật một phiếu nhập đã tồn tại")
    public ResponseEntity<ImportSlipResponseDTO> updateImportSlip(
            @PathVariable Integer id,
            @Valid @RequestBody ImportSlipRequestDTO requestDTO) {
        ImportSlipResponseDTO updatedImportSlip = importSlipService.updateImportSlip(id, requestDTO);
        return ResponseEntity.ok(updatedImportSlip);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa phiếu nhập", description = "Xóa một phiếu nhập bằng ID")
    public ResponseEntity<Void> deleteImportSlip(@PathVariable Integer id) {
        importSlipService.deleteImportSlip(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/by-supplier")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
    @Operation(summary = "Tìm kiếm theo nhà cung cấp", description = "Lấy tất cả phiếu nhập của một nhà cung cấp cụ thể")
    public ResponseEntity<List<ImportSlipResponseDTO>> searchBySupplier(
            @RequestParam Integer supplierId) {
        List<ImportSlipResponseDTO> importSlips = importSlipService.searchBySupplier(supplierId);
        return ResponseEntity.ok(importSlips);
    }

    @GetMapping("/filter/by-month")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Lọc theo tháng và năm", description = "Lấy danh sách phiếu nhập được lọc theo tháng và năm")
    public ResponseEntity<List<ImportSlipResponseDTO>> filterByMonthAndYear(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        List<ImportSlipResponseDTO> importSlips = importSlipService.filterByMonthAndYear(month, year);
        return ResponseEntity.ok(importSlips);
    }

    @GetMapping("/filter/by-year")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Lọc theo năm", description = "Lấy danh sách phiếu nhập được lọc theo năm")
    public ResponseEntity<List<ImportSlipResponseDTO>> filterByYear(@RequestParam Integer year) {
        List<ImportSlipResponseDTO> importSlips = importSlipService.filterByYear(year);
        return ResponseEntity.ok(importSlips);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
    @Operation(summary = "Tìm kiếm và lọc nâng cao", description = "Tìm kiếm và lọc phiếu nhập với nhiều tiêu chí")
    public ResponseEntity<List<ImportSlipResponseDTO>> searchAndFilter(
            @RequestParam(required = false) Integer supplierId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ImportSlipResponseDTO> importSlips = importSlipService.searchAndFilter(
                supplierId, month, year, startDate, endDate);
        return ResponseEntity.ok(importSlips);
    }
}