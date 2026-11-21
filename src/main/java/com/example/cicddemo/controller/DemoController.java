package com.example.cicddemo.controller;

import com.example.cicddemo.dto.AppInfoResponse;
import com.example.cicddemo.service.DemoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class DemoController {

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping("/")
    public String home() {
        return demoService.getWelcomeMessage();
    }

    @GetMapping("/info")
    public AppInfoResponse getInfo() {
        return demoService.getApplicationInfo();
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return demoService.getHealthStatus();
    }
}