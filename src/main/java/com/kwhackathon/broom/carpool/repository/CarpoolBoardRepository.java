package com.kwhackathon.broom.carpool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kwhackathon.broom.carpool.entity.CarpoolBoard;

import java.util.List;
import java.time.LocalDate;



public interface CarpoolBoardRepository extends JpaRepository<CarpoolBoard, Long> {
    @Query("SELECT e FROM CarpoolBoard e ORDER BY e.createdAt DESC")
    List<CarpoolBoard> findAllOrderByCreatedAtDesc();
    
    List<CarpoolBoard> findByTitleContainingOrderByCreatedAtDesc(String title);

    List<CarpoolBoard> findByTrainingDateOrderByCreatedAtDesc(LocalDate trainingDate);

    List<CarpoolBoard> findByDepartPlaceContainingOrderByCreatedAtDesc(String departPlace);

    List<CarpoolBoard> findByIsFullOrderByCreatedAtDesc(boolean full);
}
