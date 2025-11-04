package com.example.kho_be.repository;

import com.example.kho_be.entity.ImportSlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ImportSlipRepository extends JpaRepository<ImportSlip, Integer>, JpaSpecificationExecutor<ImportSlip> {

    // Tìm kiếm theo supplier
    List<ImportSlip> findBySupplierId(Integer supplierId);

    // Tìm kiếm theo user
    List<ImportSlip> findByUserId(Integer userId);

    // Tìm kiếm theo khoảng thời gian
    List<ImportSlip> findByImportDateBetween(LocalDate startDate, LocalDate endDate);

    // Tìm kiếm theo tháng và năm
    @Query("SELECT i FROM ImportSlip i WHERE MONTH(i.importDate) = :month AND YEAR(i.importDate) = :year")
    List<ImportSlip> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

    // Tìm kiếm theo năm
    @Query("SELECT i FROM ImportSlip i WHERE YEAR(i.importDate) = :year")
    List<ImportSlip> findByYear(@Param("year") int year);

    // Tìm kiếm theo supplier và khoảng thời gian
    List<ImportSlip> findBySupplierIdAndImportDateBetween(Integer supplierId, LocalDate startDate, LocalDate endDate);
}
