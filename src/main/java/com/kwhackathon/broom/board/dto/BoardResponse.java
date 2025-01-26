package com.kwhackathon.broom.board.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.kwhackathon.broom.board.entity.Board;
import com.kwhackathon.broom.board.util.category.Category;
import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.util.MilitaryChaplain;

import lombok.Getter;

public class BoardResponse {
    @Getter
    public static class BoardListElement {
        private String boardId;
        private String title;
        private String createdAt;
        private LocalDate trainingDate;
        private String place;
        private LocalTime time;
        private boolean isFull;

        public BoardListElement(Board board) {
            this.boardId = board.getId();
            this.title = board.getTitle();
            this.createdAt = formattingCreatedAt(board.getCreatedAt());
            this.trainingDate = board.getTrainingDate();
            this.place = board.getPlace();
            this.time = board.getTime();
            this.isFull = board.isFull();
        }

        // DTO에 있어야 할 내용은 아닌거 같아서 옮겨야 할듯
        private String formattingCreatedAt(LocalDateTime createdAt) {
            // 게시글 생성 일자가 오늘인 경우 작성 시간만 반환
            if (createdAt.toLocalDate().compareTo(LocalDate.now()) == 0) {
                return createdAt.getHour() + ":" + createdAt.getMinute();
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
    public static class SingleBoardDetail {
        private String boardId;
        private String title;
        private String content;
        private String place;
        private LocalTime time;
        private LocalDateTime createdAt;
        private int personnel;
        private boolean isFull;
        private LocalDate trainingDate;
        private Category category;
        private String WriterNickname;
        private int writerDischargeYear;
        private MilitaryChaplain writerMilitaryChaplain;

        public SingleBoardDetail(Board board, User user) {
            this.boardId = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.place = board.getPlace();
            this.time = board.getTime();
            this.createdAt = board.getCreatedAt();
            this.personnel = board.getPersonnel();
            this.isFull = board.isFull();
            this.trainingDate = board.getTrainingDate();
            this.category = board.getCategory();

            // 작성자 정보
            this.WriterNickname = user.getNickname();
            this.writerDischargeYear = user.getDischargeYear();
            this.writerMilitaryChaplain = user.getMilitaryChaplain();
        }
    }
}
