package com.example.cicddemo.dto;

public class AppInfoResponse {

    private String applicationName;
    private String version;
    private String description;
    private String environment;

    public AppInfoResponse(String applicationName, String version,
                           String description, String environment) {
        this.applicationName = applicationName;
        this.version = version;
        this.description = description;
        this.environment = environment;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public String getEnvironment() {
        return environment;
    }
}
