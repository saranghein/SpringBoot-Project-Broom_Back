package com.kwhackathon.broom.board.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kwhackathon.broom.board.dto.BoardResponse.BoardWithBookmarkDto;
import com.kwhackathon.broom.board.entity.Board;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, String>{
        // 내가 작성한 게시글 조회하기
        @Query("""
                        SELECT new com.kwhackathon.broom.board.dto.BoardResponse$BoardWithBookmarkDto(
                                b, SUM(CASE WHEN p.isExpelled = false AND (b.user.userId != p.user.userId) THEN 1 ELSE 0 END), CASE WHEN COUNT(bm) > 0 THEN true ELSE false END
                        )
                        FROM Board b
                        LEFT JOIN Participant p ON p.board.boardId = b.boardId
                        LEFT JOIN Bookmark bm ON bm.board.boardId = b.boardId AND bm.user.userId = :userId
                        WHERE b.user.userId = :userId
                        GROUP BY b
                        """)
        Slice<BoardWithBookmarkDto> findSliceByUserUserId(Pageable pageable, @Param("userId") String userId);

        // 내가 북마크로 등록한 게시글만 가져오기
        @Query("""
                        SELECT new com.kwhackathon.broom.board.dto.BoardResponse$BoardWithBookmarkDto(
                                b, SUM(CASE WHEN p.isExpelled = false AND (b.user.userId != p.user.userId) THEN 1 ELSE 0 END), CASE WHEN COUNT(bm) > 0 THEN true ELSE false END
                        )
                        FROM Board b
                        LEFT JOIN Participant p ON p.board.boardId = b.boardId
                        JOIN b.bookmarks bm WHERE bm.user.userId = :userId
                        GROUP BY b
                        """)
        Slice<BoardWithBookmarkDto> findSliceByBookmarksUserUserId(Pageable pageable, @Param("userId") String userId);

        // 게시글에 채팅 참여 가능한 자리가 있는지 여부 반환
        @Query("""
                        SELECT CASE WHEN SUM(CASE WHEN p.isExpelled = false AND (b.user.userId != p.user.userId) THEN 1 ELSE 0 END) < b.personnel + 1 THEN TRUE ELSE FALSE END
                        FROM Board b
                        LEFT JOIN Participant p ON p.board.boardId = b.boardId
                        WHERE p.board.boardId = b.boardId AND b.id = :boardId
                        """)
        boolean existsEmptySeatByBoardId(@Param("boardId") String boardId);

        // 단일 게시물 조회
        @Query("""
                        SELECT new com.kwhackathon.broom.board.dto.BoardResponse$BoardWithBookmarkDto(
                                b, SUM(CASE WHEN p.isExpelled = false AND (b.user.userId != p.user.userId) THEN 1 ELSE 0 END), CASE WHEN COUNT(bm) > 0 THEN true ELSE false END
                        )
                        FROM Board b
                        LEFT JOIN Participant p ON p.board.boardId = b.boardId
                        LEFT JOIN Bookmark bm ON bm.board.boardId = b.boardId AND bm.user.userId = :userId
                        WHERE b.boardId = :boardId
                        GROUP BY b
                        """)
        Optional<BoardWithBookmarkDto> findBoardWithBookmarkById(@Param("userId") String userId,
                        @Param("boardId") String boardId);

        // 총 게시글의 수를 조회
        @Query("SELECT COUNT(b) FROM Board b")
        Long countTotalBoard();
}