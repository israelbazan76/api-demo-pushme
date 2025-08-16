package com.bazan.demopushme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bazan.demopushme.entity.DeviceToken;

import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    Optional<DeviceToken> findByDeviceId(String deviceId);
}

