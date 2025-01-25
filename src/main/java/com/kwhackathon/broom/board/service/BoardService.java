package com.kwhackathon.broom.board.service;

import java.util.List;

import com.kwhackathon.broom.board.dto.BoardRequest.WriteBoardDto;
import com.kwhackathon.broom.board.dto.BoardResponse.BoardListElement;
import com.kwhackathon.broom.board.dto.BoardResponse.SingleBoardDetail;

public interface BoardService {
    String createBoard(WriteBoardDto writeBoardDto);

    List<BoardListElement> getAllBoard(int page, String category);

    SingleBoardDetail getSingleBoardDetail(String boardId);

    List<BoardListElement> searchBoard(int page, String category, String type, String keyword);

    List<BoardListElement> getRecruitingBoard(int page, String category);

    List<BoardListElement> getMyBoard(int page, String category);

    void updateBoard(String boardId, WriteBoardDto writeBoardDto);

    void updateIsFull(String boardId);

    void deleteBoard(String boardId);
}
