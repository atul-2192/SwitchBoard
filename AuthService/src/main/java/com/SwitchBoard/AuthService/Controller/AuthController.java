package com.SwitchBoard.AuthService.Controller;

import com.SwitchBoard.AuthService.DTO.*;
import com.SwitchBoard.AuthService.Service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin( origins = "*", allowedHeaders = "*")
@Slf4j
public class AuthController {

    private final OtpService otpService;

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse> sendOtp(@RequestBody AuthRequest authRequest) {
        log.info("AuthController : sendOtp : Request received for email - {}", authRequest.getEmail());
        ApiResponse apiResponse = otpService.generateOtp(authRequest.getEmail());
        log.info("AuthController : sendOtp : OTP sent successfully for email - {}", authRequest.getEmail());
        return ResponseEntity.ok().body(apiResponse);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@RequestBody AuthValidateRequest authValidateRequest) throws Exception {
        log.info("AuthController : verifyOtp : Request received for email - {}", authValidateRequest.getEmail());
        AuthResponse authResponse = otpService.validateOtp(authValidateRequest.getEmail(), authValidateRequest.getOtp());
        log.info("AuthController : verifyOtp : OTP verified successfully for email - {}", authValidateRequest.getEmail());
        return ResponseEntity.ok(authResponse);
    }
}
