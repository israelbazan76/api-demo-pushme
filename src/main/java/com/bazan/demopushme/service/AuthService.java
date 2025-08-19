package com.bazan.demopushme.service;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AuthService {

    private final GoogleCredentials credentials;

    
    public AuthService(GoogleCredentials credentials) {
        this.credentials = credentials;
    }

    public String getAccessToken() throws IOException {
        credentials.refreshIfExpired();
        return credentials.getAccessToken().getTokenValue();
    }
}
