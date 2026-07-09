package com.kkkarwash.service;

import com.kkkarwash.controller.UserController;
import com.kkkarwash.model.User;
import com.kkkarwash.repository.UserRepository;
import at.favre.lib.crypto.bcrypt.BCrypt;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.utils.SecurityService;
import jakarta.inject.Singleton;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Singleton
public class UserService {
    
    private final UserRepository userRepository;
    private final SecurityService securityService;
    
    public UserService(UserRepository userRepository, SecurityService securityService) {
        this.userRepository = userRepository;
        this.securityService = securityService;
    }
    
    public User getCurrentUser() {
        Optional<Authentication> auth = securityService.getAuthentication();
        if (auth.isEmpty()) {
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        
        Long userId = Long.parseLong(auth.get().getAttributes().get("id").toString());
        return userRepository.findById(userId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
    
    @Transactional
    public User updateCurrentUser(User updatedUser) {
        User currentUser = getCurrentUser();
        
        // Only update allowed fields
        if (updatedUser.getName() != null) {
            currentUser.setName(updatedUser.getName());
        }
        if (updatedUser.getPhone() != null) {
            currentUser.setPhone(updatedUser.getPhone());
        }
        // Email cannot be changed directly
        // Role and status cannot be changed by user
        
        return userRepository.update(currentUser);
    }
    
    @Transactional
    public void changePassword(UserController.PasswordChangeRequest request) {
        User currentUser = getCurrentUser();
        
        // Verify old password
        BCrypt.Result result = BCrypt.verifyer()
                .verify(request.getOldPassword().toCharArray(), currentUser.getPassword());
        
        if (!result.verified) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Incorrect old password");
        }
        
        // Validate new password
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "New password must be at least 6 characters");
        }
        
        // Hash and save new password
        String hashedPassword = BCrypt.withDefaults()
                .hashToString(12, request.getNewPassword().toCharArray());
        currentUser.setPassword(hashedPassword);
        userRepository.update(currentUser);
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Transactional
    public User updateUserStatus(Long userId, User.Status status) {
        User user = getUserById(userId);
        
        // Prevent disabling admin
        if (user.getRole() == User.Role.admin && status == User.Status.inactive) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Cannot disable admin account");
        }
        
        user.setStatus(status);
        return userRepository.update(user);
    }
    
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
    
    public long getCustomerCount() {
        return userRepository.countByRole(User.Role.customer);
    }
    
    public long getActiveCustomerCount() {
        return userRepository.countByStatus(User.Status.active);
    }
}