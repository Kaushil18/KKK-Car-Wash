package com.kkkarwash.config;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.filter.CorsFilter;
import io.micronaut.http.filter.CorsOriginConfiguration;
import io.micronaut.http.filter.CorsOriginConfigBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Factory
public class CorsConfig {
    
    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
        "http://localhost:3000",
        "http://localhost:8080",
        "http://127.0.0.1:8080"
    );
    
    private static final List<HttpMethod> ALLOWED_METHODS = Arrays.asList(
        HttpMethod.GET,
        HttpMethod.POST,
        HttpMethod.PUT,
        HttpMethod.DELETE,
        HttpMethod.OPTIONS,
        HttpMethod.PATCH
    );
    
    private static final List<String> ALLOWED_HEADERS = Arrays.asList(
        "Content-Type",
        "Authorization",
        "Accept",
        "Origin",
        "X-Requested-With"
    );
    
    private static final List<String> EXPOSED_HEADERS = Arrays.asList(
        "Authorization",
        "Content-Type"
    );
    
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsOriginConfiguration());
    }
    
    @Bean
    public CorsOriginConfiguration corsOriginConfiguration() {
        CorsOriginConfiguration config = new CorsOriginConfiguration();
        config.setAllowedOrigins(ALLOWED_ORIGINS);
        config.setAllowedMethods(ALLOWED_METHODS);
        config.setAllowedHeaders(ALLOWED_HEADERS);
        config.setExposedHeaders(EXPOSED_HEADERS);
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        return config;
    }
}