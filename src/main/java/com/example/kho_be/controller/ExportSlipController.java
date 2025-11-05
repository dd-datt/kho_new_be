package com.example.kho_be.controller;

import com.example.kho_be.dto.ExportSlipRequestDTO;
import com.example.kho_be.dto.ExportSlipResponseDTO;
import com.example.kho_be.security.CustomUserDetails;
import com.example.kho_be.service.ExportSlipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/export-slips")
@Tag(name = "Quản lý Phiếu xuất", description = "APIs quản lý phiếu xuất")
@SecurityRequirement(name = "bearerAuth")
public class ExportSlipController {

    @Autowired
    private ExportSlipService exportSlipService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
    @Operation(summary = "Lấy tất cả phiếu xuất (có phân trang)", description = "Truy xuất danh sách phiếu xuất với phân trang và sắp xếp")
    public ResponseEntity<Page<ExportSlipResponseDTO>> getAllExportSlips(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ExportSlipResponseDTO> exportSlips = exportSlipService.getAllExportSlips(pageable);
        return ResponseEntity.ok(exportSlips);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
    @Operation(summary = "Lấy phiếu xuất theo ID", description = "Truy xuất một phiếu xuất cụ thể bằng ID")
    public ResponseEntity<ExportSlipResponseDTO> getExportSlipById(@PathVariable Integer id) {
        ExportSlipResponseDTO exportSlip = exportSlipService.getExportSlipById(id);
        return ResponseEntity.ok(exportSlip);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF')")
    @Operation(summary = "Tạo phiếu xuất mới", description = "Tạo một phiếu xuất mới")
    public ResponseEntity<ExportSlipResponseDTO> createExportSlip(
            @Valid @RequestBody ExportSlipRequestDTO requestDTO,
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer userId = userDetails.getUserId();

        ExportSlipResponseDTO createdExportSlip = exportSlipService.createExportSlip(requestDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExportSlip);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF')")
    @Operation(summary = "Cập nhật phiếu xuất", description = "Cập nhật một phiếu xuất đã tồn tại")
    public ResponseEntity<ExportSlipResponseDTO> updateExportSlip(
            @PathVariable Integer id,
            @Valid @RequestBody ExportSlipRequestDTO requestDTO) {
        ExportSlipResponseDTO updatedExportSlip = exportSlipService.updateExportSlip(id, requestDTO);
        return ResponseEntity.ok(updatedExportSlip);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa phiếu xuất", description = "Xóa một phiếu xuất bằng ID")
    public ResponseEntity<Void> deleteExportSlip(@PathVariable Integer id) {
        exportSlipService.deleteExportSlip(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter/by-month")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Lọc theo tháng và năm", description = "Lấy danh sách phiếu xuất được lọc theo tháng và năm")
    public ResponseEntity<List<ExportSlipResponseDTO>> filterByMonthAndYear(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        List<ExportSlipResponseDTO> exportSlips = exportSlipService.filterByMonthAndYear(month, year);
        return ResponseEntity.ok(exportSlips);
    }

    @GetMapping("/filter/by-year")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Lọc theo năm", description = "Lấy danh sách phiếu xuất được lọc theo năm")
    public ResponseEntity<List<ExportSlipResponseDTO>> filterByYear(@RequestParam Integer year) {
        List<ExportSlipResponseDTO> exportSlips = exportSlipService.filterByYear(year);
        return ResponseEntity.ok(exportSlips);
    }

    @GetMapping("/search/by-reason")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
    @Operation(summary = "Tìm kiếm theo lý do", description = "Tìm kiếm phiếu xuất theo lý do (reason)")
    public ResponseEntity<List<ExportSlipResponseDTO>> searchByReason(
            @RequestParam String keyword) {
        List<ExportSlipResponseDTO> exportSlips = exportSlipService.searchByReason(keyword);
        return ResponseEntity.ok(exportSlips);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
    @Operation(summary = "Tìm kiếm và lọc nâng cao", description = "Tìm kiếm và lọc phiếu xuất với nhiều tiêu chí")
    public ResponseEntity<List<ExportSlipResponseDTO>> searchAndFilter(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String reason) {
        List<ExportSlipResponseDTO> exportSlips = exportSlipService.searchAndFilter(
                month, year, startDate, endDate, reason);
        return ResponseEntity.ok(exportSlips);
    }
}
