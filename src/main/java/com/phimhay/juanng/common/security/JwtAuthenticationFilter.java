package com.phimhay.juanng.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Nếu header không có tem "Bearer ..." thì cho đi qua tự do (dành cho API public như đăng nhập/đăng ký)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Cắt lấy chuỗi Token (Bỏ chữ Bearer và khoảng trắng = 7 ký tự đầu)
        jwt = authHeader.substring(7); 
        username = jwtUtil.extractUsername(jwt); // Soi tem lấy tên user

        // Nếu token có tên user và user này chưa được hệ thống ghi nhận đăng nhập
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Tra cứu hồ sơ user từ DB
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Nếu tem hoàn toàn hợp lệ
            if (jwtUtil.isTokenValid(jwt, userDetails)) {
                
                // Cấp giấy thông hành (Authentication) và lưu vào bộ nhớ an ninh (SecurityContext)
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
