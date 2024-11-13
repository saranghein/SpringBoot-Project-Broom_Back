package com.kwhackathon.broom.chatMessage.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "chat_message_for_early_departure")

public class ChatMessageForEarlyDeparture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id",unique = true, nullable = false)
    private Long id;

}
