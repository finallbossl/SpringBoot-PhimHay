package com.phimhay.juanng.modules.user.service;

import com.phimhay.juanng.common.exception.AppException;
import com.phimhay.juanng.common.exception.ResultCode;
import com.phimhay.juanng.common.security.CustomUserDetails;
import com.phimhay.juanng.common.security.JwtUtil;
import com.phimhay.juanng.modules.user.dto.AuthResponse;
import com.phimhay.juanng.modules.user.dto.LoginRequest;
import com.phimhay.juanng.modules.user.dto.RegisterRequest;
import com.phimhay.juanng.modules.user.dto.RefreshTokenRequest;
import com.phimhay.juanng.modules.user.entity.*;
import com.phimhay.juanng.modules.user.repository.AccessLogRepository;
import com.phimhay.juanng.modules.user.repository.RefreshTokenRepository;
import com.phimhay.juanng.modules.user.repository.RoleRepository;
import com.phimhay.juanng.modules.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccessLogRepository accessLogRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    private void saveRefreshToken(User user, String token) {
        // Chuyển đổi Date sang LocalDateTime
        LocalDateTime expiryDate = jwtUtil.extractExpiration(token)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiryDate(expiryDate)
                .build();
        
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1. Kiểm tra username hoặc email đã tồn tại chưa
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ResultCode.USER_EXISTED);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ResultCode.INVALID_INPUT);
        }

        // 2. Lấy hoặc tạo quyền mặc định (USER)
        Role userRole = roleRepository.findByRole(RoleType.USER)
                .orElseGet(() -> roleRepository.save(Role.builder().role(RoleType.USER).build()));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        // 3. Tạo mới tài khoản User
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .roles(roles)
                .build();

        // 4. Tạo UserProfiles liên kết 1-1 với User
        UserProfiles profiles = UserProfiles.builder()
                .user(user)
                .fullName(request.getFullName() != null ? request.getFullName() : request.getUsername())
                .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=" + request.getUsername())
                .build();

        user.setProfiles(profiles);

        // 5. Lưu vào Database
        userRepository.save(user);

        // 6. Phát hành Access Token & Refresh Token
        UserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // 7. Lưu Refresh Token vào DB
        saveRefreshToken(user, refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(profiles.getFullName())
                .avatarUrl(profiles.getAvatarUrl())
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        // 1. Xác thực thông tin đăng nhập
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (Exception e) {
            throw new AppException(ResultCode.UNAUTHENTICATED);
        }

        // 2. Lấy thông tin user
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ResultCode.USER_NOT_FOUND));

        if (!user.isActive()) {
            throw new AppException(ResultCode.UNAUTHORIZED);
        }

        // 3. Ghi lịch sử đăng nhập vào bảng access_logs
        AccessLog log = AccessLog.builder()
                .user(user)
                .ipAddress(httpRequest.getRemoteAddr())
                .userAgent(httpRequest.getHeader("User-Agent"))
                .endpoint("/auth/login")
                .build();
        accessLogRepository.save(log);

        // 4. Tạo Access Token & Refresh Token
        UserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // 5. Lưu Refresh Token vào DB
        saveRefreshToken(user, refreshToken);

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

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        
        // 1. Kiểm tra Refresh Token tồn tại trong DB không
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new AppException(ResultCode.INVALID_REFRESH_TOKEN));

        // 2. Kiểm tra Token có bị thu hồi không
        if (storedToken.isRevoked()) {
            throw new AppException(ResultCode.INVALID_REFRESH_TOKEN);
        }

        // 3. Kiểm tra Token hết hạn trong DB chưa
        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new AppException(ResultCode.INVALID_REFRESH_TOKEN);
        }

        try {
            // 4. Soi tem lấy tên user từ Refresh Token để kiểm tra signature
            String username = jwtUtil.extractUsername(refreshToken);
            User user = storedToken.getUser();

            if (username == null || !username.equals(user.getUsername())) {
                throw new AppException(ResultCode.INVALID_REFRESH_TOKEN);
            }

            if (!user.isActive()) {
                throw new AppException(ResultCode.UNAUTHORIZED);
            }

            UserDetails userDetails = new CustomUserDetails(user);

            // 5. Token Rotation: Xóa token cũ ra khỏi DB sau khi sử dụng để bảo mật tuyệt đối
            refreshTokenRepository.delete(storedToken);

            // 6. Cấp cặp Access Token & Refresh Token hoàn toàn mới
            String newAccessToken = jwtUtil.generateAccessToken(userDetails);
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

            // 7. Lưu Refresh Token mới vào DB
            saveRefreshToken(user, newRefreshToken);

            return AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .fullName(user.getProfiles() != null ? user.getProfiles().getFullName() : user.getUsername())
                    .avatarUrl(user.getProfiles() != null ? user.getProfiles().getAvatarUrl() : null)
                    .build();

        } catch (Exception e) {
            if (e instanceof AppException) {
                throw e;
            }
            throw new AppException(ResultCode.INVALID_REFRESH_TOKEN);
        }
    }
}
