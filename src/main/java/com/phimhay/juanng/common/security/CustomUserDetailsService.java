package com.phimhay.juanng.common.security;

import com.phimhay.juanng.common.exception.AppException;
import com.phimhay.juanng.common.exception.ResultCode;
import com.phimhay.juanng.modules.user.entity.User;
import com.phimhay.juanng.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm user trong DB, nếu không thấy thì ném lỗi USER_NOT_FOUND
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ResultCode.USER_NOT_FOUND));

        return new CustomUserDetails(user);
    }
}
