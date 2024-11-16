package com.kwhackathon.broom.chatMessage.controller;

import com.kwhackathon.broom.chatMessage.dto.ChatMessageForCarpoolDto;
import com.kwhackathon.broom.chatMessage.dto.ChatRoomForCarpoolDetailsDto;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ChatMessageForCarpoolController implements ChatMessageForCarpoolOperation {
    private final ChatMessageForCarpoolService chatMessageService;
    private final ChatRoomForCarpoolService chatRoomForCarpoolService;

    private static final String CHAT_EXCHANGE_NAME = "chat.carpool.exchange";
    private static final String CHAT_QUEUE_NAME = "chat.carpool.queue";
    private final UserService userService;

    // 채팅방 입장
    //@MessageMapping("chat.carpool.enter")
    @Override
    public void enterUser( ChatMessageForCarpoolDto.Request messageDto,SimpMessageHeaderAccessor headerAccessor) {
        Principal principal = (Principal) headerAccessor.getSessionAttributes().get("principal");


        // `principal`에서 `senderId` 가져오기
        String senderId = principal.getName();

        // `senderId`로 User 조회 후 `senderName` 가져오기
        User sender = (User) userService.loadUserByUsername(senderId);
        String senderName = sender.getUsername(); // User의 이름을 가져옴

        // 이전 메시지 조회
        List<ChatMessageForCarpool> previousMessages = chatMessageService.findPreviousMessages(messageDto.getChatRoomId());
        List<ChatMessageForCarpoolDto.Response> previousMessagesDto = previousMessages.stream()
                .map(ChatMessageForCarpoolDto.Response::fromEntity)
                .collect(Collectors.toList());
// 이전 메시지 개인에게 전송 (서비스 호출)
        //chatMessageService.sendPreviousMessagesToUser(senderId, messageDto.getChatRoomId());

        messageDto.setContent(messageDto.getSenderId() + "님 입장!");
        messageDto.setSenderName(senderName); // `setSenderName` 메서드가 필요합니다

        // 구독 채널에 전송
        chatMessageService.sendMessage(CHAT_EXCHANGE_NAME, "chat.carpool.room." + messageDto.getChatRoomId(), messageDto);


    }

    // 메시지 보내기
    //@MessageMapping("chat.carpool.message")
    @Override
    public void sendMessage( ChatMessageForCarpoolDto.Request messageDto, SimpMessageHeaderAccessor headerAccessor) {
        Principal principal = (Principal) headerAccessor.getSessionAttributes().get("principal");

        if (principal == null) {
            throw new IllegalArgumentException("Principal is null. User is not authenticated.");
        }

    // `principal`에서 `senderId` 가져오기
        String senderId = principal.getName();

        // `senderId`로 User 조회 후 `senderName` 가져오기
        User sender = (User) userService.loadUserByUsername(senderId);
        String senderName = sender.getUsername(); // User의 이름을 가져옴

        // `Request` DTO에 `senderName` 설정 (필드가 있는 경우)
        messageDto.setSenderName(senderName);
        messageDto.setSenderId(senderId);
            // 전송 전 로그 확인
            System.out.println("sendMessage 메서드 호출됨");
            System.out.println("WebSocket 메시지 수신: " + messageDto.getChatRoomId());

            // 구독 채널에 전송
            chatMessageService.sendMessage(CHAT_EXCHANGE_NAME, "chat.carpool.room." + messageDto.getChatRoomId(), messageDto);

            //chatRoomForCarpoolService.setLastMessage(messageDto.getChatRoomId(), messageDto,userService.loadUserByUsername(messageDto.getSenderId()));

    }

    // REST API로 채팅방에 메시지 전송 (테스트용)
//    @Override
//    public ResponseEntity<String> sendMessageViaApi(
//             String roomId,
//             ChatMessageForCarpoolDto.Request messageDto) {
//
//        // 서비스 메서드 호출
//        chatMessageService.sendMessage(CHAT_EXCHANGE_NAME, "chat.carpool.room." + roomId, messageDto);
//
//        // 성공 응답 반환
//        return ResponseEntity.ok("메시지가 전송되었습니다.");
//    }

    @Override
    public void updateReadStatus( ReadStatusUpdateDto.Request readStatusUpdate, String roomId, String userId) {
//TODO
        chatMessageService.updateReadStatus(roomId, readStatusUpdate.getUserId());
// 읽음 상태를 웹소켓을 통해 전송하여 클라이언트에 알림
//        chatRoomForCarpoolService.sendReadStatusUpdate(chatRoomId, userId);

    }

    @Override
    public ResponseEntity<?> listChatMessages( String roomId, User user) {
        try{
            ChatRoomForCarpoolDetailsDto response = chatMessageService.getChatRoomDetails(roomId, user.getUserId());
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("채팅방 정보를 갖고오는데 실패했습니다.");
        }
    }
}

