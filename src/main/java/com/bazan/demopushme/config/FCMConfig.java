package com.bazan.demopushme.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;

@Configuration
public class FCMConfig {
    @Value("${firebase.project-id}")
    private String projectId;

    @Value("${firebase.private-key}")
    private String privateKey;

    @Value("${firebase.client-email}")
    private String clientEmail;

     @Value("${firebase.private.key.id}")
    private String privateKeyId;

    @Value("${firebase.token.uri}")
    private String tokenUri;

    @Value("${firebase.client-id}")
    private String clientId;


    @Bean
    public GoogleCredentials googleCredentials() {
        try {
            String credentialsJson = String.format(
                "{\"type\":\"service_account\"," +
                "\"project_id\":\"%s\"," +
                "\"private_key_id\":\"%s\"," +
                "\"private_key\":\"%s\"," +
                "\"client_email\":\"%s\"," +
                "\"token_uri\":\"%s\"," +
                "\"client_id\":\"%s\"}",
                Objects.requireNonNull(projectId, "Firebase Project ID must not be null"),
                Objects.requireNonNull(privateKeyId, "Private Key ID must not be null"),
                Objects.requireNonNull(privateKey, "Private Key must not be null").replace("\n", "\\n"),
                Objects.requireNonNull(clientEmail, "Client Email must not be null"),
                Objects.requireNonNull(tokenUri, "Token URI must not be null"),
                Objects.requireNonNull(clientId, "Client ID must not be null")
            );

            return GoogleCredentials.fromStream(new ByteArrayInputStream(credentialsJson.getBytes()))
                .createScoped(List.of("https://www.googleapis.com/auth/firebase.messaging"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create GoogleCredentials", e);
        }
    }

    public String getProjectId() {
        return projectId;
    }
    
}
