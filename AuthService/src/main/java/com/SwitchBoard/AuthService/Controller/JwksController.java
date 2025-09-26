package com.SwitchBoard.AuthService.Controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

@RestController
@Slf4j
public class JwksController {

    @Value("${jwt.public-key}")
    private String publicKeyPath;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJwks() throws Exception {
        log.info("JwksController : getJwks : Request received for JWKS");
        try {
            RSAPublicKey publicKey = (RSAPublicKey) loadPublicKey();

            // Build JWKS format
            String modulus = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(publicKey.getModulus().toByteArray());
            String exponent = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(publicKey.getPublicExponent().toByteArray());

            Map<String, Object> jwk = Map.of(
                    "kty", "RSA",
                    "use", "sig",
                    "alg", "RS256",
                    "kid", "auth-key-1",   // Key ID (for rotation later)
                    "n", modulus,
                    "e", exponent
            );
            
            log.info("JwksController : getJwks : JWKS response generated successfully");
            return Map.of("keys", Collections.singletonList(jwk));
        } catch (Exception e) {
            log.error("JwksController : getJwks : Error generating JWKS - {}", e.getMessage());
            throw e;
        }
    }

    private PublicKey loadPublicKey() throws Exception {
        log.debug("JwksController : loadPublicKey : Loading public key from path - {}", publicKeyPath);
        try {
            byte[] keyBytes = Files.readAllBytes(Paths.get(publicKeyPath));
            String key = new String(keyBytes)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] decoded = Base64.getDecoder().decode(key);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);
            log.debug("JwksController : loadPublicKey : Public key loaded successfully");
            return publicKey;
        } catch (Exception e) {
            log.error("JwksController : loadPublicKey : Error loading public key - {}", e.getMessage());
            throw e;
        }
    }
}
