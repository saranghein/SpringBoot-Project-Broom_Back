package com.kwhackathon.broom.carpool.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SingleCarpoolBoardDto {
    private Long carpoolBoardId;
    private Author author;
    private String title;
    private String createdAt;
    private LocalDate trainingDate;
    private String departPlace;
    private LocalTime departTime;
    private int personnel;
    private int price;
    private String content;
    private boolean isFull;
}
