package com.bazan.demopushme.dto;

/**
 * Clase de respuesta que encapsula el resultado del envío de la notificación push.
 * Proporciona información sobre si el envío fue exitoso, el ID del mensaje
 * si aplica, un mensaje de error y el número de reintentos realizados.
 */
public class PushNotificationResponse {
    private final boolean success;
    private final String messageId;
    private final String error;
    private final int retries;

    public PushNotificationResponse(boolean success, String messageId, String error, int retries) {
        this.success = success;
        this.messageId = messageId;
        this.error = error;
        this.retries = retries;
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
}