package com.SwitchBoard.AuthService.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;
    private String errorCode;
    private LocalDateTime timestamp;
    private String path;

    public  static  ApiResponse success(String message,boolean success){
        return new ApiResponse(success, message, null, null, LocalDateTime.now(), null);
    }
    public static ApiResponse success(String message, Object data, String path) {
        return new ApiResponse(true, message, data, null, LocalDateTime.now(), path);
    }
    public static ApiResponse error(String message, String errorCode, String path) {
        return new ApiResponse(false, message, null, errorCode, LocalDateTime.now(), path);
    }
}
