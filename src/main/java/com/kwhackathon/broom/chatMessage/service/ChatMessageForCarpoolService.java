package com.kwhackathon.broom.chatMessage.service;


import com.kwhackathon.broom.carpool.entity.CarpoolBoard;
import com.kwhackathon.broom.chatMessage.dto.ChatMessageForCarpoolDto;
import com.kwhackathon.broom.chatMessage.dto.ChatRoomForCarpoolDetailsDto;
import com.kwhackathon.broom.chatMessage.dto.ReadStatusUpdateDto;
import com.kwhackathon.broom.chatMessage.entity.ChatMessageForCarpool;
import com.kwhackathon.broom.chatMessage.repository.ChatMessageForCarpoolRepository;
import com.kwhackathon.broom.chatRoom.entity.ChatRoomForCarpool;
import com.kwhackathon.broom.chatRoom.repository.ChatRoomForCarpoolRepository;
import com.kwhackathon.broom.chatRoom.service.ChatRoomForCarpoolService;
import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ChatMessageForCarpoolService {

    private static final String CARPOOL_EXCHANGE_NAME = "chat.carpool.exchange";
    private static final String CHAT_QUEUE_NAME = "chat.carpool.queue";

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomForCarpoolRepository chatRoomForCarpoolRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ChatMessageForCarpoolRepository chatMessageRepository;
    private final ChatRoomForCarpoolService chatRoomService;
    private final UserService userService;
    private final ChatRoomForCarpoolService chatRoomForCarpoolService;

    public void sendMessage(String exchange, String routingKey, ChatMessageForCarpoolDto.Request messageDto) {
    // chatRoomId를 통해 ChatRoomForCarpool 조회
    ChatRoomForCarpool chatRoom = chatRoomService.findByChatRoomId(messageDto.getChatRoomId());

    // ChatRoomForCarpool에서 CarpoolBoard와 User(author)를 가져오기
    CarpoolBoard carpoolBoard = chatRoom.getCarpoolBoard();
    User author = carpoolBoard.getUser();
    UserDetails sender = userService.loadUserByUsername(messageDto.getSenderId());

    // ChatRoomForCarpool 엔티티 생성 또는 조회
    ChatMessageForCarpool message = messageDto.toEntity(chatRoom,  sender);
    chatMessageRepository.save(message);
    System.out.println("RabbitMQ 전송: Exchange=" + exchange + ", RoutingKey=" + routingKey + ", Content=" + messageDto.getContent());
    // ChatRoomForCarpool에 마지막 메시지 설정
    chatRoom.setLastChatMessageForCarpool(message); // 마지막 메시지 엔티티 업데이트

        // 작성자와 참여자를 구분하여 안 읽은 메시지 수 증가
        if (chatRoom.getAuthor().getUserId().equals(messageDto.getSenderId())) {
            chatRoom.setUnreadMessageCountForParticipant(chatRoom.getUnreadMessageCountForParticipant() + 1);
        } else {
            chatRoom.setUnreadMessageCountForAuthor(chatRoom.getUnreadMessageCountForAuthor() + 1);
        }
    chatRoomForCarpoolRepository.save(chatRoom);              // ChatRoomForCarpool 업데이트

    // RabbitMQ로 메시지 전송
    rabbitTemplate.convertAndSend(exchange, routingKey, ChatMessageForCarpoolDto.Response.fromEntity(message));

    // WebSocket으로 클라이언트에게 메시지 전송
//    messagingTemplate.convertAndSend("/topic/chat.carpool.room." + messageDto.getChatRoomId(), messageDto);

}
    public List<ChatMessageForCarpool> findPreviousMessages(String chatRoomId) {
        return chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
    }

    // 읽음 상태 업데이트
    public void updateReadStatus(String chatRoomId, String userId) {
        ChatRoomForCarpool chatRoom = chatRoomForCarpoolRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat Room not found"));

        boolean isUpdated = false;

        if (chatRoom.getAuthor().getUserId().equals(userId)) {
            chatRoom.setReadByAuthor(true);
            chatRoom.setUnreadMessageCountForAuthor(0); // 작성자의 안 읽은 메시지 수 초기화
            isUpdated = true;
        } else if (chatRoom.getParticipant().getUserId().equals(userId)) {
            chatRoom.setReadByParticipant(true);
            chatRoom.setUnreadMessageCountForParticipant(0); // 참여자의 안 읽은 메시지 수 초기화
            isUpdated = true;
        }

        if (isUpdated) {
            chatRoomForCarpoolRepository.save(chatRoom);

            // WebSocket 메시지 전송
            ReadStatusUpdateDto readStatusUpdate = new ReadStatusUpdateDto(chatRoomId, userId);
            sendReadStatusUpdateMessage(chatRoomId, readStatusUpdate);
        }
    }
    // WebSocket 메시지 전송 메서드
    private void sendReadStatusUpdateMessage(String chatRoomId, ReadStatusUpdateDto readStatusUpdate) {
        // RabbitMQ는 비동기 이므로
        // 실시간 반영을 위해 WebSocket template으로 전송
        messagingTemplate.convertAndSend("chat.carpool.room." + chatRoomId, readStatusUpdate);
    }

//    public void sendPreviousMessagesToUser(String senderId, String chatRoomId) {
//        for (ChatMessageForCarpoolDto.Response previousMessage : previousMessages) {
//            messagingTemplate.convertAndSendToUser(
//                    senderId, // 수신자 ID
//                    "/queue/chat.carpool.room." + chatRoomId, // 개인 전송 경로
//                    previousMessage
//            );
//        }
//    }

    public ChatRoomForCarpoolDetailsDto getChatRoomDetails(String chatRoomId, String currentUserId) {
        // 현재 유저 정보
        User currentUser = (User) userService.loadUserByUsername(currentUserId);

        // 채팅방 정보
        ChatRoomForCarpool chatRoom = chatRoomService.findByChatRoomId(chatRoomId);

        // 상대방 정보 가져오기
        User opponent = chatRoom.getAuthor().getUserId().equals(currentUser.getUserId())
                ? chatRoom.getParticipant() // 상대방이 참여자인 경우
                : chatRoom.getAuthor();     // 상대방이 작성자인 경우

        // 이전 메시지 가져오기
        List<ChatMessageForCarpool> previousMessages = findPreviousMessages(chatRoomId);
        List<ChatMessageForCarpoolDto.ResponseForDetail> previousMessagesDto = previousMessages.stream()
                .map(ChatMessageForCarpoolDto.ResponseForDetail::fromEntity)
                .collect(Collectors.toList());

        // 전역 몇 년 차인지 계산
        int currentYear = LocalDate.now().getYear();
        int yearsSinceDischarge = currentYear - currentUser.getDischargeYear();

        //육군, 해군, ...
        String militaryChaplain=  opponent.getMilitaryChaplain().toString();

        // DTO 반환
        return new ChatRoomForCarpoolDetailsDto(
                chatRoom.getCarpoolBoard().getTitle(),
                opponent.getNickname(),
                yearsSinceDischarge,
                militaryChaplain,
                previousMessagesDto
        );
    }

}
