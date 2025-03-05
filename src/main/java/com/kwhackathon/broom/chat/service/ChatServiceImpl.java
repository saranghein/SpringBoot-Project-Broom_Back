package com.kwhackathon.broom.chat.service;

import com.kwhackathon.broom.board.dto.BoardResponse;
import com.kwhackathon.broom.board.service.BoardService;
import com.kwhackathon.broom.chat.dto.ChatRequest;
import com.kwhackathon.broom.chat.dto.ChatResponse;
import com.kwhackathon.broom.chat.entity.Chat;
import com.kwhackathon.broom.chat.repository.ChatRepository;
import com.kwhackathon.broom.participant.entity.Participant;
import com.kwhackathon.broom.participant.service.ParticipantService;
import com.kwhackathon.broom.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    private final RabbitTemplate rabbitTemplate;
    private final ParticipantService participantService;
    private final BoardService boardService;
    private final UserService userService;

    @Value("${broom.exchange-name}")
    private String EXCHANGE_NAME;

    @Value("${broom.routing-prefix}")
    private String ROUTING_KEY_PREFIX;

    // 메시지 저장
    @Transactional
    public Chat saveMessage(ChatRequest.Message messageDto,String senderId) {

        // Participant에서 user와 연결된 정보
        Participant participant=participantService.findByUserIdAndBoardId(senderId, messageDto.getBoardId());

        Chat chat = ChatRequest.Message.toEntity(messageDto, participant);
        chatRepository.save(chat);

        return chat;
    }

    // 메시지 전송 (Producer)
    @Override
    public void sendMessageToRoom(ChatResponse.Message messageDto,String boardId) {
//        String routingKey = ROUTING_KEY + boardId; // 메시지를 보낼 WebSocket 경로 설정
        String routingKey = ROUTING_KEY_PREFIX + boardId;
        String nickname= messageDto.getSenderNickname();

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, routingKey, ChatResponse.Message.fromEntity(messageDto,nickname));// 해당 경로로 메시지 전송
    }


    // 채팅 메시지 조회
    @Override
    public List<Chat> findMessagesByBoardId(String boardId){
        return chatRepository.findByParticipant_Board_BoardIdOrderByCreatedAtAsc(boardId);
    }

    @Override
    public ChatResponse.ChatRoomResponse getChatRoomInfo(String boardId,int page, int size) {
        Page<Chat> chatPage = chatRepository.findByParticipant_Board_BoardIdOrderByCreatedAtAsc(boardId, PageRequest.of(page, size));

        BoardResponse.SingleBoardDetail board = boardService.getSingleBoardDetail(boardId);
        List<Participant> participants = participantService.findParticipantsByBoardId(boardId);

        return ChatResponse.ChatRoomResponse.fromEntities(
                boardId,
                board.getContentDetail().getTitle(),
                board.getAuthor().getNickname(),
                board.getAuthor().getMilitaryBranch().toString(),
                participants,
                chatPage
        );
    }



}
