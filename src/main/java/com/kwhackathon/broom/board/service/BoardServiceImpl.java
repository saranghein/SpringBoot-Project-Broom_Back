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
import com.kwhackathon.broom.board.dto.BoardResponse.BoardWithBookmarkDto;
import com.kwhackathon.broom.board.dto.BoardResponse.SingleBoardDetail;
import com.kwhackathon.broom.board.entity.Board;
import com.kwhackathon.broom.board.repository.BoardRepository;
import com.kwhackathon.broom.participant.entity.Participant;
import com.kwhackathon.broom.participant.repository.ParticipantRepository;
import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ParticipantRepository participantRepository;
    private final static int PAGE_SIZE = 15;

    @Override
    @Transactional
    public BoardId createBoard(WriteBoardDto writeBoardDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.getReferenceById(userId);
        Board board = writeBoardDto.toEntity(user);
        boardRepository.save(board);
        participantRepository.save(Participant.builder().unread(0L).user(user).board(board).build());

        return new BoardId(board.getBoardId());
    }

    @Override
    public BoardList getAllBoard(int page, boolean recruiting) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Slice<BoardWithBookmarkDto> slice = getAllBoardByRecruiting(pageable, userId, recruiting);
        
        List<BoardListElement> elements = slice.getContent().stream()
                .map((boardWithBookmark)-> new BoardListElement(boardWithBookmark.getBoard(), boardWithBookmark.getParticipantCount(),boardWithBookmark.isBookmarked()))
                .collect(Collectors.toList());

        return new BoardList(elements, slice.hasNext());
    }

    private Slice<BoardWithBookmarkDto> getAllBoardByRecruiting(Pageable pageable, String userId,boolean recruiting) {
        if (recruiting) {
            return boardRepository.findSliceBoardWithBookmarkByRecruiting(pageable, userId);
        }
        return boardRepository.findSliceBoardWithBookmark(pageable, userId);
    }

    @Override
    public SingleBoardDetail getSingleBoardDetail(String boardId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        BoardWithBookmarkDto boardWithBookmarkDto = boardRepository.findBoardWithBookmarkById(
                userId, boardId).orElseThrow(() -> new NullPointerException("존재하지 않는 게시물입니다."));
        return new SingleBoardDetail(boardWithBookmarkDto);
    }

    @Override
    public BoardList searchBoard(int page, String type, String keyword, boolean recruiting) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Slice<BoardWithBookmarkDto> slice = searchByCondition(pageable, type, keyword, recruiting, userId);
        List<BoardListElement> elements = slice.getContent().stream()
                .map((boardWithBookmark) -> new BoardListElement(boardWithBookmark.getBoard(),
                        boardWithBookmark.getParticipantCount(), boardWithBookmark.isBookmarked()))
                .collect(Collectors.toList());
        return new BoardList(elements, slice.hasNext());
    }

    private Slice<BoardWithBookmarkDto> searchByCondition(Pageable pageable, String type, String keyword,
            boolean recruiting, String userId) {
        if (type.equals("title")) {
            return searchBoardByTitleAndRecruiting(pageable, keyword, recruiting, userId);
        }
        if (type.equals("trainingDate")) {
            return searchBoardByTrainingDateAndRecruiting(pageable, keyword, recruiting, userId);
        }
        if (type.equals("place")) {
            return searchBoardByPlaceAndRecruiting(pageable, keyword, recruiting, userId);
        }
        throw new IllegalArgumentException("올바른 검색조건이 아닙니다.");
    }

    private Slice<BoardWithBookmarkDto> searchBoardByTitleAndRecruiting(Pageable pageable, String keyword, boolean recruiting, String userId) {
        if (recruiting) {
            return boardRepository.findByRecruitingAndTitleContaining(pageable, keyword, userId);
        }
        return boardRepository.findSliceByTitleContaining(pageable, keyword, userId);
    }

    private Slice<BoardWithBookmarkDto> searchBoardByTrainingDateAndRecruiting(Pageable pageable, String keyword,
            boolean recruiting, String userId) {
        if (recruiting) {
            return boardRepository.findSliceByRecruitingAndTrainingDate(pageable,
                LocalDate.parse(keyword), userId);
        }
        return boardRepository.findSliceByTrainingDate(pageable,
                    LocalDate.parse(keyword), userId);
    }

    private Slice<BoardWithBookmarkDto> searchBoardByPlaceAndRecruiting(Pageable pageable, String keyword,
            boolean recruiting, String userId) {
        if (recruiting) {
            return boardRepository.findSliceByRecruitingAndPlaceContaining(pageable, keyword, userId);
        }
        return boardRepository.findSliceByPlaceContaining(pageable, keyword, userId);
    }

    @Override
    public BoardList getMyBoard(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        System.out.println("======");
        Slice<BoardWithBookmarkDto> slice = boardRepository.findSliceByUserUserId(pageable, userId);
        List<BoardListElement> elements = slice.getContent().stream()
                .map((boardWithBookmarkDto) -> new BoardListElement(
                        boardWithBookmarkDto.getBoard(), 
                        boardWithBookmarkDto.getParticipantCount(),
                        boardWithBookmarkDto.isBookmarked()))
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
    public void deleteBoard(String boardId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NullPointerException("게시글이 존재하지 않습니다."));
        if (!board.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("올바른 사용자가 아닙니다.");
        }
        boardRepository.deleteById(boardId);
    }
}
