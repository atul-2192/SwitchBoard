package com.SwitchBoard.Gateway.Filter;

import com.SwitchBoard.Gateway.Service.JwksService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import reactor.core.publisher.Mono;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwksService jwksService;
    private final ObjectMapper objectMapper;

    public JwtAuthFilter(JwksService jwksService, ObjectMapper objectMapper) {
        this.jwksService = jwksService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        // bypass auth endpoints (login, jwks, etc.)
        if (path.startsWith("/api/v1/auth/") || path.equals("/.well-known/jwks.json")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        try {
            String kid = extractKid(token);
            RSAPublicKey key = jwksService.getKey(kid);
            if (key == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Object userIdObj = claims.get("userId");
            String userId = userIdObj == null ? claims.getSubject() : String.valueOf(userIdObj);
            String email = claims.get("email", String.class);
            String role = claims.get("role", String.class);

            // forward identity as headers to downstream services
            ServerHttpRequest mutated = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Email", email == null ? "" : email)
                    .header("X-User-Role", role == null ? "" : role)
                    .build();

            return chain.filter(exchange.mutate().request(mutated).build());

        } catch (Exception ex) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private String extractKid(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;
            String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
            JsonNode headerNode = objectMapper.readTree(headerJson);
            JsonNode kidNode = headerNode.get("kid");
            return kidNode != null ? kidNode.asText() : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int getOrder() {
        return -1; // high priority
    }
}
