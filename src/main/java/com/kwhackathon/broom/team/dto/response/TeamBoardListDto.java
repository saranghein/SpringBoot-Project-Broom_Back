package com.kwhackathon.broom.team.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamBoardListDto {
    List<TeamBoardListElement> result;
}
