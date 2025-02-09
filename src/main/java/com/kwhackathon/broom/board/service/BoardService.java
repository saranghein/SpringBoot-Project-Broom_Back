package com.kwhackathon.broom.board.service;

import org.springframework.validation.annotation.Validated;

import com.kwhackathon.broom.board.dto.BoardRequest.WriteBoardDto;
import com.kwhackathon.broom.board.dto.BoardResponse.BoardId;
import com.kwhackathon.broom.board.dto.BoardResponse.BoardList;
import com.kwhackathon.broom.board.dto.BoardResponse.SingleBoardDetail;

import jakarta.validation.Valid;

@Validated
public interface BoardService {
    BoardId createBoard(@Valid WriteBoardDto writeBoardDto);

    BoardList getAllBoard(int page, String category, boolean isFull);

    SingleBoardDetail getSingleBoardDetail(String boardId);

    BoardList searchBoard(int page, String category, String type, String keyword, boolean isFull);

    // BoardList getRecruitingBoard(int page, String category);

    BoardList getMyBoard(int page, String category);

    void updateBoard(String boardId, @Valid WriteBoardDto writeBoardDto);

    void updateIsFull(String boardId);

    void deleteBoard(String boardId);
}
