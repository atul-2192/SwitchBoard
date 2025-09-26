package com.SwitchBoard.AuthService.Util;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;

public class OtpUtils {

    private static final Logger log = LoggerFactory.getLogger(OtpUtils.class);
    private static final SecureRandom random = new SecureRandom();

    // Generate 6-digit OTP
    public static String generateOtp() {
        log.debug("OtpUtils : generateOtp : Generating 6-digit OTP");
        int otp = 100000 + random.nextInt(900000);
        log.debug("OtpUtils : generateOtp : OTP generated successfully");
        return String.valueOf(otp);
    }

    // Hash OTP using SHA-256
    public static String hashOtp(String otp) {
        log.debug("OtpUtils : hashOtp : Hashing OTP with SHA-256");
        String hashedOtp = DigestUtils.sha256Hex(otp);
        log.debug("OtpUtils : hashOtp : OTP hashed successfully");
        return hashedOtp;
    }
}
