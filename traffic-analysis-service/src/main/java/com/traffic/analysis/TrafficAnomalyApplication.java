package com.traffic.analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TrafficAnomalyApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrafficAnomalyApplication.class, args);
    }
}