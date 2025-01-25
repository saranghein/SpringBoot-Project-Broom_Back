package com.kwhackathon.broom.board.entity;

import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.kwhackathon.broom.board.dto.BoardRequest.WriteBoardDto;
import com.kwhackathon.broom.board.util.category.Category;
import com.kwhackathon.broom.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "board")
public class Board {
    @Id
    @Builder.Default
    @Column(name = "board_id", nullable = false, unique = true)
    private String id = UUID.randomUUID().toString();

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "place", nullable = false)
    private String place;

    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "personnel", nullable = false)
    private int personnel;

    @Column(name = "is_full", nullable = false)
    @ColumnDefault("false")
    private boolean isFull;

    @Column(name = "training_date", nullable = false)
    private LocalDate trainingDate;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void updateBoard(WriteBoardDto writeBoardDto) {
        this.title = writeBoardDto.getTitle();
        this.content = writeBoardDto.getContent();
        this.place = writeBoardDto.getPlace();
        this.time = writeBoardDto.getTime();
        this.personnel = writeBoardDto.getPersonnel();
        this.trainingDate = writeBoardDto.getTrainingDate();
    }

    public void changeIsFullStatus() {
        this.isFull = !this.isFull;
    }
}
