package com.kwhackathon.broom.bookmark.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kwhackathon.broom.board.dto.BoardResponse.BoardList;
import com.kwhackathon.broom.board.dto.BoardResponse.BoardListElement;
import com.kwhackathon.broom.board.dto.BoardResponse.BoardWithBookmarkDto;
import com.kwhackathon.broom.board.entity.Board;
import com.kwhackathon.broom.board.repository.BoardRepository;
import com.kwhackathon.broom.bookmark.dto.BookmarkRequest.CreateDto;
import com.kwhackathon.broom.bookmark.entity.Bookmark;
import com.kwhackathon.broom.bookmark.repository.BookmarkRepository;
import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkServiceImpl implements BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final static int PAGE_SIZE = 15;

    @Override
    @Transactional
    public void createBookmark(CreateDto createDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        String boardId = createDto.getBoardId();

        if (bookmarkRepository.existsByUserUserIdAndBoardBoardId(userId, boardId)) {
            throw new IllegalArgumentException("이미 북마크로 등록된 게시물입니다.");
        }

        // user와 board의 프록시 객체 생성
        User user = userRepository.getReferenceById(userId);
        Board board = boardRepository.getReferenceById(boardId);

        bookmarkRepository.save(Bookmark.builder().user(user).board(board).build());
    }

    @Override
    public BoardList getBookmark(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Slice<BoardWithBookmarkDto> slice = boardRepository.findSliceByBookmarksUserUserId(pageable, userId);
        List<BoardListElement> result = slice.getContent().stream()
                .map((boardWithBookmarkDto) -> new BoardListElement(
                        boardWithBookmarkDto.getBoard(), 
                        boardWithBookmarkDto.getParticipantCount(),
                        boardWithBookmarkDto.isBookmarked()))
                .collect(Collectors.toList());
        return new BoardList(result, slice.hasNext());
    }

    @Override
    @Transactional
    public void deleteBookmark(String boardId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        bookmarkRepository.deleteByUserUserIdAndBoardBoardId(userId, boardId);
    }
}
