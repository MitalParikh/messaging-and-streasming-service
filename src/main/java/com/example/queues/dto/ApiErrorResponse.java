package com.example.queues.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Standard API error response format for failed operations.
 * Provides consistent error structure across all API endpoints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {
    
    /**
     * Response status - always "error" for this class
     */
    @Builder.Default
    private String status = "error";
    
    /**
     * Main error message describing what went wrong
     */
    private String message;
    
    /**
     * Detailed error description (optional)
     */
    private String details;
    
    /**
     * Error code for programmatic handling
     */
    private String errorCode;
    
    /**
     * List of validation errors or multiple error details
     */
    private List<String> errors;
    
    /**
     * Additional error context data
     */
    private Map<String, Object> errorData;
    
    /**
     * Timestamp when the error occurred
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * HTTP status code
     */
    private Integer httpStatus;
    
    /**
     * Request tracking identifier (optional)
     */
    private String requestId;
    
    /**
     * Stack trace or technical details (for development only)
     */
    private String stackTrace;
    
    /**
     * Creates a simple error response with just a message
     */
    public static ApiErrorResponse error(String message) {
        return ApiErrorResponse.builder()
                .message(message)
                .httpStatus(400)
                .build();
    }
    
    /**
     * Creates an error response with message and HTTP status
     */
    public static ApiErrorResponse error(String message, int httpStatus) {
        return ApiErrorResponse.builder()
                .message(message)
                .httpStatus(httpStatus)
                .build();
    }
    
    /**
     * Creates an error response with message, details, and status
     */
    public static ApiErrorResponse error(String message, String details, int httpStatus) {
        return ApiErrorResponse.builder()
                .message(message)
                .details(details)
                .httpStatus(httpStatus)
                .build();
    }
    
    /**
     * Creates an error response with error code and additional data
     */
    public static ApiErrorResponse error(String message, String errorCode, Map<String, Object> errorData) {
        return ApiErrorResponse.builder()
                .message(message)
                .errorCode(errorCode)
                .errorData(errorData)
                .httpStatus(400)
                .build();
    }
}