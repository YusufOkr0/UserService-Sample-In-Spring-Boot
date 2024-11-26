package com.user_service.exceptions;

import com.user_service.exceptions.dtos.ExceptionMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleWithMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, String> validationErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            validationErrors.put(error.getField(), error.getDefaultMessage());
        });

        errorResponse.put("errorCode", "METHOD_ARGUMENT_NOT_VALID");
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("path", request.getRequestURI());
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("validationErrors", validationErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }



    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionMessage> handleUserAlreadyExistsException(UserAlreadyExistsException ex, HttpServletRequest request) {
        ExceptionMessage message = new ExceptionMessage(
                "USERNAME_OR_EMAIL_TAKEN",
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ExceptionMessage> handleRoleNotFoundException(RoleNotFoundException ex,HttpServletRequest request) {
        ExceptionMessage message = new ExceptionMessage(
                "ROLE_NOT_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PaginationArgumentNotValidException.class)
    public ResponseEntity<ExceptionMessage> handlePaginationArgumentNotValidException(PaginationArgumentNotValidException ex,HttpServletRequest request) {
        ExceptionMessage message = new ExceptionMessage(
                "PAGINATION_ARGUMENT_NOT_VALID",
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
    }


    public ResponseEntity<ExceptionMessage> handleUserNotFoundException(UserNotFoundException ex,HttpServletRequest request) {
        ExceptionMessage message = new ExceptionMessage(
                "USER_NOT_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }


    public ResponseEntity<ExceptionMessage> handleUserAlreadyDeletedException(UserAlreadyDeletedException ex,HttpServletRequest request) {
        ExceptionMessage message = new ExceptionMessage(
                "USER_ALREADY_DELETED",
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
    }


}
