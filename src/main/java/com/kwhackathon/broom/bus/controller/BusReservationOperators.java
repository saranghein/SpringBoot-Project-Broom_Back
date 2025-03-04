package com.kwhackathon.broom.bus.controller;

import org.springframework.http.ResponseEntity;

import com.kwhackathon.broom.bus.dto.BusRequest.CreateReservationDto;

public interface BusReservationOperators {
    ResponseEntity<?> createReservation(CreateReservationDto createReservationDto);
    
    ResponseEntity<?> getIsReserved(String studentId);
    
    ResponseEntity<?> getAllReservationInfo();

    ResponseEntity<?> countAllReservation();
}
