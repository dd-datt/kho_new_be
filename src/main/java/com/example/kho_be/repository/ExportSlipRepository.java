package com.example.kho_be.repository;

import com.example.kho_be.entity.ExportSlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExportSlipRepository extends JpaRepository<ExportSlip, Integer>, JpaSpecificationExecutor<ExportSlip> {

    // Tìm kiếm phiếu xuất theo tháng và năm
    @Query("SELECT e FROM ExportSlip e WHERE MONTH(e.exportDate) = :month AND YEAR(e.exportDate) = :year ORDER BY e.exportDate DESC")
    List<ExportSlip> findByMonthAndYear(@Param("month") Integer month, @Param("year") Integer year);

    // Tìm kiếm phiếu xuất theo năm
    @Query("SELECT e FROM ExportSlip e WHERE YEAR(e.exportDate) = :year ORDER BY e.exportDate DESC")
    List<ExportSlip> findByYear(@Param("year") Integer year);

    // Tìm kiếm phiếu xuất theo khoảng thời gian
    @Query("SELECT e FROM ExportSlip e WHERE e.exportDate BETWEEN :startDate AND :endDate ORDER BY e.exportDate DESC")
    List<ExportSlip> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Tìm kiếm phiếu xuất theo user
    List<ExportSlip> findByUserId(Integer userId);

    // Tìm kiếm phiếu xuất theo lý do (reason)
    @Query("SELECT e FROM ExportSlip e WHERE LOWER(e.reason) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY e.exportDate DESC")
    List<ExportSlip> searchByReason(@Param("keyword") String keyword);
}
