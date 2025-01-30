package com.kwhackathon.broom.bookmark.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kwhackathon.broom.board.dto.BoardResponse.BoardListElement;
import com.kwhackathon.broom.board.entity.Board;
import com.kwhackathon.broom.board.repository.BoardRepository;
import com.kwhackathon.broom.board.util.category.Category;
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

        User user = userRepository.findById(userId).orElseThrow(() -> new NullPointerException("올바른 사용자 정보가 아닙니다."));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NullPointerException("게시글이 존재하지 않습니다."));

        bookmarkRepository.save(Bookmark.builder().user(user).board(board).build());
    }

    @Override
    public List<BoardListElement> getBookmark(int page, Category category) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(userId).orElseThrow(() -> new NullPointerException("올바른 사용자 정보가 아닙니다."));
        
        return boardRepository.findSliceByBookmarksUser(pageable, user).stream()
                .map((board) -> new BoardListElement(board))
                .collect(Collectors.toList());
        // return bookmarkRepository.findSliceByUserUserIdOrderByBoardCreatedAtDesc(pageable, userId).getContent()
        //         .stream()
        //         .map((bookmark) -> new BoardListElement(bookmark.getBoard()))
        //         .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteBookmark(String boardId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        bookmarkRepository.deleteByUserUserIdAndBoardBoardId(userId, boardId);
    }
}
