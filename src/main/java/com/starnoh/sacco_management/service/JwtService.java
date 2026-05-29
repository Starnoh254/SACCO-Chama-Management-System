package com.starnoh.sacco_management.service;

import com.starnoh.sacco_management.entity.Users;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    /**
     * Generates a JWT token for a authenticated user.
     */
    public String generateToken(Users user) {
        // 1. Prepare extra claims to embed in the payload
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId());
        extraClaims.put("role", user.getRole().getName());
        extraClaims.put("firstName", user.getFirstName());

        // 2. Build and sign the token
        return Jwts.builder()
                .setClaims(extraClaims)                         // Set custom claims
                .setSubject(user.getEmail())                    // Set username/email as subject
                .setIssuedAt(new Date(System.currentTimeMillis())) // Set creation time
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Set expiry
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Cryptographically sign it
                .compact();
    }

    /**
     * Helper method to decode the secret string into a cryptographic signing Key.
     */
    private Key getSigningKey() {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
