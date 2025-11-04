package com.example.kho_be.service;

import com.example.kho_be.dto.LoginRequest;
import com.example.kho_be.dto.LoginResponse;
import com.example.kho_be.dto.RegisterRequest;
import com.example.kho_be.entity.User;
import com.example.kho_be.repository.UserRepository;
import com.example.kho_be.security.CustomUserDetails;
import com.example.kho_be.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Đăng nhập với username và password
     */
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            // Lấy thông tin user từ authentication
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // Generate JWT token
            String token = jwtUtil.generateToken(userDetails);

            // Trả về response với role
            return new LoginResponse(
                    token,
                    userDetails.getUserId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    userDetails.getFullName(),
                    userDetails.getRole().name()); // Thêm role

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Username hoặc password không đúng");
        }
    }

    /**
     * Đăng ký user mới
     */
    public User register(RegisterRequest registerRequest) {
        // Kiểm tra username đã tồn tại
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        // Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        // Tạo user mới
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setFullName(registerRequest.getFullName());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(User.UserRole.USER); // Mặc định role USER khi đăng ký
        user.setIsActive(true);

        // Lưu vào database
        return userRepository.save(user);
    }
}
