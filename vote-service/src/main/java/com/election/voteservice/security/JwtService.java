package com.election.voteservice.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "MySuperSecretElectionSystemKeyForJWT2026Secure123456789";

    private final Key key =
            Keys.hmacShaKeyFor(
                    SECRET_KEY.getBytes(StandardCharsets.UTF_8)
            );

    public Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {

        return extractAllClaims(token).getSubject();
    }

    public Long extractUserId(String token) {

        Object userId =
                extractAllClaims(token).get("userId");

        if (userId == null) {
            return null;
        }

        return ((Number) userId).longValue();
    }

    public String extractRole(String token) {

        Object role =
                extractAllClaims(token).get("role");

        if (role == null) {
            return null;
        }

        return role.toString();
    }

    public boolean isTokenValid(String token) {

        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}