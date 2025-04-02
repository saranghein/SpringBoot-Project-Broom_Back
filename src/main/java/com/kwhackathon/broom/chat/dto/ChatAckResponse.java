package com.kwhackathon.broom.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatAckResponse {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        // SUCCESS, ERROR, UNDETECTED
        private String status;
        private int statusCode;
        private String message;
        private String boardId;

    }

}
