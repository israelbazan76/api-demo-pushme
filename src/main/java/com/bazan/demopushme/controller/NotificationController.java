package com.bazan.demopushme.controller;

import com.bazan.demopushme.model.PushNotificationRequest;
import com.bazan.demopushme.model.TokenRegisterRequest;
import com.bazan.demopushme.service.FCMService;
import com.bazan.demopushme.service.TokenService;

import java.util.Date;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class NotificationController {

    private final FCMService fcmService;
    private final TokenService tokenService;

    public NotificationController(FCMService fcmService, TokenService tokenService) {
        this.fcmService = fcmService;
        this.tokenService = tokenService;
    }

    @PostMapping("/token/register")
    public String registerToken(@RequestBody TokenRegisterRequest request) {
        tokenService.registerOrUpdateToken(request);
        return "Token guardado correctamente";
    }

    @PostMapping("/notification/send")
    public String sendNotification(@RequestBody PushNotificationRequest request) throws Exception {
        fcmService.sendNotification(request);
        return "Notificación enviada";
    }

    @GetMapping("/test")
    public String getTest() {
        return new Date().toString();
    }
}
