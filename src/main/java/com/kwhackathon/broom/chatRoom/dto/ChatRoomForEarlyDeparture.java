package com.kwhackathon.broom.chatRoom.dto;

import com.kwhackathon.broom.earlyDepartureBoard.entity.EarlyDepartureBoard;
import com.kwhackathon.broom.user.entity.User;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

public class ChatRoomForEarlyDeparture {
    @Data
    public static class Request{
        private String chatRoomId;
        private User userId;
        private EarlyDepartureBoard earlyDepartureBoardId;
    }

    @Getter
    public static class Response{
        private String chatRoomId;
        private LocalDateTime createAt;
        private boolean isRead;
    }
}
