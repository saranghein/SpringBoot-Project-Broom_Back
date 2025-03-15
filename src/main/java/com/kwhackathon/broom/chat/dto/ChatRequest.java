package com.kwhackathon.broom.chat.dto;

import com.kwhackathon.broom.board.entity.Board;
import com.kwhackathon.broom.chat.entity.Chat;
import com.kwhackathon.broom.participant.entity.Participant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


public class ChatRequest {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message{
        private String boardId; // 채팅방 ID (게시판 ID)
        private String message; // 보낼 메시지

        // Request.Message -> Chat
        public static Chat toEntity(Message message, Participant participant, Board board){
            return new Chat(
                    null,
                    LocalDateTime.now(),
                    message.getMessage(),
                    participant,
                    board
            );
        }

    }

}
