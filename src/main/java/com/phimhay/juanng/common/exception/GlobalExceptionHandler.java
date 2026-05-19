package com.phimhay.juanng.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.phimhay.juanng.common.response.ApiResponse;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;



@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

       // 1. Bắt các lỗi Bất ngờ (Ví dụ: NullPointerException)
       @ExceptionHandler(value = Exception.class)
       public ResponseEntity<ApiResponse<Object>> 
       handleGlobalException(Exception exception){

        log.error("Lỗi hệ thống: ", exception);
         ApiResponse<Object> apiResponse = new ApiResponse<>();
         apiResponse.setCode(ResultCode.UNCATEGORIZED.getCode());
         apiResponse.setMessage(ResultCode.UNCATEGORIZED.getMessage());
         
         return ResponseEntity.status(ResultCode.UNCATEGORIZED.getStatusCode())
         .body(apiResponse);
        
        }

        
    // 2. Bắt các lỗi Nghiệp vụ (Ví dụ: throw new AppException(ResultCode.USER_EXISTED))
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<Object>> handlingAppException(AppException exception) {
        ResultCode resultCode = exception.getResultCode(); 
        
        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setCode(resultCode.getCode());
        apiResponse.setMessage(resultCode.getMessage());
        return ResponseEntity.status(resultCode.getStatusCode()).body(apiResponse);
    }

     // 3. Bắt các lỗi khi Validate (Ví dụ: @NotBlank, @Email gửi từ Client)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handlingValidation(MethodArgumentNotValidException exception) {
        String errorMessage = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setCode(ResultCode.INVALID_INPUT.getCode());
        apiResponse.setMessage(errorMessage);
        return ResponseEntity.badRequest().body(apiResponse);
    }


}
