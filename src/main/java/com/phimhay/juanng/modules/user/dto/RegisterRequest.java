package com.phimhay.juanng.modules.user.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username không được để trống.")
    @Size(min = 4, max = 25, message = "Username phải từ 4 đến 25 ký tự.")
    private String username;
    @NotBlank(message = "Email không được để trống.")
    @Email(message = "Email không hợp lệ.")
    private String email;
    @NotBlank(message = "Password không được để trống.")
    @Size(min = 6, message = "Password phải từ 6 ký tự trở lên.")
    private String password;
    private String fullName;
}
