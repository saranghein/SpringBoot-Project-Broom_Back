package com.kwhackathon.broom.chatMessage.dto;

import lombok.Data;

public class ReadStatusUpdateDto {

    public ReadStatusUpdateDto(String chatRoomId, String userId) {
    }

    @Data
    public static class Request {
        private String chatRoomId;   // 채팅방 ID
        private String userId;     // 읽은 사용자 ID
    }
}
