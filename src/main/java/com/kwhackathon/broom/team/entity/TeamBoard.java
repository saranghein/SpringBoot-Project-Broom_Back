package com.kwhackathon.broom.team.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;

import com.kwhackathon.broom.team.dto.request.IsFullCheckDto;
import com.kwhackathon.broom.team.dto.request.WriteTeamBoardDto;
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
public class TeamBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_board_id", nullable = false)
    private Long teamBoardId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "meeting_time", nullable = false)
    private LocalTime meetingTime;

    @Column(name = "meeting_place", nullable = false)
    private String meetingPlace;

    @Column(name = "personnel", nullable = false)
    private int personnel;

    @Column(name = "is_full", nullable = false)
    private boolean isFull;

    @Column(name = "training_date", nullable = false)
    private LocalDate trainingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void updateTeamBoard(WriteTeamBoardDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.trainingDate = dto.getTrainingDate();
        this.meetingPlace = dto.getMeetingPlace();
        this.meetingTime = dto.getMeetingTime();
        this.personnel = dto.getPersonnel();
    }

    public void updateIsFull(IsFullCheckDto dto) {
        this.isFull = dto.isFull();
    }
}
