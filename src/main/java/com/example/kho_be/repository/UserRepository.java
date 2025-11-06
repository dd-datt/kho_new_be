package com.example.kho_be.repository;

import com.example.kho_be.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    // Thêm các method mới cho phân quyền
    java.util.List<User> findByRole(User.UserRole role);

    java.util.List<User> findByUsernameContainingOrEmailContaining(String username, String email);

    // Thêm các method phân trang
    Page<User> findByRole(User.UserRole role, Pageable pageable);

    Page<User> findByUsernameContainingOrEmailContaining(String username, String email, Pageable pageable);

    @Query("SELECT u FROM User u WHERE (u.username LIKE %:keyword% OR u.email LIKE %:keyword%) AND u.role = :role")
    Page<User> findByUsernameContainingOrEmailContainingAndRole(
            @Param("keyword") String keyword1,
            @Param("keyword") String keyword2,
            @Param("role") User.UserRole role,
            Pageable pageable);
}
