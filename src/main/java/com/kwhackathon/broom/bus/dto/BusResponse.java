package com.kwhackathon.broom.bus.dto;

import java.util.List;

import com.kwhackathon.broom.bus.entity.BusReservation;
import com.kwhackathon.broom.bus.entity.BusReservationActivate;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class BusResponse {
    @Getter
    @AllArgsConstructor
    public static class ReservationInfoElement {
        private Long reservationId;
        private String name;
        private String studentId;
        private String phoneNumber;

        public ReservationInfoElement(BusReservation busReservation) {
            this.reservationId = busReservation.getBusReservationId();
            this.name = busReservation.getName();
            this.studentId = busReservation.getStudentId();
            this.phoneNumber = busReservation.getPhoneNumber();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class IsReservedDto {
        private boolean isReserved;
    }

    @Getter
    @AllArgsConstructor
    public static class ReservationInfoDto {
        private List<ReservationInfoElement> result;
    }

    @Getter
    @AllArgsConstructor
    public static class ReservationCount {
        private Long reservationCount;
    }

    @Getter
    @AllArgsConstructor
    public static class ActivationDto {
        private boolean isActivated;

        public ActivationDto(BusReservationActivate busReservationActivate) {
            this.isActivated = busReservationActivate.isActivated();
        }
    }
}
