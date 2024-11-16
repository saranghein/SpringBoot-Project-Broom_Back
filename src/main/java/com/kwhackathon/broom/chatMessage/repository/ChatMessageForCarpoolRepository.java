package com.kwhackathon.broom.chatMessage.repository;

import com.kwhackathon.broom.chatMessage.entity.ChatMessageForCarpool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageForCarpoolRepository extends JpaRepository<ChatMessageForCarpool, Long> {
    void deleteByChatRoomId(String chatRoomId);

    List<ChatMessageForCarpool> findByChatRoomIdOrderByCreatedAtAsc(String chatRoomId);
}
