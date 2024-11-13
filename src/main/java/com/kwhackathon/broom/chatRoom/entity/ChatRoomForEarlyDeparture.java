package com.kwhackathon.broom.chatRoom.entity;

import com.kwhackathon.broom.chatMessage.entity.ChatMessageForEarlyDeparture;
import com.kwhackathon.broom.earlyDepartureBoard.entity.EarlyDepartureBoard;
import com.kwhackathon.broom.user.entity.User;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_room_for_early_departure")
@EntityListeners(AuditingEntityListener.class)
public class ChatRoomForEarlyDeparture {
    @EqualsAndHashCode.Include
    @Id
    @Column(name = "chat_room_id", updatable = false, nullable = false)
    private String id = UUID.randomUUID().toString(); // UUID로 고유 ID 생성

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="early_departure_board_id",nullable = false)
    private EarlyDepartureBoard earlyDepartureBoard;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "last_chat_message_id")
    private ChatMessageForEarlyDeparture chatMessageForEarlyDeparture; // 마지막 채팅 메시지

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "chatroom_user",
//            joinColumns = @JoinColumn(name = "chatroom_id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id")
//    )
//    private Set<User> chatRoomMembers = new HashSet<>(); // 채팅방 멤버 목록

    @Column(name="is_read",nullable = false)
    private boolean isRead;
}
