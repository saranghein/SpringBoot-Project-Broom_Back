package com.kwhackathon.broom.user.dto;

import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.util.MilitaryBranch;

import lombok.AllArgsConstructor;
import lombok.Data;

public class UserResponse {
    @Data
    @AllArgsConstructor
    public static class MypageInfoDto {
        private String nickname;
        private int reserveYear;
        private MilitaryBranch militaryBranch;
    }

    @Data
    @AllArgsConstructor
    public static class TokenDto {
        private String accessToken;
        private String refreshToken;
    }

    @Data
    public static class UserInfoDto {
        private String nickname;
        private int dischargeYear;
        private MilitaryBranch militaryBranch;

        public UserInfoDto(User user) {
            this.nickname = user.getNickname();
            this.dischargeYear = user.getDischargeYear();
            this.militaryBranch = user.getMilitaryBranch();
        }
    }
}
