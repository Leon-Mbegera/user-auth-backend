package com.example.util;

import com.example.model.User;
import com.example.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private final SecretKey secretKey = Keys.hmacShaKeyFor(
        "this-is-a-very-secure-and-long-secret-key-for-jwt-authentication-1234567890".getBytes(StandardCharsets.UTF_8)
    );
    private final long expirationMs = 86400000; // 1 day

    @Autowired
    private UserRepository userRepository;

    public String generateToken(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found."));

        String roles = user.getRoles().stream().map(role -> role.getName()).collect(Collectors.joining(","));
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String getRolesFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("roles", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}