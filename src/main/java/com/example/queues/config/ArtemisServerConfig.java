package com.example.queues.config;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyAcceptorFactory;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for standalone Artemis server
 * This will start Artemis on default port 61616
 */
@Configuration
@ConditionalOnProperty(name = "artemis.server.embedded.enabled", havingValue = "true", matchIfMissing = false)
public class ArtemisServerConfig {

    @Bean
    public EmbeddedActiveMQ embeddedArtemisServer() throws Exception {
        ConfigurationImpl configuration = new ConfigurationImpl();
        
        // Server configuration
        configuration.setPersistenceEnabled(false);
        configuration.setSecurityEnabled(false);  // Disabled for development simplicity
        configuration.setJournalDirectory("target/artemis-data/journal");
        configuration.setBindingsDirectory("target/artemis-data/bindings");
        configuration.setLargeMessagesDirectory("target/artemis-data/largemessages");
        
        // Configure acceptor for default port 61616
        Map<String, Object> acceptorParams = new HashMap<>();
        acceptorParams.put("host", "localhost");
        acceptorParams.put("port", "61616");
        
        TransportConfiguration acceptorConfig = new TransportConfiguration(
            NettyAcceptorFactory.class.getName(), 
            acceptorParams, 
            "netty-acceptor"
        );
        configuration.getAcceptorConfigurations().add(acceptorConfig);
        
        // Security is handled by Spring Boot configuration
        // For embedded development, we disable security for simplicity
        configuration.setSecurityEnabled(false);
        
        EmbeddedActiveMQ server = new EmbeddedActiveMQ();
        server.setConfiguration(configuration);
        
        System.out.println("Starting embedded Artemis server on port 61616...");
        server.start();
        System.out.println("Embedded Artemis server started successfully!");
        
        return server;
    }
}
