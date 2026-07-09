package com.kkkarwash.controller;

import com.kkkarwash.model.User;
import com.kkkarwash.service.UserService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.util.List;

@Controller("/api/users")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @Get("/me")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public HttpResponse<User> getCurrentUser() {
        return HttpResponse.ok(userService.getCurrentUser());
    }
    
    @Put("/me")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public HttpResponse<User> updateCurrentUser(@Body User user) {
        return HttpResponse.ok(userService.updateCurrentUser(user));
    }
    
    @Put("/me/password")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public HttpResponse<?> changePassword(@Body PasswordChangeRequest request) {
        userService.changePassword(request);
        return HttpResponse.ok();
    }
    
    @Get("/{id}")
    @Secured({"admin"})
    public HttpResponse<User> getUserById(@PathVariable Long id) {
        return HttpResponse.ok(userService.getUserById(id));
    }
    
    @Get
    @Secured({"admin"})
    public HttpResponse<List<User>> getAllUsers() {
        return HttpResponse.ok(userService.getAllUsers());
    }
    
    @Put("/{id}/status")
    @Secured({"admin"})
    public HttpResponse<User> updateUserStatus(@PathVariable Long id, @Body User user) {
        return HttpResponse.ok(userService.updateUserStatus(id, user.getStatus()));
    }
    
    // Inner class for password change request
    public static class PasswordChangeRequest {
        private String oldPassword;
        private String newPassword;
        
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}