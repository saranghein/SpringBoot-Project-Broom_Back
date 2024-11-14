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
        private TeamBoard teamBoard; // 게시물
    }

    @Data
    public static class ResponseForGetChatRoomList {

        private String chatRoomId;          //채팅 id
        //private LocalDateTime createdAt;    //생성 날짜
        //private boolean isReadByAuthor;     //읽음 기능
        //private boolean isReadByParticipant;//읽음 기능
        private boolean isRead;             // 읽음 기능
        private String authorId;            //게시물 작성자
        private String participantId;       //참여자
        private String opponentNickname;    //상대방 닉네임
        private String teamBoardTitle;   //게시물 제목
        private String lastMessage;         //최근 메시지
        private int lastMessageDaysAgo;     //최근 메시지 생성 날짜 (1일전, 2일전)

        public static ChatRoomForTeamDto.ResponseForGetChatRoomList fromEntity(ChatRoomForTeam chatRoom, User currentUser) {
            ChatRoomForTeamDto.ResponseForGetChatRoomList responseForGetChatRoomList = new ChatRoomForTeamDto.ResponseForGetChatRoomList();
            responseForGetChatRoomList.chatRoomId = chatRoom.getId();
            //responseForGetChatRoomList.createdAt = chatRoom.getCreatedAt();
            //responseForGetChatRoomList.isReadByAuthor = chatRoom.isReadByAuthor();
            //responseForGetChatRoomList.isReadByParticipant = chatRoom.isReadByParticipant();
            responseForGetChatRoomList.authorId = chatRoom.getAuthor().getUserId();
            responseForGetChatRoomList.participantId = chatRoom.getParticipant().getUserId();

            //상대방
            User opponent = chatRoom.getAuthor().equals(currentUser) ? chatRoom.getParticipant() : chatRoom.getAuthor();
            responseForGetChatRoomList.opponentNickname = opponent.getNickname();

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
                responseForGetChatRoomList.lastMessageDaysAgo = getDaysAgo(chatRoom.getLastChatMessageForCarpool().getCreatedAt());
            } else {
                responseForGetChatRoomList.lastMessage = "";
                responseForGetChatRoomList.lastMessageDaysAgo = -1; // 메시지가 없는 경우 표시할 값
            }
            return responseForGetChatRoomList;
        }

        private static int getDaysAgo(LocalDateTime messageDate) {
            return (int) ChronoUnit.DAYS.between(messageDate, LocalDateTime.now());
        }

        // ChatRoomForCarpool 엔티티 생성 메서드
        public static ChatRoomForTeam toEntity(User author, User participant, Optional<TeamBoard> teamBoard) {
            ChatRoomForTeam newRoom = new ChatRoomForTeam();
            newRoom.setAuthor(author);
            newRoom.setParticipant(participant);
            newRoom.setTeamBoard(teamBoard.orElse(null));
            newRoom.setCreatedAt(LocalDateTime.now());
            return newRoom;
        }
    }
    @Data
    public static class ResponseForCreateChatRoomList {

        private String chatRoomId;          //채팅 id
        //private LocalDateTime createdAt;    //생성 날짜
        //private boolean isReadByAuthor;     //읽음 기능
        //private boolean isReadByParticipant;//읽음 기능
        //private boolean isRead;             // 읽음 기능
        private String authorId;            //게시물 작성자
        private String participantId;       //참여자
        private String opponentNickname;    //상대방 닉네임
        private String teamBoardTitle;   //게시물 제목
        //private String lastMessage;         //최근 메시지
        //private int lastMessageDaysAgo;     //최근 메시지 생성 날짜 (1일전, 2일전)

        public static ChatRoomForTeamDto.ResponseForCreateChatRoomList fromEntity(ChatRoomForTeam chatRoom, User currentUser) {
            ChatRoomForTeamDto.ResponseForCreateChatRoomList responseForCreateChatRoomList = new ChatRoomForTeamDto.ResponseForCreateChatRoomList();
            responseForCreateChatRoomList.chatRoomId = chatRoom.getId();
            //responseForGetChatRoomList.createdAt = chatRoom.getCreatedAt();
            //responseForGetChatRoomList.isReadByAuthor = chatRoom.isReadByAuthor();
            //responseForGetChatRoomList.isReadByParticipant = chatRoom.isReadByParticipant();
            responseForCreateChatRoomList.authorId = chatRoom.getAuthor().getUserId();
            responseForCreateChatRoomList.participantId = chatRoom.getParticipant().getUserId();

            //상대방
            User opponent = chatRoom.getAuthor().equals(currentUser) ? chatRoom.getParticipant() : chatRoom.getAuthor();
            responseForCreateChatRoomList.opponentNickname = opponent.getNickname();

            responseForCreateChatRoomList.teamBoardTitle = chatRoom.getTeamBoard().getTitle();

            //ChatMessageForCarpool lastMessage = chatRoom.getLastChatMessageForCarpool();

            return responseForCreateChatRoomList;
        }

        public static ChatRoomForTeam toEntity(User author, User participant, Optional<TeamBoard> teamBoard) {
            ChatRoomForTeam newRoom = new ChatRoomForTeam();
            newRoom.setAuthor(author);
            newRoom.setParticipant(participant);
            newRoom.setTeamBoard(teamBoard.orElse(null));
            newRoom.setCreatedAt(LocalDateTime.now());
            return newRoom;

        }
    }
}
