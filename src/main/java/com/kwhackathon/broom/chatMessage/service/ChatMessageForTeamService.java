package com.kwhackathon.broom.chatMessage.service;

import com.kwhackathon.broom.chatMessage.dto.ChatMessageForTeamDto;
import com.kwhackathon.broom.chatMessage.dto.ReadStatusUpdateDto;
import com.kwhackathon.broom.chatMessage.repository.ChatMessageForTeamRepository;
import com.kwhackathon.broom.chatRoom.entity.ChatRoomForTeam;
import com.kwhackathon.broom.chatRoom.repository.ChatRoomForTeamRepository;
import com.kwhackathon.broom.chatRoom.service.ChatRoomForTeamService;
import com.kwhackathon.broom.team.entity.TeamBoard;
import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageForTeamService {
    private static final String CARPOOL_EXCHANGE_NAME = "chat.team.exchange";
    private static final String CHAT_QUEUE_NAME = "chat.team.queue";

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomForTeamRepository chatRoomForTeamRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ChatMessageForTeamRepository chatMessageForTeamRepository;
    private final ChatRoomForTeamService chatRoomService;
    private final UserService userService;

    public void sendMessage(String exchange, String routingKey, ChatMessageForTeamDto.Request messageDto) {
        // chatRoomId를 통해 ChatRoomForCarpool 조회
        ChatRoomForTeam chatRoom = chatRoomService.findByChatRoomId(messageDto.getChatRoomId());

        // ChatRoomForCarpool에서 CarpoolBoard와 User(author)를 가져오기
        TeamBoard teamBoard = chatRoom.getTeamBoard();
        User author = teamBoard.getUser();
        UserDetails sender = userService.loadUserByUsername(messageDto.getSenderId());

        // ChatRoomForCarpool 엔티티 생성 또는 조회
        com.kwhackathon.broom.chatMessage.entity.ChatMessageForTeam message = messageDto.toEntity(chatRoom,  sender);
        chatMessageForTeamRepository.save(message);
        System.out.println("RabbitMQ 전송: Exchange=" + exchange + ", RoutingKey=" + routingKey + ", Content=" + messageDto.getContent());

        // RabbitMQ로 메시지 전송
        rabbitTemplate.convertAndSend(exchange, routingKey, messageDto);

        // WebSocket으로 클라이언트에게 메시지 전송
//    messagingTemplate.convertAndSend("/topic/chat.carpool.room." + messageDto.getChatRoomId(), messageDto);

    }

//    @RabbitListener(queues = CHAT_QUEUE_NAME)//디버그 용도
//    public void receive(ChatMessageForCarpoolDto.Request messageDto) {
//        System.out.println("received: " + messageDto.getContent());
//    }

    // 읽음 상태 업데이트
    public void updateReadStatus(String chatRoomId, String userId) {
        ChatRoomForTeam chatRoom = chatRoomForTeamRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat Room not found"));

        boolean isUpdated = false;

        if (chatRoom.getAuthor().getUserId().equals(userId)) {
            chatRoom.setReadByAuthor(true);
            isUpdated = true;
        } else if (chatRoom.getParticipant().getUserId().equals(userId)) {
            chatRoom.setReadByParticipant(true);
            isUpdated = true;
        }

        if (isUpdated) {
            chatRoomForTeamRepository.save(chatRoom);

            // WebSocket 메시지 전송
            ReadStatusUpdateDto readStatusUpdate = new ReadStatusUpdateDto(chatRoomId, userId);
            sendReadStatusUpdateMessage(chatRoomId, readStatusUpdate);
        }
    }
    // WebSocket 메시지 전송 메서드
    private void sendReadStatusUpdateMessage(String chatRoomId, ReadStatusUpdateDto readStatusUpdate) {
        // RabbitMQ는 비동기 이므로
        // 실시간 반영을 위해 WebSocket template으로 전송
        messagingTemplate.convertAndSend("chat.team.room." + chatRoomId, readStatusUpdate);
    }

}
