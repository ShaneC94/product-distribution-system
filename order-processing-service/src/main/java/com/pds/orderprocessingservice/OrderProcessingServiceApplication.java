package com.pds.orderprocessingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate; // Simple HTTP client

@SpringBootApplication
public class OrderProcessingServiceApplication {

	public static void main(String[] args) {

        SpringApplication.run(OrderProcessingServiceApplication.class, args);
	}

    // A simple, blocking HTTP client for inter-service communication
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
