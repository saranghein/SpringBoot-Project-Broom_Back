package com.kwhackathon.broom.chatRoom.repository;

import com.kwhackathon.broom.carpool.entity.CarpoolBoard;
import com.kwhackathon.broom.chatRoom.entity.ChatRoomForCarpool;
import com.kwhackathon.broom.chatRoom.entity.ChatRoomForEarlyDeparture;
import com.kwhackathon.broom.earlyDepartureBoard.entity.EarlyDepartureBoard;
import com.kwhackathon.broom.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomForEarlyDepartureRepository extends JpaRepository<ChatRoomForEarlyDeparture, String> {
    // 특정 작성자와 참여자가 특정 게시물에 대해 만든 채팅방 조회
    Optional<ChatRoomForEarlyDeparture> findByEarlyDepartureBoardAndAuthorAndParticipant(Optional<EarlyDepartureBoard> earlyDepartureBoard, User author, User participant);


    // 특정 사용자와 관련된 채팅방 조회 (작성자 또는 참여자)
    List<ChatRoomForEarlyDeparture> findByAuthorOrParticipant(User author, User participant);
}
