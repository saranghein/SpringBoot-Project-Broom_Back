package com.kwhackathon.broom.bus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.kwhackathon.broom.bus.dto.request.CreateReservationDto;
import com.kwhackathon.broom.bus.service.BusReservationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BusReservationController implements BusReservationOperators{
    private final BusReservationService busReservationService;

    @Override
    public ResponseEntity<?> createReservation(CreateReservationDto createReservationDto) {
        busReservationService.createReservation(createReservationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("신청이 완료되었습니다");
    }

    @Override
    public ResponseEntity<?> getIsReserved(String studentId) {
        return ResponseEntity.status(HttpStatus.OK).body(busReservationService.isReserved(studentId));
    }

    @Override
    public ResponseEntity<?> getAllReservationInfo() {
        return ResponseEntity.status(HttpStatus.OK).body(busReservationService.getAllReservationInfo());
    }
}
