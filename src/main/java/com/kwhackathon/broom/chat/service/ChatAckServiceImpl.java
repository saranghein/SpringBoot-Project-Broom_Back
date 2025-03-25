package com.kwhackathon.broom.chat.service;

import com.kwhackathon.broom.chat.dto.ChatAckRequest;
import com.kwhackathon.broom.chat.dto.ChatAckResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChatAckServiceImpl implements ChatAckService {

    private final SimpMessagingTemplate messagingTemplate;
    @Value("${broom.user-queue-prefix}")
    static String USER_QUEUE_PREFIX;


    @Override
    public void sendAckMessage(ChatAckRequest.Request chatAckRequest) {
        messagingTemplate.convertAndSendToUser(
                chatAckRequest.getSenderId(),
                USER_QUEUE_PREFIX + chatAckRequest.getBoardId(),
                ChatAckResponse.Response.toResponse(chatAckRequest));
    }
}

