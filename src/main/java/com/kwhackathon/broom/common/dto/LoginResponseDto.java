package com.kwhackathon.broom.common.dto;

import com.kwhackathon.broom.user.util.MilitaryChaplain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    private String nickname;
    private MilitaryChaplain militaryChaplain;
}
