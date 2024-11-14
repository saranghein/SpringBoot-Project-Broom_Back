package com.kwhackathon.broom.team.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SingleTeamBoardDto {
    private Long teamBoardId;
    private Author author;
    private String title;
    private String createdAt;
    private String trainingDate;
    private String meetingPlace;
    private String meetingTime;
    private int personnel;
    private String content;
    private boolean isFull;
}
