package com.kwhackathon.broom.chatRoom.entity;

import com.kwhackathon.broom.carpool.entity.CarpoolBoard;
import com.kwhackathon.broom.chatMessage.entity.ChatMessageForCarpool;
import com.kwhackathon.broom.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "chat_room_for_carpool")
@EntityListeners(AuditingEntityListener.class)
@Data
public class ChatRoomForCarpool {

    //chat_room_id
    @EqualsAndHashCode.Include
    @Id
    @Column(name = "chat_room_id", updatable = false, nullable = false)
    private String id = UUID.randomUUID().toString(); // UUID로 고유 ID 생성

    //작성자 Id
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    //참여자 Id
    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private User participant;

    //카풀 게시판 id
    @ManyToOne
    @JoinColumn(name="carpool_board_id",nullable = false)
    private CarpoolBoard carpoolBoard;

    //채팅방 생성 날짜
    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    //최근 message id
    @OneToOne
    @JoinColumn(name = "last_chat_message_id")
    private ChatMessageForCarpool lastChatMessageForCarpool; // 마지막 채팅 메시지

    //author가 읽었는지(보류)
    @Column(name="is_read_by_author",nullable = false)
    private boolean isReadByAuthor;

    //participant가 읽었는지
    @Column(name="is_read_by_participant",nullable = false)
    private boolean isReadByParticipant;


}
