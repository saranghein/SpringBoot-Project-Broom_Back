package com.kwhackathon.broom.chatMessage.repository;

import com.kwhackathon.broom.chatMessage.entity.ChatMessageForEarlyDeparture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ChatMessageForEarlyDepartureRepository extends JpaRepository<ChatMessageForEarlyDeparture, Long> {
    void deleteByChatRoomId(String chatRoomId);

}
