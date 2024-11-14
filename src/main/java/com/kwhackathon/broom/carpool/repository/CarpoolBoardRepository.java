package com.kwhackathon.broom.carpool.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kwhackathon.broom.carpool.entity.CarpoolBoard;
import java.util.List;
import java.time.LocalDate;



public interface CarpoolBoardRepository extends JpaRepository<CarpoolBoard, Long> {
    List<CarpoolBoard> findByTitleContaining(String title);

    List<CarpoolBoard> findByTrainingDate(LocalDate trainingDate);

    List<CarpoolBoard> findByDepartPlaceContaining(String departPlace);
}
