package com.example.kho_be.controller;

import com.example.kho_be.dto.ImportSlipRequestDTO;
import com.example.kho_be.dto.ImportSlipResponseDTO;
import com.example.kho_be.security.CustomUserDetails;
import com.example.kho_be.service.ImportSlipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
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

import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/import-slips")
@Tag(name = "Quản lý Phiếu nhập", description = "APIs quản lý phiếu nhập")
@SecurityRequirement(name = "bearerAuth")
public class ImportSlipController {

    @Autowired
    private ImportSlipService importSlipService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
    @Operation(summary = "Lấy tất cả phiếu nhập (có phân trang)", description = "Truy xuất danh sách phiếu nhập với phân trang và sắp xếp")
    public ResponseEntity<Page<ImportSlipResponseDTO>> getAllImportSlips(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ImportSlipResponseDTO> importSlips = importSlipService.getAllImportSlips(pageable);
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

    @GetMapping("/{id}/export-pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
    @Operation(summary = "Xuất PDF phiếu nhập", description = "Xuất phiếu nhập thành file PDF")
    public void exportImportSlipPdf(@PathVariable Integer id, HttpServletResponse response) throws Exception {
        // Lấy dữ liệu phiếu nhập từ service
        ImportSlipResponseDTO slip = importSlipService.getImportSlipById(id);

        // Chuẩn bị parameters cho báo cáo
        Map<String, Object> params = new HashMap<>();
        params.put("id", slip.getId() != null ? slip.getId().toString() : "");
        params.put("supplierName", slip.getSupplierName() != null ? slip.getSupplierName() : "");
        params.put("supplierCode", slip.getSupplierCode() != null ? slip.getSupplierCode() : "");
        params.put("importDate", slip.getImportDate() != null ? java.sql.Date.valueOf(slip.getImportDate()) : null);
        params.put("createdAt", slip.getCreatedAt() != null ? java.sql.Timestamp.valueOf(slip.getCreatedAt()) : null);
        params.put("username", slip.getUsername() != null ? slip.getUsername() : "");
        params.put("reason", slip.getReason() != null ? slip.getReason() : "");
        params.put("totalAmount", slip.getTotalAmount() != null ? slip.getTotalAmount() : java.math.BigDecimal.ZERO);

        // Chuẩn bị data source cho bảng chi tiết
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(slip.getDetails());

        // Load file jasper (nếu chưa có thì compile từ jrxml)
        InputStream jasperStream = getClass().getResourceAsStream("/reports/phieu_nhap.jasper");
        JasperReport jasperReport;

        if (jasperStream == null) {
            // Nếu chưa có file .jasper, compile từ file .jrxml
            InputStream jrxmlStream = getClass().getResourceAsStream("/reports/phieu_nhap.jrxml");
            if (jrxmlStream == null) {
                throw new RuntimeException(
                        "Không tìm thấy file báo cáo phieu_nhap.jrxml hoặc phieu_nhap.jasper trong thư mục resources/reports/");
            }
            jasperReport = JasperCompileManager.compileReport(jrxmlStream);
        } else {
            jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
        }

        // Fill report với dữ liệu
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

        // Xuất PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=phieu_nhap_" + slip.getId() + ".pdf");
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }
}