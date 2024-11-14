package com.kwhackathon.broom.team.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WriteTeamBoardDto {
    private String title;
    private String content;
    private LocalDate trainingDate;
    private String meetingPlace;
    private LocalTime meetingTime;
    private int personnel;
}
