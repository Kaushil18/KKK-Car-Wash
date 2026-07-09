package com.kkkarwash.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.kkkarwash.dto.LoginRequest;
import com.kkkarwash.dto.LoginResponse;
import com.kkkarwash.dto.RegisterRequest;
import com.kkkarwash.model.User;
import com.kkkarwash.repository.UserRepository;
import at.favre.lib.crypto.bcrypt.BCrypt;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public class AuthService {
    
    private final UserRepository userRepository;
    private final String jwtSecret;
    private final long jwtExpiration;
    
    public AuthService(
            UserRepository userRepository,
            @Value("${micronaut.security.token.jwt.signatures.secret.generator.secret}") String jwtSecret,
            @Value("${micronaut.security.token.jwt.generator.expiration}") long jwtExpiration) {
        this.userRepository = userRepository;
        this.jwtSecret = jwtSecret;
        this.jwtExpiration = jwtExpiration;
    }
    
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        
        if (userOpt.isEmpty()) {
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        
        User user = userOpt.get();
        
        if (user.getStatus() == User.Status.inactive) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Account is deactivated");
        }
        
        BCrypt.Result result = BCrypt.verifyer().verify(request.getPassword().toCharArray(), user.getPassword());
        
        if (!result.verified) {
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        
        String token = generateToken(user);
        return new LoginResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }
    
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new HttpStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        
        String hashedPassword = BCrypt.withDefaults().hashToString(12, request.getPassword().toCharArray());
        
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword);
        user.setPhone(request.getPhone());
        user.setRole(User.Role.customer);
        user.setStatus(User.Status.active);
        
        userRepository.save(user);
    }
    
    public void forgotPassword(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // Don't reveal if user exists or not
            return;
        }
        
        // In production, send email with reset token
        // For demo, just log
        System.out.println("Password reset requested for: " + email);
    }
    
    public void resetPassword(String token, String newPassword) {
        // In production, validate token and update password
        // For demo, just log
        System.out.println("Password reset with token: " + token);
    }
    
    private String generateToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpiration);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("name", user.getName());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());
        
        return JWT.create()
                .withSubject(user.getId().toString())
                .withIssuer("kkkarwash")
                .withIssuedAt(now)
                .withExpiresAt(expiry)
                .withPayload(claims)
                .sign(algorithm);
    }
    
    public User getUserFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
            Long userId = Long.parseLong(jwt.getSubject());
            return userRepository.findById(userId).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}