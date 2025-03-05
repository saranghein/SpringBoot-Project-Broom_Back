package com.kwhackathon.broom.participant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

public class ParticipantRequest {
    @AllArgsConstructor
    @Data
    public static class ExpellRequestInfo {
        private String expellId;//강퇴할 사용자 id
        private String boardId;// 채팅방 id
    }

    @AllArgsConstructor
    @Data
    public static class ExpellInfo {
        private String boardId;// 채팅방 id
    }
}
