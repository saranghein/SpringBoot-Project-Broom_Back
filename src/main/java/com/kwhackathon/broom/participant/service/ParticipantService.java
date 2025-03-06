package com.kwhackathon.broom.participant.service;

import com.kwhackathon.broom.participant.dto.ParticipantResponse;
import com.kwhackathon.broom.participant.entity.Participant;
import com.kwhackathon.broom.user.entity.User;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface ParticipantService {

    // 해당 채팅방의 참여자 목록 조회
    ParticipantResponse.ParticipantList getParticipantsByBoardId(String boardId);

    // 해당 유저의 채팅방 목록 조회 (게시판의 기능과 겹침)
    //BoardResponse.BoardList getBoardByParticipantId(String participantId);

    // 사용자와 게시판 ID를 기반으로 참가자 조회
    Participant findByUserIdAndBoardId(String userId, String boardId);

    // 참가자 추가
    void addParticipant(String userId, String boardId);

    boolean isFull(String boardId);

    boolean deleteParticipantByBoardIdAndUserId(String boardId,String userId);

    List<Participant> findParticipantsByBoardId(String boardId);


    boolean isAuthor(String boardId,String userId);

    void addExpellUserByBoardId(String expellId, String boardId);

    ParticipantResponse.ChatRoomList getChatRoomListByUser(User user, int page,int size);

//    List<String> findUserIdsByBoardId(String boardId);
}
