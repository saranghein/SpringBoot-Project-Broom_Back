package com.kwhackathon.broom.carpool.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Author {
    private String userId;
    private String nickname;
    private int dischargeYear;
}
