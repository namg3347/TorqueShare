package com.redhat.torqueshare.exceptions;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ContentNotFoundException.class)
    public ResponseEntity<ApiErrorResponse>
    handleContentNotFound(ContentNotFoundException ex) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                "CONTENT_NOT_FOUND",
                "Requested content does not exist"
        );
    }

    @ExceptionHandler(FileExpiredException.class)
    public ResponseEntity<ApiErrorResponse>
    handleExpired(FileExpiredException ex) {

        return buildResponse(
                HttpStatus.GONE,
                "FILE_EXPIRED",
                "File has expired"
        );
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ApiErrorResponse>
    handleRateLimit(RateLimitException ex) {

        return buildResponse(
                HttpStatus.TOO_MANY_REQUESTS,
                "RATE_LIMIT_EXCEEDED",
                "Too many requests"
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse>
    handleGeneric(Exception ex) {

        log.error("Unhandled exception", ex);

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "Unexpected server error"
        );
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String error,
            String message
    ) {

        ApiErrorResponse response =
                ApiErrorResponse.builder()
                        .timestamp(Instant.now())
                        .status(status.value())
                        .error(error)
                        .message(message)
                        .build();

        return ResponseEntity
                .status(status)
                .body(response);
    }
}