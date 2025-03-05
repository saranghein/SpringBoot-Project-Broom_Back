package com.kwhackathon.broom.chat.entity;

import com.kwhackathon.broom.participant.entity.Participant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "chat")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "message")
    private String message;

    // 보낸 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="participant_id",nullable = true)
    @ToString.Exclude // 순환 참조 방지
    private Participant participant;
}
