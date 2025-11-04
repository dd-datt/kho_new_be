package com.example.kho_be.repository;

import com.example.kho_be.entity.ImportSlipDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportSlipDetailRepository extends JpaRepository<ImportSlipDetail, Integer> {

    // Tìm chi tiết theo import slip id
    List<ImportSlipDetail> findByImportSlipId(Integer importSlipId);

    // Tìm chi tiết theo product id
    List<ImportSlipDetail> findByProductId(Integer productId);
}
