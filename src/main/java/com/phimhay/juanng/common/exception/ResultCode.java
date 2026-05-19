package com.phimhay.juanng.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResultCode {
    UNCATEGORIZED(9999, "Lỗi hệ thống không xác định.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_INPUT(1001, "Dữ liệu đầu vào không hợp lệ.", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "Người dùng đã tồn tại.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1005, "Người dùng không tồn tại.", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Chưa xác thực.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "Bạn không có quyền truy cập.", HttpStatus.FORBIDDEN),
    INVALID_REFRESH_TOKEN(1008, "Token làm mới không hợp lệ hoặc đã hết hạn.", HttpStatus.UNAUTHORIZED);

    private final int code;
    private final String message;
    private final HttpStatus statusCode;

    ResultCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}