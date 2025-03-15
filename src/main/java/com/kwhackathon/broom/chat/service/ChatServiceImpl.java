package com.kwhackathon.broom.chat.service;

import com.kwhackathon.broom.board.dto.BoardResponse;
import com.kwhackathon.broom.board.entity.Board;
import com.kwhackathon.broom.board.repository.BoardRepository;
import com.kwhackathon.broom.board.service.BoardService;
import com.kwhackathon.broom.chat.config.RabbitmqConfig;
import com.kwhackathon.broom.chat.dto.ChatRequest;
import com.kwhackathon.broom.chat.dto.ChatResponse;
import com.kwhackathon.broom.chat.entity.Chat;
import com.kwhackathon.broom.chat.repository.ChatRepository;
import com.kwhackathon.broom.participant.entity.Participant;
import com.kwhackathon.broom.participant.service.ParticipantService;
import com.kwhackathon.broom.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{
    private final ChatRepository chatRepository;
    private final ParticipantService participantService;
    private final BoardService boardService;
    private final BoardRepository boardRepository;


    // 메시지 저장
    @Transactional
    public Chat saveMessage(ChatRequest.Message messageDto,String senderId) {

        // Participant에서 user와 연결된 정보
        Participant participant=participantService.findByUserIdAndBoardId(senderId, messageDto.getBoardId());
        Board board=boardRepository.findByBoardId(messageDto.getBoardId());
        Chat chat = ChatRequest.Message.toEntity(messageDto, participant,board);
        chatRepository.save(chat);

        return chat;
    }

    // 해당 채팅방 정보 조회
    @Override
    public ChatResponse.ChatRoomResponse getChatRoomInfo(String boardId,int page, int size) {
        Page<Chat> chatPage = chatRepository.findByBoard_BoardIdOrderByCreatedAtAsc(boardId, PageRequest.of(page, size));

        BoardResponse.SingleBoardDetail board = boardService.getSingleBoardDetail(boardId);
        List<Participant> participants = participantService.findParticipantsByBoardId(boardId);

        return ChatResponse.ChatRoomResponse.fromEntities(
                board.getContentDetail().getTitle(),
                board.getAuthor().getNickname(),
                participants,
                chatPage
        );
    }

}
