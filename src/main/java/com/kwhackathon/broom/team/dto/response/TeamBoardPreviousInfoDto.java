package com.kwhackathon.broom.team.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamBoardPreviousInfoDto {
    private Long teamBoardId;
    private String title;
    private String content;
    private LocalDate trainingDate;
    private String meetingPlace;
    private String meetingTime;
    private int personnel;
}
