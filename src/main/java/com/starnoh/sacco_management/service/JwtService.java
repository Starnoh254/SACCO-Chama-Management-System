package com.starnoh.sacco_management.service;

import com.starnoh.sacco_management.entity.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
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
     * Extracts the user ID custom claim from the token.
     */
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        // Cast the value to Long (jjwt usually parses numbers as Integer or Long)
        Object userId = claims.get("userId");
        if (userId instanceof Number) {
            return ((Number) userId).longValue();
        }
        return null;
    }

    /**
     * Extracts the user role custom claim from the token.
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /**
     * Extracts the user first name custom claim from the token.
     */
    public String extractFirstName(String token) {
        return extractAllClaims(token).get("firstName", String.class);
    }

    /**
     * Extracts the subject (usually email or username) from the token.
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Helper method to parse the token and extract all claims.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public boolean validateToken(
            String token
    ) {

        try {

            Claims claims = extractAllClaims(token);

            return !claims
                    .getExpiration()
                    .before(new Date());

        } catch (JwtException | IllegalArgumentException e) {

            return false;
        }
    }



    /**
     * Helper method to decode the secret string into a cryptographic signing Key.
     */
    private Key getSigningKey() {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
