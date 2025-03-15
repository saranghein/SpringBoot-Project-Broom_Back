package com.kwhackathon.broom.participant.service;

import com.kwhackathon.broom.board.entity.Board;
import com.kwhackathon.broom.board.repository.BoardRepository;
import com.kwhackathon.broom.board.service.BoardService;
import com.kwhackathon.broom.chat.entity.Chat;
import com.kwhackathon.broom.chat.repository.ChatRepository;
import com.kwhackathon.broom.participant.dto.ParticipantResponse;
import com.kwhackathon.broom.participant.entity.Participant;
import com.kwhackathon.broom.participant.repository.ParticipantRepository;
import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {
    private final ParticipantRepository participantRepository;
    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final UserService userService;
    private final ChatRepository chatRepository;

    // 해당 채팅방의 참여자 목록 조회
    @Override
    // 현재 로그인한 사용자가 boardId의 참여자인지 확인한 후 메서드 실행
    // authentication.name → 현재 인증된 userId
    @PreAuthorize("@participantRepository.existsByUserUserIdAndBoardBoardId(authentication.name, #boardId)")
    public ParticipantResponse.ParticipantList getParticipantsByBoardId(String boardId) {

        // 참가자 조회
        List<Participant> participants=participantRepository.findActiveParticipantsByBoardId(boardId);

        // 작성자 조회
        Participant author = participants.stream()
                .filter(participant -> isAuthor(boardId, participant.getUser().getUserId())) // isAuthor() 이용
                .findFirst()
                .orElse(null); // 작성자가 없는 경우


        // 일반 참가자 리스트 필터링 (작성자 제외)
        List<Participant> filteredParticipants = participants.stream()
                .filter(participant -> author == null || !participant.getUser().getUserId().equals(author.getUser().getUserId()))
                .toList();

        // List 변환
        return ParticipantResponse.ParticipantList.fromEntities(
                boardService.getSingleBoardDetail(boardId).getContentDetail().getTitle(),
                author,
                filteredParticipants,
                boardService.getSingleBoardDetail(boardId).getContentDetail().getTrainingDate());
    }

    // 사용자와 게시판 ID를 기반으로 참가자 조회
    @Override
    public Participant findByUserIdAndBoardId(String userId, String boardId) {
        return participantRepository.findByUser_UserIdAndBoard_BoardId(userId, boardId)
                .orElse(null);
    }

    // 참가자 추가
    @Override
    @Transactional
    public void addParticipant(String userId, String boardId) {
        // User 엔티티 조회
        User user = userService.loadUserByUsername(userId);
        // Board 엔티티 조회
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판 없음: " + boardId));

        // 새로운 참가자 생성
        Participant participant = new Participant();
        participant.setUser(user);
        participant.setBoard(board);
        participant.setUnread(0L); // 기본적으로 안 읽은 메시지 수는 0
        participantRepository.save(participant); // 새 참가자를 저장
    }

    @Override
    public boolean isFull(String boardId) {

        // 현재 인원
        return !boardRepository.existsEmptySeatByBoardId(boardId);

    }

    @Override
    @Transactional
    public boolean deleteParticipantByBoardIdAndUserId(String boardId, String userId) {

        // participant_id를 NULL로 변경
        chatRepository.setParticipantToNull(userId,boardId);

        // 참여자 삭제
        int deletedCount = participantRepository.deleteByUserIdAndBoardId(userId, boardId);

        return deletedCount > 0;

    }

    @Override
    public List<Participant> findParticipantsByBoardId(String boardId) {
        return participantRepository.findByBoard_BoardIdAndIsExpelledFalse(boardId);
    }

    @Override
    public boolean isAuthor(String boardId, String userId) {
        return boardService.getSingleBoardDetail(boardId).getAuthor().getNickname().equals(
                userService.loadUserByUsername(userId).getNickname()
        );
    }

    @Override
    @Transactional
    public void addExpellUserByBoardId(String expellId, String boardId) {

        // 강퇴할 참가자 찾기
        Optional<Participant> participantOptional = participantRepository.findByUser_UserIdAndBoard_BoardId(expellId, boardId);

        if (participantOptional.isPresent()) {
            Participant participant = participantOptional.get();
            participant.setIsExpelled(true); // isExpelled 값을 true로 변경
            participantRepository.save(participant);
        } else {
            throw new EntityNotFoundException("해당 유저가 채팅방에 없습니다.");
        }
    }

    @Override
    public ParticipantResponse.ChatRoomList getChatRoomListByUser(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size); // 페이지네이션 적용

        // 유저가 참여한 채팅방 목록을 가져옴
//        Page<Participant> participantPage = participantRepository.findByUser(user, pageable);

        // 최신 메시지 기준 정렬된 채팅방 목록 가져오기
        Page<Participant> participantPage = participantRepository.findParticipantsByUserOrderByLatestChatTime(user, pageable);


        // 각 채팅방의 최신 메시지 가져와서 DTO 변환
        List<ParticipantResponse.ChatRoomElement> chatRooms = participantPage.getContent().stream()
                .map(participant -> {
                    // 최신 메시지 조회
                    Optional<Chat> latestChat = chatRepository.findLatestMessageByBoardId(participant.getBoard().getBoardId());

                    String lastMessage = latestChat.map(Chat::getMessage).orElse("");
                    LocalDateTime lastMessageTime = latestChat.map(Chat::getCreatedAt).orElse(null);

                    // DTO 변환
                    return ParticipantResponse.ChatRoomElement.fromEntity(participant, lastMessage, lastMessageTime);
                })
                .collect(Collectors.toList());

        return new ParticipantResponse.ChatRoomList(chatRooms, participantPage.hasNext());
    }
}


