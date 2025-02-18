package com.kwhackathon.broom.board.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.kwhackathon.broom.board.entity.Board;
import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.util.MilitaryBranch;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class BoardResponse {
    @Getter
    public static class BoardId {
        private String boardId;

        public BoardId(String boardId) {
            this.boardId = boardId;
        }
    }

    @Getter
    private static class Author {
        private String nickname;
        private int reserveYear;
        private MilitaryBranch militaryBranch;

        public Author(User user) {
            this.nickname = user.getNickname();
            this.reserveYear = LocalDateTime.now().getYear() - user.getDischargeYear();
            this.militaryBranch = user.getMilitaryBranch();
        }
    }

    @Getter
    private static class Content {
        private String title;
        private LocalDate trainingDate;
        private String place;
        private String time;

        public Content(Board board) {
            this.title = board.getTitle();
            this.trainingDate = board.getTrainingDate();
            this.place = board.getPlace();
            this.time = board.getTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        }
    }

    @Getter
    private static class ContentDetail extends Content {
        private String content;
        private int personnel;

        public ContentDetail(Board board) {
            super(board);
            this.content = board.getContent();
            this.personnel = board.getPersonnel();
        }
    }

    @Getter
    private static class Status {
        private String boardId;
        private String createdAt;
        private Long currentPersonnel;
        private int totalPersonnel;
        private boolean isBookmark;

        public Status(Board board, Long currentPersonnel, boolean isBookmark) {
            this.boardId = board.getBoardId();
            this.createdAt = board.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm"));
            this.currentPersonnel = currentPersonnel;
            this.totalPersonnel = board.getPersonnel();
            this.isBookmark = isBookmark;
        }

        public Status(String boardId, String createdAt, Long currentPersonnel, int totalPersonnel, boolean isBookmark) {
            this.boardId = boardId;
            this.createdAt = createdAt;
            this.currentPersonnel = currentPersonnel;
            this.totalPersonnel = totalPersonnel;
            this.isBookmark = isBookmark;
        }
    }

    @Getter
    public static class BoardListElement {
        private Status status;

        private Content content;

        public BoardListElement(Board board, Long participantCount, boolean isBookmark) {
            this.status = new Status(board.getBoardId(), formattingCreatedAt(board.getCreatedAt()),
                    participantCount, board.getPersonnel(),
                    isBookmark);
            this.content = new Content(board);
        }

        private String formattingCreatedAt(LocalDateTime createdAt) {
            // 게시글 생성 일자가 오늘인 경우 작성 시간만 반환
            if (createdAt.toLocalDate().compareTo(LocalDate.now()) == 0) {
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

    @Getter
    public static class BoardList {
        private List<BoardListElement> result;
        private boolean hasNext;

        public BoardList(List<BoardListElement> boardListElement, boolean hasNext) {
            this.result = boardListElement;
            this.hasNext = hasNext;
        }
    }

    @Getter
    public static class SingleBoardDetail {
        private Author author;

        private Status status;

        private ContentDetail contentDetail;

        public SingleBoardDetail(BoardWithBookmarkDto dto) {
            this.author = new Author(dto.getBoard().getUser());
            this.status = new Status(dto.getBoard(), dto.getParticipantCount(), dto.isBookmarked());
            this.contentDetail = new ContentDetail(dto.getBoard());
        }
    }

    @Getter
    @AllArgsConstructor
    public static class BoardWithBookmarkDto {
        private Board board;
        private Long participantCount;
        private boolean bookmarked;
    }
}
