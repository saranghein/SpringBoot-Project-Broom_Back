package com.kwhackathon.broom.participant.controller;

import com.kwhackathon.broom.participant.dto.ParticipantRequest;
import com.kwhackathon.broom.user.entity.User;
import org.springframework.http.ResponseEntity;

public interface ParticipantController {

    // 해당 채팅방의 참여자 목록 조회
    ResponseEntity<?> getParticipantsByBoardId(String boardId,int page);

    // 해당 유저의 채팅방 목록 조회
    ResponseEntity<?> getBoardByUserId(User user,int page,int size);

    // 해당 채팅방의 참여자 삭제 (채팅방 나가기-방장이 아닌경우!)
    ResponseEntity<?> deleteParticipantByBoardIdAndUserId(String boardId, User user);

    // 강퇴요청
    ResponseEntity<?> expellRequire(ParticipantRequest.ExpellRequestInfo requestBody, User user);

}
