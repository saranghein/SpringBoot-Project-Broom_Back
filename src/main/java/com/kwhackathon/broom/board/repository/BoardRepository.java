package com.kwhackathon.broom.board.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kwhackathon.broom.board.dto.BoardResponse.BoardWithBookmarkDto;
import com.kwhackathon.broom.board.entity.Board;

import java.time.LocalDate;

public interface BoardRepository extends JpaRepository<Board, String> {
        // 카풀 또는 팀원 모집 게시글 전체 조회
        // board를 조회할 때, participant의 수와 북마크 여부를 동시에 가져옴
        @Query("""
                        SELECT new com.kwhackathon.broom.board.dto.BoardResponse$BoardWithBookmarkDto(
                                b, SIZE(b.participants), CASE WHEN COUNT(bm) > 0 THEN true ELSE false END
                        )
                        FROM Board b
                        LEFT JOIN Bookmark bm ON bm.board.boardId = b.boardId AND bm.user.userId = :userId
                        GROUP BY b
                        """)
        Slice<BoardWithBookmarkDto> findSliceBoardWithBookmark(Pageable pageable,
                        @Param("userId") String userId);

        // 카테고리별로 인원 모집이 진행 중인 게시글만 전체 조회
        @Query("""
                        SELECT new com.kwhackathon.broom.board.dto.BoardResponse$BoardWithBookmarkDto(
                                b, SIZE(b.participants), CASE WHEN COUNT(bm) > 0 THEN true ELSE false END
                        )
                        FROM Board b
                        LEFT JOIN Bookmark bm ON bm.board.boardId = b.boardId AND bm.user.userId = :userId
                        WHERE SIZE(b.participants) < b.personnel
                        GROUP BY b
                        """)
        Slice<BoardWithBookmarkDto> findSliceBoardWithBookmarkByRecruiting(Pageable pageable,
                        @Param("userId") String userId);

        // 제목으로 카풀 또는 팀원 모집 게시글 검색
        @Query("""
                        SELECT new com.kwhackathon.broom.board.dto.BoardResponse$BoardWithBookmarkDto(
                                b, SIZE(b.participants), CASE WHEN COUNT(bm) > 0 THEN true ELSE false END
                        )
                        FROM Board b
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
                                b, SIZE(b.participants), CASE WHEN COUNT(bm) > 0 THEN true ELSE false END
                        )
                        FROM Board b
                        LEFT JOIN Bookmark bm ON bm.board.boardId = b.boardId AND bm.user.userId = :userId
                        WHERE SIZE(b.participants) < b.personnel AND b.title LIKE %:title%
                        GROUP BY b
                        """)
        Slice<BoardWithBookmarkDto> findByRecruitingAndTitleContaining(Pageable pageable,
                        @Param("title") String title,
                        @Param("userId") String userId);

        // 훈련 날짜로 카풀 또는 팀원 모집 게시글 검색
        @Query("""
                        SELECT new com.kwhackathon.broom.board.dto.BoardResponse$BoardWithBookmarkDto(
                                b, SIZE(b.participants), CASE WHEN COUNT(bm) > 0 THEN true ELSE false END
                        )
                        FROM Board b
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
                                b, SIZE(b.participants), CASE WHEN COUNT(bm) > 0 THEN true ELSE false END
                        )
                        FROM Board b
                        LEFT JOIN Bookmark bm ON bm.board.boardId = b.boardId AND bm.user.userId = :userId
                        WHERE SIZE(b.participants) < b.personnel AND b.trainingDate = :trainingDate
                        GROUP BY b
                        """)
        Slice<BoardWithBookmarkDto> findSliceByRecruitingAndTrainingDate(Pageable pageable,
                        @Param("trainingDate") LocalDate trainingDate,
                        @Param("userId") String userId);

        // 사용자 지정 장소로 카풀 또는 팀원 모집 게시글 검색
        @Query("""
                        SELECT new com.kwhackathon.broom.board.dto.BoardResponse$BoardWithBookmarkDto(
                                b, SIZE(b.participants), CASE WHEN COUNT(bm) > 0 THEN true ELSE false END
                        )
                        FROM Board b
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
                                b, SIZE(b.participants), CASE WHEN COUNT(bm) > 0 THEN true ELSE false END
                        )
                        FROM Board b
                        LEFT JOIN Bookmark bm ON bm.board.boardId = b.boardId AND bm.user.userId = :userId
                        WHERE SIZE(b.participants) < b.personnel AND b.place LIKE %:place%
                        GROUP BY b
                        """)
        Slice<BoardWithBookmarkDto> findSliceByRecruitingAndPlaceContaining(Pageable pageable,
                        @Param("place") String place,
                        @Param("userId") String userId);

        // 내가 작성한 게시글 조회하기
        Slice<Board> findSliceByUserUserId(Pageable pageable, String userId);

        // 내가 북마크로 등록한 게시글만 가져오기
        @Query("SELECT b FROM Board b JOIN b.bookmarks bm WHERE bm.user.userId = :userId")
        Slice<Board> findSliceByBookmarksUserUserId(Pageable pageable, @Param("userId") String userId);

        // 내가 채팅 참여중인 게시글 가져오기
        @Query("SELECT b FROM Board b JOIN b.participants p WHERE p.user.id = :userId")
        Slice<Board> findSliceByParticipantsUserId(Pageable pageable, @Param("userId") String userId);
}