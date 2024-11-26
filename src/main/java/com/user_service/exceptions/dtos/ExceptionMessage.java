package com.user_service.exceptions.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;


public record ExceptionMessage(
        String errorCode,
        String message,
        int status,
        String path,
        LocalDateTime timestamp
) {
}
