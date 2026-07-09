package com.kkkarwash.exception;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.validation.exceptions.ValidationException;
import jakarta.inject.Singleton;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Produces
@Singleton
@Requires(classes = {Exception.class, ExceptionHandler.class})
public class GlobalExceptionHandler implements ExceptionHandler<Exception, HttpResponse<?>> {
    
    @Override
    public HttpResponse<?> handle(HttpRequest request, Exception exception) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("path", request.getPath());
        errorResponse.put("method", request.getMethod().name());
        
        if (exception instanceof HttpStatusException) {
            HttpStatusException httpEx = (HttpStatusException) exception;
            errorResponse.put("status", httpEx.getStatus().getCode());
            errorResponse.put("error", httpEx.getStatus().getReason());
            errorResponse.put("message", httpEx.getMessage());
            
            return HttpResponse.status(httpEx.getStatus()).body(errorResponse);
            
        } else if (exception instanceof ValidationException || exception instanceof ConstraintViolationException) {
            errorResponse.put("status", HttpStatus.BAD_REQUEST.getCode());
            errorResponse.put("error", "Validation Error");
            errorResponse.put("message", exception.getMessage());
            
            return HttpResponse.badRequest().body(errorResponse);
            
        } else if (exception instanceof NullPointerException) {
            errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.getCode());
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", "A null pointer exception occurred");
            
            return HttpResponse.serverError().body(errorResponse);
            
        } else if (exception instanceof IllegalArgumentException) {
            errorResponse.put("status", HttpStatus.BAD_REQUEST.getCode());
            errorResponse.put("error", "Bad Request");
            errorResponse.put("message", exception.getMessage());
            
            return HttpResponse.badRequest().body(errorResponse);
            
        } else {
            errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.getCode());
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", exception.getMessage());
            
            return HttpResponse.serverError().body(errorResponse);
        }
    }
}