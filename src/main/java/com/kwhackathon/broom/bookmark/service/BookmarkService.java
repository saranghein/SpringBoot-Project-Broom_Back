package com.kwhackathon.broom.bookmark.service;

import com.kwhackathon.broom.board.dto.BoardResponse.BoardList;
import com.kwhackathon.broom.bookmark.dto.BookmarkRequest.CreateDto;

public interface BookmarkService {
    // 게시글을 북마크로 추가
    void createBookmark(CreateDto createDto);

    // 카테고리 별로 북마크로 추가한 게시글들 조회
    BoardList getBookmark(int page);

    // 북마크를 해제
    void deleteBookmark(String boardId);
}
