package com.kwhackathon.broom.participant.dto;

import com.kwhackathon.broom.chat.formatter.LocalDateFormatter;
import com.kwhackathon.broom.participant.entity.Participant;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ParticipantResponse {
    @Getter
    @AllArgsConstructor
    public static class ParticipantElement {
        private String userId;
        private String userNickname;
        private String militaryBranch;

        public static ParticipantElement fromEntity(Participant participant) {
            return new ParticipantElement(
                    participant.getUser().getUserId(),
                    participant.getUser().getNickname(),
                    participant.getUser().getMilitaryBranch().toString()
            );

        }
    }

    @Getter
    @AllArgsConstructor
    public static class ParticipantList {
        private String boardTitle;
        private LocalDate trainingDate;
        private ParticipantElement author;
        private List<ParticipantElement> participants;

        public static ParticipantList fromEntities(String boardTitle,
                                                   Participant author,
                                                   List<Participant> participants,
                                                   LocalDate trainingDate) {

            return new ParticipantList(
                    boardTitle,
                    trainingDate,
                    ParticipantElement.fromEntity(author),
                    participants.stream()
                    .map(ParticipantElement::fromEntity) // fromEntity 메서드 사용
                    .toList()
            );
        }
    }
    @Getter
    @AllArgsConstructor
    public static class ChatRoomElement {
        private String boardId;
        private String boardName;
        private String lastMessage;
        private boolean isExpelled;

        private String lastMessageTime;

        private List<String> militaryBranches;

        // Participant , Chat -> ChatRoomElement
        public static ChatRoomElement fromEntity(Participant participant, String lastChatMessage, LocalDateTime lastMessageTime) {
            List<Participant> participants = participant.getBoard().getParticipants();
            System.out.println(participants);
            return new ChatRoomElement(
                    participant.getBoard().getBoardId(), // Board ID (채팅방 ID)
                    participant.getBoard().getTitle(), // 채팅방 이름 (게시판 제목)
                    lastChatMessage, // 최신 메시지
                    participant.getIsExpelled(), // 강퇴 여부
                    LocalDateFormatter.CreatedAt.formattingCreatedAt(lastMessageTime), // 최신 메시지 시간
                    participants.stream()
                            .filter(p -> !p.getIsExpelled()) // 강퇴되지 않은 사용자의 military branch만 조회
                            .map(p -> p.getUser().getMilitaryBranch().toString()).toList()
            );
        }

    }

    @Getter
    @AllArgsConstructor
    public static class ChatRoomList {
        private List<ChatRoomElement> chatRooms;
        private boolean hasNext;



    }
}
