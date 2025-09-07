package com.bazan.demopushme.dto;

import java.time.LocalDateTime;

/**
 * Clase de respuesta que encapsula el resultado del envío de la notificación push.
 * Proporciona información sobre si el envío fue exitoso, el ID del mensaje
 * si aplica, un mensaje de error y el número de reintentos realizados.
 */
public class PushNotificationResponse {
    private boolean success;
    private String messageId;
    private String error;
    private int retries;
    private LocalDateTime timestamp;

    public PushNotificationResponse(boolean success, String messageId, String error, int retries, LocalDateTime timestamp) {
        this.success = success;
        this.messageId = messageId;
        this.error = error;
        this.retries = retries;
        this.timestamp = timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getError() {
        return error;
    }

    public int getRetries() {
        return retries;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
