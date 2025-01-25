package com.kwhackathon.broom.board.controller;

import org.springframework.http.ResponseEntity;

import com.kwhackathon.broom.board.dto.BoardRequest.WriteBoardDto;

public interface BoardController {
    // 게시불 생성
    ResponseEntity<?> createBoard(WriteBoardDto writeBoardDto);

    // 모든 게시물 보기
    ResponseEntity<?> getAllBoards(int page, String category);

    // 게시물 검색
    ResponseEntity<?> searchBoard(int page, String category, String type, String keyword);

    // 인원 모집이 진행 중인 게시물만 조회
    ResponseEntity<?> getRecruitingBoard(int page, String category);

    // 단일 게시물 내용 조회
    ResponseEntity<?> getSingleBoardDetail(String boardId);

    // 게시물 수정
    ResponseEntity<?> editBoard(String boardId, WriteBoardDto writeBoardDto);

    // 게시물 삭제
    ResponseEntity<?> deleteBoard(String boardId);

    // 내가 작성한 게시물 보기
    ResponseEntity<?> getMyBoard(int page, String category);

    // 게시물의 인원 모집 여부 상태 변경
    ResponseEntity<?> checkIsFull(String boardId);
}
