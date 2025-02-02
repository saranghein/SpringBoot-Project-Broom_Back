package com.kwhackathon.broom.bookmark.controller;

import org.springframework.http.ResponseEntity;

import com.kwhackathon.broom.board.util.category.Category;
import com.kwhackathon.broom.bookmark.dto.BookmarkRequest.CreateDto;

public interface BookmarkController {
    // 게시글을 북마크로 추가
    ResponseEntity<?> createBookmark(CreateDto createDto);

    // 카테고리 별로 북마크로 추가한 게시글들 조회
    ResponseEntity<?> getBookmark(int page, Category category);

    // 북마크를 해제
    ResponseEntity<?> deleteBookmark(String boardId);
}
