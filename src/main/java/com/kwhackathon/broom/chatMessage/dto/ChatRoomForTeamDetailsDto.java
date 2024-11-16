package com.kwhackathon.broom.chatMessage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomForTeamDetailsDto {
    private String teamBoardTitle; // 카풀 게시판 제목
    private String opponentNickname; // 상대방 닉네임
    private int yearsSinceDischarge; // 전역 몇 년 차
    private String militaryChaplain; // 육군, 해군, ...
    private List<ChatMessageForTeamDto.ResponseForDetail> previousMessages; // 이전 채팅 메시지 리스트
}
