package com.kwhackathon.broom.team.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kwhackathon.broom.team.entity.TeamBoard;

public interface TeamBoardRepository extends JpaRepository<TeamBoard, Long> {
    @Query("SELECT e FROM TeamBoard e ORDER BY e.createdAt DESC")
    List<TeamBoard> findAllOrderByCreatedAtDesc();

    List<TeamBoard> findByTitleContainingOrderByCreatedAtDesc(String title);

    List<TeamBoard> findByTrainingDateOrderByCreatedAtDesc(LocalDate trainingDate);

    List<TeamBoard> findByIsFullOrderByCreatedAtDesc(boolean full);
}
