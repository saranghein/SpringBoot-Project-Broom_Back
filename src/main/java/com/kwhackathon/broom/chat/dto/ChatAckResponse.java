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
        private String status;    // 오류 상태
        private int statusCode;   // STOMP/WebSocket 상태 코드
        private String message;   // 오류 메시지

        // Request -> Response
        public static Response toResponse(ChatAckRequest.Request chatAckRequest){
            return new Response(
                    chatAckRequest.getStatus(),
                    chatAckRequest.getStatusCode(),
                    chatAckRequest.getMessage()
            );
        }
    }

}
