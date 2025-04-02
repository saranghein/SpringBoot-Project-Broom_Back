package com.kwhackathon.broom.chat.service;

import com.kwhackathon.broom.board.entity.Board;
import com.kwhackathon.broom.chat.component.AckMessageListener;
import com.kwhackathon.broom.chat.component.QueueManager;
import com.kwhackathon.broom.participant.dto.ParticipantResponse;
import com.kwhackathon.broom.participant.entity.Participant;
import com.kwhackathon.broom.participant.repository.ParticipantRepository;
import com.kwhackathon.broom.participant.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final QueueManager queueManager;
    private final AckMessageListener ackMessageListener;
    private final ParticipantRepository participantRepository;

    @Override
    public void createChatRoom(String boardId){
        queueManager.createChatRoomQueue(boardId);
    }

    @Override
    public void deleteChatRoom(String boardId) {
        queueManager.deleteChatRoomQueue(boardId);
    }

    @Override
    public void createUserRoom(String userNickname, String boardId) {
        queueManager.createQueueForUserInRoom(boardId, userNickname);
        ackMessageListener.registerAckListener(boardId, userNickname);

    }

    @Override
    public void deleteUserRoom(String userNickname, String boardId) {
        queueManager.deleteQueueForUserInRoom( boardId,userNickname);
    }

    @Override
    public void deleteAllUserRooms(String boardId) {
//        ParticipantResponse.ParticipantList participantList = participantService.getParticipantsByBoardId(boardId);
        List<Participant>Users=  participantRepository.findByBoard_BoardId(boardId);
        // author 큐 삭제
//        deleteUserRoom(participantList.getAuthor().getUserNickname(), boardId);

        // 참가자 큐 삭제
        for (Participant participant : Users) {
            deleteUserRoom(participant.getUser().getNickname(), boardId);
        }
    }
}
