package com.kwhackathon.broom.carpool.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SingleCarpoolBoardDto {
    private Long carpoolBoardId;
    private Author author;
    private String title;
    private String createdAt;
    private String trainingDate;
    private String departPlace;
    private String departTime;
    private int personnel;
    private int price;
    private String content;
    private boolean isFull;
}
