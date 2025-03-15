package com.kwhackathon.broom.chat.service;

public interface ChatRoomService {

    // 채팅방 생성
    void createChatRoom(String boardId);

    // 채팅방 삭제
    void deleteChatRoom(String boardId);
}
