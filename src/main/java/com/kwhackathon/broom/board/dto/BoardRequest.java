package com.kwhackathon.broom.board.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.kwhackathon.broom.board.entity.Board;
import com.kwhackathon.broom.board.util.category.Category;
import com.kwhackathon.broom.user.entity.User;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BoardRequest {
    @Getter
    @NoArgsConstructor
    public static class WriteBoardDto {
        @NotBlank(message = "제목을 입력해주세요.")
        private String title;
        private String content;
        @NotBlank(message = "장소를 입력해주세요.")
        private String place;
        private LocalTime time;
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
