package com.kwhackathon.broom.chatRoom.repository;

import com.kwhackathon.broom.chatRoom.entity.ChatRoomForCarpool;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomForEarlyDepartureRepository extends JpaRepository<ChatRoomForCarpool, String> {
}
