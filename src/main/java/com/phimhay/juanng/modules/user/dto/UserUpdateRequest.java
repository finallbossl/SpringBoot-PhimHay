package com.phimhay.juanng.modules.user.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    @Size(max = 100, message = "Họ và tên không vượt quá 100 ký tự.")
    private String fullName;

    private String avatarUrl;
}
