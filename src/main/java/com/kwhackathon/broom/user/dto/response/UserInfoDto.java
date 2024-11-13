package com.kwhackathon.broom.user.dto.response;

import com.kwhackathon.broom.user.util.MilitaryChaplain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoDto {
    private String nickname;
    private int dischargeYear;
    private MilitaryChaplain militaryChaplain;
}
