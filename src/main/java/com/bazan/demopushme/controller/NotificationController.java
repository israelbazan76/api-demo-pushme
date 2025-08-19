package com.bazan.demopushme.controller;

import com.bazan.demopushme.model.PushNotificationRequest;
import com.bazan.demopushme.model.TokenRegisterRequest;
import com.bazan.demopushme.service.FcmRestService;
import com.bazan.demopushme.service.TokenService;

import java.util.Date;

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
        return "Twwwwwwoken guardado correctamente";
    }

    @PostMapping("/notification/send")
    public String sendNotification(@RequestBody PushNotificationRequest request) {
        try {
            return fcmRestService.sendPushNotification(request.getTargetToken(), request.getTitle(), request.getBody());
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/test")
    public String getTest() {
        return new Date().toString();
    }
}
