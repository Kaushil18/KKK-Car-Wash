package com.kkkarwash.controller;

import com.kkkarwash.dto.LoginRequest;
import com.kkkarwash.dto.LoginResponse;
import com.kkkarwash.dto.RegisterRequest;
import com.kkkarwash.service.AuthService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.Valid;

@Controller("/auth")
@Validated
@Tag(name = "Authentication")
public class AuthController {
    
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @Post("/login")
    public HttpResponse<LoginResponse> login(@Body @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);
        return HttpResponse.ok(response);
    }
    
    @Post("/register")
    public HttpResponse<?> register(@Body @Valid RegisterRequest request) {
        authService.register(request);
        return HttpResponse.created();
    }
    
    @Post("/forgot-password")
    public HttpResponse<?> forgotPassword(@Body String email) {
        authService.forgotPassword(email);
        return HttpResponse.ok();
    }
    
    @Post("/reset-password")
    public HttpResponse<?> resetPassword(@Body String token, @Body String newPassword) {
        authService.resetPassword(token, newPassword);
        return HttpResponse.ok();
    }
}