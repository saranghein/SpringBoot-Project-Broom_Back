package com.kwhackathon.broom.carpool.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;

import com.kwhackathon.broom.carpool.dto.request.IsFullCheckDto;
import com.kwhackathon.broom.carpool.dto.request.WriteCarpoolBoardDto;
import com.kwhackathon.broom.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CarpoolBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carpool_board_id", nullable = false)
    private Long carpoolBoardId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "depart_time", nullable = false)
    private LocalTime departTime;

    @Column(name = "depart_place", nullable = false)
    private String departPlace;

    @Column(name = "personnel", nullable = false)
    private int personnel;

    @Column(name = "is_full", nullable = false)
    private boolean isFull;

    @Column(name = "training_date", nullable = false)
    private LocalDate trainingDate;

    @Column(name = "price", nullable = false)
    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void updateCarpoolBoard(WriteCarpoolBoardDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.trainingDate = dto.getTrainingDate();
        this.departPlace = dto.getDepartPlace();
        this.departTime = dto.getDepartTime();
        this.personnel = dto.getPersonnel();
        this.price = dto.getPrice();
    }

    public void updateIsFull(IsFullCheckDto dto) {
        this.isFull = dto.isFull();
    }
}
