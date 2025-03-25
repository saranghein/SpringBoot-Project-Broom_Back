package com.kwhackathon.broom.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatAckRequest {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request{
        private String status;
        private int statusCode;
        private String message;
        private String senderId;
        private String boardId;
    }
}
