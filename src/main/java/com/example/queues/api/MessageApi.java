package com.example.queues.api;

import com.example.queues.dto.ApiSuccessResponse;
import com.example.queues.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API interface for Apache Artemis JMS messaging operations.
 * Defines endpoints for queue management and message processing with standardized response formats.
 */
@Tag(name = "JMS APIs", description = "Apache Artemis JMS messaging operations for queue management and message processing")
public interface MessageApi {

    /**
     * Sends a custom message to the specified Artemis JMS queue.
     */
    @Operation(
        summary = "Send message to JMS queue",
        description = "Sends a custom message to the specified Artemis JMS queue. Supports queue URI format (queue://queueName) and targetClient parameter."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Message sent successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiSuccessResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid queue name or message sending failed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/sendMesage/{rawQueueName}")
    ResponseEntity<?> sendMessage(
            @Parameter(description = "Queue name or URI (supports queue://queueName format)", example = "queue1")
            @PathVariable("rawQueueName") String rawQueueName,
            @Parameter(description = "Message content to send", example = "Hello World")
            @RequestParam String message,
            @Parameter(description = "Target client identifier (optional)", example = "1")
            @RequestParam(value = "targetClient", required = false) String targetClient);

    /**
     * Retrieves all messages from the specified Artemis JMS queue without consuming them.
     */
    @Operation(
        summary = "Browse JMS queue messages",
        description = "Retrieves all messages from the specified Artemis JMS queue without consuming them. Supports queue URI format and returns message count and content."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Queue browsed successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiSuccessResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid queue name or browsing failed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/browse/{rawDestination}")
    ResponseEntity<?> browseQueueMessages(
            @Parameter(description = "Queue name or URI to browse (supports queue://queueName format)", example = "queue1")
            @PathVariable("rawDestination") String rawDestination,
            @Parameter(description = "Target client identifier (optional)", example = "1")
            @RequestParam(value = "targetClient", required = false) String targetClient);
}