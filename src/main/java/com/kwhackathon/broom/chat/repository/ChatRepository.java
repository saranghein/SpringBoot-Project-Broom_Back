package com.kwhackathon.broom.chat.repository;

import com.kwhackathon.broom.chat.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findByParticipant_Board_BoardIdOrderByCreatedAtAsc(String boardId);

    Page<Chat> findByParticipant_Board_BoardIdOrderByCreatedAtAsc(String boardId, Pageable pageable);

    @Query("SELECT c FROM Chat c JOIN FETCH c.participant p JOIN FETCH p.user WHERE c.participant.board.boardId = :boardId")
    List<Chat> findChatsWithParticipantsByBoardId(@Param("boardId") String boardId);

    // 각 채팅방(Board)의 최신 메시지 1개 조회 (Participant를 경유)
    @Query("SELECT c FROM Chat c " +
            "WHERE c.createdAt = (" +
            "   SELECT MAX(c2.createdAt) FROM Chat c2 " +
            "   WHERE c2.participant.board.boardId = c.participant.board.boardId" +
            ") AND c.participant.board.boardId IN :boardIds")
    List<Chat> findLatestChatsForBoards(@Param("boardIds") List<String> boardIds);

    // 채팅방(boardId)별 가장 최근 메시지 조회
    @Query("SELECT c FROM Chat c WHERE c.participant.board.boardId = :boardId ORDER BY c.createdAt DESC LIMIT 1")
    Optional<Chat> findLatestMessageByBoardId(@Param("boardId") String boardId);

    @Modifying
    @Query("UPDATE Chat c SET c.participant = NULL WHERE c.participant.id IN (SELECT p.id FROM Participant p WHERE p.user.userId = :userId AND p.board.boardId = :boardId)")
    void setParticipantToNull(@Param("userId") String userId, @Param("boardId") String boardId);
}
