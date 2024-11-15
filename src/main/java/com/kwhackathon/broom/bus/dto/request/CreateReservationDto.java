package com.kwhackathon.broom.bus.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateReservationDto {
    private String name;
    private String studentId;
    private String phoneNumber;
}
