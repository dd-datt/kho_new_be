package com.example.kho_be.repository;

import com.example.kho_be.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

    Optional<Supplier> findByCode(String code);

    Optional<Supplier> findByEmail(String email);

    boolean existsByCode(String code);

    boolean existsByEmail(String email);

    // Tìm kiếm theo tên, mã, email, số điện thoại (không phân trang)
    @Query("SELECT s FROM Supplier s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Supplier> searchSuppliers(@Param("keyword") String keyword);

    // Tìm kiếm với phân trang
    @Query("SELECT s FROM Supplier s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Supplier> searchSuppliers(@Param("keyword") String keyword, Pageable pageable);
}
