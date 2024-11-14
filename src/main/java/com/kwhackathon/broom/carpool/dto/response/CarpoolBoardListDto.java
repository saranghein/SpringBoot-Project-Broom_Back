package com.kwhackathon.broom.carpool.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CarpoolBoardListDto {
    List<CarpoolBoardListElement> result;
}
