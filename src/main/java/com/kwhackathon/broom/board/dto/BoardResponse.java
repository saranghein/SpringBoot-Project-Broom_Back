package com.kwhackathon.broom.board.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        private LocalDateTime createdAt;
        private LocalDate trainingDate;
        private String place;
        private LocalDateTime time;
        private boolean isFull;

        public BoardListElement(Board board) {
            this.boardId = board.getId();
            this.title = board.getTitle();
            this.createdAt = board.getCreatedAt();
            this.trainingDate = board.getTrainingDate();
            this.place = board.getPlace();
            this.time = board.getTime();
            this.isFull = board.isFull();
        }
    }

    @Getter
    public static class SingleBoardDetail {
        private String boardId;
        private String title;
        private String content;
        private String place;
        private LocalDateTime time;
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
