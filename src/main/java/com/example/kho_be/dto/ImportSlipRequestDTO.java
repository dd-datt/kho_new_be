package com.example.kho_be.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class ImportSlipRequestDTO {

    @NotNull(message = "Ngày nhập không được để trống")
    private LocalDate importDate;

    private Integer supplierId;

    private String reason;

    @NotEmpty(message = "Chi tiết phiếu nhập không được để trống")
    @Valid
    private List<ImportSlipDetailDTO> details;

    // Constructors
    public ImportSlipRequestDTO() {
    }

    public ImportSlipRequestDTO(LocalDate importDate, Integer supplierId, String reason,
                                List<ImportSlipDetailDTO> details) {
        this.importDate = importDate;
        this.supplierId = supplierId;
        this.reason = reason;
        this.details = details;
    }

    // Getters and Setters
    public LocalDate getImportDate() {
        return importDate;
    }

    public void setImportDate(LocalDate importDate) {
        this.importDate = importDate;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<ImportSlipDetailDTO> getDetails() {
        return details;
    }

    public void setDetails(List<ImportSlipDetailDTO> details) {
        this.details = details;
    }
}