package com.kwhackathon.broom.chatRoom.repository;

import com.kwhackathon.broom.chatRoom.entity.ChatRoomForTeam;
import com.kwhackathon.broom.team.entity.TeamBoard;
import com.kwhackathon.broom.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomForTeamRepository extends JpaRepository<ChatRoomForTeam, String> {
    // 특정 작성자와 참여자가 특정 게시물에 대해 만든 채팅방 조회
    Optional<ChatRoomForTeam> findByTeamBoardAndAuthorAndParticipant(Optional<TeamBoard> teamBoard, User author, User participant);


    // 특정 사용자와 관련된 채팅방 조회 (작성자 또는 참여자)
    List<ChatRoomForTeam> findByAuthorOrParticipant(User author, User participant);
}
