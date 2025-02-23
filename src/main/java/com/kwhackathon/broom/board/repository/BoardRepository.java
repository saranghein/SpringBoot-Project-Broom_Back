package com.kwhackathon.broom.board.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kwhackathon.broom.board.dto.BoardResponse.BoardWithBookmarkDto;
import com.kwhackathon.broom.board.entity.Board;

import java.time.LocalDate;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, String> {
        // 게시글 전체 조회
        @Query("""
                        SELECT new com.kwhackathon.broom.board.dto.BoardResponse$BoardWithBookmarkDto(
                                b, SUM(CASE WHEN p.isExpelled = false AND (b.user.userId != p.user.userId) THEN 1 ELSE 0 END), CASE WHEN COUNT(bm) > 0 THEN true ELSE false END
                        )
                        FROM Board b
                        LEFT JOIN Participant p ON p.board.boardId = b.boardId
                        LEFT JOIN Bookmark bm ON bm.board.boardId = b.boardId AND bm.user.userId = :userId
                        GROUP BY b
                        """)
        Slice<BoardWithBookmarkDto> findSliceBoardWithBookmark(Pageable pageable,
                        @Param("userId") String userId);

        // 인원 모집이 진행 중인 게시글만 전체 조회
        @Query("""
                        SELECT new com.kwhackathon.broom.board.dto.BoardResponse$BoardWithBookmarkDto(
                                b, SUM(CASE WHEN p.isExpelled = false AND (b.user.userId != p.user.userId) THEN 1 ELSE 0 END), CASE WHEN COUNT(bm) > 0 THEN true ELSE false END
                        )
                        FROM Board b
                        LEFT JOIN Participant p ON p.board.boardId = b.boardId
                        LEFT JOIN Bookmark bm ON bm.board.boardId = b.boardId AND bm.user.userId = :userId

                        GROUP BY b
                        HAVING SUM(CASE WHEN p.isExpelled = false AND (b.user.userId != p.user.userId) THEN 1 ELSE 0 END) < b.personnel
                        """)
        Slice<BoardWithBookmarkDto> findSliceBoardWithBookmarkByRecruiting(Pageable pageable,
                        @Param("userId") String userId);

        // 제목으로 게시글 검색
        @Query("""
                        SELECT new com.kwhackathon.broom.board.dto.BoardResponse$BoardWithBookmarkDto(
                                b, SUM(CASE WHEN p.isExpelled = false AND (b.user.userId != p.user.userId) THEN 1 ELSE 0 END), CASE WHEN COUNT(bm) > 0 THEN true ELSE false END
                        )
                        FROM Board b
                        LEFT JOIN Participant p ON p.board.boardId = b.boardId
                        LEFT JOIN Bookmark bm ON bm.board.boardId = b.boardId AND bm.user.userId = :userId
                        WHERE b.title LIKE %:title%
                        GROUP BY b
                        """)
        Slice<BoardWithBookmarkDto> findSliceByTitleContaining(Pageable pageable,
                        @Param("title") String title,
                        @Param("userId") String userId);

        // 제목으로 검색 시 모집 중인 게시글만 조회
        @Query("""
                        SELECT new com.kwhackathon.broom.board.dto.BoardResponse$BoardWithBookmarkDto(
                                b, SUM(CASE WHEN p.isExpelled = false AND (b.user.userId != p.user.userId) THEN 1 ELSE 0 END), CASE WHEN COUNT(bm) > 0 THEN true ELSE false END
                        )
                        FROM Board b
                        LEFT JOIN Participant p ON p.board.boardId = b.boardId
                        LEFT JOIN Bookmark bm ON bm.board.boardId = b.boardId AND bm.user.userId = :userId
                        WHERE b.title LIKE %:title%
                        GROUP BY b
                        HAVING SUM(CASE WHEN p.isExpelled = false AND (b.user.userId != p.user.userId) THEN 1 ELSE 0 END) < b.personnel
                        """)
        Slice<BoardWithBookmarkDto> findByRecruitingAndTitleContaining(Pageable pageable,
                        @Param("title") String title,
                        @Param("userId") String userId);

        // 훈련 날짜로 게시글 검색
        @Query("""
                        SELECT new com.kwhackathon.broom.board.dto.BoardResponse$BoardWithBookmarkDto(
                                b, SUM(CASE WHEN p.isExpelled = false AND (b.user.userId != p.user.userId) THEN 1 ELSE 0 END), CASE WHEN COUNT(bm) > 0 THEN true ELSE false END
                        )
                        FROM Board b
                        LEFT JOIN Participant p ON p.board.boardId = b.boardId
                        LEFT JOIN Bookmark bm ON bm.board.boardId = b.boardId AND bm.user.userId = :userId
                        WHERE b.trainingDate = :trainingDate
                        GROUP BY b
                        """)
        Slice<BoardWithBookmarkDto> findSliceByTrainingDate(Pageable pageable,
                        @Param("trainingDate") LocalDate trainingDate,
                        @Param("userId") String userId);

        // 훈련 날짜로 검색 시 모집 중인 게시글만 조회
        @Query("""
                        SELECT new com.kwhackathon.broom.board.dto.BoardResponse$BoardWithBookmarkDto(
                                b, SUM(CASE WHEN p.isExpelled = false AND (b.user.userId != p.user.userId) THEN 1 ELSE 0 END), CASE WHEN COUNT(bm) > 0 THEN true ELSE false END
                        )
                        FROM Board b
                        LEFT JOIN Participant p ON p.board.boardId = b.boardId
                        LEFT JOIN Bookmark bm ON bm.board.boardId = b.boardId AND bm.user.userId = :userId
                        WHERE b.trainingDate = :trainingDate
                        GROUP BY b
                        HAVING SUM(CASE WHEN p.isExpelled = false AND (b.user.userId != p.user.userId) THEN 1 ELSE 0 END) < b.personnel
                        """)
        Slice<BoardWithBookmarkDto> findSliceByRecruitingAndTrainingDate(Pageable pageable,
                        @Param("trainingDate") LocalDate trainingDate,
                        @Param("userId") String userId);

        // 사용자 지정 장소로 게시글 검색
        @Query("""
                        SELECT new com.kwhackathon.broom.board.dto.BoardResponse$BoardWithBookmarkDto(
                                b, SUM(CASE WHEN p.isExpelled = false AND (b.user.userId != p.user.userId) THEN 1 ELSE 0 END), CASE WHEN COUNT(bm) > 0 THEN true ELSE false END
                        )
                        FROM Board b
                        LEFT JOIN Participant p ON p.board.boardId = b.boardId
                        LEFT JOIN Bookmark bm ON bm.board.boardId = b.boardId AND bm.user.userId = :userId
                        WHERE b.place LIKE %:place%
                        GROUP BY b
                        """)
        Slice<BoardWithBookmarkDto> findSliceByPlaceContaining(Pageable pageable,
                        @Param("place") String place,
                        @Param("userId") String userId);

        // 사용자 지정 장소로 검색 시 모집 중인 게시글만 조회
        @Query("""
                        SELECT new com.kwhackathon.broom.board.dto.BoardResponse$BoardWithBookmarkDto(
                                b, SUM(CASE WHEN p.isExpelled = false AND (b.user.userId != p.user.userId) THEN 1 ELSE 0 END), CASE WHEN COUNT(bm) > 0 THEN true ELSE false END
                        )
                        FROM Board b
                        LEFT JOIN Participant p ON p.board.boardId = b.boardId
                        LEFT JOIN Bookmark bm ON bm.board.boardId = b.boardId AND bm.user.userId = :userId
                        WHERE b.place LIKE %:place%
                        GROUP BY b
                        HAVING SUM(CASE WHEN p.isExpelled = false AND (b.user.userId != p.user.userId) THEN 1 ELSE 0 END) < b.personnel
                        """)
        Slice<BoardWithBookmarkDto> findSliceByRecruitingAndPlaceContaining(Pageable pageable,
                        @Param("place") String place,
                        @Param("userId") String userId);

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
                        SELECT CASE WHEN SUM(CASE WHEN p.isExpelled = false AND (b.user.userId != p.user.userId) THEN 1 ELSE 0 END) > b.personnel THEN TRUE ELSE FALSE END
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
}