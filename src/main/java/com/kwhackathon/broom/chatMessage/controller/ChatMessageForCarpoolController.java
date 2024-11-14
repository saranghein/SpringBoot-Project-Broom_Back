package com.kwhackathon.broom.chatMessage.controller;

import com.kwhackathon.broom.chatMessage.dto.ChatMessageForCarpoolDto;
import com.kwhackathon.broom.chatMessage.dto.ReadStatusUpdateDto;
import com.kwhackathon.broom.chatMessage.entity.ChatMessageForCarpool;
import com.kwhackathon.broom.chatMessage.service.ChatMessageForCarpoolService;
import com.kwhackathon.broom.chatRoom.service.ChatRoomForCarpoolService;
import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class ChatMessageForCarpoolController implements ChatMessageForCarpoolOperation {
    private final ChatMessageForCarpoolService chatMessageService;
    private final ChatRoomForCarpoolService chatRoomForCarpoolService;

    private static final String CHAT_EXCHANGE_NAME = "chat.carpool.exchange";
    private static final String CHAT_QUEUE_NAME = "chat.carpool.queue";
    private final UserService userService;

    // 채팅방 입장
    @MessageMapping("chat.carpool.enter")
    @Override
    public void enterUser(@Payload ChatMessageForCarpoolDto.Request messageDto,@AuthenticationPrincipal User user) {


        // user id, sender id 확인
            if(!messageDto.getSenderId().equals(user.getUserId())){
                throw new IllegalArgumentException("전송 유저와 로그인 유저가 일치하지 않습니다.");
            }
            messageDto.setContent(messageDto.getSenderId() + "님 입장!");
            // 구독 채널에 전송
            chatMessageService.sendMessage(CHAT_EXCHANGE_NAME, "chat.carpool.room." + messageDto.getChatRoomId(), messageDto);


    }

    // 메시지 보내기
//    @MessageMapping("chat.carpool.message.{chatRoomId}")
//    @Override
//    public void sendMessage(@Payload ChatMessageForCarpoolDto.Request messageDto, @DestinationVariable String chatRoomId) {
//        // 전송 전 로그 확인
//        System.out.println("sendMessage 메서드 호출됨");
//        System.out.println("WebSocket 메시지 수신: " + chatRoomId);
//        // 구독 채널에 전송
//        chatMessageService.sendMessage(CHAT_EXCHANGE_NAME, "chat.carpool.room." + chatRoomId, messageDto);
//    }
    @MessageMapping("chat.carpool.message")
    @Override
    public void sendMessage(@Payload ChatMessageForCarpoolDto.Request messageDto, SimpMessageHeaderAccessor headerAccessor) {
        Principal principal = (Principal) headerAccessor.getSessionAttributes().get("principal");

        if (principal == null) {
            throw new IllegalArgumentException("Principal is null. User is not authenticated.");
        }
        System.out.println(messageDto.getSenderId());
        System.out.println(principal.getName());
//        if(!messageDto.getSenderId().equals(chatRoomForCarpoolService.findByChatRoomId(messageDto.getChatRoomId()).getAuthor())
//        && !messageDto.getSenderId().equals(chatRoomForCarpoolService.findByChatRoomId(messageDto.getChatRoomId()).getParticipant())){
//                throw new IllegalArgumentException("전송 유저와 로그인 유저가 일치하지 않습니다.");
//        }
            // user id, sender id 확인
            if(!messageDto.getSenderId().equals(principal.getName())){
                throw new IllegalArgumentException("전송 유저와 로그인 유저가 일치하지 않습니다.");
            }

            // 전송 전 로그 확인
            System.out.println("sendMessage 메서드 호출됨");
            System.out.println("WebSocket 메시지 수신: " + messageDto.getChatRoomId());

            // 구독 채널에 전송
            chatMessageService.sendMessage(CHAT_EXCHANGE_NAME, "chat.carpool.room." + messageDto.getChatRoomId(), messageDto);

            //chatRoomForCarpoolService.setLastMessage(messageDto.getChatRoomId(), messageDto,userService.loadUserByUsername(messageDto.getSenderId()));

    }

    // REST API로 채팅방에 메시지 전송 (테스트용)
    @Override
    public ResponseEntity<String> sendMessageViaApi(
            @PathVariable String chatRoomId,
            @RequestBody ChatMessageForCarpoolDto.Request messageDto) {

        // 서비스 메서드 호출
        chatMessageService.sendMessage(CHAT_EXCHANGE_NAME, "chat.carpool.room." + chatRoomId, messageDto);

        // 성공 응답 반환
        return ResponseEntity.ok("메시지가 전송되었습니다.");
    }

    @MessageMapping("chat.carpool.read.{chatRoomId}")
    @Override
    public void updateReadStatus(@Payload ReadStatusUpdateDto.Request readStatusUpdate, @DestinationVariable String chatRoomId,@AuthenticationPrincipal String userId) {
//TODO: 읽으면 되긴하는데 다시 안 읽으면 false로 교체하는 로직 필요
        chatMessageService.updateReadStatus(chatRoomId, readStatusUpdate.getUserId());


    }
}

