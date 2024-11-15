package com.kwhackathon.broom.bus.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kwhackathon.broom.bus.dto.request.CreateReservationDto;
import com.kwhackathon.broom.bus.dto.response.ReservationBoolean;
import com.kwhackathon.broom.bus.dto.response.ReservationInfoElement;
import com.kwhackathon.broom.bus.dto.response.Reservations;
import com.kwhackathon.broom.bus.entity.BusReservation;
import com.kwhackathon.broom.bus.repository.BusReservationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BusReservationService {
    private final BusReservationRepository busReservationRepository;

    public void createReservation(CreateReservationDto dto) {
        busReservationRepository.save(BusReservation
                .builder()
                .name(dto.getName())
                .studentId(dto.getStudentId())
                .phoneNumber(dto.getPhoneNumber()).build());
    }

    public ReservationBoolean isReserved(String studentId) {
        return new ReservationBoolean(busReservationRepository.existsByStudentId(studentId));
    }

    public Reservations getAllReservationInfo() {
        List<ReservationInfoElement> elements = new ArrayList<>();
        elements = busReservationRepository.findAll().stream()
                .map(reservation -> new ReservationInfoElement(
                        reservation.getBusReservationId(),
                        reservation.getName(),
                        reservation.getStudentId(),
                        reservation.getPhoneNumber()))
                .collect(Collectors.toList());
        return new Reservations(elements);
    }
}
