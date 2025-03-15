package com.kwhackathon.broom.participant.service;

import com.kwhackathon.broom.participant.dto.ParticipantResponse;
import com.kwhackathon.broom.participant.entity.Participant;
import com.kwhackathon.broom.user.entity.User;
import org.springframework.validation.annotation.Validated;

import java.util.List;

public interface ParticipantService {

    // 해당 채팅방의 참여자 목록 조회
    ParticipantResponse.ParticipantList getParticipantsByBoardId(String boardId);

    // 사용자와 게시판 ID를 기반으로 참가자 조회
    Participant findByUserIdAndBoardId(String userId, String boardId);

    // 참가자 추가
    void addParticipant(String userId, String boardId);

    // 참가자가 모두 찼는 지 여부
    boolean isFull(String boardId);

    // 참가자 나가기
    boolean deleteParticipantByBoardIdAndUserId(String boardId,String userId);

    // 참가자 목록 조회
    List<Participant> findParticipantsByBoardId(String boardId);

    // 방장 여부 조회
    boolean isAuthor(String boardId,String userId);

    // 강퇴 유저 추가
    void addExpellUserByBoardId(String expellId, String boardId);

    // 채팅방 목록 조회
    ParticipantResponse.ChatRoomList getChatRoomListByUser(User user, int page,int size);

}
