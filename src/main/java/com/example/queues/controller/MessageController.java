package com.example.queues.controller;

import com.example.queues.api.MessageApi;
import com.example.queues.dto.ApiSuccessResponse;
import com.example.queues.dto.ApiErrorResponse;
import com.example.queues.service.MessageSenderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.jms.core.JmsTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.jms.*;

/**
 * REST Controller implementation for Apache Artemis JMS messaging operations.
 * Implements MessageApi interface for queue management and message processing.
 * Uses constructor injection with Lombok @AllArgsConstructor for dependency management.
 */
@RestController
@RequestMapping("/api/queues")
@AllArgsConstructor
public class MessageController implements MessageApi {

    private final MessageSenderService messageSenderService;
    private final JmsTemplate jmsTemplate;

    @Override
    public ResponseEntity<?> sendMessage(String rawQueueName, String message, String targetClient) {
        String queueName = extractQueueName(rawQueueName);
        if ("1".equals(targetClient)) {
            queueName = queueName + "?targetClient=1";
        }
        
        if (queueName == null || queueName.isBlank()) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("rawQueue", rawQueueName);
            if (targetClient != null) errorData.put("targetClient", targetClient);
            
            ApiErrorResponse errorResponse = ApiErrorResponse.error(
                "Invalid queue name", 
                "INVALID_QUEUE_NAME", 
                errorData
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        try {
            messageSenderService.sendCustomMessage(queueName, message);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("queue", queueName);
            responseData.put("rawQueue", rawQueueName);
            responseData.put("message", message);
            if (targetClient != null) responseData.put("targetClient", targetClient);
            
            ApiSuccessResponse<Map<String, Object>> successResponse = ApiSuccessResponse.success(
                "Message sent successfully", 
                responseData
            );
            return ResponseEntity.ok(successResponse);
            
        } catch (Exception e) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("rawQueue", rawQueueName);
            errorData.put("queue", queueName);
            if (targetClient != null) errorData.put("targetClient", targetClient);
            
            ApiErrorResponse errorResponse = ApiErrorResponse.error(
                "Failed to send message", 
                e.getMessage(), 
                500
            );
            errorResponse.setErrorCode("MESSAGE_SEND_FAILED");
            errorResponse.setErrorData(errorData);
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> browseQueueMessages(String rawDestination, String targetClient) {
        String queueName = extractQueueName(rawDestination);
        
        // Special handling for queue:// prefix - auto-append targetClient=1
        if (rawDestination.startsWith("/queue://") || rawDestination.startsWith("queue://")) {
            queueName = queueName.split("\\?")[0] + "?targetClient=1";
        } else if ("1".equals(targetClient)) {
            queueName = queueName + "?targetClient=1";
        }
        
        if (queueName == null || queueName.isBlank()) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("rawDestination", rawDestination);
            if (targetClient != null) errorData.put("targetClient", targetClient);
            
            ApiErrorResponse errorResponse = ApiErrorResponse.error(
                "Invalid queue name", 
                "INVALID_QUEUE_NAME", 
                errorData
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        try {
            System.out.println("Browsing queue: " + queueName  );
            List<String> messages = jmsTemplate.browse(queueName, (session, browser) -> {
                List<String> list = new ArrayList<>();
                java.util.Enumeration<?> enumeration = browser.getEnumeration();
                while (enumeration.hasMoreElements()) {
                    Message msg = (Message) enumeration.nextElement();
                    if (msg instanceof TextMessage textMessage) {
                        list.add(textMessage.getText());
                    } else {
                        list.add("Non-text message: " + msg.getJMSMessageID());
                    }
                }
                return list;
            });
            
            if (messages == null) messages = new ArrayList<>();
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("queueName", queueName);
            responseData.put("rawDestination", rawDestination);
            responseData.put("messages", messages);
            if (targetClient != null) responseData.put("targetClient", targetClient);
            
            ApiSuccessResponse<Map<String, Object>> successResponse = ApiSuccessResponse.success(
                "Queue browsed successfully", 
                responseData
            );
            return ResponseEntity.ok(successResponse);
            
        } catch (Exception e) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("queueName", queueName);
            errorData.put("rawDestination", rawDestination);
            if (targetClient != null) errorData.put("targetClient", targetClient);
            
            ApiErrorResponse errorResponse = ApiErrorResponse.error(
                "Failed to browse queue", 
                e.getMessage(), 
                500
            );
            errorResponse.setErrorCode("QUEUE_BROWSE_FAILED");
            errorResponse.setErrorData(errorData);
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Extracts the actual queue name from various input formats.
     * Handles URL decoding, queue:// prefix removal, and query parameter stripping.
     */
    private String extractQueueName(String rawDestination) {
        try {
            String decoded = URLDecoder.decode(rawDestination, StandardCharsets.UTF_8);
            System.out.println("Decoded destination: " + decoded );
            String queueName = decoded.startsWith("/") ? decoded.substring(1) : decoded;
            
            if (queueName.startsWith("queue://")) {
                queueName = queueName.substring(8);
            }
            
            int queryIndex = queueName.indexOf('?');
            if (queryIndex != -1) {
                queueName = queueName.substring(0, queryIndex);
            }
            
            return queueName;
        } catch (Exception e) {
            return rawDestination;
        }
    }
}
