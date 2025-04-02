package com.kwhackathon.broom.chat.component;
import com.kwhackathon.broom.chat.config.BroomProperties;
import org.springframework.amqp.core.*;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
public class QueueManager {
    private final AmqpAdmin amqpAdmin;
    private final BroomProperties broomProperties;
    private final TopicExchange ackExchange;
    private final TopicExchange chatExchange;

    public QueueManager(AmqpAdmin amqpAdmin, BroomProperties broomProperties, TopicExchange ackExchange, TopicExchange chatExchange) {
        this.amqpAdmin = amqpAdmin;
        this.broomProperties = broomProperties;
        this.ackExchange = ackExchange;
        this.chatExchange = chatExchange;
    }

    // 유저 ACK 큐 생성
    public void createQueueForUserInRoom(String boardId, String userNickname) {
        String queueName = broomProperties.getAck().getQueueName() + "." + userNickname + "." + boardId;
        String routingKey = broomProperties.getAck().getRoutingPrefix() + userNickname + "." + boardId;

        Queue queue = new Queue(queueName, true);
        amqpAdmin.declareQueue(queue);

        Binding binding = BindingBuilder.bind(queue)
                .to(ackExchange)
                .with(routingKey);
        amqpAdmin.declareBinding(binding);

        System.out.println("[ACK 큐 생성] " + queueName);
    }


    // 유저 ACK 큐 삭제
    public void deleteQueueForUserInRoom(String boardId, String userNickname) {
        String queueName = broomProperties.getAck().getQueueName() + "." + userNickname + "." + boardId;
        String routingKey = broomProperties.getAck().getRoutingPrefix() + userNickname + "." + boardId;


        Binding binding = BindingBuilder.bind(new Queue(queueName))
                .to(ackExchange)
                .with(routingKey);
        amqpAdmin.removeBinding(binding);
        amqpAdmin.deleteQueue(queueName);
        System.out.println("[ACK 큐 삭제] " + queueName);
    }

    // 채팅방 큐 생성
    public void createChatRoomQueue(String boardId) {
        String queueName = broomProperties.getQueueName() + "." + boardId;
        String routingKey = broomProperties.getRoutingPrefix() + boardId;

        Queue queue = new Queue(queueName, true);
        amqpAdmin.declareQueue(queue);
        Binding binding = BindingBuilder.bind(queue)
                .to(chatExchange)
                .with(routingKey);
        amqpAdmin.declareBinding(binding);
    }

    // 채팅방 큐 삭제
    public void deleteChatRoomQueue(String boardId) {
        String queueName = broomProperties.getQueueName() + "." + boardId;
        String routingKey = broomProperties.getRoutingPrefix() + boardId;

        Binding binding = BindingBuilder.bind(new Queue(queueName))
                .to(chatExchange)
                .with(routingKey);
        amqpAdmin.removeBinding(binding);
        amqpAdmin.deleteQueue(queueName);
        System.out.println("채팅 큐 삭제 완료: " + queueName);
    }
}
