package com.kwhackathon.broom.board.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.kwhackathon.broom.board.dto.BoardRequest.WriteBoardDto;
import com.kwhackathon.broom.board.dto.BoardResponse.BoardId;
import com.kwhackathon.broom.board.dto.BoardResponse.BoardList;
import com.kwhackathon.broom.board.dto.BoardResponse.BoardListElement;
import com.kwhackathon.broom.board.dto.BoardResponse.SingleBoardDetail;
import com.kwhackathon.broom.board.entity.Board;
import com.kwhackathon.broom.board.repository.BoardRepository;
import com.kwhackathon.broom.board.util.category.Category;
import com.kwhackathon.broom.bookmark.repository.BookmarkRepository;
import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
// 모든 반환 값에 북마크인지 아닌지 표시하기 위해 게시판 하나 마다 exist쿼리 하나 나가는데 이거 최적화 필요
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserService userService;
    private final static int PAGE_SIZE = 15;

    @Override
    @Transactional
    public BoardId createBoard(WriteBoardDto writeBoardDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.loadUserByUsername(userId);
        Board board = writeBoardDto.toEntity(user);
        boardRepository.save(board);

        return new BoardId(board.getBoardId());
    }

    @Override
    public BoardList getAllBoard(int page, String category) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Slice<Board> slice = boardRepository.findSliceByCategory(pageable, Category.valueOf(category));
        List<BoardListElement> elements = slice.getContent().stream()
                .map((board) -> new BoardListElement(board, bookmarkRepository.existsByUserUserIdAndBoardBoardId(
                        userId, board.getBoardId())))
                .collect(Collectors.toList());
        return new BoardList(elements, slice.hasNext());
    }

    @Override
    public SingleBoardDetail getSingleBoardDetail(String boardId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NullPointerException("존재하지 않는 게시물입니다."));
        return new SingleBoardDetail(board.getUser(), board, bookmarkRepository.existsByUserUserIdAndBoardBoardId(
                userId, board.getBoardId()));
    }

    @Override
    public BoardList searchBoard(int page, String category, String type, String keyword) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Slice<Board> slice = searchByCondition(pageable, category, type, keyword);
        List<BoardListElement> elements = slice.getContent().stream()
                .map((board) -> new BoardListElement(board, bookmarkRepository.existsByUserUserIdAndBoardBoardId(
                        userId, board
                                .getBoardId())))
                .collect(Collectors.toList());
        return new BoardList(elements, slice.hasNext());
    }

    private Slice<Board> searchByCondition(Pageable pageable, String category, String type, String keyword) {
        if (type.equals("title")) {
            return boardRepository.findSliceByCategoryAndTitle(pageable, Category.valueOf(category), keyword);
        }
        if (type.equals("trainingDate")) {
            return boardRepository.findSliceByCategoryAndTrainingDate(pageable, Category.valueOf(category),
                    LocalDate.parse(keyword));
        }
        if (type.equals("place")) {
            return boardRepository.findSliceByCategoryAndPlace(pageable, Category.valueOf(category), keyword);
        }
        throw new IllegalArgumentException("올바른 검색조건이 아닙니다.");
    }

    @Override
    public BoardList getRecruitingBoard(int page, String category) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Slice<Board> slice = boardRepository.findSliceByCategoryAndIsFull(pageable, Category.valueOf(category), false);
        List<BoardListElement> elements = slice.getContent().stream()
                .map((board) -> new BoardListElement(board, bookmarkRepository.existsByUserUserIdAndBoardBoardId(
                        userId, board
                                .getBoardId())))
                .collect(Collectors.toList());
        return new BoardList(elements, slice.hasNext());
    }

    @Override
    public BoardList getMyBoard(int page, String category) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Slice<Board> slice = boardRepository.findSliceByCategoryAndUserUserId(pageable, Category.valueOf(category),
                userId);
        List<BoardListElement> elements = slice.getContent().stream()
                .map((board) -> new BoardListElement(board, bookmarkRepository.existsByUserUserIdAndBoardBoardId(
                        userId, board
                                .getBoardId())))
                .collect(Collectors.toList());
        return new BoardList(elements, slice.hasNext());
    }

    @Override
    @Transactional
    public void updateBoard(String boardId, WriteBoardDto writeBoardDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NullPointerException("게시글이 존재하지 않습니다."));
        if (!board.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("올바른 사용자가 아닙니다.");
        }
        board.updateBoard(writeBoardDto);
    }

    @Override
    @Transactional
    public void updateIsFull(String boardId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NullPointerException("게시글이 존재하지 않습니다."));
        if (!board.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("올바른 사용자가 아닙니다.");
        }
        board.changeIsFullStatus();
    }

    @Override
    @Transactional
    public void deleteBoard(String boardId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NullPointerException("게시글이 존재하지 않습니다."));
        if (!board.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("올바른 사용자가 아닙니다.");
        }
        boardRepository.deleteById(boardId);
    }
}
