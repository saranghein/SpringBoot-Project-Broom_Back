package com.kwhackathon.broom.board.controller;

import org.springframework.http.ResponseEntity;

import com.kwhackathon.broom.board.dto.BoardRequest.WriteBoardDto;

public interface BoardController {
    // 게시물 생성
    ResponseEntity<?> createBoard(WriteBoardDto writeBoardDto);

    // 조건에 따라 게시물 리스트 조회, 검색
    ResponseEntity<?> getBoardByCondition(int page, String title, String place, String trainingDate, boolean recruiting);

    // 단일 게시물 내용 조회
    ResponseEntity<?> getSingleBoardDetail(String boardId);

    // 게시물 수정
    ResponseEntity<?> editBoard(String boardId, WriteBoardDto writeBoardDto);

    // 게시물 삭제
    ResponseEntity<?> deleteBoard(String boardId);

    // 내가 작성한 게시물 보기
    ResponseEntity<?> getMyBoard(int page);

    ResponseEntity<?> getTotalBoardCount();

    // 최근 7일 내에 작성된 글 중에 남은 모집 인원이 가장 적은 글을 날짜 태그 맞는 날짜로 가져오기
    ResponseEntity<?> getAlmostFull();
}
