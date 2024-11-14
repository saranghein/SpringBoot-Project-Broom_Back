package com.kwhackathon.broom.carpool.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CarpoolBoardListElement {
    private Long carpoolBoardId;
    private String title;
    private String createdAt;
    private String trainingDate;
    private String departPlace;
    private String departTime;
    private boolean isFull;
}
