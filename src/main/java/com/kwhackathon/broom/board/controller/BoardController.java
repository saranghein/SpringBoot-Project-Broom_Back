package com.kwhackathon.broom.board.controller;

import org.springframework.http.ResponseEntity;

import com.kwhackathon.broom.board.dto.BoardRequest.WriteBoardDto;

public interface BoardController {
    // 게시불 생성
    ResponseEntity<?> createBoard(WriteBoardDto writeBoardDto);

    // 모든 게시물 보기
    ResponseEntity<?> getAllBoards(int page, boolean recruiting);

    // 게시물 검색
    ResponseEntity<?> searchBoard(int page, String type, String keyword, boolean recruiting);

    // 단일 게시물 내용 조회
    ResponseEntity<?> getSingleBoardDetail(String boardId);

    // 게시물 수정
    ResponseEntity<?> editBoard(String boardId, WriteBoardDto writeBoardDto);

    // 게시물 삭제
    ResponseEntity<?> deleteBoard(String boardId);

    // 내가 작성한 게시물 보기
    ResponseEntity<?> getMyBoard(int page);

    ResponseEntity<?> getTotalBoardCount();
}
