package com.kkkarwash.exception;

import io.micronaut.http.HttpStatus;

public class CustomException extends RuntimeException {
    
    private final HttpStatus status;
    private final String errorCode;
    
    public CustomException(String message) {
        this(message, HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }
    
    public CustomException(String message, HttpStatus status) {
        this(message, status, status.getReason());
    }
    
    public CustomException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
    
    public HttpStatus getStatus() {
        return status;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    // Common exception factory methods
    public static CustomException notFound(String resource) {
        return new CustomException(
            resource + " not found",
            HttpStatus.NOT_FOUND,
            "RESOURCE_NOT_FOUND"
        );
    }
    
    public static CustomException alreadyExists(String resource) {
        return new CustomException(
            resource + " already exists",
            HttpStatus.CONFLICT,
            "RESOURCE_ALREADY_EXISTS"
        );
    }
    
    public static CustomException unauthorized(String message) {
        return new CustomException(
            message,
            HttpStatus.UNAUTHORIZED,
            "UNAUTHORIZED"
        );
    }
    
    public static CustomException forbidden(String message) {
        return new CustomException(
            message,
            HttpStatus.FORBIDDEN,
            "FORBIDDEN"
        );
    }
    
    public static CustomException validation(String message) {
        return new CustomException(
            message,
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR"
        );
    }
    
    public static CustomException internal(String message) {
        return new CustomException(
            message,
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_ERROR"
        );
    }
}