package com.kwhackathon.broom.user.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdatePasswordDto {
    private String password;
    private String newPassword;
}
