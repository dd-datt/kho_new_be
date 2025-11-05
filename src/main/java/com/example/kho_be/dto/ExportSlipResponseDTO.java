package com.example.kho_be.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ExportSlipResponseDTO {

    private Integer id;
    private LocalDate exportDate;
    private Integer userId;
    private String username;
    private String reason;
    private LocalDateTime createdAt;
    private List<ExportSlipDetailDTO> details;

    // Constructors
    public ExportSlipResponseDTO() {
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getExportDate() {
        return exportDate;
    }

    public void setExportDate(LocalDate exportDate) {
        this.exportDate = exportDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<ExportSlipDetailDTO> getDetails() {
        return details;
    }

    public void setDetails(List<ExportSlipDetailDTO> details) {
        this.details = details;
    }
}
