package com.kwhackathon.broom.chatRoom.dto;

import com.kwhackathon.broom.chatRoom.entity.ChatRoomForTeam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ChatRoomListForTeamDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response{
        private List<ChatRoomForTeamDto.ResponseForGetChatRoomList> result; // 채팅방 목록
    }
}
