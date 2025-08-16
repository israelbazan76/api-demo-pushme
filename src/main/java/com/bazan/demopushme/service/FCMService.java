package com.bazan.demopushme.service;
import com.bazan.demopushme.model.PushNotificationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;


import java.io.FileInputStream;
import java.util.List;

@Service
public class FCMService {

    @Value("${firebase.server.key}")
    private String firebaseServerKey;

    //private final String FCM_API_URL = "https://fcm.googleapis.com/fcm/send";
    private final String FCM_API_URL = "https://fcm.googleapis.com/v1/projects/demopushme/messages:send";

    private static final Logger log = LoggerFactory.getLogger(FCMService.class);


    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    

    public void sendNotification_original(PushNotificationRequest request) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "key=" + firebaseServerKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("to", request.getTargetToken());

        Map<String, String> notification = new HashMap<>();
        notification.put("title", request.getTitle());
        notification.put("body", request.getBody());

        body.put("notification", notification);
        if (request.getData() != null) {
            body.put("data", request.getData());
        }

        log.info("body: " , body);
        log.info("headers: " , headers);


        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(FCM_API_URL, entity, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
          throw new RuntimeException("Error al enviar notificación: " + response.getBody());
        }

        
    }
    public void sendNotification(PushNotificationRequest request) throws Exception {
         String accessToken = getAccessToken();

        Map<String, Object> payload = Map.of(
                "message", Map.of(
                        "token", request.getTargetToken(),
                        "notification", Map.of(
                                "title", request.getTitle(),
                                "body", request.getBody()
                        )
                )
        );

        String jsonPayload = objectMapper.writeValueAsString(payload);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                FCM_API_URL,
                HttpMethod.POST,
                entity,
                String.class
        );

        System.out.println("FCM Response: " + response.getStatusCode() + " - " + response.getBody());
    }

    private String getAccessToken() throws Exception {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream("src/main/resources/demopushme-firebase-adminsdk-fbsvc-3882f1b084.json"))
                .createScoped(List.of("https://www.googleapis.com/auth/firebase.messaging"));
        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
