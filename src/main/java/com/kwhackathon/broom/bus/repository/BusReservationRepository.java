package com.kwhackathon.broom.bus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kwhackathon.broom.bus.entity.BusReservation;

public interface BusReservationRepository  extends JpaRepository<BusReservation, Long> {
    Optional<BusReservation> findByStudentId(String studentId);

    List<BusReservation> findAllByStudentId(String studentId);

    boolean existsByStudentId(String studentId);

    @Query("SELECT COUNT(r) FROM BusReservation r")
    Long countTotalReservation();
}
