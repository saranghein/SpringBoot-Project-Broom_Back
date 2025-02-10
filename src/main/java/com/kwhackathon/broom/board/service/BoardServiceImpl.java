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

import com.kwhackathon.broom.board.dto.BoardRequest.WriteBoardDto;
import com.kwhackathon.broom.board.dto.BoardResponse.BoardId;
import com.kwhackathon.broom.board.dto.BoardResponse.BoardList;
import com.kwhackathon.broom.board.dto.BoardResponse.BoardListElement;
import com.kwhackathon.broom.board.dto.BoardResponse.SingleBoardDetail;
import com.kwhackathon.broom.board.entity.Board;
import com.kwhackathon.broom.board.repository.BoardRepository;
import com.kwhackathon.broom.board.util.category.Category;
import com.kwhackathon.broom.bookmark.repository.BookmarkRepository;
import com.kwhackathon.broom.participant.entity.Participant;
import com.kwhackathon.broom.participant.repository.ParticipantRepository;
import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
// 모든 반환 값에 북마크인지 아닌지 표시하기 위해 게시판 하나 마다 exist쿼리 하나 나가는데 이거 최적화 필요
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ParticipantRepository participantRepository;
    private final UserService userService;
    private final static int PAGE_SIZE = 15;

    @Override
    @Transactional
    public BoardId createBoard(WriteBoardDto writeBoardDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.loadUserByUsername(userId);
        Board board = writeBoardDto.toEntity(user);
        boardRepository.save(board);
        participantRepository.save(Participant.builder().unread(0L).user(user).board(board).build());

        return new BoardId(board.getBoardId());
    }

    @Override
    public BoardList getAllBoard(int page, String category, boolean isFull) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        System.out.println(isFull);
        Slice<Board> slice = getAllBoardByIsFull(pageable, category, isFull);
        // boardRepository.findSliceByCategory(pageable, Category.valueOf(category));
        List<BoardListElement> elements = slice.getContent().stream()
                .map((board) -> new BoardListElement(board, bookmarkRepository.existsByUserUserIdAndBoardBoardId(
                        userId, board.getBoardId())))
                .collect(Collectors.toList());
        return new BoardList(elements, slice.hasNext());
    }

    private Slice<Board> getAllBoardByIsFull(Pageable pageable, String category, boolean isFull) {
        if (isFull) {
            return boardRepository.findSliceByCategory(pageable, Category.valueOf(category));
        }
        return boardRepository.findSliceByCategoryAndIsFull(pageable, Category.valueOf(category), false);
    }

    @Override
    public SingleBoardDetail getSingleBoardDetail(String boardId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NullPointerException("존재하지 않는 게시물입니다."));
        return new SingleBoardDetail(board.getUser(), board, bookmarkRepository.existsByUserUserIdAndBoardBoardId(
                userId, board.getBoardId()));
    }

    @Override
    public BoardList searchBoard(int page, String category, String type, String keyword, boolean isFull) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Slice<Board> slice = searchByCondition(pageable, category, type, keyword, isFull);
        List<BoardListElement> elements = slice.getContent().stream()
                .map((board) -> new BoardListElement(board, bookmarkRepository.existsByUserUserIdAndBoardBoardId(
                        userId, board
                                .getBoardId())))
                .collect(Collectors.toList());
        return new BoardList(elements, slice.hasNext());
    }

    private Slice<Board> searchByCondition(Pageable pageable, String category, String type, String keyword,
            boolean isFull) {
        if (type.equals("title")) {
            // return boardRepository.findSliceByCategoryAndTitle(pageable,
            // Category.valueOf(category), keyword);
            return searchBoardByTitleAndIsFull(pageable, category, keyword, isFull);
        }
        if (type.equals("trainingDate")) {
            // return boardRepository.findSliceByCategoryAndTrainingDate(pageable,
            // Category.valueOf(category),
            // LocalDate.parse(keyword));
            return searchBoardByTrainingDateAndIsFull(pageable, category, keyword, isFull);
        }
        if (type.equals("place")) {
            // return boardRepository.findSliceByCategoryAndPlace(pageable,
            // Category.valueOf(category), keyword);
            return searchBoardByPlaceAndIsFull(pageable, category, keyword, isFull);
        }
        throw new IllegalArgumentException("올바른 검색조건이 아닙니다.");
    }

    private Slice<Board> searchBoardByTitleAndIsFull(Pageable pageable, String category, String keyword,
            boolean isfull) {
        if (isfull) {
            return boardRepository.findSliceByCategoryAndTitleContaining(pageable, Category.valueOf(category), keyword);
        }
        return boardRepository.findByCategoryAndIsFullAndTitleContaining(pageable, Category.valueOf(category), keyword,
                isfull);
    }

    private Slice<Board> searchBoardByTrainingDateAndIsFull(Pageable pageable, String category, String keyword,
            boolean isfull) {
        if (isfull) {
            return boardRepository.findSliceByCategoryAndTrainingDate(pageable, Category.valueOf(category),
                    LocalDate.parse(keyword));
        }
        return boardRepository.findSliceByCategoryAndIsFullAndTrainingDate(pageable, Category.valueOf(category),
                LocalDate.parse(keyword), isfull);
    }

    private Slice<Board> searchBoardByPlaceAndIsFull(Pageable pageable, String category, String keyword,
            boolean isfull) {
        if (isfull) {
            return boardRepository.findSliceByCategoryAndPlaceContaining(pageable, Category.valueOf(category), keyword);
        }
        return boardRepository.findSliceByCategoryAndIsFullAndPlaceContaining(pageable, Category.valueOf(category), keyword,
                isfull);
    }

    // @Override
    // public BoardList getRecruitingBoard(int page, String category) {
    // Pageable pageable = PageRequest.of(page, PAGE_SIZE,
    // Sort.by("createdAt").descending());
    // String userId =
    // SecurityContextHolder.getContext().getAuthentication().getName();
    // Slice<Board> slice = boardRepository.findSliceByCategoryAndIsFull(pageable,
    // Category.valueOf(category), false);
    // List<BoardListElement> elements = slice.getContent().stream()
    // .map((board) -> new BoardListElement(board,
    // bookmarkRepository.existsByUserUserIdAndBoardBoardId(
    // userId, board
    // .getBoardId())))
    // .collect(Collectors.toList());
    // return new BoardList(elements, slice.hasNext());
    // }

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
