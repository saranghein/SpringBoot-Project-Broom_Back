package com.kwhackathon.broom.chat.component;

import com.kwhackathon.broom.chat.dto.ChatRequest;
import com.kwhackathon.broom.chat.dto.ChatResponse;
import com.kwhackathon.broom.chat.entity.Chat;
import com.kwhackathon.broom.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class ChatMessageProducer {//메시지를 RabbitMQ로 전송
    private final RabbitTemplate rabbitTemplate;
    @Value("${broom.exchange-name}")
    private String EXCHANGE_NAME;
    @Value("${broom.routing-prefix}")
    private String ROUTING_KEY_PREFIX;
    private final ChatService chatService;

    public void sendMessage(ChatRequest.Message messageDto, String senderId) {
        // 메시지 DB 저장
        Chat savedChat = chatService.saveMessage(messageDto, senderId);

        // 메시지 헤더 추가
        //MessageHeaders headers = new MessageHeaders(Collections.singletonMap("senderId", senderId));
        //Message<ChatResponse.Message> messageWithHeaders = MessageBuilder.createMessage(ChatResponse.Message.fromEntity(savedChat, messageDto.getBoardId()), headers);
        ChatResponse.Message responseMessage = ChatResponse.Message.fromEntity(savedChat, messageDto.getBoardId());

        // Server -> RabbitMQ로 메시지 전송
        String routingKey = ROUTING_KEY_PREFIX + messageDto.getBoardId();
        System.out.println("📩 RabbitMQ 전송: exchange=" + EXCHANGE_NAME + ", routingKey=" + routingKey);

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, routingKey, responseMessage);
    }

}