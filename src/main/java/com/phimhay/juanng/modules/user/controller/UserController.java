package com.phimhay.juanng.modules.user.controller;

import com.phimhay.juanng.common.response.ApiResponse;
import com.phimhay.juanng.modules.user.dto.UserPremiumUpdateRequest;
import com.phimhay.juanng.modules.user.dto.UserResponse;
import com.phimhay.juanng.modules.user.dto.UserUpdateRequest;
import com.phimhay.juanng.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 1. Lấy thông tin cá nhân của user đang đăng nhập
    @GetMapping("/my-info")
    public ApiResponse<UserResponse> getMyInfo() {
        UserResponse response = userService.getMyInfo();
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Lấy thông tin cá nhân thành công!")
                .result(response)
                .build();
    }

    // 2. Cập nhật thông tin cá nhân (Profile)
    @PutMapping("/my-info")
    public ApiResponse<UserResponse> updateMyInfo(@RequestBody @Valid UserUpdateRequest request) {
        UserResponse response = userService.updateMyInfo(request);
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Cập nhật thông tin thành công!")
                .result(response)
                .build();
    }

    // 3. Admin: Lấy tất cả người dùng
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        List<UserResponse> response = userService.getAllUsers();
        return ApiResponse.<List<UserResponse>>builder()
                .code(1000)
                .message("Lấy danh sách tất cả người dùng thành công!")
                .result(response)
                .build();
    }

    // 4. Admin: Lấy chi tiết một người dùng
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserResponse> getUserById(@PathVariable String userId) {
        UserResponse response = userService.getUserById(userId);
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Lấy thông tin người dùng chi tiết thành công!")
                .result(response)
                .build();
    }

    // 5. Admin: Khóa hoặc mở khóa tài khoản
    @PutMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserResponse> updateUserStatus(
            @PathVariable String userId,
            @RequestParam boolean isActive) {
        UserResponse response = userService.updateUserStatus(userId, isActive);
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message(isActive ? "Mở khóa tài khoản thành công!" : "Khóa tài khoản thành công!")
                .result(response)
                .build();
    }

    // 6. Admin: Nâng cấp VIP/Premium cho người dùng
    @PutMapping("/{userId}/premium")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserResponse> updateUserPremium(
            @PathVariable String userId,
            @RequestBody @Valid UserPremiumUpdateRequest request) {
        UserResponse response = userService.updateUserPremium(userId, request);
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Cập nhật gói Premium/VIP thành công!")
                .result(response)
                .build();
    }
}
