package com.kwhackathon.broom.board.service;

import org.springframework.validation.annotation.Validated;

import com.kwhackathon.broom.board.dto.BoardRequest.WriteBoardDto;
import com.kwhackathon.broom.board.dto.BoardResponse.BoardCount;
import com.kwhackathon.broom.board.dto.BoardResponse.BoardId;
import com.kwhackathon.broom.board.dto.BoardResponse.BoardList;
import com.kwhackathon.broom.board.dto.BoardResponse.SingleBoardDetail;

import jakarta.validation.Valid;

@Validated
public interface BoardService {
    BoardId createBoard(@Valid WriteBoardDto writeBoardDto);

    BoardList getBoardByCondition(int page, String title,
            String place, String trainingDate, boolean recruiting);

    SingleBoardDetail getSingleBoardDetail(String boardId);

    BoardList getMyBoard(int page);

    void updateBoard(String boardId, @Valid WriteBoardDto writeBoardDto);

    void deleteBoard(String boardId);

    BoardCount getTotalBoardCount();

    BoardList getAlmostFullBoard();
}
