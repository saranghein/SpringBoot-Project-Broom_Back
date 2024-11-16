package com.kwhackathon.broom.chatRoom.controller;

import com.kwhackathon.broom.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public interface ChatRoomForTeamOperation {
    // 로그인 유저가 참여 중인 채팅방 목록 조회
    @GetMapping("/list")
    ResponseEntity<?> getChatRoomList(@AuthenticationPrincipal User participant);

    // 채팅방 생성 또는 기존 채팅방 ID 반환
    @PostMapping("/create/{teamBoardId}")
    ResponseEntity<?> createChatRoom(
            @PathVariable Long teamBoardId,
            @AuthenticationPrincipal User participant);

    // 채팅방 삭제
    @DeleteMapping("/list/{chatRoomId}")
    ResponseEntity<?> deleteChatRoom(
            @PathVariable String chatRoomId,
            @AuthenticationPrincipal User user);
}
