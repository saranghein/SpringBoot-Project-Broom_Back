package com.kwhackathon.broom.chat.service;

import com.kwhackathon.broom.chat.config.BroomProperties;
import com.kwhackathon.broom.chat.dto.ChatAckRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatAckServiceImpl implements ChatAckService {
    private final SimpMessagingTemplate messagingTemplate;
    private final BroomProperties broomProperties;

    @Override
    public void sendAckMessage(ChatAckRequest.Request ack) {
        String destination = "/queue/" + broomProperties.getAck().getRoutingPrefix()
                + ack.getSenderNickname() + "." + ack.getBoardId();

        System.out.println("📬 ACK WebSocket 전송: " + destination);

        messagingTemplate.convertAndSend(destination, ack); // ✅ convertAndSend 사용
    }

}

