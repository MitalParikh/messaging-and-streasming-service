package com.example.queues.api;
import com.example.queues.dto.ApiSuccessResponse;
import com.example.queues.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API interface for JMS listener lifecycle management with standardized response formats.
 */
@Tag(name = "JMS APIs", description = "JMS listener management operations for controlling message processing")
public interface ListenerApi {

    @Operation(
        summary = "Get status of all JMS listeners",
        description = "Retrieves the current status (RUNNING/STOPPED) of all configured JMS listeners for queue processing."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listener statuses retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiSuccessResponse.class))),
        @ApiResponse(responseCode = "400", description = "Failed to retrieve listener statuses",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/status")
    ResponseEntity<?> getAllListenerStatus();

    @Operation(
        summary = "Get status of specific JMS listener",
        description = "Retrieves detailed status information for a specific JMS listener including running state, active status, and configuration."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listener status retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiSuccessResponse.class))),
        @ApiResponse(responseCode = "400", description = "Listener not found or failed to retrieve status",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/status/{listenerId}")
    ResponseEntity<?> getListenerStatus(
            @Parameter(description = "Unique listener identifier (e.g., queue1Listener)", example = "queue1Listener")
            @PathVariable String listenerId);

    @Operation(
        summary = "Start specific JMS listener",
        description = "Starts a specific JMS listener to begin processing messages from its associated queue."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listener started successfully or already running",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiSuccessResponse.class))),
        @ApiResponse(responseCode = "400", description = "Listener not found or failed to start",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/start/{listenerId}")
    ResponseEntity<?> startListener(
            @Parameter(description = "Unique listener identifier to start", example = "queue1Listener")
            @PathVariable String listenerId);

    @Operation(
        summary = "Stop specific JMS listener",
        description = "Stops a specific JMS listener to halt message processing from its associated queue."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listener stopped successfully or already stopped",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiSuccessResponse.class))),
        @ApiResponse(responseCode = "400", description = "Listener not found or failed to stop",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/stop/{listenerId}")
    ResponseEntity<?> stopListener(
            @Parameter(description = "Unique listener identifier to stop", example = "queue1Listener")
            @PathVariable String listenerId);

    @Operation(
        summary = "Start all JMS listeners",
        description = "Starts all configured JMS listeners simultaneously for bulk message processing activation."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bulk start operation completed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiSuccessResponse.class))),
        @ApiResponse(responseCode = "400", description = "Failed to start all listeners",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/start/all")
    ResponseEntity<?> startAllListeners();

    @Operation(
        summary = "Stop all JMS listeners",
        description = "Stops all configured JMS listeners simultaneously for bulk message processing deactivation."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bulk stop operation completed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiSuccessResponse.class))),
        @ApiResponse(responseCode = "400", description = "Failed to stop all listeners",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/stop/all")
    ResponseEntity<?> stopAllListeners();
}