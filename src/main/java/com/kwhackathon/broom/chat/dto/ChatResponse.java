package com.kwhackathon.broom.chat.dto;

import com.kwhackathon.broom.chat.entity.Chat;
import com.kwhackathon.broom.participant.entity.Participant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ChatResponse {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        private String boardId;
        private String message;      // 메시지 내용
        private String senderNickname;   // 보낸 사람 이름
        private String createdAt; // 메시지 작성 시간
        private String militaryBranch;   // 군 type
        private int dischargeYear;    // 몇 년차
        private boolean isExpelled;     // 강퇴 여부

        // Chat -> Response.Message
        public static Message fromEntity(Chat chat,String boardId){
            return new Message(
                    boardId,
                    chat.getMessage(),
                    (chat.getParticipant() != null && chat.getParticipant().getUser() != null)
                            ? chat.getParticipant().getUser().getNickname()
                            : "알 수 없음", // Participant가 삭제된 경우
                    formattingCreatedAt(chat.getCreatedAt()),
                    (chat.getParticipant() != null && chat.getParticipant().getUser() != null)
                            ? chat.getParticipant().getUser().getMilitaryBranch().toString()
                            : "알 수 없음",
                    (chat.getParticipant() != null && chat.getParticipant().getUser() != null)
                            ? LocalDate.now().getYear() - chat.getParticipant().getUser().getDischargeYear()
                            : 0, // 기본값
                    (chat.getParticipant() != null) ? chat.getParticipant().getIsExpelled() : false
            );
        }

        // Request.Message -> Response.Message
        public static Message fromEntity(Message message, String nickname){
            return new Message(
                    message.getBoardId(),
                    message.getMessage(), // 메시지 내용
                    nickname, // 보낸 사람 닉네임
                    message.getCreatedAt() ,// 생성시간
                    message.getMilitaryBranch(),
                    message.getDischargeYear(),
                    message.isExpelled()
            );
        }


        private static String formattingCreatedAt(LocalDateTime createdAt) {
            if(createdAt==null){
                return "";
            }
            // 게시글 생성 일자가 오늘인 경우 작성 시간만 반환
            if (createdAt.toLocalDate().isEqual(LocalDate.now())) {
                return createdAt.format(DateTimeFormatter.ofPattern("HH:mm"));
            }

            // 게시글 생성 일자가 올해인 경우 월/일 행태로 반환
            if (createdAt.getYear() == LocalDate.now().getYear()) {
                return createdAt.format(DateTimeFormatter.ofPattern("MM/dd"));
            }

            // 위의 두 조건 모두 해당되지 않으면 년/월/일 형태로 반환
            return createdAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        }

    }

    @Data
    @AllArgsConstructor
    public static class ParticipantInfo {
        private String userId;
        private String nickname;
        private String militaryBranch;
        private int dischargeYear;

        // Participant -> ParticipantInfo
        public static ParticipantInfo fromEntity(Participant participant) {
            return new ParticipantInfo(
                    participant.getUser().getUserId(),
                    participant.getUser().getNickname(),
                    participant.getUser().getMilitaryBranch().toString(),
                    LocalDateTime.now().getYear()-participant.getUser().getDischargeYear()
            );
        }

        // List<Participant> -> List<ParticipantInfo>
        public static List<ParticipantInfo> fromEntityList(List<Participant> participants) {
            return participants.stream()
                    .map(ParticipantInfo::fromEntity)
                    .collect(Collectors.toList());
        }

    }

    @Data
    @AllArgsConstructor
    public static class ChatRoomResponse {
        private String boardTitle;
        private String ownerNickname;
        private List<String> militaryBranches;
        private List<MessageInfo> messages;
        private boolean hasNext;

        // Entities -> ChatRoomResponse
        public static ChatRoomResponse fromEntities(String boardId, String boardTitle,
                                                    String ownerNickname,String ownerMilitaryBranch,
                                                    List<Participant> participants, Page<Chat> chatPage) {


            return new ChatRoomResponse(
                    boardTitle,
                    ownerNickname,
                    participants.stream()
                            .map(p -> p.getUser() != null ? p.getUser().getMilitaryBranch().toString() : "알 수 없음")
                            .toList(),
                    MessageInfo.fromEntityList(chatPage.getContent()),
                    chatPage.hasNext() // 다음 페이지 존재 여부
            );
        }

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MessageInfo {
        private Long messageId;      // 메시지 ID
        private String message;      // 메시지 내용
        private String senderNickname;   // 보낸 사람 이름
        private String createdAt; // 메시지 작성 시간
        private String militaryBranch;   // 군 type
        private int dischargeYear; // 몇 년차

        // Chat -> Response.Message
        public static MessageInfo fromEntity(Chat chat){
            return new MessageInfo(
                    chat.getId(),
                    chat.getMessage(),
                    (chat.getParticipant() != null && chat.getParticipant().getUser() != null)
                            ? chat.getParticipant().getUser().getNickname()
                            : "알 수 없음", // Participant가 삭제된 경우
                    formattingCreatedAt(chat.getCreatedAt()),
                    (chat.getParticipant() != null && chat.getParticipant().getUser() != null)
                            ? chat.getParticipant().getUser().getMilitaryBranch().toString()
                            : "알 수 없음",
                    (chat.getParticipant() != null && chat.getParticipant().getUser() != null)
                            ? LocalDate.now().getYear() - chat.getParticipant().getUser().getDischargeYear()
                            : 0 // 기본값
            );
        }



        public static List<MessageInfo> fromEntityList(List<Chat> chats) {
            // Collections.emptyList()로 불필요한 객체 생성 방지
            // .parallelStream()로 멀티코어 활용
            return chats.isEmpty() ? Collections.emptyList() : chats.parallelStream()
                    .map(MessageInfo::fromEntity)
                    .toList();
        }

        private static String formattingCreatedAt(LocalDateTime createdAt) {
            if(createdAt==null){
                return "";
            }
            // 게시글 생성 일자가 오늘인 경우 작성 시간만 반환
            if (createdAt.toLocalDate().isEqual(LocalDate.now())) {
                return createdAt.format(DateTimeFormatter.ofPattern("HH:mm"));
            }

            // 게시글 생성 일자가 올해인 경우 월/일 행태로 반환
            if (createdAt.getYear() == LocalDate.now().getYear()) {
                return createdAt.format(DateTimeFormatter.ofPattern("MM/dd"));
            }

            // 위의 두 조건 모두 해당되지 않으면 년/월/일 형태로 반환
            return createdAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        }

    }


}
