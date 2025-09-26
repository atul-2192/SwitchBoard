package com.SwitchBoard.AuthService.Service;

import com.SwitchBoard.AuthService.DTO.ApiResponse;
import com.SwitchBoard.AuthService.DTO.AuthResponse;
import com.SwitchBoard.AuthService.Exception.ResourceNotFoundException;
import com.SwitchBoard.AuthService.Exception.UnauthorizedException;
import com.SwitchBoard.AuthService.Exception.UnexpectedException;
import com.SwitchBoard.AuthService.Model.User;
import com.SwitchBoard.AuthService.Repository.UserRepository;
import com.SwitchBoard.AuthService.Util.JwtUtil;
import com.SwitchBoard.AuthService.Util.OtpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private static final String OTP_PREFIX = "otp:";
    private static final String COOLDOWN_PREFIX = "cooldown:";
    private static final int OTP_TTL_MINUTES = 5;
    private static final int COOLDOWN_SECONDS = 60;
    private static final int MAX_ATTEMPTS = 3;

    public ApiResponse generateOtp(String email) {
        log.info("OtpService : generateOtp : Generating OTP for email - {}", email);
        
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            log.warn("OtpService : generateOtp : User with email {} not found", email);
            throw new ResourceNotFoundException("User with email " + email + " not found.");
        }

        String key = OTP_PREFIX + email.toLowerCase();
        String cooldownKey = COOLDOWN_PREFIX + email.toLowerCase();

        if (redisTemplate.hasKey(cooldownKey)) {
            log.warn("OtpService : generateOtp : Cooldown period active for email - {}", email);
            throw new UnexpectedException("Please wait before requesting a new OTP.");
        }

        log.debug("OtpService : generateOtp : Deleting any existing OTP for email - {}", email);
        redisTemplate.delete(key);

        String otp = OtpUtils.generateOtp();
        String hashedOtp = OtpUtils.hashOtp(otp);
        log.debug("OtpService : generateOtp : OTP generated for email - {}", email);

        log.debug("OtpService : generateOtp : Storing OTP in Redis");
        redisTemplate.opsForHash().put(key, "hash", hashedOtp);
        redisTemplate.opsForHash().put(key, "attempts", 0);
        redisTemplate.expire(key, OTP_TTL_MINUTES, TimeUnit.MINUTES);

        log.debug("OtpService : generateOtp : Setting cooldown for email - {}", email);
        redisTemplate.opsForValue().set(cooldownKey, "1", Duration.ofSeconds(COOLDOWN_SECONDS));

        // (In real project: Send OTP via Email/SMS)
        log.info("OtpService : generateOtp : OTP sent successfully to email - {}", email);
        return ApiResponse.success("OTP sent successfully to " + email + ". OTP: " + otp, true);
    }

    public AuthResponse validateOtp(String email, String otp) throws Exception {
        log.info("OtpService : validateOtp : Validating OTP for email - {}", email);

        String key = OTP_PREFIX + email.toLowerCase();

        if (!redisTemplate.hasKey(key)) {
            log.warn("OtpService : validateOtp : OTP expired or not found for email - {}", email);
            throw new ResourceNotFoundException("OTP expired or not found. Please request a new one.");
        }

        String hashedOtp = (String) redisTemplate.opsForHash().get(key, "hash");
        Integer attempts = (Integer) redisTemplate.opsForHash().get(key, "attempts");

        if (attempts == null) attempts = 0;
        log.debug("OtpService : validateOtp : Current attempt count - {}", attempts);

        if (attempts >= MAX_ATTEMPTS) {
            log.warn("OtpService : validateOtp : Maximum attempts exceeded for email - {}", email);
            redisTemplate.delete(key);
            throw new UnauthorizedException("Maximum attempts exceeded. OTP invalidated. Please request a new one.");
        }

        if (hashedOtp != null && hashedOtp.equals(OtpUtils.hashOtp(otp))) {
            log.info("OtpService : validateOtp : OTP verified successfully for email - {}", email);
            redisTemplate.delete(key);
            
            log.debug("OtpService : validateOtp : Retrieving user information");
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                log.error("OtpService : validateOtp : User with email {} not found after OTP validation", email);
                throw new ResourceNotFoundException("User with email " + email + " not found.");
            }
            
            log.debug("OtpService : validateOtp : Generating JWT token");
            String jwtString = jwtUtil.generateToken(email, user.getName(), user.getUserRole());
            log.info("OtpService : validateOtp : JWT token generated successfully");
            
            return new AuthResponse(jwtString);
        } else {
            log.warn("OtpService : validateOtp : Invalid OTP provided for email - {}", email);
            redisTemplate.opsForHash().put(key, "attempts", attempts + 1);
            throw new UnauthorizedException("Invalid OTP. Please try again.");
        }
    }
}
