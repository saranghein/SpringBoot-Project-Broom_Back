package com.kwhackathon.broom.chatRoom.controller;

import com.kwhackathon.broom.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public interface ChatRoomForEarlyDepartureOperation {
    // 로그인 유저가 참여 중인 채팅방 목록 조회
    @GetMapping("/list")
    ResponseEntity<?> getChatRoomList(@AuthenticationPrincipal User participant);

    // 해당 게시판, 로그인 유저의 채팅 목록 반환
    @GetMapping("/list/{earlyDepartureBoardId}")
    ResponseEntity<?> getChatRoom(
            @PathVariable Long earlyDepartureBoardId,
            @AuthenticationPrincipal User participant);

    // 채팅방 생성 또는 기존 채팅방 ID 반환
    @PostMapping("/create/{earlyDepartureBoardId}")
    ResponseEntity<?> createChatRoom(
            @PathVariable Long earlyDepartureBoardId,
            @AuthenticationPrincipal User participant);

    // 채팅방 삭제
    @DeleteMapping("/list/{earlyDepartureBoardId}")
    ResponseEntity<?> deleteChatRoom(
            @PathVariable String earlyDepartureBoardId,
            @AuthenticationPrincipal User user);
}
