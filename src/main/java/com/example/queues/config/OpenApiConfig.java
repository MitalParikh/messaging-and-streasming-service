package com.example.queues.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Boot Artemis JMS API")
                        .description("REST API for Apache Artemis JMS messaging operations including queue management, message sending, and browsing")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("mital.parikh@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .tags(List.of(
                        new Tag()
                                .name("JMS APIs")
                                .description("Apache Artemis JMS messaging operations for queue management and message processing")
                ));
    }
}