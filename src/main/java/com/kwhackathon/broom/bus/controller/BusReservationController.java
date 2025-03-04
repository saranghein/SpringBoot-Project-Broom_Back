package com.kwhackathon.broom.bus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kwhackathon.broom.bus.dto.BusRequest.CreateReservationDto;
import com.kwhackathon.broom.bus.service.BusReservationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BusReservationController implements BusReservationOperators{
    private final BusReservationService busReservationService;

    @Override
    @PostMapping("/bus/reservation")
    public ResponseEntity<?> createReservation(@RequestBody CreateReservationDto createReservationDto) {
        try {
            busReservationService.createReservation(createReservationDto);
        } catch (Exception e) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("신청이 완료되었습니다");
    }

    @Override
    @GetMapping("/bus/reservation/{studentId}")
    public ResponseEntity<?> getIsReserved(@PathVariable("studentId") String studentId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(busReservationService.isReserved(studentId));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Override
    @GetMapping("/admin/bus/reservation")
    public ResponseEntity<?> getAllReservationInfo() {
        return ResponseEntity.status(HttpStatus.OK).body(busReservationService.getAllReservationInfo());
    }

    @Override
    @GetMapping("/admin/bus/reservation/count")
    public ResponseEntity<?> countAllReservation() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(busReservationService.countReservation());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
