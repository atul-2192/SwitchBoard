package com.SwitchBoard.AuthService.DTO;

import lombok.Data;

@Data
public class AuthValidateRequest {
    private String email;
    private String otp;
}
