package com.kkkarwash.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class JwtUtil {
    
    @Value("${micronaut.security.token.jwt.signatures.secret.generator.secret:supersecretjwtkey1234567890}")
    private String jwtSecret;
    
    @Value("${micronaut.security.token.jwt.generator.expiration:86400000}")
    private long jwtExpiration;
    
    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(jwtSecret);
    }
    
    public String generateToken(Long userId, String email, String name, String role) {
        Algorithm algorithm = getAlgorithm();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpiration);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("email", email);
        claims.put("name", name);
        claims.put("role", role);
        
        return JWT.create()
                .withSubject(userId.toString())
                .withIssuer("kkkarwash")
                .withIssuedAt(now)
                .withExpiresAt(expiry)
                .withPayload(claims)
                .sign(algorithm);
    }
    
    public DecodedJWT verifyToken(String token) throws JWTVerificationException {
        Algorithm algorithm = getAlgorithm();
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("kkkarwash")
                .build();
        return verifier.verify(token);
    }
    
    public Long getUserIdFromToken(String token) {
        try {
            DecodedJWT jwt = verifyToken(token);
            return Long.parseLong(jwt.getSubject());
        } catch (JWTVerificationException e) {
            return null;
        }
    }
    
    public String getEmailFromToken(String token) {
        try {
            DecodedJWT jwt = verifyToken(token);
            return jwt.getClaim("email").asString();
        } catch (JWTVerificationException e) {
            return null;
        }
    }
    
    public String getNameFromToken(String token) {
        try {
            DecodedJWT jwt = verifyToken(token);
            return jwt.getClaim("name").asString();
        } catch (JWTVerificationException e) {
            return null;
        }
    }
    
    public String getRoleFromToken(String token) {
        try {
            DecodedJWT jwt = verifyToken(token);
            return jwt.getClaim("role").asString();
        } catch (JWTVerificationException e) {
            return null;
        }
    }
    
    public boolean isTokenValid(String token) {
        try {
            verifyToken(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }
    
    public ZonedDateTime getExpirationDate(String token) {
        try {
            DecodedJWT jwt = verifyToken(token);
            Date expiresAt = jwt.getExpiresAt();
            return Instant.ofEpochMilli(expiresAt.getTime())
                    .atZone(ZoneId.systemDefault());
        } catch (JWTVerificationException e) {
            return null;
        }
    }
    
    public Map<String, Object> getTokenClaims(String token) {
        try {
            DecodedJWT jwt = verifyToken(token);
            Map<String, Object> claims = new HashMap<>();
            jwt.getClaims().forEach((key, value) -> {
                claims.put(key, value.asString());
            });
            return claims;
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}