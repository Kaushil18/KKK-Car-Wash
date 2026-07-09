package com.kkkarwash.security;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.inject.Singleton;

@Singleton
public class PasswordUtil {
    
    private static final int BCRYPT_ROUNDS = 12;
    
    public String hashPassword(String plainPassword) {
        return BCrypt.withDefaults()
                .hashToString(BCRYPT_ROUNDS, plainPassword.toCharArray());
    }
    
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        BCrypt.Result result = BCrypt.verifyer()
                .verify(plainPassword.toCharArray(), hashedPassword);
        return result.verified;
    }
    
    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }
        
        // At least 3 of 4 criteria should be met
        int criteria = 0;
        if (hasUppercase) criteria++;
        if (hasLowercase) criteria++;
        if (hasDigit) criteria++;
        if (hasSpecial) criteria++;
        
        return criteria >= 3;
    }
    
    public String generateRandomPassword() {
        // Generate a random password with special characters
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder sb = new StringBuilder(12);
        
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 12; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        
        return sb.toString();
    }
    
    public String getPasswordStrengthMessage(String password) {
        if (password == null || password.isEmpty()) {
            return "Enter a password";
        }
        
        if (password.length() < 6) {
            return "Weak - Password too short";
        }
        
        if (!isPasswordStrong(password)) {
            return "Medium - Add uppercase, numbers, or symbols";
        }
        
        if (password.length() < 12) {
            return "Strong - Good password";
        }
        
        return "Very Strong - Excellent password";
    }
}