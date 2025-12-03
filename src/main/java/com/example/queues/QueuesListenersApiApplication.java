package com.example.queues;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJms
@EnableAsync
public class QueuesListenersApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueuesListenersApiApplication.class, args);
    }
}
