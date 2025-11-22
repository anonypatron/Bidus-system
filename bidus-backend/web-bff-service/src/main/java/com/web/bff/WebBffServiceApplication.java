package com.web.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class WebBffServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebBffServiceApplication.class, args);
    }
}
