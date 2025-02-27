package com.kwhackathon.broom.date_tag.dto;

import java.time.LocalDate;

import com.kwhackathon.broom.date_tag.entity.DateTag;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class DateTagRequest {
    @Getter
    @NoArgsConstructor
    public static class TrainingDateDto {
        private LocalDate trainingDate;


        public DateTag toEntity(){
            return DateTag.builder()
                    .trainingDate(this.trainingDate)
                    .build();
        }
    }

}
