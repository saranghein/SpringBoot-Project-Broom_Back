package com.kwhackathon.broom.chatMessage.controller;

import com.kwhackathon.broom.chatMessage.dto.ChatMessageForCarpoolDto;
import com.kwhackathon.broom.chatMessage.dto.ReadStatusUpdateDto;
import com.kwhackathon.broom.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

public interface ChatMessageForCarpoolOperation {
    // 채팅방 입장
    @MessageMapping("chat.carpool.enter")
    void enterUser(@Payload ChatMessageForCarpoolDto.Request messageDto,@AuthenticationPrincipal User user);

    // 메시지 보내기
    @MessageMapping("chat.carpool.message")
    public void sendMessage(@Payload ChatMessageForCarpoolDto.Request messageDto, SimpMessageHeaderAccessor headerAccessor) ;

    // REST API로 채팅방에 메시지 전송
    @PostMapping("/carpool/chat/message/{chatRoomId}")
    ResponseEntity<String> sendMessageViaApi(
            @PathVariable String chatRoomId,
            @RequestBody ChatMessageForCarpoolDto.Request messageDto);

    @MessageMapping("chat.carpool.read.{chatRoomId}")
    void updateReadStatus(@Payload ReadStatusUpdateDto.Request readStatusUpdate, @DestinationVariable String chatRoomId,@AuthenticationPrincipal String userId);
}
