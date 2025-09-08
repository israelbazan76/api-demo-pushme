package com.bazan.demopushme.service;

import com.bazan.demopushme.entity.DeviceToken;
import com.bazan.demopushme.model.TokenRegisterRequest;
import com.bazan.demopushme.repository.DeviceTokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TokenService {

    private final DeviceTokenRepository tokenRepository;

    public TokenService(DeviceTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public void registerOrUpdateToken(TokenRegisterRequest request) {
        Optional<DeviceToken> existing = tokenRepository.findByDeviceId(request.getDeviceId());

        DeviceToken token = existing.orElseGet(DeviceToken::new);

        token.setUserId(request.getUserId());
        token.setDeviceId(request.getDeviceId());
        token.setFirebaseToken(request.getFirebaseToken());

        tokenRepository.save(token);
    }

    /**
     * Devuelve una lista de todos los dispositivos registrados en la base de datos.
     * @return Una lista de objetos DeviceToken.
     */
    public List<DeviceToken> getAllRegisteredDevices() {
        return tokenRepository.findAll();
    }
}
