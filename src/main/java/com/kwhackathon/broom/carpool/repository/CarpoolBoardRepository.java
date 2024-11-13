package com.kwhackathon.broom.carpool.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kwhackathon.broom.carpool.entity.CarpoolBoard;

public interface CarpoolBoardRepository extends JpaRepository<CarpoolBoard, Long> {
    
}
