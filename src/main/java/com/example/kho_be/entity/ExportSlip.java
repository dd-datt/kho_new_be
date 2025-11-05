package com.example.kho_be.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "export_slips")
public class ExportSlip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "export_date", nullable = false)
    private LocalDate exportDate;

    @Column(name = "user_id")
    private Integer userId;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "exportSlip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExportSlipDetail> details = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public ExportSlip() {
    }

    public ExportSlip(LocalDate exportDate, Integer userId, String reason) {
        this.exportDate = exportDate;
        this.userId = userId;
        this.reason = reason;
    }

    // Helper methods
    public void addDetail(ExportSlipDetail detail) {
        details.add(detail);
        detail.setExportSlip(this);
    }

    public void removeDetail(ExportSlipDetail detail) {
        details.remove(detail);
        detail.setExportSlip(null);
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

    public List<ExportSlipDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ExportSlipDetail> details) {
        this.details = details;
    }
}
