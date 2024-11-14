package com.kwhackathon.broom.bus.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kwhackathon.broom.bus.dto.request.CreateReservationDto;

public interface BusReservationOperators {
    @PostMapping("/bus/reservation")
    ResponseEntity<?> createReservation(@RequestBody CreateReservationDto createReservationDto);

    // @GetMapping("/bus/reservation/{studentId}")
    // ResponseEntity<?> getReservationInfo(@PathVariable String studentId);

    @GetMapping("/bus/reservation/{studentId}")
    ResponseEntity<?> getIsReserved(@PathVariable String studentId);

    @GetMapping("bus/reservation")
    ResponseEntity<?> getAllReservationInfo();
}
