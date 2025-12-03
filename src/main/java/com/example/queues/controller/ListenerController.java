package com.example.queues.controller;

import com.example.queues.api.ListenerApi;
import com.example.queues.dto.ApiSuccessResponse;
import com.example.queues.dto.ApiErrorResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller implementation for managing JMS listener lifecycle operations.
 * Implements ListenerApi interface for status checking and start/stop operations.
 * Uses constructor injection with Lombok @AllArgsConstructor for dependency management.
 */
@RestController
@RequestMapping("/api/listeners")
@AllArgsConstructor
public class ListenerController implements ListenerApi {

    private final JmsListenerEndpointRegistry jmsListenerEndpointRegistry;

    @Value("#{'${artemis.queues}'.split(',')}")
    private final List<String> queueNames;

    @Override
    public ResponseEntity<?> getAllListenerStatus() {
        try {
            Map<String, String> listenerStatuses = new HashMap<>();
            
            jmsListenerEndpointRegistry.getListenerContainerIds().forEach(listenerId -> {
                MessageListenerContainer container = jmsListenerEndpointRegistry.getListenerContainer(listenerId);
                
                if (container != null) {
                    listenerStatuses.put(listenerId, container.isRunning() ? "RUNNING" : "STOPPED");
                } else {
                    listenerStatuses.put(listenerId, "NOT_FOUND");
                }
            });
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("listeners", listenerStatuses);
            responseData.put("totalListeners", listenerStatuses.size());

            ApiSuccessResponse<Map<String, Object>> successResponse = ApiSuccessResponse.success(
                "Listener statuses retrieved successfully", 
                responseData
            );
            return ResponseEntity.ok(successResponse);
            
        } catch (Exception e) {
            ApiErrorResponse errorResponse = ApiErrorResponse.error(
                "Failed to get listener status", 
                e.getMessage(), 
                500
            );
            errorResponse.setErrorCode("LISTENER_STATUS_FAILED");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> getListenerStatus(String listenerId) {
        try {
            MessageListenerContainer container = jmsListenerEndpointRegistry.getListenerContainer(listenerId);
            
            if (container != null) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("listenerId", listenerId);
                responseData.put("listenerStatus", container.isRunning() ? "RUNNING" : "STOPPED");
                responseData.put("autoStartup", container.isAutoStartup());
                
                ApiSuccessResponse<Map<String, Object>> successResponse = ApiSuccessResponse.success(
                    "Listener status retrieved successfully", 
                    responseData
                );
                return ResponseEntity.ok(successResponse);
            } else {
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("listenerId", listenerId);
                
                ApiErrorResponse errorResponse = ApiErrorResponse.error(
                    "Listener not found", 
                    "LISTENER_NOT_FOUND", 
                    errorData
                );
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
        } catch (Exception e) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("listenerId", listenerId);
            
            ApiErrorResponse errorResponse = ApiErrorResponse.error(
                "Failed to get listener status", 
                e.getMessage(), 
                500
            );
            errorResponse.setErrorCode("LISTENER_STATUS_FAILED");
            errorResponse.setErrorData(errorData);
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> startListener(String listenerId) {
        try {
            MessageListenerContainer container = jmsListenerEndpointRegistry.getListenerContainer(listenerId);
            
            if (container != null) {
                String resultMessage;
                String statusValue;
                
                if (!container.isRunning()) {
                    container.start();
                    resultMessage = "Listener started successfully";
                    statusValue = "RUNNING";
                } else {
                    resultMessage = "Listener is already running";
                    statusValue = "RUNNING";
                }
                
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("listenerId", listenerId);
                responseData.put("listenerStatus", statusValue);
                
                ApiSuccessResponse<Map<String, Object>> successResponse = ApiSuccessResponse.success(
                    resultMessage, 
                    responseData
                );
                return ResponseEntity.ok(successResponse);
            } else {
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("listenerId", listenerId);
                
                ApiErrorResponse errorResponse = ApiErrorResponse.error(
                    "Listener not found", 
                    "LISTENER_NOT_FOUND", 
                    errorData
                );
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
        } catch (Exception e) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("listenerId", listenerId);
            
            ApiErrorResponse errorResponse = ApiErrorResponse.error(
                "Failed to start listener", 
                e.getMessage(), 
                500
            );
            errorResponse.setErrorCode("LISTENER_START_FAILED");
            errorResponse.setErrorData(errorData);
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> stopListener(String listenerId) {
        try {
            MessageListenerContainer container = jmsListenerEndpointRegistry.getListenerContainer(listenerId);
            
            if (container != null) {
                String resultMessage;
                String statusValue;
                
                if (container.isRunning()) {
                    container.stop();
                    resultMessage = "Listener stopped successfully";
                    statusValue = "STOPPED";
                } else {
                    resultMessage = "Listener is already stopped";
                    statusValue = "STOPPED";
                }
                
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("listenerId", listenerId);
                responseData.put("listenerStatus", statusValue);
                
                ApiSuccessResponse<Map<String, Object>> successResponse = ApiSuccessResponse.success(
                    resultMessage, 
                    responseData
                );
                return ResponseEntity.ok(successResponse);
            } else {
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("listenerId", listenerId);
                
                ApiErrorResponse errorResponse = ApiErrorResponse.error(
                    "Listener not found", 
                    "LISTENER_NOT_FOUND", 
                    errorData
                );
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
        } catch (Exception e) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("listenerId", listenerId);
            
            ApiErrorResponse errorResponse = ApiErrorResponse.error(
                "Failed to stop listener", 
                e.getMessage(), 
                500
            );
            errorResponse.setErrorCode("LISTENER_STOP_FAILED");
            errorResponse.setErrorData(errorData);
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> startAllListeners() {
        try {
            Map<String, String> results = new HashMap<>();
            
            for (String queueName : queueNames) {
                String listenerId = queueName + "Listener";
                MessageListenerContainer container = jmsListenerEndpointRegistry.getListenerContainer(listenerId);
                
                if (container != null) {
                    if (!container.isRunning()) {
                        container.start();
                        results.put(listenerId, "STARTED");
                    } else {
                        results.put(listenerId, "ALREADY_RUNNING");
                    }
                } else {
                    results.put(listenerId, "NOT_FOUND");
                }
            }
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("results", results);
            responseData.put("totalProcessed", results.size());

            ApiSuccessResponse<Map<String, Object>> successResponse = ApiSuccessResponse.success(
                "Bulk start operation completed", 
                responseData
            );
            return ResponseEntity.ok(successResponse);
            
        } catch (Exception e) {
            ApiErrorResponse errorResponse = ApiErrorResponse.error(
                "Failed to start all listeners", 
                e.getMessage(), 
                500
            );
            errorResponse.setErrorCode("BULK_START_FAILED");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> stopAllListeners() {
        try {
            Map<String, String> results = new HashMap<>();
            
            for (String queueName : queueNames) {
                String listenerId = queueName + "Listener";
                MessageListenerContainer container = jmsListenerEndpointRegistry.getListenerContainer(listenerId);
                
                if (container != null) {
                    if (container.isRunning()) {
                        container.stop();
                        results.put(listenerId, "STOPPED");
                    } else {
                        results.put(listenerId, "ALREADY_STOPPED");
                    }
                } else {
                    results.put(listenerId, "NOT_FOUND");
                }
            }
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("results", results);
            responseData.put("totalProcessed", results.size());

            ApiSuccessResponse<Map<String, Object>> successResponse = ApiSuccessResponse.success(
                "Bulk stop operation completed",
                responseData
            );
            return ResponseEntity.ok(successResponse);
            
        } catch (Exception e) {
            ApiErrorResponse errorResponse = ApiErrorResponse.error(
                "Failed to stop all listeners", 
                e.getMessage(), 
                500
            );
            errorResponse.setErrorCode("BULK_STOP_FAILED");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}