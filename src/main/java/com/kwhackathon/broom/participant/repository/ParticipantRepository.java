package com.kwhackathon.broom.participant.repository;

import com.kwhackathon.broom.participant.entity.Participant;
import com.kwhackathon.broom.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    List<Participant> findByBoard_BoardId(String boardId);

    boolean existsByUserUserIdAndBoardBoardId(String userId, String boardId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Participant p WHERE p.user.userId = :userId AND p.board.boardId = :boardId")
    int deleteByUserIdAndBoardId(@Param("userId") String userId, @Param("boardId") String boardId);

    @Query("SELECT p FROM Participant p JOIN FETCH p.user WHERE p.user.userId = :userId AND p.board.boardId = :boardId")
    Optional<Participant> findByUser_UserIdAndBoard_BoardId(@Param("userId") String userId, @Param("boardId") String boardId);

    // 유저가 참여한 채팅방 리스트 조회 (페이징)
    Page<Participant> findByUser(User user, Pageable pageable);

    // 강퇴되지 않은 사용자 조회
    @Query("SELECT p " +
            "FROM Participant p " +
            "WHERE p.board.boardId = :boardId AND p.isExpelled = false")
    List<Participant> findActiveParticipantsByBoardId(@Param("boardId") String boardId);

    // expelled == false 필터링
    List<Participant> findByBoard_BoardIdAndIsExpelledFalse(String boardId);


    @Query("""
        SELECT p FROM Participant p 
        LEFT JOIN p.board b 
        LEFT JOIN Chat c ON c.board = b 
        WHERE p.user = :user
        GROUP BY p
        ORDER BY MAX(c.createdAt) DESC
    """)
    Page<Participant> findParticipantsByUserOrderByLatestChatTime(@Param("user") User user, Pageable pageable);

    // 사용자가 참여중인 채팅방 목록 가져오기
//    @Query("""
//                SELECT new com.kwhackathon.broom.chatting.dto.ChatMessageResponse$ChatRoomElement(
//                    p.board.boardId, p.board.title, COALESCE(cm.message, ''), cm.createdAt, p.unread
//                )
//                FROM Participant p
//                LEFT JOIN p.chatMessages cm
//                WHERE p.user.id = :userId
//                AND (cm.createdAt IS NULL OR cm.createdAt = (
//                    SELECT MAX(cm2.createdAt) FROM ChatMessage cm2 WHERE cm2.participant = p
//                ))
//                ORDER BY cm.createdAt DESC
//            """)
//    Slice<ChatRoomElement> findChatRoomsByUserId(Pageable pageable, @Param("userId") String userId);

    // 작성자 조회

    // 작성자 외 참가자 최적화
}

