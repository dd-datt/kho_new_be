package com.example.kho_be.service;

import com.example.kho_be.dto.ImportSlipDetailDTO;
import com.example.kho_be.dto.ImportSlipRequestDTO;
import com.example.kho_be.dto.ImportSlipResponseDTO;
import com.example.kho_be.entity.*;
import com.example.kho_be.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImportSlipService {

    @Autowired
    private ImportSlipRepository importSlipRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Lấy tất cả phiếu nhập
     */
    public List<ImportSlipResponseDTO> getAllImportSlips() {
        List<ImportSlip> importSlips = importSlipRepository.findAll();
        return importSlips.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy phiếu nhập theo ID
     */
    public ImportSlipResponseDTO getImportSlipById(Integer id) {
        ImportSlip importSlip = importSlipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phiếu nhập với id: " + id));
        return convertToResponseDTO(importSlip);
    }

    /**
     * Tạo phiếu nhập mới
     */
    @Transactional
    public ImportSlipResponseDTO createImportSlip(ImportSlipRequestDTO requestDTO, Integer userId) {
        // Kiểm tra nhà cung cấp nếu có
        if (requestDTO.getSupplierId() != null) {
            supplierRepository.findById(requestDTO.getSupplierId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Không tìm thấy nhà cung cấp với id: " + requestDTO.getSupplierId()));
        }

        // Tạo phiếu nhập
        ImportSlip importSlip = new ImportSlip();
        importSlip.setImportDate(requestDTO.getImportDate());
        importSlip.setSupplierId(requestDTO.getSupplierId());
        importSlip.setUserId(userId);
        importSlip.setReason(requestDTO.getReason());

        // Tạo chi tiết phiếu nhập
        for (ImportSlipDetailDTO detailDTO : requestDTO.getDetails()) {
            // Kiểm tra sản phẩm tồn tại
            Product product = productRepository.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Không tìm thấy sản phẩm với id: " + detailDTO.getProductId()));

            ImportSlipDetail detail = new ImportSlipDetail();
            detail.setProductId(detailDTO.getProductId());
            detail.setQuantity(detailDTO.getQuantity());
            detail.setImportPrice(detailDTO.getImportPrice());

            importSlip.addDetail(detail);

            // Cập nhật tồn kho sản phẩm
            product.setCurrentStock(product.getCurrentStock() + detailDTO.getQuantity());
            productRepository.save(product);
        }

        ImportSlip savedImportSlip = importSlipRepository.save(importSlip);
        return convertToResponseDTO(savedImportSlip);
    }

    /**
     * Cập nhật phiếu nhập
     */
    @Transactional
    public ImportSlipResponseDTO updateImportSlip(Integer id, ImportSlipRequestDTO requestDTO) {
        ImportSlip importSlip = importSlipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phiếu nhập với id: " + id));

        // Kiểm tra nhà cung cấp nếu có
        if (requestDTO.getSupplierId() != null) {
            supplierRepository.findById(requestDTO.getSupplierId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Không tìm thấy nhà cung cấp với id: " + requestDTO.getSupplierId()));
        }

        // Hoàn trả lại số lượng tồn kho cũ
        for (ImportSlipDetail oldDetail : importSlip.getDetails()) {
            Product product = productRepository.findById(oldDetail.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm"));
            product.setCurrentStock(product.getCurrentStock() - oldDetail.getQuantity());
            productRepository.save(product);
        }

        // Xóa chi tiết cũ
        importSlip.getDetails().clear();

        // Cập nhật các trường của phiếu nhập
        importSlip.setImportDate(requestDTO.getImportDate());
        importSlip.setSupplierId(requestDTO.getSupplierId());
        importSlip.setReason(requestDTO.getReason());

        // Thêm chi tiết mới
        for (ImportSlipDetailDTO detailDTO : requestDTO.getDetails()) {
            Product product = productRepository.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Không tìm thấy sản phẩm với id: " + detailDTO.getProductId()));

            ImportSlipDetail detail = new ImportSlipDetail();
            detail.setProductId(detailDTO.getProductId());
            detail.setQuantity(detailDTO.getQuantity());
            detail.setImportPrice(detailDTO.getImportPrice());

            importSlip.addDetail(detail);

            // Cập nhật tồn kho sản phẩm với số lượng mới
            product.setCurrentStock(product.getCurrentStock() + detailDTO.getQuantity());
            productRepository.save(product);
        }

        ImportSlip updatedImportSlip = importSlipRepository.save(importSlip);
        return convertToResponseDTO(updatedImportSlip);
    }

    /**
     * Xóa phiếu nhập
     */
    @Transactional
    public void deleteImportSlip(Integer id) {
        ImportSlip importSlip = importSlipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phiếu nhập với id: " + id));

        // Hoàn trả lại số lượng tồn kho
        for (ImportSlipDetail detail : importSlip.getDetails()) {
            Product product = productRepository.findById(detail.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm"));
            product.setCurrentStock(product.getCurrentStock() - detail.getQuantity());
            productRepository.save(product);
        }

        importSlipRepository.delete(importSlip);
    }

    /**
     * Tìm kiếm phiếu nhập theo supplier
     */
    public List<ImportSlipResponseDTO> searchBySupplier(Integer supplierId) {
        List<ImportSlip> importSlips = importSlipRepository.findBySupplierId(supplierId);
        return importSlips.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lọc phiếu nhập theo tháng và năm
     */
    public List<ImportSlipResponseDTO> filterByMonthAndYear(Integer month, Integer year) {
        List<ImportSlip> importSlips = importSlipRepository.findByMonthAndYear(month, year);
        return importSlips.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lọc phiếu nhập theo năm
     */
    public List<ImportSlipResponseDTO> filterByYear(Integer year) {
        List<ImportSlip> importSlips = importSlipRepository.findByYear(year);
        return importSlips.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Tìm kiếm và lọc phiếu nhập với nhiều tiêu chí
     */
    public List<ImportSlipResponseDTO> searchAndFilter(Integer supplierId, Integer month, Integer year,
                                                       LocalDate startDate, LocalDate endDate) {
        Specification<ImportSlip> spec = (root, query, cb) -> cb.conjunction();

        if (supplierId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("supplierId"), supplierId));
        }

        if (month != null && year != null) {
            spec = spec.and((root, query, cb) -> cb.and(
                    cb.equal(cb.function("MONTH", Integer.class, root.get("importDate")), month),
                    cb.equal(cb.function("YEAR", Integer.class, root.get("importDate")), year)));
        } else if (year != null) {
            spec = spec.and(
                    (root, query, cb) -> cb.equal(cb.function("YEAR", Integer.class, root.get("importDate")), year));
        }

        if (startDate != null && endDate != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("importDate"), startDate, endDate));
        }

        List<ImportSlip> importSlips = importSlipRepository.findAll(spec);
        return importSlips.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi Entity sang Response DTO
     */
    private ImportSlipResponseDTO convertToResponseDTO(ImportSlip importSlip) {
        ImportSlipResponseDTO responseDTO = new ImportSlipResponseDTO();
        responseDTO.setId(importSlip.getId());
        responseDTO.setImportDate(importSlip.getImportDate());
        responseDTO.setSupplierId(importSlip.getSupplierId());
        responseDTO.setUserId(importSlip.getUserId());
        responseDTO.setReason(importSlip.getReason());
        responseDTO.setCreatedAt(importSlip.getCreatedAt());

        // Lấy thông tin nhà cung cấp
        if (importSlip.getSupplierId() != null) {
            supplierRepository.findById(importSlip.getSupplierId()).ifPresent(supplier -> {
                responseDTO.setSupplierName(supplier.getName());
                responseDTO.setSupplierCode(supplier.getCode());
            });
        }

        // Lấy thông tin người dùng
        if (importSlip.getUserId() != null) {
            userRepository.findById(importSlip.getUserId()).ifPresent(user -> {
                responseDTO.setUsername(user.getUsername());
            });
        }

        // Chuyển đổi chi tiết
        List<ImportSlipDetailDTO> detailDTOs = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (ImportSlipDetail detail : importSlip.getDetails()) {
            ImportSlipDetailDTO detailDTO = new ImportSlipDetailDTO();
            detailDTO.setId(detail.getId());
            detailDTO.setProductId(detail.getProductId());
            detailDTO.setQuantity(detail.getQuantity());
            detailDTO.setImportPrice(detail.getImportPrice());

            // Lấy thông tin sản phẩm
            productRepository.findById(detail.getProductId()).ifPresent(product -> {
                detailDTO.setProductSku(product.getSku());
                detailDTO.setProductName(product.getName());
            });

            detailDTOs.add(detailDTO);

            // Tính tổng tiền
            totalAmount = totalAmount.add(
                    detail.getImportPrice().multiply(new BigDecimal(detail.getQuantity())));
        }

        responseDTO.setDetails(detailDTOs);
        responseDTO.setTotalAmount(totalAmount);

        return responseDTO;
    }
}