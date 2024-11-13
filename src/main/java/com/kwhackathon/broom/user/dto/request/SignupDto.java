package com.kwhackathon.broom.user.dto.request;

import com.kwhackathon.broom.user.util.MilitaryChaplain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignupDto {
    private String userId;
    private String password;
    private String nickname;
    private int dischargeYear;
    private MilitaryChaplain militaryChaplain;
}
