package com.kwhackathon.broom.carpool.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WriteCarpoolBoardDto {
    private String title;
    private String content;
    private LocalDate trainingDate;
    private String departPlace;
    private LocalTime departTime;
    private int personnel;
    private int price;
}
