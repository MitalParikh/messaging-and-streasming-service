package com.example.queues.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API success response format for successful operations.
 * Provides consistent structure across all API endpoints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiSuccessResponse<T> {
    
    /**
     * Response status - always "success" for this class
     */
    @Builder.Default
    private String status = "success";
    
    /**
     * Human-readable message describing the operation result
     */
    private String message;
    
    /**
     * Main response data payload
     */
    private T data;
    
    /**
     * Timestamp when the response was generated
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * HTTP status code for reference
     */
    private Integer httpStatus;
    
    /**
     * Request tracking identifier (optional)
     */
    private String requestId;
    
    /**
     * Creates a simple success response with just data
     */
    public static <T> ApiSuccessResponse<T> success(T data) {
        return ApiSuccessResponse.<T>builder()
                .data(data)
                .message("Operation completed successfully")
                .httpStatus(200)
                .build();
    }
    
    /**
     * Creates a success response with custom message
     */
    public static <T> ApiSuccessResponse<T> success(String message, T data) {
        return ApiSuccessResponse.<T>builder()
                .message(message)
                .data(data)
                .httpStatus(200)
                .build();
    }
}