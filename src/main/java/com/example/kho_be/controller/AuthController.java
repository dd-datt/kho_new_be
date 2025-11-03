package com.example.kho_be.controller;

import com.example.kho_be.dto.LoginRequest;
import com.example.kho_be.dto.LoginResponse;
import com.example.kho_be.dto.RegisterRequest;
import com.example.kho_be.entity.User;
import com.example.kho_be.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private com.example.kho_be.service.TokenBlacklistService tokenBlacklistService;

    /**
     * Endpoint đăng nhập
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Endpoint đăng ký user mới
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = authService.register(registerRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đăng ký thành công");
            response.put("userId", user.getId());
            response.put("username", user.getUsername());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Endpoint kiểm tra token có hợp lệ không
     * GET /api/auth/validate
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Token hợp lệ");
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint đăng xuất
     * POST /api/auth/logout
     * Thêm token vào blacklist để không thể sử dụng lại
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        System.out.println("=== LOGOUT ENDPOINT CALLED ===");
        System.out.println("Authorization header: " + authHeader);

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                // Thêm token vào blacklist
                tokenBlacklistService.blacklistToken(token);

                System.out.println("User logged out. Token blacklisted: "
                        + token.substring(0, Math.min(20, token.length())) + "...");
            } else {
                System.out.println("No valid Authorization header found");
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Đăng xuất thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Logout error: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("message", "Lỗi khi đăng xuất: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
