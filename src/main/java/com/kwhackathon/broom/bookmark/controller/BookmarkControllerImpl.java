package com.kwhackathon.broom.bookmark.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kwhackathon.broom.bookmark.dto.BookmarkRequest.CreateDto;
import com.kwhackathon.broom.bookmark.service.BookmarkService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BookmarkControllerImpl implements BookmarkController {
    private final BookmarkService bookmarkService;

    @Override
    @PostMapping("/mypage/bookmark")
    public ResponseEntity<?> createBookmark(@RequestBody CreateDto createDto) {
        try {
            bookmarkService.createBookmark(createDto);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("게시글이 북마크에 추가되었습니다.");
    }

    @Override
    @GetMapping("/mypage/bookmark/{page}")
    public ResponseEntity<?> getBookmark(@PathVariable("page") int page) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(bookmarkService.getBookmark(page));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Override
    @DeleteMapping("/mypage/bookmark/{boardId}")
    public ResponseEntity<?> deleteBookmark(@PathVariable("boardId") String boardId) {
        try {
            bookmarkService.deleteBookmark(boardId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("오류가 발생하였습니다.");
        }
        return ResponseEntity.status(HttpStatus.OK).body("게시글이 북마크에서 삭제되었습니다.");
    }
}
