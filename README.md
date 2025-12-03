# Queues Listeners API - Spring Boot with Artemis

A Spring Boot web application demonstrating Apache Artemis messaging with segregated server and client architecture, queue creation, listeners, and automatic message processing.

## Features

- **Segregated Architecture**: Separate Artemis server and client configurations
- **Flexible Deployment**: Support for both embedded and external Artemis server
- **Default Port Configuration**: Artemis server runs on default port 61616
- **Client-based Queue Management**: Uses Artemis client API for queue creation
- **Multiple Queues**: Automatically creates 4 queues (queue1, queue2, queue3, queue4)
- **Message Listeners**: Dedicated consumers for each queue with concurrent processing
- **Automatic Messaging**: Sends sample messages to all queues on application startup
- **REST API**: Endpoints for sending custom messages and health monitoring
- **Maven Integration**: Uses Artemis Maven plugin for external broker management

## Architecture

### Server Configuration
- **Embedded Mode (Default)**: Starts Artemis server within the application
- **External Mode**: Connects to separately running Artemis server
- **Default Port**: 61616 (standard Artemis port - auto-configured)
- **Security**: Username/password authentication (admin/admin)

### Client Configuration  
- **Connection Factory**: Configurable Artemis connection from properties
- **Queue Creation**: Uses Jakarta JMS API for simpler integration
- **Modern JMS**: Uses Jakarta JMS (jakarta.jms) instead of legacy javax.jms
- **JMS Templates**: Configured for transactional messaging
- **Listeners**: Auto-configured with connection factory
- **Flexible Configuration**: Host and port configurable via properties

## Technologies Used

- Spring Boot 3.2.0
- Apache Artemis 2.31.2
- JMS (Java Message Service)
- Maven
- Java 17

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+

### Running the Application

#### Option 1: With Embedded Artemis Server (Default)
1. **Build the project:**
   ```bash
   mvn clean compile
   ```

2. **Run with embedded server:**
   ```bash
   mvn spring-boot:run
   ```
   This will start both the Artemis server on port 61616 and the Spring Boot application on port 8080.

#### Option 2: With External Artemis Server
1. **Set up external Artemis broker:**
   - Download and install Artemis from https://activemq.apache.org/components/artemis/
   - Create a broker instance: `artemis create mybroker --user admin --password admin`
   - Start the broker: `mybroker/bin/artemis run`

2. **Run application with external profile:**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=external
   ```

#### Access Points
- Application: `http://localhost:8080`
- Artemis Console: `http://localhost:8161` (when using external server)
- Health check: `GET http://localhost:8080/api/messages/health`

## Project Structure

```
src/
├── main/
│   ├── java/com/example/queues/
│   │   ├── QueuesListenersApiApplication.java    # Main application class
│   │   ├── config/
│   │   │   ├── ArtemisConfig.java                # Artemis client configuration
│   │   │   ├── ArtemisServerConfig.java          # Artemis server configuration  
│   │   │   └── QueueInitializer.java             # Client-based queue creation
│   │   ├── controller/
│   │   │   └── MessageController.java            # REST endpoints
│   │   ├── listener/
│   │   │   └── MessageListeners.java             # JMS message listeners
│   │   └── service/
│   │       └── MessageSenderService.java         # Message sending service
│   └── resources/
│       ├── application.properties                # Default configuration (embedded)
│       └── application-external.properties       # External server configuration
```

## Configuration

### Default Configuration (Embedded Server)
The application uses `application.properties` for embedded mode:

- **Server**: Embedded Artemis server on default port 61616
- **Client Connection**: Configurable via spring.artemis.host and spring.artemis.port
- **Security**: Username/password authentication (admin/admin)  
- **Queues**: Configurable queue names via artemis.queues property (comma-separated)
- **Queue Creation**: Auto-created via Jakarta JMS API
- **Persistence**: Disabled for development

### External Server Configuration  
Use `application-external.properties` profile for external server:

- **Connection**: Configurable Artemis connection (default: tcp://localhost:61616)
- **Server Management**: Separate Artemis broker process
- **Queue Creation**: Jakarta JMS-based using standard API

- **Server Port**: 8080
- **Artemis Mode**: Embedded
- **Queue Names**: queue1, queue2, queue3, queue4
- **Listener Concurrency**: 3-10 concurrent consumers per queue
- **Persistence**: Disabled (in-memory for development)

## API Endpoints

### Send Custom Message
```
POST /api/messages/send
Parameters:
  - queueName: Target queue name (queue1, queue2, queue3, queue4)
  - message: Message content

Example:
POST http://localhost:8080/api/messages/send?queueName=queue1&message=Hello%20World
```

### Health Check
```
GET /api/messages/health
```

## How It Works

1. **Application Startup**:
   - Embedded Artemis server starts
   - Four queues are automatically created
   - JMS listeners are initialized for each queue
   - Sample messages are sent to all queues

2. **Message Processing**:
   - Each queue has a dedicated listener
   - Messages are processed concurrently (3-10 consumers per queue)
   - Processing includes simulated work and logging

3. **Queue Management**:
   - Queues are created using Artemis core API
   - ANYCAST routing type for point-to-point messaging
   - Durable queues with automatic creation

## Artemis Maven Plugin

The project includes the Artemis Maven plugin for broker management:

```bash
# Create Artemis broker instance
mvn artemis:create

# Start broker (if using external instance)
mvn artemis:cli -Dargs="run"
```

## Development

### Building
```bash
mvn clean compile
```

### Testing
```bash
mvn test
```

### Packaging
```bash
mvn clean package
```

### Running with Different Profiles
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Monitoring and Logging

- **Console Logging**: Debug level for application components
- **JMS Logging**: Debug level for Spring JMS operations  
- **Artemis Logging**: Info level for server operations
- **Message Processing**: Real-time console output for sent/received messages

## Customization

### Configuring Queue Names
Update the `artemis.queues` property in `application.properties`:
```properties
# Default queues
artemis.queues=queue1,queue2,queue3,queue4

# Custom queues
artemis.queues=orders,payments,notifications,reports
```

### Adding New Queues
1. Update the `artemis.queues` property in configuration files
2. Add corresponding listener method in `MessageListeners.java`
3. Restart the application to create new queues

### Configuring Persistence
- Modify `ArtemisServerConfig.java` to enable persistence
- Update data directories in configuration
- Consider external broker for production use

## Troubleshooting

- **Port Conflicts**: Change server.port in application.properties
- **Queue Creation Issues**: Check Artemis server logs
- **Message Not Received**: Verify queue names and listener configuration
- **Performance Issues**: Adjust concurrency settings in configuration
