package com.kwhackathon.broom.bus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationInfoElement {
    private Long busReservationId;
    private String name;
    private String studentId;
    private String phoneNumber;
}
