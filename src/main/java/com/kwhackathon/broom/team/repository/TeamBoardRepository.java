package com.kwhackathon.broom.team.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kwhackathon.broom.team.entity.TeamBoard;

public interface TeamBoardRepository extends JpaRepository<TeamBoard, Long> {
    List<TeamBoard> findByTitleContaining(String title);

    List<TeamBoard> findByTrainingDate(LocalDate trainingDate);

    List<TeamBoard> findByIsFull(boolean full);
}
