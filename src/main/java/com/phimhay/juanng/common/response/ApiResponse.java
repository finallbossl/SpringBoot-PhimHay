package com.phimhay.juanng.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Ẩn đi các trường bị null (vd: nếu không có data thì không in chữ data: null ra)
public class ApiResponse<T> {
    
    @Builder.Default
    private int code = 1000; // Quy ước mặc định: Code 1000 là Thành công
    
    private String message;
    
    private T result; // Chứa dữ liệu (Ví dụ List User, Thông tin Phim...)
}
