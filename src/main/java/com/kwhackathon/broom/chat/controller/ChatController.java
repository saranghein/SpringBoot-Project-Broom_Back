package com.kwhackathon.broom.chat.controller;

import com.kwhackathon.broom.chat.dto.ChatRequest;
import com.kwhackathon.broom.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public interface ChatController {
    // 채팅방 입장
    ResponseEntity<?> enterUser( String boardId, User user);

    // 채팅 보내기
    void sendMessage(ChatRequest.Message messageDto,SimpMessageHeaderAccessor headerAccessor) ;

    // 채팅방 입장시 이전 채팅 및 채팅방 정보 조회
    ResponseEntity<?> listChatMessages(String boardId,User user,int page, int size);

    // 읽음 처리(보류)
}
