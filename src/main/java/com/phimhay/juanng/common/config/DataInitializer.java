package com.phimhay.juanng.common.config;

import com.phimhay.juanng.modules.user.entity.Role;
import com.phimhay.juanng.modules.user.entity.RoleType;
import com.phimhay.juanng.modules.user.entity.User;
import com.phimhay.juanng.modules.user.repository.RoleRepository;
import com.phimhay.juanng.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        log.info("Bắt đầu kiểm tra và khởi tạo dữ liệu mẫu...");

        // 1. Khởi tạo các Role nếu chưa tồn tại
        Role userRole = roleRepository.findByRole(RoleType.USER)
                .orElseGet(() -> {
                    log.info("Tạo mới Role USER");
                    return roleRepository.save(Role.builder().role(RoleType.USER).build());
                });

        Role adminRole = roleRepository.findByRole(RoleType.ADMIN)
                .orElseGet(() -> {
                    log.info("Tạo mới Role ADMIN");
                    return roleRepository.save(Role.builder().role(RoleType.ADMIN).build());
                });

        // 2. Khởi tạo tài khoản Admin tự động
        if (!userRepository.existsByEmail(adminEmail)) {
            log.info("Không tìm thấy tài khoản admin {}, tiến hành tạo mới...", adminEmail);

            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            roles.add(adminRole);

            User adminUser = User.builder()
                    .username("nguyenluan")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .isActive(true)
                    .isEmailVerified(true)
                    .isPremium(true)
                    .roles(roles)
                    .build();

            userRepository.save(adminUser);
            log.info("Tạo tài khoản admin thành công: {}", adminEmail);
        } else {
            log.info("Tài khoản admin {} đã tồn tại.", adminEmail);
        }
    }
}
