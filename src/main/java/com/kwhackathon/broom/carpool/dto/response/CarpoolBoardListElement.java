package com.kwhackathon.broom.carpool.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CarpoolBoardListElement {
    private Long carpoolBoardId;
    private String title;
    private String createdAt;
    private LocalDate trainingDate;
    private String departPlace;
    private LocalTime departTime;
    private boolean isFull;
}
