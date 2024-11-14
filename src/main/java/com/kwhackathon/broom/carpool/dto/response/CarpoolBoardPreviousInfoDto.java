package com.kwhackathon.broom.carpool.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CarpoolBoardPreviousInfoDto {
    private Long carpoolBoardId;
    private String title;
    private String content;
    private LocalDate trainingDate;
    private String departPlace;
    private String departTime;
    private int personnel;
    private int price;
}
