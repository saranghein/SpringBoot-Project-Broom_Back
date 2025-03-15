package com.kwhackathon.broom.chat.service;

import com.kwhackathon.broom.chat.config.RabbitmqConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final AmqpAdmin amqpAdmin;
    private final RabbitmqConfig rabbitmqConfig;

    @Override
    public void createChatRoom(String boardId){
        rabbitmqConfig.createChatRoomQueue(boardId,amqpAdmin);
    }

    @Override
    public void deleteChatRoom(String boardId) {
        rabbitmqConfig.deleteChatRoomQueue(boardId,amqpAdmin);
    }
}
