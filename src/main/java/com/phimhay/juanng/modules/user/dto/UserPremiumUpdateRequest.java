package com.phimhay.juanng.modules.user.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPremiumUpdateRequest {
    private boolean isPremium;
    
    @Min(value = 0, message = "Số ngày kích hoạt VIP không thể âm.")
    private int durationDays;
}
