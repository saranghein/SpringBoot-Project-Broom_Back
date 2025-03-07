package com.kwhackathon.broom.bus.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kwhackathon.broom.bus.dto.BusRequest.CreateReservationDto;
import com.kwhackathon.broom.bus.dto.BusResponse.ActivationDto;
import com.kwhackathon.broom.bus.dto.BusResponse.IsReservedDto;
import com.kwhackathon.broom.bus.dto.BusResponse.ReservationCount;
import com.kwhackathon.broom.bus.dto.BusResponse.ReservationInfoDto;
import com.kwhackathon.broom.bus.dto.BusResponse.ReservationInfoElement;
import com.kwhackathon.broom.bus.entity.BusReservationActivate;
import com.kwhackathon.broom.bus.repository.BusReservationActivateRepository;
import com.kwhackathon.broom.bus.repository.BusReservationRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BusReservationService {
    private final BusReservationRepository busReservationRepository;
    private final BusReservationActivateRepository busReservationActivateRepository;

    @Transactional
    public void createReservation(CreateReservationDto dto) {
        busReservationRepository.save(dto.toEntity());
    }

    public IsReservedDto isReserved(String studentId) {
        if (!busReservationRepository.existsByStudentId(studentId)) {
            throw new NullPointerException("예약정보가 존재하지 않습니다.");
        }
        return new IsReservedDto(true);
    }

    public ReservationInfoDto getAllReservationInfo() {
        List<ReservationInfoElement> elements = new ArrayList<>();
        elements = busReservationRepository.findAll().stream()
                .map(reservation -> new ReservationInfoElement(reservation))
                .collect(Collectors.toList());
        return new ReservationInfoDto(elements);
    }

    public ReservationCount countReservation() {
        return new ReservationCount(busReservationRepository.countTotalReservation());
    }

    public ActivationDto getActivationStatus() {
        BusReservationActivate busReservationActivate = busReservationActivateRepository.findById(1L)
                .orElseThrow(() -> new NullPointerException("정보를 불러오지 못했습니다."));
        return new ActivationDto(busReservationActivate);
    }
    
    @Transactional
    public ActivationDto updateActivationStatus() {
        BusReservationActivate busReservationActivate = busReservationActivateRepository.findById(1L)
                .orElseThrow(() -> new NullPointerException("정보를 불러오지 못했습니다."));
        busReservationActivate.updateActivatedStatus();
        return new ActivationDto(busReservationActivate);
    }
}
