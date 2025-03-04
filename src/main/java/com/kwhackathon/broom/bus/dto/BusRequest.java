package com.kwhackathon.broom.bus.dto;

import com.kwhackathon.broom.bus.entity.BusReservation;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class BusRequest {
    @Getter
    @NoArgsConstructor
    public static class CreateReservationDto {
        private String name;
        private String studentId;
        private String phoneNumber;

        public BusReservation toEntity() {
            return BusReservation.builder()
                    .name(this.name)
                    .studentId(this.studentId)
                    .phoneNumber(this.phoneNumber)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ConfirmReservationDto {
        private String studentId;
    }
}
