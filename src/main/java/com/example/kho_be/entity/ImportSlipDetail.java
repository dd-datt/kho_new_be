package com.example.kho_be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "import_slip_details")
public class ImportSlipDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "import_slip_id", nullable = false)
    @JsonIgnore
    private ImportSlip importSlip;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "import_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal importPrice;

    // Constructors
    public ImportSlipDetail() {
    }

    public ImportSlipDetail(Integer productId, Integer quantity, BigDecimal importPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.importPrice = importPrice;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ImportSlip getImportSlip() {
        return importSlip;
    }

    public void setImportSlip(ImportSlip importSlip) {
        this.importSlip = importSlip;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(BigDecimal importPrice) {
        this.importPrice = importPrice;
    }
}
