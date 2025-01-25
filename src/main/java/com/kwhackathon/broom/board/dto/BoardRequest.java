package com.kwhackathon.broom.board.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kwhackathon.broom.board.entity.Board;
import com.kwhackathon.broom.board.util.category.Category;
import com.kwhackathon.broom.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class BoardRequest {
    @Getter
    @NoArgsConstructor
    public static class WriteBoardDto {
        private String title;
        private String content;
        private String place;
        private LocalDateTime time;
        private int personnel;
        private LocalDate trainingDate;
        private Category category;

        public Board toEntity(User user) {
            return Board.builder()
                    .title(this.title)
                    .content(this.content)
                    .place(this.place)
                    .time(this.time)
                    .personnel(this.personnel)
                    .trainingDate(this.trainingDate)
                    .category(this.category)
                    .user(user)
                    .build();
        }
    }
}
