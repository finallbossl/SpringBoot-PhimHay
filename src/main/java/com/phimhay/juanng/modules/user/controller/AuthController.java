package com.phimhay.juanng.modules.user.controller;

import com.phimhay.juanng.common.response.ApiResponse;
import com.phimhay.juanng.modules.user.dto.AuthResponse;
import com.phimhay.juanng.modules.user.dto.LoginRequest;
import com.phimhay.juanng.modules.user.dto.RegisterRequest;
import com.phimhay.juanng.modules.user.dto.RefreshTokenRequest;
import com.phimhay.juanng.modules.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        AuthResponse response = authService.register(request);
        
        return ApiResponse.<AuthResponse>builder()
                .code(1000)
                .message("Đăng ký tài khoản thành công!")
                .result(response)
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody @Valid LoginRequest request, HttpServletRequest httpRequest) {
        AuthResponse response = authService.login(request, httpRequest);

        return ApiResponse.<AuthResponse>builder()
                .code(1000)
                .message("Đăng nhập thành công!")
                .result(response)
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);

        return ApiResponse.<AuthResponse>builder()
                .code(1000)
                .message("Làm mới token thành công!")
                .result(response)
                .build();
    }
}
