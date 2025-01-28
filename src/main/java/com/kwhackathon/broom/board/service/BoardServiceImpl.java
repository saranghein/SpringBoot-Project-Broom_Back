package com.kwhackathon.broom.board.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kwhackathon.broom.board.dto.BoardRequest.WriteBoardDto;
import com.kwhackathon.broom.board.dto.BoardResponse.BoardListElement;
import com.kwhackathon.broom.board.dto.BoardResponse.SingleBoardDetail;
import com.kwhackathon.broom.board.entity.Board;
import com.kwhackathon.broom.board.repository.BoardRepository;
import com.kwhackathon.broom.board.util.category.Category;
import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.repository.UserRepository;
import com.kwhackathon.broom.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
// 조회 결과에 hasNext추가될 수 있음
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final static int PAGE_SIZE = 15;

    @Override
    @Transactional
    public String createBoard(WriteBoardDto writeBoardDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.loadUserByUsername(userId);
        Board board = writeBoardDto.toEntity(user);
        boardRepository.save(board);
        return board.getBoardId();
    }

    @Override
    public List<BoardListElement> getAllBoard(int page, String category) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());
        return boardRepository.findSliceByCategory(pageable, Category.valueOf(category)).getContent().stream()
                .map((board) -> new BoardListElement(board))
                .collect(Collectors.toList());
    }

    @Override
    public SingleBoardDetail getSingleBoardDetail(String boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NullPointerException("존재하지 않는 게시물입니다."));
        return new SingleBoardDetail(board, board.getUser());
    }

    @Override
    public List<BoardListElement> searchBoard(int page, String category, String type, String keyword) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());
        List<Board> result = new ArrayList<>();
        if (type.equals("title")) {
            result = boardRepository.findSliceByCategoryAndTitle(pageable, Category.valueOf(category), keyword)
                    .getContent();
        }
        if (type.equals("trainingDate")) {
            result = boardRepository.findSliceByCategoryAndTrainingDate(pageable, Category.valueOf(category),
                    LocalDate.parse(keyword)).getContent();
        }
        if (type.equals("place")) {
            result = boardRepository.findSliceByCategoryAndPlace(pageable, Category.valueOf(category), keyword)
                    .getContent();
        }
        return result.stream().map((board) -> new BoardListElement(board)).collect(Collectors.toList());
    }

    @Override
    public List<BoardListElement> getRecruitingBoard(int page, String category) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());
        return boardRepository.findSliceByCategoryAndIsFull(pageable, Category.valueOf(category), false)
                .getContent()
                .stream()
                .map((board) -> new BoardListElement(board))
                .collect(Collectors.toList());
    }

    @Override
    public List<BoardListElement> getMyBoard(int page, String category) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new NullPointerException("사용자 정보가 없습니다."));
        return user.getBoards().stream().map((board) -> new BoardListElement(board)).collect(Collectors.toList());
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
