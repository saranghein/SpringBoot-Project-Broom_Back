package com.kwhackathon.broom.chatMessage.repository;

import com.kwhackathon.broom.chatMessage.entity.ChatMessageForTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ChatMessageForTeamRepository extends JpaRepository<ChatMessageForTeam, Long> {
    void deleteByChatRoomId(String chatRoomId);

}
