package com.example.queues.listener;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListeners {

    @JmsListener(id = "queue1Listener", destination = "queue1", containerFactory = "jmsListenerContainerFactory")
    public void receiveMessageFromQueue1(String message) {
        System.out.println("Queue1 Listener received: " + message);
        // Process message for queue1
        processMessage("Queue1", message);
    }

    @JmsListener(id = "queue2Listener", destination = "queue2", containerFactory = "jmsListenerContainerFactory")
    public void receiveMessageFromQueue2(String message) {
        System.out.println("Queue2 Listener received: " + message);
        // Process message for queue2
        processMessage("Queue2", message);
    }

    @JmsListener(id = "queue3Listener", destination = "queue3", containerFactory = "jmsListenerContainerFactory")
    public void receiveMessageFromQueue3(String message) {
        System.out.println("Queue3 Listener received: " + message);
        // Process message for queue3
        processMessage("Queue3", message);
    }

    @JmsListener(id = "queue4Listener", destination = "queue4", containerFactory = "jmsListenerContainerFactory")
    public void receiveMessageFromQueue4(String message) {
        System.out.println("Queue4 Listener received: " + message);
        // Process message for queue4
        processMessage("Queue4", message);
    }

    private void processMessage(String queueName, String message) {
        try {
            // Simulate processing time
            Thread.sleep(100);
            System.out.println("Successfully processed message from " + queueName + ": " + message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Error processing message from " + queueName + ": " + e.getMessage());
        }
    }
}
