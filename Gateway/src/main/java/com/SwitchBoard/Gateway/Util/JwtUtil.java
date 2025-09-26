package com.SwitchBoard.Gateway.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;
import com.SwitchBoard.Gateway.Service.JwksService;

import java.security.interfaces.RSAPublicKey;

@Component
public class JwtUtil {

    private final JwksService jwksService;

    public JwtUtil(JwksService jwksService) {
        this.jwksService = jwksService;
    }

    /**
     * Parse JWT token and verify using public key from JWKS
     */
    public Claims parseClaims(String token) {
        // Extract "kid" from token header
        String kid = Jwts.parserBuilder().build()
                .parseClaimsJws(token)
                .getHeader()
                .getKeyId();

        RSAPublicKey key = jwksService.getKey(kid);

        if (key == null) {
            throw new RuntimeException("Public key not found for kid: " + kid);
        }

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
