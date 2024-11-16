package com.kwhackathon.broom.chatRoom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ChatRoomListForCarpoolDto {


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response{
        private List<ChatRoomForCarpoolDto.ResponseForGetChatRoomList> result; // 채팅방 목록
    }
}
