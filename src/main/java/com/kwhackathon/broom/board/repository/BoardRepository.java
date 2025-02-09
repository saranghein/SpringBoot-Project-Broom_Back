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

    // 카테고리별로 인원 모집이 진행 중인 게시글만 전체 조회
    Slice<Board> findSliceByCategoryAndIsFull(Pageable pageable, Category category, Boolean isFull);

    // 제목으로 카풀 또는 팀원 모집 게시글 검색
    Slice<Board> findSliceByCategoryAndTitle(Pageable pageable, Category category, String title);

    // 제목으로 검색 시 모집 중인 게시글만 조회
    Slice<Board> findSliceByCategoryAndTitleAndIsFull(Pageable pageable, Category category, String title, boolean isFull);

    // 훈련 날짜로 카풀 또는 팀원 모집 게시글 검색
    Slice<Board> findSliceByCategoryAndTrainingDate(Pageable pageable, Category category, LocalDate trainingDate);

    // 훈련 날짜로 검색 시 모집 중인 게시글만 조회
    Slice<Board> findSliceByCategoryAndTrainingDateAndIsFull(Pageable pageable, Category category,
            LocalDate trainingDate, boolean isFull);
    
    // 사용자 지정 장소로 카풀 또는 팀원 모집 게시글 검색
    Slice<Board> findSliceByCategoryAndPlace(Pageable pageable, Category category, String place);

    // 사용자 지정 장소로 검색 시 모집 중인 게시글만 조회
    Slice<Board> findSliceByCategoryAndPlaceAndIsFull(Pageable pageable, Category category, String place, boolean isFull);

    // 인원 모집이 진행 중인 게시글만 조회
    // Slice<Board> findSliceByCategoryAndIsFull(Pageable pageable, Category category, Boolean isFull);
            
    Slice<Board> findSliceByCategoryAndUserUserId(Pageable pageable, Category category, String userId);

    @Query("SELECT b FROM Board b JOIN b.bookmarks bm WHERE bm.user.userId = :userId")
    Slice<Board> findSliceByBookmarksUserUserId(Pageable pageable, @Param("userId")String userId);
}