package com.example.queues.config;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import jakarta.jms.Connection;
import jakarta.jms.JMSException;
import jakarta.jms.Session;
import java.util.List;

/**
 * Queue initialization using Artemis JMS API
 * Creates queues on the Artemis server using port from properties
 */
@Component
public class QueueInitializer implements ApplicationRunner {

    @Value("${spring.artemis.host:localhost}")
    private String artemisHost;

    @Value("${spring.artemis.port:61616}")
    private int artemisPort;

    @Value("${spring.artemis.user:admin}")
    private String artemisUser;

    @Value("${spring.artemis.password:admin}")
    private String artemisPassword;

    @Value("#{'${artemis.queues}'.split(',')}")
    private List<String> queueNames;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Add a small delay to ensure server is ready
        Thread.sleep(2000);
        createQueuesUsingJMS();
    }

    private void createQueuesUsingJMS() {
        Connection connection = null;
        Session session = null;

        try {
            // Use Artemis connection URL from properties
            String brokerUrl = String.format("tcp://%s:%d", artemisHost, artemisPort);
            try (ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl)) {
                connectionFactory.setUser(artemisUser);
                connectionFactory.setPassword(artemisPassword);

                System.out.println("Connecting to Artemis server at: " + brokerUrl);

                connection = connectionFactory.createConnection();
            }
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            connection.start();

            for (String queueName : queueNames) {
                try {
                    // Create queue using JMS session
                    session.createQueue(queueName);
                    System.out.println("Successfully created queue: " + queueName);
                } catch (JMSException e) {
                    if (e.getMessage() != null && e.getMessage().contains("already exists")) {
                        System.out.println("Queue already exists: " + queueName);
                    } else {
                        System.err.println("Error creating queue " + queueName + ": " + e.getMessage());
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error connecting to Artemis server: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up resources
            try {
                if (session != null) session.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                System.err.println("Error closing JMS resources: " + e.getMessage());
            }
        }
    }
}
