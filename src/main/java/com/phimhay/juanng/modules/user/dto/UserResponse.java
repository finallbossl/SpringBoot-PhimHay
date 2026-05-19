package com.phimhay.juanng.modules.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private String avatarUrl;
    private boolean isActive;
    private boolean isEmailVerified;
    private boolean isPremium;
    private LocalDateTime premiumExpiredAt;
    private String provider;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
