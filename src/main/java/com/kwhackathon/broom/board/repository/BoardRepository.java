package com.kwhackathon.broom.board.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kwhackathon.broom.board.entity.Board;
import com.kwhackathon.broom.board.util.category.Category;

import java.time.LocalDate;

public interface BoardRepository extends JpaRepository<Board, String> {
    // 카풀 또는 팀원 모집 게시글 전체 조회
    Slice<Board> findSliceByCategory(Pageable pageable, Category category);

    // 제목으로 카풀 또는 팀원 모집 게시글 검색
    Slice<Board> findSliceByCategoryAndTitle(Pageable pageable, Category category, String title);

    // 훈련 날짜로 카풀 또는 팀원 모집 게시글 검색
    Slice<Board> findSliceByCategoryAndTrainingDate(Pageable pageable, Category category, LocalDate trainingDate);

    // 사용자 지정 장소로 카풀 또는 팀원 모집 게시글 검색
    Slice<Board> findSliceByCategoryAndPlace(Pageable pageable, Category category, String place);

    // 인원 모집이 진행 중인 게시글만 조회
    Slice<Board> findSliceByCategoryAndIsFull(Pageable pageable, Category category, Boolean isFull);
            
    Slice<Board> findSliceByCategoryAndUserUserId(Pageable pageable, Category category, String userId);

    @Query("SELECT b FROM Board b JOIN b.bookmarks bm WHERE bm.user.userId = :userId")
    Slice<Board> findSliceByBookmarksUserUserId(Pageable pageable, @Param("userId")String userId);
}