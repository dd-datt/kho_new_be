package com.example.kho_be.dto;

import com.example.kho_be.entity.User;

public class UserMapper {

    /**
     * Chuyển đổi từ User entity sang UserDTO
     */
    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());

        return dto;
    }

    /**
     * Chuyển đổi từ RegisterRequest sang User entity
     */
    public static User fromRegisterRequest(RegisterRequest request, String encodedPassword) {
        if (request == null) {
            return null;
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPasswordHash(encodedPassword);
        user.setIsActive(true);

        return user;
    }
}
