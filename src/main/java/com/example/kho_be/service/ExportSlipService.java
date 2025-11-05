package com.example.kho_be.service;

import com.example.kho_be.dto.ExportSlipDetailDTO;
import com.example.kho_be.dto.ExportSlipRequestDTO;
import com.example.kho_be.dto.ExportSlipResponseDTO;
import com.example.kho_be.entity.*;
import com.example.kho_be.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExportSlipService {

    @Autowired
    private ExportSlipRepository exportSlipRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Lấy tất cả phiếu xuất với phân trang
     */
    public Page<ExportSlipResponseDTO> getAllExportSlips(Pageable pageable) {
        Page<ExportSlip> exportSlips = exportSlipRepository.findAll(pageable);
        return exportSlips.map(this::convertToResponseDTO);
    }

    /**
     * Lấy tất cả phiếu xuất (không phân trang)
     */
    public List<ExportSlipResponseDTO> getAllExportSlipsWithoutPaging() {
        List<ExportSlip> exportSlips = exportSlipRepository.findAll();
        return exportSlips.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy phiếu xuất theo ID
     */
    public ExportSlipResponseDTO getExportSlipById(Integer id) {
        ExportSlip exportSlip = exportSlipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phiếu xuất với id: " + id));
        return convertToResponseDTO(exportSlip);
    }

    /**
     * Tạo phiếu xuất mới
     */
    @Transactional
    public ExportSlipResponseDTO createExportSlip(ExportSlipRequestDTO requestDTO, Integer userId) {
        // Tạo phiếu xuất
        ExportSlip exportSlip = new ExportSlip();
        exportSlip.setExportDate(requestDTO.getExportDate());
        exportSlip.setUserId(userId);
        exportSlip.setReason(requestDTO.getReason());

        // Tạo chi tiết phiếu xuất
        for (ExportSlipDetailDTO detailDTO : requestDTO.getDetails()) {
            // Kiểm tra sản phẩm tồn tại
            Product product = productRepository.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Không tìm thấy sản phẩm với id: " + detailDTO.getProductId()));

            // Kiểm tra số lượng tồn kho
            if (product.getCurrentStock() < detailDTO.getQuantity()) {
                throw new RuntimeException(
                        "Số lượng tồn kho không đủ cho sản phẩm " + product.getName() +
                                ". Tồn kho hiện tại: " + product.getCurrentStock() +
                                ", Yêu cầu: " + detailDTO.getQuantity());
            }

            ExportSlipDetail detail = new ExportSlipDetail();
            detail.setProductId(detailDTO.getProductId());
            detail.setQuantity(detailDTO.getQuantity());

            exportSlip.addDetail(detail);

            // Cập nhật tồn kho sản phẩm (giảm tồn kho)
            product.setCurrentStock(product.getCurrentStock() - detailDTO.getQuantity());
            productRepository.save(product);
        }

        ExportSlip savedExportSlip = exportSlipRepository.save(exportSlip);
        return convertToResponseDTO(savedExportSlip);
    }

    /**
     * Cập nhật phiếu xuất
     */
    @Transactional
    public ExportSlipResponseDTO updateExportSlip(Integer id, ExportSlipRequestDTO requestDTO) {
        ExportSlip exportSlip = exportSlipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phiếu xuất với id: " + id));

        // Hoàn trả lại số lượng tồn kho cũ
        for (ExportSlipDetail oldDetail : exportSlip.getDetails()) {
            Product product = productRepository.findById(oldDetail.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm"));
            product.setCurrentStock(product.getCurrentStock() + oldDetail.getQuantity());
            productRepository.save(product);
        }

        // Xóa chi tiết cũ
        exportSlip.getDetails().clear();

        // Cập nhật các trường của phiếu xuất
        exportSlip.setExportDate(requestDTO.getExportDate());
        exportSlip.setReason(requestDTO.getReason());

        // Thêm chi tiết mới
        for (ExportSlipDetailDTO detailDTO : requestDTO.getDetails()) {
            Product product = productRepository.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Không tìm thấy sản phẩm với id: " + detailDTO.getProductId()));

            // Kiểm tra số lượng tồn kho
            if (product.getCurrentStock() < detailDTO.getQuantity()) {
                throw new RuntimeException(
                        "Số lượng tồn kho không đủ cho sản phẩm " + product.getName() +
                                ". Tồn kho hiện tại: " + product.getCurrentStock() +
                                ", Yêu cầu: " + detailDTO.getQuantity());
            }

            ExportSlipDetail detail = new ExportSlipDetail();
            detail.setProductId(detailDTO.getProductId());
            detail.setQuantity(detailDTO.getQuantity());

            exportSlip.addDetail(detail);

            // Cập nhật tồn kho sản phẩm với số lượng mới (giảm tồn kho)
            product.setCurrentStock(product.getCurrentStock() - detailDTO.getQuantity());
            productRepository.save(product);
        }

        ExportSlip updatedExportSlip = exportSlipRepository.save(exportSlip);
        return convertToResponseDTO(updatedExportSlip);
    }

    /**
     * Xóa phiếu xuất
     */
    @Transactional
    public void deleteExportSlip(Integer id) {
        ExportSlip exportSlip = exportSlipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phiếu xuất với id: " + id));

        // Hoàn trả lại số lượng tồn kho
        for (ExportSlipDetail detail : exportSlip.getDetails()) {
            Product product = productRepository.findById(detail.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm"));
            product.setCurrentStock(product.getCurrentStock() + detail.getQuantity());
            productRepository.save(product);
        }

        exportSlipRepository.delete(exportSlip);
    }

    /**
     * Lọc phiếu xuất theo tháng và năm
     */
    public List<ExportSlipResponseDTO> filterByMonthAndYear(Integer month, Integer year) {
        List<ExportSlip> exportSlips = exportSlipRepository.findByMonthAndYear(month, year);
        return exportSlips.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lọc phiếu xuất theo năm
     */
    public List<ExportSlipResponseDTO> filterByYear(Integer year) {
        List<ExportSlip> exportSlips = exportSlipRepository.findByYear(year);
        return exportSlips.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Tìm kiếm phiếu xuất theo lý do (reason)
     */
    public List<ExportSlipResponseDTO> searchByReason(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllExportSlipsWithoutPaging();
        }
        List<ExportSlip> exportSlips = exportSlipRepository.searchByReason(keyword.trim());
        return exportSlips.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Tìm kiếm và lọc phiếu xuất với nhiều tiêu chí
     */
    public List<ExportSlipResponseDTO> searchAndFilter(Integer month, Integer year,
            LocalDate startDate, LocalDate endDate, String reason) {
        Specification<ExportSlip> spec = (root, query, cb) -> cb.conjunction();

        if (month != null && year != null) {
            spec = spec.and((root, query, cb) -> cb.and(
                    cb.equal(cb.function("MONTH", Integer.class, root.get("exportDate")), month),
                    cb.equal(cb.function("YEAR", Integer.class, root.get("exportDate")), year)));
        } else if (year != null) {
            spec = spec.and(
                    (root, query, cb) -> cb.equal(cb.function("YEAR", Integer.class, root.get("exportDate")), year));
        }

        if (startDate != null && endDate != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("exportDate"), startDate, endDate));
        }

        if (reason != null && !reason.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("reason")),
                    "%" + reason.trim().toLowerCase() + "%"));
        }

        List<ExportSlip> exportSlips = exportSlipRepository.findAll(spec);
        return exportSlips.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi Entity sang Response DTO
     */
    private ExportSlipResponseDTO convertToResponseDTO(ExportSlip exportSlip) {
        ExportSlipResponseDTO responseDTO = new ExportSlipResponseDTO();
        responseDTO.setId(exportSlip.getId());
        responseDTO.setExportDate(exportSlip.getExportDate());
        responseDTO.setUserId(exportSlip.getUserId());
        responseDTO.setReason(exportSlip.getReason());
        responseDTO.setCreatedAt(exportSlip.getCreatedAt());

        // Lấy thông tin người dùng
        if (exportSlip.getUserId() != null) {
            userRepository.findById(exportSlip.getUserId()).ifPresent(user -> {
                responseDTO.setUsername(user.getUsername());
            });
        }

        // Chuyển đổi chi tiết
        List<ExportSlipDetailDTO> detailDTOs = new ArrayList<>();

        for (ExportSlipDetail detail : exportSlip.getDetails()) {
            ExportSlipDetailDTO detailDTO = new ExportSlipDetailDTO();
            detailDTO.setId(detail.getId());
            detailDTO.setProductId(detail.getProductId());
            detailDTO.setQuantity(detail.getQuantity());

            // Lấy thông tin sản phẩm
            productRepository.findById(detail.getProductId()).ifPresent(product -> {
                detailDTO.setProductSku(product.getSku());
                detailDTO.setProductName(product.getName());
            });

            detailDTOs.add(detailDTO);
        }

        responseDTO.setDetails(detailDTOs);

        return responseDTO;
    }
}
