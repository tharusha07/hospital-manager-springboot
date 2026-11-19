package com.example.hospital_manager.exception;

import com.example.hospital_manager.dto.ErrorResponse;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(
            ApiException ex) {

        return ResponseEntity.status(ex.getStatus()).body(
                new ErrorResponse(
                        LocalDateTime.now(),
                        ex.getStatus().value(),
                        ex.getStatus().getReasonPhrase(),
                        ex.getMessage()
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        LocalDateTime.now(),
                        400,
                        "Bad Request",
                        message
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(Exception ex) {

        return ResponseEntity.status(500).body(
                new ErrorResponse(
                        LocalDateTime.now(),
                        500,
                        "Internal Server Error",
                        "An unexpected error occurred"
                )
        );
    }
}