package com.starnoh.sacco_management.exception;

import com.starnoh.sacco_management.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Handles application-wide exceptions and converts them
 * into consistent API responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(ResourceNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(
                        false,
                        ex.getMessage(),
                        null
                ));
    }

    // BAD REQUEST → 400
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<?>> handleBadRequest(
            BadRequestException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(
                        false,
                        ex.getMessage(),
                        null
                ));
    }

    // DUPLICATE → 409
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicate(
            DuplicateResourceException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(
                        false,
                        ex.getMessage(),
                        null
                ));
    }

    // UNAUTHORIZED → 401
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<?>> handleUnauthorized(
            UnauthorizedException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(
                        false,
                        ex.getMessage(),
                        null
                ));
    }

    // FORBIDDEN → 403
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<?>> handleForbidden(
            ForbiddenException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(
                        false,
                        ex.getMessage(),
                        null
                ));
    }

    // DATABASE CONSTRAINT VIOLATION
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(
                        false,
                        "Database constraint violation",
                        null
                ));
    }

    // FALLBACK
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleFallback(
            Exception ex
    ) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(
                        false,
                        "Something went wrong",
                        null
                ));
    }
}
