package com.kwhackathon.broom.team.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamBoardListElement {
    private Long teamBoardId;
    private String title;
    private String createdAt;
    private String trainingDate;
    private String meetingPlace;
    private String meetingTime;
    private boolean isFull;
}
