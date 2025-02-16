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

    BoardList getAllBoard(int page, boolean isFull);

    SingleBoardDetail getSingleBoardDetail(String boardId);

    BoardList searchBoard(int page, String type, String keyword, boolean isFull);

    BoardList getMyBoard(int page);

    void updateBoard(String boardId, @Valid WriteBoardDto writeBoardDto);

    void deleteBoard(String boardId);
}
