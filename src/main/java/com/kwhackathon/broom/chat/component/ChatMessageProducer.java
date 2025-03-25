package com.kwhackathon.broom.chat.component;

import com.kwhackathon.broom.chat.dto.ChatRequest;
import com.kwhackathon.broom.chat.dto.ChatResponse;
import com.kwhackathon.broom.chat.entity.Chat;
import com.kwhackathon.broom.chat.service.ChatAckService;
import com.kwhackathon.broom.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ChatMessageProducer {//메시지를 RabbitMQ로 전송
    private final RabbitTemplate rabbitTemplate;
    private final ChatService chatService;
    private final ChatAckService chatAckService;
    @Value("${broom.exchange-name}")
    private String EXCHANGE_NAME;
    @Value("${broom.routing-prefix}")
    private String ROUTING_KEY_PREFIX;

    @Transactional
    public void sendMessage(ChatRequest.Message messageDto, String senderId) {
        try {
            // 강제로 예외 발생 (테스트용)
            if (messageDto.getMessage().equals("errorTest")) {
                throw new RuntimeException("강제 오류 발생");
            }
            // 메시지 DB 저장
            Chat savedChat = chatService.saveMessage(messageDto, senderId);
            String roomId = messageDto.getBoardId();

            // 메시지 헤더 추가(Listener에서 senderId를 받아오기 위함)
//        MessageHeaders headers = new MessageHeaders(Collections.singletonMap("senderId", senderId));
//        Message<ChatResponse.Message> messageWithHeaders = MessageBuilder.createMessage(ChatResponse.Message.fromEntity(savedChat, messageDto.getBoardId()), headers);
            ChatResponse.Message responseMessage = ChatResponse.Message.fromEntity(savedChat, roomId);
            String routingKey = ROUTING_KEY_PREFIX + roomId;

            // Server -> RabbitMQ로 메시지 전송
            System.out.println("📩 RabbitMQ 전송: exchange=" + EXCHANGE_NAME + ", routingKey=" + routingKey);

            // 수신 완료 ack 전송
//            chatAckService.sendAckMessage();
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, routingKey, responseMessage);

        } catch (Exception e) {
            // 수신 실패 ack 전송
//            chatAckService.sendErrorMessage("오류발생", "", "");
            System.err.println("🚨 메시지 전송 중 오류 발생: " + e.getMessage());

        }


    }

}