package com.kwhackathon.broom.team.dto.response;

import com.kwhackathon.broom.user.util.MilitaryChaplain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Author {
    private String userId;
    private String nickname;
    private int dischargeYear;
    private MilitaryChaplain militaryChaplain;
}
