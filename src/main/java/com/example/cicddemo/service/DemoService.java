package com.example.cicddemo.service;

import com.example.cicddemo.dto.AppInfoResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class DemoService {

    public String getWelcomeMessage() {
        return "Welcome Sanket Godse to Spring Boot CI/CD Demo!";
    }

    public AppInfoResponse getApplicationInfo() {
        return new AppInfoResponse(
                "CI/CD Demo Application",
                "1.0.0",
                "Spring Boot application deployed using Jenkins CI/CD",
                "Development"
        );
    }

    public Map<String, Object> getHealthStatus() {
        return Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now(),
                "service", "cicd-demo"
        );
    }
}