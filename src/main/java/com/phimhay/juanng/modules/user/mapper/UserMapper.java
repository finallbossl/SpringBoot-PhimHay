package com.phimhay.juanng.modules.user.mapper;

import com.phimhay.juanng.modules.user.dto.AuthResponse;
import com.phimhay.juanng.modules.user.dto.UserResponse;
import com.phimhay.juanng.modules.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    // Ánh xạ từ User sang UserResponse (Dùng cho quản lý thông tin cá nhân và admin)
    public UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getProfiles() != null ? user.getProfiles().getFullName() : user.getUsername())
                .avatarUrl(user.getProfiles() != null ? user.getProfiles().getAvatarUrl() : null)
                .isActive(user.isActive())
                .isEmailVerified(user.isEmailVerified())
                .isPremium(user.isPremium())
                .premiumExpiredAt(user.getPremiumExpiredAt())
                .provider(user.getProvider())
                .roles(user.getRoles().stream()
                        .map(role -> role.getRole().name())
                        .collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    // Ánh xạ từ User và cặp Token sang AuthResponse (Dùng cho Register/Login/Refresh)
    public AuthResponse toAuthResponse(User user, String accessToken, String refreshToken) {
        if (user == null) {
            return null;
        }

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getProfiles() != null ? user.getProfiles().getFullName() : user.getUsername())
                .avatarUrl(user.getProfiles() != null ? user.getProfiles().getAvatarUrl() : null)
                .build();
    }
}
