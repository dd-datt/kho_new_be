package com.example.kho_be.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "import_slips")
public class ImportSlip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "import_date", nullable = false)
    private LocalDate importDate;

    @Column(name = "supplier_id")
    private Integer supplierId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "importSlip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImportSlipDetail> details = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public ImportSlip() {
    }

    public ImportSlip(LocalDate importDate, Integer supplierId, Integer userId, String reason) {
        this.importDate = importDate;
        this.supplierId = supplierId;
        this.userId = userId;
        this.reason = reason;
    }

    // Helper methods
    public void addDetail(ImportSlipDetail detail) {
        details.add(detail);
        detail.setImportSlip(this);
    }

    public void removeDetail(ImportSlipDetail detail) {
        details.remove(detail);
        detail.setImportSlip(null);
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public List<ImportSlipDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ImportSlipDetail> details) {
        this.details = details;
    }
}
