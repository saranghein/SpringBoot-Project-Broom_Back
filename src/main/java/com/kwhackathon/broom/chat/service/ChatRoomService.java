package com.kwhackathon.broom.chat.service;

public interface ChatRoomService {

    // 채팅방 생성
    void createChatRoom(String boardId);

    // 채팅방 삭제
    void deleteChatRoom(String boardId);

    // 채팅방당 유저방 생성
    void createUserRoom(String userNickname, String boardId);

    // 채팅방당 유저방 삭제
    void deleteUserRoom(String userNickname, String boardId);

    // 채팅방의 모든 유저의 큐 삭제
    void deleteAllUserRooms(String boardId);
}
