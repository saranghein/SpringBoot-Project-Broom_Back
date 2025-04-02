package com.kwhackathon.broom.board.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.kwhackathon.broom.chat.config.RabbitmqConfig;
import com.kwhackathon.broom.chat.service.ChatRoomService;
import com.kwhackathon.broom.chat.service.ChatService;

import com.kwhackathon.broom.participant.dto.ParticipantResponse;
import com.kwhackathon.broom.participant.service.ParticipantService;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kwhackathon.broom.board.dto.BoardRequest.WriteBoardDto;
import com.kwhackathon.broom.board.dto.BoardResponse.BoardCount;
import com.kwhackathon.broom.board.dto.BoardResponse.BoardId;
import com.kwhackathon.broom.board.dto.BoardResponse.BoardList;
import com.kwhackathon.broom.board.dto.BoardResponse.BoardListElement;
import com.kwhackathon.broom.board.dto.BoardResponse.BoardWithBookmarkDto;
import com.kwhackathon.broom.board.dto.BoardResponse.SingleBoardDetail;
import com.kwhackathon.broom.board.entity.Board;
import com.kwhackathon.broom.board.repository.BoardRepository;
import com.kwhackathon.broom.board.repository.BoardSearchRepository;
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
    private final BoardSearchRepository boardSearchRepository;
    private final ParticipantRepository participantRepository;
    private final static int PAGE_SIZE = 15;
    private final ChatRoomService chatRoomService;

    @Override
    @Transactional
    public BoardId createBoard(WriteBoardDto writeBoardDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.getReferenceById(userId);
        Board board = writeBoardDto.toEntity(user);
        boardRepository.save(board);
        Participant participant = new Participant(null, 0L, user, board,false,null);
        participantRepository.save(participant);
        chatRoomService.createChatRoom(board.getBoardId()); // 채팅방이 생성될 때 RabbitMQ 동적 큐도 생성
        chatRoomService.createUserRoom(user.getNickname(), board.getBoardId());// 유저 큐 생성
        return new BoardId(board.getBoardId());
    }

    @Override
    public BoardList getBoardByCondition(int page, String title,
            String place, String trainingDate, boolean recruiting) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());

        Slice<BoardWithBookmarkDto> slice = boardSearchRepository.findBoardWithBookmarkByCondition(pageable, userId,
                title, place,
                trainingDate, recruiting);
        List<BoardListElement> elements = slice.getContent().stream()
                .map((boardWithBookmark) -> new BoardListElement(boardWithBookmark.getBoard(),
                        boardWithBookmark.getParticipantCount(), boardWithBookmark.isBookmarked()))
                .collect(Collectors.toList());
        return new BoardList(elements, slice.hasNext());
    }

    @Override
    public SingleBoardDetail getSingleBoardDetail(String boardId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        BoardWithBookmarkDto boardWithBookmarkDto = boardRepository.findBoardWithBookmarkById(
                userId, boardId).orElseThrow(() -> new NullPointerException("존재하지 않는 게시물입니다."));
        return new SingleBoardDetail(boardWithBookmarkDto);
    }

    @Override
    public BoardList getMyBoard(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

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
            throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
        }

        // 채팅방 큐 삭제
        chatRoomService.deleteChatRoom(boardId);

        // 유저 큐 삭제
        chatRoomService.deleteAllUserRooms(boardId);

        boardRepository.deleteById(boardId);

    }

    @Override
    public BoardCount getTotalBoardCount() {
        return new BoardCount(boardRepository.countTotalBoard());
    }

    @Override
    public BoardList getAlmostFullBoard() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Pageable pageable = PageRequest.of(0, 10);

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7L);
        
        Slice<BoardWithBookmarkDto> slice = boardRepository.findAlmostFullBoardEachDates(pageable, 
                userId, sevenDaysAgo);

        List<BoardListElement> elements = slice.getContent().stream()
                .map((boardWithBookmark) -> new BoardListElement(boardWithBookmark.getBoard(),
                        boardWithBookmark.getParticipantCount(), boardWithBookmark.isBookmarked()))
                .collect(Collectors.toList());
        return new BoardList(elements, slice.hasNext());
    }
}
