package com.phimhay.juanng.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Cấu hình CORS
                .csrf(AbstractHttpConfigurer::disable) // Tắt bảo vệ CSRF (Vì dùng JWT không xài cookie)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // TỰ DO: Mọi API bắt đầu bằng /auth/ ai cũng vào được
                        .requestMatchers("/catalog/sync/**").hasRole("ADMIN") // Chỉ ADMIN mới có quyền truy cập API đồng bộ & cấu hình nguồn
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/catalog/**", "/streaming/**").hasRole("ADMIN") // Thêm mới phim/tập phim phải là ADMIN
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/catalog/**", "/streaming/**").hasRole("ADMIN") // Cập nhật phim/tập phim phải là ADMIN
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/catalog/**", "/streaming/**").hasRole("ADMIN") // Xóa phim/tập phim phải là ADMIN
                        .anyRequest().authenticated() // BẮT BUỘC: Mọi API còn lại đều phải có thẻ JWT hợp lệ mới cho vào
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Chế độ không lưu phiên (Stateless)
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Đặt ông bảo vệ gác cổng lên đầu hàng

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001", "http://127.0.0.1:3000", "http://127.0.0.1:3001"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * @return
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Công cụ băm mật khẩu ra chuỗi loằng ngoằng cực kỳ bảo mật
    }
}
