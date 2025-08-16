package com.bazan.demopushme.entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "device_tokens", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"deviceId"})
})
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false, length = 4096)
    private String firebaseToken;

    private LocalDateTime registeredAt;

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    @PrePersist
    @PreUpdate
    public void preSave() {
        this.registeredAt = LocalDateTime.now();
    }
}

