package com.kwhackathon.broom.chatMessage.repository;

import com.kwhackathon.broom.chatMessage.entity.ChatMessageForTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ChatMessageForTeamRepository extends JpaRepository<ChatMessageForTeam, Long> {
    void deleteByChatRoomId(String chatRoomId);

    List<ChatMessageForTeam> findByChatRoomIdOrderByCreatedAtAsc(String chatRoomId);
}
