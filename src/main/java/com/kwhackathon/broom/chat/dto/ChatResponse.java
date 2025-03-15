package com.kwhackathon.broom.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kwhackathon.broom.chat.formatter.LocalDateFormatter;
import com.kwhackathon.broom.chat.entity.Chat;
import com.kwhackathon.broom.participant.entity.Participant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class ChatResponse {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        private String boardId;
        private String message;          // 메시지 내용
        private String senderNickname;   // 보낸 사람 이름
        private String createdAt;        // 메시지 작성 시간
        private String militaryBranch;   // 군 type
        private int dischargeYear;       // 몇 년차
        @JsonProperty("expelled") // JSON의 "expelled" 필드를 isExpelled와 매핑
        private boolean isExpelled;      // 강퇴 여부

        // Chat -> Response.Message
        public static Message fromEntity(Chat chat, String boardId) {
            return new Message(
                    boardId,
                    chat.getMessage(),
                    (chat.getParticipant() != null && chat.getParticipant().getUser() != null)
                            ? chat.getParticipant().getUser().getNickname()
                            : "알 수 없음", // Participant가 삭제된 경우
                    LocalDateFormatter.CreatedAt.formattingCreatedAt(chat.getCreatedAt()),
                    (chat.getParticipant() != null && chat.getParticipant().getUser() != null)
                            ? chat.getParticipant().getUser().getMilitaryBranch().toString()
                            : "알 수 없음",
                    (chat.getParticipant() != null && chat.getParticipant().getUser() != null)
                            ? LocalDate.now().getYear() - chat.getParticipant().getUser().getDischargeYear()
                            : 0, // 기본값
                    (chat.getParticipant() != null) ? chat.getParticipant().getIsExpelled() : false
            );
        }

    }

    @Getter
    @AllArgsConstructor
    public static class ChatRoomResponse {
        private String boardTitle;
        private String ownerNickname;
        private List<String> militaryBranches;
        private List<MessageInfo> messages;
        private boolean hasNext;

        // Entities -> ChatRoomResponse
        public static ChatRoomResponse fromEntities(String boardTitle,
                                                    String ownerNickname,
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

    @Getter
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
        public static MessageInfo fromEntity(Chat chat) {
            return new MessageInfo(
                    chat.getId(),
                    chat.getMessage(),
                    (chat.getParticipant() != null && chat.getParticipant().getUser() != null)
                            ? chat.getParticipant().getUser().getNickname()
                            : "알 수 없음", // Participant가 삭제된 경우
                    LocalDateFormatter.CreatedAt.formattingCreatedAt(chat.getCreatedAt()),
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
    }

}
