package com.example.kho_be.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class ExportSlipRequestDTO {

    @NotNull(message = "Ngày xuất không được để trống")
    private LocalDate exportDate;

    private String reason;

    @NotEmpty(message = "Chi tiết phiếu xuất không được để trống")
    @Valid
    private List<ExportSlipDetailDTO> details;

    // Constructors
    public ExportSlipRequestDTO() {
    }

    public ExportSlipRequestDTO(LocalDate exportDate, String reason, List<ExportSlipDetailDTO> details) {
        this.exportDate = exportDate;
        this.reason = reason;
        this.details = details;
    }

    // Getters and Setters
    public LocalDate getExportDate() {
        return exportDate;
    }

    public void setExportDate(LocalDate exportDate) {
        this.exportDate = exportDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<ExportSlipDetailDTO> getDetails() {
        return details;
    }

    public void setDetails(List<ExportSlipDetailDTO> details) {
        this.details = details;
    }
}
