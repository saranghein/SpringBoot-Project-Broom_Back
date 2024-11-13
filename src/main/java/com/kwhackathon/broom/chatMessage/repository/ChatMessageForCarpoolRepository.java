package com.kwhackathon.broom.chatMessage.repository;

import com.kwhackathon.broom.chatMessage.entity.ChatMessageForCarpool;
import com.kwhackathon.broom.chatRoom.entity.ChatRoomForCarpool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageForCarpoolRepository extends JpaRepository<ChatMessageForCarpool, Long> {
}
