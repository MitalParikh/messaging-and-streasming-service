package com.example.queues.config;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import jakarta.jms.ConnectionFactory;

/**
 * Artemis client configuration
 * Connects to Artemis server using host and port from properties
 */
@Configuration
@EnableJms
public class ArtemisConfig {

    @Value("${spring.artemis.host:localhost}")
    private String artemisHost;

    @Value("${spring.artemis.port:61616}")
    private int artemisPort;

    @Value("${spring.artemis.user:admin}")
    private String artemisUser;

    @Value("${spring.artemis.password:admin}")
    private String artemisPassword;

    @Bean
    public ConnectionFactory connectionFactory() {
        // Use Artemis connection URL from properties
        String brokerUrl = String.format("tcp://%s:%d", artemisHost, artemisPort);
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connectionFactory.setUser(artemisUser);
        connectionFactory.setPassword(artemisPassword);
        
        System.out.println("Configured Artemis connection to: " + brokerUrl);
        return connectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory);
        template.setPubSubDomain(false); // Use queues, not topics
        template.setSessionTransacted(true);
        return template;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency("3-10");
        factory.setSessionTransacted(true);
        factory.setAutoStartup(true);
        return factory;
    }
}
