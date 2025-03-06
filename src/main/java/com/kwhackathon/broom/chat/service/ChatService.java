package com.kwhackathon.broom.chat.service;

import com.kwhackathon.broom.chat.dto.ChatRequest;
import com.kwhackathon.broom.chat.dto.ChatResponse;
import com.kwhackathon.broom.chat.entity.Chat;

import java.util.List;

public interface ChatService {

    // 메시지 저장
    Chat saveMessage(ChatRequest.Message messageDto,String senderId);
    // 메시지 전송
    void sendMessageToRoom(ChatResponse.Message messageDto,String boardId);
    // 채팅 메시지 조회
    List<Chat> findMessagesByBoardId(String boardId);

    ChatResponse.ChatRoomResponse getChatRoomInfo(String boardId,int page, int size);

}
