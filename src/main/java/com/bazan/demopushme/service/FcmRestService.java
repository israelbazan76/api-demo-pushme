package com.bazan.demopushme.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bazan.demopushme.config.FCMConfig;
import com.bazan.demopushme.dto.PushNotificationResponse;
import com.bazan.demopushme.entity.DeviceToken;
import com.bazan.demopushme.repository.DeviceTokenRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FcmRestService {

    private final RestTemplate restTemplate;
    private final GoogleCredentials credentials;
    private final String projectId;
    private final Gson gson = new Gson();
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000; // 2 segundos de retraso
    @Autowired
    private DeviceTokenRepository deviceTokenRepository;

    public FcmRestService(RestTemplate restTemplate, FCMConfig firebaseConfig) throws IOException {
        this.restTemplate = restTemplate;
        this.credentials = firebaseConfig.googleCredentials();
        this.projectId = firebaseConfig.getProjectId();
    }

    public PushNotificationResponse sendPushNotification(String deviceToken, String title, String body) throws IOException {
             // Bucle de reintentos.
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
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

                // Verificar la respuesta
                if (response.getStatusCode().is2xxSuccessful()) {
                    // Envío exitoso, devolver respuesta de éxito.
                    return new PushNotificationResponse(true, response.getBody(), null, i, LocalDateTime.now());
                } else {
                    // La solicitud no fue exitosa, lanzar excepción para reintentar.
                    throw new RuntimeException("FCM error: " + response.getStatusCode());
                }

            } catch (Exception e) {
                // Capturar cualquier excepción de la solicitud.
                System.err.println("Intento de envío fallido #" + (i + 1) + ": " + e.getMessage());

                if (i < MAX_RETRIES - 1) {
                    try {
                        System.out.println("Esperando " + RETRY_DELAY_MS + " ms antes de reintentar...");
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return new PushNotificationResponse(false, null, "Proceso de reintentos interrumpido.", i, LocalDateTime.now());
                    }
                } else {
                    // Todos los reintentos han fallado, devolver respuesta de error.
                    return new PushNotificationResponse(false, null, "Fallaron todos los reintentos. Error: " + e.getMessage(), i, LocalDateTime.now());
                }
            }
        }
        // Este punto solo se alcanza si el bucle termina sin éxito, lo cual
        // es manejado por el return dentro del último 'else' del catch.
        return new PushNotificationResponse(false, null, "Error desconocido después de reintentos.", MAX_RETRIES, LocalDateTime.now());
    }

    /**
     * Envía una notificación push a todos los dispositivos registrados en la base de datos.
     * Utiliza el método sendPushNotification para cada token, recogiendo los resultados.
     *
     * @param title Título de la notificación.
     * @param body  Cuerpo de la notificación.
     * @return Una lista de PushNotificationResponse, una por cada token.
     */
    public List<PushNotificationResponse> sendPushNotificationsToAll(String title, String body) {
         System.out.println("sendPushNotificationsToAll(title:" + title + ",body: " + body);
        // Obtener todos los tokens del repositorio.
        List<DeviceToken> deviceTokens = deviceTokenRepository.findAll();
        List<PushNotificationResponse> results = new ArrayList<>();

        for (DeviceToken deviceToken : deviceTokens) {
            try {
                System.out.println("try-inicio sendPushNotificationsToAll for token: " + deviceToken.getFirebaseToken());
                // Reutiliza el método existente para enviar la notificación a un solo token.
                PushNotificationResponse response = sendPushNotification(deviceToken.getFirebaseToken(), title, body);
                System.out.println("try-fin sendPushNotificationsToAll ,response.status: " + response.isSuccess());
                 // Añadir la respuesta a la lista de resultados.
                results.add(response);
            } catch (Exception e) {
                // En caso de que sendPushNotification falle, capturamos el error
                // y continuamos con el siguiente token para evitar que el bucle se detenga.
                System.err.println("Error al enviar notificación al dispositivo " + deviceToken.getDeviceId() + ": " + e.getMessage());
                results.add(new PushNotificationResponse(false, null, "Error al enviar: " + e.getMessage(), 0, LocalDateTime.now()));
            }
        }
        return results;
    }
}