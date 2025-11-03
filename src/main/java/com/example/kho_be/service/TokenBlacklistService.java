package com.example.kho_be.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service để quản lý blacklist của JWT tokens
 * Trong production, nên dùng Redis hoặc database để lưu trữ
 */
@Service
public class TokenBlacklistService {

    // Sử dụng ConcurrentHashMap để thread-safe
    // Trong production nên dùng Redis với TTL = token expiration time
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    /**
     * Thêm token vào blacklist
     */
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
        System.out.println("Token added to blacklist. Total blacklisted: " + blacklistedTokens.size());
    }

    /**
     * Kiểm tra token có trong blacklist không
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    /**
     * Xóa token khỏi blacklist (thường không cần dùng)
     */
    public void removeFromBlacklist(String token) {
        blacklistedTokens.remove(token);
    }

    /**
     * Xóa tất cả tokens trong blacklist (để testing)
     */
    public void clearBlacklist() {
        blacklistedTokens.clear();
    }

    /**
     * Lấy số lượng tokens trong blacklist
     */
    public int getBlacklistSize() {
        return blacklistedTokens.size();
    }
}
