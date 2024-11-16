package com.kwhackathon.broom.chatRoom.dto;

import com.kwhackathon.broom.chatRoom.entity.ChatRoomForTeam;
import com.kwhackathon.broom.team.entity.TeamBoard;
import com.kwhackathon.broom.user.entity.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class ChatRoomForTeamDto {
    @Data
    public static class Request{
        private String chatRoomId;          // 채팅방 id
        private User author;                // 게시물 작성자
        private TeamBoard teamBoard;        // 게시물
    }

    @Data
    public static class ResponseForGetChatRoomList {

        private String chatRoomId;          //채팅 id
        private boolean isRead;             // 읽음 기능
        private String authorId;            //게시물 작성자
        private String participantId;       //참여자
        private String opponentNickname;    //상대방 닉네임
        private String teamBoardTitle;      //게시물 제목
        private String lastMessage;         //최근 메시지
        private String lastMessageDaysAgo;     //최근 메시지 생성 날짜 (1일전, 2일전)
        private String militaryChaplain;


        public static ChatRoomForTeamDto.ResponseForGetChatRoomList fromEntity(ChatRoomForTeam chatRoom, User currentUser) {
            ChatRoomForTeamDto.ResponseForGetChatRoomList responseForGetChatRoomList = new ChatRoomForTeamDto.ResponseForGetChatRoomList();
            responseForGetChatRoomList.chatRoomId = chatRoom.getId();
            responseForGetChatRoomList.authorId = chatRoom.getAuthor().getUserId();
            responseForGetChatRoomList.participantId = chatRoom.getParticipant().getUserId();

            //상대방
            User opponent = chatRoom.getAuthor().equals(currentUser) ? chatRoom.getParticipant() : chatRoom.getAuthor();
            responseForGetChatRoomList.opponentNickname = opponent.getNickname();
            responseForGetChatRoomList.militaryChaplain=opponent.getMilitaryChaplain().toString();

            //읽음기능
            if(chatRoom.getAuthor().equals(currentUser)&&chatRoom.isReadByAuthor()){
                responseForGetChatRoomList.isRead=true;
            }else if(chatRoom.getParticipant().equals(currentUser)&&chatRoom.isReadByParticipant()){
                responseForGetChatRoomList.isRead=true;
            }

            responseForGetChatRoomList.teamBoardTitle = chatRoom.getTeamBoard().getTitle();

            //ChatMessageForCarpool lastMessage = chatRoom.getLastChatMessageForCarpool();
            if (chatRoom.getLastChatMessageForCarpool() != null) {
                responseForGetChatRoomList.lastMessage = chatRoom.getLastChatMessageForCarpool().getMessage();
                responseForGetChatRoomList.lastMessageDaysAgo = getTimeDifference(chatRoom.getLastChatMessageForCarpool().getCreatedAt());
            } else {
                responseForGetChatRoomList.lastMessage = "";
                responseForGetChatRoomList.lastMessageDaysAgo = ""; // 메시지가 없는 경우 표시할 값
            }
            return responseForGetChatRoomList;
        }

        private static String getTimeDifference(LocalDateTime messageDate) {
            long days = ChronoUnit.DAYS.between(messageDate, LocalDateTime.now());

            if (days == 0) {
                long hours = ChronoUnit.HOURS.between(messageDate, LocalDateTime.now());
                if (hours == 0) {
                    long minutes = ChronoUnit.MINUTES.between(messageDate, LocalDateTime.now());
                    return minutes + "분 전";
                }
                return hours + "시간 전";
            }

            return days + "일 전";
        }

        // ChatRoomForCarpool 엔티티 생성 메서드
        public static ChatRoomForTeam toEntity(User author, User participant, Optional<TeamBoard> teamBoard) {
            ChatRoomForTeam newRoom = new ChatRoomForTeam();
            newRoom.setAuthor(author);
            newRoom.setParticipant(participant);
            newRoom.setTeamBoard(teamBoard.orElseThrow(() -> new NullPointerException("게시물을 찾을 수 없습니다")));
            newRoom.setCreatedAt(LocalDateTime.now());
            return newRoom;
        }

    }
    @Data
    public static class ResponseForCreateChatRoomList {

        private String chatRoomId;          //채팅 id

        public static ChatRoomForTeamDto.ResponseForCreateChatRoomList fromEntity(ChatRoomForTeam chatRoom) {
            ChatRoomForTeamDto.ResponseForCreateChatRoomList responseForCreateChatRoomList = new ChatRoomForTeamDto.ResponseForCreateChatRoomList();
            responseForCreateChatRoomList.chatRoomId = chatRoom.getId();
            return responseForCreateChatRoomList;
        }

        public static ChatRoomForTeam toEntity(User author, User participant, Optional<TeamBoard> teamBoard) {
            ChatRoomForTeam newRoom = new ChatRoomForTeam();
            newRoom.setAuthor(author);
            newRoom.setParticipant(participant);
            newRoom.setTeamBoard(teamBoard.orElseThrow(() -> new NullPointerException("게시물을 찾을 수 없습니다")));
            newRoom.setCreatedAt(LocalDateTime.now());
            return newRoom;

        }
    }
}
