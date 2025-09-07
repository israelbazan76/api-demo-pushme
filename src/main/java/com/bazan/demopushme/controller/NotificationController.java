package com.bazan.demopushme.controller;

import com.bazan.demopushme.dto.PushNotificationResponse;
import com.bazan.demopushme.model.PushNotificationRequest;
import com.bazan.demopushme.model.TokenRegisterRequest;
import com.bazan.demopushme.service.FcmRestService;
import com.bazan.demopushme.service.TokenService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class NotificationController {

    private final FcmRestService fcmRestService;
    private final TokenService tokenService;

    public NotificationController(FcmRestService fcmRestService, TokenService tokenService) {
        this.fcmRestService = fcmRestService;
        this.tokenService = tokenService;
    }

    @PostMapping("/token/register")
    public String registerToken(@RequestBody TokenRegisterRequest request) {
        tokenService.registerOrUpdateToken(request);
        return "Token guardado correctamente";
    }

    @PostMapping("/notification/send")
    public PushNotificationResponse sendNotification(@RequestBody PushNotificationRequest request) {
        try {
            return fcmRestService.sendPushNotification(request.getTargetToken(), request.getTitle(), request.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new PushNotificationResponse(false, null, "Error enviando notificación", 0, LocalDateTime.now());
    }

    @PostMapping("/notification/sendToAll")
    public List<PushNotificationResponse> sendNotificationToAll(@RequestBody PushNotificationRequest request) {
        return fcmRestService.sendPushNotificationsToAll(request.getTitle(), request.getBody());
    }

    @GetMapping("/test")
    public String getTest() {
        return new Date().toString();
    }
}
