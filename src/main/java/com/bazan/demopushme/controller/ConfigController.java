package com.bazan.demopushme.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Value("${firebase.project-id}")
    private String projectId;

    @GetMapping
    public Map<String, String> getConfig() {
        return Map.of(
            "projectId", projectId,
            "loaded", "true"
        );
    }
}
