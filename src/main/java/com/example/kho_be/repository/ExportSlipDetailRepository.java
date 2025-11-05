package com.example.kho_be.repository;

import com.example.kho_be.entity.ExportSlipDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExportSlipDetailRepository extends JpaRepository<ExportSlipDetail, Integer> {

    List<ExportSlipDetail> findByExportSlipId(Integer exportSlipId);

    List<ExportSlipDetail> findByProductId(Integer productId);
}
