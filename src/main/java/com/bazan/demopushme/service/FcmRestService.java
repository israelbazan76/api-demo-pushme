package com.bazan.demopushme.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bazan.demopushme.config.FCMConfig;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.*;

@Service
public class FcmRestService {

    private final RestTemplate restTemplate;
    private final GoogleCredentials credentials;
    private final String projectId;
    private final Gson gson = new Gson();

    @Autowired
    public FcmRestService(RestTemplate restTemplate, FCMConfig firebaseConfig) throws IOException {
        this.restTemplate = restTemplate;
        this.credentials = firebaseConfig.googleCredentials();
        this.projectId = firebaseConfig.getProjectId();
    }

    public String sendPushNotification(String deviceToken, String title, String body) throws IOException {
        // Obtener token de acceso
        credentials.refreshIfExpired();
        String accessToken = credentials.getAccessToken().getTokenValue();

        // Construir URL del endpoint FCM
        String fcmUrl = "https://fcm.googleapis.com/v1/projects/" + projectId + "/messages:send";

        // Construir cuerpo de la petición
        Map<String, Object> message = new HashMap<>();
        message.put("token", deviceToken);
        
        Map<String, String> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("body", body);
        message.put("notification", notification);

        Map<String, Object> request = new HashMap<>();
        request.put("message", message);

        // Configurar headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        // Enviar la solicitud
        HttpEntity<String> entity = new HttpEntity<>(gson.toJson(request), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(fcmUrl, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("FCM error: " + response.getStatusCode());
        }
    }
}