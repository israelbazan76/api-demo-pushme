package com.bazan.demopushme.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FcmMessage {
    private Message message;

    @Data
    public static class Message {
        private Notification notification;
        private String token;
        private Object data;
    }

    @Data
    public static class Notification {
        private String title;
        private String body;
        private String image;
    }
}
