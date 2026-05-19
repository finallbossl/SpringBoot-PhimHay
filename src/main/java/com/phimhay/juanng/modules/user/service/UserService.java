package com.phimhay.juanng.modules.user.service;

import com.phimhay.juanng.common.exception.AppException;
import com.phimhay.juanng.common.exception.ResultCode;
import com.phimhay.juanng.modules.user.dto.UserPremiumUpdateRequest;
import com.phimhay.juanng.modules.user.dto.UserResponse;
import com.phimhay.juanng.modules.user.dto.UserUpdateRequest;
import com.phimhay.juanng.modules.user.entity.User;
import com.phimhay.juanng.modules.user.entity.UserProfiles;
import com.phimhay.juanng.modules.user.mapper.UserMapper;
import com.phimhay.juanng.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    // Lấy thông tin user hiện tại đang đăng nhập
    @Transactional(readOnly = true)
    public UserResponse getMyInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ResultCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    // Cập nhật thông tin profile của user hiện tại đang đăng nhập
    @Transactional
    public UserResponse updateMyInfo(UserUpdateRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ResultCode.USER_NOT_FOUND));

        UserProfiles profile = user.getProfiles();
        if (profile == null) {
            profile = UserProfiles.builder().user(user).build();
            user.setProfiles(profile);
        }

        if (request.getFullName() != null) {
            profile.setFullName(request.getFullName());
        }
        if (request.getAvatarUrl() != null) {
            profile.setAvatarUrl(request.getAvatarUrl());
        }

        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    // Admin: Lấy danh sách tất cả người dùng
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    // Admin: Lấy chi tiết một người dùng bất kỳ
    @Transactional(readOnly = true)
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ResultCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    // Admin: Khóa hoặc mở khóa tài khoản
    @Transactional
    public UserResponse updateUserStatus(String id, boolean isActive) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ResultCode.USER_NOT_FOUND));
        user.setActive(isActive);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    // Admin: Nâng cấp hoặc thu hồi gói Premium/VIP
    @Transactional
    public UserResponse updateUserPremium(String id, UserPremiumUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ResultCode.USER_NOT_FOUND));

        user.setPremium(request.isPremium());
        if (request.isPremium()) {
            LocalDateTime expiry = user.getPremiumExpiredAt();
            // Nếu đã có hạn VIP cũ và chưa hết hạn thì cộng dồn ngày, không thì gia hạn từ bây giờ
            if (expiry != null && expiry.isAfter(LocalDateTime.now())) {
                user.setPremiumExpiredAt(expiry.plusDays(request.getDurationDays()));
            } else {
                user.setPremiumExpiredAt(LocalDateTime.now().plusDays(request.getDurationDays()));
            }
        } else {
            user.setPremiumExpiredAt(null);
        }

        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }
}
