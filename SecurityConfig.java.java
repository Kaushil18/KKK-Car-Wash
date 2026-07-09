package com.kkkarwash.config;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UserDetails;
import io.micronaut.security.token.jwt.signature.SecretSignatureConfiguration;
import io.micronaut.security.token.jwt.signature.SignatureGeneratorConfiguration;
import io.micronaut.security.token.jwt.validator.JwtTokenValidator;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

@Factory
public class SecurityConfig {
    
    @Value("${micronaut.security.token.jwt.signatures.secret.generator.secret:supersecretjwtkey1234567890}")
    private String jwtSecret;
    
    @Bean
    @Singleton
    public SecretSignatureConfiguration secretSignatureConfiguration() {
        SecretSignatureConfiguration config = new SecretSignatureConfiguration();
        config.setSecret(jwtSecret);
        return config;
    }
    
    @Bean
    @Singleton
    public SignatureGeneratorConfiguration signatureGeneratorConfiguration() {
        return secretSignatureConfiguration();
    }
    
    @Bean
    @Singleton
    public AuthenticationProvider authenticationProvider() {
        return new AuthenticationProvider() {
            @Override
            public Publisher<AuthenticationResponse> authenticate(AuthenticationRequest<?, ?> request) {
                // Authentication logic is handled in AuthService
                return Mono.empty();
            }
        };
    }
}