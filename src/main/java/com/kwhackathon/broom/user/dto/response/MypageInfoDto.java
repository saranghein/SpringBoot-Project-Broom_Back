package com.kwhackathon.broom.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MypageInfoDto {
    private String nickname;
    private int dischargeYear;
}
