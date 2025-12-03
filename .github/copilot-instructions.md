<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

# Spring Boot Artemis Messaging Project

This is a Spring Boot web application that demonstrates Artemis messaging capabilities with:
- Segregated Artemis server and client architecture
- Embedded Artemis server running on default port 61616
- Multiple queue creation and management using JMS API
- JMS listeners and consumers with simplified connection handling
- Automatic message sending on application startup

## Key Technologies
- Spring Boot 3.2.0
- Apache Artemis 2.31.2
- JMS (Java Message Service)
- Maven

## Architecture
- **Embedded Artemis Server**: Configured for development on default port 61616
- **Client Configuration**: Uses standard JMS API without ServerLocator complexity
- **Queue Management**: JMS-based queue creation (queue1, queue2, queue3, queue4)
- **Message Listeners**: Dedicated listeners for each queue with concurrent processing
- **REST API**: Endpoints for sending custom messages and health checks

## Code Guidelines
- Follow Spring Boot best practices
- Use proper dependency injection with @Autowired
- Use Jakarta JMS API (jakarta.jms) instead of legacy javax.jms
- Implement proper error handling for JMS operations
- Use appropriate logging levels for debugging
- Follow Java naming conventions
- Default port 61616 is used for all connections
